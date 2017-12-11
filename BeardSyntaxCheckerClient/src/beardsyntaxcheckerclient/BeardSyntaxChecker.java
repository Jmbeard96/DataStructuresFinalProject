/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beardsyntaxcheckerclient;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Jeremy Beard
 */
public class BeardSyntaxChecker {

    private final ArrayList<String> fileData;
    private File inputFile;
    private File outputFile;
    private char legalSymbols[] = new char[5];

    private enum ErrorType {
        ATOZ, PATTERNTHE, ILLEGALSYMBOL
    }

    public BeardSyntaxChecker(String inputFilePath, String outputFilePath) {
        inputFile = new File(inputFilePath);
        outputFile = new File(outputFilePath);
        legalSymbols[0] = '#';
        legalSymbols[1] = '@';
        legalSymbols[2] = '&';
        legalSymbols[3] = '*';
        legalSymbols[4] = '!';
        fileData = new ArrayList();

        try {
            Scanner scan = new Scanner(inputFile);

            while (scan.hasNextLine()) {
                String fileLine;
                fileLine = scan.nextLine();
                fileData.add(fileLine);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            System.out.println(e);
        }

        try {
            try (FileWriter writer = new FileWriter(outputFile)) {
                int lineNo = 1;
                for (String fileLine : fileData) {
                    writer.write(lineNo + ": " + fileLine + '\n');
                    lineNo++;
                }
                writer.write("\n\n\nErrors:\n");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * @Description Attempts to open file and read the data into ArrayList
     * instance variable fileData. Calls error checking methods to check syntax
     * of file
     */
    public void checkFile() {
        boolean errorsFound = false;
        if (!illegalSymbol()) {
            errorsFound = true;
        }
        if (!aToZChecker()) {
            errorsFound = true;
        }
        if (!patternThe()) {
            errorsFound = true;
        }
        if (!nextFiveLines()) {
            errorsFound = true;
        }
        try{
            try (FileWriter writer = new FileWriter(outputFile, true)) {
                writer.append("No errors detected.");
            }
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    private boolean illegalSymbol() {
        boolean containsErrors = false;
        int lineNo = 1;

        for (String fileLine : fileData) {
            for (int i = 0; i < fileLine.length(); i++) {
                char charToCheck = fileLine.charAt(i);

                if (!Character.isWhitespace(charToCheck)) {
                    if (!Character.isLetter(charToCheck)) {
                        if (!Character.isDigit(charToCheck)) {
                            if (!isLegalSymbol(charToCheck)) {
                                containsErrors = true;
                                System.out.println(
                                        errorMessage(ErrorType.ILLEGALSYMBOL,
                                                lineNo, charToCheck));
                            }
                        }
                    }
                }
            }
            lineNo++;
        }

        return !containsErrors;
    }

    /**
     * @Description Checks ArrayList for balanced a's and z's
     * @return true if no errors were detected.
     */
    private boolean aToZChecker() {
        Iterator it = fileData.iterator();
        Stack<Character> azStack = new Stack();
        int lineNo = 1;
        boolean isError = false;

        while (it.hasNext() && !isError) {
            String line = (String) it.next();
            for (int i = 0; i < line.length() && !isError; i++) {
                char lineChar = line.charAt(i);
                if (lineChar == 'z') {
                    if (!azStack.empty()) {
                        azStack.pop();
                    } else {
                        isError = true;
                        System.out.println(errorMessage(ErrorType.ATOZ, lineNo, ' '));
                    }
                }
                if (lineChar == 'a') {
                    azStack.push(lineChar);
                }
            }
            lineNo++;
        }
        if (!azStack.empty()) {
            int remaining = azStack.size();
            System.out.println(errorMessage(--lineNo, remaining));
            isError = true;
        }

        //return true if no errors were found.
        return !isError;
    }

    /**
     * @Description Checks ArrayList against specified pattern involving the
     * word "the"
     * @return true if no errors were found
     */
    private boolean patternThe() {
        int lineNo = 1;
        boolean containsErrors = false;

        for (String fileLine : fileData) {
            Stack<Character> theStack = new Stack();
            Stack<Character> argStack = new Stack();
            fileLine = fileLine.toLowerCase();
            boolean patternFound = false;

            for (int i = 0; i < fileLine.length(); i++) {
                char argStackTop;
                char theStackTop;
                char lineChar = fileLine.charAt(i);
                char noChar = Character.MIN_VALUE;

                if (theStack.empty()) {
                    //show empty stack without throwing exception.
                    theStackTop = noChar;
                } else {
                    theStackTop = theStack.peek();
                }
                if (argStack.empty()) {
                    //show empty stack without throwing exception.
                    argStackTop = noChar;
                } else {
                    argStackTop = argStack.peek();
                }

                //Ignoring whitespace
                if (!Character.isWhitespace(lineChar)) {

                    //If pattern "the" has not benn found.
                    if (!patternFound) {

                        //Find pattern "the".
                        if (lineChar == 't') {
                            theStack.push(lineChar);
                        }
                        if (theStackTop == 't') {
                            if (lineChar == 'h') {
                                theStack.push(lineChar);
                            } else {
                                //Conditions for pattern "the" not met. 
                                //Reset stack.
                                theStack.clear();
                            }
                        }
                        if (theStackTop == 'h') {
                            if (lineChar == 'e') {
                                //Conditions for pattern "the" met.
                                //Begin search for necessary arguments on next
                                //iteration.
                                patternFound = true;
                            } else {
                                //Conditions for pattern "the" not met. 
                                //Reset stack.
                                theStack.clear();
                            }
                        }
                    } //If pattern "the" has been found.
                    else {

                        //Search for necessary arguments.
                        if (argStackTop == noChar) {
                            if (Character.isLetter(lineChar)) {
                                argStack.push(lineChar);
                            } else {
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo, noChar));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }

                        if (Character.isLetter(argStackTop)) {
                            if (Character.isDigit(lineChar)) {
                                argStack.push(lineChar);
                            } else {
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo, noChar));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }

                        if (Character.isDigit(argStackTop)) {
                            if (isLegalSymbol(lineChar)) {
                                argStack.push(lineChar);
                            } else {
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo, noChar));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }

                        if (isLegalSymbol(argStackTop)) {
                            if (Character.isDigit(lineChar)) {
                                //All arguments met. Reset stacks.
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                            } else {
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo, noChar));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }
                    }
                }
            }
            lineNo++;
        }
        //return true if no errors were found.
        return !containsErrors;
    }

    /**
     * @Description Checks ArrayList so that each element's first character is
     * contained in the next five elements' first ten characters.
     * @return true if no errors were found.
     */
    private boolean nextFiveLines() {
        boolean errorsDetected = false;

        //Create ListIterator to keep track of first char of each line.
        ListIterator fileIt = fileData.listIterator();

        //Line number of each required character.
        int firstCharLineNo = 1;

        while (fileIt.hasNext()) {
            String fileLine = (String) fileIt.next();

            //Ignoring case.
            fileLine = fileLine.toLowerCase();

            //Start second iterator at the line of the first iterator.
            int itIndex = fileIt.nextIndex();
            ListIterator it = fileData.listIterator(itIndex);

            //The character that must be in the first ten characters of the next 
            //five lines
            char necessaryChar = fileLine.charAt(0);
            int count = 0;

            //Line number of the next five lines after the line with required 
            //character.
            int requiredCharLineNo = firstCharLineNo + 1;

            while (count < 5 && it.hasNext()) {
                boolean charFound = false;

                //Move second iterator to line after first iterator
                String nextLine = (String) it.next();

                //Ignoring case
                nextLine = nextLine.toLowerCase();

                for (int i = 0; i < 10 && i < nextLine.length(); i++) {
                    if (nextLine.charAt(i) == necessaryChar) {
                        charFound = true;

                        //Exit for loop as soon as character is found.
                        break;
                    }
                }

                if (!charFound) {
                    errorsDetected = true;
                    //print error
                    System.out.println(errorMessage(firstCharLineNo,
                            requiredCharLineNo, necessaryChar));
                }
                count++;
                requiredCharLineNo++;
            }
            firstCharLineNo++;
        }

        //return true if no errors were detected.
        return !errorsDetected;
    }

    private boolean isLegalSymbol(char checkChar) {

        boolean isLegal = false;
        for (int i = 0; i < legalSymbols.length; i++) {
            if (checkChar == legalSymbols[i]) {
                isLegal = true;
            }
        }
        return isLegal;
    }

    private String errorMessage(int lineNo, int remainingZs) {
        String returnString;
        if (remainingZs > 1) {
            returnString = "Line " + lineNo + ": Requires " + remainingZs
                    + " more z's at the end.";
        } else {
            returnString = "Line " + lineNo + ": Requires " + remainingZs
                    + " more z at the end.";
        }
        try {
            try (FileWriter writer = new FileWriter(outputFile, true)) {
                writer.append(returnString + '\n');
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return returnString;
    }

    /**
     *
     * @param firstLineNo Line number of elements whose first character must be
     * within next five elements' first ten characters.
     * @param secondLineNo Line number of five succeeding elements
     * @param necChar Character that must be within next five elements' first
     * ten characters.
     * @return String containing error message.
     */
    private String errorMessage(int firstLineNo, int secondLineNo, char necChar) {
        String returnString = "Line " + firstLineNo + ": Begins with \'" + necChar + "\'. Line "
                + secondLineNo + " does not have a \'" + necChar + "\' in the "
                + "first ten characters.";
        try {
            try (FileWriter writer = new FileWriter(outputFile, true)) {
                writer.append(returnString + '\n');
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        return returnString;
    }

    /**
     *
     * @param errorType
     * @param lineNo Line on which the error occurred
     * @return String containing error message for specified error type.
     */
    private String errorMessage(ErrorType errorType, int lineNo, char illegalSym) {
        String returnString = "";
        try {
            try (FileWriter writer = new FileWriter(outputFile, true)) {
                switch (errorType) {
                    case ATOZ:
                        returnString = "Line " + lineNo + ": \'z\' before preceding \'a\'";
                        writer.append(returnString + '\n');
                        break;
                    case PATTERNTHE:
                        returnString = "Line " + lineNo + ": Pattern \"the\" without proper arguments.";
                        writer.append(returnString + '\n');
                        break;
                    default:
                        returnString = "Line " + lineNo + ": Contains use of illegal symbol \'" + illegalSym + "\'";
                        writer.append(returnString + '\n');
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return returnString;
    }

    /**
     *
     * @return String containing formatted contents of instance variable
     * fileData
     */
    @Override
    public String toString() {
        Iterator it = fileData.iterator();
        String printString = "";
        while (it.hasNext()) {
            printString += ((String) it.next() + '\n');
        }
        return printString;
    }
}
