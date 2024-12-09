package Client.Event;

import Client.Form.CrCreateForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CrCreateConfirmEvent implements ActionListener {
    private CrCreateForm crCreateForm;
    private Socket socket;
    private String id;

    public CrCreateConfirmEvent(CrCreateForm crCreateForm, Socket socket, String id) {
        this.crCreateForm = crCreateForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String roomName = crCreateForm.getChatRoomField().getText().trim();
        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(crCreateForm, "채팅방 이름을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 서버로 요청 전송
            out.println("CREATE_ROOM " + id + " " + roomName);

            // 서버 응답 대기
            String response = in.readLine();
            if (response != null) {
                switch (response) {
                    case "CREATE_ROOM_SUCCESS":
                        JOptionPane.showMessageDialog(crCreateForm, "채팅방이 성공적으로 생성되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                        crCreateForm.dispose();
                        break;

                    case "CREATE_ROOM_FAIL_DUPLICATE_NAME":
                        JOptionPane.showMessageDialog(crCreateForm, "이미 존재하는 채팅방 이름입니다.", "중복 이름", JOptionPane.WARNING_MESSAGE);
                        break;

                    case "CREATE_ROOM_FAIL":
                        JOptionPane.showMessageDialog(crCreateForm, "채팅방 생성에 실패했습니다.", "실패", JOptionPane.ERROR_MESSAGE);
                        break;

                    default:
                        JOptionPane.showMessageDialog(crCreateForm, "알 수 없는 서버 응답: " + response, "오류", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(crCreateForm, "서버와의 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
