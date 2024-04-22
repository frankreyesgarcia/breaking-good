package se.kth.explaining;

import se.kth.core.Changes_v2;

import java.io.FileWriter;
import java.io.IOException;

import static java.lang.Thread.sleep;

public abstract class ExplanationTemplate {

    protected Changes_v2 changes_v2;

    protected String fileName;

    public ExplanationTemplate(Changes_v2 changes, String fileName) {
        this.changes_v2 = changes;
        this.fileName = fileName;

    }

    public abstract String getHead();

    public abstract String logLine();

    public abstract String brokenElement();

    public String translateCategory(String category) {
        return switch (category) {
            case "[METHOD_REMOVED]":
            case "REMOVED":
                yield "removed";
            case "[METHOD_ADDED]":
                yield "added";
            default:
                yield "";
        };
    }

    public void generateTemplate() {

        if (changes_v2.changes().getBrokenUse().isEmpty()) {
            return;
        }

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

    ;


}