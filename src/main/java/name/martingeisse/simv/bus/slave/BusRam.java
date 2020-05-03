package name.martingeisse.simv.bus.slave;

import name.martingeisse.simv.bus.BusSlave;

/**
 * A simple on-chip memory that uses the lowest N address bits to store 2^N 32-bit words.
 */
public final class BusRam implements BusSlave {

    private static final int[] BYTE_MASK_TO_BIT_MASK = {
            0x00000000,
            0x000000ff,
            0x0000ff00,
            0x0000ffff,
            0x00ff0000,
            0x00ff00ff,
            0x00ffff00,
            0x00ffffff,
            0xff000000,
            0xff0000ff,
            0xff00ff00,
            0xff00ffff,
            0xffff0000,
            0xffff00ff,
            0xffffff00,
            0xffffffff,
    };

    private final int addressBits;
    private final int addressMask;
    private final int[] data;

    public BusRam(int addressBits) {
        this.addressBits = addressBits;
        this.data = new int[1 << addressBits];
        this.addressMask = data.length - 1;
    }

    public int getAddressBits() {
        return addressBits;
    }

    @Override
    public int getLocalAddressBits() {
        return addressBits;
    }

    public int getSize() {
        return data.length;
    }

    public int getValue(int address) {
        return data[address];
    }

    public void setValue(int address, int value) {
        data[address] = value;
    }

    @Override
    public int read(int address) {
        return getValue(address & addressMask);
    }

    @Override
    public void write(int address, int data, int byteMask) {
        address &= addressMask;

        // optimize common case
        if (byteMask == 15) {
            setValue(address, data);
            return;
        }

        // general case
        int bitMask = BYTE_MASK_TO_BIT_MASK[byteMask];
        setValue(address, (data & bitMask) | (getValue(address) & ~bitMask));

    }

}
