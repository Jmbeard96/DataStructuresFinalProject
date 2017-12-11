/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beardsyntaxcheckerclient;

/**
 *
 * @author Jeremy Beard
 */
public class BeardSyntaxCheckerClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BeardSyntaxChecker sc = new BeardSyntaxChecker("/Users/jeremybeard/Desktop/test.txt",
                "/Users/jeremybeard/Desktop/testErrorLog.txt");

        sc.checkFile();

    }

}
