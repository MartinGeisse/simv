package name.martingeisse.simv.cpu;

public class CpuImplementationUtil {

    private CpuImplementationUtil() {
    }

    /**
     * Parameter validation. Throws an {@link IllegalArgumentException} if invalid.
     */
    public static int validateRegisterIndex(int index) {
        if (index < 0 || index >= 32) {
            throw new IllegalArgumentException("invalid register index: " + index);
        }
        return index;
    }

    public static int getDestinationRegisterIndex(int instructionWord) {
        return (instructionWord >> 7) & 31;
    }

    public static int getSourceRegisterIndex1(int instructionWord) {
        return (instructionWord >> 15) & 31;
    }

    public static int getSourceRegisterIndex2(int instructionWord) {
        return (instructionWord >> 20) & 31;
    }

    public static int validateMask(int value, int mask) {
        if (value != (value & mask)) {
            throw new IllegalArgumentException("value " + value + " does not correspond to mask " + mask);
        }
        return value;
    }

}
