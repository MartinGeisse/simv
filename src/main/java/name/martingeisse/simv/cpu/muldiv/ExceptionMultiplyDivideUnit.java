package name.martingeisse.simv.cpu.muldiv;

import name.martingeisse.simv.cpu.Cpu;

/**
 *
 */
public final class ExceptionMultiplyDivideUnit implements MultiplyDivideUnit {

	private final Cpu cpu;

	public ExceptionMultiplyDivideUnit(Cpu cpu) {
		this.cpu = cpu;
	}

	@Override
	public void performMultiplayDivideInstruction(int instruction) {
		cpu.triggerException(Cpu.ExceptionType.ILLEGAL_INSTRUCTION);
	}

}
