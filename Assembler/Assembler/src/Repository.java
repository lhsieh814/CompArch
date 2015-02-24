/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Gavin
 */
import java.util.LinkedHashMap;

public class Repository {

    private static String OutputString = null;

    private static long Address = 596;

    private static boolean BreakIssued = false;

    private static LinkedHashMap<String, String> OpcodeMap = new LinkedHashMap<String, String>();

    private static LinkedHashMap<String, String> SpecialFuncMap = new LinkedHashMap<String, String>();

    private static LinkedHashMap<String, String> RegimmMap = new LinkedHashMap<String, String>();

    public Repository() {

        SpecialFuncMapping();

        OpcodeMapping();

        RegimmMapping();

    }

    private void OpcodeMapping() {

        OpcodeMap.put("101011", "SW");//store ---
        OpcodeMap.put("100011", "LW");//load ---
        OpcodeMap.put("000010", "J");//Jump
        OpcodeMap.put("000100", "BEQ");//Branch BEQ R10, R8, #4
        OpcodeMap.put("000101", "BNE");//Branch BNE R10, R8, #16
        OpcodeMap.put("000111", "BGTZ");//Branch BGTZ R10, #32
        OpcodeMap.put("000110", "BLEZ");//Branch BLEZ R10, #-160
        //Branch BGEZ R10, #32
        //Branch BLTZ R10, #32
        OpcodeMap.put("001000", "ADDI");//ALU --
        OpcodeMap.put("001001", "ADDIU");//ALU--
        OpcodeMap.put("001010", "SLTI");//ALU--
        OpcodeMap.put("001011", "SLTIU");//Not Listed;  ALU 

    }

    private void SpecialFuncMapping() {
        SpecialFuncMap.put("001101", "BREAK");//Break
        SpecialFuncMap.put("101010", "SLT");//ALU --
        SpecialFuncMap.put("101011", "SLTU");//ALU --
        SpecialFuncMap.put("000000", "SLL");//ALU --
        SpecialFuncMap.put("000010", "SRL");//ALU --
        SpecialFuncMap.put("000011", "SRA");//ALU --
        SpecialFuncMap.put("100010", "SUB");//ALU --
        SpecialFuncMap.put("100011", "SUBU");//ALU --
        SpecialFuncMap.put("100000", "ADD");//ALU ---
        SpecialFuncMap.put("100001", "ADDU");//ALU ---
        SpecialFuncMap.put("100100", "AND");//ALU --
        SpecialFuncMap.put("100101", "OR");//ALU ---
        SpecialFuncMap.put("100110", "XOR");//ALU ---
        SpecialFuncMap.put("100111", "NOR");//ALU  ---
        //SpecialFuncMap.put("000000", "NOP");

    }

    private void RegimmMapping() {

        RegimmMap.put("00001", "BGEZ");
        RegimmMap.put("00000", "BLTZ");
    }

    public void ProcessLine(String inputLine) {

        String InstructionString = null;

        long data = 0;

        if (BreakIssued) {
            /*
             * After the instruction BREAK, 32 bit 2's complement data
             * values are recorded.
             */
            data = Long.parseLong(inputLine, 2);
            /*
             * Each 32 bit data is signed. Each data value can represent values from
             * -2147483648 to +2147483647
             * 
             */
            if (inputLine.charAt(0) == '1') {
                data = data - 4294967296L;
            }

            OutputString = inputLine + " " + Address + " " + data;

        }
        else {

            String Opcode = inputLine.substring(0, 6);

            String rs = inputLine.substring(6, 11); // rs

            String rt = inputLine.substring(11, 16); // rt

            String rd = inputLine.substring(16, 21); // rd

            String FiveFour = inputLine.substring(21, 26);

            String FuncField = inputLine.substring(26, 32);

            OutputString = Opcode + " " + rs + " " + rt + " "
                    + rd + " " + FiveFour + " " + FuncField + " " + Address + " ";

            if (OpcodeMap.containsKey(Opcode)) {

                InstructionString = OpcodeMap.get(Opcode);

                /*
                 * Opcode = SW
                 * SW rt, offset(base)
                 */
                if (Opcode.equalsIgnoreCase("101011")) {

                    InstructionString = InstructionString + " R" + Integer.parseInt(rt, 2) + ", ";

                    int offset = Integer.parseInt(rd + FiveFour + FuncField, 2);

                    if (rd.charAt(0) == '1') {
                        offset = offset - 65536;
                    }

                    InstructionString = InstructionString + offset + "(R" + Integer.parseInt(rs, 2) + ")";

                }

                /*
                 * Opcode = LW
                 * LW rt, offset(base)
                 */
                if (Opcode.equalsIgnoreCase("100011")) {

                    InstructionString = InstructionString + " R" + Integer.parseInt(rt, 2) + ", ";

                    int offset = Integer.parseInt(rd + FiveFour + FuncField, 2);

                    /*
                     * The 16 bit offset is signed.
                     */
                    if (rd.charAt(0) == '1') {
                        offset = offset - 65536;
                    }

                    InstructionString = InstructionString + offset + "(R" + Integer.parseInt(rs, 2) + ")";

                }

                /*
                 * Opcode = J
                 * J target
                 */
                if (Opcode.equalsIgnoreCase("000010")) {

                    InstructionString = InstructionString + " #" + Integer.parseInt(rs + rt + rd + FiveFour + FuncField + "00", 2);

                }

                /*
                 * Opcode = BEQ 000100
                 * BEQ rs, rt, offset
                 * Opcode = BNE 000101
                 * BNE rs, rt, offset
                 */
                if ((Opcode.equalsIgnoreCase("000100")) || (Opcode.equalsIgnoreCase("000101"))) {

                    InstructionString = InstructionString + " R" + Integer.parseInt(rs, 2) + ","
                            + " R" + Integer.parseInt(rt, 2) + ",";

                    int offset = Integer.parseInt(rd + FiveFour + FuncField + "00", 2);

                    /*
                     * The 18 bit offset is signed.
                     */
                    if (rd.charAt(0) == '1') {
                        offset = offset - 262144;
                    }

                    InstructionString = InstructionString + " #" + offset;

                }

                /*
                 * Opcode = ADDI 001000, ADDIU 001001, SLTI 001010, SLTIU 001011,
                 * ADDI rt, rs, immediate, ADDIU rt, rs, immediate, 
                 * SLTI rt, rs, immediate, SLTIU rt, rs, immediate
                 */
                if ((Opcode.equalsIgnoreCase("001000")) || (Opcode.equalsIgnoreCase("001001"))
                        || (Opcode.equalsIgnoreCase("001010")) || (Opcode.equalsIgnoreCase("001011"))) {

                    InstructionString = InstructionString + " R" + Integer.parseInt(rt, 2) + ","
                            + " R" + Integer.parseInt(rs, 2) + ",";

                    int offset = Integer.parseInt(rd + FiveFour + FuncField, 2);

                    /*
                     * The 16 bit immediate is signed.
                     */
                    //if (!Opcode.equalsIgnoreCase("001011")) {
                    if (rd.charAt(0) == '1') {
                        offset = offset - 65536;
                    }
                    //}

                    InstructionString = InstructionString + " #" + offset;

                }

                /*
                 * Opcode = BGTZ, BLEZ
                 * BGTZ rs, offset
                 * BLEZ rs, offset
                 */
                if ((Opcode.equalsIgnoreCase("000110")) || (Opcode.equalsIgnoreCase("000111"))) {

                    InstructionString = InstructionString + " R" + Integer.parseInt(rs, 2) + ",";

                    int offset = Integer.parseInt(rd + FiveFour + FuncField + "00", 2);

                    /*
                     * The 18 bit offset is signed.
                     */
                    if (rd.charAt(0) == '1') {
                        offset = offset - 262144;
                    }

                    InstructionString = InstructionString + " #" + offset;

                }

            }

            if (Opcode.equalsIgnoreCase("000000")) {

                if (SpecialFuncMap.containsKey(FuncField)) {

                    /*
                     * Special Func = BREAK
                     */
                    if (FuncField.equalsIgnoreCase("001101")) {

                        InstructionString = SpecialFuncMap.get(FuncField);

                        /*
                         * Set the BreakIssued boolean, so that next 32 bit words are
                         * taken as data values.
                         */
                        BreakIssued = true;

                    }

                    /*
                     * Special Func = SLT
                     * SLT rd, rs, rt
                     */
                    if ((FuncField.equalsIgnoreCase("101010"))
                            || (FuncField.equalsIgnoreCase("101011"))) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);
                    }

                    /*
                     * Special Func = SLL
                     * SLL rd, rt, sa
                     */
                    if (FuncField.equalsIgnoreCase("000000")) {

                        int rdReg = Integer.parseInt(rd, 2);
                        int rtReg = Integer.parseInt(rt, 2);
                        int saNum = Integer.parseInt(FiveFour, 2);

                        if ((rdReg == 0) && (rtReg == 0) && (saNum == 0)) {
                            /*
                             * If all the fields are zero, then the instruction
                             * is NOP; otherwise SLL.
                             */
                            InstructionString = "NOP";

                        }
                        else {

                            InstructionString = SpecialFuncMap.get(FuncField)
                                    + " R" + rdReg + ","
                                    + " R" + rtReg + ","
                                    + " #" + saNum;

                        }

                    }

                    /*
                     * Special Func = SRL
                     * SRL rd, rt, sa
                     * 
                     * [rs.charAt(4) = bit 21] is zero for SRL 
                     */
                    if (FuncField.equalsIgnoreCase("000010") && rs.charAt(4) == '0') {

                        int rdReg = Integer.parseInt(rd, 2);
                        int rtReg = Integer.parseInt(rt, 2);
                        int saNum = Integer.parseInt(FiveFour, 2);

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + rdReg + ","
                                + " R" + rtReg + ","
                                + " #" + saNum;

                    }

                    /*
                     * Special Func = SRA
                     * SRA rd, rt, sa
                     */
                    if (FuncField.equalsIgnoreCase("000011")) {

                        int rdReg = Integer.parseInt(rd, 2);
                        int rtReg = Integer.parseInt(rt, 2);
                        int saNum = Integer.parseInt(FiveFour, 2);

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + rdReg + ","
                                + " R" + rtReg + ","
                                + " #" + saNum;

                    }

                    /*
                     * Special Func = SUB
                     * SUB rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100010")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = SUBU
                     * SUBU rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100011")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = ADD
                     * ADD rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100000")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = ADDU
                     * ADDU rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100001")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = AND
                     * AND rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100100")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = OR
                     * OR rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100101")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = XOR
                     * XOR rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100110")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                    /*
                     * Special Func = NOR
                     * NOR rd, rs, rt
                     */
                    if (FuncField.equalsIgnoreCase("100111")) {

                        InstructionString = SpecialFuncMap.get(FuncField)
                                + " R" + Integer.parseInt(rd, 2) + ","
                                + " R" + Integer.parseInt(rs, 2) + ","
                                + " R" + Integer.parseInt(rt, 2);

                    }

                }

            }

            if (Opcode.equalsIgnoreCase("000001")) {

                if (RegimmMap.containsKey(rt)) {

                    /*
                     * REGIMM = BGEZ, BLTZ;
                     * BGEZ rs, offset;BLTZ rs, offset.
                     */
                    int offset = Integer.parseInt(rd + FiveFour + FuncField + "00", 2);

                    /*
                     * The 18 bit offset is signed.
                     */
                    if (rd.charAt(0) == '1') {

                        offset = offset - 262144;

                    }

                    InstructionString = RegimmMap.get(rt) + " R" + Integer.parseInt(rs, 2) + ", #" + offset;

                }

            }

        }

        //System.out.println(OutputString);
        if (InstructionString != null) {

            Assembler.OutputBuffer.add(OutputString + InstructionString + "\n");

            Assembler.MainMemory.put(Address, InstructionString);
            InstructionInfo info = new InstructionInfo();
            info.setAddress(Address);
            info.setInstruction(InstructionString);
            info.setStage("IF");
            Assembler.InstructionMap.put(Address, info);
            Assembler.Instructions.add(InstructionString);
            Assembler.Addresses.add(Address);

        }
        else {

            Assembler.OutputBuffer.add(OutputString + "\n");

            if (BreakIssued) {

                Assembler.MainMemory.put(Address, Long.toString(data));

            }

        }

        Address = Address + 4;

    }

}
