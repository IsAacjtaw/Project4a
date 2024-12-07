package tomasulogui;

public class IntAlu extends FunctionalUnit {

    public static final int EXEC_CYCLES = 1;
    CDB cdbAlu = new CDB(simulator);

    public IntAlu(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        // Is it an ALU or an adder? Who can say. 
        int result = stations[station].data1 + stations[station].data2;
        return result;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
    
    public void sendToCDB(int resultTag, int resultValue) {
        if (!cdbAlu.getDataValid()) {
            cdbAlu.setDataTag(resultTag);
            cdbAlu.setDataValue(resultValue);
            cdbAlu.setDataValid(true);
        }
    }
}
