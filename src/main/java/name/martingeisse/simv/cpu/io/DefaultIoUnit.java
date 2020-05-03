package name.martingeisse.simv.cpu.io;

import name.martingeisse.simv.bus.Bus;

/**
 *
 */
public final class DefaultIoUnit implements IoUnit {

	private final Bus bus;

	public DefaultIoUnit(Bus bus) {
		this.bus = bus;
	}

	@Override
	public int fetchInstruction(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	public int read(int wordAddress) {
		return bus.read(wordAddress);
	}

	@Override
	public void write(int wordAddress, int data, int byteMask) {
		bus.write(wordAddress, data, byteMask);
	}

}
