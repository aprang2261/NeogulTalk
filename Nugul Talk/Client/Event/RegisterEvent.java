package Client.Event;

import Client.Form.LoginForm;
import Client.Form.RegisterForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class RegisterEvent implements ActionListener {
    private LoginForm loginForm;
    private Socket socket;

    public RegisterEvent(LoginForm loginForm, Socket socket) {
        this.loginForm = loginForm;
        this.socket = socket;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            new RegisterForm(socket);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
