package se.kth.transitive_changes;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;



    public Dependency(String groupId, String artifactId, String scope, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public Dependency(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;

    }

    @Override
    public String toString() {
        return "Dependency{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return groupId.hashCode() + artifactId.hashCode() + version.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dependency other = (Dependency) obj;
        return groupId.equals(other.groupId)
                && artifactId.equals(other.artifactId)
                && version.equals(other.version);
    }

    public static Set<Dependency> findUniqueDependencies(Set<Dependency> v1, Set<Dependency> v2) {
        Set<String> v1Identifiers = new HashSet<>();

        // Create a set of "groupId:artifactId" for all dependencies in v1
        for (Dependency dep : v1) {
            v1Identifiers.add(dep.getGroupId() + ":" + dep.getArtifactId());
        }

        Set<Dependency> uniqueDependencies = new HashSet<>();

        // Find dependencies in v2 that don't have the same "groupId:artifactId" in v1
        for (Dependency dep : v2) {
            String identifier = dep.getGroupId() + ":" + dep.getArtifactId();
            System.out.println("Identifier: " + identifier);
            if (!v1Identifiers.contains(identifier)) {
                uniqueDependencies.add(dep);
            }
        }

        return uniqueDependencies;
    }
}
