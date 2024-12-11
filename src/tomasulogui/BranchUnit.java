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
        IssuedInst.INST_TYPE instructype = stations[station].getFunction();
        
        switch(instructype){
            case BEQ:
                if(op1 == op2){
                    return 1;
                }
                break;
            case BNE:
                if(op1 != op2){
                    return 1;
                }
                break;
            case BLTZ:
                if(op1 < op2){
                    return 1;
                }
                break;
            case BLEZ:
                if(op1 <= op2){
                    return 1;
                }
                break;
            case BGEZ:
                if(op1 > op2){
                    return 1;
                }
                break;
            case BGTZ:
                if(op1 >= op2){
                    return 1;
                }
                break;
            case JALR:
            case JAL:
            default:
                System.out.println("Branch unit default case no branch type?"
                        + " shouldn't be here I think");
                return -1;
        }
        return 0;
    }

    public int getExecCycles() {
        return EXEC_CYCLES;
    }
    
    public boolean sendToCDB(int resultTag, int resultValue) {
        if (resultValue == -1) {
            return true;
        }
        boolean sendable = !cdbBranch.getDataValid();
        if (sendable) {
            cdbBranch.setDataTag(resultTag);
            cdbBranch.setDataValue(resultValue);
            cdbBranch.setDataValid(true);
        }
        return sendable;
    }
}
