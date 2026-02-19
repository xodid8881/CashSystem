# CashSystem

Paper 1.21.x 서버용 캐시/상점 플러그인입니다.  
플레이어 캐시 지급/회수/조회, GUI 기반 캐시상점, 다국어 메시지, SQLite/MySQL 저장을 지원합니다.

## 주요 기능
- 캐시 관리
  - `/캐시 확인 [플레이어]`로 잔액 조회
  - OP 전용: 지급/회수/설정
- 캐시상점 관리
  - 상점 생성/삭제
  - GUI에서 물품 배치
  - GUI+명령어로 물품 가격 설정
  - 플레이어 구매 확인 GUI 제공
- 데이터 저장
  - SQLite(기본) 또는 MySQL 선택 가능
  - 플레이어 접속 시 계정 자동 초기화
- 다국어 메시지
  - `message_kor.yml`, `message_eng.yml`
  - `config.yml`의 `language`로 언어 전환
- PlaceholderAPI 연동 (선택)
  - PlaceholderAPI 설치 시 `%cash_get%` 사용 가능

## 환경
- Java 21
- Paper API 1.21.10+
- Kotlin 기반 플러그인

## 설치
1. 릴리즈 jar를 `plugins/` 폴더에 넣습니다.
2. 서버를 1회 실행해 기본 설정 파일을 생성합니다.
3. 필요 시 `plugins/CashSystem/config.yml`에서 DB/언어를 수정합니다.
4. 서버를 재시작합니다.

## 명령어
### 일반 사용자
- `/캐시 확인 [플레이어]`
- `/캐시상점 열기 <상점명>`

### OP 전용
- `/캐시 지급 <플레이어> <금액>`
- `/캐시 회수 <플레이어> <금액>`
- `/캐시 설정 <플레이어> <금액>`
- `/캐시상점 생성 <상점명> <라인(1~5)>`
- `/캐시상점 삭제 <상점명>`
- `/캐시상점 물품설정 <상점명>`
- `/캐시상점 금액설정 <상점명>`
- `/금액 <금액>`

참고:
- 별도 permission 노드는 없고 OP 여부로 관리됩니다.
- `/금액`은 `금액설정` GUI에서 아이템 클릭 후에만 동작합니다.

## GUI 동작 흐름
1. OP가 `/캐시상점 생성`으로 상점을 만듭니다.
2. `/캐시상점 물품설정`에서 아이템을 배치하면 저장됩니다.
3. `/캐시상점 금액설정`에서 아이템 클릭 후 `/금액`으로 가격을 설정합니다.
4. 플레이어가 `/캐시상점 열기`로 상점을 열고 구매합니다.

## 설정 파일
### `config.yml`
- `language`: `kor` 또는 `eng`
- `database.type`: `sqlite` 또는 `mysql`
- `database.sqlite.file`: SQLite DB 파일명
- `database.mysql.*`: MySQL 연결 정보
- `database.migrate-player-yml`: 구버전 플레이어 캐시 마이그레이션 여부

### `CashSystem.yml`
- `cash-system.prefix`: 채팅 접두사
- `캐시상점.*`: 상점/물품/금액 데이터

### `Player.yml`
- 플레이어별 GUI 상태(페이지, 선택 슬롯, 설정 상태 등)

### `message_kor.yml`, `message_eng.yml`
- 메시지/GUI 타이틀/버튼/로어 문구 관리
- `%player%`, `%amount%`, `%shop%`, `%page%`, `%cash%` 치환 지원

## 데이터베이스
- 기본 테이블: `cash_balances`
  - `player_name VARCHAR(16) PRIMARY KEY`
  - `cash BIGINT NOT NULL DEFAULT 0`
- 음수 금액 저장 방지(`setCash`에서 0 미만 차단)

## PlaceholderAPI
- 식별자: `cash`
- 사용 가능한 플레이스홀더:
  - `%cash_get%` -> 대상 플레이어 캐시 반환

## 빌드
```bash
./gradlew build
```

Windows:
```powershell
.\gradlew.bat build
```
