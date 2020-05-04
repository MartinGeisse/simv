package name.martingeisse.simv.cpu;

import name.martingeisse.simv.cpu.testutil.Program;
import name.martingeisse.simv.cpu.testutil.ProgramStorageKey;
import name.martingeisse.simv.cpu.testutil.TestMachine;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class BasicInstructionTest {

    @Rule
    public TestName testName = new TestName();

    private TestMachine testMachine;
    private Cpu cpu;

    @Before
    public void setUp() {
        ProgramStorageKey key = new ProgramStorageKey(getClass(), testName.getMethodName());
        testMachine = new TestMachine();
        testMachine.loadProgram(key.getImageFile());
        cpu = testMachine.getCpu();
    }

    @Test
    @Program("addi x0, x0, 0")
    public void testNop() {

    }

}
