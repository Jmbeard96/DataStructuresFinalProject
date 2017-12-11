/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beardsyntaxcheckerclient;

import java.util.Scanner;

/**
 *
 * @author Jeremy Beard
 */
public class BeardSyntaxCheckerClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        String inputFile;
        String outputFile;

        System.out.println("Enter file path for the file you'd like to check.");
        inputFile = scan.nextLine();
        System.out.println("Enter file path for the file to which you'd like to"
                + " write any errors.");
        outputFile = scan.nextLine();

        BeardSyntaxChecker sc = new BeardSyntaxChecker(inputFile, outputFile);

        sc.checkFile();

    }

}
