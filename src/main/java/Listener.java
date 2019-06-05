import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;

import com.github.difflib.algorithm.DiffException;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

public class Listener implements VirtualFileListener {
    private Project project;
    private Metrics m;
    private File BC;
    private File AC;

    public Listener(Project project, Metrics m){
        this.project = project;
        this.m = m;

    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        Metric a = new Metric("CreateFile",event.getFile().toString(),Instant.now(),Instant.now(),"");
        m.addMetric(a);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        Metric a = new Metric("DeletedFile",event.getFile().toString(),Instant.now(),Instant.now(),"");
        m.addMetric(a);
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        Metric a = new Metric("MovedFile",event.getFile().toString(),Instant.now(),Instant.now(),"");
        m.addMetric(a);
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        Metric a = new Metric("CopiedFile",event.getFile().toString(),Instant.now(),Instant.now(),"");
        m.addMetric(a);
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event){

        String a = event.getFile().getCanonicalPath();

        if (!(a.contains(".xml"))) {
            this.AC = new File(a);
            try{
                this.diff();

            }  catch (IOException ex) {
                System.out.println(ex);
            } catch (DiffException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void beforeContentsChange(@NotNull VirtualFileEvent event){
        String a = event.getFile().getCanonicalPath();
        String f = "";

        if (!(a.contains(".xml"))) {
            File fileToRead = new File(a);

            try {
                this.BC = File.createTempFile("temp", null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Files.write(this.BC.toPath(), ("").getBytes());
            } catch (IOException e) {
                System.out.println(e);  // exception handling
            }

            try (FileReader fileStream = new FileReader(fileToRead);
                 BufferedReader bufferedReader = new BufferedReader(fileStream)) {
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    f += line + '\n';

                }

                try {
                    Files.write(this.BC.toPath(), f.getBytes(), StandardOpenOption.CREATE);
                } catch (IOException e) {
                  System.out.println(e);  // exception handling
                }

            } catch (FileNotFoundException ex) {
                System.out.println(ex);
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }

    }



    public void diff() throws IOException, DiffException {
        //build simple lists of the lines of the two testfiles

        int linesAdd = 0;
        int linesChange = 0;
        int linesRemove = 0;
        int commentsAdd = 0;
        int commentsRemove = 0;

        List<String> original = Files.readAllLines(this.BC.toPath());
        List<String> revised = Files.readAllLines(this.AC.toPath());
        Patch<String> patch = DiffUtils.diff(original, revised);

        for (AbstractDelta<String> delta : patch.getDeltas()) {
            String [] parts = delta.toString().split(",");
            String [] change = delta.toString().split("] to ");
            boolean startComment= false;

            String [] linesR;
            String [] linesA;
            if (change.length>1) {          //case ChangeDelta
                linesR = change[0].split(",");
                linesA = change[1].split(",");

                String [] temp = new String[linesR.length - 2];
                int j = 0;
                while (j < linesR.length) {
                    if (j > 1){
                        temp[j-2] = linesR[j];
                    }
                    j++;

                }
                linesR = temp;

                int d = linesR.length - linesA.length;
                if(d > 0){
                    //deleted more lines than added lines
                    int i = 0;
                    while(i <linesR.length){
                        if (i < linesA.length){
                            linesChange++;
                        }
                        else{
                            if (linesR[i].contains("//")){
                                commentsRemove ++;
                                linesRemove ++;
                            }
                            else linesRemove ++;
                        }
                        i++;
                    }

                }
                if (d == 0){
                    //delete and add same lines number
                    int i=0;
                    while (i< linesA.length){
                        linesChange++;
                        i++;
                    }
                }
                else{
                    //added more lines than deleted lines
                    int i = 0;
                    while(i <linesA.length){
                        if (i < linesR.length){
                            linesChange++;
                        }
                        else{
                            if (linesA[i].contains("//")){
                                commentsAdd ++;
                                linesAdd ++;
                            }
                            else linesAdd ++;
                        }
                        i++;
                    }
                }
            }
            else {              //case InsertDelta and RemoveDelta
                int i = 2;
                while (i < parts.length) {
                    if (parts[0].contains("InsertDelta")) {
                        if (parts[i].contains("/*")) {
                            startComment = true;
                        }

                        if (startComment == true || parts[i].contains("//")) {
                            linesAdd++;
                            commentsAdd++;
                        } else linesAdd++;

                        if (parts[i].contains("*/")) {
                            startComment = false;
                        }
                    }
                    if (parts[0].contains("DeleteDelta")) {
                        if (parts[i].contains("/*")) {
                            startComment = true;
                        }

                        if (startComment == true || parts[i].contains("//")) {
                            linesRemove++;
                            commentsRemove++;
                        } else linesRemove++;

                        if (parts[i].contains("*/")) {
                            startComment = false;
                        }
                    }
                    i++;
                }
            }

            if(linesAdd > 0){
                Metric a = new Metric("eclipse_lines_insert",this.AC.toPath().toString(),Instant.now(),Instant.now(),Integer.toString(linesAdd));
                m.addMetric(a);
            }
            if(linesChange > 0){
                Metric a = new Metric("eclipse_lines_change",this.AC.toPath().toString(),Instant.now(),Instant.now(),Integer.toString(linesChange));
                m.addMetric(a);
            }
            if(linesRemove > 0){
                Metric a = new Metric("eclipse_lines_delete",this.AC.toPath().toString(),Instant.now(),Instant.now(),Integer.toString(linesRemove));
                m.addMetric(a);
            }
            if(commentsAdd > 0){
                Metric a = new Metric("eclipse_comments_added",this.AC.toPath().toString(),Instant.now(),Instant.now(),Integer.toString(commentsAdd));
                m.addMetric(a);
            }
            if(commentsRemove > 0){
                Metric a = new Metric("eclipse_comments_deleted",this.AC.toPath().toString(),Instant.now(),Instant.now(),Integer.toString(commentsRemove));
                m.addMetric(a);
            }
            Server.post(this.m);
            this.BC.deleteOnExit();
        }
    }
}