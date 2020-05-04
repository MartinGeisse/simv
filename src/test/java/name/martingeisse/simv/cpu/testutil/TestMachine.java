package name.martingeisse.simv.cpu.testutil;

import name.martingeisse.simv.bus.Bus;
import name.martingeisse.simv.bus.slave.BusRam;
import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.io.DefaultIoUnit;

import java.io.File;

public final class TestMachine {

    private final Bus bus;
    private final Cpu cpu;
    private final BusRam ram;

    public TestMachine() {
        bus = new Bus();
        cpu = new Cpu();
        cpu.setIoUnit(new DefaultIoUnit(bus));
        ram = new BusRam(10);
        bus.addSlave(0, ram);
    }

    public Bus getBus() {
        return bus;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public BusRam getRam() {
        return ram;
    }

    public void loadProgram(File imageFile) {
        System.out.println("loading: " + imageFile);
    }

}
