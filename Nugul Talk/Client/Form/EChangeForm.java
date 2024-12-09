package Client.Form;

import Client.Event.EChangeConfirmEvent;
import Client.Event.NnChangeConfirmEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.Socket;

public class EChangeForm extends JFrame {
    private JButton change;
    private JTextField eField;
    private EChangeConfirmEvent eChangeConfirmEvent;

    public EChangeForm(Socket socket, String id) {
        eChangeConfirmEvent = new EChangeConfirmEvent(this, socket, id);

        setTitle("닉네임 변경");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        JLabel nickname = new JLabel("바꿀 이메일을 입력해주세요");
        nickname.setBounds(10, 30, 266, 20);
        nickname.setHorizontalAlignment(JLabel.CENTER);
        nickname.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        eField = new JTextField();
        eField.setBounds(50, 50, 186, 30);
        eField.setBorder(border);
        eField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        change = new JButton("변경");
        change.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        change.setBounds(100, 85, 86, 30);
        change.setBorderPainted(false);
        change.setContentAreaFilled(false);
        change.addActionListener(eChangeConfirmEvent);

        add(nickname);
        add(eField);
        add(change);
        setVisible(true);
    }

    public JTextField geteField() { return eField; }
}
