package Client.Form;

import Client.Event.RegisterConfirmEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class RegisterForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField idField;
    private JTextField pwField;
    private JButton register;
    private RegisterConfirmEvent registerConfirmEvent;

    public RegisterForm(Socket socket) throws IOException {
        registerConfirmEvent = new RegisterConfirmEvent(this, socket);

        setTitle("너굴톡");
        setSize(300, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        JLabel logo = new JLabel("");
        logo.setBounds(87, 30, 125, 125);

        ImageIcon icon = new ImageIcon("Client/Img/icon1.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(125, 125, Image.SCALE_SMOOTH);
        ImageIcon newicon = new ImageIcon(newimg);

        logo.setIcon(newicon);

        JLabel title1 = new JLabel("너굴톡에 오신 걸");
        JLabel title2 = new JLabel("환영합니다!");
        title1.setBounds(50, 130, 186, 20);
        title1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        title1.setHorizontalAlignment(JLabel.CENTER);
        title2.setBounds(50, 160, 186, 20);
        title2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        title2.setHorizontalAlignment(JLabel.CENTER);

        JLabel name = new JLabel("Name");
        name.setBounds(55, 198, 40, 20);
        name.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        name.setHorizontalAlignment(JLabel.LEFT);

        nameField = new JTextField(20);
        nameField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        nameField.setBounds(50, 220, 186, 30);
        nameField.setBorder(border);

        JLabel email = new JLabel("Email");
        email.setBounds(55, 258, 40, 20);
        email.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        email.setHorizontalAlignment(JLabel.LEFT);

        emailField = new JTextField(20);
        emailField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        emailField.setBounds(50, 280, 186, 30);
        emailField.setBorder(border);

        JLabel id = new JLabel("ID");
        id.setBounds(55, 318, 40, 20);
        id.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        id.setHorizontalAlignment(JLabel.LEFT);

        idField = new JTextField(20);
        idField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        idField.setBounds(50, 340, 186, 30);
        idField.setBorder(border);

        JLabel pw = new JLabel("PW");
        pw.setBounds(55, 378, 40, 20);
        pw.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        pw.setHorizontalAlignment(JLabel.LEFT);

        pwField = new JTextField(20);
        pwField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        pwField.setBounds(50, 400, 186, 30);
        pwField.setBorder(border);

        JLabel pwTerm = new JLabel("영어 대소문자 + 특수문자를 포함하여야 합니다.");
        pwTerm.setBounds(53, 428, 186, 20);
        pwTerm.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 9));
        pwTerm.setHorizontalAlignment(JLabel.LEFT);

        register = new JButton("회원가입");
        register.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        register.setBounds(100, 460, 86, 30);
        register.setBorderPainted(false);
        register.setContentAreaFilled(false);
        register.addActionListener(registerConfirmEvent);

        add(logo);
        add(title1);
        add(title2);
        add(name);
        add(nameField);
        add(email);
        add(emailField);
        add(id);
        add(idField);
        add(pw);
        add(pwField);
        add(pwTerm);
        add(register);
        setVisible(true);
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getEmailField() {
        return emailField;
    }

    public JTextField getIdField() {
        return idField;
    }

    public JTextField getPwField() {
        return pwField;
    }
}
