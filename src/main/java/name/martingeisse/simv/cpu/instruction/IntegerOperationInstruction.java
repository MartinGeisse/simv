package name.martingeisse.simv.cpu.instruction;

import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.CpuImplementationUtil;

public final class IntegerOperationInstruction implements Instruction {

    private final IntegerOperation operation;
    private final int registerIndexDestination;
    private final int registerIndexSource1;
    private final int registerIndexSource2;

    public IntegerOperationInstruction(IntegerOperation operation, int registerIndexDestination, int registerIndexSource1, int registerIndexSource2) {
        this.operation = operation;
        this.registerIndexDestination = CpuImplementationUtil.validateRegisterIndex(registerIndexDestination);
        this.registerIndexSource1 = CpuImplementationUtil.validateRegisterIndex(registerIndexSource1);
        this.registerIndexSource2 = CpuImplementationUtil.validateRegisterIndex(registerIndexSource2);
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

    public int getRegisterIndexSource2() {
        return registerIndexSource2;
    }

    @Override
    public void execute(Cpu cpu) {
        int x = cpu.getRegister(registerIndexSource1);
        int y = cpu.getRegister(registerIndexSource2);
        cpu.setRegister(registerIndexDestination, operation.compute(x, y));
        cpu.incPc();
    }

}
