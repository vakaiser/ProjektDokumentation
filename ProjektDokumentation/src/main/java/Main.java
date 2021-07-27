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

        File file = new File("C:\\Users\\efsun\\OneDrive\\POS\\_projekt\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\Test.java");
        File md = new File("C:\\Users\\efsun\\OneDrive\\POS\\_projekt\\ProjektDokumentation\\README.md");

        main.findSnippets(file);
    }

    public List<CodeSnippet> findSnippets(File file) {
        List<CodeSnippet> result = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            boolean marked = false;
            String content = ""; //test stuff delete later
            String id = "";
            List<String> lines = br.lines().collect(Collectors.toList());
            System.out.println(lines.size());

            String line = "";

            for (int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                if (line.toLowerCase().contains("@prodoc")) {
                    if (line.contains("end")) {
                        marked = false;
                        result.add(new CodeSnippet(id, content));
                    } else {
                        marked = true;
                        id = line.split(" ")[1];
                    }
                } else if (marked) {
                    content += line.replace("  ", "") + "\n";  //delete later
                }
            }

            System.out.println(content);

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
                    String replace = line.split(" ")[2];
                    line.replace(replace, "sus");
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
        File file = new File("C:\\Users\\efsun\\OneDrive\\POS\\_projekt\\ProjektDokumentation\\ProjektDokumentation\\src\\main\\java\\Test.java");
        File md = new File("C:\\Users\\efsun\\OneDrive\\POS\\_projekt\\ProjektDokumentation\\README.md");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            int counter = 0;
            //br.readLine();
            boolean marked = false;
            String code = "";
            List<String> lines = br.lines().collect(Collectors.toList());
            System.out.println(lines.size());

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

            System.out.println(code);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
