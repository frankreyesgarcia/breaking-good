package se.kth.explaining;

import se.kth.breaking_changes.BrokenUse;
import se.kth.core.BreakingChange;
import se.kth.core.BrokenChange;
import se.kth.core.Changes_v2;
import se.kth.core.Results;
import se.kth.log_Analyzer.ErrorInfo;
import se.kth.spoon_compare.SpoonResults;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

public class CompilationErrorTemplate extends ExplanationTemplate {


    public CompilationErrorTemplate(Changes_v2 changes, String fileName) {
        super(changes, fileName);
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes_v2.oldApiVersion().getName(), changes_v2.newApiVersion().getName());
    }

    public String clientErrorLine(ErrorInfo errorInfo, BrokenUse brokenUse) {
        return "            *   An error was detected in line %s which is making use of an outdated API.\n".formatted(errorInfo.getClientLinePosition()) +
                "             ``` java\n" +
                "             %s   %s;\n".formatted(errorInfo.getClientLinePosition(), brokenUse.element()) +
                "            ```\n";
    }

    @Override
    public String logLine() {
        return "";
    }


    public String logLineErrorMessage(ErrorInfo errorInfo) {
        try {
            return "            *   >[%s](%s)\n".formatted(errorInfo.getErrorMessage().concat("<br>&nbsp;&nbsp;&nbsp;&nbsp;" + errorInfo.getAdditionalInfo()), errorInfo.getErrorLogGithubLink());
        } catch (
                Exception e) {

            return "";
        }

    }

    public String errorSection(Results breakingChange, int instructions) {
        StringBuilder message = new StringBuilder();
        for (ErrorInfo spoonResults : breakingChange.getErrorInfo()) {
            try {
                message.append(logLineErrorMessage(spoonResults)).append(clientErrorLine(spoonResults, breakingChange.getBrokenUse()));
            } catch (Exception e) {
                return "";
            }

        }
        return message.toString();
    }


    /**
     * This method generates the broken element section of the markdown file
     *
     * @return String broken element section
     */
    @Override
    public String brokenElement() {

        String message = "";
        // if there are more than one changes
        if (!changes_v2.changes().getBrokenUse().isEmpty()) {
            String instructions = changes_v2.changes().getBrokenUse().size() > 1 ? "instructions" : "instruction";
            String firstLine = "1. Your client utilizes **%d** %s which has been modified in the new version of the dependency."
                    .formatted(changes_v2.changes().getBrokenUse().size(), instructions);

            String text = "";
            for (Results results : changes_v2.changes().getBrokenUse()) {
                String category = translateCategory(results.getBrokenUse().change().toString());
                final var singleChange = generateElementExplanation(results, category, this.changes_v2.changes().getBrokenUse().size());
                text = text.concat(singleChange);
            }
            message = firstLine + "\n" + text;
        }
        return message;
    }

    private String generateElementExplanation(Results result, String category, int instructions) {
        BrokenUse use = result.getBrokenUse();
        if (instructions > 1) {
            return "   * <details>\n" +
                    "        <summary>%s <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(use.change(),use.usedApiElement().toString(), category) +
                    "            \n" +
                    "        * <details>\n" +
                    "          <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(result, instructions) +
                    "\n" +
                    "          </details>\n" +
                    "            \n" +
//                    newCandidates(changes) +
                    "     </details>\n";
        } else {
            return "   * <summary>%s <b>%s</b> which has been <b>%s</b> in the new version of the dependency</summary>\n".formatted(use.change(),use.usedApiElement().toString(), category) +
                    "            \n" +
                    "        *  <summary>The failure is identified from the logs generated in the build process. </summary>\n" +
                    "          \n" +
                    errorSection(result, instructions) +
                    "            \n";
//                    newCandidates(changes);""

        }
    }

    private String getName(CtElement bc) {
        return bc instanceof CtNamedElement ne ? ne.getSimpleName() : bc.getShortRepresentation();
    }


    /**
     * This method translates the category of the change to a human-readable format
     *
     * <p>To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible instruction currently used in the client. You can consider substituting the existing instruction with one of the following options provided by the new version of the dependency
     * ``` java
     * net.datafaker.DateAndTime.between(java.sql.Timestamp,java.sql.Timestamp);
     * <p/> ```
     *
     * @param breakingChange BreakingChange
     * @return Number of new candidates and their method signature
     */
    public String newCandidates(BreakingChange breakingChange) {
        if (breakingChange.getApiChanges().getNewVariants().isEmpty()) {
            return "";
        }
        int amountVariants = breakingChange.getApiChanges().getNewVariants().size();
        StringBuilder message = new StringBuilder();

        if (amountVariants > 1) {
            message.append("        To address this incompatibility, there are ")
                    .append(amountVariants)
                    .append(" alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency:\n".formatted(breakingChange.getApiChanges().getInstruction().toLowerCase(), breakingChange.getApiChanges().getInstruction().toLowerCase()));

            breakingChange.getApiChanges().getNewVariants().forEach(v -> {
                message.append("        ``` java\n")
                        .append("        ").append(v.getReference().variantName()).append(";\n")
                        .append("        ```\n")
                ;
            });
        } else {
            message.append(
                    "        To resolve this issue, there are alternative options available in the new version of the dependency that can replace the incompatible %s currently used in the client. You can consider substituting the existing %s with one of the following options provided by the new version of the dependency\n".formatted(breakingChange.getApiChanges().getInstruction().toLowerCase(), breakingChange.getApiChanges().getInstruction().toLowerCase()));
            breakingChange.getApiChanges().getNewVariants().forEach(v -> {
                if (v.getReference() != null)
                    try {
                        message.append("        ``` java\n")
                                .append("        ").append(v.getReference().variantName()).append(";\n")
                                .append("        ```\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
        }
        return message.toString();
    }


}
