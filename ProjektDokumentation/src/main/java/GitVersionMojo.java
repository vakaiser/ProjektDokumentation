import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mojo(name = "version", defaultPhase = LifecyclePhase.INITIALIZE)
public class GitVersionMojo extends AbstractMojo {

    //@Parameter(property = "project", readonly = true)
    //private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        //getLog().info("Cool, a maven plugin!");
        //System.out.println("asdf");


        File file = new File("..\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test.java");
        File file2 = new File("..\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test2.java");
        List<File> files = new ArrayList<>();
        files.add(file);
        files.add(file2);

        //File md = new File("..\\ProjektDokumentation\\README.md");
        File md2 = new File("..\\ProjektDokumentation\\README2.md");

        //List<String> temp = Arrays.stream(file.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
        //temp.stream().forEach(System.out::println);



        //System.out.println(file.getParentFile());
        //System.out.println(file.getAbsolutePath());
        List<CodeSnippet> snippets = findSnippetsBeta(files);
        snippets.forEach(System.out::println);
        //main.generateEnrichedMd(md, snippets);
        generateNewReadMe(md2, snippets);
        //System.out.println("AAAAAASDFASDFSADFASDFASDFASDFDS");
    }



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
            System.out.println(lines.size());

            //String item = "";

            for (Map.Entry<String, List<String>> entry : pathLines.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());

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

            System.out.println(content); //test stuff

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private void generateNewReadMe(File md, List<CodeSnippet> snippets) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(md));
            //BufferedWriter bw = new BufferedWriter(new FileWriter("README2.md"));

            List<String> lines = br.lines().collect(Collectors.toList());

            String line = "";
            boolean ignore = false;
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line.toLowerCase().contains("prodoc") && line.toLowerCase().contains("!")) {
                    //line = line.replace("prodoc", "java");

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
                else if (line.toLowerCase().contains("start doc")) {
                    ignore = true;
                    continue;
                }
                else if (line.toLowerCase().contains("end doc")) {
                    ignore = false;
                    continue;
                }

                if (!ignore) {
                    //bw.write(line);
                    result += line;

                    //bw.newLine();
                    result += "\n";
                }

                //bw.flush();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(md.getName()));
            //System.out.println("AAAAAASDFASDFSADFASDFASDFASDFDS");
            bw.write(result);
            bw.flush();
            bw.close();
        } catch (Exception e) {
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
                    for (CodeSnippet snippet : snippets) {
                        if (snippet.getId().toLowerCase().equals(line.toLowerCase())) {
                            line = snippet.getContent();
                        }

                    }

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
        File file = new File("..\\ProjektDokumentation\\src\\main\\java\\Test.java");
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
}