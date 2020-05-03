package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public interface InstructionDecoder {

    Instruction decode(Cpu cpu, int word) throws InstructionDecodingException;

}
