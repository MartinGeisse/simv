package name.martingeisse.simv.cpu.io;

/**
 *
 */
public interface IoUnit {

	int fetchInstruction(int wordAddress);

	int read(int wordAddress);

	void write(int wordAddress, int data, int byteMask);

}
