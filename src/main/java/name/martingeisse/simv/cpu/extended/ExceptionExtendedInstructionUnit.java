package name.martingeisse.simv.cpu.extended;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.instruction.Instruction;
import name.martingeisse.simv.cpu.instruction.InstructionDecodingException;

/**
 *
 */
public final class ExceptionExtendedInstructionUnit implements ExtendedInstructionUnit {

	private final Cpu cpu;

	public ExceptionExtendedInstructionUnit(Cpu cpu) {
		this.cpu = cpu;
	}

	@Override
	public Instruction decodeExtendedInstruction(int instruction) throws InstructionDecodingException {
		throw new InstructionDecodingException("extended instructions not supported by this configuration");
	}

}
