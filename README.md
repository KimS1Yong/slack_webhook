# Java 학습 주의점 Slack 알림 봇

이 프로젝트는 JavaScript 및 Python을 배운 사람이 Java를 처음 배울때 주의해야 할 점을 Slack 채널에 정각마다 자동으로 알림으로 보내는 GitHub Actions 기반의 Slack 봇입니다.

## 📌 주요 기능
- 매시간 정각마다 실행
- JavaScript, Python과 Java의 차이점을 강조하는 메시지 전송
- GitHub Actions와 Slack Webhook을 사용하여 자동화

## 🛠 기술 스택
- Java 17 (Temurin)
- GitHub Actions
- Slack Webhook

## 📂 프로젝트 구조
```
├── .github/workflows/
│   ├── send-message.yml   # GitHub Actions 워크플로우 파일
├── Webhook.java           # Slack 메시지를 보내는 Java 프로그램
├── README.md              # 프로젝트 설명서
```

## ⚙️ GitHub Actions 설정
### 1. 환경 변수 설정
Slack Webhook URL 및 기타 API 키를 GitHub Secrets에 추가해야 합니다.

GitHub Repository의 **Settings → Secrets and variables → Actions**에서 다음 환경 변수를 추가하세요.

| Name                     | Description |
|--------------------------|-------------|
| `LLM2_API_KEY`          | AI IMAGE API Key |
| `LLM2_API_URL`          | AI IMAGE API URL |
| `LLM2_MODEL`            | AI IMAGE Model Name |
| `LLM2_IMAGE_TEMPLATE`   | AI Image Template |
| `LLM_API_KEY`           | AI API Key |
| `LLM_API_URL`           | AI API URL |
| `LLM_MODEL`             | AI Model Name |
| `LLM_PROMPT`            | 메시지 프롬프트 |
| `SLACK_WEBHOOK_TITLE`   | Slack 메시지 제목 |
| `SLACK_WEBHOOK_URL`     | Slack Webhook URL |


## 🚀 실행 방법
### 1. 로컬 실행
Java가 설치된 환경에서 다음 명령어를 실행하면 메시지가 전송됩니다.
```sh
javac Webhook.java
java Webhook
```

### 2. GitHub Actions에서 자동 실행
위의 GitHub Actions가 설정된 경우, **정각마다 자동 실행**되며 Slack으로 메시지를 보냅니다.

## ⚠️ Java 학습 시 주의할 점
### Java vs JavaScript vs Python 차이점
| 구분 | Java | JavaScript | Python |
|------|------|-----------|--------|
| 타입 시스템 | 정적 타입 (Strongly Typed) | 동적 타입 (Weakly Typed) | 동적 타입 (Strongly Typed) |
| 실행 환경 | JVM (컴파일 필요) | 브라우저, Node.js (인터프리터) | 인터프리터 |
| 문법 | 엄격한 문법, 세미콜론 필수 | 유연한 문법, 세미콜론 선택적 | 들여쓰기 기반 |
| 객체 지향 | 클래스 기반 OOP | 프로토타입 기반 OOP | 클래스 기반 OOP (but, 다중 패러다임 지원) |
| 비동기 처리 | 스레드 기반 | 이벤트 루프 기반 (async/await) | 멀티스레딩 지원 but GIL 존재 |

### 초보자가 주의해야 할 Java 개념
1. **자료형 명시 필요**: `int`, `double`, `String` 등 명확한 타입 선언 필요
2. **컴파일 과정 필수**: `javac`로 컴파일 후 실행해야 함 (`.class` 파일 생성)
3. **객체 지향 원칙 엄격**: 모든 메서드는 클래스 내에서 정의해야 함
4. **메모리 관리**: `new` 키워드 사용 시 객체 생성, 가비지 컬렉터 자동 실행
5. **예외 처리 필수**: `try-catch` 문법 필수 활용 (`throws` 선언 필요 가능성)

## 📌 참고 자료
- [Java 공식 문서](https://docs.oracle.com/en/java/)
- [GitHub Actions 공식 문서](https://docs.github.com/en/actions)
- [Slack API Webhooks](https://api.slack.com/messaging/webhooks)

---
이 프로젝트는 Java를 처음 배우는 사람들을 돕기 위해 제작되었습니다. 🚀

