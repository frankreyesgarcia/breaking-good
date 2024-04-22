package se.kth.breaking_changes;

import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.japicompare.IBreakingChange;
import se.kth.japicompare.IBreakingChange;
import se.kth.japicompare.JApiCmpDeltaVisitor;
import se.kth.japicompare.JApiCmpToSpoonVisitor;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;

import java.util.List;
import java.util.Set;

@lombok.Setter
@lombok.Getter
public class JApiCmpAnalyze {
    private static final Logger log = LoggerFactory.getLogger(JApiCmpAnalyze.class);


    private final ApiMetadata oldJar;
    private final ApiMetadata newJar;


    public JApiCmpAnalyze(ApiMetadata oldJar, ApiMetadata newJar) {
        this.oldJar = oldJar;
        this.newJar = newJar;
    }


    public Set<ApiChange> useJApiCmp() {
        BreakingGoodOptions options = new BreakingGoodOptions();

        Options defaultOptions = options.getDefaultOptions();
        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.getFile().toFile(), newJar.getName());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.getFile().toFile(), oldJar.getName());

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);

        BreakingGoodFilter filter = new BreakingGoodFilter(options);
        filter.filter(jApiClasses);

        JApiCmpElements jApiCmpElements = new JApiCmpElements();
        JApiCompareScan.visit(jApiClasses, jApiCmpElements);

        System.out.println("JApiCmp own  changes:  " + jApiCmpElements.getChanges().size());

        return jApiCmpElements.getChanges();
    }

    public List<IBreakingChange> useJApiCmp_v2() {
        BreakingGoodOptions options = new BreakingGoodOptions();

        Options defaultOptions = options.getDefaultOptions();

        JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(defaultOptions);
        comparatorOptions.setClassPathMode(JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH);
        comparatorOptions.getClassPathEntries().addAll(oldJar.buildClasspath());

        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        JApiCmpArchive newF = new JApiCmpArchive(newJar.getFile().toFile(), newJar.getName());
        JApiCmpArchive old = new JApiCmpArchive(oldJar.getFile().toFile(), oldJar.getName());


        List<JApiClass> jApiClasses = jarArchiveComparator.compare(old, newF);

        BreakingGoodFilter filter = new BreakingGoodFilter(options);
        filter.filter(jApiClasses);

        // build model
        CtModel model = oldJar.buildModel();
        CtPackage root = model.getRootPackage();

        // from maracas
        JApiCmpToSpoonVisitor visitor = new JApiCmpToSpoonVisitor(root);
        JApiCmpDeltaVisitor.visit(jApiClasses, visitor);

        System.out.println("Maracas changes:  " + visitor.getBreakingChanges().size());

        return visitor.getBreakingChanges();
    }
}
