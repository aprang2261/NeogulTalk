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
    private String userId; // 사용자의 ID

    public LogoutEvent(LobbyForm lobbyForm, Socket socket, String userId) {
        this.socket = socket;
        this.lobbyForm = lobbyForm;
        this.userId = userId; // 사용자 ID 전달받음
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // LOGOUT 메시지와 사용자 ID를 서버로 전송
            String message ="LOGOUT " + userId;
            sendMessage(socket, message);

            // 로그아웃 후 로그인 화면 표시
            new LoginForm(socket);
            lobbyForm.dispose();
        } catch (IOException ex) {
            // 오류 발생 시 사용자에게 알림 표시
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
