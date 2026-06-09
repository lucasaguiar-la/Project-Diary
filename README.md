# Project Diary

Diário de emoções pessoal. Permite criar anotações diárias associadas a um estado de humor e acompanhar, ao longo do tempo, como o usuário tem se sentido.

## Stack

| Camada | Tecnologia |
|---|---|
| Backend | Java 17, Spring Boot 3.4.x, Spring Data JPA |
| Banco de dados | PostgreSQL 16 |
| Frontend | Vue.js 2 (CDN), Bootstrap 5 |
| Proxy | nginx |
| Autenticação | JWT (JJWT 0.12.6), BCrypt |
| Containerização | Docker, Docker Compose |

## Arquitetura

```
Browser
  |
  v
nginx : 8420 (host)
  |-- /* ---------> arquivos estáticos (HTML, JS, CSS)
  |-- /api/* -----> backend : 8421 (interno)
                        |
                        v
                   PostgreSQL : 5432 (interno)
                   exposto em 5433 no host para acesso DBA
```

O backend nunca é exposto diretamente ao host. Todo o tráfego passa pelo nginx.

## Pré-requisitos

- Docker e Docker Compose
- Git

## Como rodar

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd Project-Diary
```

### 2. Configurar o `.env`

O arquivo `.env` já existe na raiz com valores de exemplo. Edite-o com os valores reais antes de subir:

```env
DB_NAME=meudiario
DB_USER=meudiario_user
DB_PASSWORD=sua_senha_segura
JWT_SECRET=string_aleatoria_com_minimo_32_caracteres
```

O `JWT_SECRET` precisa ter no mínimo 32 caracteres (requisito do algoritmo HMAC-SHA256).

### 3. Subir os serviços

```bash
docker compose up -d --build
```

Na primeira execução, os moods padrão (feliz, neutro, triste, ansioso, calmo) são inseridos automaticamente no banco.

### 4. Acompanhar os logs

```bash
docker compose logs -f backend
```

Aguarde a mensagem `Started DiaryApplication` antes de acessar a aplicação.

### 5. Acessar

```
http://localhost:8420
```

### Comandos úteis

```bash
# Derrubar os serviços mantendo os dados do banco
docker compose down

# Derrubar e apagar o volume do banco (dados perdidos)
docker compose down -v

# Rebuild de um serviço específico
docker compose up -d --build backend
```

## Endpoints da API

Todos os endpoints, exceto `/api/users/login` e `/api/users/register`, exigem o header:

```
Authorization: Bearer <token>
```

O token é obtido no login e armazenado no `localStorage` pelo frontend.

### Usuários

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/users/register` | Criar conta | Não |
| POST | `/api/users/login` | Autenticar e obter token | Não |

**Registro - corpo:**
```json
{
  "firstName": "Lucas",
  "lastName": "Aguiar",
  "email": "lucas@exemplo.com",
  "password": "senha123"
}
```

**Login - corpo:**
```json
{
  "email": "lucas@exemplo.com",
  "password": "senha123"
}
```

**Login - resposta:**
```json
{
  "token": "eyJ...",
  "userId": 1,
  "firstName": "Lucas",
  "lastName": "Aguiar"
}
```

### Notas

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/notes` | Criar nota |
| GET | `/api/notes/user/{userId}` | Listar notas do usuário |
| GET | `/api/notes/{id}` | Buscar nota por ID |
| PUT | `/api/notes/{id}` | Editar título e conteúdo |
| DELETE | `/api/notes/{id}` | Excluir nota |

**Criar nota - corpo:**
```json
{
  "title": "2 de junho de 2026",
  "content": "Hoje foi um bom dia.",
  "userId": 1,
  "moodIds": [1]
}
```

**Editar nota - corpo:**
```json
{
  "title": "2 de junho de 2026",
  "content": "Conteúdo atualizado."
}
```

### Moods

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/moods` | Listar moods disponíveis |
| GET | `/api/moods/user/{userId}` | Histórico de moods do usuário |
| GET | `/api/moods/user/{userId}?year=YYYY&month=MM` | Moods filtrados por mês |

### Atividades (Dashboard)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/activities/user/{userId}` | Listar atividades com status do dia |
| POST | `/api/activities` | Cadastrar nova atividade |
| DELETE | `/api/activities/{id}` | Remover atividade |
| POST | `/api/activities/{id}/complete?userId={userId}` | Marcar atividade como concluída hoje |
| DELETE | `/api/activities/{id}/complete/today?userId={userId}` | Desmarcar conclusão de hoje |
| GET | `/api/activities/user/{userId}/streak` | Retornar streak atual (dias consecutivos) |

**Cadastrar atividade - corpo:**
```json
{
  "title": "Meditar",
  "userId": 1
}
```

### Eventos de Agenda

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/events/user/{userId}?year=YYYY&month=MM` | Listar eventos de um mês |
| POST | `/api/events` | Criar evento |
| PUT | `/api/events/{id}` | Atualizar evento |
| DELETE | `/api/events/{id}` | Remover evento |

**Criar evento - corpo:**
```json
{
  "title": "Consulta médica",
  "description": "Retorno cardiologista",
  "eventDate": "2025-06-15",
  "eventTime": "14:00:00",
  "userId": 1
}
```

**Resposta de `/api/moods`:**
```json
[
  { "id": 1, "title": "feliz", "emoji": "😊" },
  { "id": 2, "title": "neutro", "emoji": "😐" },
  { "id": 3, "title": "triste", "emoji": "😢" },
  { "id": 4, "title": "ansioso", "emoji": "😟" },
  { "id": 5, "title": "calmo", "emoji": "😌" }
]
```

## Estrutura do projeto

```
Project-Diary/
├── backend/
│   └── Diary/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/main/java/com/meudiario/Diary/
│           ├── controller/       # endpoints REST
│           ├── database/         # DataInitializer (seed de moods)
│           ├── dto/              # objetos de request e response
│           ├── filter/           # JwtFilter
│           ├── model/            # entidades JPA
│           ├── repository/       # interfaces Spring Data
│           ├── service/          # regras de negócio
│           └── util/             # JwtUtil
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── index.html                # landing page
│   ├── screen/
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── notes.html            # mural de notas
│   │   ├── mood-history.html     # histórico de humor
│   │   ├── dashboard.html        # dashboard de atividades e ofensivo
│   │   └── agenda.html           # agenda com calendário e mood visual
│   └── scripts/
│       ├── login.js
│       ├── register.js
│       ├── notes.js
│       ├── mood-history.js
│       ├── dashboard.js
│       └── agenda.js
├── docker-compose.yml
└── .env
```

## Variáveis de ambiente

| Variável | Descrição | Obrigatório |
|---|---|---|
| `DB_NAME` | Nome do banco de dados | Sim |
| `DB_USER` | Usuário do banco | Sim |
| `DB_PASSWORD` | Senha do banco | Sim |
| `JWT_SECRET` | Chave secreta para assinar tokens JWT (mín. 32 chars) | Sim |

## Portas no host

| Serviço | Porta | Observação |
|---|---|---|
| Frontend | 8420 | Acesso principal da aplicação |
| PostgreSQL | 5433 | Acesso direto ao banco para administração |
| Backend | não exposta | Tráfego apenas via nginx internamente |

## Pendências conhecidas

- Erros de negócio no backend (e-mail duplicado, credenciais inválidas) retornam HTTP 500 em vez dos códigos semânticos corretos (409, 401). A correção requer um `@ControllerAdvice`.
- Não há verificação de ownership nas notas: um usuário autenticado pode acessar notas de outro usuário conhecendo o ID via URL.
- O humor "ansioso" está disponível no banco mas não aparece no seletor da interface de criação de notas.
