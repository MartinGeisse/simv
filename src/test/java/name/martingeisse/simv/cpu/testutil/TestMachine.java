package name.martingeisse.simv.cpu.testutil;

import name.martingeisse.simv.bus.Bus;
import name.martingeisse.simv.bus.slave.BusRam;
import name.martingeisse.simv.cpu.Cpu;
import name.martingeisse.simv.cpu.instruction.StandardInstructionDecoder;
import name.martingeisse.simv.cpu.io.DefaultIoUnit;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class TestMachine {

    public static final int OVERWRITE_CANARY = 0x53b79aa1;

    private final Bus bus;
    private final Cpu cpu;
    private final BusRam ram;
    private boolean stopped = false;

    public TestMachine() {
        bus = new Bus();
        cpu = new Cpu();
        cpu.setIoUnit(new DefaultIoUnit(bus));
        cpu.setInstructionDecoder((cpu, instructionWord) -> {
            if (instructionWord == -4) {
                return cpu2 -> setStopped(true);
            } else {
                return StandardInstructionDecoder.INSTANCE.decode(cpu, instructionWord);
            }
        });
        ram = new BusRam(10);
        bus.addSlave(0, ram);
        for (int i = 1; i < 32; i++) {
            cpu.setRegister(i, OVERWRITE_CANARY);
        }
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

    public void loadProgram(File imageFile) throws IOException {
        try (FileInputStream in = new FileInputStream(imageFile)) {
            int wordAddress = 0;
            while (true) {
                int first = in.read();
                if (first < 0) {
                    break;
                }
                int word = first | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
                ram.setValue(wordAddress, word);
                wordAddress++;
            }
        }
    }

    public void assertRegistersIntact(int... indices) {
        for (int index : indices) {
            if (cpu.getRegister(index) != OVERWRITE_CANARY) {
                Assert.fail("register x" + index + " was overwritten");
            }
        }
    }

    public void assertRegistersIntactExcept(int... indices) {
        outer: for (int i = 1; i < 32; i++) {
            if (cpu.getRegister(i) != OVERWRITE_CANARY) {
                for (int exceptedIndex : indices) {
                    if (i == exceptedIndex) {
                        continue outer;
                    }
                }
                Assert.fail("register x" + i + " was overwritten");
            }
        }
    }

    public void assertRegisterValues(int... values) {
        for (int i = 0; i < values.length; i++) {
            int reg = i + 1;
            Assert.assertEquals("register x" + reg, values[i], cpu.getRegister(reg));
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

}
