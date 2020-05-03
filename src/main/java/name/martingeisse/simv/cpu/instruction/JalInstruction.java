package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

public final class JalInstruction implements Instruction {

    private final int registerIndexDestination;
    private final int offset;

    public JalInstruction(int registerIndexDestination, int offset) {
        this.registerIndexDestination = CpuImplementationUtil.validateRegisterIndex(registerIndexDestination);
        this.offset = offset;
    }

    @Override
    public void execute(Cpu cpu) {
        int pc = cpu.getPc();
        cpu.setRegister(registerIndexDestination, pc + 4);
        cpu.setPc(pc + offset);
    }

}
