package se.kth.core;

import se.kth.breaking_changes.BrokenUse;
import se.kth.log_Analyzer.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

@lombok.Getter
@lombok.Setter
public class BrokenChange {

    private List<Results> brokenUse;

    public BrokenChange() {
        this.brokenUse = new ArrayList<>();
    }


    public void addBrokenUse(BrokenUse use, ErrorInfo errorInfo) {
        for (Results results : brokenUse) {
            if (results.getBrokenUse().usedApiElement().toString().equals(use.usedApiElement().toString())) {
                results.getErrorInfo().add(errorInfo);
                return;
            }
        }
        Results results = new Results(use, errorInfo);
        brokenUse.add(results);

    }


}


