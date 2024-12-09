package Client.Event;

import Client.Form.RegisterForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class RegisterConfirmEvent implements ActionListener {
    private RegisterForm registerForm;
    private Socket socket;

    public RegisterConfirmEvent(RegisterForm registerForm, Socket socket) throws IOException {
        this.registerForm = registerForm;
        this.socket = socket;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        String name = registerForm.getNameField().getText();
        String email = registerForm.getEmailField().getText();
        String id = registerForm.getIdField().getText();
        String password = registerForm.getPwField().getText();

        // 정규식 패턴
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // 이메일 검증 패턴
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).+$"; // 영어 대소문자 + 특수문자 포함

        if (name.isEmpty() || email.isEmpty() || id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(registerForm, "모든 필드를 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } else if (!email.matches(emailRegex)) {
            // 이메일 형식 검증
            JOptionPane.showMessageDialog(registerForm, "올바른 이메일 주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } else if (!password.matches(passwordRegex)) {
            // 비밀번호 형식 검증
            JOptionPane.showMessageDialog(registerForm, "비밀번호는 영어 대소문자와 특수문자를 포함해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                // 서버에 회원가입 메시지 전송
                String message = "REGISTER " + name + " " + email + " " + id + " " + password;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                // 서버 응답에 따라 처리
                if ("REGISTER_SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(registerForm, "회원가입 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
                    // 회원가입 창 닫기
                    registerForm.dispose(); // 회원가입 창 닫기
                } else if ("REGISTER_FAIL_DUPLICATE_NAME".equals(response)) {
                    // 중복된 아이디로 회원가입 시도 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(registerForm, "이미 사용 중인 이름입니다. 다른 이름을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if ("REGISTER_FAIL_DUPLICATE_EMAIL".equals(response)) {
                    // 중복된 아이디로 회원가입 시도 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(registerForm, "이미 사용 중인 이메일입니다. 다른 이메일를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if ("REGISTER_FAIL_DUPLICATE_ID".equals(response)) {
                    // 중복된 아이디로 회원가입 시도 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(registerForm, "이미 사용 중인 아이디입니다. 다른 아이디를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else if ("REGISTER_FAIL".equals(response)) {
                    // 회원가입 실패 시, 오류 메시지 표시
                    JOptionPane.showMessageDialog(registerForm, "회원가입 실패. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 알 수 없는 응답이 온 경우, 오류 메시지 표시
                    JOptionPane.showMessageDialog(registerForm, "알 수 없는 응답: " + response, "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // 서버 연결 실패 시, 오류 메시지 표시
                JOptionPane.showMessageDialog(registerForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
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
