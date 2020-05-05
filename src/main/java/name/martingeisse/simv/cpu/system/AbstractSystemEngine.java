package name.martingeisse.simv.cpu.system;

public abstract class AbstractSystemEngine implements SystemEngine {

    @Override
    public int readCsr(int csrIndex) {
        return getSimpleCsr(csrIndex);
    }

    @Override
    public void writeCsr(int csrIndex, int writeValue) {
        setSimpleCsr(csrIndex, writeValue);
    }

    @Override
    public int readWriteCsr(int csrIndex, int writeValue) {
        return readWriteSimpleCsr(csrIndex, writeValue);
    }

    @Override
    public int readSetCsr(int csrIndex, int setMask) {
        return readSetSimpleCsr(csrIndex, setMask);
    }

    @Override
    public int readClearCsr(int csrIndex, int clearMask) {
        return readClearSimpleCsr(csrIndex, clearMask);
    }

    protected abstract int getSimpleCsr(int csrIndex);

    protected abstract void setSimpleCsr(int csrIndex, int value);

    protected final int readWriteSimpleCsr(int csrIndex, int writeValue) {
        int oldValue = getSimpleCsr(csrIndex);
        setSimpleCsr(csrIndex, writeValue);
        return oldValue;
    }

    protected final int readSetSimpleCsr(int csrIndex, int setMask) {
        int oldValue = getSimpleCsr(csrIndex);
        setSimpleCsr(csrIndex, oldValue | setMask);
        return oldValue;
    }

    protected final int readClearSimpleCsr(int csrIndex, int clearMask) {
        int oldValue = getSimpleCsr(csrIndex);
        setSimpleCsr(csrIndex, oldValue & ~clearMask);
        return oldValue;
    }

}
