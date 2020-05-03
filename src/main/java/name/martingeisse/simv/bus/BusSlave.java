package name.martingeisse.simv.bus;

/**
 *
 */
public interface BusSlave {

	int getLocalAddressBits();

	int read(int localAddress);

	void write(int localAddress, int data, int byteMask);

}
