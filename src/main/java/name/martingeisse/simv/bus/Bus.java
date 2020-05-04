package name.martingeisse.simv.bus;

import java.util.ArrayList;
import java.util.List;

/**
 * No-delay transaction-level Mybus implementation.
 */
public final class Bus {

    private final List<SlaveEntry> slaveEntries = new ArrayList<>();

    public Bus() {
    }

    public List<SlaveEntry> getSlaveEntries() {
        return slaveEntries;
    }

    public void addSlave(SlaveEntry entry) {
        for (SlaveEntry existing : slaveEntries) {
            if (entry.overlaps(existing)) {
                throw new IllegalArgumentException("bus slaves overlap");
            }
        }
        slaveEntries.add(entry);
    }

    public void addSlave(int address, BusSlave slave) {
        addSlave(new SlaveEntry(address, slave));
    }

    public SlaveEntry getMatchingEntry(int address) {
        for (SlaveEntry slaveEntry : slaveEntries) {
            if (slaveEntry.matchesAddress(address)) {
                return slaveEntry;
            }
        }
        return null;
    }

    public int read(int address) {
        // For now, reads 0 if no matching slave was found. Should actually cause a CPU exception.
        SlaveEntry slaveEntry = getMatchingEntry(address);
        if (slaveEntry == null) {
            return 0;
        } else {
            return slaveEntry.read(address);
        }
    }

    public void write(int address, int data, int byteMask) {
        if ((byteMask & ~15) != 0) {
            throw new IllegalArgumentException("invalid byte mask: " + byteMask);
        }
        SlaveEntry slaveEntry = getMatchingEntry(address);
        if (slaveEntry != null) {
            slaveEntry.write(address, data, byteMask);
        } // else: should actually cause a CPU exception
    }

}
