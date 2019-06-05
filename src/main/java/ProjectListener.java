import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;


public class ProjectListener implements ProjectComponent {
    private Project project;
    private Metrics m;

    public ProjectListener(Project project, Metrics m){
        this.project = project;
        this.m = m;
    }


    @Override
    public void projectOpened() {
        FilenameIndex.getVirtualFilesByName(
                project,
                "es",
                false,
                GlobalSearchScope.allScope(project)
        );
        VirtualFileManager.getInstance().addVirtualFileListener(
                new Listener(project, m)
        );
    }
}
