package se.kth.transitive_changes;

import se.kth.breaking_changes.ApiMetadata;
import se.kth.japianalysis.BreakingChange;
import se.kth.data.JsonUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static se.kth.transitive_changes.Dependency.findUniqueDependencies;

public class Main {


    public static void main(String[] args) {

        ApiMetadata apiMetadata = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/db02c6bcb989a5b0f08861c3344b532769530467/asto-core-v1.13.0.jar"));
        ApiMetadata apiMetadata2 = new ApiMetadata(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/db02c6bcb989a5b0f08861c3344b532769530467/asto-core-v1.15.3.jar"));

        Dependency oldVersion = new Dependency("com.artipie", "asto-core","jar", "1.13.0");
        Dependency newVersion = new Dependency("com.artipie", "asto-core","jar", "1.15.0");


        Set<Dependency> v1 = MavenTree.read(apiMetadata, oldVersion);
        Set<Dependency> v2 = MavenTree.read(apiMetadata2, newVersion);

        Set<PairTransitiveDependency> transitiveDependencies = MavenTree.diff(v1, v2);


        Set<Dependency> newDependencies = findUniqueDependencies(v1, v2);
        Set<Dependency> removedDependencies = findUniqueDependencies(v2, v1);

        for (PairTransitiveDependency pair : transitiveDependencies) {
            try {
                System.out.println("Comparing " + pair.newVersion() + " and " + pair.oldVersion());
                CompareTransitiveDependency compareTransitiveDependency = new CompareTransitiveDependency(pair.newVersion(), pair.oldVersion());
                List<BreakingChange> changes = compareTransitiveDependency.getChangesBetweenDependencies();
                System.out.println("Breaking changes for " + pair.newVersion() + " and " + pair.oldVersion());
                System.out.println("Breaking Changes amount: " + changes.size());

                JsonUtils.writeToFile(Path.of("breaking-changes-%s.json".formatted(pair.oldVersion().getArtifactId())), compareTransitiveDependency);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
