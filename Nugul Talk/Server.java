import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import Server.*;

public class Server {
    public static void main(String[] args) {

        // 너굴톡 서버 포트
        int port = 1207;

        // Llama API 서버 실행
        Process llamaProcess = startLlamaApi();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("너굴톡 서버가 시작되었습니다. 포트: " + port);

            while (true) {
                System.out.println("클라이언트를 기다리는 중...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다: " + clientSocket.getInetAddress());

                // 새로운 스레드로 클라이언트와 통신
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // Llama API 서버 종료
            if (llamaProcess != null) {
                llamaProcess.destroy();
                System.out.println("Llama API 서버가 종료되었습니다.");
            }
        }
    }

    // Llama API 서버 실행 메서드
    private static Process startLlamaApi() {
        try {
            System.out.println("Llama API 서버를 실행합니다...");

            // llamaApi.py 실행 경로
            String llamaApiPath = "python3 llamaApi.py";

            // 프로세스 빌더 생성
            ProcessBuilder processBuilder = new ProcessBuilder(llamaApiPath.split(" "));
            processBuilder.redirectErrorStream(true);

            // llamaApi.py 실행
            Process process = processBuilder.start();
            System.out.println("Llama API 서버가 실행되었습니다.");

            return process;

        } catch (IOException e) {
            System.err.println("Llama API 서버 실행 실패: " + e.getMessage());
            return null;
        }
    }
}
