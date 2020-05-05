package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;

public interface InstructionDecoder {

    Instruction decode(int word) throws InstructionDecodingException;

}
