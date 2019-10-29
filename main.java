import java.io.*;
import java.util.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.BufferedReader;
import java.util.Hashtable;

public class main {
    public static void main(String[] args) throws IOException {
        // Creating a scanner to get file input (assembly instructions) from user
        Scanner input = new Scanner(System.in);

        // Storing assembly instructions as a string
        String fileName = input.next();

        // FIRST PASS
        ArrayList<String> firstPass = new ArrayList<String>();
        firstPass = fileHandlesOpening(fileName);

        // Search through all of the file for lables, etc
        Hashtable<String, Integer> table = new Hashtable<String, Integer>();

        for (int i = 0; i < firstPass.size(); i++) {
            if (firstPass.get(i).startsWith("@")) {
                if (!isNumberic(firstPass.get(i))) {
                    table.put(firstPass.get(i), i);
                }
            }
        }

        /* SECOND PASS */

        // Creates file and the writer to write to the file
        String hackFile = createNewFile(fileName);
        FileWriter writer = new FileWriter(hackFile);

        // Handles everything with opening the file
        ArrayList<String> instructions = new ArrayList<String>();
        instructions = fileHandlesOpening(fileName);

        for (int i = 0; i < instructions.size(); i++) {
            // Translate A instructions into binary machine code
            // begining with a 0 bit followed by 15 bitss
            if (instructions.get(i).startsWith("@")) {
                // sending the a instruction to be converted to binary and written to .hack file
                String bits = handleAInstruct(instructions.get(i), table);
                writer.write(bits);
                writer.write(System.getProperty("line.separator"));
            } else {
                String CInstruct = handleCInstruct(instructions.get(i));
                writer.write(CInstruct);
                writer.write(System.getProperty("line.separator"));
            }
        }
        // Closes the Scanner and Writer
        writer.close();
        input.close();
    }

    public static String handleCInstruct(String instruction) {
        StringBuilder result = new StringBuilder();
        result.append("111");
        int i = 0;

        /*
         * finds the value of dest.toString().equalsIgnoreCase() and locates equal sign
         * or semi colon
         */
        StringBuilder dest = new StringBuilder();
        while (i < instruction.length()) {
            char inst = instruction.charAt(i);
            if (inst == '=') {
                break;
            } else if (inst == ';') {
                break;
            }
            dest.append(inst);
            i++;
        }

        StringBuilder comp = new StringBuilder();
        while (i < instruction.length()) {
            char compInst = instruction.charAt(i);
            comp.append(compInst);
            i++;
        }

        // delete the ; or = while keeping a string containing it
        String totalComp = comp.toString();
        comp.delete(0, 1);

        // Comp value
        if (totalComp.contains("=")) {
            if (comp.toString().equalsIgnoreCase("D|A")) {
                result.append("0010101");
            } else if (comp.toString().equalsIgnoreCase("D&A")) {
                result.append("0000000");
            } else if (comp.toString().equalsIgnoreCase("A-D")) {
                result.append("0000111");
            } else if (comp.toString().equalsIgnoreCase("D-A")) {
                result.append("0010011");
            } else if (comp.toString().equalsIgnoreCase("D+A")) {
                result.append("0000010");
            } else if (comp.toString().equalsIgnoreCase("A-1")) {
                result.append("0110010");
            } else if (comp.toString().equalsIgnoreCase("D-1")) {
                result.append("0001110");
            } else if (comp.toString().equalsIgnoreCase("A+1")) {
                result.append("0110111");
            } else if (comp.toString().equalsIgnoreCase("D+1")) {
                result.append("0011111");
            } else if (comp.toString().equalsIgnoreCase("D|M")) {
                result.append("1010101");
            } else if (comp.toString().equalsIgnoreCase("D&M")) {
                result.append("1000000");
            } else if (comp.toString().equalsIgnoreCase("M-D")) {
                result.append("1000111");
            } else if (comp.toString().equalsIgnoreCase("D-M")) {
                result.append("1010011");
            } else if (comp.toString().equalsIgnoreCase("D+M")) {
                result.append("1000010");
            } else if (comp.toString().equalsIgnoreCase("M-1")) {
                result.append("1110010");
            } else if (comp.toString().equalsIgnoreCase("M+1")) {
                result.append("1110111");
            } else if (comp.toString().equalsIgnoreCase("-M")) {
                result.append("1110011");
            } else if (comp.toString().equalsIgnoreCase("-A")) {
                result.append("0110011");
            } else if (comp.toString().equalsIgnoreCase("-D")) {
                result.append("0001111");
            } else if (comp.toString().equalsIgnoreCase("!A")) {
                result.append("0110001");
            } else if (comp.toString().equalsIgnoreCase("!M")) {
                result.append("1110001");
            } else if (comp.toString().equalsIgnoreCase("!D")) {
                result.append("0001101");
            } else if (comp.toString().equalsIgnoreCase("A")) {
                result.append("0110000");
            } else if (comp.toString().equalsIgnoreCase("M")) {
                result.append("1110000");
            } else if (comp.toString().equalsIgnoreCase("D")) {
                result.append("0001100");
            } else if (comp.toString().equalsIgnoreCase("-1")) {
                result.append("0111010");
            } else if (comp.toString().equalsIgnoreCase("1")) {
                result.append("0111111");
            } else if (comp.toString().equalsIgnoreCase("0")) {
                result.append("0101010");
            }
        }

        // Dest value
        // whats the case it wouldn't be stored
        if (dest.toString().equalsIgnoreCase(null)) {
            result.append("000");
        } else if (dest.toString().equalsIgnoreCase("AMD")) {
            result.append("111");
        } else if (dest.toString().equalsIgnoreCase("MD") || dest.toString().equalsIgnoreCase("DM")) {
            result.append("011");
        } else if (dest.toString().equalsIgnoreCase("AM") || dest.toString().equalsIgnoreCase("MA")) {
            result.append("101");
        } else if (dest.toString().equalsIgnoreCase("AD") || dest.toString().equalsIgnoreCase("DA")) {
            result.append("110");
        } else if (dest.toString().equalsIgnoreCase("M")) {
            result.append("001");
        } else if (dest.toString().equalsIgnoreCase("D")) {
            result.append("010");
        } else if (dest.toString().equalsIgnoreCase("A")) {
            result.append("100");
        } else {
            result.append("000");
        }

        // Jump value
        if (totalComp.contains(";")) {
            StringBuilder jump = new StringBuilder();
            if (dest.toString().equalsIgnoreCase("0")) {
                jump.append("1110101010000111");
                return jump.toString();
            } else {
                jump.append("1110001100000");
                if (comp.toString().equalsIgnoreCase("JGT")) {
                    jump.append("001");
                } else if (comp.toString().equalsIgnoreCase("JEQ")) {
                    jump.append("010");
                } else if (comp.toString().equalsIgnoreCase("JGE")) {
                    jump.append("011");
                } else if (comp.toString().equalsIgnoreCase("JLT")) {
                    jump.append("100");
                } else if (comp.toString().equalsIgnoreCase("JNE")) {
                    jump.append("101");
                } else if (comp.toString().equalsIgnoreCase("JLE")) {
                    jump.append("110");
                } else if (comp.toString().equalsIgnoreCase("JMP")) {
                    jump.append("111");
                }
                return jump.toString();
            }
        }
        result.append("000");
        return result.toString();
    }

    public static boolean isNumberic(String firstPass) {
        String newInst = firstPass.substring(1);
        try {
            int bo = Integer.parseInt(newInst);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static String handleAInstruct(String instruction, Hashtable table) {
        if (!isNumberic(instruction)) {
            if (instruction == "R0") {
                return "0";
            } else if (instruction == "R1") {
                return "1";
            } else if (instruction == "R2") {
                return "2";
            } else if (instruction == "R3") {
                return "3";
            } else if (instruction == "R4") {
                return "4";
            } else if (instruction == "R5") {
                return "5";
            } else if (instruction == "R6") {
                return "6";
            } else if (instruction == "R7") {
                return "7";
            } else if (instruction == "R8") {
                return "8";
            } else if (instruction == "R9") {
                return "9";
            } else if (instruction == "R10") {
                return "10";
            } else if (instruction == "R11") {
                return "11";
            } else if (instruction == "R12") {
                return "12";
            } else if (instruction == "R13") {
                return "13";
            } else if (instruction == "R14") {
                return "14";
            } else if (instruction == "R15") {
                return "15";
            } else if (instruction == "SCREEN") {
                return "16384";
            } else if (instruction == "KBD") {
                return "24576";
            } else if (instruction == "SP") {
                return "0";
            } else if (instruction == "LCL") {
                return "1";
            } else if (instruction == "ARG") {
                return "2";
            } else if (instruction == "THIS") {
                return "3";
            } else if (instruction == "THAT") {
                return "4";
            }
            Object value = table.get(instruction);
            String result = value.toString();
            return result;
        } else {
            // Cut off the @ symbol
            String newInst = instruction.substring(1);
            // Convert to an int for decimal->binary conversion
            int decimal = Integer.parseInt(newInst);
            // Convert back to a string and add on all the remaining 0's
            String bits = Integer.toBinaryString(decimal);
            while (bits.length() < 16) {
                bits = "0" + bits;
            }
            return bits;
        }
    }

    public static String createNewFile(String fileName) {
        fileName = fileName.replace(".asm", ".hack");
        try {
            File file = new File(fileName);
            if (file.createNewFile())
                System.out.println("File was created successfully!");
            else
                System.out.println("Error, file already exists. Instructions have been RE-WRITTEN in the file!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileName;
    }

    public static ArrayList<String> fileHandlesOpening(String fileName) {
        // Open and sort through lines, ignoring comments and empty space
        BufferedReader reader;
        ArrayList<String> instructions = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                if (!line.isEmpty() || !line.isBlank()) {
                    if (line.startsWith("//") == false) {
                        instructions.add(line);
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instructions;
    }
}