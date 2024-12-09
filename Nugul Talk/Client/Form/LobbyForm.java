package Client.Form;

import Client.Event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LobbyForm extends JFrame {
    private CrCreateEvent crCreateEvent;
    private LogoutEvent logoutEvent;
    private NnChangeEvent nnChangeEvent;
    private EChangeEvent eChangeEvent;
    private PwChangeEvent pwChangeEvent;
    private ResignEvent resignEvent;

    public LobbyForm(Socket socket, String id) {
        // 창 설정
        setTitle("너굴톡");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Border border = BorderFactory.createLineBorder(Color.BLACK, 5);

        // 정보창 패널
        JPanel panel1 = new JPanel();
        panel1.setBounds(0, 0, 600, 600);
        panel1.setLayout(null);
        panel1.setBackground(Color.WHITE);
        
        // 채팅방
        JLabel chatlist1 = new JLabel("방 목록");
        chatlist1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        chatlist1.setBounds(10, 5, 40, 20);
        panel1.add(chatlist1);
        
        // 채팅방 추가 아이콘 버튼
        ImageIcon icon2 = new ImageIcon("Client/Img/icon2.png");
        Image img2 = icon2.getImage();
        Image newimg2 = img2.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon newicon2 = new ImageIcon(newimg2);

        JButton makeRoom = new JButton(newicon2);
        makeRoom.setBounds(50, 5, 20, 20);
        makeRoom.setFocusPainted(false);
        makeRoom.setContentAreaFilled(false);
        crCreateEvent = new CrCreateEvent(this, socket, id);
        makeRoom.addActionListener(crCreateEvent);
        panel1.add(makeRoom);

        // 채팅방 리스트
        JTextArea chatlist2 = new JTextArea();
        chatlist2.setEditable(false);
        chatlist2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        JScrollPane scrollPane1 = new JScrollPane(chatlist2);
        scrollPane1.setBounds(10, 30, 180, 200);
        scrollPane1.setBorder(border);
        panel1.add(scrollPane1);

        // 채팅방 클릭 이벤트 추가
        chatlist2.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭
                    try {
                        int pos = chatlist2.viewToModel2D(e.getPoint());
                        int start = Utilities.getRowStart(chatlist2, pos);
                        int end = Utilities.getRowEnd(chatlist2, pos);
                        String selectedRoom = chatlist2.getText(start, end - start).trim();

                        if (!selectedRoom.isEmpty()) {
                            new CrEnterEvent(LobbyForm.this, socket, selectedRoom, id).actionPerformed(null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        startChatRoomListUpdater(socket, chatlist2);

        // 유저 목록
        JLabel userlist1 = new JLabel("유저 목록");
        userlist1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        userlist1.setBounds(10, 235, 50, 20);
        panel1.add(userlist1);

        // 유저 목록 리스트
        JTextArea userlist2 = new JTextArea();
        userlist2.setEditable(false);
        userlist2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        JScrollPane scrollPane2 = new JScrollPane(userlist2);
        scrollPane2.setBounds(10, 260, 180, 290);
        scrollPane2.setBorder(border);
        panel1.add(scrollPane2);

        // 사용자 목록 갱신 스레드 시작
        startUserListUpdater(socket, userlist2);

        // AI 도우미
        JLabel aiHelper1 = new JLabel("AI 도우미");
        aiHelper1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        aiHelper1.setBounds(200, 5, 60, 20);
        panel1.add(aiHelper1);

        // AI 도우미 채팅
        JTextArea aiHelper2 = new JTextArea();
        aiHelper2.setEditable(false);

        JScrollPane scrollPane3 = new JScrollPane(aiHelper2);
        scrollPane3.setBounds(200, 30, 390, 200);
        scrollPane3.setBorder(border);

        aiHelper2.setLineWrap(true);
        aiHelper2.setWrapStyleWord(true);
        scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        aiHelper2.append("도우미: 안녕하세요! 너굴톡 AI 도우미입니다.\n");
        aiHelper2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        panel1.add(scrollPane3);

        // AI 도우미 채팅 입력
        JTextField aiHelper3 = new JTextField(20);
        aiHelper3.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        aiHelper3.setBounds(200, 235, 360, 25);
        aiHelper3.setBorder(border);
        panel1.add(aiHelper3);

        // AI 도우미 입력 아이콘 버튼
        ImageIcon icon3 = new ImageIcon("Client/Img/icon3.png");
        Image img3 = icon3.getImage();
        Image newimg3 = img3.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon newicon3 = new ImageIcon(newimg3);

        JButton aiHelper4 = new JButton(newicon3);
        aiHelper4.setBounds(565, 235, 25, 25);
        aiHelper4.setFocusPainted(false);
        aiHelper4.setContentAreaFilled(false);
        panel1.add(aiHelper4);

        // 상태
        JLabel status1 = new JLabel("서버 상태");
        status1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        status1.setBounds(200, 265, 60, 20);
        panel1.add(status1);

        // 서버 상태 창
        JTextArea status2 = new JTextArea();
        status2.setEditable(false);
        appendStatusLog(status2, "접속되었습니다.");
        status2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));

        status2.setLineWrap(true);
        status2.setWrapStyleWord(true);

        aiHelper4.addActionListener(e -> {
            String question = aiHelper3.getText().trim();
            if (question.isEmpty()) {
                appendStatusLog(status2, "질문이 입력되지 않았습니다.");
                return;
            }

            // 질문 표시 및 상태 업데이트
            aiHelper2.append("나: " + question + "\n");
            appendStatusLog(status2, "AI 답변 생성 중...");

            // 비동기 처리 시작
            new Thread(() -> {
                String response = sendQuestionToServer(socket, question);
                SwingUtilities.invokeLater(() -> {
                    if (response != null) {
                        aiHelper2.append("도우미: " + response + "\n\n");
                        appendStatusLog(status2, "AI 답변 생성 완료.");
                    } else {
                        aiHelper2.append("도우미: AI 응답 생성 중 오류 발생.\n\n");
                        appendStatusLog(status2, "AI 답변 생성 중 오류 발생.");
                    }
                    aiHelper3.setText(""); // 입력창 초기화
                });
            }).start();
        });

        JScrollPane scrollPane4 = new JScrollPane(status2);
        scrollPane4.setBounds(200, 290, 390, 260);
        scrollPane4.setBorder(border);
        scrollPane4.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel1.add(scrollPane4);

        // 정보 수정 패널
        JPanel panel2 = new JPanel();
        panel2.setBounds(600, 0, 300, 600);
        panel2.setLayout(null);
        panel2.setBackground(Color.white);

        // 인삿말
        JLabel welcome = new JLabel("안녕하세요!");
        welcome.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        welcome.setBounds(50, 40, 200, 20);
        welcome.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(welcome);

        LocalTime now = LocalTime.now();
        String greeting1;

        if (now.isBefore(LocalTime.of(6, 0))) {
            greeting1 = "좋은 새벽입니다!";
        } else if (now.isBefore(LocalTime.NOON)) {
            greeting1 = "좋은 아침입니다!";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            greeting1 = "좋은 오후입니다!";
        } else {
            greeting1 = "좋은 저녁입니다!";
        }

        JLabel greeting2 = new JLabel(greeting1);
        greeting2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 20));
        greeting2.setBounds(50, 60, 200, 20);
        greeting2.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(greeting2);

        // 날짜
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter monthDayFormatter = DateTimeFormatter.ofPattern("M월 d일");
        String monthDay = currentDate.format(monthDayFormatter);

        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        String koreanDayOfWeek = getKoreanDayOfWeek(dayOfWeek);

        // 날짜 라벨
        JLabel date = new JLabel("오늘은 " + monthDay + " " + koreanDayOfWeek + "입니다.");
        date.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 16));
        date.setBounds(50, 87, 200, 20);
        date.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(date);

        // 접속 정보
        String clientIpAddress = "알 수 없음";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            clientIpAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            clientIpAddress = "IP 주소를 가져올 수 없습니다.";
        }

        JLabel ip = new JLabel("접속 아이피 - " + clientIpAddress);
        ip.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 12));
        ip.setBounds(50, 115, 200, 20);
        ip.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(ip);

        // 닉네임 수정
        JButton nicknameChange = new JButton("닉네임 수정");
        nicknameChange.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        nicknameChange.setBounds(75, 155, 150, 35);
        nicknameChange.setBackground(Color.WHITE);
        nicknameChange.setBorder(border);
        nnChangeEvent = new NnChangeEvent(this, socket, id);
        nicknameChange.addActionListener(nnChangeEvent);
        panel2.add(nicknameChange);

        // 이메일 수정
        JButton emailChange = new JButton("이메일 수정");
        emailChange.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        emailChange.setBounds(75, 200, 150, 35);
        emailChange.setBackground(Color.WHITE);
        emailChange.setBorder(border);
        eChangeEvent = new EChangeEvent(this, socket, id);
        emailChange.addActionListener(eChangeEvent);
        panel2.add(emailChange);

        // 비밀번호 수정
        JButton pwChange = new JButton("비밀번호 수정");
        pwChange.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        pwChange.setBounds(75, 245, 150, 35);
        pwChange.setBackground(Color.WHITE);
        pwChange.setBorder(border);
        pwChangeEvent = new PwChangeEvent(this, socket, id);
        pwChange.addActionListener(pwChangeEvent);
        panel2.add(pwChange);

        // 로그아웃
        JButton logout = new JButton("로그아웃");
        logout.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        logout.setBounds(75, 290, 150, 35);
        logout.setBackground(Color.WHITE);
        logout.setBorder(border);
        logoutEvent = new LogoutEvent(this, socket, id);
        panel2.add(logout);

        // 회원탈퇴
        JButton resign = new JButton("회원탈퇴");
        resign.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 13));
        resign.setForeground(Color.RED);
        resign.setBounds(75, 335, 150, 35);
        resign.setBackground(Color.WHITE);
        resign.setBorder(border);
        resignEvent = new ResignEvent(this, socket, id);
        resign.addActionListener(resignEvent);

        panel2.add(resign);

        // 로고
        JLabel logo = new JLabel("");
        logo.setBounds(100, 380, 100, 100);

        ImageIcon icon4 = new ImageIcon("Client/Img/icon1.png");
        Image img4 = icon4.getImage();
        Image newimg4 = img4.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon newicon4 = new ImageIcon(newimg4);

        logo.setIcon(newicon4);
        panel2.add(logo);

        // 타이틀
        JLabel title = new JLabel("너굴톡");
        title.setBounds(50, 460, 200, 20);
        title.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 16));
        title.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(title);

        // 버전 정보
        JLabel version1 = new JLabel("버전 정보");
        JLabel version2 = new JLabel("v1.0.0-release1");
        version1.setBounds(50, 490, 200, 20);
        version1.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 10));
        version1.setHorizontalAlignment(JLabel.CENTER);
        version2.setBounds(50, 500, 200, 20);
        version2.setFont(new Font("AppleSDGothicNeoB00", Font.PLAIN, 10));
        version2.setHorizontalAlignment(JLabel.CENTER);
        panel2.add(version1);
        panel2.add(version2);

        // 활성화
        add(panel1);
        add(panel2);
        setVisible(true);

        // 창 종료 이벤트 감지
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopUserListUpdaterThread();
                stopChatRoomListUpdaterThread();
                handleWindowClosing(socket, id);
            }
        });

        logout.addActionListener(e -> {
            stopUserListUpdaterThread(); // 스레드 종료
            logoutEvent.actionPerformed(e); // 기존 로그아웃 이벤트 실행
        });
    }

    // 날짜
    private static String getKoreanDayOfWeek(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "월요일";
            case TUESDAY:
                return "화요일";
            case WEDNESDAY:
                return "수요일";
            case THURSDAY:
                return "목요일";
            case FRIDAY:
                return "금요일";
            case SATURDAY:
                return "토요일";
            case SUNDAY:
                return "일요일";
            default:
                return "";
        }
    }

    private void handleWindowClosing(Socket socket, String userId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "정말로 종료하시겠습니까? 자동 로그아웃됩니다.",
                "종료 확인",
                JOptionPane.YES_NO_OPTION
        );

        // 사용자가 YES를 클릭한 경우에만 로그아웃 처리 및 창 종료
        if (confirm == JOptionPane.YES_OPTION) {
            sendLogoutMessage(socket, userId);
            dispose(); // 창 닫기
        }
        // NO를 선택한 경우 아무 작업도 하지 않음
    }

    private void sendLogoutMessage(Socket socket, String userId) {
        try {
            if (socket != null && !socket.isClosed()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("LOGOUT " + userId);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Thread userListUpdaterThread;
    private volatile boolean isRunning = true;

    public void startUserListUpdater(Socket socket, JTextArea userlist2) {
        userListUpdaterThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // 사용자 목록 요청
                    requestUserList(socket, userlist2);
                    Thread.sleep(5000); // 5초 간격으로 갱신
                } catch (InterruptedException e) {
                    System.out.println("사용자 목록 갱신 스레드가 중단되었습니다.");
                    Thread.currentThread().interrupt(); // 스레드 중단 상태 설정
                }
            }
        });
        userListUpdaterThread.start();
    }

    public void stopUserListUpdaterThread() {
        isRunning = false; // 실행 플래그 설정
        if (userListUpdaterThread != null && userListUpdaterThread.isAlive()) {
            userListUpdaterThread.interrupt(); // 스레드 중단
        }
        System.out.println("사용자 목록 갱신 스레드가 종료되었습니다.");
    }

    private void requestUserList(Socket socket, JTextArea userlist2) {
        try {
            if (socket == null || socket.isClosed()) {
                System.out.println("소켓이 닫혀 있습니다. 사용자 목록 요청 불가.");
                SwingUtilities.invokeLater(() -> userlist2.setText("ERROR: Unable to connect to server."));
                return;
            }

            // 서버로 요청 전송
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("GET_USER_LIST\n");
            writer.flush();

            // 서버 응답 수신
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder userList = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if ("END_OF_LIST".equals(line)) break; // 종료 신호 감지
                userList.append(line).append("\n");
            }

            System.out.println(userList.toString());

            // UI 업데이트
            SwingUtilities.invokeLater(() -> userlist2.setText(userList.toString()));

        } catch (IOException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> userlist2.setText("ERROR: Unable to retrieve user list."));
        }
    }

    private String sendQuestionToServer(Socket socket, String question) {
        try {
            // 서버로 질문 전송
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("ASK_LLM " + question + "\n");
            writer.flush();

            // 서버로부터 응답 수신
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine(); // 서버로부터 한 줄 응답 받음
            if (response.startsWith("ASK_LLM_SUCCESS")) {
                return response.substring("ASK_LLM_SUCCESS ".length());
            } else {
                return "서버에서 처리하지 못한 요청입니다.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 서버와의 통신 실패
        }
    }

    private void appendStatusLog(JTextArea textArea, String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        textArea.append("[" + timestamp + "] " + message + "\n");
    }

    private Thread chatRoomListUpdaterThread;
    private volatile boolean isChatRoomListRunning = true;

    public void startChatRoomListUpdater(Socket socket, JTextArea chatlist2) {
        chatRoomListUpdaterThread = new Thread(() -> {
            while (isChatRoomListRunning) {
                try {
                    // 채팅방 목록 요청
                    requestChatRoomList(socket, chatlist2);
                    Thread.sleep(5000); // 5초 간격으로 갱신
                } catch (InterruptedException e) {
                    System.out.println("채팅방 목록 갱신 스레드가 중단되었습니다.");
                    Thread.currentThread().interrupt(); // 스레드 중단 상태 설정
                }
            }
        });
        chatRoomListUpdaterThread.start();
    }

    public void stopChatRoomListUpdaterThread() {
        isChatRoomListRunning = false; // 실행 플래그 설정
        if (chatRoomListUpdaterThread != null && chatRoomListUpdaterThread.isAlive()) {
            chatRoomListUpdaterThread.interrupt(); // 스레드 중단
        }
        System.out.println("채팅방 목록 갱신 스레드가 종료되었습니다.");
    }

    private void requestChatRoomList(Socket socket, JTextArea chatlist2) {
        try {
            if (socket == null || socket.isClosed()) {
                System.out.println("소켓이 닫혀 있습니다. 채팅방 목록 요청 불가.");
                SwingUtilities.invokeLater(() -> chatlist2.setText("ERROR: Unable to connect to server."));
                return;
            }

            // 서버로 요청 전송
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("GET_CHAT_ROOM_LIST\n");
            writer.flush();

            // 서버 응답 수신
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder chatRoomList = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if ("END_OF_LIST".equals(line)) break; // 종료 신호 감지
                chatRoomList.append(line).append("\n");
            }

            System.out.println(chatRoomList.toString());

            // UI 업데이트
            SwingUtilities.invokeLater(() -> chatlist2.setText(chatRoomList.toString()));

        } catch (IOException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> chatlist2.setText("ERROR: Unable to retrieve chat room list."));
        }
    }

}


