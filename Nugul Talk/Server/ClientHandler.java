package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;         // 클라이언트 소켓
    private BufferedReader in;          // 클라이언트로부터의 입력 스트림
    private PrintWriter out;            // 클라이언트로의 출력 스트림
    private MessageHandler messageHandler; // 메시지 처리 핸들러

    public ClientHandler(Socket clientSocket) throws IOException, SQLException {
        this.clientSocket = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.messageHandler = new MessageHandler(in, out, this);
    }

    @Override
    public void run() {
        try {
            String clientMessage;
            // 클라이언트 메시지 처리 루프
            while ((clientMessage = in.readLine()) != null) {
                messageHandler.processClientMessage(clientMessage);
            }
        } catch (IOException e) {
            System.err.println("클라이언트 통신 오류: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 리소스 정리
            closeResources();
        }
    }

    // 리소스 정리 메서드
    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("리소스 종료 오류: " + e.getMessage());
        }
    }
}
