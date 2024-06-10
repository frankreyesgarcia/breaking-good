package se.kth.explaining;

import se.kth.werror.WErrorMetadata;

public class WErrorTemplate extends ExplanationTemplate {


    public WErrorTemplate(WErrorMetadata errorMetadata, String fileName) {
        super(fileName);
    }

    @Override
    public String getHead() {

        return "CI detected that the dependency upgrade from version **%s** to **%s** has failed. Here are details to help you understand and fix the problem:"
                .formatted(changes.oldApiVersion().getName(), changes.newApiVersion().getName());
    }

    @Override
    public String logLine() {
        return null;
    }

    @Override
    public String brokenElement() {
        return null;
    }
}
