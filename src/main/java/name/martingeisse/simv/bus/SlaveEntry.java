package name.martingeisse.simv.bus;

/**
 *
 */
public final class SlaveEntry {

	private final int address;
	private final int localAddressBits;
	private final int upperAddressMask;
	private final int lowerAddressMask;
	private final BusSlave slave;

	public SlaveEntry(int address, int localAddressBits, BusSlave slave) {
		this.address = address;
		this.localAddressBits = localAddressBits;
		this.lowerAddressMask = (1 << localAddressBits) - 1;
		this.upperAddressMask = ~lowerAddressMask;
		this.slave = slave;
		if ((address & lowerAddressMask) != 0) {
			throw new IllegalArgumentException("device address has local address bits set. Number of local bits: " + localAddressBits + ", address: " + address);
		}
	}

	public int getAddress() {
		return address;
	}

	public int getLocalAddressBits() {
		return localAddressBits;
	}

	public int getUpperAddressMask() {
		return upperAddressMask;
	}

	public int getLowerAddressMask() {
		return lowerAddressMask;
	}

	public BusSlave getSlave() {
		return slave;
	}

	public boolean matchesAddress(int address) {
		return (address & upperAddressMask) == this.address;
	}

	// ignores upper address bits
	int read(int address) {
		return slave.read(address & lowerAddressMask);
	}

	// ignores upper address bits
	void write(int address, int data, int byteMask) {
		slave.write(address & lowerAddressMask, data, byteMask);
	}

}
