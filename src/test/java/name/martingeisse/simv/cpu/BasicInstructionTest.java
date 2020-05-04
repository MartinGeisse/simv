package name.martingeisse.simv.cpu;

import name.martingeisse.simv.cpu.testutil.Program;

public class BasicInstructionTest {

    @Program("addi x0, x0, 0")
    public void testNop() {
    }

}
