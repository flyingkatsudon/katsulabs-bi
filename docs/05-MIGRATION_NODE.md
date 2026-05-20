# Node.js 마이그레이션 가이드

`insight-board`를 **NestJS** (또는 Express + Zod)로 이전할 때의 가이드입니다.  
TypeScript를 쓰면 React 프론트와 **타입 공유**가 가능합니다.

---

## 1. 목표 아키텍처

```
bdp-node/
├── src/
│   ├── main.ts
│   ├── app.module.ts
│   ├── auth/                 # JWT module
│   ├── dashboard/            # CBoard 메타
│   ├── report/               # 인사이트
│   ├── domain/               # shared types
│   └── infrastructure/
│       └── prisma/           # 또는 TypeORM
├── prisma/
│   └── migrations/           # Flyway 대응
├── test/                     # e2e + unit
├── packages/
│   └── shared-types/         # React와 monorepo 공유
└── package.json
```

**권장 스택:** Node 22 LTS, NestJS 11, Prisma 6, `@nestjs/jwt`, `passport-jwt`, `class-validator`.

---

## 2. Monorepo (React + API 타입 공유)

```
bdp-monorepo/
├── apps/
│   ├── api/          # NestJS
│   └── web/          # React (Vite)
├── packages/
│   └── api-types/    # OpenAPI → typescript-fetch
└── pnpm-workspace.yaml
```

```bash
npx openapi-typescript http://localhost:8080/v3/api-docs -o packages/api-types/schema.d.ts
```

---

## 3. 레이어별 매핑

| Java | NestJS |
|------|--------|
| `@RestController` | `@Controller()` |
| `@Service` | `@Injectable()` provider |
| JPA Repository | Prisma Client / TypeORM Repository |
| `SecurityFilterChain` | `JwtAuthGuard` + `APP_GUARD` |
| Flyway | `prisma migrate` |
| `@WebMvcTest` | `@nestjs/testing` + `supertest` |

---

## 4. 인증 예시

```typescript
// auth/auth.service.ts
@Injectable()
export class AuthService {
  constructor(private prisma: PrismaService, private jwt: JwtService) {}

  async login(dto: LoginDto) {
    const user = await this.prisma.dashboardUser.findUnique({ where: { loginName: dto.username }});
    if (!user || !await bcrypt.compare(dto.password, user.userPassword)) throw new UnauthorizedException();
    return {
      accessToken: this.jwt.sign({ sub: user.userId }),
      refreshToken: this.jwt.sign({ sub: user.userId }, { expiresIn: '7d' }),
    };
  }
}
```

---

## 5. Prisma 스키마 (메타 테이블 예)

```prisma
model DashboardUser {
  userId       String   @id @map("user_id")
  loginName    String?  @map("login_name")
  userPassword String?  @map("user_password")
  boards       DashboardBoard[]
  @@map("dashboard_user")
}
```

`prisma db pull`로 기존 PostgreSQL에서 역생성 가능.

---

## 6. 복잡 SQL

- Prisma `$queryRaw`로 레거시 SQL 유지 (단기)
- 중기: Materialized View + Prisma read model
- `@prisma/client` extension으로 리포트 전용 client 분리

---

## 7. 실시간·대시보드

- WebSocket: `@nestjs/websockets` — near_realtime_stock_data 푸시
- Redis: `@nestjs/cache-manager` + `ioredis` (세션·캐시)

---

## 8. 테스트

```typescript
describe('AuthController (e2e)', () => {
  it('/api/v1/auth/login (POST)', () => {
    return request(app.getHttpServer())
      .post('/api/v1/auth/login')
      .send({ username: 'admin', password: 'admin' })
      .expect(201);
  });
});
```

- Unit: Jest, mock Prisma
- E2E: Testcontainers PostgreSQL

---

## 9. 배포

- `node:22-alpine` multi-stage build
- ECS: Java 대비 메모리 적으나 CPU-bound 리포트는 Worker 분리 권장
- **BFF 패턴:** React → NestJS → (내부) Java 레거시 병행 기간 가능

---

## 10. Java vs Node 선택 가이드

| 시나리오 | 권장 |
|----------|------|
| 금융 도메인·기존 Java 인력 | Java `insight-board` 유지 |
| 프론트 팀 중심·빠른 API 프로토타입 | Node monorepo |
| NLP/배치/크롤링 | Python 워커 + Node/Java API |

---

## 11. 이전 순서

1. OpenAPI + Nest scaffold (`nest g resource`)
2. Prisma migrate from Flyway SQL
3. Auth module
4. Dashboard CRUD
5. Report module (raw SQL)
6. React `apps/web` 연동
