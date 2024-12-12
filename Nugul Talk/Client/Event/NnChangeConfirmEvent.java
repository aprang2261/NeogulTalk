package Client.Event;

import Client.Form.NnChangeForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class NnChangeConfirmEvent implements ActionListener {
    private NnChangeForm nnChangeForm;
    private Socket socket;
    private String id;

    public NnChangeConfirmEvent(NnChangeForm nnChangeForm, Socket socket, String id) {
        this.nnChangeForm = nnChangeForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newNickname = nnChangeForm.getNnField().getText().trim();

        if (newNickname.isEmpty()) {
            JOptionPane.showMessageDialog(nnChangeForm, "닉네임은 공백일 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newNickname.length() < 3 || newNickname.length() > 15) {
            JOptionPane.showMessageDialog(nnChangeForm, "닉네임은 3~15자 이내여야 합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String message = "UPDATE_NICKNAME " + id + " " + newNickname;
            sendMessage(socket, message);

            String response = receiveMessage(socket);
            if (response.equals("UPDATE_NICKNAME_SUCCESS")) {
                JOptionPane.showMessageDialog(nnChangeForm, "닉네임이 성공적으로 변경되었습니다.");
                nnChangeForm.dispose();
            } else if (response.equals("UPDATE_NICKNAME_FAIL_DUPLICATE")) {
                JOptionPane.showMessageDialog(nnChangeForm, "닉네임이 중복되었습니다. 다른 닉네임을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(nnChangeForm, "닉네임 변경에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(nnChangeForm, "서버와의 연결에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
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
