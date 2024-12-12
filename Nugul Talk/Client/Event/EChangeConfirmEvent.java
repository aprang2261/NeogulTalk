package Client.Event;

import Client.Form.EChangeForm;
import Client.Form.NnChangeForm; // 이메일 입력 창 재사용

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class EChangeConfirmEvent implements ActionListener {
    private EChangeForm eChangeForm;
    private Socket socket;
    private String id;

    public EChangeConfirmEvent(EChangeForm eChangeForm, Socket socket, String id) {
        this.eChangeForm = eChangeForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEmail = eChangeForm.geteField().getText().trim();

        if (newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(eChangeForm, "이메일은 공백일 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(eChangeForm, "유효하지 않은 이메일 형식입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String message = "UPDATE_EMAIL " + id + " " + newEmail;
            sendMessage(socket, message);

            String response = receiveMessage(socket);
            if (response.equals("UPDATE_EMAIL_SUCCESS")) {
                JOptionPane.showMessageDialog(eChangeForm, "이메일이 성공적으로 변경되었습니다.");
                eChangeForm.dispose();
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
