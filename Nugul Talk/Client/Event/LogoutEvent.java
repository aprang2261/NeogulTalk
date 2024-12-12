package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.LoginForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class LogoutEvent implements ActionListener {
    private LobbyForm lobbyForm;
    private Socket socket;
    private String userId;

    public LogoutEvent(LobbyForm lobbyForm, Socket socket, String userId) {
        this.socket = socket;
        this.lobbyForm = lobbyForm;
        this.userId = userId; // 사용자 ID 전달받음
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String message ="LOGOUT " + userId;
            sendMessage(socket, message);

            new LoginForm(socket);
            lobbyForm.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(lobbyForm, "로그아웃 중 오류가 발생했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
    }
}
