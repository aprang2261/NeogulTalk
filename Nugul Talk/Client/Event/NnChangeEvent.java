package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.NnChangeForm;
import Client.Form.PwChangeForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class NnChangeEvent implements ActionListener {
    private LobbyForm lobbyForm;    // 대기 화면 폼
    private String id;          // 현재 로그인한 사용자의 아이디
    private Socket socket;              // 클라이언트 소켓

    public NnChangeEvent(LobbyForm lobbyForm, Socket socket, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new NnChangeForm(socket, id);
    }
}