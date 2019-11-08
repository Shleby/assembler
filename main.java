import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class main {
    public static Hashtable<String, Integer> symbolTable = new Hashtable<String, Integer>();
    public static Hashtable<String, String> A0BitsTable = new Hashtable<String, String>();
    public static Hashtable<String, String> A1BitsTable = new Hashtable<String, String>();
    public static Hashtable<String, String> DBitsTable = new Hashtable<String, String>();
    public static Hashtable<String, String> JBitsTable = new Hashtable<String, String>();

    public static void main(String[] args) throws IOException {
        // Creating a scanner to get file input (assembly instructions) from user
        System.out.println(
                "Type in file path. In visual studio you can just right click a .asm file in the test files folder and select copy path (shift alt c). This will copy it to your clipboard so you can just paste it below.");
        Scanner input = new Scanner(System.in);

        // Storing filename as a string
        String fileName = input.next();

        if (!fileName.contains(".asm")) {
            input.close();
            throw new IllegalArgumentException("Please submit a file with a .asm extension.");
        }

        // Creates file and the writer to write to the file
        String hackFile = createNewFile(fileName);
        FileWriter writer = new FileWriter(hackFile);

        // Handles everything with opening the file
        ArrayList<String> instructions = new ArrayList<String>();
        instructions = fileHandlesOpening(fileName);

        Hashtable<String, Integer> firstPassTable = firstPass(instructions);
        Hashtable<String, Integer> holder = new Hashtable<String, Integer>();

        String bits, a, d, cInstruct, dBit, cBit, jBit = "";
        int eql, semi = -1;
        int lineTracker = 0;

        for (int i = 0; i < instructions.size(); i++) {
            lineTracker++;
            // Translate A instructions
            String newInst = instructions.get(i).replaceAll("\\s+", "");
            if (newInst.startsWith("@")) {
                // sending the a instruction to be converted to binary and written to .hack file
                // Cut off the @ symbol
                newInst = newInst.substring(1);
                // jump address
                if (firstPassTable.containsKey("(" + newInst + ")")) {
                    int decimal = firstPassTable.get("(" + newInst + ")");
                    bits = Integer.toBinaryString(decimal);
                    while (bits.length() < 16) {
                        bits = "0" + bits;
                    }
                }
                // Check if a value
                else if (newInst.matches("[0-9]+")) {
                    // Convert to an int for decimal->binary conversion
                    int decimal = Integer.parseInt(newInst);
                    // Convert back to a string and add on all the remaining 0's
                    bits = Integer.toBinaryString(decimal);
                    while (bits.length() < 16) {
                        bits = "0" + bits;
                    }
                }
                // else this is a user symbol
                else {
                    if (symbolTable.containsKey(newInst)) {
                        int decimal = symbolTable.get(newInst);
                        bits = Integer.toBinaryString(decimal);
                        while (bits.length() < 16) {
                            bits = "0" + bits;
                        }
                    } else {
                        if (holder.containsKey(newInst)) {
                            int decimal = holder.get(newInst);
                            bits = Integer.toBinaryString(decimal);
                            while (bits.length() < 16) {
                                bits = "0" + bits;
                            }
                        } else {
                            int address = holder.size() + 16;
                            if (address >= 16384) {
                                throw new IllegalStateException("Out of Memory. Line #" + lineTracker);
                            }

                            holder.put(newInst, address);
                            bits = Integer.toBinaryString(address);
                            while (bits.length() < 16) {
                                bits = "0" + bits;
                            }
                        }
                    }
                }
                writer.write(bits);
                writer.write(System.getProperty("line.separator"));
            } else if (newInst.startsWith("(")) {
                continue;
            } else {

                dBit = "";
                cBit = "";
                jBit = "";
                cInstruct = "";
                eql = newInst.indexOf("=");
                semi = newInst.indexOf(";");

                // d=c:j
                if (eql != -1 && semi != -1) {
                    dBit = newInst.substring(0, eql);
                    cBit = newInst.substring(eql + 1, semi);
                    jBit = newInst.substring(semi + 1);
                }
                // c:j
                else if (eql == -1 && semi != -1) {
                    cBit = newInst.substring(0, semi);
                    jBit = newInst.substring(semi + 1);
                }
                // d=c
                else if (eql != -1 && semi == -1) {
                    dBit = newInst.substring(0, eql);
                    cBit = newInst.substring(eql + 1);
                }
                // d
                else {
                    d = newInst;
                }

                if (DBitsTable.containsKey(dBit) && (A1BitsTable.containsKey(cBit) || A0BitsTable.containsKey(cBit))
                        && JBitsTable.containsKey(jBit)) {
                    if (A0BitsTable.containsKey(cBit)) {
                        a = "0";
                        cBit = A0BitsTable.get(cBit);
                    } else {
                        a = "1";
                        cBit = A1BitsTable.get(cBit);
                    }
                    cInstruct = "111" + a + cBit + DBitsTable.get(dBit) + JBitsTable.get(jBit);
                }
                writer.write(cInstruct);
                writer.write(System.getProperty("line.separator"));
            }
        }
        // Closes the Scanner and Writer
        writer.close();
        input.close();
        System.out.println("Operations complete... .hack was written successfully");
    }

    static {
        // put all predefined symbols into a Hashtable
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);

        // When a=0
        A0BitsTable.put("0", "101010");
        A0BitsTable.put("1", "111111");
        A0BitsTable.put("-1", "111010");
        A0BitsTable.put("D", "001100");
        A0BitsTable.put("A", "110000");
        A0BitsTable.put("!D", "001101");
        A0BitsTable.put("!A", "110001");
        A0BitsTable.put("-D", "001111");
        A0BitsTable.put("-A", "110011");
        A0BitsTable.put("D+1", "011111");
        A0BitsTable.put("A+1", "110111");
        A0BitsTable.put("D-1", "001110");
        A0BitsTable.put("A-1", "110010");
        A0BitsTable.put("D+A", "000010");
        A0BitsTable.put("D-A", "010011");
        A0BitsTable.put("A-D", "000111");
        A0BitsTable.put("D&A", "000000");
        A0BitsTable.put("D|A", "010101");

        // When a=1
        A1BitsTable.put("M", "110000");
        A1BitsTable.put("!M", "110001");
        A1BitsTable.put("-M", "110011");
        A1BitsTable.put("M+1", "110111");
        A1BitsTable.put("M-1", "110010");
        A1BitsTable.put("D+M", "000010");
        A1BitsTable.put("D-M", "010011");
        A1BitsTable.put("M-D", "000111");
        A1BitsTable.put("D&M", "000000");
        A1BitsTable.put("D|M", "010101");

        // put all dst posibilities into a Hashtable
        DBitsTable.put("", "000");
        DBitsTable.put("M", "001");
        DBitsTable.put("D", "010");
        DBitsTable.put("MD", "011");
        DBitsTable.put("A", "100");
        DBitsTable.put("AM", "101");
        DBitsTable.put("AD", "110");
        DBitsTable.put("AMD", "111");

        // put all jmp posibilities into a Hashtable
        JBitsTable.put("", "000");
        JBitsTable.put("JGT", "001");
        JBitsTable.put("JEQ", "010");
        JBitsTable.put("JGE", "011");
        JBitsTable.put("JLT", "100");
        JBitsTable.put("JNE", "101");
        JBitsTable.put("JLE", "110");
        JBitsTable.put("JMP", "111");
    }

    public static Hashtable<String, Integer> firstPass(ArrayList<String> instructions) {
        Hashtable<String, Integer> table = new Hashtable<String, Integer>();
        int counter = 0;
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).startsWith("(") && instructions.get(i).endsWith(")")) {
                counter++;
                table.put(instructions.get(i), counter - 1);
            } else {
                counter++;
            }
        }

        return table;
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
                if (!line.isEmpty()) {
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
