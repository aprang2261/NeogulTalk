package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.PwChangeForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class PwChangeConfirmEvent implements ActionListener {
    private PwChangeForm pwChangeForm;
    private Socket socket;
    private String id;

    public PwChangeConfirmEvent(PwChangeForm pwChangeForm, Socket socket, String id) {
        this.pwChangeForm = pwChangeForm;
        this.socket = socket;
        this.id = id;
    }

    public void actionPerformed(ActionEvent e) {
        UIManager.put("OptionPane.messageFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        try {
            if (pwChangeForm != null) {
                String currentPassword = pwChangeForm.getCurrentPwField().getText();
                String changePassword = pwChangeForm.getChangePwField().getText();

                if (currentPassword.isEmpty() || changePassword.isEmpty()) {
                    JOptionPane.showMessageDialog(pwChangeForm, "모든 필드를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (changePassword.length() < 8) {
                    JOptionPane.showMessageDialog(pwChangeForm, "비밀번호는 최소 8자 이상이어야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).+$";
                if (!changePassword.matches(passwordRegex)) {
                    JOptionPane.showMessageDialog(pwChangeForm, "비밀번호는 영어 대소문자와 특수문자를 포함해야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String message = "CHANGE_PASSWORD " + id + " " + currentPassword + " " + changePassword;
                sendMessage(socket, message);
                String response = receiveMessage(socket);

                if (response == null) {
                    JOptionPane.showMessageDialog(pwChangeForm, "서버 응답을 받지 못했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 서버 응답 처리
                switch (response) {
                    case "CHANGE_PASSWORD_SUCCESS":
                        JOptionPane.showMessageDialog(pwChangeForm, "비밀번호가 성공적으로 변경되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
                        pwChangeForm.dispose();

                        break;

                    case "CHANGE_PASSWORD_EQUAL":
                        JOptionPane.showMessageDialog(pwChangeForm, "현재 비밀번호와 새 비밀번호가 동일합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                        break;

                    case "CHANGE_PASSWORD_INCORRECT_CURRENT":
                        JOptionPane.showMessageDialog(pwChangeForm, "현재 비밀번호가 잘못되었습니다. 다시 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                        break;

                    case "CHANGE_PASSWORD_FAIL":
                        JOptionPane.showMessageDialog(pwChangeForm, "비밀번호 변경에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                        break;

                    default:
                        JOptionPane.showMessageDialog(pwChangeForm, "알 수 없는 서버 응답: " + response, "오류", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(pwChangeForm, "서버 연결에 실패했습니다. 다시 시도하세요.", "오류", JOptionPane.ERROR_MESSAGE);
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
