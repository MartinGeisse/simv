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
        Assert.assertEquals(42, cpu.getRegister(1));
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
        Assert.assertEquals(42, cpu.getRegister(1));
        Assert.assertEquals(49, cpu.getRegister(2));
        Assert.assertEquals(32, cpu.getRegister(3));
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
        Assert.assertEquals(0x1000, cpu.getRegister(1));
        Assert.assertEquals(0x2000, cpu.getRegister(2));
        Assert.assertEquals(0xfffff000, cpu.getRegister(3));
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
        Assert.assertEquals(3, cpu.getRegister(2));
        Assert.assertEquals(0, cpu.getRegister(3));
        Assert.assertEquals(43, cpu.getRegister(4));
        Assert.assertEquals(-1, cpu.getRegister(5));
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
        Assert.assertEquals(47, cpu.getRegister(2));
        Assert.assertEquals(43, cpu.getRegister(3));
        Assert.assertEquals(-1, cpu.getRegister(4));
        Assert.assertEquals(-1, cpu.getRegister(5));
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
        Assert.assertEquals(43 ^ 7, cpu.getRegister(2));
        Assert.assertEquals(43, cpu.getRegister(3));
        Assert.assertEquals(~43, cpu.getRegister(4));
        Assert.assertEquals(-1, cpu.getRegister(5));
    }

    //endregion

}
