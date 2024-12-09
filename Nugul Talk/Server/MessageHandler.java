package Server;

import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class MessageHandler {
    private BufferedReader in;
    private PrintWriter out;
    private DatabaseManager databaseManager;
    private ClientHandler clientHandler;

    // 생성자
    public MessageHandler(BufferedReader in, PrintWriter out, ClientHandler clientHandler) throws SQLException {
        this.in = in;
        this.out = out;
        this.clientHandler = clientHandler;
        this.databaseManager = new DatabaseManager();
    }

    // 클라이언트 메시지 처리 메서드
    public void processClientMessage(String message) throws IOException, SQLException {
        String[] tokens = message.split("\\s+");
        System.out.println(message);

        if (tokens.length > 0) {
            String command = tokens[0].toUpperCase(); // 대소문자 무시

            switch (command) {
                case "REGISTER":
                    if (tokens.length == 5) {
                        System.out.println(message);
                        String name = tokens[1];
                        String email = tokens[2];
                        String id = tokens[3];
                        String pw = tokens[4];
                        registerUser(name, email, id, pw);
                    } else {
                        sendMessageToClient("REGISTER_FAIL_INVALID_INPUT");
                    }
                    break;

                case "LOGIN":
                    if (tokens.length == 3) {
                        String id = tokens[1];
                        String pw = tokens[2];
                        loginUser(id, pw);
                    } else {
                        sendMessageToClient("LOGIN_FAIL_INVALID_INPUT");
                    }
                    break;

                case "LOGOUT":
                    if (tokens.length == 2) {
                        String id = tokens[1];
                        logoutUser(id);
                    } else {
                        sendMessageToClient("LOGOUT_FAIL_INVALID_INPUT");
                    }
                    break;

                case "CHANGE_PASSWORD":
                    if (tokens.length == 4) {  // 올바른 조건
                        String id = tokens[1];
                        String currentPassword = tokens[2];
                        String changePassword = tokens[3];
                        changePassword(id, currentPassword, changePassword);
                    } else {
                        sendMessageToClient("CHANGE_PASSWORD_FAIL_INVALID_INPUT");
                    }
                    break;

                case "RESIGN":
                    if (tokens.length == 2) {
                        String id = tokens[1];
                        resignUser(id);
                    } else {
                        sendMessageToClient("RESIGN_FAIL_INVALID_INPUT");
                    }
                    break;

                case "UPDATE_NICKNAME":
                    if (tokens.length == 3) {
                        String id = tokens[1];
                        String newNickname = tokens[2];
                        updateNickname(id, newNickname);
                    } else {
                        sendMessageToClient("UPDATE_NICKNAME_FAIL_INVALID_INPUT");
                    }
                    break;

                case "UPDATE_EMAIL":
                    if (tokens.length == 3) {
                        String id = tokens[1];
                        String newEmail = tokens[2];
                        updateEmail(id, newEmail);
                    } else {
                        sendMessageToClient("UPDATE_EMAIL_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_USER_LIST":
                    sendUserListToClient();
                    break;

                case "ASK_LLM":
                    if (tokens.length > 1) {
                        // "ASK_LLM" 명령 뒤의 질문 내용을 합쳐 전달
                        String question = message.substring(message.indexOf(" ") + 1);
                        askLLM(question);
                    } else {
                        sendMessageToClient("ASK_LLM_FAIL_INVALID_INPUT");
                    }
                    break;

                case "CREATE_ROOM":
                    if (tokens.length >= 3) {
                        String userId = tokens[1];
                        String roomName = message.substring(message.indexOf(tokens[2])); // 채팅방 이름 전체를 가져오기
                        createRoom(userId, roomName);
                    } else {
                        sendMessageToClient("CREATE_ROOM_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_CHAT_ROOM_LIST":
                    sendChatRoomListToClient();
                    break;

                case "JOIN_CHAT_ROOM":
                    if (tokens.length == 3) { // 명령어 + 사용자 ID + 채팅방 이름
                        String roomName = tokens[1];
                        String userId = tokens[2];
                        joinChatRoom(userId, roomName);
                    } else {
                        sendMessageToClient("JOIN_CHAT_ROOM_FAIL_INVALID_INPUT");
                    }
                    break;

                case "SEND_MESSAGE":
                    if (tokens.length >= 4) {
                        String roomName = tokens[1];
                        String userId = tokens[2];
                        String chatMessage = message.substring(message.indexOf(tokens[3]));
                        databaseManager.saveMessageToDatabase(roomName, userId, chatMessage);
                    } else {
                        sendMessageToClient("SEND_MESSAGE_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_RECENT_CHAT":
                    if (tokens.length == 3) { // 명령어 + 채팅방 이름 + 메시지 개수
                        String roomName = tokens[1];
                        int limit;
                        try {
                            limit = Integer.parseInt(tokens[2]); // 요청한 메시지 개수
                        } catch (NumberFormatException e) {
                            sendMessageToClient("GET_RECENT_CHAT_FAIL_INVALID_LIMIT");
                            break;
                        }
                        sendRecentChatHistoryToClient(roomName, limit);
                    } else {
                        sendMessageToClient("GET_RECENT_CHAT_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_ROOM_USERS": // 채팅방 유저 목록 처리
                    if (tokens.length == 2) {
                        String roomName = tokens[1];
                        sendRoomUserListToClient(roomName);
                    } else {
                        sendMessageToClient("GET_ROOM_USERS_FAIL_INVALID_INPUT");
                    }
                    break;

                case "LEAVE_CHAT_ROOM":
                    if (tokens.length == 3) { // 명령어 + 채팅방 이름 + 유저 ID
                        String roomName = tokens[1];
                        String userId = tokens[2];
                        leaveChatRoom(roomName, userId);
                    } else {
                        sendMessageToClient("LEAVE_CHAT_ROOM_FAIL_INVALID_INPUT");
                    }
                    break;

                case "DELETE_CHAT_ROOM":
                    if (tokens.length == 2) { // 명령어 + 채팅방 이름
                        String roomName = tokens[1];
                        deleteChatRoom(roomName);
                    } else {
                        sendMessageToClient("DELETE_CHAT_ROOM_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_CHAT_ROOM_DETAILS":
                    if (tokens.length == 2) { // 명령어 + 채팅방 이름
                        String roomName = tokens[1];
                        sendChatRoomDetails(roomName);
                    } else {
                        sendMessageToClient("GET_CHAT_ROOM_DETAILS_FAIL_INVALID_INPUT");
                    }
                    break;

                case "DRAW_LINE":
                    if (tokens.length == 9) {
                        String roomName = tokens[1];
                        String userId = tokens[2];
                        int startX = Integer.parseInt(tokens[3]);
                        int startY = Integer.parseInt(tokens[4]);
                        int endX = Integer.parseInt(tokens[5]);
                        int endY = Integer.parseInt(tokens[6]);
                        int colorRGB = Integer.parseInt(tokens[7]);
                        int strokeWidth = Integer.parseInt(tokens[8]);

                        databaseManager.saveDrawingDataToDatabase(roomName, userId, startX, startY, endX, endY, colorRGB, strokeWidth);
                    } else {
                        sendMessageToClient("DRAW_LINE_FAIL_INVALID_INPUT");
                    }
                    break;

                case "GET_DRAWING_DATA":
                    if (tokens.length == 3) {
                        String roomName = tokens[1];
                        int lastDrawingId = Integer.parseInt(tokens[2]);
                        sendDrawingDataToClient(roomName, lastDrawingId);
                    } else {
                        sendMessageToClient("GET_DRAWING_DATA_FAIL_INVALID_INPUT");
                    }
                    break;

                case "CLEAR_DRAWING_DATA":
                    if (tokens.length == 2) { // 명령어 + 채팅방 이름
                        String roomName = tokens[1];
                        clearDrawingData(roomName);
                    } else {
                        sendMessageToClient("CLEAR_DRAWING_DATA_FAIL_INVALID_INPUT");
                    }
                    break;

                default:
                    sendMessageToClient("Unknown command: " + command);
                    break;
            }
        }
    }

    // 사용자 등록
    private void registerUser(String name, String email, String id, String pw) throws IOException, SQLException {
        boolean isNameDuplicate = databaseManager.isNameDuplicate(name);
        boolean isEmailDuplicate = databaseManager.isEmailDuplicate(email);
        boolean isIdDuplicate = databaseManager.isIdDuplicate(id);

        if (isNameDuplicate) {
            sendMessageToClient("REGISTER_FAIL_DUPLICATE_NAME");
        } else if (isEmailDuplicate) {
            sendMessageToClient("REGISTER_FAIL_DUPLICATE_EMAIL");
        } else if (isIdDuplicate) {
            sendMessageToClient("REGISTER_FAIL_DUPLICATE_ID");
        } else {
            boolean isSuccess = databaseManager.registerUser(name, email, id, pw);

            if (isSuccess) {
                sendMessageToClient("REGISTER_SUCCESS");
            } else {
                sendMessageToClient("REGISTER_FAIL");
            }
        }
    }

    // 사용자 로그인
    private void loginUser(String id, String password) throws IOException {
        boolean userExists = databaseManager.doesUserExist(id);
        if (!userExists) {
            sendMessageToClient("LOGIN_ERROR_USER_NOT_FOUND");
            return;
        }

        boolean isLoginSuccessful = databaseManager.checkLogin(id, password);

        if (isLoginSuccessful) {
            // 로그인 성공 시 status를 ONLINE으로 업데이트
            boolean statusUpdated = databaseManager.updateUserStatus(id, "ONLINE");

            if (statusUpdated) {
                String userName = databaseManager.getUserName(id);
                sendMessageToClient("LOGIN_SUCCESS " + userName);
            } else {
                sendMessageToClient("LOGIN_ERROR_STATUS_UPDATE_FAIL");
            }
        } else {
            sendMessageToClient("LOGIN_ERROR_INCORRECT_CREDENTIALS");
        }
    }

    private void logoutUser(String id) throws IOException {
        boolean userExists = databaseManager.doesUserExist(id);

        if (!userExists) {
            sendMessageToClient("LOGOUT_FAIL_USER_NOT_FOUND");
            return;
        }

        boolean statusUpdated = databaseManager.updateUserStatus(id, "OFFLINE");
    }


    private void updateNickname(String id, String newNickname) throws IOException, SQLException {
        // 닉네임이 빈칸인지 확인
        if (newNickname == null || newNickname.trim().isEmpty()) {
            sendMessageToClient("UPDATE_NICKNAME_FAIL_EMPTY");
            return;
        }

        boolean isNicknameDuplicate = databaseManager.isNameDuplicate(newNickname);

        if (isNicknameDuplicate) {
            sendMessageToClient("UPDATE_NICKNAME_FAIL_DUPLICATE");
        } else {
            boolean isUpdated = databaseManager.updateNickname(id, newNickname);
            if (isUpdated) {
                sendMessageToClient("UPDATE_NICKNAME_SUCCESS");
            } else {
                sendMessageToClient("UPDATE_NICKNAME_FAIL");
            }
        }
    }

    private void updateEmail(String id, String newEmail) throws IOException, SQLException {
        // 이메일이 빈칸인지 확인
        if (newEmail == null || newEmail.trim().isEmpty()) {
            sendMessageToClient("UPDATE_EMAIL_FAIL_EMPTY");
            return;
        }

        boolean isEmailDuplicate = databaseManager.isEmailDuplicate(newEmail);

        if (isEmailDuplicate) {
            sendMessageToClient("UPDATE_EMAIL_FAIL_DUPLICATE");
        } else {
            boolean isUpdated = databaseManager.updateEmail(id, newEmail);
            if (isUpdated) {
                sendMessageToClient("UPDATE_EMAIL_SUCCESS");
            } else {
                sendMessageToClient("UPDATE_EMAIL_FAIL");
            }
        }
    }

    // 비밀번호 변경
    private void changePassword(String id, String currentPassword, String newPassword) throws IOException, SQLException {
        // 현재 비밀번호가 올바른지 확인
        boolean isCurrentPasswordValid = databaseManager.verifyPassword(id, currentPassword);

        if (!isCurrentPasswordValid) {
            // 현재 비밀번호가 잘못된 경우
            sendMessageToClient("CHANGE_PASSWORD_INCORRECT_CURRENT");
            return; // 이후 로직 실행 방지
        }

        // 현재 비밀번호와 새 비밀번호가 동일한지 확인
        boolean isSameAsNewPassword = BCrypt.checkpw(newPassword, databaseManager.getCurrentPasswordHash(id));
        if (isSameAsNewPassword) {
            // 새 비밀번호와 현재 비밀번호가 동일한 경우
            sendMessageToClient("CHANGE_PASSWORD_EQUAL");
        } else {
            // 새 비밀번호를 데이터베이스에 저장
            boolean isPasswordChanged = databaseManager.changePassword(id, newPassword);

            if (isPasswordChanged) {
                // 비밀번호 변경 성공
                sendMessageToClient("CHANGE_PASSWORD_SUCCESS");
            } else {
                // 비밀번호 변경 실패
                sendMessageToClient("CHANGE_PASSWORD_FAIL");
            }
        }
    }

    private void resignUser(String id) throws IOException, SQLException {
        boolean userExists = databaseManager.doesUserExist(id);

        if (!userExists) {
            sendMessageToClient("RESIGN_FAIL_USER_NOT_FOUND");
            return;
        }

        boolean isResigned = databaseManager.deleteUser(id);

        if (isResigned) {
            sendMessageToClient("RESIGN_SUCCESS");
        } else {
            sendMessageToClient("RESIGN_FAIL");
        }
    }

    private void sendUserListToClient() {
        try {
            StringBuilder userList = new StringBuilder();
            List<String> users = databaseManager.getUserList();
            for (String user : users) {
                userList.append(user).append("\n");
            }
            userList.append("END_OF_LIST\n"); // 종료 신호 추가
            out.write(userList.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void askLLM(String question) throws IOException {
        // LLM 서버 URL
        String llmServerUrl = "http://localhost:5000/ask";
        String jsonInput = new JSONObject().put("question", question).toString();

        try {
            // HTTP POST 요청 생성
            URL url = new URL(llmServerUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // 질문 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // LLM 서버 응답 수신
            StringBuilder responseBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    responseBuilder.append(responseLine.trim());
                }
            }

            // 응답 처리
            JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
            String answer = jsonResponse.getString("answer");

            // 클라이언트로 응답 전송
            sendMessageToClient("ASK_LLM_SUCCESS " + answer);

        } catch (Exception e) {
            e.printStackTrace();
            sendMessageToClient("ASK_LLM_FAIL_SERVER_ERROR");
        }
    }

    private void createRoom(String userId, String roomName) throws IOException, SQLException {
        boolean isRoomNameDuplicate = databaseManager.isRoomNameDuplicate(roomName);

        if (isRoomNameDuplicate) {
            sendMessageToClient("CREATE_ROOM_FAIL_DUPLICATE_NAME");
        } else {
            boolean isRoomCreated = databaseManager.createChatRoom(userId, roomName);

            if (isRoomCreated) {
                sendMessageToClient("CREATE_ROOM_SUCCESS");
            } else {
                sendMessageToClient("CREATE_ROOM_FAIL");
            }
        }
    }

    private void sendChatRoomListToClient() {
        try {
            StringBuilder chatRoomList = new StringBuilder();
            List<String> rooms = databaseManager.getChatRoomList();
            for (String room : rooms) {
                chatRoomList.append(room).append("\n");
            }
            chatRoomList.append("END_OF_LIST\n"); // 종료 신호 추가
            out.write(chatRoomList.toString());
            out.flush();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void joinChatRoom(String userId, String roomName) throws IOException, SQLException {
        if (!databaseManager.isRoomNameDuplicate(roomName)) {
            sendMessageToClient("JOIN_CHAT_ROOM_FAIL_ROOM_NOT_FOUND");
            return;
        }

        if (databaseManager.isUserInRoom(userId, roomName)) {
            sendMessageToClient("JOIN_CHAT_ROOM_FAIL_ALREADY_IN_ROOM");
            return;
        }

        boolean isAdded = databaseManager.addUserToChatRoom(userId, roomName);
        if (isAdded) {
            sendMessageToClient("JOIN_CHAT_ROOM_SUCCESS " + roomName);
        } else {
            sendMessageToClient("JOIN_CHAT_ROOM_FAIL");
        }
    }

    private void sendRecentChatHistoryToClient(String roomName, int limit) {
        try {
            List<String> chatHistory = databaseManager.getRecentChatHistory(roomName, limit);
            for (String chat : chatHistory) {
                sendMessageToClient(chat); // 각 메시지를 클라이언트에 전송
                System.out.println(chat);
            }
            sendMessageToClient("END_OF_HISTORY"); // 메시지 전송 완료 신호
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("GET_RECENT_CHAT_FAIL_DATABASE_ERROR");
        }
    }

    private void sendRoomUserListToClient(String roomName) {
        try {
            List<String> userList = databaseManager.getRoomUserListWithTime(roomName);
            for (String user : userList) {
                sendMessageToClient(user); // 각 유저 정보 전송
            }
            sendMessageToClient("END_OF_USER_LIST"); // 목록 종료 신호
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("GET_ROOM_USERS_FAIL_DATABASE_ERROR");
        }
    }

    private void leaveChatRoom(String roomName, String userId) {
        try {
            boolean isRemoved = databaseManager.removeUserFromRoom(roomName, userId);
            if (isRemoved) {
                sendMessageToClient("LEAVE_CHAT_ROOM_SUCCESS");
            } else {
                sendMessageToClient("LEAVE_CHAT_ROOM_FAIL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("LEAVE_CHAT_ROOM_FAIL_DATABASE_ERROR");
        }
    }

    private void deleteChatRoom(String roomName) {
        try {
            databaseManager.deleteRoomWithDependencies(roomName);
            sendMessageToClient("DELETE_CHAT_ROOM_SUCCESS");
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("DELETE_CHAT_ROOM_FAIL_DATABASE_ERROR");
        }
    }

    private void sendChatRoomDetails(String roomName) {
        try {
            String[] details = databaseManager.getChatRoomDetails(roomName);
            if (details != null) {
                // 채팅방 이름과 생성일시 전송
                sendMessageToClient("CHAT_ROOM_NAME " + details[0]);
                sendMessageToClient("CHAT_ROOM_CREATE_TIME " + details[1]);
            } else {
                sendMessageToClient("GET_CHAT_ROOM_DETAILS_FAIL_NOT_FOUND");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("GET_CHAT_ROOM_DETAILS_FAIL_DATABASE_ERROR");
        } finally {
            // 데이터 전송 완료 메시지 추가
            sendMessageToClient("END_OF_DETAILS");
        }
    }

    private void sendDrawingDataToClient(String roomName, int lastDrawingId) {
        try {
            List<String> drawingData = databaseManager.getDrawingData(roomName, lastDrawingId);
            for (String data : drawingData) {
                sendMessageToClient(data);
            }
            sendMessageToClient("END_OF_DRAWING_DATA");
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("GET_DRAWING_DATA_FAIL_DATABASE_ERROR");
        }
    }

    private void clearDrawingData(String roomName) {
        try {
            boolean isCleared = databaseManager.clearDrawingData(roomName);
            if (isCleared) {
            } else {
                sendMessageToClient("CLEAR_DRAWING_DATA_FAIL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendMessageToClient("CLEAR_DRAWING_DATA_FAIL_DATABASE_ERROR");
        }
    }

    // 클라이언트에 메시지 전송
    private void sendMessageToClient(String message) {
        out.println(message);
        out.flush();
    }
}
