package tomasulogui;

public class IntMult extends FunctionalUnit {

    public static final int EXEC_CYCLES = 4;
    CDB cdbMult;
    public IntMult(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        int result = 0;
        return result;

    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
