package Client.Event;

import Client.Form.LobbyForm;
import Client.Form.NnChangeForm;
import Client.Form.PwChangeForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class NnChangeEvent implements ActionListener {
    private LobbyForm lobbyForm;
    private String id;
    private Socket socket;

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
