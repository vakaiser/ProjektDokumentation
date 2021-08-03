import com.sun.org.apache.bcel.internal.classfile.Code;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String args[]) {
        System.out.println("asdf");

        Main main = new Main();

        File file = new File("..\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test.java");
        File file2 = new File("..\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\at\\diggah\\lost\\Test2.java");
        List<File> files = new ArrayList<>();
        files.add(file);
        files.add(file2);

        File md = new File("..\\ProjektDokumentation\\README.md");

        List<String> temp = Arrays.stream(file.getAbsolutePath().split(Pattern.quote(File.separator))).collect(Collectors.toList());
        temp.stream().forEach(System.out::println);

        //System.out.println(file.getParentFile());
        System.out.println(file.getAbsolutePath());
        List<CodeSnippet> snippets = main.findSnippetsBeta(files);
        snippets.forEach(System.out::println);
        main.generateEnrichedMd(md, snippets);
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
}
