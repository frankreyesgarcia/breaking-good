package se.kth.transitive_changes;

import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.Client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class NewOrRemoved {


    public static void main(String[] args) throws IOException {

        MavenLogAnalyzer mavenLog = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/Java-incom/Hamcrest/db02c6bcb989a5b0f08861c3344b532769530467/docker-adapter/db02c6bcb989a5b0f08861c3344b532769530467.log"));


//

        Client sourceClient = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/Java-incom/Hamcrest/hamcrest-core-1.3-sources"));
        sourceClient.setClasspath(List.of(Path.of("/Users/frank/Documents/Work/PHD/Explaining/Java-incom/cactoos-0.46.jar")));
        Client client = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/Java-incom/Hamcrest/db02c6bcb989a5b0f08861c3344b532769530467/docker-adapter"));
//        client.setClasspath(List.of(Path.of());


        SpoonTransitiveComparison spoonTransitiveComparison = new SpoonTransitiveComparison(mavenLog.analyzeCompilationErrors(), client, sourceClient);

        List<SpoonTransitiveComparison.MatchElements> elements = spoonTransitiveComparison.findTransitiveChanges();

        elements.forEach(System.out::println);
//


//        CtModel clientModel = client.createModel();
//        CtModel sourceModel = sourceClient.createModel();
//
//        List<CtElement> elements = clientModel.filterChildren(element ->
//                !SpoonAnalyzer.shouldBeIgnored(element)
//                        && element.getPosition().isValidPosition()
//                        && element.getPosition().toString().contains("docker-adapter/src/test/java/com/artipie/docker/http/CachingProxyITCase.java")
//                        && element.getPosition().getLine() == 170
//        ).list();
//        System.out.println(elements.size());
//
//        elements.forEach(c -> {
//            Class<?> a = c.getClass();
//            System.out.println("A:      " + c.getClass());
//            sourceModel.getElements(new TypeFilter<>(a)).forEach(System.out::println);
//        });

//        List<CtNamedElement> listaPequena = elements.stream()
//                .filter(CtNamedElement.class::isInstance)
//                .map(CtNamedElement.class::cast)
//                .collect(Collectors.toList());
//
//
//        List<CtNamedElement> elements2 = sourceModel.getElements(new TypeFilter<>(CtNamedElement.class));
//        System.out.println(elements2.size());
//
//
//        Set<String> conjuntoNombresGrandes = elements2.stream()
//                .map(e -> {
//                    CtClass<?> declaringClass = e.getParent(CtClass.class);
//                    return (declaringClass != null ? declaringClass.getQualifiedName() : "") + "#" + e.getSimpleName();
//                })
//                .collect(Collectors.toSet());
//
//        List<CtNamedElement> elementosEnComun = listaPequena.stream()
//                .filter(e -> {
//                    CtClass<?> declaringClass = e.getParent(CtClass.class);
//                    String qualifiedName = (declaringClass != null ? declaringClass.getQualifiedName() : "") + "#" + e.getSimpleName();
//
//                    return conjuntoNombresGrandes.contains(qualifiedName);
//                })
//                .collect(Collectors.toList());
//
//
//        if (!elementosEnComun.isEmpty()) {
//            System.out.println("Los siguientes elementos de la lista pequeña están en la lista grande:");
//            elementosEnComun.forEach(System.out::println);
//        } else {
//            System.out.println("Ninguno de los elementos de la lista pequeña está en la lista grande.");
//        }

    }

}


