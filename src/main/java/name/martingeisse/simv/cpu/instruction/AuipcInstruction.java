package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

/**
 * The upper immediate bits must be stored in the upper bits of the upperBits field. The lower bits of the upperBits
 * field must be zero.
 */
public final class AuipcInstruction implements Instruction {

    private final int registerIndexDestination;
    private final int upperBits;

    public AuipcInstruction(int registerIndexDestination, int upperBits) {
        this.registerIndexDestination = CpuImplementationUtil.validateRegisterIndex(registerIndexDestination);
        this.upperBits = CpuImplementationUtil.validateMask(upperBits, 0xfffff000);
    }

    @Override
    public void execute(Cpu cpu) {
        cpu.setRegister(registerIndexDestination, upperBits + cpu.getPc());
        cpu.incPc();
    }

}
