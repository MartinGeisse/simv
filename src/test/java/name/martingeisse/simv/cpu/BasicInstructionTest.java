package name.martingeisse.simv.cpu;

import name.martingeisse.simv.bus.slave.BusRam;
import name.martingeisse.simv.cpu.testutil.BuildProgramsMain;
import name.martingeisse.simv.cpu.testutil.Program;
import name.martingeisse.simv.cpu.testutil.ProgramStorageKey;
import name.martingeisse.simv.cpu.testutil.TestMachine;
import org.junit.*;
import org.junit.rules.TestName;

public class BasicInstructionTest {

    //region support

    @Rule
    public TestName testName = new TestName();

    private TestMachine testMachine;
    private Cpu cpu;
    private BusRam ram;
    private boolean hasBeenRun = false;

    // TODO remove
    @BeforeClass
    public static void setUpClass() throws Exception {
        BuildProgramsMain.main(new String[0]);
    }

    @Before
    public void setUp() throws Exception {
        ProgramStorageKey key = new ProgramStorageKey(getClass(), testName.getMethodName());
        testMachine = new TestMachine();
        testMachine.loadProgram(key.getImageFile());
        cpu = testMachine.getCpu();
        ram = testMachine.getRam();
    }

    @After
    public void tearDown() {
        Assert.assertTrue(hasBeenRun);
    }

    private void execute() {
        hasBeenRun = true;
        while (!testMachine.isStopped()) {
            cpu.step();
        }
    }

    //endregion

    //region basic, addi, add, sub, lui

    @Test
    @Program("addi x0, x0, 0")
    public void testNop() {
        execute();
        Assert.assertEquals(4, cpu.getPc());
        testMachine.assertRegistersIntactExcept();
    }

    @Test
    @Program("addi x1, x0, 42")
    public void testLoadSmallConstant() {
        execute();
        Assert.assertEquals(4, cpu.getPc());
        testMachine.assertRegistersIntactExcept(1);
        testMachine.assertRegisterValues(42);
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x1, 5",
            "addi x2, x2, 2",
            "addi x3, x1, -10"
    })
    public void testAddi() {
        execute();
        Assert.assertEquals(16, cpu.getPc());
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        testMachine.assertRegisterValues(42, 49, 32);
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 5",
            "add x3, x1, x2",
    })
    public void testAdd() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(47, cpu.getRegister(3));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x0, x0, 5",
            "add x0, x0, x1",
    })
    public void testWriteToZeroRegister() {
        execute();
        testMachine.assertRegistersIntactExcept(1);
        Assert.assertEquals(0, cpu.getRegister(0));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 5",
            "sub x3, x1, x2",
    })
    public void testSub() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(37, cpu.getRegister(3));
    }

    @Test
    @Program({
            "lui x0, 1",
            "lui x1, 1",
            "lui x2, 2",
            "lui x3, 0xfffff",
    })
    public void testLui() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(0, cpu.getRegister(0));
        testMachine.assertRegisterValues(0x1000, 0x2000, 0xfffff000);
    }

    //endregion

    //region shift

    @Test
    @Program({
            "addi x1, x0, 42",
            "sll x1, x1, 1",
            "addi x2, x0, 42",
            "sll x2, x2, 2",
            "addi x3, x0, 3",
            "sll x3, x3, 30",
            "addi x4, x0, 3",
            "sll x4, x4, 31",
            "addi x5, x0, 6",
            "sll x5, x5, 31",
            "addi x6, x0, 42",
            "sll x6, x6, 0",
            "addi x7, x0, -9",
            "sll x7, x7, 1",
    })
    public void testShiftLeftImmediate() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5, 6, 7);
        Assert.assertEquals(84, cpu.getRegister(1));
        Assert.assertEquals(168, cpu.getRegister(2));
        Assert.assertEquals(0xc0000000, cpu.getRegister(3));
        Assert.assertEquals(0x80000000, cpu.getRegister(4));
        Assert.assertEquals(0, cpu.getRegister(5));
        Assert.assertEquals(42, cpu.getRegister(6));
        Assert.assertEquals(-18, cpu.getRegister(7));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 2",
            "sll x2, x1, x2",
            "addi x3, x0, 32",
            "sll x3, x1, x3",
    })
    public void testShiftLeft() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(42, cpu.getRegister(1));
        Assert.assertEquals(168, cpu.getRegister(2));
        Assert.assertEquals(42, cpu.getRegister(3));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "srl x1, x1, 1",
            "addi x2, x0, 42",
            "srl x2, x2, 2",
            "li x3, 0xc0000000",
            "srl x3, x3, 30",
            "li x4, 0xc0000000",
            "srl x4, x4, 31",
            "li x5, 0x60000000",
            "srl x5, x5, 31",
            "addi x6, x0, 42",
            "srl x6, x6, 0",
            "addi x7, x0, -9", // 0xffff_fff7
            "srl x7, x7, 1",
    })
    public void testShiftRightLogicalImmediate() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5, 6, 7);
        Assert.assertEquals(21, cpu.getRegister(1));
        Assert.assertEquals(10, cpu.getRegister(2));
        Assert.assertEquals(3, cpu.getRegister(3));
        Assert.assertEquals(1, cpu.getRegister(4));
        Assert.assertEquals(0, cpu.getRegister(5));
        Assert.assertEquals(42, cpu.getRegister(6));
        Assert.assertEquals(0x7fff_fffb, cpu.getRegister(7));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 2",
            "srl x2, x1, x2",
            "addi x3, x0, 32",
            "srl x3, x1, x3",
    })
    public void testShiftRightLogical() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(42, cpu.getRegister(1));
        Assert.assertEquals(10, cpu.getRegister(2));
        Assert.assertEquals(42, cpu.getRegister(3));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "sra x1, x1, 1",
            "addi x2, x0, 42",
            "sra x2, x2, 2",
            "li x3, 0xc0000000",
            "sra x3, x3, 30",
            "li x4, 0xc0000000",
            "sra x4, x4, 31",
            "li x5, 0x60000000",
            "sra x5, x5, 31",
            "addi x6, x0, 42",
            "sra x6, x6, 0",
            "addi x7, x0, -9",
            "sra x7, x7, 1",
    })
    public void testShiftRightArithmeticImmediate() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5, 6, 7);
        Assert.assertEquals(21, cpu.getRegister(1));
        Assert.assertEquals(10, cpu.getRegister(2));
        Assert.assertEquals(-1, cpu.getRegister(3));
        Assert.assertEquals(-1, cpu.getRegister(4));
        Assert.assertEquals(0, cpu.getRegister(5));
        Assert.assertEquals(42, cpu.getRegister(6));
        Assert.assertEquals(-5, cpu.getRegister(7));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 2",
            "sra x2, x1, x2",
            "addi x3, x0, 32",
            "sra x3, x1, x3",
    })
    public void testShiftRightArithmetic() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        Assert.assertEquals(42, cpu.getRegister(1));
        Assert.assertEquals(10, cpu.getRegister(2));
        Assert.assertEquals(42, cpu.getRegister(3));
    }

    //endregion

    //region slt, sltu

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 42",
            "addi x3, x0, 41",
            "addi x4, x0, 43",
            "addi x5, x0, -1",
            "slt x2, x1, x2",
            "slt x3, x1, x3",
            "slt x4, x1, x4",
            "slt x5, x1, x5",
    })
    public void testSlt() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5);
        Assert.assertEquals(0, cpu.getRegister(2));
        Assert.assertEquals(0, cpu.getRegister(3));
        Assert.assertEquals(1, cpu.getRegister(4));
        Assert.assertEquals(0, cpu.getRegister(5));
    }

    @Test
    @Program({
            "addi x1, x0, 42",
            "addi x2, x0, 42",
            "addi x3, x0, 41",
            "addi x4, x0, 43",
            "addi x5, x0, -1",
            "sltu x2, x1, x2",
            "sltu x3, x1, x3",
            "sltu x4, x1, x4",
            "sltu x5, x1, x5",
    })
    public void testSltu() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5);
        Assert.assertEquals(0, cpu.getRegister(2));
        Assert.assertEquals(0, cpu.getRegister(3));
        Assert.assertEquals(1, cpu.getRegister(4));
        Assert.assertEquals(1, cpu.getRegister(5));
    }

    //endregion

    //region and, or, xor

    @Test
    @Program({
            "addi x1, x0, 43", // 32 + 8 + 2 + 1
            "andi x2, x1, 7",
            "andi x3, x1, 0",
            "andi x4, x1, -1",

            // sign extension
            "addi x5, x0, -1",
            "andi x5, x5, -1",
    })
    public void testAndi() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5);
        testMachine.assertRegisterValues(43, 3, 0, 43, -1);
    }

    @Test
    @Program({
            "addi x1, x0, 43", // 32 + 8 + 2 + 1
            "ori x2, x1, 7",
            "ori x3, x1, 0",
            "ori x4, x1, -1",

            // sign extension
            "ori x5, x0, -1",
    })
    public void testOri() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5);
        testMachine.assertRegisterValues(43, 47, 43, -1, -1);
    }

    @Test
    @Program({
            "addi x1, x0, 43", // 32 + 8 + 2 + 1
            "xori x2, x1, 7",
            "xori x3, x1, 0",
            "xori x4, x1, -1",

            // sign extension
            "xori x5, x0, -1",
    })
    public void testXori() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3, 4, 5);
        testMachine.assertRegisterValues(43, 43 ^ 7, 43, ~43, -1);
    }

    //endregion

    //region AUIPC, MISC-MEM (as NOP)

    @Test
    @Program({
            "auipc x1, 1",
            "auipc x2, 1",
            "auipc x3, 1",
    })
    public void testAuipc() {
        execute();
        testMachine.assertRegistersIntactExcept(1, 2, 3);
        testMachine.assertRegisterValues(0x1000, 0x1004, 0x1008);
    }

    @Test
    @Program({
            "fence",
    })
    public void testMiscMemAsNop() {
        execute();
        Assert.assertEquals(4, cpu.getPc());
        testMachine.assertRegistersIntactExcept();
    }

    //endregion

    //region load, store

    @Test
    @Program({
            "lw x0, 600(x0)",

            "lw x1, 600(x0)",
            "lw x2, 601(x0)",
            "lw x3, 602(x0)",
            "lw x4, 603(x0)",
            "lw x5, 604(x0)",

            "lh x6, 600(x0)",
            "lh x7, 601(x0)",
            "lh x8, 602(x0)",
            "lh x9, 603(x0)",
            "lh x10, 604(x0)",

            "lhu x11, 600(x0)",
            "lhu x12, 601(x0)",
            "lhu x13, 602(x0)",
            "lhu x14, 603(x0)",
            "lhu x15, 604(x0)",

            "lb x16, 600(x0)",
            "lb x17, 601(x0)",
            "lb x18, 602(x0)",
            "lb x19, 603(x0)",
            "lb x20, 604(x0)",

            "lbu x21, 600(x0)",
            "lbu x22, 601(x0)",
            "lbu x23, 602(x0)",
            "lbu x24, 603(x0)",
            "lbu x25, 604(x0)",
    })
    public void testLoad() {
        cpu.setSupportsMisalignedIo(true);
        ram.setValue(150, 0x1234abcd); // byte address 600 is word address 150
        ram.setValue(151, 0xef567890);
        execute();
        testMachine.assertRegistersIntact(26, 27, 28, 29, 30, 31);
        Assert.assertEquals(0, cpu.getRegister(0));
        testMachine.assertRegisterValues(
                0x1234abcd, 0x901234ab, 0x78901234, 0x56789012, 0xef567890,
                0xffffabcd, 0x34ab, 0x1234, 0xffff9012, 0x7890,
                0xabcd, 0x34ab, 0x1234, 0x9012, 0x7890,
                0xffffffcd, 0xffffffab, 0x34, 0x12, 0xffffff90,
                0xcd, 0xab, 0x34, 0x12, 0x90
        );
    }

    @Test
    @Program({
            "li x1, 0x12345678",

            "sw x1, 600(x0)",
            "sw x1, 605(x0)",
            "sw x1, 610(x0)",
            "sw x1, 615(x0)",
            "sw x1, 620(x0)",

            "sh x1, 624(x0)",
            "sh x1, 627(x0)",
            "sh x1, 630(x0)",
            "sh x1, 633(x0)",
            "sh x1, 636(x0)",

            "sb x1, 640(x0)",
            "sb x1, 641(x0)",
            "sb x1, 642(x0)",
            "sb x1, 643(x0)",
            "sb x1, 644(x0)",
    })
    public void testStore() {
        cpu.setSupportsMisalignedIo(true);
        execute();
        testMachine.assertRegistersIntactExcept(1);

        Assert.assertEquals(0x12345678, ram.getValue(150)); // byte address 600 is word address 150
        Assert.assertEquals(0x34567800, ram.getValue(151));
        Assert.assertEquals(0x56780012, ram.getValue(152));
        Assert.assertEquals(0x78001234, ram.getValue(153));
        Assert.assertEquals(0x00123456, ram.getValue(154));
        Assert.assertEquals(0x12345678, ram.getValue(155));

        Assert.assertEquals(0x78005678, ram.getValue(156));
        Assert.assertEquals(0x56780056, ram.getValue(157));
        Assert.assertEquals(0x00567800, ram.getValue(158));
        Assert.assertEquals(0x00005678, ram.getValue(159));

        Assert.assertEquals(0x78787878, ram.getValue(160));
        Assert.assertEquals(0x00000078, ram.getValue(161));
    }

    // TODO addressing, exception when misaligned IO is forbidden

    //endregion

    //region branch, jal, jalr

    // TODO

    //endregion

}
