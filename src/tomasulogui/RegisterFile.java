package tomasulogui;

public class RegisterFile {

    PipelineSimulator simulator;
    // Value of the register
    int[] regs = new int[32];
    // Slot/Tag for register
    int[] robSlot = new int[32];

    public RegisterFile(PipelineSimulator sim) {
        simulator = sim;
        for (int i = 0; i < 32; i++) {
            robSlot[i] = -1;
        }
    }

    public int getReg(int regNum) {
        return regs[regNum];
    }

    public void setReg(int regNum, int regValue) {
        regs[regNum] = regValue;
    }

    public int getSlotForReg(int regNum) {
        return robSlot[regNum];
    }

    public void setSlotForReg(int regNum, int slot) {
        robSlot[regNum] = slot;
    }

    public void squashAll() {
        for (int i = 0; i < 32; i++) {
            robSlot[i] = -1;
        }
    }

    public void printRegFile() {
        for (int i = 0; i < 32; i++) {
            if (robSlot[i] != -1) {
                System.out.println("Reg " + i + " in Slot " + robSlot[i]);
            }
        }
    }

}
