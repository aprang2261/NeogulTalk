package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class DatabaseManager implements AutoCloseable {
    private Connection connection;

    public DatabaseManager() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/neogulTalk";
        String user = "root";
        String password = "1234";

        this.connection = DriverManager.getConnection(url, user, password);
    }

    public boolean doesUserExist(String id) {
        String query = "SELECT id FROM Users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isNameDuplicate(String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // 중복 여부 반환
            }
        }
        return false;
    }

    public boolean isIdDuplicate(String id) {
        try {
            String query = "SELECT id FROM Users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailDuplicate(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // 중복 여부 반환
            }
        }
        return false;
    }

    public boolean registerUser(String name, String email, String id, String pw) {
        String hashedPassword = BCrypt.hashpw(pw, BCrypt.gensalt());
        String query = "INSERT INTO Users (name, email, id, pw) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, id);
            preparedStatement.setString(4, hashedPassword);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkLogin(String id, String pw) {
        String query = "SELECT pw FROM Users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("pw");
                    return BCrypt.checkpw(pw, storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserStatus(String id, String status) {
        String query = "UPDATE Users SET status = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0; // 업데이트 성공 여부 반환
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserName(String id) {
        String query = "SELECT name FROM Users WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verifyPassword(String id, String inputPassword) {
        try {
            String query = "SELECT pw FROM Users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                // 쿼리 실행 및 결과 처리
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHash = resultSet.getString("pw"); // 데이터베이스에 저장된 해시값
                        return BCrypt.checkpw(inputPassword, storedHash); // 입력한 비밀번호를 해시와 비교
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(String id, String newPassword) throws SQLException {
        String query = "UPDATE users SET pw = ? WHERE id = ?";
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, hashedPassword);
            statement.setString(2, id);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;  // 비밀번호 변경 성공 여부 반환
        }
    }

    public String getCurrentPasswordHash(String id) {
        try {
            String query = "SELECT pw FROM Users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("pw");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteUser(String id) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // 삭제 성공 여부 반환
        }
    }

    public boolean updateNickname(String id, String newNickname) throws SQLException {
        String query = "UPDATE users SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateEmail(String id, String newEmail) throws SQLException {
        String query = "UPDATE users SET email = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<String> getUserList() throws SQLException {
        List<String> userList = new ArrayList<>();
        String query = "SELECT name, status FROM Users";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String status = resultSet.getString("status");
                userList.add(name + " (" + status + ")");
            }
        }
        return userList;
    }

    public boolean isRoomNameDuplicate(String roomName) throws SQLException {
        String query = "SELECT COUNT(*) FROM rooms WHERE room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            System.out.println(roomName);
            stmt.setString(1, roomName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public boolean createChatRoom(String userId, String roomName) throws SQLException {
        String query = "INSERT INTO rooms (room_name, created_at) VALUES (?, NOW())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public List<String> getChatRoomList() throws SQLException {
        String query = "SELECT room_name FROM rooms";
        List<String> chatRooms = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                chatRooms.add(rs.getString("room_name"));
            }
        }
        return chatRooms;
    }

    public boolean addUserToChatRoom(String userId, String roomName) throws SQLException {
        String roomIdQuery = "SELECT room_id FROM rooms WHERE room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(roomIdQuery)) {
            stmt.setString(1, roomName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int roomId = rs.getInt("room_id");

                    String insertQuery = "INSERT INTO room_users (room_id, user_id) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, roomId);
                        insertStmt.setString(2, userId);
                        int rowsAffected = insertStmt.executeUpdate();
                        return rowsAffected > 0; // 성공 여부 반환
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public boolean isUserInRoom(String userId, String roomName) throws SQLException {
        String query = "SELECT COUNT(*) FROM room_users ru " +
                "JOIN rooms r ON ru.room_id = r.room_id " +
                "WHERE ru.user_id = ? AND r.room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, roomName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // 존재 여부 반환
            }
        }
    }

    public void saveMessageToDatabase(String roomName, String userId, String message) throws SQLException {
        String query = "INSERT INTO messages (room_id, id, message_text) " +
                "SELECT room_id, ?, ? FROM rooms WHERE room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, message);
            stmt.setString(3, roomName);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to save message. Room might not exist.");
            }
        }
    }

    public List<String> getRecentChatHistory(String roomName, int limit) throws SQLException {
        List<String> chatHistory = new ArrayList<>();
        String query = "SELECT u.name, m.message_text, DATE_FORMAT(m.sent_at, '%m/%d %H:%i') AS sent_time " +
                "FROM messages m " +
                "JOIN users u ON m.id = u.id " +
                "JOIN rooms r ON m.room_id = r.room_id " +
                "WHERE r.room_name = ? " +
                "ORDER BY m.sent_at DESC " + // 최근 메시지부터 가져옴
                "LIMIT ?"; // 메시지 개수 제한

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String userName = rs.getString("name");
                    String messageText = rs.getString("message_text");
                    String sentTime = rs.getString("sent_time"); // 포맷된 날짜 및 시간
                    chatHistory.add("[" + sentTime + "] " + userName + ": " + messageText);
                }
            }
        }
        return chatHistory;
    }

    public List<String> getRoomUserListWithTime(String roomName) throws SQLException {
        List<String> userList = new ArrayList<>();
        String query = "SELECT u.name, DATE_FORMAT(ru.joined_at, '%m/%d %H:%i') AS joined_time " +
                "FROM users u " +
                "JOIN room_users ru ON u.id = ru.user_id " +
                "JOIN rooms r ON ru.room_id = r.room_id " +
                "WHERE r.room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String userName = rs.getString("name");
                    String joinedTime = rs.getString("joined_time");
                    userList.add(userName + " " + joinedTime);
                }
            }
        }
        return userList;
    }

    public boolean removeUserFromRoom(String roomName, String userId) throws SQLException {
        String query = "DELETE ru " +
                "FROM room_users ru " +
                "JOIN rooms r ON ru.room_id = r.room_id " +
                "WHERE r.room_name = ? AND ru.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            stmt.setString(2, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public void deleteRoomWithDependencies(String roomName) throws SQLException {
        // drawings, messages, room_users, rooms 테이블 삭제 쿼리
        String deleteDrawingsQuery = "DELETE d FROM drawings d " +
                "JOIN rooms r ON d.room_id = r.room_id " +
                "WHERE r.room_name = ?";
        String deleteMessagesQuery = "DELETE m FROM messages m " +
                "JOIN rooms r ON m.room_id = r.room_id " +
                "WHERE r.room_name = ?";
        String deleteRoomUsersQuery = "DELETE ru FROM room_users ru " +
                "JOIN rooms r ON ru.room_id = r.room_id " +
                "WHERE r.room_name = ?";
        String deleteRoomQuery = "DELETE FROM rooms WHERE room_name = ?";

        try (PreparedStatement deleteDrawingsStmt = connection.prepareStatement(deleteDrawingsQuery);
             PreparedStatement deleteMessagesStmt = connection.prepareStatement(deleteMessagesQuery);
             PreparedStatement deleteRoomUsersStmt = connection.prepareStatement(deleteRoomUsersQuery);
             PreparedStatement deleteRoomStmt = connection.prepareStatement(deleteRoomQuery)) {

            // drawings 삭제
            deleteDrawingsStmt.setString(1, roomName);
            deleteDrawingsStmt.executeUpdate();

            // messages 삭제
            deleteMessagesStmt.setString(1, roomName);
            deleteMessagesStmt.executeUpdate();

            // room_users 삭제
            deleteRoomUsersStmt.setString(1, roomName);
            deleteRoomUsersStmt.executeUpdate();

            // rooms 삭제
            deleteRoomStmt.setString(1, roomName);
            deleteRoomStmt.executeUpdate();
        }
    }

    public String[] getChatRoomDetails(String roomName) throws SQLException {
        String query = "SELECT room_name, DATE_FORMAT(created_at, '%Y년 %m월 %d일 %H시 %i분') AS created_time " +
                "FROM rooms WHERE room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("room_name");
                    String createdTime = rs.getString("created_time");
                    return new String[]{name, createdTime};
                }
            }
        }
        return null; // 채팅방이 존재하지 않을 경우
    }

    public void saveDrawingDataToDatabase(String roomName, String userId, int startX, int startY, int endX, int endY, int colorRGB, int strokeWidth) throws SQLException {
        String query = "INSERT INTO drawings (room_id, user_id, start_x, start_y, end_x, end_y, color_rgb, stroke_width, created_at) " +
                "SELECT r.room_id, ?, ?, ?, ?, ?, ?, ?, NOW() FROM rooms r WHERE r.room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setInt(2, startX);
            stmt.setInt(3, startY);
            stmt.setInt(4, endX);
            stmt.setInt(5, endY);
            stmt.setInt(6, colorRGB);
            stmt.setInt(7, strokeWidth);
            stmt.setString(8, roomName);
            stmt.executeUpdate();
        }
    }

    public List<String> getDrawingData(String roomName, int lastDrawingId) throws SQLException {
        String query = "SELECT d.drawing_id, d.start_x, d.start_y, d.end_x, d.end_y, d.color_rgb, d.stroke_width " +
                "FROM drawings d " +
                "JOIN rooms r ON d.room_id = r.room_id " +
                "WHERE r.room_name = ? AND d.drawing_id > ? " +
                "ORDER BY d.drawing_id ASC";
        List<String> drawingData = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, lastDrawingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    drawingData.add(
                            rs.getInt("drawing_id") + " " +
                                    rs.getInt("start_x") + " " +
                                    rs.getInt("start_y") + " " +
                                    rs.getInt("end_x") + " " +
                                    rs.getInt("end_y") + " " +
                                    rs.getInt("color_rgb") + " " +
                                    rs.getInt("stroke_width")
                    );
                }
            }
        }
        return drawingData;
    }

    public boolean clearDrawingData(String roomName) throws SQLException {
        String query = "DELETE d FROM drawings d " +
                "JOIN rooms r ON d.room_id = r.room_id " +
                "WHERE r.room_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, roomName);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0; // 삭제 성공 여부 반환
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
