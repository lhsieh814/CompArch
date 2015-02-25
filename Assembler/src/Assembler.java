import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;

/**
 * ECSE 415 Project, Assembler Implementation
 * Author: Yang Zhou
 * Group 6
 * Members: Yukun Su, Yang Zhou, Wei Sing Ta, Lena Hsieh
 */
public class Assembler {

    private static File file;
    private static int lineNumber = 0;

    private static boolean debugMode = false;

    private static HashMap<String, String> instructionCodes = new HashMap<String, String>();
    private static HashMap<String, instructionParser> instructions = new HashMap<String, instructionParser>();
    private static HashMap<String, String> registers = new HashMap<String, String>();
    private static HashMap<String, Integer> labels = new HashMap<String, Integer>();
    
    public static StringBuffer resultBuffer = new StringBuffer();
    /**
     * Prevent an object of this class from being created
     */
    private Assembler() {
    }

    private static void initInstructionCodes() {
        // R-Type Instructions
        instructionCodes.put("add", "100000");
        instructionCodes.put("sub", "100010");
        instructionCodes.put("mult", "011000");
        instructionCodes.put("div", "011010");
        instructionCodes.put("and", "100100");
        instructionCodes.put("or", "100101");
        instructionCodes.put("nor", "100111");
        instructionCodes.put("slt", "101010");
        instructionCodes.put("sll", "000000");
        instructionCodes.put("srl", "000010");
        instructionCodes.put("jr", "001000");

        // I-Type Instructions
        instructionCodes.put("addi", "001000");
        instructionCodes.put("andi", "001100");
        instructionCodes.put("ori", "001101");
        instructionCodes.put("beq", "000100");
        instructionCodes.put("bne", "000101");
        instructionCodes.put("lw", "100011");
        instructionCodes.put("sw", "101011");
        instructionCodes.put("slti", "001010");

        // J-Type Instructions
        instructionCodes.put("j", "000010");
        instructionCodes.put("jal", "000011");
        instructionCodes.put("jr", "001000");
        
        // Move Instruction
        instructionCodes.put("mflo", "010010");
    }
    
    /**
     * Initialize all instructions
     */
    private static void initInstructions() {
        // R-Type Instructions
        instructions.put("add", instructionR_std);
        instructions.put("sub", instructionR_std);
        instructions.put("and", instructionR_std);
        instructions.put("or", instructionR_std);
        instructions.put("nor", instructionR_std);
        instructions.put("slt", instructionR_std);
        instructions.put("sll", instructionR_shift);
        instructions.put("srl", instructionR_shift);
        instructions.put("jr", instructionR_jr);
        instructions.put("mult", instructionR_mul);
        instructions.put("div", instructionR_mul);

        // I-Type Instructions
        instructions.put("addi", instructionI_std);
        instructions.put("andi", instructionI_std);
        instructions.put("slti", instructionI_std);
        instructions.put("ori", instructionI_std);
        instructions.put("beq", instructionI_branch);
        instructions.put("bne", instructionI_branch);
        instructions.put("lw", instructionI_word);
        instructions.put("sw", instructionI_word);


        // J-Type Instructions
        instructions.put("j", instructionJ);
        instructions.put("jal", instructionJ);
        
        // M-Type Instructions
        instructions.put("mflo", instructionR_move);
    }

    /**
     * Initialize all registers, with register name, number, call convention
     */
    private static void initRegisterCodes() {
        // The Constant Value 0
        registers.put("$zero", "00000");
        // Assembler Temporary
        registers.put("$at", "00001");

        // Function Results & Expression Evaluation
        registers.put("$v0", "00010");
        registers.put("$v1", "00011");

        // Arguments
        registers.put("$a0", "00100");
        registers.put("$a1", "00101");
        registers.put("$a2", "00110");
        registers.put("$a3", "00111");

        // Temporaries
        registers.put("$t0", "01000");
        registers.put("$t1", "01001");
        registers.put("$t2", "01010");
        registers.put("$t3", "01011");
        registers.put("$t4", "01100");
        registers.put("$t5", "01101");
        registers.put("$t6", "01110");
        registers.put("$t7", "01111");

        // Saved Temporaries
        registers.put("$s0", "10000");
        registers.put("$s1", "10001");
        registers.put("$s2", "10010");
        registers.put("$s3", "10011");
        registers.put("$s4", "10100");
        registers.put("$s5", "10101");
        registers.put("$s6", "10110");
        registers.put("$s7", "10111");

        // Temporaries
        registers.put("$t8", "11000");
        registers.put("$t9", "11001");

        // Reserved for OS Kernel
        registers.put("$k0", "11010");
        registers.put("$k1", "11011");

        // Global Pointer
        registers.put("$gp", "11100");
        // Stack Pointer
        registers.put("$sp", "11101");
        // Frame Pointer
        registers.put("$fp", "11110");
        // Return Address
        registers.put("$ra", "11111");
    }

    /**
     * Interface to allow instruction mapping to a parse function
     */
    private interface instructionParser {

        void parse(String[] parts);
    }

    /**
     * Convert a decimal number to 5-bit binary
     * @param dec
     * @return unsigned 5-bit binary representation
     */
    private static String parseUnsigned5BitBin(int dec) {
        //int decValue = Integer.parseInt(dec); this was used when argument was a string
        String bin = Integer.toBinaryString(dec);

        int l = bin.length();
        if (l < 5) {
            for (int i = 0; i < (5 - l); i++) {
                bin = "0" + bin;
            }
        }

        return bin;
    }

    /**
     * Convert a decimal number to 16-bit binary number
     * @param dec decimal number input
     * @return 16-bit binary representation
     */
    private static String parseSigned16BitBin(int dec) {
        //int decValue = Integer.parseInt(dec);
        String bin = Integer.toBinaryString(dec);
        
        
        int l = bin.length();
        if (l < 16 && dec >= 0) {
            for (int i = 0; i < (16 - l); i++) {
                bin = "0" + bin;
            }
        }
        else if (dec < 0) {
            bin = bin.substring(l - 16);
        }

        return bin;
    }

    /**
     * Convert a decimal number to 32-bit binary number
     * @param dec
     * @return unsigned 32-bit binary
     */
    private static String parseUnsigned32BitBin(int dec) {
        String bin = Integer.toBinaryString(dec);

        int l = bin.length();
        if (l < 32) {
            for (int i = 0; i < (32 - l); i++) {
                bin = "0" + bin;
            }
        }

        return bin;
    }

    /**
     * Convert a decimal to a 8-bit hex
     * @param dec
     * @return 8-digit hexadecimal string 
     */
    private static String parse8DigitHex(int dec) {
        String hex = Integer.toHexString(dec);

        int l = hex.length();
        if (l < 8) {
            for (int i = 0; i < (8 - l); i++) {
                hex = "0" + hex;
            }
        }

        return hex;
    }

    /**
     * Get the address of a given register
     * @param a register
     * @return the register address as a String
     */
    private static String getRegister(String reg) {
        // Numeral address reference, e.g. $8
        if (reg.matches("[$]\\d+")) {
            return parseUnsigned5BitBin(Integer.parseInt(reg.substring(1)));
        }
        // Standard reference, e.g. $t0
        return registers.get(reg);
    }

    /**
     * Instructions: add, sub, and, or, nor, slt
     */
    private static instructionParser instructionR_std = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = "000000"; //instrCode.substring(2, 8);
            String rs = getRegister(parts[2]);
            String rt = getRegister(parts[3]);
            String rd = getRegister(parts[1]);
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);

            System.out.println(opcode + rs + rt + rd + shamt + funct);
            
            resultBuffer.append(opcode + rs + rt + rd + shamt + funct + "\n");
        }
    };

    /**
     *  Instructions for mult, div
     */
    private static instructionParser instructionR_mul = new instructionParser() {

        public void parse(String[] parts){
            String opcode = "000000";
            String rs = getRegister(parts[1]);
            String rt = getRegister(parts[2]);
            String rd = "00000";
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);
            
            System.out.println(opcode + rs + rt + rd + shamt + funct);
            
            resultBuffer.append(opcode + rs + rt + rd + shamt + funct + "\n");
        }
    };
    /**
     * Instructions: sll, srl
     */
    private static instructionParser instructionR_shift = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = "000000";
            String rs = "00000";
            String rt = getRegister(parts[2]);
            String rd = getRegister(parts[1]);
            String shamt = parseUnsigned5BitBin(Integer.parseInt(parts[3]));
            String funct = instructionCodes.get(parts[0]);

            System.out.println(opcode + rs + rt + rd + shamt + funct);
            
            resultBuffer.append(opcode + rs + rt + rd + shamt + funct + "\n");
        }
    };

    /**
     * Instructions: jr
     */
    private static instructionParser instructionR_jr = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = "000000";
            String rs = getRegister(parts[1]);
            String rt = "00000";
            String rd = "00000";
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);

            System.out.println(opcode + rs + rt + rd + shamt + funct);
            
            resultBuffer.append(opcode + rs + rt + rd + shamt +funct + "\n");
        }
    };

    /**
     * Instructions: jr
     */
    private static instructionParser instructionI_std = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[2]);
            String rt = getRegister(parts[1]);
//            String immediate = parseSigned16BitBin(Integer.parseInt(parts[3]));
            String immediate = parseSigned16BitBin((int)Float.parseFloat(parts[3]));
            System.out.println(opcode + rs + rt + immediate);
            
            resultBuffer.append(opcode + rs + rt +immediate + "\n");
        }
    };

    /**
     * Instructions: beq, bne
     */
    private static instructionParser instructionI_branch = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[1]);
            String rt = getRegister(parts[2]);
            String immediate = parseSigned16BitBin(labels.get(parts[3]) - lineNumber - 1);

            System.out.println(opcode + rs + rt + immediate);
            
            resultBuffer.append(opcode + rs + rt + immediate + "\n");
        }
    };

    /**
     * Instructions: lw, sw
     */
    private static instructionParser instructionI_word = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            String rs = getRegister(parts[3]);
            String rt = getRegister(parts[1]);
            String immediate = parseSigned16BitBin(Integer.parseInt(parts[2]));

            System.out.println(opcode + rs + rt + immediate);
            
            resultBuffer.append(opcode + rs + rt + immediate + "\n");
        }
    };

    /**
     * Instructions: j, jal
     */
    private static instructionParser instructionJ = new instructionParser() {
        public void parse(String[] parts) {
            String opcode = instructionCodes.get(parts[0]);
            // Compute the jump address and crop to 26 bits
            int fullAddress = 0x00400000 + 4 * labels.get(parts[1]);
            String address = parseUnsigned32BitBin(fullAddress).substring(4, 30);

            System.out.println(opcode + address);
            
            resultBuffer.append(opcode + address + "\n");
        }
    };

    /**
     * Instruction: mflo
     */
    private static instructionParser instructionR_move = new instructionParser() {
        public void parse(String[] parts){
            String opcode = "000000";
            String rs = "00000";
            String rt = "00000";
            String rd = getRegister(parts[1]);
            String shamt = "00000";
            String funct = instructionCodes.get(parts[0]);
            
            System.out.println(opcode + rs + rt + rd + shamt + funct);
            
            resultBuffer.append(opcode + rs + rt + rd + shamt + funct + "\n");
        }
    };
    
    /**
     * Set debug mode, which shows detailed parsing information
     * @param mode 
     */
    public static void setDebugMode(boolean mode) {
        debugMode = mode;
    }

    /**
     * Run assembly process on file with given filename
     * @param filename 
     */
    public static void assembleFile(String filename) {
        initInstructionCodes();
        initInstructions();
        initRegisterCodes();

        file = new File(filename);

        getLabels();
        assemble();
        
        TextWriter.createTextFile(resultBuffer);
    }

    /**
     * Scan file for labels and add their reference to the labels HashMap
     */
    private static void getLabels() {
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line.trim(); // Trim leading & trailing white space

                // If line contains a label
                if (line.matches(".+:.*")) {
                    String labelName = line.substring(0, line.indexOf(':'));
                    labels.put(labelName, lineNumber);
                    // Debugging mode displays label names & their associated line numbers
                    if (debugMode) {
                        System.out.println(labelName + ":  " + (lineNumber + 1));
                    }
                }

                // Remove empty lines
                line = line.replaceAll("^.+:([\\s]+)?", "");

                if (!line.isEmpty()) {
                    lineNumber++;
                }
            }

            scanner.close();
            lineNumber = 0;
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Perform actual assembly of the instructions into binary
     */
    private static void assemble() {
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                
                line = line.trim(); // Trim leading & trailing white space
                
                
                
                line = line.replaceAll("^.+:([\\s]+)?", ""); // Remove labels from the line
                
//                line = line.replaceAll("(?m)^[#].+", ""); // Remove comments
                line = line.replaceAll("[#].+", "");
                line = line.replace("(", ","); // Remove () for sw, and lw instruction
                line = line.replace(")", "");
                
                System.out.print(line);

                // Do not try to parse line if it is blank or contains only white space/tabs
                if (line.isEmpty()) {
                    continue;
                }

                // Split into each word by commas & white space
                String[] parts = line.split("[,\\s]+");

                // This section is for debugging purposes
                if (debugMode) {
                    System.out.println();
                    for (int i = 0; i < parts.length; i++) {
                        System.out.print("[" + parts[i] + "] ");
                    }
                    System.out.println();
                    System.out.print((lineNumber + 1) + ": ");
                }

                // Print line number (in hex format)
                if (!debugMode) {
                    int fullAddress = 0x00400000 + 4 * lineNumber;
                    System.out.print(parse8DigitHex(fullAddress) + ": ");
                }
                // Parse and write instruction
                 instructions.get(parts[0]).parse(parts);

                lineNumber++;
            }

            scanner.close();
        }
        catch (FileNotFoundException e) {
            // Do not print anything since parseLabels() already took care of that.
        }
    }
}
