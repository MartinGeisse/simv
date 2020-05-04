package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

public abstract class IoInstruction implements Instruction {

    //region base class

    private final int dataRegisterIndex;
    private final int addressRegisterIndex;
    private final int offset;

    public IoInstruction(int dataRegisterIndex, int addressRegisterIndex, int offset) {
        this.dataRegisterIndex = CpuImplementationUtil.validateRegisterIndex(dataRegisterIndex);
        this.addressRegisterIndex = CpuImplementationUtil.validateRegisterIndex(addressRegisterIndex);
        this.offset = offset;
    }

    public int getDataRegisterIndex() {
        return dataRegisterIndex;
    }

    public int getAddressRegisterIndex() {
        return addressRegisterIndex;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public void execute(Cpu cpu) {
        int writeData = cpu.getRegister(getDataRegisterIndex());
        int address = cpu.getRegister(addressRegisterIndex) + offset;
        if (!cpu.isSupportsMisalignedIo() && (address & getAlignmentMask()) != 0) {
            cpu.triggerException(Cpu.ExceptionType.DATA_ADDRESS_MISALIGNED);
        }
        int wordAddress = address >> 2;
        int byteOffset = (address & 3);
        executeInternal(cpu, writeData, wordAddress, byteOffset);
        cpu.incPc();
    }

    protected abstract void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset);

    protected abstract int getAlignmentMask();

    protected final void write(Cpu cpu, int address, int data, int byteMask) {
        cpu.getIoUnit().write(address & 0x3fff_ffff, data, byteMask);
    }

    protected final int read(Cpu cpu, int address) {
        return cpu.getIoUnit().read(address & 0x3fff_ffff);
    }

    //endregion

    //region subclasses

    //region store

    public static final class StoreByte extends IoInstruction {

        public StoreByte(int dataRegisterIndex, int addressRegisterIndex, int offset) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            write(cpu, wordAddress, writeData << (byteOffset * 8), 1 << byteOffset);
        }

        @Override
        protected int getAlignmentMask() {
            return 0;
        }

    }

    public static final class StoreHalfWord extends IoInstruction {

        public StoreHalfWord(int dataRegisterIndex, int addressRegisterIndex, int offset) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            switch (byteOffset) {

                case 0:
                    write(cpu, wordAddress, writeData, 3);
                    break;

                case 1:
                    write(cpu, wordAddress, writeData << 8, 6);
                    break;

                case 2:
                    write(cpu, wordAddress, writeData << 16, 12);
                    break;

                case 3:
                    write(cpu, wordAddress, writeData << 24, 8);
                    write(cpu, wordAddress + 1, writeData >> 8, 1);
                    break;

                default:
                    throw new RuntimeException("wtf");

            }

        }

        @Override
        protected int getAlignmentMask() {
            return 1;
        }

    }

    public static final class StoreWord extends IoInstruction {

        public StoreWord(int dataRegisterIndex, int addressRegisterIndex, int offset) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            switch (byteOffset) {

                case 0:
                    write(cpu, wordAddress, writeData, 15);
                    break;

                case 1:
                    write(cpu, wordAddress, writeData << 8, 14);
                    write(cpu, wordAddress + 1, writeData >> 24, 1);
                    break;

                case 2:
                    write(cpu, wordAddress, writeData << 16, 12);
                    write(cpu, wordAddress + 1, writeData >> 16, 3);
                    break;

                case 3:
                    write(cpu, wordAddress, writeData << 24, 8);
                    write(cpu, wordAddress + 1, writeData >> 8, 7);
                    break;

                default:
                    throw new RuntimeException("wtf");

            }

        }

        @Override
        protected int getAlignmentMask() {
            return 3;
        }

    }

    //endregion store

    //region load

    public static final class LoadByte extends IoInstruction {

        private final boolean unsigned;

        public LoadByte(int dataRegisterIndex, int addressRegisterIndex, int offset, boolean unsigned) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
            this.unsigned = unsigned;
        }

        public boolean isUnsigned() {
            return unsigned;
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            int readData = read(cpu, wordAddress) >> (byteOffset * 8);
            readData = (unsigned ? (readData & 0xff) : (byte) readData);
            cpu.setRegister(getDataRegisterIndex(), readData);
        }

        @Override
        protected int getAlignmentMask() {
            return 0;
        }

    }

    public static final class LoadHalfword extends IoInstruction {

        private final boolean unsigned;

        public LoadHalfword(int dataRegisterIndex, int addressRegisterIndex, int offset, boolean unsigned) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
            this.unsigned = unsigned;
        }

        public boolean isUnsigned() {
            return unsigned;
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            int readData = read(cpu, wordAddress) >>> byteOffset * 8;
            if (byteOffset == 3) {
                readData |= read(cpu, wordAddress + 1) << 8;
            }
            readData = (unsigned ? (readData & 0xffff) : (short) readData);
            cpu.setRegister(getDataRegisterIndex(), readData);
        }

        @Override
        protected int getAlignmentMask() {
            return 1;
        }

    }

    public static final class LoadWord extends IoInstruction {

        public LoadWord(int dataRegisterIndex, int addressRegisterIndex, int offset) {
            super(dataRegisterIndex, addressRegisterIndex, offset);
        }

        @Override
        protected void executeInternal(Cpu cpu, int writeData, int wordAddress, int byteOffset) {
            int readData;
            switch (byteOffset) {

                case 0:
                    readData = read(cpu, wordAddress);
                    break;

                case 1:
                    readData = (read(cpu, wordAddress) >>> 8) | (read(cpu, wordAddress + 1) << 24);
                    break;

                case 2:
                    readData = (read(cpu, wordAddress) >>> 16) | (read(cpu, wordAddress + 1) << 16);
                    break;

                case 3:
                    readData = (read(cpu, wordAddress) >>> 24) | (read(cpu, wordAddress + 1) << 8);
                    break;

                default:
                    throw new RuntimeException("wtf");

            }
            cpu.setRegister(getDataRegisterIndex(), readData);
        }

        @Override
        protected int getAlignmentMask() {
            return 3;
        }

    }

    //endregion load

    //endregion

}
