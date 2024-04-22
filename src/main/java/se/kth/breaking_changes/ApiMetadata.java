package se.kth.breaking_changes;

import com.google.common.base.Stopwatch;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.support.compiler.SpoonPom;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Represents the metadata of an API
 * Contains the name of the API and the path to the file
 */
@lombok.Setter
@lombok.Getter
public class ApiMetadata {

    private static final Logger log = LoggerFactory.getLogger(ApiMetadata.class);

    String name;

    Path file;

    private static final Map<String, String> repositoryUrls = Map.of(
            "mavenCentral", "https://repo1.maven.org/maven2/",
            "jenkins", "https://repo.jenkins-ci.org/artifactory/releases/"
    );

    public ApiMetadata(String name, Path file) {
        this.name = name;
        this.file = file;

    }

    public ApiMetadata(Path file) {
        this.file = file;
        this.name = file.getFileName().toString();
    }


    public String getName() {
        return name.contains(".jar") ? name.substring(0, name.indexOf(".jar")) : name;
    }

    public CtModel buildModel() {
        Stopwatch sw = Stopwatch.createStarted();
        Launcher launcher = new Launcher();

        // Spoon will prioritize the JVM's classpath over our own
        // custom classpath in case of conflict. Not what we want,
        // so we use a custom child-first classloader instead.
        // cf. https://github.com/INRIA/spoon/issues/3789
        List<URL> jarDependenciesUrl = new ArrayList<>();
        try {
            jarDependenciesUrl.add(new URL("file:" + file));
            for (String dep : buildClasspath())
                jarDependenciesUrl.add(new URL("file:" + dep));
        } catch (MalformedURLException e) {
            // Checked exceptions are a blessing, kill me
            System.out.println(e);
        }
        ClassLoader cl = new ParentLastURLClassLoader(jarDependenciesUrl.toArray(new URL[0]));
        launcher.getEnvironment().setInputClassLoader(cl);

        CtModel spoonModel = launcher.buildModel();
//        logger.info("Building binary Spoon model for {} took {}ms", this, sw.elapsed().toMillis());
        return spoonModel;
    }

    public List<String> buildClasspath() {
        Stopwatch sw = Stopwatch.createStarted();
        Path pom = extractPomFromJar();

        List<String> cp =
                pom != null
                        ? buildClasspathFromPom(pom)
                        : Collections.emptyList();

        if (pom != null)
//            logger.info("Extracting classpath from {} [{} entries] took {}ms", pom, cp.size(), sw.elapsed().toMillis());
            System.out.println("Extracting classpath from {} [{} entries] took {}ms" + pom + cp.size() + sw.elapsed().toMillis());
        return cp;
    }

    private List<String> buildClasspathFromPom(Path pom) {
        System.out.println("Building classpath from " + pom);
        try {
            if (pom.toFile().exists()) {
                SpoonPom spoonPom = new SpoonPom(
                        pom.toAbsolutePath().toString(),
                        MavenLauncher.SOURCE_TYPE.APP_SOURCE,
                        null
                );

                return Arrays.asList(spoonPom.buildClassPath(
                        null, MavenLauncher.SOURCE_TYPE.APP_SOURCE, null, true
                ));
            }
        } catch (IOException | XmlPullParserException e) {
//            logger.error(e);
            System.out.println(e);
        }

        return Collections.emptyList();
    }

    private static final Path TMP_DIR;

    static {
        Path tmp = Path.of(".");
        try {
            tmp = Files.createTempDirectory("maracas-pom-tmp");
        } catch (IOException e) {

        }
        TMP_DIR = tmp;
    }

    private Path extractPomFromJar() {
        try (JarFile jarFile = new JarFile(file.toFile())) {
            List<JarEntry> poms = jarFile.stream().filter(e -> e.getName().endsWith("pom.xml")).toList();

            if (poms.size() == 1) {
                JarEntry pom = poms.get(0);
                InputStream pomStream = jarFile.getInputStream(pom);

                Path out = Files.createTempFile(TMP_DIR, "pom", ".xml");
                Files.copy(pomStream, out, StandardCopyOption.REPLACE_EXISTING);
                return out;
            } else
//                logger.warn("Found {} pom.xml files in {}, no classpath inferred", poms.size(), label);
                System.out.println("Found {} pom.xml files in {}, no classpath inferred" + poms.size() + file);
        } catch (IOException e) {
//            logger.error(e);
            System.out.println(e.getStackTrace());
        }

        return null;
    }
}

