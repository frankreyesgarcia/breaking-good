package se.kth.explaining;

import se.kth.core.Changes_V2;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.sponvisitors.BrokenChanges;
import se.kth.transitive_changes.Dependency;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class RemAddModdIndirectTemplate extends ExplanationTemplate {

    private final Dependency dependency;
    String action;

    public RemAddModdIndirectTemplate(Changes_V2 changes,String fileName, Dependency dependency, String action) {
        super(changes,fileName);
        this.dependency = dependency;
        this.action = action;
    }

    @Override
    public String getHead() {

        String header = "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());

        String indirectDependency = "\n" +
                "\n" +
                "Your code depends on indirect dependency **%s-%s** which has been %s remove in the new version of the updated dependency."
                        .formatted(dependency.getArtifactId(), dependency.getVersion(), action);
        return header + indirectDependency;
    }


    public String clientErrorLine(ErrorInfo errorInfo, BrokenChanges brokenChange) {
        return "";
    }
//    public String clientErrorLine(ErrorInfo errorInfo, BrokenChanges brokenChange) {
//        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(errorInfo.getClientLinePosition()) +
//                "             ``` java\n" +
//                "             %s   %s;\n".formatted(errorInfo.getClientLinePosition(), brokenChange.getBrokenUse().element().toString()) +
//                "            ```\n";
//    }

    @Override
    public String logLine() {
        return "";
    }

    @Override
    public String brokenElement() {
        return "";
    }


    public String logLineErrorMessage(ErrorInfo errorInfo) {
        return "";

    }

    public String errorSection(BrokenChanges brokenChange, int instructions) {
        return "";
    }

    public void generateTemplate() {
        FileWriter markdownFile = null;
        try {
            markdownFile = new FileWriter(fileName);
            markdownFile.write(getHead());
            markdownFile.write("\n");
            markdownFile.write(brokenElement());
            markdownFile.write("\n");
            markdownFile.write("\n");
            markdownFile.close(); //
            sleep(3000);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
