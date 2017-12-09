/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beardsyntaxcheckerclient;

import java.io.*;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Jeremy Beard
 */
public class BeardSyntaxChecker {
    
    private final ArrayList<String> fileData;
    
    public BeardSyntaxChecker(){
        fileData = new ArrayList();
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
    }
    
    public boolean aToZChecker(){
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
                        System.out.println("Encountered z before a at line " + lineNo);
                        System.out.println("Terminating a-z error check.");
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
    
    public String toString(){
        Iterator it = fileData.iterator(); 
        String printString = "";
        while (it.hasNext()){
            printString += ((String)it.next() + '\n');
        }
        return printString;
    }
}
