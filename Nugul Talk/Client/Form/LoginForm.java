package Client.Form;

import Client.Event.LoginEvent;
import Client.Event.RegisterEvent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.Socket;

public class LoginForm extends JFrame {
    private JTextField idField;
    private JPasswordField pwField;
    private JButton login;
    private JButton register;
    private RegisterEvent registerEvent;
    private LoginEvent loginEvent;

    public LoginForm(Socket socket) {
        loginEvent = new LoginEvent(this, socket);
        registerEvent = new RegisterEvent(this, socket);

        // 창 설정
        setTitle("너굴톡");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 로고
        JLabel logo = new JLabel("");
        logo.setBounds(395, 130, 100, 100);

        ImageIcon icon = new ImageIcon("Client/Img/icon1.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(125, 125, Image.SCALE_SMOOTH);
        ImageIcon newicon = new ImageIcon(newimg);

        logo.setIcon(newicon);

        // 타이틀
        JLabel title = new JLabel("너굴톡");
        title.setBounds(350, 220, 200, 20);
        title.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        title.setHorizontalAlignment(JLabel.CENTER);

        // 아이디
        JLabel id = new JLabel("ID");
        id.setBounds(320, 268, 25, 30);
        id.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        id.setHorizontalAlignment(JLabel.CENTER);

        // 아이디 입력 칸
        idField = new JTextField(20);
        idField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        idField.setBounds(350, 270, 200, 30);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);
        idField.setBorder(border);

        // 비밀번호
        JLabel pw = new JLabel("PW");
        pw.setBounds(320, 308, 25, 30);
        pw.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        pw.setHorizontalAlignment(JLabel.CENTER);

        // 비밀번호 입력 칸
        pwField = new JPasswordField(20);
        pwField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        pwField.setBounds(350, 310, 200, 30);
        pwField.setBorder(border);

        // 로그인 버튼
        login = new JButton("로그인");
        login.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        login.setBounds(350, 350, 90, 30);
        login.setBorderPainted(false);
        login.setContentAreaFilled(false);
        login.addActionListener(loginEvent);

        // 회원가입 버튼
        register = new JButton("회원가입");
        register.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 15));
        register.setBounds(460, 350, 90, 30);
        register.setBorderPainted(false);
        register.setContentAreaFilled(false);
        register.addActionListener(registerEvent);

        // 활성화
        add(logo);
        add(title);
        add(id);
        add(idField);
        add(pw);
        add(pwField);
        add(login);
        add(register);
        setVisible(true);
    }

    public JTextField getIdField() {
        return idField;
    }
    public JPasswordField getPwField() {
        return pwField;
    }
}
