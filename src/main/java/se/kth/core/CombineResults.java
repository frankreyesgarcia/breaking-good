package se.kth.core;

import japicmp.model.JApiChangeStatus;
import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BrokenUse;
import se.kth.japicompare.IBreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.SpoonAnalyzer;
import se.kth.spoon_compare.SpoonResults;
import se.kth.spoon_visitor.BreakingChangeVisitor;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

;

@lombok.Getter
@lombok.Setter
public class CombineResults {

    private Set<ApiChange> apiChanges;

    MavenErrorLog mavenLog;

    String dependencyGroupID;

    String project;

    Set<ApiChange> breakingChanges;

    ApiMetadata oldVersion;

    ApiMetadata newVersion;

    CtModel model;

    List<IBreakingChange> changes;

    public CombineResults(Set<ApiChange> apiChanges, ApiMetadata oldVersion, ApiMetadata newVersion, MavenErrorLog mavenLog, CtModel model) {
        Objects.requireNonNull(apiChanges);
        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        Objects.requireNonNull(mavenLog);
        Objects.requireNonNull(model);


        this.apiChanges = apiChanges;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.mavenLog = mavenLog;
        this.model = model;
    }public CombineResults( ApiMetadata oldVersion, ApiMetadata newVersion, MavenErrorLog mavenLog, CtModel model) {

        Objects.requireNonNull(oldVersion);
        Objects.requireNonNull(newVersion);
        Objects.requireNonNull(mavenLog);
        Objects.requireNonNull(model);



        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.mavenLog = mavenLog;
        this.model = model;
    }


    public List<BreakingChangeVisitor> getVisitors() {
        return
                changes.stream().map(IBreakingChange::getVisitor).filter(Objects::nonNull).toList();
    }

    public Changes analyze() throws IOException {


        Set<BreakingChange> change = new HashSet<>();
        Set<BrokenUse> use = new HashSet<>();
        try {
            // client.setClasspath(List.of(oldVersion.getFile()));

            mavenLog.getErrorInfo().forEach((k, v) -> {
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(v, apiChanges, model);
//                List<SpoonResults> results = spoonAnalyzer.applySpoon(project + k);
                Set<BrokenUse> tmpUse = spoonAnalyzer.compare(getVisitors(), project + k);
                use.addAll(tmpUse);

//                findBreakingChanges(re, change);
            });
            identifyBreakingChanges(use);

        } catch (Exception e) {
            System.out.println("Error identifying breaking changes in client " + e.toString());
            throw new RuntimeException(e);
        }

        return new Changes(oldVersion, newVersion, change);
    }

    public Changes_v2 analyze_v2() throws IOException {

        Set<BrokenUse> use = new HashSet<>();
        BrokenChange brokenChange = new BrokenChange();
        try {
            mavenLog.getErrorInfo().forEach((k, v) -> {
                SpoonAnalyzer spoonAnalyzer = new SpoonAnalyzer(v, apiChanges, model);
                Set<BrokenUse> tmpUse = spoonAnalyzer.compare(getVisitors(), project + k);
                use.addAll(tmpUse);

            });
            brokenChange = identifyBreakingChanges(use);
        } catch (Exception e) {
            System.out.println("Error identifying breaking changes in client " + e.toString());
            throw new RuntimeException(e);
        }

        return new Changes_v2(oldVersion, newVersion, brokenChange);
    }

    private BrokenChange identifyBreakingChanges(Set<BrokenUse> use) {

        BrokenChange brokenChange = new BrokenChange();

        use.forEach(brokenUse -> {
            mavenLog.getErrorInfo().forEach((k, v) -> {
                if (brokenUse.element().getPosition().toString().contains(k)) {
                    v.forEach(errorInfo -> {
                        if (brokenUse.element().getPosition().getLine() == Integer.parseInt(errorInfo.getClientLinePosition())) {
                            brokenChange.addBrokenUse(brokenUse, errorInfo);
                        }
                    });
                }
            });
        });
        return brokenChange;

    }

    public void findBreakingChanges(List<SpoonResults> spoonResults, Set<BreakingChange> change) {

        spoonResults.forEach(spoonResult -> {
            apiChanges.forEach(apiChange -> {
                if ((apiChange.getChangeType().equals(JApiChangeStatus.REMOVED) ||
                        apiChange.getChangeType().equals(JApiChangeStatus.MODIFIED)
                ) && apiChange.getName().equals(spoonResult.getName())) {
                    for (BreakingChange breakingChange : change) {
                        if (breakingChange.getApiChanges().getName().equals(apiChange.getName())) {
                            breakingChange.addErrorInfo(spoonResult);
                            return;
                        }
                    }
                    BreakingChange breakingChange = new BreakingChange(apiChange);
                    breakingChange.addErrorInfo(spoonResult);
                    change.add(breakingChange);
                    // find new variants
                    Set<ApiChange> newVariants = findNewVariant(apiChange);
                    apiChange.setNewVariants(newVariants);

                }
            });
        });
    }

    public Set<ApiChange> findNewVariant(ApiChange apiChange) {

        Set<ApiChange> newVariants = new HashSet<>();
        apiChanges.forEach(apiChange1 -> {
            if (apiChange1.getName().equals(apiChange.getName()) && !apiChange1.getChangeType().equals(JApiChangeStatus.REMOVED)) {
                newVariants.add(apiChange1);
            }
        });
        return newVariants;
    }
}