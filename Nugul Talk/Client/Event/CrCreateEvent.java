package Client.Event;

import Client.Form.CrCreateForm;
import Client.Form.LobbyForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class CrCreateEvent implements ActionListener {
    private LobbyForm lobbyForm;
    private String id;
    private Socket socket;

    public CrCreateEvent(LobbyForm lobbyForm, Socket socket, String id) {
        this.lobbyForm = lobbyForm;
        this.socket = socket;
        this.id = id;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new CrCreateForm(socket, id);
    }
}
