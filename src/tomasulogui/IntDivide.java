package tomasulogui;

public class IntDivide extends FunctionalUnit {

    public static final int EXEC_CYCLES = 7;
    CDB cdbDiv = new CDB(simulator);
    public IntDivide(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        int result = stations[station].data1 / stations[station].data2;
        return result;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
    
    public boolean sendToCDB(int resultTag, int resultValue) {
        boolean sendable = !cdbDiv.getDataValid();
        if (sendable) {
            cdbDiv.setDataTag(resultTag);
            cdbDiv.setDataValue(resultValue);
            cdbDiv.setDataValid(true);
        }
        return sendable;
    }
}
