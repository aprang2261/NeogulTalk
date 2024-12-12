package Client.Event;

import Client.Form.ChatRoomForm;
import Client.Form.LobbyForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class CrEnterEvent implements ActionListener {
    private LobbyForm lobbyForm;
    private Socket socket;
    private String name;
    private String id;

    public CrEnterEvent(LobbyForm lobbyForm, Socket socket, String name, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.name = name;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (socket != null && !socket.isClosed()) {
                String message = "JOIN_CHAT_ROOM " + name + " " + id;
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write(message + "\n");
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = reader.readLine();

                if (response.startsWith("JOIN_CHAT_ROOM_SUCCESS")) {
                    String roomName = response.substring("JOIN_CHAT_ROOM_SUCCESS ".length()).trim();
                    new ChatRoomForm(socket, roomName, id);
                } else if (response.equals("JOIN_CHAT_ROOM_FAIL_ALREADY_IN_ROOM")) {
                    new ChatRoomForm(socket, name, id);
                } else {
                    JOptionPane.showMessageDialog(lobbyForm, "채팅방 입장에 실패했습니다: " + response, "입장 실패", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(lobbyForm, "서버와 연결이 끊어졌습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(lobbyForm, "채팅방 입장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
