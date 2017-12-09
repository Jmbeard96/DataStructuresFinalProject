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
                        System.out.println("Line " + lineNo + ": \'z\' before preceding \'a\'");
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
            boolean theInStack = false;
            
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
                    argStackTop = ' ';
                }
                else{
                    argStackTop = argStack.peek();
                }
                
                if(!Character.isWhitespace(lineChar)){
                    if(theInStack){
                        if(Character.isLetter(lineChar) && argStackTop == ' '){
                            argStack.push(lineChar);
                        }
                        if(!Character.isLetter(lineChar) && argStackTop == ' '){
                            System.out.println("Line " + lineNo + 
                                    ": Pattern \"the\" without proper arguments.");
                            theStack.clear();
                            argStack.clear();
                            theInStack = false;
                            containsErrors = true;
                        }
                        if(Character.isDigit(lineChar) && 
                                Character.isLetter(argStackTop)){
                            argStack.push(lineChar);
                        }
                        if(!Character.isDigit(lineChar) && 
                                Character.isLetter(argStackTop)){
                            System.out.println("Line " + lineNo + 
                                    ": Pattern \"the\" without proper arguments.");
                            theStack.clear();
                            argStack.clear();
                            theInStack = false;
                            containsErrors = true;
                        }
                        if(isLegalSymbol(lineChar) && 
                                Character.isDigit(argStackTop)){
                            argStack.push(lineChar);
                        }
                        if(!isLegalSymbol(lineChar) && 
                                Character.isDigit(argStackTop)){
                            System.out.println("Line " + lineNo + 
                                    ": Pattern \"the\" without proper arguments.");
                            theStack.clear();
                            argStack.clear();
                            theInStack = false;
                            containsErrors = true;
                        }
                        if(Character.isDigit(lineChar) && 
                                isLegalSymbol(argStackTop)){
                            theStack.clear();
                            argStack.clear();
                            theInStack = false;
                        }
                        if(!Character.isDigit(lineChar) && 
                                isLegalSymbol(argStackTop)){
                            System.out.println("Line " + lineNo + 
                                    ": Pattern \"the\" without proper arguments.");
                            theStack.clear();
                            argStack.clear();
                            theInStack = false;
                            containsErrors = true;
                        }
                        
                    }
                    else{
                        if(lineChar == 't'){
                            theStack.push(lineChar);
                        }
                        if(lineChar == 'h' && theStackTop == 't'){
                            theStack.push(lineChar);
                        }
                        if(lineChar != 'h' && theStackTop == 't'){
                            theStack.clear();
                        }
                        if(lineChar == 'e' && theStackTop == 'h'){
                            theStack.push(lineChar);
                            theInStack = true;
                        }
                        if(lineChar != 'e' && theStackTop == 'h'){
                            theStack.clear();
                        }
                    }
                }
            }
            lineNo++;
        }
        return containsErrors;
    }
    
    public boolean isLegalSymbol(char checkChar){
        
        boolean isLegal = false;
        for(int i = 0; i < legalSymbols.length; i++){
            if(checkChar == legalSymbols[i]){
                isLegal = true;
            }
        }
        return isLegal;
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
