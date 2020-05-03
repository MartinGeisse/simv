package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

public final class IntegerImmediateOperationInstruction implements Instruction {

    private final IntegerOperation operation;
    private final int registerIndexDestination;
    private final int registerIndexSource1;
    private final int value2;

    public IntegerImmediateOperationInstruction(IntegerOperation operation, int registerIndexDestination, int registerIndexSource1, int value2) {
        this.operation = operation;
        this.registerIndexDestination = CpuImplementationUtil.validateRegisterIndex(registerIndexDestination);
        this.registerIndexSource1 = CpuImplementationUtil.validateRegisterIndex(registerIndexSource1);
        this.value2 = value2;
    }

    public IntegerOperation getOperation() {
        return operation;
    }

    public int getRegisterIndexDestination() {
        return registerIndexDestination;
    }

    public int getRegisterIndexSource1() {
        return registerIndexSource1;
    }

    public int getValue2() {
        return value2;
    }

    @Override
    public void execute(Cpu cpu) {
        int x = cpu.getRegister(registerIndexSource1);
        cpu.setRegister(registerIndexDestination, operation.compute(x, value2));
        cpu.incPc();
    }

}
