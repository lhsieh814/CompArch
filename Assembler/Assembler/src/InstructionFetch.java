/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Gavin
 */
import java.util.*;

public class InstructionFetch {

    private long PC;

    private boolean hitBTB = false;

    private ArrayList<String> InstructionQueue;

    public InstructionFetch(long InitialAddress) {
        PC = InitialAddress;
        InstructionQueue = new ArrayList<String>();
    }

    public void setPC(long PC) {
        this.PC = PC;
    }

    public long getPC() {
        return this.PC;
    }

    public void setHitOrMissBTB(boolean hitOrMiss) {
        this.hitBTB = hitOrMiss;
    }

    public void AddInstruction(String instruction) {
        this.InstructionQueue.add(instruction);

    }

    public String getInstruction(int issueIndex) {
        return this.InstructionQueue.get(issueIndex);
    }

    public void removeInstruction(int issueIndex) {
        this.InstructionQueue.remove(issueIndex);

    }

    public void CompleteFlush() {
        this.InstructionQueue.clear();
    }

    public int getIQsize() {
        return this.InstructionQueue.size();
    }

    public void displayIQ() {
        for (int i = 0; i < InstructionQueue.size(); i++) {
            System.out.println("[" + InstructionQueue.get(i) + "]");
        }
    }

}
