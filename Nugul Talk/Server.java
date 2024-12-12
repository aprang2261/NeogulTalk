import Server.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Server {
    public static void main(String[] args) {
        int port = 1207;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("너굴톡 서버가 시작되었습니다. 포트: " + port);

            while (true) {
                System.out.println("클라이언트를 기다리는 중...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
