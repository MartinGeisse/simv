package name.martingeisse.simv.cpu;

import name.martingeisse.simv.cpu.extended.ExceptionExtendedInstructionUnit;
import name.martingeisse.simv.cpu.extended.ExtendedInstructionUnit;
import name.martingeisse.simv.cpu.floating.ExceptionFloatingPointUnit;
import name.martingeisse.simv.cpu.floating.FloatingPointUnit;
import name.martingeisse.simv.cpu.instruction.Instruction;
import name.martingeisse.simv.cpu.instruction.InstructionDecoder;
import name.martingeisse.simv.cpu.instruction.InstructionDecodingException;
import name.martingeisse.simv.cpu.instruction.StandardInstructionDecoder;
import name.martingeisse.simv.cpu.io.BrokenIoUnit;
import name.martingeisse.simv.cpu.io.IoUnit;
import name.martingeisse.simv.cpu.muldiv.ExceptionMultiplyDivideUnit;
import name.martingeisse.simv.cpu.muldiv.MultiplyDivideUnit;

/**
 * Note: Interrupts are not supported for now.
 * <p>
 * This implementation is little-endian only, which in a word-based addressing scheme means: For an aligned 4-byte block
 * (i.e. one addressing word), accessing the lowest byte accesses the 8 bits with lowest significance in the 32-bit value.
 */
public final class Cpu {

    private static final int WORD_ADDRESS_MASK = 0x3fff_ffff;

    private InstructionDecoder instructionDecoder;
    private IoUnit ioUnit;
    private MultiplyDivideUnit multiplyDivideUnit;
    private FloatingPointUnit floatingPointUnit;
    private ExtendedInstructionUnit extendedInstructionUnit;
    private boolean supportsMisalignedIo;

    private final int[] registers = new int[32];
    private int pc;

    public Cpu() {
        setIoUnit(null);
        setMultiplyDivideUnit(null);
        setFloatingPointUnit(null);
        setExtendedInstructionUnit(null);
        setSupportsMisalignedIo(false);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // configuration
    // ----------------------------------------------------------------------------------------------------------------


    public InstructionDecoder getInstructionDecoder() {
        return instructionDecoder;
    }

    public void setInstructionDecoder(InstructionDecoder instructionDecoder) {
        this.instructionDecoder = (instructionDecoder == null ? StandardInstructionDecoder.INSTANCE : instructionDecoder);
    }

    public IoUnit getIoUnit() {
        return ioUnit;
    }

    public void setIoUnit(IoUnit ioUnit) {
        this.ioUnit = (ioUnit == null ? BrokenIoUnit.INSTANCE : ioUnit);
    }

    public MultiplyDivideUnit getMultiplyDivideUnit() {
        return multiplyDivideUnit;
    }

    public void setMultiplyDivideUnit(MultiplyDivideUnit multiplyDivideUnit) {
        this.multiplyDivideUnit = (multiplyDivideUnit == null ? new ExceptionMultiplyDivideUnit(this) : multiplyDivideUnit);
    }

    public FloatingPointUnit getFloatingPointUnit() {
        return floatingPointUnit;
    }

    public void setFloatingPointUnit(FloatingPointUnit floatingPointUnit) {
        this.floatingPointUnit = (floatingPointUnit == null ? new ExceptionFloatingPointUnit(this) : floatingPointUnit);
    }

    public ExtendedInstructionUnit getExtendedInstructionUnit() {
        return extendedInstructionUnit;
    }

    public void setExtendedInstructionUnit(ExtendedInstructionUnit extendedInstructionUnit) {
        this.extendedInstructionUnit = (extendedInstructionUnit == null ? new ExceptionExtendedInstructionUnit(this) : extendedInstructionUnit);
    }

    public boolean isSupportsMisalignedIo() {
        return supportsMisalignedIo;
    }

    public void setSupportsMisalignedIo(boolean supportsMisalignedIo) {
        this.supportsMisalignedIo = supportsMisalignedIo;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // operation
    // ----------------------------------------------------------------------------------------------------------------

    public void reset() {
        pc = 0;
    }

    public final int getPc() {
        return pc;
    }

    public final void setPc(int pc) {
        this.pc = pc;
    }

    public final void incPc() {
        pc += 4;
    }

    public final int getRegister(int index) {
        index = index & 31;
        return registers[index];
    }

    public final void setRegister(int index, int value) {
        index = index & 31;
        if (index != 0) {
            registers[index] = value;
        }
    }

    // TODO not yet implemented
    public void triggerException(ExceptionType type) {
        throw new RuntimeException("RISC-V cpu exception: " + type + " at pc = " + pc);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // instruction execution
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Executes a single instruction.
     */
    public void step() {
        if ((pc & 3) != 0) {
            triggerException(ExceptionType.INSTRUCTION_ADDRESS_MISALIGNED);
            return;
        }
        int instructionWord = ioUnit.fetchInstruction((pc >> 2) & WORD_ADDRESS_MASK);
        Instruction instruction;
        try {
            instruction = instructionDecoder.decode(this, instructionWord);
        } catch (InstructionDecodingException e) {
            triggerException(ExceptionType.ILLEGAL_INSTRUCTION);
            return;
        }
        instruction.execute(this);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // helper classes
    // ----------------------------------------------------------------------------------------------------------------

    public enum ExceptionType {
        INSTRUCTION_ADDRESS_MISALIGNED,
        ILLEGAL_INSTRUCTION,
        DATA_ADDRESS_MISALIGNED,
        SYSTEM_INSTRUCTION
    }

}
