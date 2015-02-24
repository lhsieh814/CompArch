/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assembler;

import java.io.File;
import util.InstructionInfo;
import util.InstructionFetch;
import util.LoadStoreQueue;
import util.AddCalcRS;
import util.ArithLogicRS;
import util.LoadRS;
import util.ReorderBuffer;
import util.BranchTargetBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import util.StagesInCycle;

/**
 *
 * @author Yang Zhou
 */
public class Assembler {

    private static int byteCt = 0;

    private static int Cycle = 1;

    private static long PC = 596;

    private static LinkedHashMap<String, Long> Registers = new LinkedHashMap<String, Long>();

    public static ArrayList<String> Instructions = new ArrayList<String>();

    public static ArrayList<Long> Addresses = new ArrayList<Long>();

    private static ArrayList<Boolean> RegistersBusy = new ArrayList<Boolean>();

    private static ArrayList<String> NoImmediateALU = new ArrayList<String>();

    private static ArrayList<String> BranchOne = new ArrayList<String>();

    private static ArrayList<String> BranchTwo = new ArrayList<String>();

    private static ArrayList<String> ImmediateALU = new ArrayList<String>();

    private static ArrayList<String> ALUInstructions = new ArrayList<String>();

    private static long IQIssueIndex = 0;

    private static StagesInCycle thisCycle = new StagesInCycle();

    /*
     * Memory Access for Load
     */
    private static int LoadMemoryAccess = 1;
    /*
     * Commit Count per Cycle
     */
    private static int CommitCountPerCycle = 1;
    /*
     * Issue Count per Cycle
     */
    private static int IssueCountPerCycle = 1;
    /*
     * Branch Target Buffer
     */
    public static BranchTargetBuffer BTB = new BranchTargetBuffer();
    /*
     * ROB, ALUStation, LoadRS, AddCalcRS, LSQ 
     */
    private static int ROBIdInitial = 1;
    private static ReorderBuffer ROB = new ReorderBuffer();
    private static ArithLogicRS ALUStation = new ArithLogicRS();
    private static LoadRS LoadStation = new LoadRS();
    private static AddCalcRS AddCalcStation = new AddCalcRS();
    private static LoadStoreQueue LSQ = new LoadStoreQueue();

    /*
     * Address Units and Integer Units can have values 1 or 2
     */
    private static int AddressUnit = 1;
    private static int IntegerUnit = 1;

    private static StagesInCycle nextCycle = new StagesInCycle();

    private static InstructionFetch InstrFetch = new InstructionFetch(596);

    private static ArrayList<String> ByteBuffer = new ArrayList<String>();

    private static boolean theEnd = false;

    public static ArrayList<String> OutputBuffer = new ArrayList<String>();

    public static LinkedHashMap<Long, String> MainMemory = new LinkedHashMap<Long, String>();

    public static LinkedHashMap<Long, InstructionInfo> InstructionMap = new LinkedHashMap<Long, InstructionInfo>();

    private static int mTrace;

    private static int nTrace;

    private static boolean Break = false;

    private static String InputFileName = null;

    private static String OutputFileName = null;

    private static String Operation = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Use Command 
        if (args.length < 2) {

            System.out.println("Please atleast 2 arguments... \n"
                    + "... \t java MIPSsim InputFileName OutputFileName [-Tm:n]");

            System.exit(1);

        }
        // Use input file
        else {

            InputFileName = args[0];

            OutputFileName = args[1];
            File f = new File(OutputFileName);

            if (f.exists()) {

                f.delete();

            }
            if (args.length == 3) {
                if ((args[2].charAt(0) == '-')
                        && (args[2].charAt(1) == 'T')) {
                    String trace = args[2].substring(2);
                    String[] traces = trace.split(":");
                    mTrace = Integer.parseInt(traces[0]);
                    nTrace = Integer.parseInt(traces[1]);

                    System.out.println("mtrace: " + mTrace);
                    System.out.println("ntrace: " + nTrace);

                }
                else {
                    System.out.println("Please enter third argument in the format '-Tm:n'... \n"
                            + "... \t this argument is optional; 'T' is case sensitive; m, n can be integers where m <= n. ");

                }

            }

        }
    }

    /**
     *
     * Function to return 32-bit-String with 32 bit word as input argument. Size
     * of bytInt is 4 bytes.
     */
    private static String LineString(int[] bytInt) {

        StringBuffer Line = new StringBuffer();

        int bytMask;

        for (int byt : bytInt) {

            bytMask = ((int) byt & 0x00ff);

            for (int bit = 7; bit >= 0; bit--) {

                if ((bytMask & (1 << bit)) > 0) {
                    Line.append("1");
                }

                else {
                    Line.append("0");
                }
            }
        }
        return Line.toString();
    }

    /**
     * Initialize all registers with status
     */
    private static void InitializeRegisters() {
        for (int i = 0; i < 32; i++) {
            Registers.put("R" + i, 0L);
            RegistersBusy.add(i, false);
        }
    }

    /**
     * Convert Long type to Int
     *
     * @param L
     * @return
     */
    private static int LongToInt(long L) {
        if (L >= 2147483647) {
            return 2147483647;
        }
        else if (L <= -2147483648) {
            return -2147483648;
        }
        else {
            return (int) L;
        }
    }

}
