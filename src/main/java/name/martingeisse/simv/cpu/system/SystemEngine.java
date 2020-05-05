package name.martingeisse.simv.cpu.system;

public interface SystemEngine {

    void ecall();
    void ebreak();

    int readCsr(int csrIndex);
    void writeCsr(int csrIndex, int writeValue);
    int readWriteCsr(int csrIndex, int writeValue);
    int readSetCsr(int csrIndex, int setMask);
    int readClearCsr(int csrIndex, int clearMask);

}
