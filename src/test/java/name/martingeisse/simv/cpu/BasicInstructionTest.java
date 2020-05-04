package name.martingeisse.simv.cpu;

import name.martingeisse.simv.bus.slave.BusRam;
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

}
