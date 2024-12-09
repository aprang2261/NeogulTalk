package Client.Form;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import Client.Event.IPEvent;

public class IPForm extends JFrame {
    private JTextField ipField;
    private IPEvent ipEvent;

    public IPForm() {
        setTitle("너굴톡 서버 연결");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        // IPEvent
        JLabel ip = new JLabel("서버의 IP를 입력해주세요!");
        ip.setBounds(10, 10, 280, 20);
        ip.setHorizontalAlignment(JLabel.CENTER);
        ip.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        // IPEvent 입력 칸
        ipField = new JTextField();
        ipField.setBounds(30, 40, 200, 30);
        ipField.setBorder(border);
        ipField.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));

        // 연결 버튼
        JButton cnt = new JButton("");
        cnt.setBounds(230, 40, 30, 30);

        ImageIcon icon = new ImageIcon("Client/Img/icon4.png");
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon newicon = new ImageIcon(newimg);
        cnt.setIcon(newicon);
        cnt.setFocusPainted(false);
        cnt.setContentAreaFilled(false);

        // IP 연결 시도
        ipEvent = new IPEvent(this);
        cnt.addActionListener(ipEvent);

        // 활성화
        add(ip);
        add(ipField);
        add(cnt);
        setVisible(true);
    }

    // IP 정보를 넘기기
    public String getIpAddress() {
        return ipField.getText();
    }
}

