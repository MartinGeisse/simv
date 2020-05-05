package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.system.SystemEngine;

public abstract class CsrInstruction implements Instruction {

    protected final Operation operation;
    protected final int csrIndex;
    protected final int destinationRegisterIndex;

    public CsrInstruction(Operation operation, int csrIndex, int destinationRegisterIndex) {
        this.operation = operation;
        this.csrIndex = csrIndex;
        this.destinationRegisterIndex = destinationRegisterIndex;
    }

    public Operation getOperation() {
        return operation;
    }

    public int getCsrIndex() {
        return csrIndex;
    }

    public int getDestinationRegisterIndex() {
        return destinationRegisterIndex;
    }

    protected final void executeInternal(Cpu cpu, int sourceValue, boolean noSource) {
        SystemEngine engine = cpu.getSystemEngine();
        switch (getOperation()) {

            case WRITE:
                if (destinationRegisterIndex == 0) {
                    engine.writeCsr(csrIndex, sourceValue);
                } else {
                    cpu.setRegister(destinationRegisterIndex, engine.readWriteCsr(csrIndex, sourceValue));
                }
                break;

            case SET_BITS:
                if (noSource) {
                    cpu.setRegister(destinationRegisterIndex, engine.readCsr(csrIndex));
                } else {
                    cpu.setRegister(destinationRegisterIndex, engine.readSetCsr(csrIndex, sourceValue));
                }
                break;

            case CLEAR_BITS:
                if (noSource) {
                    cpu.setRegister(destinationRegisterIndex, engine.readCsr(csrIndex));
                } else {
                    cpu.setRegister(destinationRegisterIndex, engine.readClearCsr(csrIndex, sourceValue));
                }
                break;
        }

    }


    public static final class FromRegister extends CsrInstruction {

        private final int sourceRegisterIndex;

        public FromRegister(Operation operation, int csrIndex, int destinationRegisterIndex, int sourceRegisterIndex) {
            super(operation, csrIndex, destinationRegisterIndex);
            this.sourceRegisterIndex = sourceRegisterIndex;
        }

        public int getSourceRegisterIndex() {
            return sourceRegisterIndex;
        }

        @Override
        public void execute(Cpu cpu) {
            executeInternal(cpu, cpu.getRegister(sourceRegisterIndex), sourceRegisterIndex == 0);
        }

    }

    public static final class FromImmediate extends CsrInstruction {

        private final int sourceValue;

        public FromImmediate(Operation operation, int csrIndex, int destinationRegisterIndex, int sourceValue) {
            super(operation, csrIndex, destinationRegisterIndex);
            this.sourceValue = sourceValue;
        }

        public int getSourceValue() {
            return sourceValue;
        }

        @Override
        public void execute(Cpu cpu) {
            executeInternal(cpu, sourceValue, sourceValue == 0);
        }

    }

    public enum Operation {
        WRITE,
        SET_BITS,
        CLEAR_BITS
    }
}
