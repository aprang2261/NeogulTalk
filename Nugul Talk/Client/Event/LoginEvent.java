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

    // 생성자: 로그인 폼과 클라이언트 소켓을 매개변수로 받음
    public LoginEvent(LoginForm loginForm, Socket socket) {
        this.loginForm = loginForm;
        this.socket = socket;
    }

    // ActionListener 인터페이스의 메서드 구현
    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        // 입력된 아이디와 비밀번호를 가져옴
        String id = loginForm.getIdField().getText();
        String password = new String(loginForm.getPwField().getPassword());

        try {
            // 입력된 값이 비어있는지 확인
            if (id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginForm, "빈 칸이 있습니다. 모든 항목을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                // 서버로 로그인 메시지 전송
                String message = "LOGIN " + id + " " + password;
                sendMessage(socket, message);

                // 서버로부터 응답 메시지 받음
                String response = receiveMessage(socket);

                // 로그인 성공
                if (response.startsWith("LOGIN_SUCCESS")) {
                    loginForm.dispose();

                    // 응답 메시지에서 사용자 이름 추출
                    String[] parts = response.split(" ");
                    String userName = parts[1];

                    // 대기 화면을 생성하고 클라이언트 소켓, 사용자 이름, 아이디를 전달
                    SwingUtilities.invokeLater(() -> {
                        new LobbyForm(socket, id);
                    });
                }
                // 아이디가 존재하지 않는 경우
                else if (response.equals("LOGIN_ERROR_USER_NOT_FOUND")) {
                    JOptionPane.showMessageDialog(loginForm, "해당 ID가 존재하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
                // 비밀번호가 일치하지 않는 경우
                else if (response.equals("LOGIN_ERROR_INCORRECT_CREDENTIALS")) {
                    JOptionPane.showMessageDialog(loginForm, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(loginForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 메시지를 소켓을 통해 서버에 전송하는 메서드
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