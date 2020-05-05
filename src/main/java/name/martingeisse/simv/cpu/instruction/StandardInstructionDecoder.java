package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

/**
 * Delegates instruction decoding to the appropriate sub-units based on the main opcode.
 */
public final class StandardInstructionDecoder implements InstructionDecoder {

    private final Cpu cpu;

    public StandardInstructionDecoder(Cpu cpu) {
        this.cpu = cpu;
    }

    @Override
    public Instruction decode(int instructionWord) throws InstructionDecodingException {
        if ((instructionWord & 3) != 3) {
            return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);
        }
        int opcode = (instructionWord >> 2) & 31;
        switch (opcode) {

            case 0: { // LOAD
                int widthCode = (instructionWord >> 12) & 3;
                boolean unsigned = ((instructionWord >> 12) & 4) != 0;
                int dataRegisterIndex = CpuImplementationUtil.getDestinationRegisterIndex(instructionWord);
                int addressRegisterIndex = CpuImplementationUtil.getSourceRegisterIndex1(instructionWord);
                int offset = instructionWord >> 20;
                switch (widthCode) {

                    case 0: // byte
                        return new IoInstruction.LoadByte(dataRegisterIndex, addressRegisterIndex, offset, unsigned);

                    case 1: // half-word
                        return new IoInstruction.LoadHalfword(dataRegisterIndex, addressRegisterIndex, offset, unsigned);

                    case 2: // word
                        if (unsigned) {
                            throw new InstructionDecodingException("there is no 'load unsigned word' instruction");
                        }
                        return new IoInstruction.LoadWord(dataRegisterIndex, addressRegisterIndex, offset);

                    default:
                        throw new InstructionDecodingException("invalid data width code for load: " + widthCode);

                }
            }

            case 1: // LOAD-FP
                return cpu.getFloatingPointUnit().decodeFloatingPointInstruction(instructionWord);

            case 2: // custom-0
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 3: // MISC-MEM, i.e. FENCE and FENCE.I -- implemented as NOPs
                return new NopInstruction();

            case 4: // OP-IMM
                return decodeIntegerOperationInstruction(instructionWord, true);

            case 5: // AUIPC
                return new AuipcInstruction((instructionWord >> 7) & 31, instructionWord & 0xfffff000);

            case 6: // OP-IMM-32
                throw new InstructionDecodingException("this is a 32-bit implementation -- " +
                        "32-on-64-bit operations are not supported");

            case 7: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 8: { // STORE
                int widthCode = (instructionWord >> 12) & 7;
                int dataRegisterIndex = CpuImplementationUtil.getSourceRegisterIndex2(instructionWord);
                int addressRegisterIndex = CpuImplementationUtil.getSourceRegisterIndex1(instructionWord);
                int offset = ((instructionWord >> 7) & 31) + ((instructionWord & 0xfe000000) >> 20);
                switch (widthCode) {

                    case 0: // byte
                        return new IoInstruction.StoreByte(dataRegisterIndex, addressRegisterIndex, offset);

                    case 1: // half-word
                        return new IoInstruction.StoreHalfWord(dataRegisterIndex, addressRegisterIndex, offset);

                    case 2: // word
                        return new IoInstruction.StoreWord(dataRegisterIndex, addressRegisterIndex, offset);

                    default:
                        throw new InstructionDecodingException("invalid data width code for store: " + widthCode);

                }
            }

            case 9: // STORE-FP
                return cpu.getFloatingPointUnit().decodeFloatingPointInstruction(instructionWord);

            case 10: // custom-1
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 11: // AMO (atomic memory operation)
                throw new UnsupportedOperationException("AMO not supported by this implementation");

            case 12: // OP
                if (instructionWord >>> 25 == 1) {
                    // TODO multiplyDivideUnit.performMultiplyDivideInstruction(instructionWord);
                    throw new InstructionDecodingException("multiply/divide not yet supported");
                } else {
                    return decodeIntegerOperationInstruction(instructionWord, false);
                }

            case 13: // LUI
                return new LuiInstruction((instructionWord >> 7) & 31, instructionWord & 0xfffff000);

            case 14: // OP-32
                throw new InstructionDecodingException("this is a 32-bit implementation -- " +
                        "32-on-64-bit operations are not supported");

            case 15: // reserved for 64-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 16: // MADD
            case 17: // MSUB
            case 18: // NMSUB
            case 19: // NMADD
            case 20: // OP-FP
                return cpu.getFloatingPointUnit().decodeFloatingPointInstruction(instructionWord);

            case 21: // reserved
                throw new InstructionDecodingException("reserved opcode: " + opcode);

            case 22: // custom-2
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 23: // reserved for 48-bit instructions, but we only use 32-bit instructions, so this is free for custom instructions
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 24: { // BRANCH
                int leftIndex = CpuImplementationUtil.getSourceRegisterIndex1(instructionWord);
                int rightIndex = CpuImplementationUtil.getSourceRegisterIndex2(instructionWord);
                int conditionCode = (instructionWord >> 12) & 7;
                BranchCondition condition;
                switch (conditionCode) {

                    case 0: // BEQ
                        condition = BranchCondition.EQUAL;
                        break;

                    case 1: // BNE
                        condition = BranchCondition.NOT_EQUAL;
                        break;

                    case 4: // BLT
                        condition = BranchCondition.LESS_THAN;
                        break;

                    case 5: // BGE
                        condition = BranchCondition.GREATER_EQUAL;
                        break;

                    case 6: // BLTU
                        condition = BranchCondition.LESS_THAN_UNSIGNED;
                        break;

                    case 7: // BGEU
                        condition = BranchCondition.GREATER_EQUAL_UNSIGNED;
                        break;

                    case 2: // unused
                    case 3: // unused
                    default:
                        throw new InstructionDecodingException("invalid branch condition code: " + conditionCode);

                }
                // optimization note: Angel shifts the last component by 20, not 19, then masks out that extra copy
                // of the sign bit. It uses this to merge this shift with the other shift-by-20.
                int offset =
                        ((instructionWord >> 7) & 0x0000001e) +
                                ((instructionWord >> 20) & 0x000007e0) +
                                ((instructionWord << 4) & 0x00000800) +
                                ((instructionWord >> 19) & 0xfffff000);
                return new BranchInstruction(condition, leftIndex, rightIndex, offset);
            }

            case 25: { // JALR
                int destinationRegisterIndex = CpuImplementationUtil.getDestinationRegisterIndex(instructionWord);
                int baseRegisterIndex = CpuImplementationUtil.getSourceRegisterIndex1(instructionWord);
                int offset = instructionWord >> 20;
                return new JalrInstruction(destinationRegisterIndex, baseRegisterIndex, offset);
            }

            case 26: // reserved
                throw new InstructionDecodingException("reserved opcode: " + opcode);

            case 27: { // JAL
                int destinationRegisterIndex = CpuImplementationUtil.getDestinationRegisterIndex(instructionWord);
                // instruction = imm[20], imm[10:1], imm[11], imm[19:12], rd[4:0], opcode[6:0]; implicitly imm[0] = 0
                int offset = ((instructionWord >> 11) & 0xfff00000) |
                        (instructionWord & 0x000ff000) |
                        ((instructionWord >> 9) & 0x00000800) |
                        ((instructionWord >> 20) & 0x7fe);
                return new JalInstruction(destinationRegisterIndex, offset);
            }

            case 28: { // SYSTEM
                int csrIndex = instructionWord >>> 20;
                int source = CpuImplementationUtil.getSourceRegisterIndex1(instructionWord);
                int func3 = (instructionWord >> 12) & 7;
                int destinationRegisterIndex = CpuImplementationUtil.getDestinationRegisterIndex(instructionWord);
                switch (func3) {

                    case 0: // ecall / ebreak
                        return new SystemInstruction();

                    case 1: // csrrw
                        return new CsrInstruction.FromRegister(CsrInstruction.Operation.WRITE,
                                csrIndex, destinationRegisterIndex, source);

                    case 2: // csrrs
                        return new CsrInstruction.FromRegister(CsrInstruction.Operation.SET_BITS,
                                csrIndex, destinationRegisterIndex, source);

                    case 3: // csrrc
                        return new CsrInstruction.FromRegister(CsrInstruction.Operation.CLEAR_BITS,
                                csrIndex, destinationRegisterIndex, source);

                    case 5: // csrrwi
                        return new CsrInstruction.FromImmediate(CsrInstruction.Operation.WRITE,
                                csrIndex, destinationRegisterIndex, source);

                    case 6: // csrrsi
                        return new CsrInstruction.FromImmediate(CsrInstruction.Operation.SET_BITS,
                                csrIndex, destinationRegisterIndex, source);

                    case 7: // csrrci
                        return new CsrInstruction.FromImmediate(CsrInstruction.Operation.CLEAR_BITS,
                                csrIndex, destinationRegisterIndex, source);

                    case 4:
                    default:
                        throw new InstructionDecodingException("unknown SYSTEM func3");

                }
            }

            case 29: // reserved
                throw new InstructionDecodingException("reserved opcode: " + opcode);

            case 30: // custom-3
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            case 31: // reserved for 80-bit+ instructions, but we only use 32-bit instructions, so this is free for custom instructions
                return cpu.getExtendedInstructionUnit().decodeExtendedInstruction(instructionWord);

            default:
                throw new InstructionDecodingException("unknown opcode: " + opcode);

        }
    }

    //region OP, OP-IMM

    private Instruction decodeIntegerOperationInstruction(int instructionWord, boolean immediate)
            throws InstructionDecodingException {
        int registerIndexDestination = (instructionWord >> 7) & 31;
        int registerIndexSource1 = (instructionWord >> 15) & 31;
        int registerIndexSource2OrValue2;
        int func = (instructionWord >> 12) & 7;
        boolean checkUpperBits, allowExtraBit;
        if (immediate) {
            checkUpperBits = (func == 1 || func == 5);
            allowExtraBit = (func == 5);
            registerIndexSource2OrValue2 = (instructionWord >> 20);
        } else {
            checkUpperBits = true;
            allowExtraBit = (func == 0 || func == 5);
            registerIndexSource2OrValue2 = (instructionWord >> 20) & 31;
        }
        if (checkUpperBits) {
            int upperBits = instructionWord >>> 25;
            if (upperBits != 0 && (upperBits != 32 || !allowExtraBit)) {
                throw new InstructionDecodingException("invalid upper bits for operation");
            }
        }
        boolean extraBit = allowExtraBit && ((instructionWord & 0x4000_0000) != 0);
        IntegerOperation operation = decodeIntegerOperation(func, extraBit);
        if (immediate) {
            return new IntegerImmediateOperationInstruction(operation, registerIndexDestination, registerIndexSource1,
                    registerIndexSource2OrValue2);
        } else {
            return new IntegerOperationInstruction(operation, registerIndexDestination, registerIndexSource1,
                    registerIndexSource2OrValue2);
        }
    }

    private IntegerOperation decodeIntegerOperation(int func, boolean extraBit) {
        switch (func) {

            case 0:
                return extraBit ? IntegerOperation.SUBTRACT : IntegerOperation.ADD;

            case 1:
                return IntegerOperation.SHIFT_LEFT;

            case 2:
                return IntegerOperation.SET_TO_LESS_THAN;

            case 3:
                return IntegerOperation.SET_TO_LESS_THAN_UNSIGNED;

            case 4:
                return IntegerOperation.XOR;

            case 5:
                return extraBit ? IntegerOperation.SHIFT_RIGHT_ARITHMETIC : IntegerOperation.SHIFT_RIGHT_LOGICAL;

            case 6:
                return IntegerOperation.OR;

            case 7:
                return IntegerOperation.AND;

            default:
                throw new RuntimeException("internal error in instruction decoding: decodeIntegerOperation() " +
                        "called with invalid func value: " + func);

        }

    }

    //endregion

}
