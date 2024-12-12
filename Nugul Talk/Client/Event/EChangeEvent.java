package Client.Event;

import Client.Form.EChangeForm;
import Client.Form.LobbyForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class EChangeEvent implements ActionListener {
    private LobbyForm lobbyForm;
    private String id;
    private Socket socket;

    public EChangeEvent(LobbyForm lobbyForm, Socket socket, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new EChangeForm(socket, id);
    }
}