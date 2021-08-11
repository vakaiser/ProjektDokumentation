
import java.io.*;

public class Main {
    public static void main(String[] args){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("MasterMd.md")));
            BufferedReader projects = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResources("OneClassOfProject.txt").nextElement().openStream()));
            String project = projects.readLine();
            while(project != null) {
                Class c = Class.forName(project);
                InputStream is = c.getClassLoader().getResources(project + ".md").nextElement().openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                while (line != null) {
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                    line = br.readLine();
                }
                project = projects.readLine();
                if(project != null) {
                    bw.write("\n---");
                    bw.newLine();
                    bw.write("\n---");
                    bw.newLine();
                    bw.write("\n---");
                    bw.newLine();
                    bw.flush();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Klasse nicht gefunden! Prüfen Sie, ob diese Klasse existiert oder ob Sie den Namen in der OneClassOfProject.txt Datei richtig geschrieben haben!");
        }

    }
}
