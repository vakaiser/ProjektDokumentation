import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mojo(name = "markdocuments", defaultPhase = LifecyclePhase.INSTALL)
public class GitVersionMojo extends AbstractMojo {

    List<File> mdFiles = new ArrayList<>();
    List<File> classFiles = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getAllNecessaryFiles();
        generateInitialScore();

        List<CodeSnippet> snippets = findSnippets(classFiles);
        for (File obj : mdFiles)
        {
            generateNewReadMe(obj, snippets);
        }
    }
    private void generateInitialScore() {
        try {
            BufferedWriter bwForCsv = new BufferedWriter(new FileWriter("src\\main\\resources\\Score.csv"));
            bwForCsv.write("Markdown;Prodoc;Score");
            bwForCsv.flush();
            bwForCsv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllNecessaryFiles() {
        String dir = System.getProperty("user.dir");
        Collection files = FileUtils.listFiles(new File(dir), null, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file.getName().endsWith(".java"))
                classFiles.add(file);

            if (file.getName().endsWith(".md") || file.getName().endsWith(".markdown"))
                mdFiles.add(file);
        }
        mdFiles = mdFiles.stream().filter(distinctByKey( File::getName)).sorted().collect(Collectors.toList());
    }

    public List<CodeSnippet> findSnippets(List<File> files) {
        List<CodeSnippet> result = new ArrayList<>();

        try {
            BufferedReader br;
            List<String> lines = new ArrayList<>();
            Map<String, List<String>> pathLines = new TreeMap<>();
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                lines = br.lines().collect(Collectors.toList());
                List<String> temp = Arrays.stream(file.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
                String tempString = temp.get(temp.size() - 4) + "."
                        + temp.get(temp.size() - 3) + "."
                        + temp.get(temp.size() - 2)
                        + "." + temp.get(temp.size() - 1).replace(".java", "");
                pathLines.put(tempString, lines);
            }
            boolean marked = false;
            String content = "";
            String id = "";
            for (Map.Entry<String, List<String>> entry : pathLines.entrySet()) {
                List<String> temp = entry.getValue();
                for (String item : temp) {
                    //item = entry.getKey();
                    if (item.toLowerCase().contains("@enddoc")) {
                        marked = false;
                        result.add(new CodeSnippet(id, content));
                        content = "";
                        id = "";
                    } else if (item.toLowerCase().contains("@prodoc")) {
                        marked = true;
                        item = item.replace(" ", "")
                                .replace("*", "")
                                .replace("/", "");
                        id = entry.getKey() + "." + item.split(":")[1];
                    } else if (marked) {
                        content += item.replace("  ", "") + "\n";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void generateNewReadMe(File md, List<CodeSnippet> snippets) {
        List<String> oldCode = new ArrayList<>();
        List<String> newCode = new ArrayList<>();
        String name = "";
        String result = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(md));
            List<String> lines = br.lines().collect(Collectors.toList());
            String line = "";
            boolean ignore = false;
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);

                //Write new Code Snippet
                if (line.toLowerCase().contains("prodoc") && line.toLowerCase().contains("!") && !line.startsWith("\\")) {
                    String[] nameArr = line.split("prodoc");
                    name = nameArr[1].split("-->")[0].trim();
                    result += line;
                    result += "\n";
                    result += "<!---start doc -->";
                    result += "\n";
                    result += "```java";

                    for (CodeSnippet snippet : snippets) {
                        if (line.toLowerCase().contains(snippet.getId().toLowerCase())) {
                            result += "\n";
                            result += snippet.getContent();
                            String[] arr = snippet.getContent().split("\n");
                            Arrays.stream(arr).forEach(x -> newCode.add(x));
                        }
                    }
                    result += "```";
                    result += "\n";
                    result += "<!---end doc -->";
                    result += "\n";
                    continue;
                }
                //Look if there was any old doc snippets
                else if (line.toLowerCase().contains("start doc")) {
                    ignore = true; //ignore => ignore until the code snippets isn't present anymore
                    continue;
                }
                else if (line.toLowerCase().contains("end doc")) {
                    ignore = false;
                    collectDifferences(oldCode, newCode, md.getName(), name);
                    oldCode.clear();
                    newCode.clear();
                    continue;
                }

                //if it isn't an old code block write it down
                if (!ignore) {
                    result += line;
                    result += "\n";
                }
                else if (ignore && !line.contains("```")) {
                    oldCode.add(line);
                }
            }

            //Write new MD
            BufferedWriter bw = new BufferedWriter(new FileWriter(md.getName()));
            bw.write(result);
            bw.flush();

            bw = new BufferedWriter(new FileWriter("src\\main\\resources\\"+md.getName()));
            bw.write(result);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void collectDifferences(List<String> oldCode, List<String> newCode, String mdName, String methodName) {
        int solution = 0;
        String csvLine = "";
        if (!oldCode.equals(newCode)) {
            if (oldCode.size() < newCode.size()) {
                List<String> temp = oldCode;
                temp.retainAll(newCode);
                solution = 100 - ((temp.size() * 100) / newCode.size());

            } else {
                List<String> temp = newCode;
                temp.retainAll(oldCode);
                solution = 100 - ((temp.size() * 100) / oldCode.size());
            }
            int num = -1;
            List<String> newDif = newCode;
            newDif.retainAll(oldCode);
            List<String> oldDif = oldCode;
            oldDif.retainAll(newCode);

            if (newDif.size() > oldDif.size())
                num = oldDif.size();
            else
                num = newDif.size();

            for (int i = 0; i < num; i++) {
                List<String> splittedNew = Arrays.stream(newCode.get(i).split(" ")).collect(Collectors.toList());
                List<String> splittedOld = Arrays.stream(oldCode.get(i).split(" ")).collect(Collectors.toList());

                if (splittedOld.size() < splittedNew.size()) {
                    List<String> tempWord =  splittedOld;
                    tempWord.retainAll(splittedNew);
                    solution -= (tempWord.size()*num) / splittedNew.size();
                }
                else {
                    List<String> tempWord =  splittedNew;
                    tempWord.retainAll(splittedOld);
                    solution -= (tempWord.size()*num) / splittedOld.size();
                }
            }
        }
        if (solution >= 50) {
            csvLine = mdName + ";" + methodName + ";" + solution + "";
            generateCsvForDifferences(csvLine);
        }
    }

    private void generateCsvForDifferences(String line) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("src\\main\\resources\\Score.csv", true));
            bw.append("\n"+line);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}