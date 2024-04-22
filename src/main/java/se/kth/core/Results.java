package se.kth.core;

import se.kth.breaking_changes.BrokenUse;
import se.kth.log_Analyzer.ErrorInfo;

import java.util.HashSet;
import java.util.Set;

@lombok.Getter
public class Results {
    BrokenUse brokenUse;
    Set<ErrorInfo> errorInfo = new HashSet<>();

    public Results(BrokenUse brokenUse, ErrorInfo errorInfo) {
        this.brokenUse = brokenUse;
        this.errorInfo.add(errorInfo);
    }
}
