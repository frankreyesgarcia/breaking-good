package se.kth.werror;

import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;

import java.io.File;
import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem/c5fd5187ce64d2b53602717f09cc18dd21d55e8d.log"));

        try {
            MavenErrorLog errorLog = mavenLog.extractWarningLines(mavenLog.getLogFile().getAbsolutePath());

            MavenErrorLog wError = mavenLog.extractWerrorLine(mavenLog.getLogFile().getAbsolutePath());

            System.out.println("Happy Happy Happy");

            WError wError1 = new WError();


            wError1.findWerror(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/c5fd5187ce64d2b53602717f09cc18dd21d55e8d/nem"));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
