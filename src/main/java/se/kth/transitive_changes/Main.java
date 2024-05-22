package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.data.JsonUtils;

import java.nio.file.Path;
import java.util.Set;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;

public class Main {


    public static void main(String[] args) {

        ApiMetadata apiMetadata = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/fab0f8d4f7322fa6da914c8c9f30baf740d46b99/http-v0.25.jar"));
        ApiMetadata apiMetadata2 = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/fab0f8d4f7322fa6da914c8c9f30baf740d46b99/http-v1.1.0.jar"));

        Dependency oldVersion = new Dependency("com.artipie", "http","jar", "v0.25");
        Dependency newVersion = new Dependency("com.artipie", "http","jar", "v1.1.0");


        Set<Dependency> v1 = MavenTree.read(apiMetadata, oldVersion);
        Set<Dependency> v2 = MavenTree.read(apiMetadata2, newVersion);

        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);


        Set<Dependency> newDependencies = findUniqueDependencies(v1, v2);
        Set<Dependency> removedDependencies = findUniqueDependencies(v2, v1);

        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                compareTransitiveDependency.compareDependency();
                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + compareTransitiveDependency.getBreakingChanges().size());

                JsonUtils.writeToFile(Path.of("breaking-changes-%s.json".formatted(pair.oldVersion().getArtifactId())), compareTransitiveDependency);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
