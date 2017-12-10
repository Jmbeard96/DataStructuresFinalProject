/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beardsyntaxcheckerclient;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Jeremy Beard
 */
public class BeardSyntaxChecker {
    
    private final ArrayList<String> fileData;
    private char legalSymbols[] = new char[5];
    private enum ErrorType {
        ATOZ, PATTERNTHE, NEXTFIVELINES
    }
    
    public BeardSyntaxChecker(){
        fileData = new ArrayList();
        legalSymbols[0] = '#';
        legalSymbols[1] = '@';
        legalSymbols[2] = '&';
        legalSymbols[3] = '*';
        legalSymbols[4] = '!';
        
    }
    
    public void checkFile(String file){
        try{
            File input = new File(file);
            Scanner scan = new Scanner(input);
            
            while(scan.hasNextLine()){
                String fileLine;
                fileLine = scan.nextLine();
                fileData.add(fileLine);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File not found.");
            System.out.println(e);
        }
        if(aToZChecker()){
            System.out.println("No errors!");
        }
        if(!patternThe()){
            System.out.println("No errors!");
        }
        
        
    }
    
    private boolean aToZChecker(){
        Iterator it = fileData.iterator();
        Stack<Character> azStack = new Stack();
        int lineNo = 1;
        boolean isError = false;
        
        while (it.hasNext() && !isError){
            String line = (String)it.next();
            for(int i = 0; i < line.length() && !isError; i++){
                char lineChar = line.charAt(i);
                if(lineChar == 'z'){
                    if(!azStack.empty()){
                        azStack.pop();
                    }
                    else{
                        isError = true;
                        System.out.println(errorMessage(ErrorType.ATOZ, lineNo));
                    }
                }
                if(lineChar == 'a'){
                    azStack.push(lineChar);
                }
            }
            lineNo++;
        }
        lineNo--;
        if(!azStack.empty()){
            System.out.println("Line " +lineNo+ ": Requires more z's." );
            isError = true;
        }
        
        if(!isError){
            return true;
        }
        else{
            return false;
        }
    }
    
    public boolean patternThe(){
        int lineNo = 1;
        boolean containsErrors = false;
        
        for(String fileLine : fileData){
            Stack<Character> theStack = new Stack();
            Stack<Character> argStack = new Stack();
            fileLine = fileLine.toLowerCase();
            boolean patternFound = false;
            
            for(int i = 0; i < fileLine.length(); i++){
                char argStackTop;
                char theStackTop;
                char lineChar = fileLine.charAt(i);
                
                if(theStack.empty()){
                    //show empty stack without throwing exception.
                    theStackTop = ' ';
                }
                else{
                    theStackTop = theStack.peek();
                }
                if(argStack.empty()){
                    //show empty stack without throwing exception.
                    argStackTop = ' ';
                }
                else{
                    argStackTop = argStack.peek();
                }
                
                //Ignoring whitespace
                if(!Character.isWhitespace(lineChar)){
                    
                    //If pattern "the" has not benn found.
                    if(!patternFound){
                        
                        //Find pattern "the".
                        if(lineChar == 't'){
                            theStack.push(lineChar);
                        }
                        if(theStackTop == 't'){
                            if(lineChar == 'h'){
                                theStack.push(lineChar);
                            }
                            else{
                                //Conditions for pattern "the" not met. 
                                //Reset stack.
                                theStack.clear();
                            }
                        }
                        if(theStackTop == 'h'){
                            if(lineChar == 'e'){
                                //Conditions for pattern "the" met.
                                //Begin search for necessary arguments on next
                                //iteration.
                                patternFound = true;
                            }
                            else{
                                //Conditions for pattern "the" not met. 
                                //Reset stack.
                                theStack.clear();
                            }
                        }
                    }
                    
                    //If pattern "the" has been found.
                    else{
                        
                        //Search for necessary arguments.
                        if(argStackTop == ' '){
                            if(Character.isLetter(lineChar)){
                                argStack.push(lineChar);
                            }
                            else{
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }
                        
                        if(Character.isLetter(argStackTop)){
                            if(Character.isDigit(lineChar)){
                                argStack.push(lineChar);
                            }
                            else{
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }
                        
                        if(Character.isDigit(argStackTop)){
                            if(isLegalSymbol(lineChar)){
                                argStack.push(lineChar);
                            }
                            else{
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo));
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                                containsErrors = true;
                            }
                        }
                        
                        if(isLegalSymbol(argStackTop)){
                            if(Character.isDigit(lineChar)){
                                //All arguments met. Reset stacks.
                                theStack.clear();
                                argStack.clear();
                                patternFound = false;
                            }
                            else{
                                //Arguments for pattern "the" not met.
                                //Print message and reset variables.
                                System.out.println(errorMessage(
                                        ErrorType.PATTERNTHE, lineNo));
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
        return containsErrors;
    }
    
    private boolean isLegalSymbol(char checkChar){
        
        boolean isLegal = false;
        for(int i = 0; i < legalSymbols.length; i++){
            if(checkChar == legalSymbols[i]){
                isLegal = true;
            }
        }
        return isLegal;
    }
        
    private String errorMessage(ErrorType errorType, int lineNo){
        String returnString = "";
        
        switch(errorType){
            case ATOZ:
                returnString = "Line " + lineNo + ": \'z\' before preceding \'a\'";
                break;
            case PATTERNTHE:
                returnString = "Line " + lineNo + ": Pattern \"the\" without proper arguments.";
                break;
            default:
                break;
        }
        return returnString;
    }
    
    public String toString(){
        Iterator it = fileData.iterator(); 
        String printString = "";
        while (it.hasNext()){
            printString += ((String)it.next() + '\n');
        }
        return printString;
    }
}
