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
        // 회원 탈퇴 여부를 확인하는 다이얼로그 표시
        int check = JOptionPane.showConfirmDialog(lobbyForm, "회원탈퇴하시겠습니까?", "회원탈퇴 확인", JOptionPane.YES_NO_OPTION);

        // 사용자가 "예"를 선택한 경우
        if (check == JOptionPane.YES_OPTION) {
            try {
                // 서버에 회원 탈퇴 메시지 전송
                String message = "RESIGN " + id;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                // 서버 응답에 따라 처리
                if (response.equals("RESIGN_SUCCESS")) {
                    lobbyForm.stopUserListUpdaterThread();

                    // 회원 탈퇴 성공 시, 새로운 로그인 폼을 생성하고 현재의 대기 화면을 닫음
                    SwingUtilities.invokeLater(() -> {
                        new LoginForm(socket);
                        lobbyForm.dispose();
                    });
                } else if (response.equals("RESIGN_FAIL")) {
                    // 회원 탈퇴 실패 시 오류 메시지 표시
                    JOptionPane.showMessageDialog(lobbyForm, "회원 탈퇴에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if (response.equals("RESIGN_FAIL_USER_NOT_FOUND")) {
                    // 사용자 ID가 존재하지 않는 경우
                    JOptionPane.showMessageDialog(lobbyForm, "사용자를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // 서버 연결 실패 시 오류 메시지 표시
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

    // 소켓을 통해 서버로부터 메시지를 받아오는 메서드
    private String receiveMessage(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
