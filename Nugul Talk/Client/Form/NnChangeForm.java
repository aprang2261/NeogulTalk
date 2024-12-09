package Client.Form;

import Client.Event.NnChangeConfirmEvent;
import Client.Event.NnChangeEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.Socket;

public class NnChangeForm extends JFrame {
    private JButton change;
    private JTextField nicknameField;
    private NnChangeConfirmEvent nnChangeConfirmEvent;

    public NnChangeForm(Socket socket, String id) {
        nnChangeConfirmEvent = new NnChangeConfirmEvent(this, socket, id);

        setTitle("닉네임 변경");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        JLabel nickname = new JLabel("바꿀 닉네임을 입력해주세요");
        nickname.setBounds(10, 30, 266, 20);
        nickname.setHorizontalAlignment(JLabel.CENTER);
        nickname.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        nicknameField = new JTextField();
        nicknameField.setBounds(50, 50, 186, 30);
        nicknameField.setBorder(border);
        nicknameField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        change = new JButton("변경");
        change.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        change.setBounds(100, 85, 86, 30);
        change.setBorderPainted(false);
        change.setContentAreaFilled(false);
        change.addActionListener(nnChangeConfirmEvent);

        add(nickname);
        add(nicknameField);
        add(change);
        setVisible(true);
    }

    public JTextField getNnField() { return nicknameField; }
}
