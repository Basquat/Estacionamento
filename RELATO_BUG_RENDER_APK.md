# Relato: Bug Render + APK + Cold Start

## Data
17/10/2026

## Contexto
O sistema roda no **Render Free Tier** e gera um APK automaticamente para celular. 

## Problema relatado
- Quando o frontend (servido junto com Spring Boot) fica aberto por muito tempo no Render, ele aparece "infinitamente aberto", mas **não funciona o backend**.
- Consequentemente, **não interliga com o banco de dados**.
- Quando eu abro o app no celular, ele **começa a abrir o Render** e, só assim, **passa a funcionar o site**.
- Mas, nesse momento, **o app não abre mais nunca**.
- Possuo **UptimeRobot** para tentar manter o Render aberto.

## Comportamento observado (situação real)
1. Render hiberna por inatividade (Free Tier).
2. Acessando `https://estacionamento-uers.onrender.com/`, o Spring retorna o `index.html` com `200 OK` ainda durante a inicialização.
3. UptimeRobot recebe 200 e considera "up", mesmo com backend/DB não prontos.
4. Ao abrir o APK, as requisições da API acordam a instância.
5. Durante o cold start, o WebView do APK tenta carregar e muitas vezes trava/timeout.
6. Após o Render aquecer, o site volta a responder normalmente, mas o APK fica num estado em que "não abre mais nunca".

## Causas identificadas
- **Render Free Tier hiberna** após inatividade. Primeiro request gera cold start (JVM + Spring + pool Hikari + conexão Postgres/Supabase).
- **Frontend servido pelo próprio Spring**: `src/main/resources/static/index.html` é estático. Bater em `/` não garante que controllers/services/DataSource estejam prontos.
- **Health check errado**: UptimeRobot monitorava `/` em vez de `/health`. O endpoint `/health` existe e responde rápido com `{"status":"ok",...}`.
- **Capacitor com `server.url`**: `mobile/capacitor.config.json` apontava para `https://estacionamento-uers.onrender.com`. Com isso, **o APK inteiro é carregado remotamente**. Qualquer lentidão no cold start trava o WebView.
- **Lazy initialization em prod**: `spring.main.lazy-initialization=true` adia criação de beans, o que torna o **primeiro request à API mais lento** (efeito percebido no mobile).
- **Porta**: Render injeta `PORT`. Embora Spring leia automaticamente, melhor explicitar `server.port=${PORT:8080}` para evitar variações.

## Arquivos analisados
- `src/main/resources/application.properties`
- `src/main/resources/application-prod.properties`
- `mobile/capacitor.config.json`
- `.github/workflows/android-build.yml`
- `src/main/resources/static/index.html` (lógica `API_ROOT`/`API_BASE` + SSE `/eventos`)
- Controllers: `AutoController`, `BlacklistController`, `EventoController`, `HealthController`, `ApkController`
- `Dockerfile` (expõe 8080, usa `JAVA_OPTS`, `SPRING_PROFILES_ACTIVE=prod`)

## Correções aplicadas (mais garantidas e simples)
### 1) `src/main/resources/application-prod.properties`
Adicionado:
```properties
# Render injeta PORT - garantir que escute a porta correta
server.port=${PORT:8080}
```
Mantido: `spring.main.lazy-initialization=true`, `spring.jpa.hibernate.ddl-auto=none`, pool Hikari otimizado, `spring.threads.virtual.enabled=true`.

### 2) `mobile/capacitor.config.json`
Antes:
```json
{
  "appId": "com.basquat.estacionamento",
  "appName": "Estaciona+",
  "webDir": "../src/main/resources/static",
  "server": {
    "url": "https://estacionamento-uers.onrender.com",
    "cleartext": false
  }
}
```
Depois:
```json
{
  "appId": "com.basquat.estacionamento",
  "appName": "Estaciona+",
  "webDir": "../src/main/resources/static",
  "server": {}
}
```
**Efeito:** APK abre **localmente** (arquivos embutidos). Todas as chamadas vão para a API via `<meta name="api-base">` injetado pelo workflow. Muito mais resiliente a cold start.

## Mudanças pendentes fora do código (necessárias)
- **Render Dashboard > Service > Settings > Health Check Path**: alterar para `/health`
- **UptimeRobot**: alterar URL monitorada para `https://estacionamento-uers.onrender.com/health` (ao invés de `/`)

## Deploy
- Commit: `7cbc23f` ("fix: ajustar porta do Render e capacitor para APK abrir local")
- Push: `origin/Main` — Render deve fazer deploy automático
- APK: gerar novo build via GitHub Actions `Build Android APK` (injetará `api-base` correto)

## Justificativa da escolha
Priorizei **simples + alta garantia**:
- Mudar healthcheck evita falso-positivo (maior causa do "Render parece vivo mas backend não responde").
- Remover `server.url` do Capacitor evita que o WebView fique preso esperando o Render acordar. Com app local + API remota, UI abre na hora e mostra loading enquanto busca dados.
- Explicitar `server.port=${PORT:8080}` é baixo risco e evita edge cases no Render.
- Não desativei `lazy-initialization` pra não aumentar tempo de boot desnecessariamente.

## Resultado esperado
- UptimeRobot só considera up quando `/health` retorna 200 (Spring realmente pronto).
- Ao abrir APK, ele carrega UI local instantaneamente e tenta conectar à API. Se Render estiver hibernado, a primeira chamada acorda; com UI já aberta, fica menos propenso a "não abre mais nunca".
- Site continua funcionando normalmente.
- Ciclo "abro app -> acorda Render -> site funciona mas app trava" reduz drasticamente.
