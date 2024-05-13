package se.kth.werror;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WError {

    private final String failOnWarning = "failOnWarning";
    private final String wError = "Werror";


    public NodeList parsePOM(File pom) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(pom.getAbsolutePath());

        // get the root element
        NodeList dependencies = doc.getElementsByTagName(failOnWarning);

        return dependencies;


    }

    private List<File> findPomFiles(File file) {
        List<File> pomFiles = new ArrayList<>();

        List<File> search = List.of(Objects.requireNonNull(file.listFiles()));

        for (File f : search) {
            if (f.isDirectory()) {
                pomFiles.addAll(findPomFiles(f));
            } else {
                if (f.getName().equals("pom.xml")) {
                    pomFiles.add(f);
                }
            }
        }

        return pomFiles;
    }


    public void findWerror(File file) {
        List<File> pomFiles = findPomFiles(file);

        for (File pom : pomFiles) {
            try {
                NodeList a = parsePOM(pom);
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }
    }


}
