import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class ActionListener implements AnActionListener {
    private Metrics m;
    private Instant StartTime;

    ActionListener(Metrics metrics){
        this.m = metrics;
        this.StartTime = Instant.now();
    }

    @Override
    public void beforeActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, @NotNull AnActionEvent event) {
        this.StartTime = Instant.now();
    }

    @Override
    public void afterActionPerformed(@NotNull AnAction action, @NotNull DataContext dataContext, @NotNull AnActionEvent event) {
        try {
            if (!event.getPlace().contains("Toolwindow") && !((action.getTemplateText().contains("Cut") ||
                    action.getTemplateText().contains("Backspace") || action.getTemplateText().contains("Paste") ||
                    action.getTemplateText().contains("Enter") || action.getTemplateText().contains("Copy") ||
                    action.getTemplateText().contains("Save") || action.getTemplateText().contains("Undo")
                    || action.getTemplateText().contains("Redo")))) {
                //util information
                Metric metric = new Metric(action.getTemplateText(), (action.getTemplateText() != null) ?
                        action.getTemplateText() + " in " + event.getPlace() : event.getPlace(),
                        this.StartTime, Instant.now(), "");
                m.addMetric(metric);
                Server.post(this.m);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
