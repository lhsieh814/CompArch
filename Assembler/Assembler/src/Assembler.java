/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
    /*
     * 
     */
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

    public static void main(String args[]) throws IOException {

        if (args.length < 2) {

            System.out.println("Please atleast 2 arguments... \n"
                    + "... \t java MIPSsim InputFileName OutputFileName [-Tm:n]");

            System.exit(1);

        }
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

        FileInputStream in = null;

        Repository Rp = new Repository();

        try {
            in = new FileInputStream(InputFileName);

            int byt;

            int[] bytInt = new int[4];
            /*
             * Read the input file and write into input string buffer.
             */
            while ((byt = in.read()) != -1) {

                bytInt[byteCt] = byt;

                byteCt++;

                if (byteCt == 4) {

                    byteCt = 0;

                    ByteBuffer.add(LineString(bytInt));

                }
            }

            in.close();

        }
        catch (FileNotFoundException fnfe) {

            System.out.println("Input File does not exist...");

            fnfe.printStackTrace();

        }
        catch (IOException e) {

            System.out.println("Input File I/O exception...");

            e.printStackTrace();

        }

        while (!ByteBuffer.isEmpty()) {
            /*
             * Process Each and every 32 bits represented as String.
             */

            Rp.ProcessLine(ByteBuffer.get(0));

            ByteBuffer.remove(0);

        }

        BufferedWriter writer = null;

        try {
            /*
             * Writing to ouput file.
             */
            writer = new BufferedWriter(new FileWriter("disassembly1.txt"));

            int LastIndex = OutputBuffer.size() - 1;

            int i = 0;

            for (String str : OutputBuffer) {

                if (i == LastIndex) {

                    str = str.replaceFirst("\n", "");

                }
                /*
                 * Write the output string buffer to output file.
                 */

                writer.write(str);

                i++;

            }

            writer.flush();

            writer.close();

        }
        catch (FileNotFoundException FNE) {

            FNE.printStackTrace();

        }
        catch (IOException IOE) {

            IOE.printStackTrace();

        }
        catch (Exception E) {

            E.printStackTrace();

        }

        System.out.println("The disassembly is saved in: disassembly1.txt");

        /*
         * 
         * 
         * Start Simulation.
         * 
         * 
         */
        InitializeRegisters();

        NoImmediateALU.add("SLT");
        NoImmediateALU.add("SLTU");
        NoImmediateALU.add("SUB");
        NoImmediateALU.add("SUBU");
        NoImmediateALU.add("ADD");
        NoImmediateALU.add("ADDU");
        NoImmediateALU.add("AND");
        NoImmediateALU.add("OR");
        NoImmediateALU.add("XOR");
        NoImmediateALU.add("NOR");

        ImmediateALU.add("SLTI");
        ImmediateALU.add("ADDI");
        ImmediateALU.add("ADDIU");
        ImmediateALU.add("SRL");
        ImmediateALU.add("SLL");
        ImmediateALU.add("SRA");

        ALUInstructions.addAll(NoImmediateALU);
        ALUInstructions.addAll(ImmediateALU);

        BranchOne.add("BGTZ");
        BranchOne.add("BGEZ");
        BranchOne.add("BLTZ");
        BranchOne.add("BLEZ");

        BranchTwo.add("BEQ");
        BranchTwo.add("BNE");

		//thisCycle.StagesAddress.add(PC);
        //thisCycle.Stages.add(InstructionMap.get(PC).getStage());
        while (!theEnd) {
            //if(Stage.equalsIgnoreCase("IF")) {

            int f = 0;

            /*
             * Add Instruction Fetch from the new PC.
             */
            long thisPC = InstrFetch.getPC();
            InstrFetch.AddInstruction(getInstructionInfo(thisPC).getInstruction());
            nextCycle.Stages.add("ISSUE");
            nextCycle.StagesAddress.add(thisPC);
            // check Branch Target Buffer
            // Add code based on Branch Prediction
            boolean hitBTB = false;
            for (int i = 0; i < BTB.Address.size(); i++) {
                if (BTB.Address.get(i) == thisPC) {
                    hitBTB = true;
                    InstrFetch.setPC(Assembler.BTB.PredictedPC.get(i));
                }
            }
            if (!hitBTB) {
                InstrFetch.setPC(thisPC + 4);
            }
            /*
             * Add ISSUE
             */

            while (!thisCycle.StagesAddress.isEmpty()) {

                PerformStage(thisCycle.StagesAddress.removeFirst(), thisCycle.Stages.removeFirst());

            }

            thisCycle.StagesAddress.addAll(nextCycle.StagesAddress);
            thisCycle.Stages.addAll(nextCycle.Stages);
            nextCycle.StagesAddress.clear();
            nextCycle.Stages.clear();
            CommitCountPerCycle = 1;
            IntegerUnit = 1;
            AddressUnit = 1;
            IssueCountPerCycle = 1;
            if (Cycle >= mTrace && Cycle <= nTrace) {
                syso();
            }
            else if (mTrace == 0 && nTrace == 0) {
                //break;
            }
            Cycle++;

        }

    }

    /*
     * Function to return 32-bit-String with 32 bit word as input argument.
     * Size of bytInt is 4 bytes.
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

    private static void InitializeRegisters() {
        for (int i = 0; i < 32; i++) {
            Registers.put("R" + i, 0L);
            RegistersBusy.add(i, false);
        }
    }

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

    private static void WriteOutput(ArrayList<String> str) {
        BufferedWriter writer = null;

        try {
            /*
             * Writing to ouput file.
             */
            writer = new BufferedWriter(new FileWriter(OutputFileName, true));

            for (String str1 : str) {

                writer.write(str1);

            }

            writer.flush();

            writer.close();

        }
        catch (FileNotFoundException FNE) {

            FNE.printStackTrace();

        }
        catch (IOException IOE) {

            IOE.printStackTrace();

        }
        catch (Exception E) {

            E.printStackTrace();

        }
    }

    private static void syso() {
        ArrayList<String> StringArray = new ArrayList<String>();
        StringArray.add("Cycle " + Cycle + ": \n");
        StringArray.add("IQ: \n");
        int size = InstrFetch.getIQsize();
        for (int i = 0; i < size; i++) {
            StringArray.add("[" + InstrFetch.getInstruction(i) + "]\n");
        }
        StringArray.add("RS-int: \n");
        for (int i = 0; i < ALUStation.DestID.size(); i++) {
            StringArray.add("[" + ALUStation.Instruction.get(i) + "]\n");
        }
        StringArray.add("RS-Load: \n");
        for (int i = 0; i < LoadStation.DestID.size(); i++) {
            StringArray.add("[" + LoadStation.Instruction.get(i) + "]\n");
        }
        StringArray.add("LSQ: \n");
        for (int i = 0; i < LSQ.DestID.size(); i++) {
            String Address = "Not Ready";
            if (LSQ.Address.get(i) != null) {
                Address = LSQ.Address.get(i).toString();
            }

            StringArray.add("[" + LSQ.Instruction.get(i) + "] <" + Address + ">\n");

        }
        StringArray.add("ROB: \n");
        StringArray.add("ROB Id : Instruction\n");
        for (int i = 0; i < ROB.Id.size(); i++) {
            StringArray.add(String.format("%6d", ROB.Id.get(i))
                    + " : [" + ROB.Instruction.get(i) + "]\n");
        }
        StringArray.add("BTB: \n");
        int i = 0;
        for (; i < BTB.Address.size(); i++) {
            StringArray.add("[Entry " + i + "]: <" + BTB.Address.get(i) + ", "
                    + BTB.TargetPC.get(i) + ", "
                    + BTB.PredictedPC.get(i) + ", "
                    + BTB.PredictorOutCome.get(i)
                    + ">\n");
        }
        for (; i < 8; i++) {
            StringArray.add("[Entry " + i + "]: < Empty >\n");
        }

        StringArray.add("Registers: \n");
        StringArray.add("R00: " + Registers.get("R0") + " "
                + Registers.get("R1") + " "
                + Registers.get("R2") + " "
                + Registers.get("R3") + " "
                + Registers.get("R4") + " "
                + Registers.get("R5") + " "
                + Registers.get("R6") + " "
                + Registers.get("R7") + "\n"
        );
        StringArray.add("R08: " + Registers.get("R8") + " "
                + Registers.get("R9") + " "
                + Registers.get("R10") + " "
                + Registers.get("R11") + " "
                + Registers.get("R12") + " "
                + Registers.get("R13") + " "
                + Registers.get("R14") + " "
                + Registers.get("R15") + "\n"
        );
        StringArray.add("R16: " + Registers.get("R16") + " "
                + Registers.get("R17") + " "
                + Registers.get("R18") + " "
                + Registers.get("R19") + " "
                + Registers.get("R20") + " "
                + Registers.get("R21") + " "
                + Registers.get("R22") + " "
                + Registers.get("R23") + "\n"
        );
        StringArray.add("R24: " + Registers.get("R24") + " "
                + Registers.get("R25") + " "
                + Registers.get("R26") + " "
                + Registers.get("R27") + " "
                + Registers.get("R28") + " "
                + Registers.get("R29") + " "
                + Registers.get("R30") + " "
                + Registers.get("R31") + "\n"
        );
        StringArray.add("Data Segment: \n");
        long Address = 700;
        ArrayList<String> DataSegment = new ArrayList<String>();
        for (int j = 0; j < MainMemory.size(); j++) {
            if (MainMemory.containsKey(Address)) {
                DataSegment.add(MainMemory.get(Address));
                Address += 4;
            }
        }
        Address = 700;
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < DataSegment.size(); j++) {
            if ((j != 0) && (j % 8 == 0)) {
                sb.append("\n");
                //System.out.println();
            }
            if (j % 8 == 0) {
                sb.append(Address + ":");
            }
            sb.append(" " + DataSegment.get(j));
            Address += 4;

        }
        StringArray.add(sb.toString() + "\n");
        StringArray.add("***************************************************************\n");
        WriteOutput(StringArray);
    }
    /*			
     Cycle 1:
     IQ : 
     [ADDI R8, R0, #20]
     RS-int :
     RS-load :
     LSQ :
     ROB :
     BTB:
     [Entry 0]:Empty
     [Entry 1]:Empty
     [Entry 2]:Empty
     [Entry 3]:Empty
     [Entry 4]:Empty
     [Entry 5]:Empty
     [Entry 6]:Empty
     [Entry 7]:Empty
     Registers :
     R00: 0 0 0 0 0 0 0 0
     R08: 0 0 0 0 0 0 0 0
     R16: 0 0 0 0 0 0 0 0
     R24: 0 0 0 0 0 0 0 0                      
     Data Segment :                          
     700: 0 0 0 0 0 0 0 0
     732: 0 0 0 0 0 0 0 0
     764: 0
     */

    private static InstructionInfo getInstructionInfo(long PC) {

        if (InstructionMap.containsKey(PC)) {
            return InstructionMap.get(PC);
        }
        else {
            InstructionInfo Info = new InstructionInfo();
            Info.setAddress(PC);
            Info.setInstruction("NOP");
            Info.setStage("IF");
            return Info;
        }

    }

    /*
     * Check for ROB Destination Entry
     */
    private static Long CheckROBDestination(int ROBindex, String Operand, String instruction) {
        boolean GetFromRegisters = true;
        Long returnValue = null;
        if (Registers.containsKey(Operand)) {
            for (int i = 0; i < ROBindex; i++) {
                if ((ROB.Destination.get(i) != null)
                        && (ROB.Destination.get(i).equalsIgnoreCase(Operand))) {
                    GetFromRegisters = false;
                    if (ROB.Value.get(i) != null) {
                        returnValue = ROB.Value.get(i);
                        break;
                    }
                }

            }
            if (GetFromRegisters) {
                // Bring in Values if available
                returnValue = Registers.get(Operand);
            }
            if (thisCycle.Stages.contains("WRITE RESULT")) {

            }
        }
        else {
            System.out.println("Unknown First Operand: instruction-->" + instruction);
        }
        return returnValue;
    }
    /*
     * Perform stages for a single Cycle
     */

    private static void PerformStage(long PC, String Stage) {
        if (Stage.equalsIgnoreCase("ISSUE")) {
            boolean onTop = false;
            String instr = getInstructionInfo(PC).getInstruction();
            //InstructionMap.get(PC).getInstruction();
            String instruction = InstrFetch.getInstruction(0);
            if (instr.equalsIgnoreCase(instruction)) {
                onTop = true;
            }

            if ((IssueCountPerCycle == 1)
                    && (onTop)) {

                String[] splitParts = instruction.split(" ");
                if ((splitParts[0].equalsIgnoreCase("NOP"))
                        || (splitParts[0].equalsIgnoreCase("BREAK"))) {
                    if (ROB.Id.size() < 6) {
                        ROB.Id.add(ROBIdInitial);
                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);
                        nextCycle.Stages.add("COMMIT");
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }
                        IssueCountPerCycle++;
                        InstrFetch.removeInstruction(0);

                    }
                    else {
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }

                }

                if (BranchOne.contains(splitParts[0])) {
                    if (ROB.Id.size() < 6) {
                        ROB.Id.add(ROBIdInitial);
                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);

                        String FirstOperand = splitParts[1].substring(0, splitParts[1].indexOf(","));
                        long TargetPC = PC + 4 + Long.parseLong(splitParts[2].substring(1));
                        Long FirstValue = null;

                        /*
                         * During ISSUE stage of Branch operations, if the operands are available in
                         * either register file or ROB, resolve them here.
                         */
                        // First Operand
                        FirstValue = CheckROBDestination(ROBindex, FirstOperand, instruction);

                        if ((FirstValue != null)) {
                            //Can be resolved here; Next Go to commit stage.
                            nextCycle.Stages.add("COMMIT");
                            nextCycle.StagesAddress.add((long) ROBIdInitial);
                            InstrFetch.removeInstruction(0);
                            IssueCountPerCycle++;
                            boolean result = false;
                            if (splitParts[0].equalsIgnoreCase("BLTZ")) {
                                if (FirstValue < 0) {
                                    result = true;
                                }
                            }
                            if (splitParts[0].equalsIgnoreCase("BGTZ")) {
                                if (FirstValue > 0) {
                                    result = true;
                                }
                            }
                            if (splitParts[0].equalsIgnoreCase("BLEZ")) {
                                if (FirstValue <= 0) {
                                    result = true;
                                }
                            }
                            if (splitParts[0].equalsIgnoreCase("BGEZ")) {
                                if (FirstValue >= 0) {
                                    result = true;
                                }
                            }
                            boolean BTBBranchHit = false;
                            int BTBindex = -1;
                            for (int i = 0; i < BTB.Address.size(); i++) {
                                if (BTB.Address.get(i) == PC) {
                                    BTBBranchHit = true;
                                    BTBindex = i;
                                }
                            }
                            if (!BTBBranchHit) {
                                if (BTB.Address.size() < 8) {
                                    BTBFree();
                                }
                                BTB.Address.add(PC);
                                BTBindex = BTB.Address.indexOf(PC);
                                if (result) {
                                    BTB.Predictor2Bit.add(BTBindex, "Weak Taken");
                                    BTB.PredictorOutCome.add(BTBindex, "Taken");
                                    BTB.PredictedPC.add(BTBindex, TargetPC);
                                    BTB.TargetPC.add(BTBindex, TargetPC);
                                    // Initially Prediction was Not taken...
                                    InstrFetch.CompleteFlush();
                                    for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                        if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                            thisCycle.StagesAddress.remove(j);
                                            thisCycle.Stages.remove(j);
                                            j--;
                                        }
                                    }
                                    for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                        if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                            nextCycle.StagesAddress.remove(j);
                                            nextCycle.Stages.remove(j);
                                            j--;
                                        }
                                    }

                                    //nextCycle.Stages.add("IF");
                                    //nextCycle.StagesAddress.add(TargetPC);
                                    InstrFetch.setPC(TargetPC);

                                }
                                else {
                                    BTB.Predictor2Bit.add(BTBindex, "Weak Not Taken");
                                    BTB.PredictorOutCome.add(BTBindex, "Not Taken");
                                    BTB.PredictedPC.add(BTBindex, PC + 4);
                                    BTB.TargetPC.add(BTBindex, TargetPC);
                                }
                            }
                            else {
                                // If Branch Hit 
                                if (result) {
                                    // see whether prediction was correct...
                                    if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Not Taken")) {
                                        // misprediction
                                        InstrFetch.CompleteFlush();
                                        for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                            if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                thisCycle.StagesAddress.remove(j);
                                                thisCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                            if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                nextCycle.StagesAddress.remove(j);
                                                nextCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        //nextCycle.Stages.add("IF");
                                        //nextCycle.StagesAddress.add(TargetPC);
                                        InstrFetch.setPC(TargetPC);
                                    }
                                    // setting the 2 bit predictor outcome
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Weak Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }

                                }
                                else {
                                    // see whether prediction was correct...
                                    if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Taken")) {
                                        // misprediction
                                        InstrFetch.CompleteFlush();
                                        for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                            if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                thisCycle.StagesAddress.remove(j);
                                                thisCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                            if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                nextCycle.StagesAddress.remove(j);
                                                nextCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        InstrFetch.setPC(TargetPC);
                                        //nextCycle.Stages.add("IF");
                                        //nextCycle.StagesAddress.add(PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Weak Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }

                                }
                                BTB.TargetPC.set(BTBindex, TargetPC);
                            }
                            if (ROBIdInitial == 6) {
                                ROBIdInitial = 1;
                            }
                            else {
                                ROBIdInitial++;
                            }
                        }
                        else {
                            /*
                             *  Operands' values are not available, allocate ALU station and resolve in Execute stage... 
                             *  if ALU station is not available, remove ROBId entry...ISSUE in the next cycle
                             */

                            if (ALUStation.DestID.size() < 3) {
                                ALUStation.DestID.add(ROBIdInitial);
                                int ALUindex = ALUStation.DestID.indexOf(ROBIdInitial);
                                ALUStation.Instruction.add(ALUindex, instruction);
                                ALUStation.Vj.add(ALUindex, FirstOperand);
                                ALUStation.Vk.add(ALUindex, null);
                                ALUStation.ValueVj.add(ALUindex, null);
                                ALUStation.ValueVk.add(ALUindex, null);
                                // Send PC through ALU Station to the next stage
                                ALUStation.ValueImm.add(ALUindex, PC);
                                ALUStation.DestValue.add(ALUindex, null);

                                nextCycle.Stages.add("EXECUTE");
                                nextCycle.StagesAddress.add((long) ROBIdInitial);

                                if (ROBIdInitial == 6) {
                                    ROBIdInitial = 1;
                                }
                                else {
                                    ROBIdInitial++;
                                }
                                IssueCountPerCycle++;
                                InstrFetch.removeInstruction(0);
                            }
                            else {
                                FreeROB(ROBindex);
                                nextCycle.Stages.add("ISSUE");
                                nextCycle.StagesAddress.add(PC);
                            }

                        }

                    }
                    else {
                        System.out.println("ROB Full for instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }

                }
                if (BranchTwo.contains(splitParts[0])) {
                    if (ROB.Id.size() < 6) {
                        ROB.Id.add(ROBIdInitial);
                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);

                        String FirstOperand = splitParts[1].substring(0, splitParts[1].indexOf(","));
                        String SecondOperand = splitParts[2].substring(0, splitParts[2].indexOf(","));
                        long TargetPC = PC + 4 + Long.parseLong(splitParts[3].substring(1));
                        Long FirstValue = null;
                        Long SecondValue = null;
                        /*
                         * During ISSUE stage of Branch operations, if the operands are available in
                         * either register file or ROB, resolve them here.
                         */
                        // First Operand
                        FirstValue = CheckROBDestination(ROBindex, FirstOperand, instruction);

                        // Second Operand
                        SecondValue = CheckROBDestination(ROBindex, SecondOperand, instruction);

                        if ((FirstValue != null) && (SecondValue != null)) {
                            //Can be resolved here; Next Go to commit stage.
                            nextCycle.Stages.add("COMMIT");
                            nextCycle.StagesAddress.add((long) ROBIdInitial);
                            InstrFetch.removeInstruction(0);
                            IssueCountPerCycle++;
                            boolean result = false;
                            if (splitParts[0].equalsIgnoreCase("BEQ")) {
                                if (SecondValue == FirstValue) {
                                    result = true;
                                }
                            }
                            if (splitParts[0].equalsIgnoreCase("BNE")) {
                                if (SecondValue != FirstValue) {
                                    result = true;
                                }
                            }
                            boolean BTBBranchHit = false;
                            int BTBindex = -1;
                            for (int i = 0; i < BTB.Address.size(); i++) {
                                if (BTB.Address.get(i) == PC) {
                                    BTBBranchHit = true;
                                    BTBindex = i;
                                }
                            }
                            if (!BTBBranchHit) {
                                if (BTB.Address.size() > 8) {
                                    BTBFree();
                                }
                                BTB.Address.add(PC);
                                BTBindex = BTB.Address.indexOf(PC);
                                if (result) {
                                    BTB.Predictor2Bit.add(BTBindex, "Weak Taken");
                                    BTB.PredictorOutCome.add(BTBindex, "Taken");
                                    BTB.PredictedPC.add(BTBindex, TargetPC);
                                    BTB.TargetPC.add(BTBindex, TargetPC);
                                    // Initially Prediction was Not taken...
                                    InstrFetch.CompleteFlush();
                                    for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                        if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                            thisCycle.StagesAddress.remove(j);
                                            thisCycle.Stages.remove(j);
                                            j--;
                                        }
                                    }
                                    for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                        if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                            nextCycle.StagesAddress.remove(j);
                                            nextCycle.Stages.remove(j);
                                            j--;
                                        }
                                    }
                                    //nextCycle.Stages.add("IF");
                                    //nextCycle.StagesAddress.add(TargetPC);
                                    InstrFetch.setPC(TargetPC);

                                }
                                else {
                                    BTB.Predictor2Bit.add(BTBindex, "Weak Not Taken");
                                    BTB.PredictorOutCome.add(BTBindex, "Not Taken");
                                    BTB.PredictedPC.add(BTBindex, PC + 4);
                                    BTB.TargetPC.add(BTBindex, TargetPC);
                                }
                            }
                            else {
                                // If Branch Hit 
                                if (result) {
                                    // see whether prediction was correct...
                                    if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Not Taken")) {
                                        // misprediction
                                        InstrFetch.CompleteFlush();
                                        for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                            if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                thisCycle.StagesAddress.remove(j);
                                                thisCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                            if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                                nextCycle.StagesAddress.remove(j);
                                                nextCycle.Stages.remove(j);
                                                j--;
                                            }
                                        }
                                        //nextCycle.Stages.add("IF");
                                        //nextCycle.StagesAddress.add(TargetPC);
                                        InstrFetch.setPC(TargetPC);
                                    }
                                    // setting the 2 bit predictor outcome
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Weak Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }

                                }
                                else {
                                    // see whether prediction was correct...
                                    if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Taken")) {
                                        // misprediction
                                        InstrFetch.CompleteFlush();
                                        nextCycle.Stages.clear();
                                        nextCycle.StagesAddress.clear();
                                        InstrFetch.setPC(TargetPC);
                                        //nextCycle.Stages.add("IF");
                                        //nextCycle.StagesAddress.add(PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Weak Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Taken");
                                        BTB.PredictedPC.set(BTBindex, TargetPC);
                                    }
                                    if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                        BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                        BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                        BTB.PredictedPC.set(BTBindex, PC + 4);
                                    }

                                }
                                BTB.TargetPC.set(BTBindex, TargetPC);
                            }
                            if (ROBIdInitial == 6) {
                                ROBIdInitial = 1;
                            }
                            else {
                                ROBIdInitial++;
                            }

                        }
                        else {
                            /*
                             *  Operands' values are not available, allocate ALU station and resolve in Execute stage... 
                             *  if ALU station is not available, remove ROBId entry...ISSUE in the next cycle
                             */

                            if (ALUStation.DestID.size() < 3) {
                                ALUStation.DestID.add(ROBIdInitial);
                                int ALUindex = ALUStation.DestID.indexOf(ROBIdInitial);
                                ALUStation.Instruction.add(ALUindex, instruction);
                                ALUStation.Vj.add(ALUindex, FirstOperand);
                                ALUStation.Vk.add(ALUindex, SecondOperand);
                                ALUStation.ValueVj.add(ALUindex, null);
                                ALUStation.ValueVk.add(ALUindex, null);
                                // Set Operands' values if at least one is present
                                if (FirstValue != null) {
                                    ALUStation.ValueVj.set(ALUindex, FirstValue);
                                }
                                if (SecondValue != null) {
                                    ALUStation.ValueVk.set(ALUindex, SecondValue);
                                }
                                // Send PC through ALU Station to the next stage
                                ALUStation.ValueImm.add(ALUindex, PC);
                                ALUStation.DestValue.add(ALUindex, null);

                                nextCycle.Stages.add("EXECUTE");
                                nextCycle.StagesAddress.add((long) ROBIdInitial);

                                if (ROBIdInitial == 6) {
                                    ROBIdInitial = 1;
                                }
                                else {
                                    ROBIdInitial++;
                                }
                                InstrFetch.removeInstruction(0);
                                IssueCountPerCycle++;
                            }
                            else {
                                FreeROB(ROBindex);
                                nextCycle.Stages.add("ISSUE");
                                nextCycle.StagesAddress.add(PC);
                            }

                        }

                    }
                    else {
                        System.out.println("ROB Full for instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }

                }
                if (splitParts[0].equalsIgnoreCase("J")) {
                    if (ROB.Id.size() < 6) {
                        ROB.Id.add(ROBIdInitial);
                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        // Destination, Instruction, Value, Ready
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);

                        boolean BTBJumpHit = false;
                        for (int i = 0; i < BTB.Address.size(); i++) {
                            if (BTB.Address.get(i) == PC) {
                                BTBJumpHit = true;
                            }
                        }
                        if (!BTBJumpHit) {
                            if (BTB.Address.size() > 8) {
                                BTBFree();
                            }
                            long JumpAddress = Long.parseLong(splitParts[1].substring(1));
                            BTB.Address.add(PC);
                            int BTBindex = BTB.Address.indexOf(PC);
                            BTB.Predictor2Bit.add(BTBindex, "Strong Taken");
                            BTB.PredictorOutCome.add(BTBindex, "Taken");
                            BTB.PredictedPC.add(BTBindex, JumpAddress);
                            BTB.TargetPC.add(BTBindex, JumpAddress);
                            /*
                             * Flush Instruction Queue
                             */
                            InstrFetch.CompleteFlush();
                            for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                                if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                    thisCycle.StagesAddress.remove(j);
                                    thisCycle.Stages.remove(j);
                                    j--;
                                }
                            }
                            for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                                if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                                    nextCycle.StagesAddress.remove(j);
                                    nextCycle.Stages.remove(j);
                                    j--;
                                }
                            }
                            nextCycle.StagesAddress.clear();
                            nextCycle.Stages.clear();
                            /*
                             *  set PC in InstructionFetch class to be JumpAddress, 
                             *  so that PC is set accordingly after fetch at JumpAddress.
                             */
                            InstrFetch.setPC(JumpAddress);
                            // Next Cycle fetch from JumpAddress
                            //nextCycle.Stages.add("IF");
                            //nextCycle.StagesAddress.add(JumpAddress);
                        }
                        else {
                            InstrFetch.removeInstruction(0);
                        }
                        IssueCountPerCycle++;

                        // Next Cycle- Commit this Jump instruction
                        nextCycle.Stages.add("COMMIT");
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }

                    }
                    else {
                        System.out.println("ROB Full for instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }

                }
                if (ImmediateALU.contains(splitParts[0])) {
                    //ROB,ALUStation
                    if ((ROB.Id.size() < 6)
                            && (ALUStation.DestID.size() < 3)) {
                        //Id
                        ROB.Id.add(ROBIdInitial);
                        ALUStation.DestID.add(ROBIdInitial);

                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        int ALUindex = ALUStation.DestID.indexOf(ROBIdInitial);

                        String ROBDestination = splitParts[1].substring(0, splitParts[1].indexOf(","));
                        String FirstOperand = splitParts[2].substring(0, splitParts[2].indexOf(","));
                        String SecondOperand = splitParts[3];
                        // Destination, Instruction, Value, Ready
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);
                        // ALU station - Allocate 
                        ALUStation.Instruction.add(ALUindex, instruction);
                        ALUStation.Vj.add(ALUindex, FirstOperand);
                        ALUStation.Vk.add(ALUindex, null);
                        ALUStation.ValueVj.add(ALUindex, null);
                        ALUStation.ValueVk.add(ALUindex, null);
                        ALUStation.ValueImm.add(ALUindex, Long.parseLong(SecondOperand.substring(1)));
                        ALUStation.DestValue.add(ALUindex, null);

                        /*
                         * During ISSUE stage of ALU operations, if the operands are available in
                         * either register file or ROB, write it to ALU Stations.
                         */
                        ALUStation.ValueVj.set(ALUindex, CheckROBDestination(ROBindex, FirstOperand, instruction));
                        ROB.Destination.set(ROBindex, ROBDestination);
                        /*
                         *  ISSUE is done...
                         *  Next Cycle is EXECUTE
                         */
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        nextCycle.Stages.add("EXECUTE");
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }
                        InstrFetch.removeInstruction(0);
                        IssueCountPerCycle++;
                    }
                    else {
                        System.out.println("ROB,ALUStation,etc. Full for instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }
                }
                if (NoImmediateALU.contains(splitParts[0])) {
                    //ROB,ALUStation
                    if ((ROB.Id.size() < 6)
                            && (ALUStation.DestID.size() < 3)) {
                        //Id
                        ROB.Id.add(ROBIdInitial);
                        ALUStation.DestID.add(ROBIdInitial);

                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        int ALUindex = ALUStation.DestID.indexOf(ROBIdInitial);

                        String ROBDestination = splitParts[1].substring(0, splitParts[1].indexOf(","));
                        String FirstOperand = splitParts[2].substring(0, splitParts[2].indexOf(","));
                        String SecondOperand = splitParts[3];

                        // Destination, Instruction, Value, Ready
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);
                        // ALU station - Allocate 
                        ALUStation.Instruction.add(ALUindex, instruction);
                        ALUStation.Vj.add(ALUindex, FirstOperand);
                        ALUStation.Vk.add(ALUindex, SecondOperand);
                        ALUStation.ValueVj.add(ALUindex, null);
                        ALUStation.ValueVk.add(ALUindex, null);
                        ALUStation.ValueImm.add(ALUindex, null);
                        ALUStation.DestValue.add(ALUindex, null);

                        /*
                         * During ISSUE stage of ALU operations, if the operands are available in
                         * either register file or ROB, write it to ALU Stations.
                         */
                        ALUStation.ValueVj.set(ALUindex, CheckROBDestination(ROBindex, FirstOperand, instruction));
                        ALUStation.ValueVk.set(ALUindex, CheckROBDestination(ROBindex, SecondOperand, instruction));
                        ROB.Destination.set(ROBindex, ROBDestination);
                        /*
                         *  ISSUE is done...
                         *  Next Cycle is EXECUTE
                         */
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        nextCycle.Stages.add("EXECUTE");
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }
                        InstrFetch.removeInstruction(0);
                        IssueCountPerCycle++;

                    }
                    else {
                        System.out.println("ROB,ALUStation,etc. Full for instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }
                }
                if (instruction.substring(0, 2).equalsIgnoreCase("LW")) {
                    //ROB,LoadStation,AddCalcStation,LSQ
                    if ((ROB.Id.size() < 6)
                            && (LoadStation.DestID.size() < 2)
                            && (AddCalcStation.DestID.size() < 2)
                            && (LSQ.DestID.size() < 4)) {
                        ROB.Id.add(ROBIdInitial);
                        LoadStation.DestID.add(ROBIdInitial);
                        AddCalcStation.DestID.add(ROBIdInitial);
                        LSQ.DestID.add(ROBIdInitial);

                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        int LSindex = LoadStation.DestID.indexOf(ROBIdInitial);
                        int ACRSindex = AddCalcStation.DestID.indexOf(ROBIdInitial);
                        int LSQindex = LSQ.DestID.indexOf(ROBIdInitial);

                        // Id, Destination, Instruction, Value, Ready
                        String ROBDestination = instruction.substring(3, instruction.indexOf(","));

                        //
                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);
                        //
                        LoadStation.Instruction.add(LSindex, instruction);
                        LoadStation.Operand.add(LSindex, ROBDestination);
                        LoadStation.Value.add(LSindex, null);
                        // Allocate AC RS
                        String base = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")"));
                        AddCalcStation.base.add(ACRSindex, base);
                        String offset = instruction.substring(instruction.indexOf(",") + 2, instruction.indexOf("("));
                        AddCalcStation.offset.add(ACRSindex, Long.parseLong(offset));
                        AddCalcStation.Value.add(ACRSindex, null);
                        // Add base value to the AC RS
                        AddCalcStation.Value.set(ACRSindex, CheckROBDestination(ROBindex, base, instruction));
                        ROB.Destination.set(ROBindex, ROBDestination);
                        // Allocate LSQ
                        LSQ.Address.add(LSQindex, null);
                        LSQ.value.add(LSQindex, null);
                        LSQ.Instruction.add(LSQindex, instruction);
                        LSQ.ExecState.add(LSQindex, "FIRST CYCLE");
                        // ISSUE is done...
                        // Next Cycle is EXECUTE
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        nextCycle.Stages.add("EXECUTE");
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }
                        InstrFetch.removeInstruction(0);
                        IssueCountPerCycle++;

                    }
                    else {
                        System.out.println("ROB,LSQ,etc. Full for LW instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }
                }

                if (instruction.substring(0, 2).equalsIgnoreCase("SW")) {
                    //ROB,AddCalcStation,LSQ
                    if ((ROB.Id.size() < 6)
                            && (AddCalcStation.DestID.size() < 2)
                            && (LSQ.DestID.size() < 4)) {
                        ROB.Id.add(ROBIdInitial);
                        AddCalcStation.DestID.add(ROBIdInitial);
                        LSQ.DestID.add(ROBIdInitial);

                        int ROBindex = ROB.Id.indexOf(ROBIdInitial);
                        int ACRSindex = AddCalcStation.DestID.indexOf(ROBIdInitial);
                        int LSQindex = LSQ.DestID.indexOf(ROBIdInitial);
                        // Id, Destination, Instruction, Value, Ready

                        ROB.Destination.add(ROBindex, null);
                        ROB.Instruction.add(ROBindex, instruction);
                        ROB.Ready.add(ROBindex, false);
                        ROB.Value.add(ROBindex, null);
                        String StoreSourceRegister = splitParts[1].substring(0, splitParts[1].length() - 1);
                        // Allocate AC RS
                        String base = instruction.substring(instruction.indexOf("(") + 1, instruction.indexOf(")"));
                        AddCalcStation.base.add(ACRSindex, base);
                        String offset = instruction.substring(instruction.indexOf(",") + 2, instruction.indexOf("("));
                        AddCalcStation.offset.add(ACRSindex, Long.parseLong(offset));
                        AddCalcStation.Value.add(ACRSindex, CheckROBDestination(ROBindex, base, instruction));

                        // Allocate LSQ
                        LSQ.Address.add(LSQindex, null);
                        LSQ.value.add(LSQindex, CheckROBDestination(ROBindex, StoreSourceRegister, instruction));
                        LSQ.Instruction.add(LSQindex, instruction);
                        LSQ.ExecState.add(LSQindex, "FIRST CYCLE");
                        // ISSUE done..!!
                        // Next cycle - EXECUTE .. Identify using ROB id.
                        nextCycle.StagesAddress.add((long) ROBIdInitial);
                        nextCycle.Stages.add("EXECUTE");
                        if (ROBIdInitial == 6) {
                            ROBIdInitial = 1;
                        }
                        else {
                            ROBIdInitial++;
                        }
                        // remove from Instruction Queue
                        InstrFetch.removeInstruction(0);
                        IssueCountPerCycle++;
                    }
                    else {
                        System.out.println("ROB,LSQ,etc. Full for LW instruction ISSUE... \nAdded ISSUE to NextCycle");
                        nextCycle.Stages.add("ISSUE");
                        nextCycle.StagesAddress.add(PC);
                    }
                }

            }
            else {

                nextCycle.Stages.add("ISSUE");
                nextCycle.StagesAddress.add(PC);

            }

        }

        if (Stage.equalsIgnoreCase("EXECUTE")) {

            int ROBId = (int) PC;
            String instruction = ROB.Instruction.get(ROB.Id.indexOf(ROBId));
            String[] splitParts = instruction.split(" ");
            if (BranchOne.contains(splitParts[0])) {
                int ALUindex = ALUStation.DestID.indexOf(ROBId);
                int ROBindex = ROB.Id.indexOf(ROBId);
                if ((ALUStation.ValueVj.get(ALUindex) != null)
                        && ((IntegerUnit == 1) || (IntegerUnit == 2))) {
                    //Can be resolved here; Next Go to commit stage.
                    nextCycle.Stages.add("COMMIT");
                    nextCycle.StagesAddress.add(PC);

                    IntegerUnit++;
                    long FirstValue = ALUStation.ValueVj.get(ALUindex);
                    long ProgCounter = ALUStation.ValueImm.get(ALUindex);
                    long TargetPC = ProgCounter + 4 + Long.parseLong(splitParts[3].substring(1));

                    boolean result = false;
                    if (splitParts[0].equalsIgnoreCase("BLTZ")) {
                        if (FirstValue < 0) {
                            result = true;
                        }
                    }
                    if (splitParts[0].equalsIgnoreCase("BGTZ")) {
                        if (FirstValue > 0) {
                            result = true;
                        }
                    }
                    if (splitParts[0].equalsIgnoreCase("BLEZ")) {
                        if (FirstValue <= 0) {
                            result = true;
                        }
                    }
                    if (splitParts[0].equalsIgnoreCase("BGEZ")) {
                        if (FirstValue >= 0) {
                            result = true;
                        }
                    }
                    FreeALUStation(ALUindex);

                    boolean BTBBranchHit = false;
                    int BTBindex = -1;
                    for (int i = 0; i < BTB.Address.size(); i++) {
                        if (BTB.Address.get(i) == ProgCounter) {
                            BTBBranchHit = true;
                            BTBindex = i;
                        }
                    }
                    if (!BTBBranchHit) {
                        if (BTB.Address.size() < 8) {
                            BTBFree();
                        }
                        BTB.Address.add(PC);
                        BTBindex = BTB.Address.indexOf(PC);
                        if (result) {
                            BTB.Predictor2Bit.add(BTBindex, "Weak Taken");
                            BTB.PredictorOutCome.add(BTBindex, "Taken");
                            BTB.PredictedPC.add(BTBindex, TargetPC);
                            BTB.TargetPC.add(BTBindex, TargetPC);
                            // Initially Prediction was Not taken...
							/*
                             * Flush ROB entries just above the current ROBindex
                             */
                            MisPredictedFlush(ROBindex);
                            InstrFetch.CompleteFlush();
                            InstrFetch.setPC(TargetPC);
                            //nextCycle.Stages.add("IF");
                            //nextCycle.StagesAddress.add(TargetPC);

                        }
                        else {
                            BTB.Predictor2Bit.add(BTBindex, "Weak Not Taken");
                            BTB.PredictorOutCome.add(BTBindex, "Not Taken");
                            BTB.PredictedPC.add(BTBindex, ProgCounter + 4);
                            BTB.TargetPC.add(BTBindex, TargetPC);
                        }
                    }
                    else {
                        // 
                        assert (BTBindex != -1);
                        // If Branch Hit

                        if (result) {
                            // see whether prediction was correct...
                            if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Not Taken")) {
                                // misprediction
								/*
                                 * Flush ROB entries just above the current ROBindex
                                 */
                                MisPredictedFlush(ROBindex);
                                InstrFetch.CompleteFlush();

                                InstrFetch.setPC(TargetPC);
                                //nextCycle.Stages.add("IF");
                                //nextCycle.StagesAddress.add(TargetPC);
                            }
                            // setting the 2 bit predictor outcome
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Weak Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }

                        }
                        else {
                            // see whether prediction was correct...
                            if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Taken")) {
                                // misprediction
								/*
                                 * Flush ROB entries just above the current ROBindex
                                 */
                                MisPredictedFlush(ROBindex);
                                InstrFetch.CompleteFlush();

                                InstrFetch.setPC(TargetPC);
                                //nextCycle.Stages.add("IF");
                                //nextCycle.StagesAddress.add(PC + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Weak Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }

                        }
                        BTB.TargetPC.set(BTBindex, TargetPC);
                    }

                }
                else {
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("EXECUTE");
                }
            }
            if (BranchTwo.contains(splitParts[0])) {
                int ALUindex = ALUStation.DestID.indexOf(ROBId);
                int ROBindex = ROB.Id.indexOf(ROBId);
                if ((ALUStation.ValueVj.get(ALUindex) != null)
                        && (ALUStation.ValueVk.get(ALUindex) != null)
                        && ((IntegerUnit == 1) || (IntegerUnit == 2))) {
                    //Can be resolved here; Next Go to commit stage.
                    nextCycle.Stages.add("COMMIT");
                    nextCycle.StagesAddress.add(PC);

                    IntegerUnit++;

                    long ProgCounter = ALUStation.ValueImm.get(ALUindex);
                    long TargetPC = ProgCounter + 4 + Long.parseLong(splitParts[3].substring(1));

                    boolean result = false;
                    if (splitParts[0].equalsIgnoreCase("BEQ")) {
                        if (ALUStation.ValueVj.get(ALUindex) == ALUStation.ValueVk.get(ALUindex)) {
                            result = true;
                        }
                    }
                    if (splitParts[0].equalsIgnoreCase("BNE")) {
                        if (ALUStation.ValueVj.get(ALUindex) != ALUStation.ValueVk.get(ALUindex)) {
                            result = true;
                        }
                    }
                    boolean BTBBranchHit = false;
                    int BTBindex = -1;
                    for (int i = 0; i < BTB.Address.size(); i++) {
                        if (BTB.Address.get(i) == ProgCounter) {
                            BTBBranchHit = true;
                            BTBindex = i;
                        }
                    }
                    FreeALUStation(ALUindex);
                    if (!BTBBranchHit) {
                        if (BTB.Address.size() < 8) {
                            BTBFree();
                        }
                        BTB.Address.add(PC);
                        BTBindex = BTB.Address.indexOf(PC);
                        if (result) {
                            BTB.Predictor2Bit.add(BTBindex, "Weak Taken");
                            BTB.PredictorOutCome.add(BTBindex, "Taken");
                            BTB.PredictedPC.add(BTBindex, TargetPC);
                            BTB.TargetPC.add(BTBindex, TargetPC);
                            // Initially Prediction was Not taken...
							/*
                             * Flush ROB entries just above the current ROBindex
                             */
                            MisPredictedFlush(ROBindex);
                            InstrFetch.CompleteFlush();

                            InstrFetch.setPC(TargetPC);
                            //nextCycle.Stages.add("IF");
                            //nextCycle.StagesAddress.add(TargetPC);

                        }
                        else {
                            BTB.Predictor2Bit.add(BTBindex, "Weak Not Taken");
                            BTB.PredictorOutCome.add(BTBindex, "Not Taken");
                            BTB.PredictedPC.add(BTBindex, ProgCounter + 4);
                            BTB.TargetPC.add(BTBindex, TargetPC);
                        }
                    }
                    else {
                        // 
                        assert (BTBindex != -1);
                        // If Branch Hit

                        if (result) {
                            // see whether prediction was correct...
                            if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Not Taken")) {
                                // misprediction
								/*
                                 * Flush ROB entries just above the current ROBindex
                                 */
                                MisPredictedFlush(ROBindex);
                                InstrFetch.CompleteFlush();

                                InstrFetch.setPC(TargetPC);
                                //nextCycle.Stages.add("IF");
                                //nextCycle.StagesAddress.add(TargetPC);
                            }
                            // setting the 2 bit predictor outcome
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Weak Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }

                        }
                        else {
                            // see whether prediction was correct...
                            if (BTB.PredictorOutCome.get(BTBindex).equalsIgnoreCase("Taken")) {
                                // misprediction
								/*
                                 * Flush ROB entries just above the current ROBindex
                                 */
                                MisPredictedFlush(ROBindex);
                                InstrFetch.CompleteFlush();

                                InstrFetch.setPC(TargetPC);
                                //nextCycle.Stages.add("IF");
                                //nextCycle.StagesAddress.add(PC + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Strong Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Weak Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Taken");
                                BTB.PredictedPC.set(BTBindex, TargetPC);
                            }
                            if (BTB.Predictor2Bit.get(BTBindex).equalsIgnoreCase("Weak Not Taken")) {
                                BTB.Predictor2Bit.set(BTBindex, "Strong Not Taken");
                                BTB.PredictorOutCome.set(BTBindex, "Not Taken");
                                BTB.PredictedPC.set(BTBindex, ProgCounter + 4);
                            }

                        }
                        BTB.TargetPC.set(BTBindex, TargetPC);
                    }

                }
                else {
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("EXECUTE");
                }
            }

            if (ImmediateALU.contains(splitParts[0])) {
                int ALUindex = ALUStation.DestID.indexOf(ROBId);
                int ROBindex = ROB.Id.indexOf(ROBId);
                if ((ALUStation.ValueVj.get(ALUindex) != null)
                        && ((IntegerUnit == 1) || (IntegerUnit == 2))) {

                    // IntegerUnit incremented for this cycle.	
                    IntegerUnit++;
                    //ImmediateALU.add("SLTI");
                    if (splitParts[0].equalsIgnoreCase("SLTI")) {
                        long result = 0;
                        if (ALUStation.ValueVj.get(ALUindex) < ALUStation.ValueImm.get(ALUindex)) {
                            result = 1;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    //ImmediateALU.add("ADDI"); Overflow Exception occurs;
                    if (splitParts[0].equalsIgnoreCase("ADDI")) {
                        long result = ALUStation.ValueVj.get(ALUindex) + ALUStation.ValueImm.get(ALUindex);
                        if ((result < -2147483648) || (result > 2147483647)) {
                            System.out.println("IntegerOverFlow exception for instruction: " + instruction);
                            System.exit(1);
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    //ImmediateALU.add("ADDIU"); "Unsigned" is a Misnomer; No Overflow 
                    if (splitParts[0].equalsIgnoreCase("ADDIU")) {
                        long result = ALUStation.ValueVj.get(ALUindex) + ALUStation.ValueImm.get(ALUindex);
                        if (result < -2147483648) {
                            result = -2147483648;
                        }
                        else if (result > 2147483647) {
                            result = 2147483647;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    //"SRL" : 
                    if (splitParts[0].equalsIgnoreCase("SRL")) {
                        int rt = LongToInt(ALUStation.ValueVj.get(ALUindex));
                        int sa = LongToInt(ALUStation.ValueImm.get(ALUindex));
                        long result = rt >>> sa;
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    //ImmediateALU.add("SLL");
                    if (splitParts[0].equalsIgnoreCase("SLL")) {
                        int rt = LongToInt(ALUStation.ValueVj.get(ALUindex));
                        int sa = LongToInt(ALUStation.ValueImm.get(ALUindex));
                        long result = rt << sa;
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    //ImmediateALU.add("SRA");
                    if (splitParts[0].equalsIgnoreCase("SRA")) {
                        int rt = LongToInt(ALUStation.ValueVj.get(ALUindex));
                        int sa = LongToInt(ALUStation.ValueImm.get(ALUindex));
                        long result = rt >> sa;
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    // EXECUTE Done...
                    // Go to WRITE RESULT stage
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("WRITE RESULT");
                }
                else {
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("EXECUTE");
                }
            }
            if (NoImmediateALU.contains(splitParts[0])) {
                int ALUindex = ALUStation.DestID.indexOf(ROBId);
                int ROBindex = ROB.Id.indexOf(ROBId);
                if ((ALUStation.ValueVj.get(ALUindex) != null) && (ALUStation.ValueVk.get(ALUindex) != null)
                        && ((IntegerUnit == 1) || (IntegerUnit == 2))) {

                    // IntegerUnit incremented for this cycle.	
                    IntegerUnit++;

                    if (splitParts[0].equalsIgnoreCase("AND")) {
                        long result = (LongToInt(ALUStation.ValueVj.get(ALUindex))
                                & LongToInt(ALUStation.ValueVk.get(ALUindex)));
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("OR")) {
                        long result = (LongToInt(ALUStation.ValueVj.get(ALUindex))
                                | LongToInt(ALUStation.ValueVk.get(ALUindex)));
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("XOR")) {
                        long result = (LongToInt(ALUStation.ValueVj.get(ALUindex))
                                ^ LongToInt(ALUStation.ValueVk.get(ALUindex)));
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("NOR")) {
                        long result = (~(LongToInt(ALUStation.ValueVj.get(ALUindex))
                                | LongToInt(ALUStation.ValueVk.get(ALUindex))));
                        ALUStation.DestValue.set(ALUindex, result);
                    }

                    if (splitParts[0].equalsIgnoreCase("ADD")) {
                        long result = ALUStation.ValueVj.get(ALUindex) + ALUStation.ValueVk.get(ALUindex);
                        if ((result < -2147483648) || (result > 2147483647)) {
                            System.out.println("IntegerOverFlow exception for instruction: " + instruction);
                            System.exit(1);
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("ADDU")) {
                        long result = ALUStation.ValueVj.get(ALUindex) + ALUStation.ValueVk.get(ALUindex);
                        if (result < -2147483648) {
                            result = -2147483648;
                        }
                        else if (result > 2147483647) {
                            result = 2147483647;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("SUB")) {
                        long result = ALUStation.ValueVj.get(ALUindex) - ALUStation.ValueVk.get(ALUindex);
                        if ((result < -2147483648) || (result > 2147483647)) {
                            System.out.println("IntegerOverFlow exception for instruction: " + instruction);
                            System.exit(1);
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("SUBU")) {
                        long result = ALUStation.ValueVj.get(ALUindex) - ALUStation.ValueVk.get(ALUindex);
                        if (result < -2147483648) {
                            result = -2147483648;
                        }
                        else if (result > 2147483647) {
                            result = 2147483647;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }

                    if (splitParts[0].equalsIgnoreCase("SLT")) {
                        long result = 0;
                        if (ALUStation.ValueVj.get(ALUindex) < ALUStation.ValueVk.get(ALUindex)) {
                            result = 1;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    if (splitParts[0].equalsIgnoreCase("SLTU")) {
                        long result = 0;
                        if (Math.abs(ALUStation.ValueVj.get(ALUindex)) < Math.abs(ALUStation.ValueVk.get(ALUindex))) {
                            result = 1;
                        }
                        ALUStation.DestValue.set(ALUindex, result);
                    }
                    // EXECUTE Done...
                    // Go to WRITE RESULT stage
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("WRITE RESULT");
                }
                else {
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("EXECUTE");
                }
            }
            if (instruction.substring(0, 2).equalsIgnoreCase("LW")) {
                int LSQindex = LSQ.DestID.indexOf(ROBId);
                int ACRSindex = AddCalcStation.DestID.indexOf(ROBId);
                int LSindex = LoadStation.DestID.indexOf(ROBId);
                if (LSQ.ExecState.get(LSQindex).equalsIgnoreCase("FIRST CYCLE")) {
                    if ((AddressUnit == 1) || (AddressUnit == 2)) {
                        if (AddCalcStation.Value.get(ACRSindex) != null) {
                            long memoryLocation = (long) AddCalcStation.Value.get(ACRSindex) + AddCalcStation.offset.get(ACRSindex);
                            // AddressUnit is incremented for this cycle.
                            AddressUnit++;
                            // AC Reservation Station freed.
                            AddCalcStation.base.remove(ACRSindex);
                            AddCalcStation.DestID.remove(ACRSindex);
                            AddCalcStation.offset.remove(ACRSindex);
                            AddCalcStation.Value.remove(ACRSindex);
                            // Send memoryLocation to LSQ.
                            LSQ.Address.set(LSQindex, memoryLocation);
                            // Check for Store-Load forwarding
                            boolean forwarded = false;
                            forwarded = checkStoreForwarding(forwarded, memoryLocation, LSQindex);
                            //Indicate for next cycle.
                            if (!forwarded) {
                                nextCycle.StagesAddress.add(PC);
                                nextCycle.Stages.add("EXECUTE");
                                LSQ.ExecState.set(LSQindex, "SECOND CYCLE");
                            }
                            else {
                                // set the value at Reservation station
                                LoadStation.Value.set(LSindex, LSQ.value.get(LSQindex));
                                // next stage
                                nextCycle.StagesAddress.add(PC);
                                nextCycle.Stages.add("WRITE RESULT");
                                // LSQ entry is freed.
                                FreeLSQ(LSQindex);
                            }

                        }
                        else {
                            /*
                             * Wait for the Value at AC RS available.
                             */

                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("EXECUTE");
                        }

                    }
                    else {
                        // Address Unit not available.
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("EXECUTE");
                    }

                }
                else if (LSQ.ExecState.get(LSQindex).equalsIgnoreCase("SECOND CYCLE")) {
                    long memoryLocation = LSQ.Address.get(LSQindex);
                    // Check for Store-Load forwarding
                    boolean forwarded = false;
                    forwarded = checkStoreForwarding(forwarded, memoryLocation, LSQindex);
                    if (forwarded) {
                        // set the value at Reservation station
                        // write code to set a flag for branch prediction
                        LoadStation.Value.set(LSindex, LSQ.value.get(LSQindex));
                        // next stage
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("WRITE RESULT");
                        // LSQ entry is freed.
                        FreeLSQ(LSQindex);
                    }
                    else {
                        if ((LoadMemoryAccess == 1) && (checkLoadFront(memoryLocation, LSQindex))) {
                            LoadMemoryAccess++;
                            LSQ.ExecState.set(LSQindex, "THIRD CYCLE");
                        }
                        else {
                            LSQ.ExecState.set(LSQindex, "SECOND CYCLE");
                        }
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("EXECUTE");
                    }
                }
                else if (LSQ.ExecState.get(LSQindex).equalsIgnoreCase("THIRD CYCLE")) {
                    long memoryLocation = LSQ.Address.get(LSQindex);
                    // Check for Store-Load forwarding
                    boolean forwarded = false;
                    forwarded = checkStoreForwarding(forwarded, memoryLocation, LSQindex);
                    if (!forwarded) {
                        // Access Memory
                        if (checkLoadFront(memoryLocation, LSQindex)) {
                            LSQ.value.set(LSQindex, Long.parseLong(MainMemory.get(memoryLocation)));
                            LoadStation.Value.set(LSindex, LSQ.value.get(LSQindex));
                            // next stage
                            // Set Load Memory Access Free
                            LoadMemoryAccess = 1;
                            // next stage
                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("WRITE RESULT");
                            // LSQ entry is freed.
                            FreeLSQ(LSQindex);
                        }
                        else {
                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("EXECUTE");
                        }

                    }
                    else {
                        // set the value at Reservation station
                        LoadStation.Value.set(LSindex, LSQ.value.get(LSQindex));
                        // next stage
                        // Set Load Memory Access Free
                        LoadMemoryAccess = 1;
                        // next stage
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("WRITE RESULT");
                        // LSQ entry is freed.
                        FreeLSQ(LSQindex);

                    }
                }
            }
            if (instruction.substring(0, 2).equalsIgnoreCase("SW")) {
                int ROBindex = ROB.Id.indexOf(ROBId);
                int LSQindex = LSQ.DestID.indexOf(ROBId);
                int ACRSindex = AddCalcStation.DestID.indexOf(ROBId);
                String StoreSourceRegister = instruction.substring(3, instruction.indexOf(","));
                if (LSQ.ExecState.get(LSQindex).equalsIgnoreCase("ADDRESS CALCULATED")) {
                    if (LSQ.value.get(LSQindex) == null) {
                        LSQ.value.set(LSQindex, CheckROBDestination(ROBindex, StoreSourceRegister, instruction));
                    }
                    // Check for Store on top of LSQ and Value to write is ready or not
                    boolean available = false;
                    // Check for Store on top of LSQ and Value to write is ready or not
                    if (LSQ.value.get(LSQindex) == null) {
                        if (thisCycle.Stages.contains("WRITE RESULT")) {

                            for (int i = 0; i < thisCycle.Stages.size(); i++) {
                                long ROBID = thisCycle.StagesAddress.get(thisCycle.Stages.indexOf("WRITE RESULT"));
                                int ind = ROB.Id.indexOf((int) ROBID);
                                String dest = ROB.Destination.get(ind);
                                if (dest.equalsIgnoreCase(StoreSourceRegister)) {
                                    available = true;
                                    break;
                                }
                            }

                        }

                    }

                    if ((LSQindex == 0) && (LSQ.value.get(LSQindex) != null)) {
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("COMMIT");
                        LSQ.ExecState.set(LSQindex, "FIRST COMMIT CYCLE");
                    }
                    else if (available && LSQindex == 0) {
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("COMMIT");
                        LSQ.ExecState.set(LSQindex, "FIRST COMMIT CYCLE");
                    }
                    else {
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("EXECUTE");
                        LSQ.ExecState.set(LSQindex, "ADDRESS CALCULATED");
                    }

                }
                else if ((AddressUnit == 1) || (AddressUnit == 2)) {
                    if (AddCalcStation.Value.get(ACRSindex) != null) {
                        long memoryLocation = (long) AddCalcStation.Value.get(ACRSindex) + AddCalcStation.offset.get(ACRSindex);
                        // AddressUnit is incremented for this cycle.
                        AddressUnit++;
                        // AC Reservation Station freed.
                        FreeACStation(ACRSindex);
                        // Send memoryLocation to LSQ.
                        LSQ.Address.set(LSQindex, memoryLocation);
                        // 
                        if (LSQ.value.get(LSQindex) == null) {

                            LSQ.value.set(LSQindex, CheckROBDestination(ROBindex, StoreSourceRegister, instruction));
                        }
                        boolean available = false;
                        // Check for Store on top of LSQ and Value to write is ready or not
                        if (LSQ.value.get(LSQindex) == null) {
                            if (thisCycle.Stages.contains("WRITE RESULT")) {

                                for (int i = 0; i < thisCycle.Stages.size(); i++) {
                                    long ROBID = thisCycle.StagesAddress.get(thisCycle.Stages.indexOf("WRITE RESULT"));
                                    int ind = ROB.Id.indexOf((int) ROBID);
                                    String dest = ROB.Destination.get(ind);
                                    if (dest.equalsIgnoreCase(StoreSourceRegister)) {
                                        available = true;
                                        break;
                                    }
                                }

                            }

                        }

                        if ((LSQindex == 0) && (LSQ.value.get(LSQindex) != null)) {
                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("COMMIT");
                            LSQ.ExecState.set(LSQindex, "FIRST COMMIT CYCLE");
                        }
                        else if (available && LSQindex == 0) {
                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("COMMIT");
                            LSQ.ExecState.set(LSQindex, "FIRST COMMIT CYCLE");
                        }
                        else {
                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("EXECUTE");
                            LSQ.ExecState.set(LSQindex, "ADDRESS CALCULATED");
                        }

                    }
                    else {
                        nextCycle.StagesAddress.add(PC);
                        nextCycle.Stages.add("EXECUTE");
                    }

                }
                else {
                    // Address Unit not available.
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("EXECUTE");
                }

            }
        }

        if (Stage.equalsIgnoreCase("WRITE RESULT")) {
            int ROBId = (int) PC;
            int index = ROB.Id.indexOf(ROBId);
            String instruction = ROB.Instruction.get(index);
            String[] splitParts = instruction.split(" ");
            if (ALUInstructions.contains(splitParts[0])) {
                int ALUindex = ALUStation.DestID.indexOf(ROBId);
                int ROBindex = ROB.Id.indexOf(ROBId);

                for (int i = ROBindex + 1; i < ROB.Id.size(); i++) {
                    for (int j = 0; j < ALUStation.DestID.size(); j++) {

                        if (ALUStation.DestID.get(j) == ROB.Id.get(i)) {
                            if ((ALUStation.Vj.get(j).equalsIgnoreCase(ROB.Destination.get(ROBindex)))
                                    && (ALUStation.ValueVj.get(j) == null)) {

                                ALUStation.ValueVj.set(j, ALUStation.DestValue.get(ALUindex));

                            }
                            if ((ALUStation.Vk.get(j) != null)
                                    && (ALUStation.Vk.get(j).equalsIgnoreCase(ROB.Destination.get(ROBindex)))
                                    && (ALUStation.ValueVk.get(j) == null)) {

                                ALUStation.ValueVk.set(j, ALUStation.DestValue.get(ALUindex));

                            }
                        }

                    }
                    for (int j = 0; j < AddCalcStation.DestID.size(); j++) {
                        if (AddCalcStation.DestID.get(j) == ROB.Id.get(i)) {
                            if ((AddCalcStation.base.get(j).equalsIgnoreCase(ROB.Destination.get(ROBindex)))
                                    && (AddCalcStation.Value.get(j) == null)) {
                                AddCalcStation.Value.set(j, ALUStation.DestValue.get(ALUindex));
                            }
                        }

                    }

                }
                // Update ROB
                ROB.Value.set(ROBindex, ALUStation.DestValue.get(ALUindex));
                // free ALU Reservation Station
                FreeALUStation(ALUindex);
                nextCycle.StagesAddress.add(PC);
                nextCycle.Stages.add("COMMIT");

            }
            if (instruction.substring(0, 2).equalsIgnoreCase("LW")) {
                int LSindex = LoadStation.DestID.indexOf(ROBId);
                ROB.Value.set(index, LoadStation.Value.get(LSindex));
                // Update ALU Reservation Station
                int ROBindex = ROB.Id.indexOf(ROBId);

                for (int i = ROBindex + 1; i < ROB.Id.size(); i++) {
                    for (int j = 0; j < ALUStation.DestID.size(); j++) {

                        if (ALUStation.DestID.get(j) == ROB.Id.get(i)) {
                            if ((ALUStation.Vj.get(j).equalsIgnoreCase(LoadStation.Operand.get(LSindex)))
                                    && (ALUStation.ValueVj.get(j) == null)) {
                                ALUStation.ValueVj.set(j, LoadStation.Value.get(LSindex));

                            }
                            if ((ALUStation.Vk.get(j) != null)
                                    && (ALUStation.Vk.get(j).equalsIgnoreCase(LoadStation.Operand.get(LSindex)))
                                    && (ALUStation.ValueVk.get(j) == null)) {
                                ALUStation.ValueVk.set(j, LoadStation.Value.get(LSindex));

                            }
                        }

                    }
                    for (int j = 0; j < AddCalcStation.DestID.size(); j++) {
                        if (AddCalcStation.DestID.get(j) == ROB.Id.get(i)) {
                            if ((AddCalcStation.base.get(j).equalsIgnoreCase(LoadStation.Operand.get(LSindex)))
                                    && (AddCalcStation.Value.get(j) == null)) {
                                AddCalcStation.Value.set(j, LoadStation.Value.get(LSindex));
                            }
                        }

                    }

                }

                // free load station
                FreeLoadStation(LSindex);
                // next stage
                nextCycle.StagesAddress.add(PC);
                nextCycle.Stages.add("COMMIT");
            }
        }

        if (Stage.equalsIgnoreCase("COMMIT")) {
            int ROBId = (int) PC;
            int index = ROB.Id.indexOf(ROBId);
            String instruction = ROB.Instruction.get(index);
            String[] splitParts = instruction.split(" ");

            if (commitInOrder(ROBId)) {
                if ((CommitCountPerCycle) <= 2 && (index == 0)) {
                    boolean doNotCountforStore = false;
                    if (splitParts[0].equalsIgnoreCase("NOP")) {
                        // free ROB entry
                        FreeROB(index);

                    }
                    if (splitParts[0].equalsIgnoreCase("BREAK")) {
                        for (int j = 0; j < ROB.Id.size(); j++) {
                            FreeROB(j);
                        }
                        theEnd = true;

                    }
                    if ((splitParts[0].equalsIgnoreCase("J"))
                            || (BranchOne.contains(splitParts[0]))
                            || (BranchTwo.contains(splitParts[0]))) {
                        // free ROB entry
                        FreeROB(index);
                    }
                    if ((ALUInstructions.contains(splitParts[0]))
                            || (splitParts[0].equalsIgnoreCase("LW"))) {
                        // update register file
                        Registers.put(ROB.Destination.get(index), ROB.Value.get(index));
                        // free ROB entry
                        FreeROB(index);
                    }
                    if (splitParts[0].equalsIgnoreCase("SW")) {
                        int ROBindex = ROB.Id.indexOf(ROBId);
                        int LSQindex = LSQ.DestID.indexOf(ROBId);
                        String StoreSourceRegister = instruction.substring(3, instruction.indexOf(","));
                        if (LSQ.value.get(LSQindex) == null) {
                            LSQ.value.set(LSQindex, CheckROBDestination(ROBindex, StoreSourceRegister, instruction));
                        }
                        if ((LSQindex == 0) && (LSQ.value.get(LSQindex) != null)) {
                            if (LSQ.ExecState.get(LSQindex).equalsIgnoreCase("FIRST COMMIT CYCLE")) {
                                nextCycle.StagesAddress.add(PC);
                                nextCycle.Stages.add("COMMIT");
                                LSQ.ExecState.set(LSQindex, "SECOND COMMIT CYCLE");
                            }
                            else {
                                // update Main Memory
                                MainMemory.put(LSQ.Address.get(LSQindex), LSQ.value.get(LSQindex).toString());
                                // free ROB entry
                                FreeROB(index);
                                // free LSQ entry
                                FreeLSQ(LSQindex);
                            }
                        }
                        else {

                            nextCycle.StagesAddress.add(PC);
                            nextCycle.Stages.add("COMMIT");
                            doNotCountforStore = true;
                        }

                    }

                    if (!doNotCountforStore) {
                        CommitCountPerCycle++;
                    }

                }
                else {
                    nextCycle.StagesAddress.add(PC);
                    nextCycle.Stages.add("COMMIT");
                }

            }

        }

    }

    private static boolean commitInOrder(int ROBId) {
        int ROBindex = ROB.Id.indexOf(ROBId);
        for (int i = 0; i < thisCycle.Stages.size(); i++) {
            if (thisCycle.Stages.get(i).equalsIgnoreCase("COMMIT")) {
                long compROBId = thisCycle.StagesAddress.get(i);
                int CompROBindex = ROB.Id.indexOf((int) compROBId);
                if (CompROBindex < ROBindex) {
                    thisCycle.Stages.add(i + 1, "COMMIT");
                    thisCycle.StagesAddress.add(i + 1, (long) ROBId);
                    return false;
                }
            }

        }
        return true;
    }

    private static boolean checkLoadFront(long memoryLocation, int index) {
        if (index == 0) {
            return true;
        }
        else {
            for (int j = index - 1; j >= 0; j--) {
                if (LSQ.Instruction.get(j).substring(0, 2).equalsIgnoreCase("SW")) {
                    if (LSQ.Address.get(j) != null) {
                        if (LSQ.Address.get(j) == memoryLocation) {
                            return false;
                        }

                    }
                    else {
                        return false;
                    }

                }
            }
            return true;
        }
    }

    private static boolean checkStoreForwarding(boolean forwarded, long memoryLocation, int index) {
        if (index == 0) {
            return false;
        }
        else {
            for (int j = index - 1; j >= 0; j--) {
                if (LSQ.Instruction.get(j).substring(0, 2).equalsIgnoreCase("SW")) {
                    if (LSQ.Address.get(j) != null) {
                        if (LSQ.Address.get(j) == memoryLocation) {
                            if (LSQ.value.get(j) != null) {
                                LSQ.value.set(index, LSQ.value.get(j));
                                return true;
                            }
                            else {
                                return false;
                            }
                        }
                    }
                    else {
                        return false;
                    }

                }
            }
            return false;
        }
    }

    private static void MisPredictedFlush(int index) {
        for (int i = index + 1; i < ROB.Id.size(); i++) {
            int removeROBid = ROB.Id.get(i);
            FreeROB(i);
            for (int j = 0; j < LSQ.DestID.size(); j++) {
                if (LSQ.DestID.get(j) == removeROBid) {
                    FreeLSQ(j);
                }
            }
            for (int j = 0; j < AddCalcStation.DestID.size(); j++) {
                if (AddCalcStation.DestID.get(j) == removeROBid) {
                    FreeACStation(j);
                }
            }
            for (int j = 0; j < LoadStation.DestID.size(); j++) {
                if (LoadStation.DestID.get(j) == removeROBid) {
                    FreeLoadStation(j);
                }
            }
            for (int j = 0; j < ALUStation.DestID.size(); j++) {
                if (ALUStation.DestID.get(j) == removeROBid) {
                    FreeALUStation(j);
                }
            }
            for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
                if (nextCycle.StagesAddress.get(j) == removeROBid) {
                    nextCycle.StagesAddress.remove(j);
                    nextCycle.Stages.remove(j);
                    j--;
                }
            }
            for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
                if (thisCycle.StagesAddress.get(j) == removeROBid) {
                    thisCycle.StagesAddress.remove(j);
                    thisCycle.Stages.remove(j);
                    j--;
                }
            }
        }
        for (int j = 0; j < thisCycle.StagesAddress.size(); j++) {
            if (thisCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                thisCycle.StagesAddress.remove(j);
                thisCycle.Stages.remove(j);
                j--;
            }
        }
        for (int j = 0; j < nextCycle.StagesAddress.size(); j++) {
            if (nextCycle.Stages.get(j).equalsIgnoreCase("ISSUE")) {
                nextCycle.StagesAddress.remove(j);
                nextCycle.Stages.remove(j);
                j--;
            }
        }

    }

    private static void FreeALUStation(int index) {
        ALUStation.DestValue.remove(index);
        ALUStation.DestID.remove(index);
        ALUStation.Instruction.remove(index);
        ALUStation.ValueImm.remove(index);
        ALUStation.ValueVj.remove(index);
        ALUStation.ValueVk.remove(index);
        ALUStation.Vj.remove(index);
        ALUStation.Vk.remove(index);
    }

    private static void FreeLoadStation(int index) {
        LoadStation.DestID.remove(index);
        LoadStation.Instruction.remove(index);
        LoadStation.Operand.remove(index);
        LoadStation.Value.remove(index);
    }

    private static void FreeLSQ(int index) {
        LSQ.DestID.remove(index);
        LSQ.Address.remove(index);
        LSQ.Instruction.remove(index);
        LSQ.value.remove(index);
        LSQ.ExecState.remove(index);
    }

    private static void FreeROB(int index) {
        ROB.Destination.remove(index);
        ROB.Instruction.remove(index);
        ROB.Ready.remove(index);
        ROB.Value.remove(index);
        ROB.Id.remove(index);
    }

    private static void FreeACStation(int index) {
        AddCalcStation.base.remove(index);
        AddCalcStation.DestID.remove(index);
        AddCalcStation.offset.remove(index);
        AddCalcStation.Value.remove(index);
    }

    private static void BTBFree() {
        BTB.Address.remove(0);
        BTB.PredictedPC.remove(0);
        BTB.Predictor2Bit.remove(0);
        BTB.PredictorOutCome.remove(0);
        BTB.TargetPC.remove(0);
    }
}
