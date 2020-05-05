package name.martingeisse.simv.cpu.muldiv;

import name.martingeisse.simv.cpu.Cpu;

/**
 *
 */
public final class HardwareMultiplyDivideUnit implements MultiplyDivideUnit {

	private static final long ZERO_EXTEND_MASK = 0xffff_ffff;

	private final Cpu cpu;

	public HardwareMultiplyDivideUnit(Cpu cpu) {
		this.cpu = cpu;
	}

	@Override
	public void performMultiplyDivideInstruction(int instruction) {
		int x = cpu.getRegister(instruction >> 15);
		int y = cpu.getRegister(instruction >> 20);
		int func = (instruction >> 12) & 7;
		int result;
		switch (func) {

			case 0: // MUL
				result = x * y;
				break;

			case 1: // MULH
				result = mulh(x, y);
				break;

			case 2: // MULHSU
				result = mulh(x, zeroExtend(y));
				break;

			case 3: // MULHU
				result = mulh(zeroExtend(x), zeroExtend(y));
				break;

			case 4: // DIV
				result = divide(x, y);
				break;

			case 5: // DIVU
				result = divide(zeroExtend(x), zeroExtend(y));
				break;

			case 6: // REM
				result = remainder(x, y);
				break;

			case 7: // REMU
				result = remainder(zeroExtend(x), zeroExtend(y));
				break;

			default:
				throw new RuntimeException("wtf");

		}
		cpu.setRegister(instruction >> 7, result);
	}

	private static int mulh(long x, long y) {
		return (int)((x * y) >> 32);
	}

	private static long zeroExtend(long x) {
		return x & ZERO_EXTEND_MASK;
	}

	private static int divide(long x, long y) {
		if (y == 0) {
			return -1;
		}
		return (int)(x / y);
	}

	private static int remainder(long x, long y) {
		if (y == 0) {
			return (int)x;
		}
		return (int)(x % y);
	}

}
