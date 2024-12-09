package tomasulogui;
import tomasulogui.IssuedInst.INST_TYPE;

public class IntAlu extends FunctionalUnit {

    public static final int EXEC_CYCLES = 1;
    CDB cdbAlu = new CDB(simulator);

    public IntAlu(PipelineSimulator sim) {
        super(sim);
    }

    
   /* public enum INST_TYPE {
        ADD, ADDI, SUB, MUL, DIV, AND, ANDI, OR, ORI, XOR, XORI, SLL, SRL, SRA,
        LOAD, STORE, HALT,
        NOP, BEQ, BNE, BLTZ, BLEZ, BGEZ, BGTZ, J, JAL, JR, JALR
    };*/
    public int calculateResult(int station) {
        // Is it an ALU or an adder? Who can say. 
        //set data one and two then run the instruction type on them
        INST_TYPE instructype = stations[station].function;
        int data1 = stations[station].data1;
        int data2 = stations[station].data2;
        int result = -999;
        switch(instructype){
            case ADD:
                result = data1 + data2;
                break;
            case ADDI:    
                result = data1 + data2;
                break;    
            case SUB: 
                result = data1 - data2;
                break;    
            case MUL:  
                System.out.println("You shouldn't be here multiplying in the intALU");
                break;
            case DIV:
                System.out.println("You shouldn't be here dividing in the intALU");
                break;
            case AND:    
                result = data1 & data2;
                break;    
            case ANDI: 
                result = data1 & data2;
                break;    
            case OR:  
                result = data1 | data2;
                break;
            case ORI:
                result = data1 | data2;
                break;
            case XOR:    
                result = data1 ^ data2;
                break;    
            case XORI: 
                result = data1 ^ data2;
                break;    
            case SLL:  
                result = data1 << data2;
                break;
            case SRL:
                result = data1 >> data2;
                break;
            case SRA:    
                result = data1 >>> data2;
                break;    
          //We potentially need theses they're in our 5 stage pipeline
          //  case "LW":
            //  return regA + immed;
          //  case "SW":
            //  return regA + immed;    
            default:
                System.out.println("You should be here INTALU default");
                
        }
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
