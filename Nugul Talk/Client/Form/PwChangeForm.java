package Client.Form;

import Client.Event.PwChangeConfirmEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.Socket;

public class PwChangeForm extends JFrame {
    private JButton change;
    private JTextField currentPwField;
    private JTextField changePwField;
    private PwChangeConfirmEvent pwChangeConfirmEvent;

    public PwChangeForm(Socket socket, String id) {
        pwChangeConfirmEvent = new PwChangeConfirmEvent(this, socket, id);

        setTitle("비밀번호 변경");
        setSize(300, 260);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        JLabel currentPw = new JLabel("현재 비밀번호를 입력해주세요");
        currentPw.setBounds(10, 30, 266, 20);
        currentPw.setHorizontalAlignment(JLabel.CENTER);
        currentPw.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        currentPwField = new JTextField();
        currentPwField.setBounds(50, 50, 186, 30);
        currentPwField.setBorder(border);
        currentPwField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        JLabel changePw = new JLabel("바꿀 비밀번호를 입력해주세요");
        changePw.setBounds(10, 90, 266, 20);
        changePw.setHorizontalAlignment(JLabel.CENTER);
        changePw.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        changePwField = new JTextField();
        changePwField.setBounds(50, 110, 186, 30);
        changePwField.setBorder(border);
        changePwField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        JLabel pwTerm = new JLabel("영어 대소문자 + 특수문자를 포함하여야 합니다.");
        pwTerm.setBounds(53, 138, 186, 20);
        pwTerm.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 9));
        pwTerm.setHorizontalAlignment(JLabel.LEFT);

        change = new JButton("변경");
        change.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        change.setBounds(100, 165, 86, 30);
        change.setBorderPainted(false);
        change.setContentAreaFilled(false);
        change.addActionListener(pwChangeConfirmEvent);

        add(currentPw);
        add(currentPwField);
        add(changePw);
        add(changePwField);
        add(pwTerm);
        add(change);
        setVisible(true);
    }

    public JTextField getCurrentPwField() { return currentPwField; }
    public JTextField getChangePwField() {
        return changePwField;
    }
}
