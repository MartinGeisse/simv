package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public final class JalrInstruction implements Instruction {

    private final int registerIndexDestination;
    private final int registerIndexBase;
    private final int offset;

    public JalrInstruction(int registerIndexDestination, int registerIndexBase, int offset) {
        this.registerIndexDestination = registerIndexDestination;
        this.registerIndexBase = registerIndexBase;
        this.offset = offset;
    }

    @Override
    public void execute(Cpu cpu) {
        int pc = cpu.getPc();
        cpu.setRegister(registerIndexDestination, pc + 4);
        cpu.setPc((cpu.getRegister(registerIndexBase) + offset) & -2);
    }

}
