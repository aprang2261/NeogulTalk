package Client.Form;

import Client.Event.CrCreateConfirmEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.Socket;

public class CrCreateForm extends JFrame {
    private JButton change;
    private JTextField chatRoomField;
    private CrCreateConfirmEvent crCreateConfirmEvent;

    public CrCreateForm(Socket socket, String id) {
        crCreateConfirmEvent = new CrCreateConfirmEvent(this, socket, id);

        setTitle("채팅방 이름 설정");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        JLabel chatRoom = new JLabel("채팅방 이름을 입력해주세요");
        chatRoom.setBounds(10, 30, 266, 20);
        chatRoom.setHorizontalAlignment(JLabel.CENTER);
        chatRoom.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        chatRoomField = new JTextField();
        chatRoomField.setBounds(50, 50, 186, 30);
        chatRoomField.setBorder(border);
        chatRoomField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        change = new JButton("생성");
        change.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        change.setBounds(100, 85, 86, 30);
        change.setBorderPainted(false);
        change.setContentAreaFilled(false);
        change.addActionListener(crCreateConfirmEvent);

        add(chatRoom);
        add(chatRoomField);
        add(change);
        setVisible(true);
    }

    public JTextField getChatRoomField() {
        return chatRoomField;
    }
}
