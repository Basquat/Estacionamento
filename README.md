<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&height=200&text=Estaciona%2B&fontColor=F59E0B&fontAlign=50&fontAlignY=38&fontSize=52&desc=Gest%C3%A3o%20de%20estacionamento%20%E2%80%94%20Web%20%2B%20Android&descAlign=50&descAlignY=60&descSize=16&descColor=94A3B8&color=0:0d0f14,100:1a1f2e" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-F59E0B?style=for-the-badge&logo=openjdk&logoColor=0d0f14" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-1a1f2e?style=for-the-badge&logo=springboot&logoColor=6EE7B7" />
  <img src="https://img.shields.io/badge/PostgreSQL%20(Supabase)-1a1f2e?style=for-the-badge&logo=postgresql&logoColor=5b9cf6" />
  <img src="https://img.shields.io/badge/React%2018-1a1f2e?style=for-the-badge&logo=react&logoColor=38BDF8" />
  <img src="https://img.shields.io/badge/Android%20(Capacitor)-1a1f2e?style=for-the-badge&logo=android&logoColor=6EE7B7" />
  <img src="https://img.shields.io/badge/Docker-1a1f2e?style=for-the-badge&logo=docker&logoColor=2496ED" />
  <img src="https://img.shields.io/badge/Arquitetura-Hexagonal-1a1f2e?style=for-the-badge&logoColor=6EE7B7" />
  <img src="https://img.shields.io/badge/Testes-61-1a1f2e?style=for-the-badge&logoColor=6EE7B7" />
</p>

---

## Visão geral

Sistema para controlar **entrada, cobrança e saída de veículos** em um estacionamento, usado direto do celular (app Android) ou do navegador. O backend em **Spring Boot** expõe uma API REST e **serve o próprio frontend** (React, sem etapa de build): um único artefato e um único deploy.

<!--
FOTOS: adicione os prints em .github/screenshots/ e descomente este bloco.
Dica: `python scripts/demo_server.py` abre a tela com dados fictícios em http://localhost:8099

<p align="center">
  <img src=".github/screenshots/inicio.png" width="260" />
  <img src=".github/screenshots/pagamento.png" width="260" />
  <img src=".github/screenshots/relatorios.png" width="260" />
</p>
-->

### O que ele faz

- **Cadastro rápido** de carros e motos (placa, valor, horário de entrada).
- **Confirmação de pagamento em gaveta** (celular) ou janela (desktop), com todas as informações do veículo num só lugar.
- **Pagamento dividido**: um veículo pode ser quitado em várias formas ao mesmo tempo (ex.: R$ 20 em dinheiro + R$ 5 no Pix).
- **Valor final flexível**: o valor recebido pode diferir do previsto na chegada, com aviso e registro dos dois.
- **Troco separado por método**: pagou em dinheiro e o troco saiu no Pix? Cada valor é abatido do caixa certo, e o Resumo mostra o "troco abatido" no cartão do método correspondente.
- **Foto do veículo**: tire a foto na própria gaveta (câmera ao vivo, com **flash**); ela vira o ícone do veículo na lista e abre em tela cheia ao tocar.
- **Blacklist** de placas que saíram sem pagar, com motivo e foto opcional.
- **Tempo real**: qualquer alteração aparece na hora em todos os aparelhos conectados (Server-Sent Events, sem polling).
- **Resumo e Relatórios** com totais por tipo, status e método de pagamento.
- **Tema claro/escuro** e export/import de backup em JSON.
- **App Android** gerado automaticamente por GitHub Actions, com ícone próprio e permissão de câmera.

---

## Qualidade

- **61 testes automatizados**: unitários (regras de negócio e mapeamento), fluxo completo da API via MockMvc e uma suíte de segurança (injeção de SQL, XSS, validação, IDOR, concorrência, adulteração de HTTP).
- **Arquitetura Hexagonal** com domínio em Java puro, DTOs separando a API do banco e tratamento de erros padronizado.
- **Deploy em Docker** no Render, com health check, e pipeline de build do APK no GitHub Actions.

---

## Arquitetura Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────────────┐
│                  FRONTEND (React 18) / APP ANDROID                  │
│                  src/main/resources/static/index.html               │
└────────────────────────────┬────────────────────────────────────────┘
                             │ REST + SSE
┌────────────────────────────▼────────────────────────────────────────┐
│                      INFRASTRUCTURE                                  │
│  Web (Controllers, CORS)  ·  Persistence (JPA Adapters)  ·  Events   │
└────────────────────────────┬────────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      APPLICATION                                     │
│  DTOs (Request/Response)  ·  Use Cases  ·  Ports  ·  Mappers         │
└────────────────────────────┬────────────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      DOMAIN (Java puro)                              │
│  Auto · Pagamento · BlacklistEntry · Exceções de negócio             │
│  Sem Spring, JPA ou qualquer framework                               │
└─────────────────────────────────────────────────────────────────────┘
```

| Princípio | Implementação |
|-----------|---------------|
| **Domínio isolado** | `domain/` não importa Spring nem JPA, só Java puro |
| **Inversão de dependência** | A aplicação define *Ports* (interfaces); a infraestrutura implementa os *Adapters* |
| **DTOs separam API e domínio** | Request/Response com validação; entidades de domínio sem anotações de framework |
| **Casos de uso explícitos** | `AutoServiceImpl` orquestra: valida → persiste → notifica |
| **Mapeamento sem reflection** | `Mapper` converte Domínio ↔ DTO de forma simples e testável |
| **Erros padronizados** | `GlobalExceptionHandler` responde `{"status":409,"message":"..."}` |

---

## Estrutura de pastas

```
Estacionamento/
├── .github/workflows/android-build.yml   # Gera o APK (Capacitor)
├── mobile/                               # Projeto do app Android (config + ícone)
├── scripts/demo_server.py                # Tela com dados fictícios (para prints)
├── src/main/java/basquat/estacionamento/
│   ├── application/   (dto, mapper, port/out, service)
│   ├── domain/        (model, exception)
│   └── infrastructure/
│       ├── event/                        # Broadcast SSE
│       ├── persistence/                  # adapter, entity, repository
│       ├── utils/exception/              # GlobalExceptionHandler
│       └── web/                          # controller, config (CORS)
├── src/main/resources/static/index.html  # Frontend React
├── src/test/                             # Unitários, fluxo completo, segurança
├── Dockerfile
└── pom.xml
```

---

## Modelo de dados

**`auto_model`**: `id`, `placa` (única), `tipo` (`carro` | `moto`), `valor` (valor final), `valor_previsto`, `pago`, `entrada` (epoch ms), `foto` (JPEG em base64, opcional).

**`pagamento`** (0..N por veículo): `id`, `auto_id`, `metodo_pagamento` (`dinheiro` | `pix` | `cartao`), `valor` (o que o cliente entregou), `troco_dinheiro`, `troco_pix`, `data`.

> O valor líquido de cada pagamento é `valor − troco`. O caixa de cada método é o que foi entregue nele menos o troco devolvido nele.

**`blacklist`**: `id`, `placa`, `motivo`, `foto` (opcional), `criado_em`.

---

## API

A API responde em `/` e também em `/api` (ex.: `/Automoveis` e `/api/Automoveis`). O app Android usa o prefixo `/api`.

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/Automoveis` | Lista veículos com pagamentos |
| `GET` | `/Automoveis/{id}` | Busca um veículo |
| `POST` | `/Automoveis/Add` | Cadastra veículo |
| `PUT` | `/Automoveis/{id}` | Atualiza veículo, pagamentos e foto (`foto` ausente mantém, `""` remove) |
| `DELETE` | `/Automoveis/{id}` | Remove veículo e seus pagamentos |
| `GET` | `/Blacklist` | Lista placas bloqueadas |
| `POST` | `/Blacklist/Add` | Adiciona à blacklist |
| `DELETE` | `/Blacklist/{id}` | Remove da blacklist |
| `GET` | `/eventos` | Stream SSE; evento `atualizacao` a cada mudança |
| `GET` | `/health` | `{"status":"ok","timestamp":<epoch>}` |
| `GET` | `/baixar-apk` | Baixa o APK mais recente |

**Erros**

```json
{"status": 404, "message": "Automóvel não encontrado"}
{"status": 409, "message": "A placa ABC-1234 já está cadastrada."}
{"status": 400, "message": "Erro de validação", "erros": {"placa": "A placa é obrigatória"}}
```

**Exemplo: `POST /Automoveis/Add`**

```json
{
  "placa": "ABC-1234",
  "tipo": "carro",
  "valor": 25.0,
  "pagamentos": [
    { "metodo": "dinheiro", "valor": 30.0, "trocoPix": 5.0 }
  ]
}
```

---

## Testes

```bash
./mvnw test                                           # tudo
./mvnw test -Dtest=AutoServiceTest,MapperTest         # rápidos, sem banco
./mvnw test -Dtest=FluxoCompletoMockMvcTest           # fluxo completo, sem abrir porta
./mvnw test -Dtest=SecurityAttackIntegrationTest      # sobe Tomcat + H2
```

| Suíte | Testes | Foco |
|-------|--------|------|
| `AutoServiceTest` | 7 | Regras de negócio (ports simulados) |
| `MapperTest` | 3 | Mapeamento Domínio ↔ DTO |
| `FluxoCompletoMockMvcTest` | 10 | Fluxo completo da API, prefixo `/api` e CORS |
| `ApiExceptionHandlerTest` | 2 | Formato padronizado de erros |
| `SecurityAttackIntegrationTest` | 38 | SQLi, XSS, validação, IDOR, concorrência, adulteração HTTP |
| `EstacionamentoApplicationTests` | 1 | Carga de contexto com H2 |

---

## Configuração

Nenhuma credencial fica no repositório. A aplicação lê, nesta ordem:

1. Variáveis de ambiente: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`.
2. `src/main/resources/application-local.properties` (ignorado pelo git), para rodar local.

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
# edite com as credenciais do pooler do Supabase (Project Settings → Database)
```

`DATABASE_URL`: `jdbc:postgresql://<host>.pooler.supabase.com:6543/postgres?prepareThreshold=0`

---

## Rodando local

**Pré-requisitos:** JDK 21 · Maven Wrapper incluso.

```bash
./mvnw spring-boot:run
```

Abra **http://localhost:8080**: frontend e API na mesma porta. Em dev, `ddl-auto=update` cria e ajusta as tabelas; em produção (`ddl-auto=none`) o schema é alterado à mão no Supabase.

---

## Deploy (Render + Docker)

`Dockerfile` multi-stage: build com Maven + Temurin 21, execução em `eclipse-temurin:21-jre`.

1. **New → Web Service**, conecte o repositório, runtime **Docker**.
2. **Environment**: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` e `SPRING_PROFILES_ACTIVE=prod`.
3. **Health Check Path:** `/health`.
4. O plano gratuito dorme após 15 min ocioso; aponte um [UptimeRobot](https://uptimerobot.com) para `GET /health` a cada 5 min.

---

## App Android (APK)

O workflow **Build Android APK** (`.github/workflows/android-build.yml`) empacota o sistema com [Capacitor](https://capacitorjs.com), com ícone próprio e permissão de câmera, e publica o arquivo em **Releases**.

- **Gerar:** em *Actions → Build Android APK → Run workflow* (ou a cada push na `Main`).
- **Baixar:** `/baixar-apk` no servidor ou o item *APK mais recente* em *Releases*.
- **Atualizar sem desinstalar:** o app abre a tela direto do servidor, então mudanças de tela aparecem sozinhas. Para APKs novos serem instalados por cima do anterior, cadastre o secret `ANDROID_KEYSTORE_B64` (chave de assinatura fixa em base64) em *Settings → Secrets and variables → Actions*.

---

## Autor

**João Daniel de Cerqueira Lisboa** · [GitHub @Basquat](https://github.com/Basquat) · [LinkedIn](https://www.linkedin.com/in/jo%C3%A3o-daniel-de-cerqueira-lisboa-0184a7356)
