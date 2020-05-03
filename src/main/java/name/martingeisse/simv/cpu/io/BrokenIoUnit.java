package name.martingeisse.simv.cpu.io;

/**
 * Used as a Null Object, but the CPU cannot work without a real implementation.
 */
public final class BrokenIoUnit implements IoUnit {

	public static final BrokenIoUnit INSTANCE = new BrokenIoUnit();

	@Override
	public int fetchInstruction(int wordAddress) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int read(int wordAddress) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(int wordAddress, int data, int byteMask) {
		throw new UnsupportedOperationException();
	}

}
