package name.martingeisse.simv.cpu.floating;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.instruction.Instruction;
import name.martingeisse.simv.cpu.instruction.InstructionDecodingException;

/**
 *
 */
public final class ExceptionFloatingPointUnit implements FloatingPointUnit {

	private final Cpu cpu;

	public ExceptionFloatingPointUnit(Cpu cpu) {
		this.cpu = cpu;
	}

	@Override
	public Instruction decodeFloatingPointInstruction(int instruction) throws InstructionDecodingException {
		throw new InstructionDecodingException("instruction decoding does not yet support floating-point instructions");
	}

}
