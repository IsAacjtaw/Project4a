package tomasulogui;

import tomasulogui.IssuedInst.INST_TYPE;

public class ReservationStation {

    PipelineSimulator simulator;
    public enum Status {
        FREE, PROCESSING, SITTING
    };
    int tag1;
    int tag2;
    int data1;
    int data2;
    boolean data1Valid = false;
    boolean data2Valid = false;
    Status stat = Status.FREE;
    
    // destTag doubles as branch tag
    int destTag;
    IssuedInst.INST_TYPE function = IssuedInst.INST_TYPE.NOP;

    // following just for branches
    int addressTag;
    boolean addressValid = false;
    int address;
    boolean predictedTaken = false;

    
    public ReservationStation(PipelineSimulator sim) {
        simulator = sim;
    }

    public int getDestTag() {
        return destTag;
    }

    public int getData1() {
        return data1;
    }

    public int getData2() {
        return data2;
    }

    public boolean isPredictedTaken() {
        return predictedTaken;
    }

    public IssuedInst.INST_TYPE getFunction() {
        return function;
    }

    public void snoop(CDB cdb) {
        // TODO - add code to snoop on CDB each cycle
        // Check if the tag value in the CDB is the same as tag1 value of the reservation station
        if (cdb.getDataTag() == tag1) {
            // Set the resevation station data value for the corresponing tag and set data as valid
            data1 = cdb.getDataValue();
            data1Valid = true;
        }
        // Check if the tag value in the CDB is the same as tag1 value of the reservation station
        if (cdb.getDataTag() == tag2) {
            // Set the resevation station data value for the corresponing tag and set data as valid
            data2 = cdb.getDataValue();
            data2Valid = true;
        }
    }

    public boolean isReady() {
        return data1Valid && data2Valid;
    }

    public void loadInst(IssuedInst inst) {
        // TODO add code to insert inst into reservation station
        // check instruction type
        // Set reservation station correctly
       
//Check for type if immediate run this 
        if(inst.opcode == INST_TYPE.ADDI || inst.opcode == INST_TYPE.ANDI ||
                inst.opcode == INST_TYPE.ORI || inst.opcode == INST_TYPE.XORI){
            tag1 = inst.regSrc1Tag;
            tag2 = inst.regSrc2Tag;
            data1 = inst.regSrc1;
            data2 = inst.regSrc2Value;
            data1Valid = inst.regSrc1Valid;
            data2Valid = true;
            stat = Status.SITTING;
        }
        if(inst.opcode == INST_TYPE.ADD || inst.opcode == INST_TYPE.AND || 
                inst.opcode == INST_TYPE.DIV || inst.opcode == INST_TYPE.MUL ||
                inst.opcode == INST_TYPE.OR || inst.opcode == INST_TYPE.SLL ||
                inst.opcode == INST_TYPE.SRA || inst.opcode == INST_TYPE.SRL ||
                inst.opcode == INST_TYPE.SUB || inst.opcode == INST_TYPE.XOR){
            tag1 = inst.regSrc1Tag;
            tag2 = inst.regSrc2Tag;
            data1 = inst.regSrc1Value;
            data2 = inst.regSrc2Value;
            data1Valid = inst.regSrc1Valid;
            data2Valid = inst.regSrc2Valid;
            stat = Status.SITTING;
        }
        
    }
}
