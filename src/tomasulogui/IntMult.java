package tomasulogui;

public class IntMult extends FunctionalUnit {

    public static final int EXEC_CYCLES = 4;
    CDB cdbMult = new CDB(simulator);
    public IntMult(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        int result = stations[station].data1 * stations[station].data2;
        return result;

    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
    
    public boolean sendToCDB(int resultTag, int resultValue) {
        boolean sendable = !cdbMult.getDataValid();
        if (sendable) {
            cdbMult.setDataTag(resultTag);
            cdbMult.setDataValue(resultValue);
            cdbMult.setDataValid(true);
        }
        return sendable;
    }
}
