package tomasulogui;

public abstract class FunctionalUnit {

    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];
    int currExCycle;

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
        stations[0] = new ReservationStation(sim);
        stations[1] = new ReservationStation(sim);
    }

    public void squashAll() {
        // todo fill in
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();
    
    public abstract void sendToCDB(int resultTag, int resultValue);

    public void execCycle(CDB cdb) {
        //todo - start executing, ask for CDB, etc.
        if (stations[0].stat == ReservationStation.Status.PROCESSING) {
            currExCycle++;
            if (currExCycle >= getExecCycles()) {
                // station is done computing
                sendToCDB(stations[0].destTag, calculateResult(0));
            }
        }
        else if (stations[1].stat == ReservationStation.Status.PROCESSING) {
            currExCycle++;
            if (currExCycle >= getExecCycles()) {
                // station is done computing
                sendToCDB(stations[1].destTag, calculateResult(1));
            }
        }
        else if (stations[0].stat == ReservationStation.Status.SITTING) {
            // initiate computation
            if (stations[0].isReady()) {
                stations[0].stat = ReservationStation.Status.PROCESSING;
            }
        }
        else if (stations[1].stat == ReservationStation.Status.SITTING) {
            // initiate computation
            if (stations[1].isReady()) {
                stations[1].stat = ReservationStation.Status.PROCESSING;
            }
        }
        
    }

    public void acceptIssue(IssuedInst inst) {
        // todo - fill in reservation station (if available) with data from inst
        // Right now, this is handled in issue unit in execution cycle. 
    }

}
