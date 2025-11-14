# BitRoute 🔗

> Um serviço de compartilhamento de textos inspirado no Pastebin.com - Estudo de caso de System Design e Arquitetura de Software

## 📋 Sobre o Projeto

BitRoute é uma aplicação desenvolvida como estudo de caso para praticar conceitos de **System Design** e **Arquitetura de Software**, baseada no design do Pastebin.com. O projeto permite que usuários compartilhem blocos de texto através de links curtos gerados automaticamente, com suporte a expiração opcional e análise de acessos.

Este projeto foi desenvolvido seguindo os princípios de escalabilidade, alta disponibilidade e boas práticas de arquitetura distribuída.

## 🎯 Funcionalidades (a serem desenvolvidas durante o projeto)

- ✅ **Criação de Pastes**: Usuários podem criar pastes (blocos de texto) e receber um link curto gerado automaticamente
- ⏰ **Expiração Configurável**: Suporte para pastes sem expiração ou com tempo de expiração definido em minutos
- 📊 **Analytics**: Rastreamento de visualizações e estatísticas mensais de acesso
- 🗑️ **Limpeza Automática**: Job agendado para remoção de pastes expirados
- 🔒 **Segurança**: Integração com Spring Security
- 💾 **Armazenamento Distribuído**: Separação entre metadados (banco de dados) e conteúdo (storage service) para melhor escalabilidade.

## 🏗️ Arquitetura

### Componentes Principais

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────┐
│     Controllers (REST API)       │
│  - PasteWriteController          │
│  - PasteReadController           │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│         Services                 │
│  - PasteService                  │
│  - StorageService                │
│  - AnalyticsService              │
└──────┬──────────────────────────┘
       │
       ▼
┌──────────────┐    ┌──────────────┐
│    MySQL     │    │  RabbitMQ    │
│  (Metadata)  │    │  (Messages)  │
└──────────────┘    └──────────────┘
```

### Tecnologias Utilizadas

- **Backend Framework**: Spring Boot 3.5.7
- **Java**: 21
- **Banco de Dados**: MySQL
- **Message Broker**: RabbitMQ
- **Segurança**: Spring Security
- **ORM**: Spring Data JPA / Hibernate
- **Containerização**: Docker Compose
- **Build Tool**: Maven

## 📊 Design System

### Casos de Uso

1. **Criação de Paste**
   - Usuário envia um bloco de texto
   - Sistema gera um shortlink único (7 caracteres em Base62)
   - Conteúdo é armazenado no Storage Service
   - Metadados são salvos no banco de dados
   - Retorna o shortlink gerado

2. **Visualização de Paste**
   - Usuário acessa o shortlink
   - Sistema verifica se o paste existe e não está expirado
   - Busca o conteúdo no Storage Service
   - Posteriormente registra analytics de visualização
   - Retorna o conteúdo

3. **Limpeza de Pastes Expirados**
   - Job agendado executa periodicamente
   - Identifica pastes com expiração vencida
   - Remove metadados e conteúdo

### Estimativas de Capacidade

Baseado no design original do Pastebin:

- **Usuários**: 10 milhões
- **Escritas**: 10 milhões de pastes/mês (~4 writes/segundo)
- **Leituras**: 100 milhões de leituras/mês (~40 reads/segundo)
- **Ratio Leitura/Escrita**: 10:1
- **Armazenamento**: ~1.27 KB por paste
- **Capacidade**: ~450 GB em 3 anos, 360 milhões de shortlinks

### Geração de Shortlinks

O sistema utiliza o seguinte algoritmo:

1. Geração UUID v4 (128 bits de aleatoriedade)
2. Codificação em Base62 (a-zA-Z0-9)
3. Primeiros 7 caracteres = 62^7 = ~3.5 trilhões de combinações possíveis
4. Verificação de unicidade no banco de dados

## 🗂️ Estrutura do Projeto

```
bitroute/
├── src/
│   ├── main/
│   │   ├── java/com/moura/bitroute/
│   │   │   ├── config/
│   │   │   │   ├── CacheConfig.java
│   │   │   │   └── StorageConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── PasteReadController.java
│   │   │   │   └── PasteWriteController.java
│   │   │   ├── dto/
│   │   │   │   ├── CreatePasteRequest.java
│   │   │   │   ├── CreatePasteResponse.java
│   │   │   │   └── ViewPasteResponse.java
│   │   │   ├── jobs/
│   │   │   │   └── ExpiredPastesCleaner.java
│   │   │   ├── model/
│   │   │   │   └── Paste.java
│   │   │   ├── repository/
│   │   │   │   └── PasteRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AnalyticsService.java
│   │   │   │   ├── PasteService.java
│   │   │   │   └── StorageService.java
│   │   │   ├── utils/
│   │   │   │   └── Base62Encoder.java
│   │   │   └── BitRouteApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
├── compose.yaml
├── pom.xml
└── README.md
```

## 🚀 Como Executar

### Pré-requisitos

- Java 21 ou superior
- Maven 3.8+
- Docker e Docker Compose

### Executando o Projeto

1. **Clone o repositório**
```bash
git clone <repository-url>
cd bitroute
```

2. **Inicie os serviços de infraestrutura**
```bash
docker-compose up -d
```

Isso iniciará:
- MySQL na porta 3306
- RabbitMQ na porta 5672

3. **Execute a aplicação**
```bash
./mvnw spring-boot:run
```

Ou no Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

4. **Acesse a aplicação**
```
http://localhost:8080
```

### Configuração

As configurações principais estão em `application.properties`:

```properties
spring.application.name=BitRoute
spring.profiles.active=dev
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 📡 API Endpoints

### Criar um Paste

```http
POST /api/v1/paste
Content-Type: application/json

{
  "paste_contents": "Hello World!",
  "expiration_length_in_minutes": 60
}
```

**Resposta:**
```json
{
  "shortlink": "foobar"
}
```

### Visualizar um Paste

```http
GET /api/v1/paste?shortlink=foobar
```

**Resposta:**
```json
{
  "paste_contents": "Hello World!",
  "created_at": "2025-11-14T10:30:00",
  "expiration_length_in_minutes": 60
}
```

## 🗄️ Modelo de Dados

### Tabela `pastes`

| Campo                         | Tipo         | Descrição                                    |
|-------------------------------|--------------|----------------------------------------------|
| shortlink                     | CHAR(7)      | Chave primária, identificador único          |
| expiration_length_in_minutes  | INT          | Duração até expiração (null = sem expiração) |
| created_at                    | DATETIME     | Data e hora de criação                       |
| paste_path                    | VARCHAR(255) | Caminho do conteúdo no storage               |

## 🔧 Melhorias Futuras

- [ ] **Cache Layer**: Implementar Redis para cache de pastes populares
- [ ] **CDN**: Distribuição de conteúdo estático
- [ ] **Load Balancer**: Múltiplos servidores web
- [ ] **Sharding de Banco de Dados**: Para escalabilidade horizontal
- [ ] **Autenticação de Usuários**: Gerenciamento de pastes por usuário
- [ ] **Edição de Pastes**: Permitir edição de pastes existentes
- [ ] **Syntax Highlighting**: Suporte para diferentes linguagens de programação
- [ ] **API Rate Limiting**: Controle de taxa de requisições
- [ ] **Monitoramento**: Prometheus + Grafana
- [ ] **Object Storage**: Integração com S3/MinIO para armazenamento de conteúdo

## 📚 Referências

Este projeto foi desenvolvido seguindo princípios de System Design baseados em:

- [System Design Primer - Pastebin Design](https://github.com/donnemartin/system-design-primer)
- Conceitos de arquitetura distribuída e escalabilidade
- Padrões de design de sistemas de alta disponibilidade

## 📝 Licença

Este é um projeto educacional desenvolvido para fins de estudo e prática de conceitos de System Design.

## 👤 Autor

**Gustavo de Moura**