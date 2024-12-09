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
        inst.setRegDestTag(rearQ);

        // TODO - This is a long and complicated method, probably the most complex
        // of the project.  It does 2 things:
        // 1. update the instruction, as shown in 2nd line of code above
        // 2. update the fields of the ROBEntry, as shown in the 1st line of code above
        
        // #1 Update the issued instruction
        if (inst.regDestUsed) {
            int regDest = inst.getRegDest();
            rob.setTagForReg(regDest, rearQ);
            inst.setRegDestTag(rob.getTagForReg(regDest));
            System.out.println(rob.getTagForReg(regDest));
        }
        if (inst.regSrc1Used) {
            // Special case for R0
            if (inst.getRegSrc1() == 0) {
                inst.setRegSrc1Value(0);
                inst.setRegSrc1Valid();
            }
            System.out.println(rob.getTagForReg(inst.getRegSrc1()));
        }
        if (inst.regSrc2Used) {
            // Special case for R0
            if (inst.getRegSrc2() == 0) {
                inst.setRegSrc2Value(0);
                inst.setRegSrc2Valid();
            }
            System.out.println(rob.getTagForReg(inst.getRegSrc2()));
        }
        System.out.println("immediate val: " + inst.immediate);
        
        // #2 Update the reorder buffer entry
        instPC = inst.getPC();
        predictTaken = inst.branchPrediction;
        writeReg = inst.regDest;
        opcode = inst.getOpcode();
        writeValue = rob.regs.getReg(inst.regDest);
        
        // set mispredict here
    }

}
