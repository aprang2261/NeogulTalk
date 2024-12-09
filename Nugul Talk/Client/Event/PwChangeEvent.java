package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.PwChangeForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class PwChangeEvent implements ActionListener {
    private LobbyForm lobbyForm;    // 대기 화면 폼
    private String id;          // 현재 로그인한 사용자의 아이디
    private Socket socket;              // 클라이언트 소켓

    // 생성자: 대기 화면 폼, 클라이언트 소켓, 로그인한 사용자의 이름과 아이디를 매개변수로 받음
    public PwChangeEvent(LobbyForm lobbyForm, Socket socket, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.id = id;
    }

    // ActionListener 인터페이스의 메서드 구현
    @Override
    public void actionPerformed(ActionEvent e) {
        new PwChangeForm(socket, id);
    }
}
