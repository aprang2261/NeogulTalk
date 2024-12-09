package Client.Event;

import Client.Form.EChangeForm;
import Client.Form.NnChangeForm; // 이메일 입력 창 재사용

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class EChangeConfirmEvent implements ActionListener {
    private EChangeForm eChangeForm; // 이메일 입력 창
    private Socket socket;
    private String id;

    public EChangeConfirmEvent(EChangeForm eChangeForm, Socket socket, String id) {
        this.eChangeForm = eChangeForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 입력된 이메일 가져오기
        String newEmail = eChangeForm.geteField().getText().trim();

        // 이메일 유효성 검사
        if (newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(eChangeForm, "이메일은 공백일 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(eChangeForm, "유효하지 않은 이메일 형식입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 서버로 이메일 변경 요청 전송
            String message = "UPDATE_EMAIL " + id + " " + newEmail;
            sendMessage(socket, message);

            // 서버 응답 처리
            String response = receiveMessage(socket);
            if (response.equals("UPDATE_EMAIL_SUCCESS")) {
                JOptionPane.showMessageDialog(eChangeForm, "이메일이 성공적으로 변경되었습니다.");
                eChangeForm.dispose(); // 창 닫기
            } else if (response.equals("UPDATE_EMAIL_FAIL_DUPLICATE")) {
                JOptionPane.showMessageDialog(eChangeForm, "이메일이 중복되었습니다. 다른 이메일을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(eChangeForm, "이메일 변경에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(eChangeForm, "서버와의 연결에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        // 간단한 이메일 형식 확인 (정규식)
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
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
