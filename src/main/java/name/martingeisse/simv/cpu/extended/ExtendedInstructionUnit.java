package name.martingeisse.simv.cpu.extended;

import name.martingeisse.simv.cpu.instruction.Instruction;
import name.martingeisse.simv.cpu.instruction.InstructionDecodingException;

/**
 *
 */
public interface ExtendedInstructionUnit {

	Instruction decodeExtendedInstruction(int instruction) throws InstructionDecodingException;

}
