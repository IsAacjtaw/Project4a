package tomasulogui;

public class ROBEntry {

    ReorderBuffer rob;

    // TODO - add many more fields into entry
    // I deleted most, and only kept those necessary to compile GUI
    boolean complete = false;
    boolean predictTaken = false;
    boolean mispredicted = false;
    int instPC = -1;
    int writeReg = -1;
    int writeValue = -1;
    
    int storeAddr = -1;
    int storeOffset = -1;

    IssuedInst.INST_TYPE opcode;

    public ROBEntry(ReorderBuffer buffer) {
        rob = buffer;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean branchMispredicted() {
        return mispredicted;
    }

    public boolean getPredictTaken() {
        return predictTaken;
    }

    public int getInstPC() {
        return instPC;
    }

    public IssuedInst.INST_TYPE getOpcode() {
        return opcode;
    }

    public boolean isHaltOpcode() {
        return (opcode == IssuedInst.INST_TYPE.HALT);
    }

    public void setBranchTaken(boolean result) {
        // TODO - maybe more than simple set
    }

    public int getWriteReg() {
        return writeReg;
    }

    public int getWriteValue() {
        return writeValue;
    }

    public void setWriteValue(int value) {
        writeValue = value;
    }

    public void copyInstData(IssuedInst inst, int rearQ) {
        // TODO - This is a long and complicated method, probably the most complex
        // of the project.  It does 2 things:
        // 1. update the instruction
        // 2. update the fields of the ROBEntry
        
        // #1 Update the issued instruction
        rob.regs.printRegFile();
        if (inst.regSrc1Used) {
            // Special case for R0
            int regSource = inst.getRegSrc1();
            if (regSource == 0) {
                inst.setRegSrc1Value(0);
                inst.setRegSrc1Valid();
            }
            // Case: register is not in reorder buffer
            else if (rob.getTagForReg(regSource) == -1) {
                inst.setRegSrc1Value(rob.getDataForReg(regSource));
                inst.setRegSrc1Valid();
            }
            // Case: register is in reorder buffer and valid
            else if (rob.buff[rob.getTagForReg(regSource)].isComplete()) {
                inst.setRegSrc1Value(rob.buff[rob.getTagForReg(regSource)].getWriteValue());
                inst.setRegSrc1Valid();
            }
            // Case: register is in reorder buffer but invalid
            else {
                inst.setRegSrc1Tag(rob.getTagForReg(inst.getRegSrc1()));
            }
        }
        if (inst.regSrc2Used) {
            // Special case for R0
            int regSource = inst.getRegSrc2();
            if (inst.getRegSrc2() == 0) {
                inst.setRegSrc2Value(0);
                inst.setRegSrc2Valid();
            }
            // Case: register is not in reorder buffer
            else if (rob.getTagForReg(regSource) == -1) {
                inst.setRegSrc2Value(rob.getDataForReg(regSource));
                inst.setRegSrc2Valid();
            }
            // Case: register is in reorder buffer and valid
            else if (rob.buff[rob.getTagForReg(regSource)].isComplete()) {
                inst.setRegSrc2Value(rob.buff[rob.getTagForReg(regSource)].getWriteValue());
                inst.setRegSrc2Valid();
            }
            // Case: register is in reorder buffer but invalid
            else {
                inst.setRegSrc2Tag(rob.getTagForReg(inst.getRegSrc2()));
            }
        }
        inst.setRegDestTag(rearQ);
        if (inst.regDestUsed) {
            int regDest = inst.getRegDest();
            rob.setTagForReg(regDest, rearQ);
        }
        
        // #2 Update the reorder buffer entry
        instPC = inst.getPC();
        predictTaken = inst.branchPrediction;
        writeReg = inst.regDest;
        opcode = inst.getOpcode();
        //writeValue = rob.regs.getReg(inst.regDest);
        
        if (opcode == IssuedInst.INST_TYPE.JAL || opcode == IssuedInst.INST_TYPE.JALR) {
            writeValue = inst.pc + 4;
            complete = true;
        }
        
        // set mispredict here
        if (inst.determineIfBranch()) {
            // What do I do?
        }
    }

}
