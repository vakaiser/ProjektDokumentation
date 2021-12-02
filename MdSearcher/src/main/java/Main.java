
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedWriter bw;
            List<File> allFiles = getResourceFolderFiles("");
            List<File> mdFiles = new ArrayList<>();
            for (Iterator iterator = allFiles.iterator(); iterator.hasNext(); ) {
                File file = (File) iterator.next();
                if (file.getName().endsWith(".md") || file.getName().endsWith(".markdown"))
                    mdFiles.add(file);
              }
            for (File f : mdFiles) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../HTMLSiteGeneration/src/site/markdown/" + f.getName())));
                String line = br.readLine();
                while (line != null) {
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                    line = br.readLine();
                }

            }
            } catch(IOException e){
                e.printStackTrace();
            }

        }

    private static List<File> getResourceFolderFiles (String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return Arrays.stream(new File(path).listFiles()).toList();
    }

//    public static void main(String[] args){
//        try {
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../HTMLSiteGeneration/src/site/markdown/MasterMd.md")));
//            BufferedReader projects = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResources("OneClassOfProject.txt").nextElement().openStream()));
//            String project = projects.readLine();
//            while(project != null) {
//                Class c = Class.forName(project);
//                String path = c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace("/target/classes/", "");
//                boolean recursive = true;
//                Collection files = FileUtils.listFiles(new File(path), null, recursive);
//                List<File> mdFiles = new ArrayList<>();
//                for (Iterator iterator = files.iterator(); iterator.hasNext();) {
//                    File file = (File) iterator.next();
//                    if (file.getName().endsWith(".md") || file.getName().endsWith(".markdown"))
//                        mdFiles.add(file);
//
//                }
//                mdFiles = mdFiles.stream().filter(distinctByKey( File::getName)).sorted().toList();
//                for (File f: mdFiles) {
//                    BufferedReader br = new BufferedReader(new FileReader(f));
//                    String line = br.readLine();
//                    while (line != null) {
//                        bw.write(line);
//                        bw.newLine();
//                        bw.flush();
//                        line = br.readLine();
//                    }
//                    if(project != null) {
//                        bw.write("\n---");
//                        bw.newLine();
//                        bw.write("---");
//                        bw.newLine();
//                        bw.flush();
//                    }
//
//                }
//                project = projects.readLine();
//                if(project != null) {
//
//                    bw.write("---");
//                    bw.newLine();
//                    bw.flush();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Klasse nicht gefunden! Prüfen Sie, ob diese Klasse existiert oder ob Sie den Namen in der OneClassOfProject.txt Datei richtig geschrieben haben!");
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
//        Set<Object> seen = ConcurrentHashMap.newKeySet();
//        return t -> seen.add(keyExtractor.apply(t));
//    }
}
