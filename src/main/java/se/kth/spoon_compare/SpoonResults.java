package se.kth.spoon_compare;


import se.kth.log_Analyzer.MavenErrorLog;

@lombok.Getter
@lombok.Setter
public class SpoonResults {

    String name;
    String element;
    String clientLine;
    String pattern;
    MavenErrorLog.ErrorInfo errorInfo;

    public SpoonResults(String name, String element, String clientLine, String pattern, MavenErrorLog.ErrorInfo errorInfo) {
        this.name = name;
        this.element = element;
        this.clientLine = clientLine;
        this.pattern = pattern;
        this.errorInfo = errorInfo;
    }

    public SpoonResults() {
    }

    public String toString() {
        return "SpoonResults{" + "name='" + name + '\'' + ", element='" + element + '\'' + ", line='" + clientLine + '\'' + ", pattern='" + pattern + '\'' + ", errorInfo=" + errorInfo.toString() + '}';

    }
}