package tomasulogui;

public class BranchUnit extends FunctionalUnit {
    
    public static final int EXEC_CYCLES = 1;
    CDB cdbBranch = new CDB(simulator);

    
    public BranchUnit(PipelineSimulator sim) {
        super(sim);
    }

    public int calculateResult(int station) {
        int op1 = stations[station].data1;
        int op2 = stations[station].data2;
        if (op1 != op2) {
            return 1;
        }
        return 0;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
}
