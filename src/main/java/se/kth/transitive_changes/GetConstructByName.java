package se.kth.transitive_changes;

import se.kth.log_Analyzer.ErrorInfo;
import se.kth.log_Analyzer.MavenErrorLog;
import se.kth.log_Analyzer.MavenLogAnalyzer;
import se.kth.spoon_compare.Client;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetConstructByName {

    MavenErrorLog mavenErrorLog;
    Client clientProject;
    Client sourceClient;
    static CtModel clientModel;
    CtModel sourceModel;

    public GetConstructByName(MavenErrorLog mavenErrorLog, Client clientProject) {
        this.mavenErrorLog = mavenErrorLog;
        this.clientProject = clientProject;
    }


    public static void main(String[] args) throws IOException {
        MavenLogAnalyzer mavenLogAnalyzer = new MavenLogAnalyzer(new File("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f/code-coverage-api-plugin/8ab7a7214f9ac1d130b416fae7280cfda533a54f.log"));
        MavenErrorLog mavenErrorLog = mavenLogAnalyzer.analyzeCompilationErrors();

        Map<String, Map<Integer, String>> lines = extractLineNumbersWithPaths("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f/code-coverage-api-plugin/8ab7a7214f9ac1d130b416fae7280cfda533a54f.log");

        Client clientProject = new Client(Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f"));
        clientModel = clientProject.createModel();

        Path projectPath = Path.of("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/projects/8ab7a7214f9ac1d130b416fae7280cfda533a54f");

        Set<String> spoonedElements = new HashSet<>();
        Set<String> allCtElements = new HashSet<>();
        Map<String, String> elementLines = new HashMap<>();
        Map<String, String> elementPatterns = new HashMap<>();

        for (Map.Entry<String, Map<Integer, String>> entry : lines.entrySet()) {
            List<Object> spoonResult = applySpoon(projectPath + entry.getKey(), entry.getValue(),
                    "hamcrest-core".replace("-", "."));
            spoonedElements.addAll((Collection<? extends String>) spoonResult.get(0));
            allCtElements.addAll((Collection<? extends String>) spoonResult.get(1));
            elementLines.putAll((Map<? extends String, ? extends String>) spoonResult.get(2));
            elementPatterns.putAll((Map<? extends String, ? extends String>) spoonResult.get(3));
        }


        Launcher launcher = new Launcher();
        launcher.addInputResource("/Users/frank/Documents/Work/PHD/Explaining/breaking-good/source/htmlunit-2.70.0-sources");
//        List<Path> path = List.of(Path.of("/Users/frank/Downloads/tmp/asto-v1.10.0-sources.jar"));
//        String[] cp2 = path.stream().map(p -> p.toAbsolutePath().toString()).toList().toArray(new String[0]);
//        launcher.getEnvironment().setSourceClasspath(cp2);
        launcher.buildModel();


        launcher.getModel().getRootPackage().getFactory().CompilationUnit().getMap().forEach((k, v) -> {
//            System.out.println(k);
//            if(k.contains("ScriptResult")) {
//                    System.out.println(v);
            System.out.println(v.getPackageDeclaration());
//            }

        });


        CtType<?> clazz = launcher.getModel().getAllTypes().iterator().next();
        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {
            if (!e.isImplicit() && e.getPosition().isValidPosition()) {
                if (e instanceof CtInvocation<?>) {
//                    System.out.println((String.valueOf(((CtInvocation<?>) e).getExecutable())));
                }
                if (e instanceof CtConstructorCall<?>) {
//                    System.out.println((String.valueOf(((CtConstructorCall<?>) e).getExecutable())));
                }
            }
        }

        for (String element : spoonedElements) {
            System.out.println(element);
        }


//        GetConstructByName getConstructByName = new GetConstructByName(mavenErrorLog, clientProject);
//        getConstructByName.extract();
    }

    private static List<Object> applySpoon(String projectFilePath, Map<Integer, String> lineNumbers, String depGrpID) {
        Launcher spoon = new Launcher();
        spoon.addInputResource(projectFilePath);
        List<Path> path = List.of(Path.of("/Users/frank/Downloads/tmp/asto-v1.10.0-sources.jar"));
        String[] cp = path.stream().map(p -> p.toAbsolutePath().toString()).toList().toArray(new String[0]);
        spoon.getEnvironment().setSourceClasspath(cp);
        spoon.buildModel();


        return getElementFromSourcePosition(spoon.getModel(), lineNumbers, depGrpID);
    }


    private static Map<String, Map<Integer, String>> extractLineNumbersWithPaths(String logFilePath) {
        Map<String, Map<Integer, String>> lineNumbersWithPaths = new HashMap<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(logFilePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            String currentPath = null;
            Pattern errorPattern = Pattern.compile("\\[ERROR\\] .*:\\[(\\d+),\\d+\\]");
            Pattern pathPattern = Pattern.compile("/[^:/]+(/[^\\[\\]:]+)");

            while ((line = reader.readLine()) != null) {
                Map<Integer, String> lines = new HashMap<>();
                Matcher matcher = errorPattern.matcher(line);
                if (matcher.find()) {
                    Integer lineNumber = Integer.valueOf(matcher.group(1));
                    Matcher pathMatcher = pathPattern.matcher(line);
                    lines.put(lineNumber, line);
                    if (pathMatcher.find()) {
                        currentPath = pathMatcher.group();
                    }
                    if (currentPath != null) {
                        if (lineNumbersWithPaths.containsKey(currentPath))
                            lineNumbersWithPaths.get(currentPath).putAll(lines);
                        else {
                            lineNumbersWithPaths.put(currentPath, lines);
                        }
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineNumbersWithPaths;
    }

    public void extract() {
        Map<Integer, String> startLines = new HashMap<>();
        for (Map.Entry<String, Set<ErrorInfo>> entry : mavenErrorLog.getErrorInfo().entrySet()) {
            for (ErrorInfo errorInfo : entry.getValue()) {
                startLines.put(Integer.parseInt(errorInfo.getClientLinePosition()), errorInfo.getErrorMessage());
            }
        }
        List<Object> elements = getElementFromSourcePosition(clientModel, startLines, "com.gargoylesoftware.htmlunit.ScriptResult");
        Set<String> elementStrings = (Set<String>) elements.get(0);
        Set<String> elements2 = (Set<String>) elements.get(1);
        Map<String, String> elementLines = (Map<String, String>) elements.get(2);
        Map<String, String> elementPatterns = (Map<String, String>) elements.get(3);


        for (String element : elementStrings) {
            System.out.println(element);
        }
    }


    private static List<Object> getElementFromSourcePosition(CtModel model, Map<Integer, String> startLines, String depGrpId) {
        Set<String> elements = new HashSet<>();
        Set<String> elementStrings = new HashSet<>();
        Map<String, String> elementLines = new HashMap<>();
        Map<String, String> elementPatterns = new HashMap<>();
        CtType<?> clazz = model.getAllTypes().iterator().next();
        for (CtElement e : clazz.getElements(new TypeFilter<>(CtElement.class))) {
            if (!e.isImplicit() && e.getPosition().isValidPosition() && startLines.containsKey(e.getPosition().getLine())) {
                if (e instanceof CtInvocation<?>) {
                    elements.add(String.valueOf(((CtInvocation<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtInvocation<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                    }
                }
                if (e instanceof CtConstructorCall<?>) {
                    elements.add(String.valueOf(((CtConstructorCall<?>) e).getExecutable()));
                    String parsedElement = parseProject(((CtConstructorCall<?>) e).getExecutable(), depGrpId);
                    if (parsedElement != null) {
                        elementStrings.add(parsedElement);
                        elementLines.put(parsedElement, startLines.get(e.getPosition().getLine()));
                        elementPatterns.put(parsedElement, replacePatterns(startLines.get(e.getPosition().getLine())));
                    }
                }
            }
        }
        // This is not good coding. Just adding the outputs to a list this way to quickly get the results,
        // without worrying about creating classes.
        return new ArrayList<>(List.of(elementStrings, elements, elementLines, elementPatterns));
    }

    private static String parseProject(CtElement e, String dependencyGrpID) {
        CtElement parent = e.getParent(new TypeFilter<>(CtClass.class));
        while (parent != null) {
            if (String.valueOf(parent).contains(dependencyGrpID)) {
                int openingParenthesisIndex = String.valueOf(e).indexOf('(');
                if (openingParenthesisIndex != -1) {
                    return String.valueOf(e).substring(0, openingParenthesisIndex);
                }
                return String.valueOf(e);
            }
            parent = parent.getParent(new TypeFilter<>(CtClass.class));
        }
        return null;
    }


    public static String replacePatterns(String line) {
        Map<String, String> patternMap = new HashMap<>();
        patternMap.put("package \\S+ does not exist", "package does not exist");
        patternMap.put("cannot access \\S+", "cannot access");
        patternMap.put("incompatible types: [^\\n]+ cannot be converted to", "incompatible types: cannot be converted to");
        patternMap.put("incompatible types: [^\\n]+ is not a functional interface", "incompatible types: is not a functional interface");
        patternMap.put("method [^\\n]+ cannot be applied to given types", "method cannot be applied to given types");
        patternMap.put("constructor \\S+ in class \\S+ cannot be applied to given types", "constructor in class cannot be applied to given types");
        patternMap.put("[^\\n]+ has private access in", "has private access in");
        patternMap.put("no suitable constructor found for", "no suitable constructor found for");
        patternMap.put("no suitable method found for", "no suitable method found for");
        patternMap.put("is not abstract and does not override abstract method", "is not abstract and does not override abstract method");
        patternMap.put("unreported exception \\S+ must be caught or declared to be thrown", "unreported exception must be caught or declared to be thrown");
        patternMap.put("incompatible types: bad return type in lambda expression", "incompatible types: bad return type in lambda expression");
        patternMap.put("cannot find symbol", "cannot find symbol");
        patternMap.put("method does not override or implement a method from a supertype", "method does not override or implement a method from a supertype");
        patternMap.put("reference to \\S+ is ambiguous", "reference to is ambiguous");
        patternMap.put("static import only from classes and interfaces", "static import only from classes and interfaces");
        patternMap.put("exception \\S+ is never thrown in body of corresponding try statement", "exception is never thrown in body of corresponding try statement");
        patternMap.put("Couldn't retrieve \\S+ annotation", "Couldn't retrieve annotation");

        for (Map.Entry<String, String> entry : patternMap.entrySet()) {
            String patternToRemove = entry.getKey();
            String patternToSub = entry.getValue();
            if (line.matches(".*" + patternToRemove + ".*")) {
                return patternToSub;
            }
        }
        return line;
    }

}
