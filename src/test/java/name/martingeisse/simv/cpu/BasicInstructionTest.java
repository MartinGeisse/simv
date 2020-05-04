package name.martingeisse.simv.cpu;

import name.martingeisse.simv.bus.slave.BusRam;
import name.martingeisse.simv.cpu.testutil.BuildProgramsMain;
import name.martingeisse.simv.cpu.testutil.Program;
import name.martingeisse.simv.cpu.testutil.ProgramStorageKey;
import name.martingeisse.simv.cpu.testutil.TestMachine;
import org.junit.*;
import org.junit.rules.TestName;

public class BasicInstructionTest {

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
    })
    public void testAddi() {
        execute();
        Assert.assertEquals(12, cpu.getPc());
        testMachine.assertRegistersIntactExcept(1, 2);
        Assert.assertEquals(42, cpu.getRegister(1));
        Assert.assertEquals(49, cpu.getRegister(2));
    }

}
