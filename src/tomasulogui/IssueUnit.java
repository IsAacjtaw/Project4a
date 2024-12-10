package tomasulogui;

public class IssueUnit {

    private enum EXEC_TYPE {
        NONE, LOAD, ALU, MULT, DIV, BRANCH
    };

    PipelineSimulator simulator;
    IssuedInst issuee;
    Object fu;
    Instruction instruc;
    Boolean reservation0Free = false;
    Boolean reservation1Free = false;
    Boolean ROBFree = false;
    EXEC_TYPE type = EXEC_TYPE.NONE;

    public IssueUnit(PipelineSimulator sim) {
        simulator = sim;
    }

    public void execCycle() {
        // an execution cycle involves:
        //get instruction from memory
        instruc = simulator.memory.getInstAtAddr(simulator.getPC());
        //Checking what resevation station the instruction is referencing
        Integer opcode = instruc.getOpcode();
        reservation0Free = false;
        reservation1Free = false;

        //Check if it is a load or a store and set type
        if (opcode == 0) {
            type = EXEC_TYPE.LOAD;
        } else if (opcode == 1) {
            type = EXEC_TYPE.NONE;
        } //Check if the instruction is a branch including jumps, 
        //set type and check reservation station
        else if ((opcode <= 43) && (opcode >= 30)) {
            type = EXEC_TYPE.BRANCH;
            if (simulator.branchUnit.stations[0].stat
                    == ReservationStation.Status.FREE) {
                reservation0Free = true;
            } else if (simulator.branchUnit.stations[1].stat
                    == ReservationStation.Status.FREE) {
                reservation1Free = true;
            }
        } //Check if the instruction is a multiply,
        //set type and check reservation station
        else if (opcode == 7) {
            type = EXEC_TYPE.MULT;
            if (simulator.multiplier.stations[0].stat
                    == ReservationStation.Status.FREE) {
                reservation0Free = true;
            } else if (simulator.multiplier.stations[1].stat
                    == ReservationStation.Status.FREE) {
                reservation1Free = true;
            }
        } //Check if it's a divide, set type and check reservation station
        else if (opcode == 8) {
            type = EXEC_TYPE.DIV;
            if (simulator.divider.stations[0].stat
                    == ReservationStation.Status.FREE) {
                reservation0Free = true;
            } else if (simulator.divider.stations[1].stat
                    == ReservationStation.Status.FREE) {
                reservation1Free = true;
            }
        } //ALU and other odds and end's instructions may have to come back 
        //and refine
        else {
            type = EXEC_TYPE.ALU;
            if (simulator.alu.stations[0].stat
                    == ReservationStation.Status.FREE) {
                reservation0Free = true;
            } else if (simulator.alu.stations[1].stat
                    == ReservationStation.Status.FREE) {
                reservation1Free = true;
            }
        }
        ROBFree = false;
        // 1. checking if ROB and Reservation Station avail
        if (!simulator.reorder.isFull()) {
            ROBFree = true;
        }
        // 2. issuing to reservation station, if no structural hazard
        // to issue, we make an IssuedInst, filling in what we know
        // We check the BTB, and put prediction if branch, updating PC
        // if pred taken, incr PC otherwise
        if ((ROBFree && reservation0Free) || (ROBFree && reservation1Free) || type == EXEC_TYPE.LOAD) {
            issuee = issuee.createIssuedInst(instruc);
            issuee.setPC(simulator.getPC());
            simulator.pc.incrPC();
            // We then send this to the ROB, which fills in the data fields
            simulator.reorder.updateInstForIssue(issuee);
        }
        if (type == EXEC_TYPE.BRANCH) {
            simulator.btb.predictBranch(issuee);
            
        }

        // We then check the CDB, and see if it is broadcasting data we need,
        //    so that we can forward during issue
        if ((issuee.regSrc1Tag == simulator.cdb.dataTag) && simulator.cdb.dataValid) {
            issuee.setRegSrc1Value(simulator.cdb.getDataValue());
            issuee.setRegSrc1Valid();
        }
        if ((issuee.regSrc2Tag == simulator.cdb.dataTag) && simulator.cdb.dataValid) {
            issuee.setRegSrc2Value(simulator.cdb.getDataValue());
            issuee.setRegSrc2Valid();
        }
        // We then send this to the FU, who stores in reservation station
        //if divide put in divide reservation station
        if (type == EXEC_TYPE.DIV) {
            //Fill the correct reservation station
            if (reservation0Free) {
                simulator.divider.stations[0].loadInst(issuee);
            } else if (reservation1Free) {
                simulator.divider.stations[1].loadInst(issuee);
            }
        } //if multiply put in multiply reservation station
        else if (type == EXEC_TYPE.MULT) {
            //Fill the correct reservation station
            if (reservation0Free) {
                simulator.multiplier.stations[0].loadInst(issuee);
            } else if (reservation1Free) {
                simulator.multiplier.stations[1].loadInst(issuee);
            }
        } //if alu op put in alu op reservation station
        else if (type == EXEC_TYPE.ALU) {
            //Fill the correct reservation station
            if (reservation0Free) {
                simulator.alu.stations[0].loadInst(issuee);
            } else if (reservation1Free) {
                simulator.alu.stations[1].loadInst(issuee);
            }
        } //if branch op put in branch op reservation station
        else if (type == EXEC_TYPE.BRANCH) {
            //Fill the correct reservation station
            if (reservation0Free) {
                simulator.branchUnit.stations[0].loadInst(issuee);
            } else if (reservation1Free) {
                simulator.branchUnit.stations[1].loadInst(issuee);
            }
        } else if (type == EXEC_TYPE.LOAD) {
            simulator.loader.acceptIssue(issuee);
        } else {

        }
    }

}
