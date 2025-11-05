import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;

/**
 * Program that converts a .txt file with a pseudocode-like programming language
 * into python code. The python code is written into a .py file.
 * The language is called Pseudopseudocode.
 *
 * @author Atri Sarker
 * @version 1.0
 * @since 2025-11-04
 */
public final class Compiler {
  /**
   * Private constructor to satisfy style checker.
   * @exception IllegalStateException for the utility class.
   * @see IllegalStateException
   */
  private Compiler() {
    // Prevents illegal states.
    throw new IllegalStateException("Utility class.");
  }

  /**
   * Helper function to get everything in a line after a keyword.
   *
   * @param line    The line to check.
   * @param keyword The keyword to check for.
   * @return A string that contains everything after the keyword.
   */
  private static String afterKeyword(final String line, final String keyword) {
    // substring() returns a string starting from keyword.length() to the end.
    return line.substring(keyword.length());
  }

  /**
   * Pseudopseudocode to Python converter.
   * Converts an array of strings representing lines of Pseudopseudocode
   * into a single string representing the equivalent python code.
   * @param arr Array of strings representing lines of Pseudopseudocode.
   * @return A single string representing the equivalent python code.
   */
  public static String convertToPython(final String[] arr) {
    // Indentation defined as 4 spaces
    final String indent = "    ";
    // Represents the current level of indentation
    int indentLevel = 0;
    // Represents the current line number
    int lineNum = 0;
    // String to store the generated code
    String code = "";
    // Loop through every line in the input array
    for (String line : arr) {
      // Increment line number
      lineNum += 1;
      // Remove leading whitespace.
      String trimmedLine = line.stripLeading();
      // String to store the code of the current line
      String pythonLine = "";
      // Add indentation for the line
      code += indent.repeat(indentLevel);
      // MAP pseudopseudocode to python
      if (trimmedLine.startsWith("FUNC ")) {
        // FUNCTION DEFINITION
        String functionDefinition = afterKeyword(trimmedLine, "FUNC ");
        // FUNC funcName(params)  -->  def funcName(params):
        pythonLine = "def " + functionDefinition + ":";
        // Increment indentation level
        indentLevel += 1;
      } else if (trimmedLine.equals("ENDFUNC")) {
        // FUNCTION CLOSE
        // Decrement indentation level
        indentLevel -= 1;
      } else if (trimmedLine.startsWith("RETURN")) {
        // RETURN STATEMENT
        String returnValue = afterKeyword(trimmedLine, "RETURN");
        // RETURN value  -->  return value
        // RETURN --> return
        pythonLine = "return " + returnValue;
      } else if (trimmedLine.startsWith("IF ")) {
        // IF STATEMENT
        String ifCondition = afterKeyword(trimmedLine, "IF ");
        // IF condition  -->  if (condition):
        pythonLine = "if (" + ifCondition + "):";
        indentLevel += 1;
      } else if (trimmedLine.equals("ENDIF")) {
        // IF STATEMENT CLOSE
        // Decrement indentation level
        indentLevel -= 1;
      } else if (trimmedLine.startsWith("WHILE ")) {
        // WHILE LOOP STATEMENT
        String whileCondition = afterKeyword(trimmedLine, "WHILE ");
        // WHILE condition  -->  while (condition):
        pythonLine = "while (" + whileCondition + "):";
        indentLevel += 1;
      } else if (trimmedLine.equals("ENDWHILE")) {
        // WHILE LOOP CLOSE
        // Decrement indentation level
        indentLevel -= 1;
      } else if (trimmedLine.startsWith("SET ")) {
        // ASSIGNMENT STATEMENT
        String assignment = afterKeyword(trimmedLine, "SET ");
        // SET var = value  -->  var = value
        pythonLine = assignment;
      } else if (trimmedLine.startsWith("PRINT ")) {
        // PRINT STATEMENT
        String printArgument = afterKeyword(trimmedLine, "PRINT ");
        // PRINT value  -->  print(value, end="")
        pythonLine = "print(" + printArgument + ", end=\"\")";
      } else if (trimmedLine.startsWith("GETSTRING ")) {
        // STRING INPUT STATEMENT
        String varName = afterKeyword(trimmedLine, "GETSTRING ");
        // GETSTRING varName  -->  varName = input()
        pythonLine = varName + " = input()";
      } else if (trimmedLine.startsWith("CASTASNUM ")) {
        // TYPE CASTING TO NUMBER STATEMENT
        String varName = afterKeyword(trimmedLine, "CASTASNUM ");
        // CASTASNUM varName  -->  varName = float(varName)
        pythonLine = varName + " = float(" + varName + ")";
      } else if (trimmedLine.startsWith("#")) {
        // COMMENT
        // Comment lines are the same in both languages
        pythonLine = trimmedLine;
      } else if (trimmedLine.isEmpty()) {
        // EMPTY LINE
        pythonLine = trimmedLine;
      } else {
        // UNRECOGNIZED LINE
        code = "ERROR: FAILED TO PROCESS LINE " + lineNum;
        break;
      }

      // CHECK IF INDENTATION LEVEL IS VALID
      if (indentLevel < 0) {
        // ERROR MESSAGE
        code = "ERROR: UNEXPECTED CLOSE AT LINE " + lineNum;
        break;
      }

      // Add the converted line to the code string
      code += pythonLine;
      // Add newline
      code += "\n";
    }
    // CHECK IF INDENTATION LEVEL IS BALANCED
    if (indentLevel != 0) {
      // ERROR MESSAGE
      code = "ERROR: INDENTATION MISMATCH";
    }
    // Return the code
    return code;
  }

  /**
   * Entrypoint of the program.
   * @param args For command line arguments.
   */
  public static void main(final String[] args) {
    // First argument is the path to the input file.
    final String inputFilePath = args[0];
    // Second argument is the path to the output file.
    final String outputFilePath = args[1];
    // Print arguments
    System.out.println("Input file: " + inputFilePath);
    System.out.println("Output file: " + outputFilePath);
    // Read the input file
    try {
      // Access the input file and create a File object.
      File inputFile = new File(inputFilePath);
      // Access the output file and create a File object.
      File outputFile = new File(outputFilePath);
      // Scanner that will read the File Object.
      Scanner scanner = new Scanner(inputFile);
      // List to store all the lines in the input file
      ArrayList<String> listOfLines = new ArrayList<>();
      // Loop through all available lines
      while (scanner.hasNextLine()) {
        // Get the line
        String line = scanner.nextLine();
        // Add the line to the list
        listOfLines.add(line);
      }
      // Close the scanner [file reader]
      scanner.close();

      // Convert list to arrays
      // Passing new String[0] to toArray()
      // because toArray() automatically makes a bigger one.
      String[] arrOfLines = listOfLines.toArray(new String[0]);

      // Convert to python code
      String pythonCode = convertToPython(arrOfLines);
      // Write python code to output file
      try {
        // Create a FileWriter object to write to the file
        FileWriter writer = new FileWriter(outputFile);
        // Write the python code to the file
        writer.write(pythonCode);
        // Close the writer
        writer.close();
      } catch (IOException error) {
        System.out.println(error);
      }
    } catch (IOException error) {
      System.out.println(error);
    }
    // Completion message
    System.out.println("DONE!");
  }
}
