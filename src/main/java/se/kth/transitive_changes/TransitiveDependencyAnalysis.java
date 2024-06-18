package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiChange;
import se.kth.breaking_changes.ApiMetadata;
import se.kth.breaking_changes.BreakingGoodOptions;
import se.kth.breaking_changes.JApiCmpAnalyze;
import se.kth.core.Changes_V2;
import se.kth.core.CombineResults;
import se.kth.explaining.TransitiveExplanationTemplate;
import se.kth.japianalysis.BreakingChange;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class TransitiveDependencyAnalysis {

    JApiCmpAnalyze jApiCmpAnalyzer;
    Client client;
    MavenErrorLog mavenLogAnalyzer;

    public TransitiveDependencyAnalysis(JApiCmpAnalyze jApiCmpAnalyzer, Client client, MavenErrorLog mavenLogAnalyzer) {
        this.jApiCmpAnalyzer = jApiCmpAnalyzer;
        this.client = client;
        this.mavenLogAnalyzer = mavenLogAnalyzer;
    }


    public static void compareTransitiveDependency(
            ApiMetadata oldApiVersion,
            Dependency oldVersion,
            ApiMetadata newApiVersion,
            Dependency newVersion,
            JApiCmpAnalyze jApiCmpAnalyze,
            Client client, Set<ApiChange> apiChanges,
            MavenErrorLog mavenLogAnalyzer,
            String commitId) {

        Changes_V2 changesV2;
        CombineResults combineResults;
        List<BreakingChangeVisitor> visitors;
        CtModel model;
        BreakingGoodOptions options;
        Set<Dependency> v1 = MavenTree.read(oldApiVersion, oldVersion);
        Set<Dependency> v2 = MavenTree.read(newApiVersion, newVersion);
//
        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);


        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                List<BreakingChange> changes = compareTransitiveDependency.getChangesBetweenDependencies();
                visitors = jApiCmpAnalyze.getVisitors(changes);
                options = new BreakingGoodOptions();

                ApiMetadata oldTransitiveDep = compareTransitiveDependency.getOldApiMetadata();

                client.setClasspath(List.of(oldTransitiveDep.getFile()));

                model = client.createModel();
                combineResults = new CombineResults(apiChanges, oldApiVersion, newApiVersion, mavenLogAnalyzer, model);
                String folderPath = client.getSourcePath().toString().lastIndexOf("/") > 0 ?
                        client.getSourcePath().toString().substring(0, client.getSourcePath().toString().lastIndexOf("/")) : client.getSourcePath().toString();
                combineResults.setProject(folderPath);

                changesV2 = combineResults.analyze_v2(visitors, options);

                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + changesV2.brokenChanges().size());

                if (!changesV2.brokenChanges().isEmpty()) {
                    generateExplanation(changesV2, pair, commitId);
                    break;
                } else {
                    System.out.println("No breaking changes found for " + oldVersion + " and " + newVersion);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateExplanation(Changes_V2 changes, PairTransitiveDependency currentPair, String commitId) throws IOException {

        if (!Files.exists(Path.of("Explanations/Transitive"))) {
            Files.createDirectory(Path.of("Explanations/Transitive"));
        }

        TransitiveExplanationTemplate explanationTemplate = new TransitiveExplanationTemplate(changes, currentPair,
                "Explanations/Transitive/%s.md".formatted(commitId));
        explanationTemplate.generateTemplate();
    }

}
