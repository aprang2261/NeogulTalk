package Client.Form;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatRoomForm extends JFrame {
    private DrawingPanel drawingPanel;
    private Socket socket;
    private String roomName;
    private String id;
    private JTextArea chat2; // 채팅 기록 표시
    private JTextField chatRoom3;
    private Timer chatUpdateTimer;
    private JTextArea userList;
    private Timer userListUpdateTimer;
    private JLabel chatRoomName;
    private JLabel chatRoomCreateTime2;
    private Timer drawingUpdateTimer;
    private int lastDrawingId = 0;

    public ChatRoomForm(Socket socket, String roomName, String id) {
        this.socket = socket; // Socket 필드 초기화
        this.roomName = roomName;
        this.id = id;

        setTitle("너굴톡");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        // 채팅방
        JLabel chat1 = new JLabel("채팅방");
        chat1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        chat1.setBounds(10, 5, 40, 20);
        add(chat1);

        chat2 = new JTextArea();
        chat2.setEditable(false);
        chat2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        JScrollPane scrollPane1 = new JScrollPane(chat2);
        scrollPane1.setBounds(10, 30, 280, 490);
        scrollPane1.setBorder(border);
        add(scrollPane1);

        JTextField chatRoom3 = new JTextField(20);
        chatRoom3.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        chatRoom3.setBounds(10, 525, 250, 25);
        chatRoom3.setBorder(border);
        add(chatRoom3);

        ImageIcon icon1 = new ImageIcon("Client/Img/icon3.png");
        Image img1 = icon1.getImage();
        Image newimg1 = img1.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon newicon1 = new ImageIcon(newimg1);

        JButton enterChat = new JButton(newicon1);
        enterChat.setBounds(265, 525, 25, 25);
        enterChat.setFocusPainted(false);
        enterChat.setContentAreaFilled(false);
        add(enterChat);

        // 메시지 전송 이벤트 리스너
        enterChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = chatRoom3.getText().trim();
                if (!message.isEmpty()) {
                    sendMessageToServer(message);
                    chatRoom3.setText(""); // 입력창 초기화
                }
            }
        });

        // 초기 채팅 기록 불러오기
        loadChatHistory();

        JLabel drawing = new JLabel("공유 그림판");
        drawing.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        drawing.setBounds(300, 5, 60, 20);
        add(drawing);

        drawingPanel = new DrawingPanel();
        drawingPanel.setBounds(300, 30, 290, 320);
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        add(drawingPanel);

        // 선 색상 선택 버튼
        JButton colorButton = new JButton("색");
        colorButton.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));
        colorButton.setFocusPainted(false);
        colorButton.setContentAreaFilled(false);
        colorButton.setBounds(365, 5, 50, 20);
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "색상 선택", Color.BLACK);
            if (selectedColor != null) {
                drawingPanel.setColor(selectedColor);
            }
        });
        add(colorButton);

        JSlider strokeWidthSlider = new JSlider(1, 10, 2); // 1 ~ 10 px 굵기
        strokeWidthSlider.setBackground(Color.WHITE);
        strokeWidthSlider.setBounds(420, 5, 60, 20);
        strokeWidthSlider.addChangeListener(e -> {
            int value = strokeWidthSlider.getValue();
            drawingPanel.setStrokeWidth(value);
        });
        add(strokeWidthSlider);

        // 지우개 버튼
        JButton eraserButton = new JButton("지우개");
        eraserButton.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));
        eraserButton.setFocusPainted(false);
        eraserButton.setContentAreaFilled(false);
        eraserButton.setBounds(485, 5, 70, 20);
        eraserButton.addActionListener(e -> {
            drawingPanel.setColor(Color.WHITE); // 지우개는 흰색으로 설정
        });
        add(eraserButton);

        // 전체 지우기 버튼
        JButton clearButton = new JButton("X");
        clearButton.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));
        clearButton.setFocusPainted(false);
        clearButton.setContentAreaFilled(false);
        clearButton.setBounds(560, 5, 30, 20);
        clearButton.addActionListener(e -> drawingPanel.clear());
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "정말로 그림판을 초기화하시겠습니까?",
                    "그림판 초기화",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // 서버로 그림 데이터 삭제 요청
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                    writer.println("CLEAR_DRAWING_DATA " + roomName); // 서버에 명령어 전송
                    writer.flush();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "그림판 초기화 실패.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(clearButton);

        JLabel uploadedFile1 = new JLabel("업로드된 파일 목록");
        uploadedFile1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        uploadedFile1.setBounds(300, 355, 120, 20);
        add(uploadedFile1);

        JTextArea uploadedFile2 = new JTextArea();
        chat2.setEditable(false);

        JScrollPane scrollPane2 = new JScrollPane(uploadedFile2);
        scrollPane2.setBounds(300, 380, 290, 140);
        scrollPane2.setBorder(border);
        add(scrollPane2);

        JLabel uploadedFile3 = new JLabel("파일 업로드");
        uploadedFile3.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        uploadedFile3.setBounds(300, 525, 60, 25);
        add(uploadedFile3);

        ImageIcon icon2 = new ImageIcon("Client/Img/icon5.png");
        Image img2 = icon2.getImage();
        Image newimg2 = img2.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon newicon2 = new ImageIcon(newimg2);

        JButton uploadedFile4 = new JButton(newicon2);
        uploadedFile4.setBounds(365, 525, 25, 25);
        uploadedFile4.setFocusPainted(false);
        uploadedFile4.setContentAreaFilled(false);
        add(uploadedFile4);

        JLabel uploadedFile5 = new JLabel("파일 삭제");
        uploadedFile5.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        uploadedFile5.setBounds(400, 525, 50, 25);
        add(uploadedFile5);

        ImageIcon icon3 = new ImageIcon("Client/Img/icon6.png");
        Image img3 = icon3.getImage();
        Image newimg3 = img3.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon newicon3 = new ImageIcon(newimg3);

        JButton uploadedFile6 = new JButton(newicon3);
        uploadedFile6.setBounds(455, 525, 25, 25);
        uploadedFile6.setFocusPainted(false);
        uploadedFile6.setContentAreaFilled(false);
        add(uploadedFile6);

        chatRoomName = new JLabel("*** 채팅방입니다.");
        chatRoomName.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        chatRoomName.setBounds(600, 50, 275, 20);
        chatRoomName.setHorizontalAlignment(JLabel.CENTER);
        add(chatRoomName);

        JLabel chatRoomCreateTime1 = new JLabel("방 생성일시");
        chatRoomCreateTime1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 10));
        chatRoomCreateTime1.setBounds(600, 87, 275, 10);
        chatRoomCreateTime1.setHorizontalAlignment(JLabel.CENTER);
        add(chatRoomCreateTime1);

        chatRoomCreateTime2 = new JLabel("****년 **월 **일 **시 **분");
        chatRoomCreateTime2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 10));
        chatRoomCreateTime2.setBounds(600, 100, 275, 10);
        chatRoomCreateTime2.setHorizontalAlignment(JLabel.CENTER);
        add(chatRoomCreateTime2);

        JButton exitChatRoom = new JButton("채팅방 나가기");
        exitChatRoom.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        exitChatRoom.setBounds(663, 155, 150, 35);
        exitChatRoom.setBackground(Color.WHITE);
        exitChatRoom.setBorder(border);
        add(exitChatRoom);
        exitChatRoom.addActionListener(e -> {
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                writer.println("LEAVE_CHAT_ROOM " + roomName + " " + id); // 서버에 요청 전송
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = reader.readLine();
                if ("LEAVE_CHAT_ROOM_SUCCESS".equals(response)) {
                    JOptionPane.showMessageDialog(this, "채팅방을 나갔습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                    stopChatUpdateTimer(); // 채팅 업데이트 타이머 중지
                    stopUserListUpdateTimer(); // 유저 목록 업데이트 타이머 중지
                    dispose(); // 창 닫기
                } else {
                    JOptionPane.showMessageDialog(this, "채팅방 나가기 실패.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버와의 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton deleteChatRoom = new JButton("채팅방 삭제");
        deleteChatRoom.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        deleteChatRoom.setBounds(663, 200, 150, 35);
        deleteChatRoom.setBackground(Color.WHITE);
        deleteChatRoom.setBorder(border);
        deleteChatRoom.setForeground(Color.RED);
        add(deleteChatRoom);

        deleteChatRoom.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "정말로 이 채팅방을 삭제하시겠습니까?",
                    "채팅방 삭제",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                    writer.println("DELETE_CHAT_ROOM " + roomName); // 서버에 삭제 요청
                    writer.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = reader.readLine();
                    if ("DELETE_CHAT_ROOM_SUCCESS".equals(response)) {
                        JOptionPane.showMessageDialog(this, "채팅방이 삭제되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "채팅방 삭제 실패.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "서버와의 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel chatRoomUserList = new JLabel("채팅방 유저 목록");
        chatRoomUserList.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        chatRoomUserList.setBounds(600, 300, 300, 20);
        add(chatRoomUserList);

        userList = new JTextArea();
        userList.setEditable(false);
        userList.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        JScrollPane scrollPane3 = new JScrollPane(userList);
        scrollPane3.setBounds(600, 325, 275, 195);
        scrollPane3.setBorder(border);
        add(scrollPane3);

        startChatUpdateTimer();
        startUserListUpdateTimer();
        startDrawingUpdateTimer();
        loadChatRoomDetails();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopUserListUpdateTimer();
                stopChatUpdateTimer();
                dispose();
            }
        });

        setVisible(true);
    }

    private void sendMessageToServer(String message) {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            writer.println("SEND_MESSAGE " + roomName + " " + id + " " + message);
            writer.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "메시지 전송 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadChatHistory() {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            writer.println("GET_RECENT_CHAT " + roomName + " 100"); // 최근 100개의 메시지를 요청
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder chatHistory = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).equals("END_OF_HISTORY")) {
                chatHistory.append(line).append("\n");
            }
            SwingUtilities.invokeLater(() -> chat2.setText(chatHistory.toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "채팅 기록 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startChatUpdateTimer() {
        chatUpdateTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadChatHistory();
            }
        });
        chatUpdateTimer.start();
    }

    private void stopChatUpdateTimer() {
        if (chatUpdateTimer != null) {
            chatUpdateTimer.stop();
        }
    }

    private void loadUserList() {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            writer.println("GET_ROOM_USERS " + roomName); // 유저 목록 요청
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder userListData = new StringBuilder();
            String line;
            while (!(line = reader.readLine()).equals("END_OF_USER_LIST")) {
                userListData.append(line).append("\n");
            }

            SwingUtilities.invokeLater(() -> userList.setText(userListData.toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "유저 목록 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startUserListUpdateTimer() {
        userListUpdateTimer = new Timer(5000, new ActionListener() { // 5초마다 유저 목록 업데이트
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUserList();
            }
        });
        userListUpdateTimer.start();
    }

    private void stopUserListUpdateTimer() {
        if (userListUpdateTimer != null) {
            userListUpdateTimer.stop();
        }
    }

    private void loadChatRoomDetails() {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            writer.println("GET_CHAT_ROOM_DETAILS " + roomName); // 채팅방 세부 정보 요청
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String chatRoomNameValue = null;
            String chatRoomCreateTimeValue = null;

            String line;
            while (!(line = reader.readLine()).equals("END_OF_DETAILS")) {
                if (line.startsWith("CHAT_ROOM_NAME")) {
                    chatRoomNameValue = line.substring("CHAT_ROOM_NAME ".length());
                } else if (line.startsWith("CHAT_ROOM_CREATE_TIME")) {
                    chatRoomCreateTimeValue = line.substring("CHAT_ROOM_CREATE_TIME ".length());
                }
            }

            String finalChatRoomNameValue = chatRoomNameValue;
            String finalChatRoomCreateTimeValue = chatRoomCreateTimeValue;
            SwingUtilities.invokeLater(() -> {
                if (finalChatRoomNameValue != null) {
                    chatRoomName.setText(finalChatRoomNameValue + " 채팅방입니다.");
                }
                if (finalChatRoomCreateTimeValue != null) {
                    chatRoomCreateTime2.setText(finalChatRoomCreateTimeValue);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "채팅방 세부 정보 로드 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    class DrawingPanel extends JPanel {
        private Image image;
        private Graphics2D g2;
        private int currentX, currentY, oldX, oldY;
        private Color currentColor = Color.BLACK;
        private int strokeWidth = 2;

        public DrawingPanel() {
            setDoubleBuffered(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    oldX = e.getX();
                    oldY = e.getY();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    currentX = e.getX();
                    currentY = e.getY();
                    if (g2 != null) {
                        g2.setStroke(new BasicStroke(strokeWidth));
                        g2.setColor(currentColor);
                        g2.drawLine(oldX, oldY, currentX, currentY);
                        repaint();

                        sendDrawingDataToServer(oldX, oldY, currentX, currentY, currentColor, strokeWidth);

                        oldX = currentX;
                        oldY = currentY;
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (image == null) {
                image = createImage(getSize().width, getSize().height);
                g2 = (Graphics2D) image.getGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                clear();
            }
            g.drawImage(image, 0, 0, null);
        }

        public void setColor(Color color) {
            currentColor = color;
        }

        public void setStrokeWidth(int width) {
            strokeWidth = width;
        }

        public void clear() {
            g2.setPaint(Color.white);
            g2.fillRect(0, 0, getSize().width, getSize().height);
            g2.setPaint(currentColor);
            repaint();
        }

        public void drawLine(int startX, int startY, int endX, int endY, Color color, int strokeWidth) {
            if (g2 != null) {
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.setColor(color);
                g2.drawLine(startX, startY, endX, endY);
                repaint();
            }
        }

        public void sendDrawingDataToServer(int startX, int startY, int endX, int endY, Color color, int strokeWidth) {
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                int colorRGB = color.getRGB();
                writer.println("DRAW_LINE " + roomName + " " + id + " " + startX + " " + startY + " " + endX + " " + endY + " " + colorRGB + " " + strokeWidth);
                writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "서버로 데이터를 전송하는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startDrawingUpdateTimer() {
        drawingUpdateTimer = new Timer(1000, e -> fetchDrawingDataFromServer());
        drawingUpdateTimer.start();
    }

    private void fetchDrawingDataFromServer() {
        try {
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            writer.println("GET_DRAWING_DATA " + roomName + " " + lastDrawingId);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (!(line = reader.readLine()).equals("END_OF_DRAWING_DATA")) {
                try {
                    String[] data = line.split(" ");
                    if (data.length < 7) {
                        System.err.println("Invalid data format: " + line);
                        continue;
                    }

                    int drawingId = Integer.parseInt(data[0]);
                    int startX = Integer.parseInt(data[1]);
                    int startY = Integer.parseInt(data[2]);
                    int endX = Integer.parseInt(data[3]);
                    int endY = Integer.parseInt(data[4]);

                    Color color;
                    try {
                        color = Color.decode(data[5]);
                    } catch (NumberFormatException e) {
                        color = new Color(Integer.parseInt(data[5]));

                        int strokeWidth = Integer.parseInt(data[6]);

                        drawingPanel.drawLine(startX, startY, endX, endY, color, strokeWidth);

                        lastDrawingId = drawingId;
                    } catch (Exception e) {
                        System.err.println("Error processing line: " + line);
                        e.printStackTrace();
                    }
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
