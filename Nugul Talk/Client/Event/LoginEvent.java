package Client.Event;

import Client.Form.LoginForm;
import Client.Form.LobbyForm;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class LoginEvent implements ActionListener {
    private LoginForm loginForm;
    private Socket socket;

    public LoginEvent(LoginForm loginForm, Socket socket) {
        this.loginForm = loginForm;
        this.socket = socket;
    }

    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        String id = loginForm.getIdField().getText();
        String password = new String(loginForm.getPwField().getPassword());

        try {
            if (id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginForm, "빈 칸이 있습니다. 모든 항목을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                String message = "LOGIN " + id + " " + password;
                sendMessage(socket, message);

                String response = receiveMessage(socket);

                if (response.startsWith("LOGIN_SUCCESS")) {
                    loginForm.dispose();

                    String[] parts = response.split(" ");
                    String userName = parts[1];

                    SwingUtilities.invokeLater(() -> {
                        new LobbyForm(socket, id);
                    });
                }
                else if (response.equals("LOGIN_ERROR_USER_NOT_FOUND")) {
                    JOptionPane.showMessageDialog(loginForm, "해당 ID가 존재하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
                else if (response.equals("LOGIN_ERROR_INCORRECT_CREDENTIALS")) {
                    JOptionPane.showMessageDialog(loginForm, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(loginForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private String receiveMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}