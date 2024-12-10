package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public class ReorderBuffer {

    public static final int size = 30;
    int frontQ = 0;
    int rearQ = 0;
    ROBEntry[] buff = new ROBEntry[size];
    int numRetirees = 0;

    PipelineSimulator simulator;
    RegisterFile regs;
    boolean halted = false;

    public ReorderBuffer(PipelineSimulator sim, RegisterFile registers) {
        simulator = sim;
        regs = registers;
    }

    public ROBEntry getEntryByTag(int tag) {
        return buff[tag];
    }

    public int getInstPC(int tag) {
        return buff[tag].getInstPC();
    }

    public boolean isHalted() {
        return halted;
    }

    public boolean isFull() {
        return (frontQ == rearQ && buff[frontQ] != null);
    }

    public int getNumRetirees() {
        return numRetirees;
    }

    public boolean retireInst() {
        // 3 cases
        // 1. regular reg dest inst
        // 2. isBranch w/ mispredict
        // 3. isStore
        ROBEntry retiree = buff[frontQ];

        if (retiree == null) {
            return false;
        }

        if (retiree.isHaltOpcode()) {
            halted = true;
            return true;
        }

        boolean shouldAdvance = true;

        if (retiree.mispredicted) {                                   // Case 2
            simulator.squashAllInsts();
            shouldAdvance = false;
            frontQ = (frontQ + 1) % size;
        }
        else if (retiree.opcode.equals(IssuedInst.INST_TYPE.STORE)) { // Case 3
            System.out.println("bruh");
            simulator.memory.setIntDataAtAddr(retiree.storeAddr + retiree.storeOffset, retiree.writeValue);
        }
        else {                                                        // Case 1
            if (retiree.isComplete()) {
                regs.regs[retiree.writeReg] = retiree.writeValue;
                regs.setSlotForReg(retiree.getWriteReg(), -1);
            }
            else {
                shouldAdvance = false;
            }
        }
            
        // TODO - this is where you look at the type of instruction and
        // figure out how to retire it properly
        // if mispredict branch, won't do normal advance
        if (shouldAdvance) {
            numRetirees++;
            buff[frontQ] = null;
            frontQ = (frontQ + 1) % size;
        }

        return false;
    }

    public void readCDB(CDB cdb) {
        // check entire CDB for someone waiting on this data
        // could be destination reg
        // could be store address source
        
        if (cdb.getDataValid()) {
            ROBEntry thisEntry = this.getEntryByTag(cdb.getDataTag());
            if (thisEntry != null) {
                thisEntry.writeValue = cdb.getDataValue();
                thisEntry.complete = true;
                if (thisEntry.opcode == INST_TYPE.BNE || thisEntry.opcode == INST_TYPE.BLTZ || 
                        thisEntry.opcode == INST_TYPE.BLEZ || thisEntry.opcode == INST_TYPE.BLTZ ||
                        thisEntry.opcode == INST_TYPE.BGEZ || thisEntry.opcode == INST_TYPE.BGTZ) {
                    if (thisEntry.writeValue == 1 && thisEntry.predictTaken) {
                        thisEntry.mispredicted = false;
                    }
                    else if (thisEntry.writeValue == 0 && !thisEntry.predictTaken) {
                        thisEntry.mispredicted = false;
                    }
                    else {
                        thisEntry.mispredicted = true;
                    }
                }
            }
        }
    }

    public void updateInstForIssue(IssuedInst inst) {
        // the task is to simply annotate the register fields
        // the dest reg will be assigned a tag, which is just our slot#
        // all src regs will either be assigned a tag, read from reg, or forwarded from ROB

        // TODO - possibly nothing if you use my model
        // I use the call to copyInstData below to do 2 things:
        // 1. update the Issued Inst
        // 2. fill in the ROB entry
        // first get a ROB slot
        if (buff[rearQ] != null) {
            throw new MIPSException("updateInstForIssue: no ROB slot avail");
        }
        ROBEntry newEntry = new ROBEntry(this);
        buff[rearQ] = newEntry;
        newEntry.copyInstData(inst, rearQ);

        rearQ = (rearQ + 1) % size;
    }

    public int getTagForReg(int regNum) {
        return (regs.getSlotForReg(regNum));
    }

    public int getDataForReg(int regNum) {
        return (regs.getReg(regNum));
    }

    public void setTagForReg(int regNum, int tag) {
        regs.setSlotForReg(regNum, tag);
    }

}
