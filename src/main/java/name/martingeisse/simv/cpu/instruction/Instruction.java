package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public interface Instruction {

    /**
     * This method must be invoked with the same CPU that was used to decode this instruction. The cpu parameter is
     * only used for convenience so the CPU object does not have to be stored in each instruction. However, specialized
     * instructions are free to refer to affected objects that are part of the CPU so they won't work correctly with
     * other CPU objects.
     */
    void execute(Cpu cpu);

}
