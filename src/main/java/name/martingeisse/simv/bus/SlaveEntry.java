package name.martingeisse.simv.bus;

/**
 *
 */
public final class SlaveEntry {

	private final int address;
	private final int upperAddressMask;
	private final int lowerAddressMask;
	private final BusSlave slave;

	public SlaveEntry(int address, BusSlave slave) {
		this.address = address;
		this.lowerAddressMask = (1 << slave.getLocalAddressBits()) - 1;
		this.upperAddressMask = ~lowerAddressMask;
		this.slave = slave;
		if ((address & lowerAddressMask) != 0) {
			throw new IllegalArgumentException("device address has local address bits set. Number of local bits: " +
					slave.getLocalAddressBits() + ", address: " + address);
		}
	}

	public int getAddress() {
		return address;
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

	boolean overlaps(SlaveEntry other) {
		return (address <= other.getAddress() + other.getLowerAddressMask() &&
			other.getAddress() <= address + lowerAddressMask);
	}

}
