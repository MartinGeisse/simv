package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public final class NopInstruction implements Instruction {

    @Override
    public void execute(Cpu cpu) {
        cpu.incPc();
    }

}
