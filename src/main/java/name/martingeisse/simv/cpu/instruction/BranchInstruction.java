package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public final class BranchInstruction implements Instruction {

    private final BranchCondition condition;
    private final int registerIndexLeft;
    private final int registerIndexRight;
    private final int offset;

    public BranchInstruction(BranchCondition condition, int registerIndexLeft, int registerIndexRight, int offset) {
        this.condition = condition;
        this.registerIndexLeft = registerIndexLeft;
        this.registerIndexRight = registerIndexRight;
        this.offset = offset;
    }

    @Override
    public void execute(Cpu cpu) {
        int left = cpu.getRegister(registerIndexLeft);
        int right = cpu.getRegister(registerIndexRight);
        if (condition.check(left, right)) {
            cpu.setPc(cpu.getPc() + offset);
        } else {
            cpu.incPc();
        }
    }

}
