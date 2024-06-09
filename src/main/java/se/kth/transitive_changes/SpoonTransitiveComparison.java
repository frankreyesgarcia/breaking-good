package se.kth.transitive_changes;

import se.kth.log_Analyzer.ErrorInfo;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.spoon_compare.Client;
import se.kth.spoon_compare.SpoonAnalyzer;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SpoonTransitiveComparison {


    MavenErrorLog mavenErrorLog;
    Client clientProject;
    Client sourceClient;
    CtModel clientModel;
    CtModel sourceModel;

    public SpoonTransitiveComparison(MavenErrorLog mavenErrorLog, Client clientProject, Client sourceClient) {
        this.mavenErrorLog = mavenErrorLog;
        this.clientProject = clientProject;
        this.sourceClient = sourceClient;
        clientModel = clientProject.createModel();
        sourceModel = sourceClient.createModel();

    }


    public Set<MatchElements> findTransitiveChanges() {

        Set<MatchElements> match = new HashSet<>();
        mavenErrorLog.getErrorInfo().forEach((k, v) -> {
            List<CtElement> elements = getElementsByClass(k, v);
            System.out.println(elements.size());
            match.addAll(findElements(elements));
        });
        return match;
    }

    private List<CtElement> getElementsByClass(String file, Set<ErrorInfo> errorInfo) {

        List<String> lines = errorInfo.stream().map(ErrorInfo::getClientLinePosition).toList();
        return clientModel.filterChildren(element ->
                !SpoonAnalyzer.shouldBeIgnored(element)
                        && element.getPosition().isValidPosition()
                        && element.getPosition().toString().contains(file)
                        && lines.contains(String.valueOf(element.getPosition().getLine())
                )
        ).list();
    }


    private List<MatchElements> findElements(List<CtElement> elements) {

        return elements.stream().map(c -> {
            Class<?> a = c.getClass();
//            if(c.getReferencedTypes().contains() instanceof CtConstructor){
//
//            }
            final List<? extends CtElement> elements1 = sourceModel.getElements(new TypeFilter<>(c.getClass()));
            for (CtElement element : elements1) {
                if (element.toString().equals(c.toString())) {
                    return new MatchElements(c, element);
                }
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }


}


