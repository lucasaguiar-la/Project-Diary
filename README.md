# Meu Diário

Meu Diário é uma aplicação fullstack de diário de emoções. Permite registrar anotações diárias associadas a um estado de humor, acompanhar padrões emocionais ao longo do tempo, manter hábitos com sequência de dias consecutivos, organizar compromissos num calendário e controlar a ingestão diária de água, tudo em um só lugar.

## Preview

<div style="max-width: 700px; overflow: hidden;">
  <img src="./frontend/images/homepage-preview.png" alt="Página inicial do Meu Diário" style="width: 100%; height: auto;">
</div>

## Tecnologias

- **Backend:** Java 17, Spring Boot 3.4, Spring Data JPA
- **Frontend:** Vue.js 2 (via CDN, sem build step), Bootstrap 5, Bootstrap Icons
- **Banco de dados:** PostgreSQL 16
- **Autenticação:** JWT (JJWT), BCrypt para hash de senha
- **E-mail:** Spring Mail (SMTP), usado no fluxo de recuperação de senha
- **Infraestrutura:** Docker, Docker Compose, nginx (proxy reverso e servidor de arquivos estáticos)

## Funcionalidades

- Cadastro, login e recuperação de senha por e-mail com token temporário
- Mural de emoções: anotações diárias associadas a uma de 8 emoções (feliz, amor, calma, surpresa, neutralidade, ansiedade, tristeza, raiva), cada uma com uma cor baseada em teoria das cores, legenda visual e filtro por humor
- Histórico de humor com cards coloridos por emoção, humor predominante e distribuição de humor no período
- Dashboard de hábitos: cadastro de atividades diárias, sequência de dias consecutivos ("Constância"), pendências do dia e total de hábitos cadastrados
- Agenda com calendário mensal, mostrando humor do dia, compromissos e dias em que a sequência de hábitos foi mantida
- Controle de hidratação: registro de garrafas bebidas no dia, meta diária configurável (em litros ou garrafas) e visualização em uma garrafa que enche conforme a meta é atingida
- Navegação por barra lateral fixa no desktop (expande ao passar o mouse) e menu colapsável no mobile
- Interface responsiva, testada em telas de smartphone, tablet e desktop

## Arquitetura

```
Navegador
  |
  v
nginx : 8420 (host)
  |-- /* ---------> arquivos estáticos (HTML, JS, CSS)
  |-- /api/* -----> backend : 8421 (interno)
                        |
                        v
                   PostgreSQL : 5432 (interno)
                   exposto em 5433 no host para acesso administrativo
```

O backend nunca é exposto diretamente ao host: todo o tráfego passa pelo nginx.

## Estrutura do projeto

```text
Project-Diary/
├── backend/
│   └── Diary/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/main/java/com/meudiario/Diary/
│           ├── controller/       # endpoints REST
│           ├── database/         # seed inicial de emoções
│           ├── dto/              # objetos de request e response
│           ├── filter/           # validação de JWT
│           ├── model/            # entidades JPA
│           ├── repository/       # interfaces Spring Data
│           ├── service/          # regras de negócio
│           └── util/             # geração e validação de token
├── frontend/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── index.html                # landing page
│   ├── images/
│   ├── style/
│   │   └── index.css             # estilos compartilhados por todas as páginas
│   ├── screen/
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── forgot-password.html
│   │   ├── reset-password.html
│   │   ├── notes.html            # mural de emoções
│   │   ├── mood-history.html     # histórico de humor
│   │   ├── dashboard.html        # hábitos e sequência de dias
│   │   ├── agenda.html           # calendário
│   │   └── water.html            # controle de hidratação
│   └── scripts/
│       ├── sidebar.js            # navegação compartilhada (injetada em todas as páginas autenticadas)
│       ├── mood-colors.js        # paleta de cores por emoção (compartilhada)
│       └── ...                   # um script por página, mesmo nome do HTML
├── docker-compose.yml
└── .env
```

## Configuração

Crie um arquivo `.env` na raiz do projeto com as variáveis abaixo:

```env
DB_NAME=meudiario
DB_USER=meudiario_user
DB_PASSWORD=sua_senha_segura
JWT_SECRET=string_aleatoria_com_minimo_32_caracteres

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu_email@gmail.com
MAIL_PASSWORD=sua_senha_de_app

FRONTEND_URL=http://localhost:8420
RESET_TOKEN_EXPIRATION_MINUTES=30
```

Notas:

- `JWT_SECRET` precisa ter no mínimo 32 caracteres (requisito do algoritmo HMAC-SHA256).
- As variáveis `MAIL_*` alimentam o envio de e-mail de recuperação de senha. Para testes, um serviço como Mailtrap ou Ethereal evita disparar e-mails reais.

## Como executar

### Via Docker (recomendado)

Sobe todos os serviços com um único comando. O nginx serve o frontend na porta 8420 e encaminha as requisições de API para o backend.

```bash
docker compose up -d --build
```

Acompanhe os logs até a mensagem `Started DiaryApplication`:

```bash
docker compose logs -f backend
```

Acesse: `http://localhost:8420`

Na primeira execução, as emoções padrão são inseridas automaticamente no banco.

```bash
# Derrubar os serviços mantendo os dados do banco
docker compose down

# Derrubar e apagar o volume do banco (dados perdidos)
docker compose down -v

# Rebuild de um serviço específico
docker compose up -d --build backend
```

### Desenvolvimento local

**Backend:**

```bash
cd backend/Diary
./mvnw spring-boot:run
```

**Frontend:**

O frontend não tem build step, é servido como arquivos estáticos. Para desenvolvimento local, basta um servidor HTTP simples apontando para a pasta `frontend/`:

```bash
cd frontend
python -m http.server 8888
```

## Endpoints principais da API

Todos os endpoints, exceto os de autenticação, exigem o header:

```
Authorization: Bearer <token>
```

| Recurso | Endpoints |
|---|---|
| Usuários | `POST /api/users/register`, `POST /api/users/login`, `POST /api/users/forgot-password`, `POST /api/users/reset-password` |
| Notas | `POST/GET/PUT/DELETE /api/notes`, `GET /api/notes/user/{userId}` |
| Emoções | `GET /api/moods`, `GET /api/moods/user/{userId}?year=&month=` |
| Hábitos | `GET/POST/DELETE /api/activities`, `POST /api/activities/{id}/complete`, `GET /api/activities/user/{userId}/streak`, `GET /api/activities/user/{userId}/completed-dates?year=&month=` |
| Agenda | `GET/POST/PUT/DELETE /api/events`, `GET /api/events/user/{userId}?year=&month=` |
| Água | `GET /api/water/user/{userId}`, `POST /api/water/increment`, `POST /api/water/decrement`, `GET /api/water/user/{userId}/history` |

## Limitações conhecidas

- Erros de negócio no backend (e-mail duplicado, credenciais inválidas) retornam HTTP 500 em vez dos códigos semânticos corretos (409, 401). A correção requer um `@ControllerAdvice` global.
- Não há verificação de ownership nas notas: um usuário autenticado pode acessar notas de outro usuário conhecendo o ID pela URL.
- A meta diária de água é armazenada apenas no navegador (localStorage), não sincroniza entre dispositivos.

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](./LICENSE) para mais detalhes.
