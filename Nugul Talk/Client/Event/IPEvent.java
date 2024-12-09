package Client.Event;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.*;

import Client.Form.IPForm;
import Client.Form.LoginForm;

public class IPEvent implements ActionListener {
    private IPForm ipForm;
    private Socket socket;

    public IPEvent(IPForm ipForm) {
        this.ipForm = ipForm;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // IP 주소 가져오기
        String enteredIpAddress = ipForm.getIpAddress();

        UIManager.put("OptionPane.messageFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        if (enteredIpAddress == null || enteredIpAddress.isEmpty()) {
            JOptionPane.showMessageDialog(ipForm, "IP 주소를 입력하세요.");
            return;
        }

        // IP 유효성 검사
        if (!isValidIpAddress(enteredIpAddress)) {
            JOptionPane.showMessageDialog(ipForm, "올바르지 않은 IP 주소입니다.");
            return;
        }

        // 서버 연결 시도
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(enteredIpAddress, 1207), 1000);
            JOptionPane.showMessageDialog(ipForm, "서버와 연결되었습니다.");

            // 로그인 폼 생성 및 현재 폼 닫기
            SwingUtilities.invokeLater(() -> {
                new LoginForm(socket);
                ipForm.dispose();
            });

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ipForm, "서버에 연결할 수 없습니다. 유효한 IP 주소를 입력하세요.");
            ex.printStackTrace();
        }
    }

    // 유효한 IP 주소인지 확인하는 메서드
    private boolean isValidIpAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int octet = Integer.parseInt(part);
                if (octet < 0 || octet > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
}
