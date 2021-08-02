import com.sun.org.apache.bcel.internal.classfile.Code;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Main {

    public static void main(String args[]) {
        System.out.println("asdf");

        Main main = new Main();

        File file = new File("..\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\Test.java");
        File file2 = new File("..\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\Test2.java");
        List<File> files = new ArrayList<>();
        files.add(file);
        files.add(file2);

        File md = new File("..\\ProjektDokumentation\\README.md");

        List<CodeSnippet> snippets = main.findSnippets(files);
        snippets.forEach(System.out::println);
        main.generateEnrichedMd(md, snippets);
    }

    public List<CodeSnippet> findSnippets(List<File> files) {
        List<CodeSnippet> result = new ArrayList<>();

        try {
            BufferedReader br;
            List<String> lines = new ArrayList<>();
            for (File file: files) {
                 br = new BufferedReader(new FileReader(file));
                 lines.addAll(br.lines().collect(Collectors.toList()));
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

    private void generateEnrichedMd(File md, List<CodeSnippet> snippets) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(md));
            BufferedWriter bw = new BufferedWriter(new FileWriter("EnrichedMd.md"));

            List<String> lines = br.lines().collect(Collectors.toList());

            String line = "";
            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line.toLowerCase().contains("prodoc")) {
                    line = line.replace(" ", "");
                    String id = line.split(":")[1];

                    for (CodeSnippet snippet : snippets) {
                        if (snippet.getId().toLowerCase().equals(id.toLowerCase())) line = "java\n"+ snippet.getContent();

                    }

                    /*line = snippets.stream().map(x -> x.getId())
                    .filter(x -> x.contains(id))
                    .findFirst()
                    .toString();*/
                    //System.out.println(line);
                    //line.replace(replace, "sus");
                }
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
