package name.martingeisse.simv.cpu.system;

public final class BrokenSystemEngine implements SystemEngine {

    @Override
    public void ecall() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void ebreak() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readCsr(int csrIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeCsr(int csrIndex, int writeValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readWriteCsr(int csrIndex, int writeValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readSetCsr(int csrIndex, int setMask) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readClearCsr(int csrIndex, int clearMask) {
        throw new UnsupportedOperationException();
    }

}
