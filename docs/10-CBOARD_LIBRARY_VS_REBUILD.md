# CBoard “라이브러리”를 더 이상 쓰지 않게 되는가?

## 짧은 답

**예. 이 리빌드가 완료되면 TuiQiao/CBoard JAR·WAR·AngularJS 번들은 필요 없습니다.**

대신 없어지는 것은 **“CBoard라는 이름의 외부 패키지”**이고, 남는 것은 **CBoard가 하던 일(기능)** 입니다.

```text
┌─────────────────────────────────────────────────────────────┐
│  레거시:  CBoard OSS (JAR/WAR)  +  신한 커스텀  →  한 덩어리 WAR   │
├─────────────────────────────────────────────────────────────┤
│  리빌드:  dashboard 모듈(구 CBoard)  +  web 모듈(구 shinhan)      │
│           +  React UI  +  Spring Boot API                      │
│           →  CBoard “라이브러리” 의존 0                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 무엇을 “CBoard”라고 부를지

| 구분 | 레거시 | 리빌드 |
|------|--------|--------|
| **제품** | CBoard CE BI | BDP Dashboard (CBoard 호환) |
| **코드** | `org.cboard.*` 복사/포크 | `com.bdp` + `dashboard` 패키지 **자가 구현** |
| **UI** | AngularJS + AdminLTE | React + AdminLTE 스타일 |
| **의존** | fastjson, Spring 4, … | Jackson, Boot 3/4, JWT |
| **업스트림 JAR** | 없음(포크) | **사용 안 함** |

즉 **“CBoard를 쓴다”** = **대시보드·위젯·데이터셋·데이터소스 도메인을 쓴다**는 뜻이지, Maven에 `org.cboard:cboard`를 넣는다는 뜻이 아닙니다.

---

## 왜 라이브러리를 끼워 넣을 수 없는가

1. 업스트림은 **0.4.2 / Spring 4 / JDK 8 / AngularJS** — Boot 3/4와 공존 불가  
2. **“최신 CBoard 라이브러리”** artifact 없음  
3. 보안: fastjson, Aliyun mirror, vendor JS — [09-CBOARD_SECURITY_AUDIT.md](./09-CBOARD_SECURITY_AUDIT.md)

**유일한 안전한 경로:** API·JSON 스키마·화면 흐름만 레거시와 맞추고 **코드는 새로 작성**.

---

## 무엇을 레거시에서 “참고”만 하는가

- URL: `/cboard/dashboard/*`, `/cboard/commons/*`  
- 테이블: `dashboard_*`  
- JSON: `layout_json`, `data_json`, 위젯 `data` 구조  
- 화면: AdminLTE skin-blue, 메뉴 트리, Config 4분할

**복사하지 않는 것:** `org.cboard` Java 클래스, `webapp/cboard/lib/*.min.js`

---

## 신한(web)과의 관계

- **dashboard** = CBoard (메타 + 차트 + 보드)  
- **web** = 인사이트/리포트 (`com.shinhan`)  
- 서로 Maven 모듈로 분리, **dashboard가 web을 참조하지 않음**

CBoard를 전부 React로 옮겨도 **web은 별도 API** (`/api/v1/web/*` 또는 `/report/*` 호환).

---

## 결론

| 질문 | 답 |
|------|-----|
| CBoard 라이브러리를 꼭 써야 하나? | **아니오** |
| CBoard 기능이 필요 없어지나? | **아니오** — BI 메타·시각화는 BDP 핵심 |
| 이 작업의 정체는? | **CBoard의 재구현(replacement)**, not **dependency upgrade** |

전체 작업이 끝나면 레거시 `bdp` WAR의 CBoard 부분은 **아카이브**하고, 운영은 `bdp-next`만 유지하면 됩니다.
