# Deploy: Project Diary

Guia de deploy para o domínio `diary.lucasaguiar.online`.

---

## Visão geral da infraestrutura

O projeto sobe três containers via Docker Compose na rede interna `diary-net`:

| Servico      | Porta interna | Porta no host | Acesso externo |
|--------------|---------------|---------------|----------------|
| Frontend     | 80            | **8420**      | Via Nginx      |
| Backend      | 8421          | (nenhuma)     | Somente interno |
| PostgreSQL   | 5432          | **5433**      | Opcional       |

O Nginx do servidor recebe as requisicoes de `diary.lucasaguiar.online` e repassa para `localhost:8420`. Dentro do container do frontend, o Nginx interno repassa `/api/` para o backend em `backend:8421`.

---

## 1. Evitando conflito de portas

Antes de subir os containers, verifique se as portas `8420` e `5433` estao livres no servidor:

```bash
ss -tlnp | grep -E '8420|5433'
```

Se alguma estiver em uso, altere o mapeamento no `docker-compose.yml`. O lado **esquerdo** do `:` e a porta do host:

```yaml
# Exemplo: trocando 8420 por 8425
ports:
  - "8425:80"   # frontend

# Exemplo: trocando 5433 por 5434
ports:
  - "5434:5432" # postgres
```

Se alterar a porta do frontend, lembre de atualizar a configuracao do Nginx do servidor tambem (secao 2).

---

## 2. Configurando o Nginx do servidor

O padrao no Debian/Ubuntu e criar um arquivo separado por aplicacao em `/etc/nginx/sites-available/` e ativar via symlink. **Nao edite o arquivo principal `/etc/nginx/nginx.conf`** se ele ja contem a linha `include /etc/nginx/sites-enabled/*;`.

### Criar o arquivo de configuracao

```bash
sudo nano /etc/nginx/sites-available/diary.lucasaguiar.online
```

Cole o conteudo abaixo:

```nginx
server {
    listen 80;
    server_name diary.lucasaguiar.online;

    location / {
        proxy_pass         http://localhost:8420;
        proxy_http_version 1.1;
        proxy_set_header   Host              $host;
        proxy_set_header   X-Real-IP         $remote_addr;
        proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto $scheme;
    }
}
```

### Ativar e recarregar

```bash
# Criar o symlink para ativar o site
sudo ln -s /etc/nginx/sites-available/diary.lucasaguiar.online \
           /etc/nginx/sites-enabled/

# Testar a sintaxe antes de recarregar
sudo nginx -t

# Recarregar o Nginx (sem derrubar conexoes ativas)
sudo systemctl reload nginx
```

### Adicionar SSL (recomendado)

Com o Certbot instalado, o proprio comando ja edita o arquivo de configuracao e renova o certificado automaticamente:

```bash
sudo certbot --nginx -d diary.lucasaguiar.online
```

Apos isso, o Certbot adiciona o bloco HTTPS e redireciona HTTP para HTTPS automaticamente.

---

## 3. Um arquivo Nginx por aplicacao ou tudo junto?

O padrao amplamente adotado e **um arquivo por aplicacao**. O arquivo `/etc/nginx/nginx.conf` nao e editado diretamente: ele apenas inclui outros arquivos:

```nginx
# Dentro de /etc/nginx/nginx.conf (nao edite esta parte)
include /etc/nginx/sites-enabled/*;
```

Cada aplicacao tem seu proprio arquivo em `sites-available/` e e ativada ou desativada com um symlink em `sites-enabled/`. Exemplo de servidor com tres aplicacoes:

```
/etc/nginx/
  nginx.conf                          <- arquivo principal, nao edite
  sites-available/
    lucasaguiar.online                <- site principal
    diary.lucasaguiar.online          <- este projeto
    outra-app.lucasaguiar.online      <- outra aplicacao
  sites-enabled/
    lucasaguiar.online -> ../sites-available/lucasaguiar.online
    diary.lucasaguiar.online -> ../sites-available/diary.lucasaguiar.online
```

Vantagens desta abordagem:
- Desativar um site e so remover o symlink, sem tocar em mais nada
- Cada arquivo e independente, sem risco de quebrar outros sites ao editar
- Facil de auditar e versionar

Alternativa para RHEL/CentOS/Fedora: nesses sistemas o diretorio e `/etc/nginx/conf.d/` e arquivos `.conf` sao incluidos automaticamente. O principio e o mesmo: um arquivo por aplicacao.

---

## 4. Configuracao do banco de dados

### Criar o arquivo .env

Crie o arquivo `.env` na raiz do projeto com as credenciais reais. O `docker-compose.yml` le este arquivo automaticamente:

```bash
# Na raiz do projeto
cp .env.example .env   # se existir exemplo
nano .env
```

Conteudo minimo:

```env
DB_NAME=meudiario
DB_USER=meudiario_user
DB_PASSWORD=senha_forte_aqui
JWT_SECRET=string_aleatoria_com_minimo_32_caracteres_aqui
```

Use um gerador para o JWT_SECRET:

```bash
openssl rand -hex 32
```

### Primeiro start: banco criado automaticamente

Na primeira execucao, o container do PostgreSQL cria o banco e o usuario automaticamente usando as variaveis `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` definidas no `docker-compose.yml`. O Hibernate com `ddl-auto=update` cria as tabelas na primeira conexao do backend.

```bash
docker compose up -d
```

### Verificar se o banco subiu corretamente

```bash
# Ver logs do postgres
docker compose logs postgres

# Acessar o banco diretamente
docker exec -it $(docker compose ps -q postgres) \
  psql -U meudiario_user -d meudiario
```

### Seguranca: nao expor o banco publicamente

Se nao precisar acessar o banco de fora do servidor, remova o mapeamento de porta do servico `postgres` no `docker-compose.yml`:

```yaml
# Remover ou comentar estas linhas do servico postgres:
ports:
  - "5433:5432"
```

O backend continua acessando o banco normalmente pela rede interna Docker. Expor a porta 5433 so e necessario para ferramentas de administracao externas como DBeaver ou TablePlus.

### Backups

O volume `postgres_data` armazena todos os dados. Para fazer backup:

```bash
# Dump completo
docker exec $(docker compose ps -q postgres) \
  pg_dump -U meudiario_user meudiario > backup_$(date +%Y%m%d).sql

# Restaurar
docker exec -i $(docker compose ps -q postgres) \
  psql -U meudiario_user meudiario < backup_20240101.sql
```

---

## Checklist de deploy

- [ ] Portas `8420` e `5433` livres no servidor (ou `docker-compose.yml` ajustado)
- [ ] Arquivo `.env` criado com credenciais reais
- [ ] `JWT_SECRET` gerado com `openssl rand -hex 32`
- [ ] `docker compose up -d` executado com sucesso
- [ ] `docker compose logs` sem erros criticos
- [ ] Arquivo `/etc/nginx/sites-available/diary.lucasaguiar.online` criado
- [ ] Symlink em `sites-enabled/` criado
- [ ] `nginx -t` sem erros
- [ ] `systemctl reload nginx` executado
- [ ] DNS `diary.lucasaguiar.online` apontando para o IP do servidor
- [ ] SSL configurado com Certbot
- [ ] Acesso ao banco restrito (porta 5433 nao exposta publicamente)
- [ ] Estrategia de backup definida
