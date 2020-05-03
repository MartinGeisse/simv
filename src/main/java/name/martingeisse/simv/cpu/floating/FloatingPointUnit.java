package name.martingeisse.simv.cpu.floating;

import name.martingeisse.simv.cpu.instruction.Instruction;
import name.martingeisse.simv.cpu.instruction.InstructionDecodingException;

/**
 *
 */
public interface FloatingPointUnit {

	Instruction decodeFloatingPointInstruction(int instruction) throws InstructionDecodingException;

}
