import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String args[]) {
        System.out.println("asdf");

        Main main = new Main();

        File file1 = new File("..\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test.java");
        File file2 = new File("..\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test2.java");
        //List<File> files = new ArrayList<>();
        //files.add(file1);
        //files.add(file2);

        //File md = new File("..\\ProjektDokumentation\\README.md");
        //File md2 = new File("..\\ProjektDokumentation\\README2.md");

        //Get all MD files
        /*List<File> allFiles = getResourceFolderFiles("");
        List<File> mdFiles = new ArrayList<>();
        List<File> javaFiles = new ArrayList<>();
        for (Iterator iterator = allFiles.iterator(); iterator.hasNext(); ) {
            File file = (File) iterator.next();
            if (file.getName().endsWith(".md") || file.getName().endsWith(".markdown"))
                mdFiles.add(file);
        }*/



        //Get all Java files
        String dir = System.getProperty("user.dir");
        Collection files = FileUtils.listFiles(new File(dir), null, true);
        List<File> classFiles = new ArrayList<>();
        List<File> mdFiles = new ArrayList<>();
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file.getName().endsWith(".java") && !file.getName().toLowerCase().contains("main") && !file.getName().toLowerCase().contains("codesnippet") && !file.getName().toLowerCase().contains("gitversionmojo"))
                classFiles.add(file);

            if (file.getName().endsWith(".md") || file.getName().endsWith(".markdown"))
                mdFiles.add(file);
        }

        mdFiles = mdFiles.stream().filter(distinctByKey( File::getName)).sorted().collect(Collectors.toList());

        //List<String> temp = Arrays.stream(file1.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
        //temp.stream().forEach(System.out::println);



        //System.out.println(file.getParentFile());
        //System.out.println(file1.getAbsolutePath());
        List<CodeSnippet> snippets = main.findSnippetsBeta(classFiles);
        //snippets.forEach(System.out::println);
        //main.generateEnrichedMd(md, snippets);
        //main.generateNewReadMe(md2, snippets);

        try {
            BufferedWriter bwForCsv = new BufferedWriter(new FileWriter("src\\main\\resources\\Score.csv"));
            bwForCsv.write("Markdown;Prodoc;Score");
            bwForCsv.flush();
            bwForCsv.close();
            System.out.println("amogus");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File obj : mdFiles)
        {
            main.generateNewReadMe(obj, snippets);
        }
    }

    /*
        public List<CodeSnippet> findSnippets(List<File> files) {
            List<CodeSnippet> result = new ArrayList<>();

            try {
                BufferedReader br;
                List<String> lines = new ArrayList<>();
                List<String> filePathEndings = new ArrayList<>();
                Map<String, List<String>> pathLines = new TreeMap<>();
                for (File file: files) {
                     br = new BufferedReader(new FileReader(file));
                     lines.addAll(br.lines().collect(Collectors.toList()));
                     List<String> testo = br.lines().collect(Collectors.toList());
                     List<String> temp = Arrays.stream(file.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
                     String tempString = temp.get(temp.size()-3) + "." + temp.get(temp.size()-2) + "." + temp.get(temp.size()-1) + "." + temp.get(temp.size());
                     filePathEndings.add(tempString);
                     pathLines.put(tempString, testo);
                }
                //List<String> lines = br.lines().collect(Collectors.toList());

                boolean marked = false;
                String content = "";
                String id = "";
                System.out.println(lines.size());

                String line = "";

                for (int i = 0; i < lines.size(); i++) {
                    line = lines.get(i);
                    if (line.toLowerCase().contains("@prodoc")) {
                        if (line.contains("end")) {
                            marked = false;
                            result.add(new CodeSnippet(id, content));
                            content = "";
                            id = "";
                        } else {
                            marked = true;
                            line = line.replace(" ", "")
                                  .replace("*", "")
                                  .replace("/", "");
                            id = line.split(":")[1];
                        }
                    } else if (marked) {
                        content += line.replace("  ", "") + "\n";
                    }
                }

                System.out.println(content); //test stuff

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    */
    public List<CodeSnippet> findSnippetsBeta(List<File> files) {
        List<CodeSnippet> result = new ArrayList<>();

        try {
            BufferedReader br;
            List<String> lines = new ArrayList<>();
            //List<String> filePathEndings = new ArrayList<>();
            Map<String, List<String>> pathLines = new TreeMap<>();
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                lines = br.lines().collect(Collectors.toList());
                List<String> temp = Arrays.stream(file.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
                String tempString = temp.get(temp.size() - 4) + "." + temp.get(temp.size() - 3) + "." + temp.get(temp.size() - 2) + "." + temp.get(temp.size() - 1).replace(".java", "");
                //filePathEndings.add(tempString);
                pathLines.put(tempString, lines);
            }
            //List<String> lines = br.lines().collect(Collectors.toList());

            boolean marked = false;
            String content = "";
            String id = "";
            //System.out.println(lines.size());

            //String item = "";

            for (Map.Entry<String, List<String>> entry : pathLines.entrySet()) {
                //System.out.println(entry.getKey() + ":" + entry.getValue());

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

            /*for (int j = 0; j < lines.size(); j++) {
                line = lines.get(j);
                if (line.toLowerCase().contains("@prodoc")) {
                    if (line.contains("end")) {
                        marked = false;
                        result.add(new CodeSnippet(id, content));
                        content = "";
                        id = "";
                    } else {
                        marked = true;
                        line = line.replace(" ", "")
                                .replace("*", "")
                                .replace("/", "");
                        id = line.split(":")[1];
                    }
                } else if (marked) {
                    content += line.replace("  ", "") + "\n";
                }
            }*/

            //System.out.println(content); //test stuff

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void generateNewReadMe(File md, List<CodeSnippet> snippets) {
        List<String> oldCode = new ArrayList<>();
        List<String> newCode = new ArrayList<>();
        String name = "";
        //String collectedLines = "";

        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(md));
            //BufferedWriter bw = new BufferedWriter(new FileWriter("README2.md"));

            List<String> lines = br.lines().collect(Collectors.toList());

            String line = "";
            boolean ignore = false;
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);

                //Write new Code Snippet
                if (line.toLowerCase().contains("prodoc") && line.toLowerCase().contains("!")) {
                    //line = line.replace("prodoc", "java");
                    //<!---prodoc at.diggah.lost.Test.ichwillesso -->
                    String[] nameArr = line.split("prodoc");
                    name = nameArr[1].split("-->")[0].trim();

                    //bw.write(line);
                    result += line;

                    //bw.newLine();
                    result += "\n";

                    //bw.write("<!---start doc -->");
                    result += "<!---start doc -->";


                    result += "\n";
                    result += "```java";

                    for (CodeSnippet snippet : snippets) {
                        if (line.toLowerCase().contains(snippet.getId().toLowerCase())) {
                            //bw.newLine();
                            result += "\n";

                            //bw.write(snippet.getContent());
                            result += snippet.getContent();
                            //line = snippet.getContent();
                            String[] arr = snippet.getContent().split("\n");
                            Arrays.stream(arr).forEach(x -> newCode.add(x));
                            //newCode.add(snippet.getContent());
                        }
                    }
                    result += "```";
                    result += "\n";

                    //bw.write("<!---end doc -->");
                    result += "<!---end doc -->";

                    //bw.newLine();
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

                    /*//SOSIG GAMING
                    if (!oldCode.equals(newCode)) {
                        newCode.forEach(x -> System.out.println(x + " NEW CODE"));
                        System.out.println(newCode.size() + " NEW");

                        oldCode.forEach(x -> System.out.println(x + " OLD CODE"));
                        System.out.println(oldCode.size() + " OLD");

                        List<String> temp =  newCode;
                        temp.retainAll(oldCode);

                        System.out.println(temp.size());

                        System.out.println();
                        int solution = 100 - ((temp.size()*100) / oldCode.size());

                        System.out.println(solution%);

                        //newMethod(solution, MD-Name, prodoc-name); => generates file that shows differences
                    }*/
                    collectDifferences(oldCode, newCode, md.getName(), name);

                    oldCode.clear();
                    newCode.clear();

                    continue;
                }

                //if it isn't an old code block write it down
                if (!ignore) {
                    //bw.write(line);
                    result += line;

                    //bw.newLine();
                    result += "\n";
                }
                else if (ignore && !line.contains("```")) {
                    oldCode.add(line);
                }

                //bw.flush();
            }

            //Write new MD
            BufferedWriter bw = new BufferedWriter(new FileWriter(md.getName()));
            bw.write(result);
            bw.flush();

            bw = new BufferedWriter(new FileWriter("src\\main\\resources\\"+md.getName()));
            bw.write(result);
            bw.flush();
            bw.close();
            //generateCsvForDifferences(collectedLines);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void collectDifferences(List<String> oldCode, List<String> newCode, String mdName, String methodName) {
        int solution = 0;
        String csvLine = "";
        if (!oldCode.equals(newCode)) {
            newCode.forEach(x -> System.out.println(x + " NEW CODE"));
            System.out.println(newCode.size() + " NEW");

            oldCode.forEach(x -> System.out.println(x + " OLD CODE"));
            System.out.println(oldCode.size() + " OLD");

            if (oldCode.size() < newCode.size()) {
                List<String> temp =  oldCode;
                temp.retainAll(newCode);

                System.out.println(temp.size());

                System.out.println();
                solution = 100 - ((temp.size()*100) / newCode.size());

                System.out.println(solution+"%");
                System.out.println();
                System.out.println();

            }
            else {
                List<String> temp =  newCode;
                temp.retainAll(oldCode);

                System.out.println(temp.size());

                System.out.println();
                solution = 100 - ((temp.size()*100) / oldCode.size());

                System.out.println(solution+"%");
                System.out.println();
                System.out.println();
            }
        }

        if (solution >= 50) {
            csvLine = mdName+";"+methodName+";"+solution+"";
            System.out.println(csvLine);
            //result =  collectedLines+"\n"+csvLine;
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

    private void generateEnrichedMd(File md, List<CodeSnippet> snippets) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(md));
            BufferedWriter bw = new BufferedWriter(new FileWriter("EnrichedMd.md"));

            List<String> lines = br.lines().collect(Collectors.toList());

            String line = "";
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line.toLowerCase().contains("prodoc")) {
                    //line = line.replace(" ", "");
                    //String id = line.split(":")[1];
                    line = line.replace("prodoc", "java");
                    /*for (CodeSnippet snippet : snippets) {
                        if (snippet.getId().toLowerCase().equals(line.toLowerCase())) {
                            line = snippet.getContent();
                        }

                    }*/

                }

                //if (line.contains("@")) {
                    for (CodeSnippet snippet : snippets) {
                        if (snippet.getId().toLowerCase().equals(line.toLowerCase())) {
                            line = snippet.getContent();
                        }
                    }
                //}


                bw.write(line);
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void placeHolder() {
        File file = new File("..\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\Test.java");
        File md = new File("..\\ProjektDokumentation\\README.md");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            int counter = 0;
            //br.readLine();
            boolean marked = false;
            String code = "";
            List<String> lines = br.lines().collect(Collectors.toList());
            //System.out.println(lines.size());

            String line = "";

            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line.contains("@Prodoc")) {
                    if (line.contains("end")) marked = false;
                    else {
                        marked = true;
                        code = line.split(" ")[1];
                    }
                } else if (marked) {
                    code += line.replace("  ", "") + "\n";
                }
            }

            //System.out.println(code);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<File> getResourceFolderFiles (String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return Arrays.stream(new File(path).listFiles()).collect(Collectors.toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
