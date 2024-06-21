package se.kth.transitive_changes;

import se.kth.breaking_changes.*;
import se.kth.core.Changes_V2;
import se.kth.core.CombineResults;
import se.kth.explaining.RemAddModdIndirectTemplate;
import se.kth.explaining.TransitiveExplanationTemplate;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.sponvisitors.BreakingChangeVisitor;
import se.kth.spoon_compare.Client;
import spoon.reflect.CtModel;
import util.MavenCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;

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
            String commitId,
            CtModel modelClientApp
    ) {

        Changes_V2 changesV2 = null;
        CombineResults combineResults;
        List<BreakingChangeVisitor> visitors;
        CtModel model;
        BreakingGoodOptions options;
        Set<Dependency> v1 = MavenTree.read(oldApiVersion, oldVersion);
        Set<Dependency> v2 = MavenTree.read(newApiVersion, newVersion);
//
//        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);
//
//
//        for (PairTransitiveDependency pair : transitiveDependencies) {
//            try {
//                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
//                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
//                List<BreakingChange> changes = compareTransitiveDependency.getChangesBetweenDependencies();
//                visitors = jApiCmpAnalyze.getVisitors(changes);
//                options = new BreakingGoodOptions();
//
//                ApiMetadata oldTransitiveDep = compareTransitiveDependency.getOldApiMetadata();
//
//                client.setClasspath(List.of(oldTransitiveDep.getFile()));
//
//                model = client.createModel();
//                combineResults = new CombineResults(apiChanges, oldApiVersion, newApiVersion, mavenLogAnalyzer, model);
//                String folderPath = client.getSourcePath().toString().lastIndexOf("/") > 0 ?
//                        client.getSourcePath().toString().substring(0, client.getSourcePath().toString().lastIndexOf("/")) : client.getSourcePath().toString();
//                combineResults.setProject(folderPath);
//
//                changesV2 = combineResults.analyze_v2(visitors, options);
//
//                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
//                System.out.println("Breaking Changes amount: " + changesV2.brokenChanges().size());
//
//                if (!changesV2.brokenChanges().isEmpty()) {
//                    generateExplanation(changesV2, pair, commitId);
//                    break;
//                } else {
//                    System.out.println("No breaking changes found for " + oldVersion + " and " + newVersion);
//                    //find added and removed dependencies
//
//                }
//            } catch (Exception e) {
////                e.printStackTrace();
//                System.out.println("Error comparing " + pair.newVersion() + " and " + pair.oldVersion()+ " "+e.getMessage());
//            }
//        }

        if (changesV2 == null || changesV2.brokenChanges().isEmpty()) {
            dep(v1, v2, mavenLogAnalyzer, client, commitId, oldApiVersion, newApiVersion, "Removed",modelClientApp);
            dep(v2, v1, mavenLogAnalyzer, client, commitId, newApiVersion, oldApiVersion, "Added",modelClientApp);
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

    public static void dep(Set<Dependency> v1, Set<Dependency> v2, MavenErrorLog mavenErrorLog, Client client, String commitId, ApiMetadata oldApiVersion, ApiMetadata newApiVersion, String action,CtModel modelClientApp) {

        Map<Dependency, List<MatchElements>> matchElements = new HashMap<>();

        //Dependencies removed in the new version
        Set<Dependency> removed = findUniqueDependencies(v2, v1);
        System.out.printf("Removed dependencies: %s%n", removed.size());
        for (Dependency dependency : removed) {
            List<MatchElements> match = new ArrayList<>();
            try {
                System.out.println(dependency);
                Path sourceFolder = null;
                Path source = Path.of("source/" + dependency.getArtifactId() + "-" + dependency.getVersion() + "-sources");
                if (!Files.exists(source)) {
                    sourceFolder = Files.createDirectory(source);
                } else {
                    sourceFolder = source;
                }
                extractClient(dependency, sourceFolder);

                // source code of dependency added
                Client sourceClient = new Client(sourceFolder);

                //client original

                SpoonTransitiveComparison spoonTransitiveComparison = new SpoonTransitiveComparison(mavenErrorLog, client, sourceClient, modelClientApp);

                Set<MatchElements> elements = spoonTransitiveComparison.findTransitiveChanges();

                for (MatchElements element : elements) {
                    System.out.print(element.toString() + " ");
                    System.out.println(element.getClientElement().equals(element.getSourceElement()));
                    if (element.getClientElement().equals(element.getSourceElement())) {
                        match.add(element);
                    }
                }
                if (!match.isEmpty()) {
                    matchElements.put(dependency, match);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error comparing " + dependency.getArtifactId() + " " + e.getMessage());
            }
        }

        try {
            generateExplanation(matchElements, mavenErrorLog, client, commitId, oldApiVersion, newApiVersion, action);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static void extractClient(Dependency sourceDependency, Path output) throws IOException, InterruptedException {


//        Files.createDirectory(path);

//        Path sourcePath = Files.createDirectory((output));

        File sources = Download.getJarFile(sourceDependency.getGroupId(), sourceDependency.getArtifactId(), sourceDependency.getVersion(), output, "sources");

        final var log = Path.of("log.txt");

        assert sources != null;
        MavenCommand.executeCommand("tar -xf %s".formatted(sources.getAbsolutePath()), log, output);

    }

    public static void generateExplanation(Map<Dependency, List<MatchElements>> element, MavenErrorLog mavenErrorLog, Client client,
                                           String commitId,
                                           ApiMetadata oldVersion,
                                           ApiMetadata newVersion, String action) throws IOException {

        Dependency dependency = element.keySet().stream().findFirst().get();
        Changes_V2 v = new Changes_V2(oldVersion, newVersion);

        RemAddModdIndirectTemplate explanationTemplate = new RemAddModdIndirectTemplate(v,
                "Explanations/RemAddMod/%s.md".formatted(commitId), dependency, action);
        explanationTemplate.generateTemplate();
    }

}
