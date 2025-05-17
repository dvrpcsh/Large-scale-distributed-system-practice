# 🚀 MAS 기반 대규모 분산 서비스

## 🧩 개요

이 프로젝트는 MAS(Microservice Agent System) 기반으로 설계된 **대규모 트래픽 처리 분산 시스템**입니다.  
고가용성과 유연한 확장성, 그리고 안정적인 사용자 경험을 제공하기 위해 다음과 같은 아키텍처와 기술 스택을 채택했습니다.

## 🏗️ 아키텍처 설계 개요

[ React(JSX, Tailwind) ]  
  │  
[Node.js API Gateway] -- (Reverse Proxy, 인증 헤더 확인)  
  │  
┌───▼─────────────┐  
│ Spring WebFlux │  ← 비동기 / 고부하 처리  
└────┬────────────┘  
    ▼  
[ Spring Boot Microservices ]  
 ├─ Auth Service (JWT + Redis + Token)  
 ├─ User Service (회원정보)  
 ├─ Transfer Service (계좌 및 송금 처리)  
 └─ History Service (내역 기록)  
  │  
 [ MySQL + JPA + Hibernate ]  
  │  
 [ Redis ] (Token 저장 및 블랙리스트)

## 🛠️ 기술 스택

| 계층       | 기술 스택                       | 선택 이유                                     |
|------------|----------------------------------|----------------------------------------------|
| Frontend   | React + JSX + Tailwind CSS       | 빠른 렌더링 + 컴포넌트 기반 개발 + 생산성 높은 스타일링 |
| API Gateway| Node.js (Express 기반)           | 빠른 라우팅 처리, Reverse Proxy 구성         |
| Backend    | Spring Boot + Spring Framework   | 대규모 트래픽 처리와 모듈화에 적합           |
| DB         | MySQL + JPA + Hibernate          | 안정성 + ORM의 생산성                        |
| 인증       | JWT + Redis                      | Stateless 인증 구조 + 토큰 관리 효율화       |
| 고부하 처리| Spring WebFlux                   | 비동기 / 논블로킹 방식으로 고성능 요청 처리  |
| 비동기 처리| Coroutine / WebClient            | 외부 I/O, API 호출 최적화                    |

## 🔐 인증 흐름

1. 로그인 시 access token과 refresh token 발급  
2. Redis에 refresh token과 함께 access token의 블랙리스트 관리  
3. 만료 시 refresh token으로 재발급, Redis 검증  
4. 로그아웃 시 access token을 Redis 블랙리스트로 이동  

## 🔄 설치 및 실행 방법

### 1. 백엔드

```bash
git clone https://github.com/your-org/backend-service.git
cd backend-service
./gradlew build
java -jar build/libs/*.jar
```

`.env` 또는 `application.yml`에 아래 환경변수 필요:

```yml
jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000
spring:
  redis:
    host: localhost
    port: 6379
```

### 2. 프론트엔드

```bash
cd frontend
npm install
npm run dev
```

## 📂 디렉토리 구조 예시 (Spring 기준)

```
src
├── auth
│   ├── controller
│   ├── service
│   ├── dto
│   ├── jwt
│   └── config
├── user
│   ├── controller
│   ├── service
│   ├── entity
│   └── repository
├── common
│   └── exception
│   └── response
```

## ✅ API 정의 예시

### [POST] /api/auth/login

- Request Body
```json
{
  "email": "test@domain.com",
  "password": "secure123!"
}
```

- Response
```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

## 🧱 SOLID 원칙 적용 전략

- **Single Responsibility Principle**  
  서비스별 관심사를 분리하여 유지보수성과 확장성 향상

- **Open-Closed Principle**  
  인터페이스 기반 설계로 기능 추가는 가능하되, 기존 코드 수정 최소화

- **Liskov Substitution Principle**  
  부모 타입 대체 가능성 보장 (ex: Controller → Service → Interface)

- **Interface Segregation Principle**  
  필요한 인터페이스만 구현, 커플링 최소화

- **Dependency Inversion Principle**  
  구현체가 아닌 추상화에 의존. DI, IoC Container 활용

## 🚀 향후 계획

- Kafka 또는 RabbitMQ 기반 비동기 이벤트 처리 도입  
- OpenTelemetry + Prometheus + Grafana로 모니터링 구성  
- CI/CD 파이프라인 (GitHub Actions, Jenkins)

## ✨ 만든 이유

이 서비스는 **트래픽이 급격히 몰리는 상황에서도 버틸 수 있는 탄탄한 구조**를 가지기 위해 시작되었습니다.  
또한, 개발자 누구나 쉽게 구조를 이해하고 유지보수할 수 있도록 모듈화와 주석 중심의 개발 문화를 지향합니다.
