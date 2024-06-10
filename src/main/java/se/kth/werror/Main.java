package se.kth.werror;

import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;

public class Main {


    /**
     * Main method to run the program
     * One line trigger werror detection
     * Many lines contains warning detection
     * @param args
     */

    public static void main(String[] args) {
        String log = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem/c5fd5187ce64d2b53602717f09cc18dd21d55e8d.log";
        String client = "/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem";

        WError werror = new WError("werror.md");

        try {
            werror.analyzeWerror(log, client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
