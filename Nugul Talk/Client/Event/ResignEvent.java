package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.LoginForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class ResignEvent implements ActionListener {
    private LobbyForm lobbyForm;   // 대기 화면 폼
    private String id;      // 현재 로그인한 사용자의 아이디
    private Socket socket;

    public ResignEvent(LobbyForm lobbyForm, Socket socket, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.id = id;
    }

    public void actionPerformed(ActionEvent e) {
        int check = JOptionPane.showConfirmDialog(lobbyForm, "회원탈퇴하시겠습니까?", "회원탈퇴 확인", JOptionPane.YES_NO_OPTION);

        if (check == JOptionPane.YES_OPTION) {
            try {
                String message = "RESIGN " + id;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                if (response.equals("RESIGN_SUCCESS")) {
                    lobbyForm.stopUserListUpdaterThread();

                    SwingUtilities.invokeLater(() -> {
                        new LoginForm(socket);
                        lobbyForm.dispose();
                    });
                } else if (response.equals("RESIGN_FAIL")) {
                    JOptionPane.showMessageDialog(lobbyForm, "회원 탈퇴에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if (response.equals("RESIGN_FAIL_USER_NOT_FOUND")) {
                    JOptionPane.showMessageDialog(lobbyForm, "사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(lobbyForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
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
