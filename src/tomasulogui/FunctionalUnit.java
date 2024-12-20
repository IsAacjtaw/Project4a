package tomasulogui;

public abstract class FunctionalUnit {

    PipelineSimulator simulator;
    ReservationStation[] stations = new ReservationStation[2];
    int currExCycle;
    int stationSent = -1;

    public FunctionalUnit(PipelineSimulator sim) {
        simulator = sim;
        stations[0] = new ReservationStation(sim);
        stations[1] = new ReservationStation(sim);
    }

    public void squashAll() {
        stations[0] = new ReservationStation(simulator);
        stations[1] = new ReservationStation(simulator);      
    }

    public abstract int calculateResult(int station);

    public abstract int getExecCycles();
    
    public abstract boolean sendToCDB(int resultTag, int resultValue);

    public void execCycle(CDB cdb) {
        //todo - start executing, ask for CDB, etc.
        
        if (stations[0].stat == ReservationStation.Status.SITTING && stations[0].isReady()) {
            // initiate computation
            stations[0].stat = ReservationStation.Status.PROCESSING;
            currExCycle++;                
        }
        else if (stations[1].stat == ReservationStation.Status.SITTING && stations[1].isReady()) {
            // initiate computation
            stations[1].stat = ReservationStation.Status.PROCESSING;
            currExCycle++;
        }
        if (stationSent > -1) {
            stations[stationSent] = new ReservationStation(simulator);
            stationSent = -1;
        }
        if (stations[0].stat == ReservationStation.Status.PROCESSING) {
            if (currExCycle >= getExecCycles()) {
                // station is done computing
                if (sendToCDB(stations[0].destTag, calculateResult(0))) {
                    stationSent = 0;
                }
            }
            currExCycle++;
        }
        else if (stations[1].stat == ReservationStation.Status.PROCESSING) {
            currExCycle++;
            if (currExCycle >= getExecCycles()) {
                // station is done computing
                if (sendToCDB(stations[1].destTag, calculateResult(1))) {
                    stationSent = 1;
                }
            }
            currExCycle++;
        }
        
        stations[0].snoop(cdb);
        stations[1].snoop(cdb);
    }

    public void acceptIssue(IssuedInst inst) {
        // todo - fill in reservation station (if available) with data from inst
        // Right now, this is handled in issue unit in execution cycle. 
    }

}
