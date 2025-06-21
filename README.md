# 너굴톡 (NeogulTalk) 🦦

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)
![Flask](https://img.shields.io/badge/Flask-000000?style=for-the-badge&logo=flask&logoColor=white)

**Java Socket 기반 실시간 멀티 채팅 애플리케이션**

</div>

## 📖 프로젝트 소개

너굴톡은 Java Socket을 기반으로 한 실시간 멀티 채팅 프로그램입니다. 클라이언트-서버 구조를 활용하여 다중 사용자가 동시에 접속하여 채팅할 수 있는 환경을 제공합니다. MySQL 데이터베이스를 통해 사용자 데이터와 채팅 기록을 관리하며, LLM(Large Language Model)을 통한 AI 도우미 기능도 포함하고 있습니다.

### 🎯 주요 목표
- 실시간 멀티 유저 간의 원활한 채팅 환경 구현
- 안정적이고 확장 가능한 채팅 시스템 구축
- 사용자 친화적인 인터페이스 제공
- AI 기반 도우미 기능으로 사용자 경험 향상

## 🚀 주요 기능

### 💬 채팅 기능
- **멀티 클라이언트 지원**: 다중 사용자가 서버에 동시 접속하여 채팅 가능
- **멀티 채팅방**: 여러 채팅방을 생성하고 관리할 수 있는 기능
- **실시간 메시지 전송**: Socket 통신을 통한 실시간 메시지 송수신
- **채팅 기록 저장**: MySQL 데이터베이스를 통한 메시지 영구 저장

### 👤 사용자 관리
- **회원가입/로그인**: 사용자 계정 생성 및 인증 시스템
- **프로필 관리**: 닉네임, 이메일, 비밀번호 변경 기능
- **접속 상태 관리**: 온라인/오프라인 상태 표시
- **계정 탈퇴**: 사용자 계정 삭제 기능

### 🎨 추가 기능
- **AI 도우미**: LLM을 활용한 질문-답변 서비스
- **이벤트 기반 구조**: 다양한 채팅 이벤트 처리 시스템
- **확장 가능한 설계**: 새로운 기능 추가를 위한 모듈화된 구조

## 🏗️ 시스템 아키텍처

```
NeogulTalk/
├── Nugul Talk/           # 메인 애플리케이션
│   ├── Client/          # 클라이언트 코드
│   │   ├── Form/        # GUI 폼 클래스들
│   │   ├── Event/       # 이벤트 처리 클래스들
│   │   └── Img/         # 이미지 리소스
│   ├── Server/          # 서버 코드
│   │   ├── ClientHandler.java
│   │   ├── DatabaseManager.java
│   │   └── MessageHandler.java
│   ├── Client.java      # 클라이언트 메인
│   └── Server.java      # 서버 메인
├── LLMServer/           # AI 도우미 서버
│   └── llamaApi.py
├── JavaLibrary/         # 외부 라이브러리
│   ├── iBCrypt/         # 암호화 라이브러리
│   ├── Jdbc Driver/     # MySQL JDBC 드라이버
│   └── Json/            # JSON 파싱 라이브러리
└── Document/            # 프로젝트 문서
```

## 🛠️ 기술 스택

### Backend
- **Java**: JDK 23 (메인 애플리케이션)
- **Socket Programming**: 실시간 통신
- **MySQL**: 데이터베이스 관리
- **JDBC**: 데이터베이스 연결

### AI 서버
- **Python**: LLM 서버
- **Flask**: REST API 서버
- **LM Studio**: 로컬 LLM 실행

### 라이브러리
- **jBCrypt**: 비밀번호 암호화
- **JSON**: 데이터 직렬화/역직렬화
- **MySQL Connector/J**: MySQL 연결 드라이버

## 📋 설치 및 실행

### 필수 요구사항
- **Java**: JDK 23 이상
- **MySQL**: 8.0 이상
- **Python**: 3.8 이상
- **LM Studio**: 로컬 LLM 실행용

### 1. 데이터베이스 설정

MySQL에서 다음 명령어를 실행하여 데이터베이스와 테이블을 생성합니다:

```sql
CREATE DATABASE neogultalk;
USE neogultalk;

-- 사용자 테이블
CREATE TABLE `users` (
  `name` varchar(45) COLLATE utf8mb4_bin NOT NULL,
  `email` varchar(45) COLLATE utf8mb4_bin NOT NULL,
  `id` varchar(45) COLLATE utf8mb4_bin NOT NULL,
  `pw` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `status` enum('ONLINE','OFFLINE') COLLATE utf8mb4_bin DEFAULT 'OFFLINE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 채팅방 테이블
CREATE TABLE `rooms` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `room_name` varchar(100) COLLATE utf8mb4_bin NOT NULL,
  `is_private` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 채팅방 사용자 관계 테이블
CREATE TABLE `room_users` (
  `room_id` int NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `room_users_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE,
  CONSTRAINT `room_users_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 메시지 테이블
CREATE TABLE `messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `id` varchar(45) COLLATE utf8mb4_bin NOT NULL,
  `message_text` text COLLATE utf8mb4_bin NOT NULL,
  `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`message_id`),
  KEY `room_id` (`room_id`),
  KEY `id` (`id`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- 그림 데이터 테이블
CREATE TABLE `drawings` (
  `drawing_id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `user_id` varchar(255) COLLATE utf8mb4_bin NOT NULL,
  `start_x` int NOT NULL,
  `start_y` int NOT NULL,
  `end_x` int NOT NULL,
  `end_y` int NOT NULL,
  `color_rgb` int NOT NULL,
  `stroke_width` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`drawing_id`),
  KEY `room_id` (`room_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `drawings_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  CONSTRAINT `drawings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61709 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```

### 2. LLM 서버 설정

1. LM Studio를 설치하고 `llama-3.2-korean-bllossom-3b` 모델을 다운로드
2. LM Studio에서 모델을 실행 (포트 1234)
3. Python 의존성 설치:

```bash
cd LLMServer
pip install flask python-dotenv openai
```

4. LLM 서버 실행:

```bash
python llamaApi.py
```

### 3. 메인 애플리케이션 실행

1. **서버 실행**:
```bash
cd "Nugul Talk"
javac -cp ".;../JavaLibrary/*" *.java Server/*.java Client/*.java Client/Form/*.java Client/Event/*.java
java -cp ".;../JavaLibrary/*" Server
```

2. **클라이언트 실행**:
```bash
java -cp ".;../JavaLibrary/*" Client
```

## 🎮 사용법

### 1. 서버 접속
- 클라이언트 실행 후 서버 IP 주소와 포트(1207) 입력
- 연결 성공 시 로그인 화면으로 이동

### 2. 계정 관리
- **회원가입**: 새로운 계정 생성 (닉네임, 이메일, ID, 비밀번호)
- **로그인**: 기존 계정으로 로그인
- **프로필 수정**: 닉네임, 이메일, 비밀번호 변경 가능
- **계정 탈퇴**: 계정 삭제 기능

### 3. 채팅 기능
- **로비**: 채팅방 목록 확인 및 입장
- **채팅방 생성**: 새로운 채팅방 생성
- **채팅**: 실시간 메시지 송수신
- **AI 도우미**: 채팅 중 AI에게 질문 가능

## 🔧 주요 클래스 설명

### 서버 측
- **`Server.java`**: 메인 서버 클래스, 클라이언트 연결 수락
- **`ClientHandler.java`**: 개별 클라이언트 연결 처리
- **`DatabaseManager.java`**: 데이터베이스 연결 및 쿼리 관리
- **`MessageHandler.java`**: 메시지 처리 및 라우팅

### 클라이언트 측
- **`Client.java`**: 클라이언트 메인 클래스
- **`IPForm.java`**: 서버 연결 설정 화면
- **`LoginForm.java`**: 로그인 화면
- **`RegisterForm.java`**: 회원가입 화면
- **`LobbyForm.java`**: 채팅방 목록 화면
- **`ChatRoomForm.java`**: 채팅방 화면

### 이벤트 처리
- **`LoginEvent.java`**: 로그인 이벤트 처리
- **`RegisterEvent.java`**: 회원가입 이벤트 처리
- **`CrEnterEvent.java`**: 채팅방 입장 이벤트 처리
- 기타 다양한 이벤트 클래스들

## 🔒 보안 기능

- **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 저장
- **SQL Injection 방지**: PreparedStatement 사용
- **사용자 인증**: 로그인 시 세션 관리

## 📈 성능 및 확장성

- **멀티스레딩**: 각 클라이언트 연결을 별도 스레드로 처리
- **데이터베이스 연결 풀**: 효율적인 DB 연결 관리
- **모듈화된 설계**: 새로운 기능 추가 용이

## 🐛 알려진 이슈

- 현재 버전에서는 한 번에 제한된 수의 클라이언트만 지원
- 네트워크 연결이 불안정할 때 재연결 기능 미구현

## 🔮 향후 개선 계획

- [ ] 웹소켓 지원으로 브라우저 클라이언트 추가
- [ ] 파일 전송 기능 구현
- [ ] 이모지 및 멀티미디어 메시지 지원
- [ ] 채팅방 비밀번호 기능
- [ ] 사용자 차단 기능
- [ ] 메시지 검색 기능

## 📝 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.

## 👥 개발자

- **개발 기간**: 2024/12/03 - 2024/12/10
- **프로젝트 유형**: Java Socket 기반 멀티 채팅 애플리케이션
