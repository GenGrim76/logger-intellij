import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Logger extends AnAction {
    public Logger() {
        super("login");
    }
    private String token;
    private User user;
    private Project project;
    private ProjectListener pListener;
    private Metrics m;

    public void actionPerformed(AnActionEvent event) {

        JFrame frame = new JFrame("Logger IntelliJ");
        frame.setSize(500, 500);

        JTextField serverAddr = new JTextField();
        JTextField userEmail = new JTextField();
        JTextField userPassword = new JPasswordField();

        Object[] message = { "Server address", serverAddr, "Email:", userEmail, "Password:", userPassword};
        int option = JOptionPane.showConfirmDialog(frame, message, null, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            this.user = new User(serverAddr.getText(), userEmail.getText(), userPassword.getText());
            this.token = Server.login(this.user);

            if (this.token == null || this.token.isEmpty()) {
                Messages.showInfoMessage("login fallito", "Logger info");
            }
            else{
                /*** INIZIO RACCOLTA METRICHE (LOGIN ANDATO A BUON FINE)***/
                Messages.showInfoMessage("login effettuato", "Logger Info");
                this.user.setToken(this.token);
                m = new Metrics(this.user);
                this.project = (Project) DataManager.getInstance().getDataContext().getData(DataConstants.PROJECT);
                this.pListener = new ProjectListener(this.project, m);  //listener Virtual File System
                this.pListener.projectOpened();
                ActionManager.getInstance().addAnActionListener( new ActionListener(new Metrics(this.user)));
                //listener UI IDE
            }
        }
    }
}