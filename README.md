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
│     Controllers (REST API)      │
│  - PasteWriteController         │
│  - PasteReadController          │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│         Services                │
│  - PasteService                 │
│  - StorageService               │
│  - AnalyticsService             │
└──────┬──────────────────────────┘
       │
       ├──────────────┬─────────────┐
       │              │             │
       ▼              ▼             ▼
┌──────────────┐ ┌─────────┐ ┌──────────────┐
│    MySQL     │ │ Storage │ │   RabbitMQ   │
│  (Metadata)  │ │ Service │ │  (Analytics) │
└──────────────┘ └─────────┘ └──────────────┘
                 (File System/
                  Object Store)
```

### Tecnologias Utilizadas

- **Backend Framework**: Spring Boot 3.5.7
- **Java**: 21
- **Banco de Dados**: MySQL
- **ORM**: Spring Data JPA / Hibernate
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

1. Via requisição pega o IP + Timestamp
2. Codifica em Base62 (a-zA-Z0-9)
3. Primeiros 7 caracteres = 62^7 = ~3.5 trilhões de combinações possíveis
4. Verificação de unicidade no banco de dados

### Sistema de Analytics - Decisões de Design

#### 🎯 Abordagem: Contadores Agregados vs. Registros Detalhados

O sistema de analytics foi projetado utilizando **contadores agregados mensais** em vez de registros individuais por visualização. Esta decisão foi tomada considerando os seguintes trade-offs:

**❌ Abordagem NÃO adotada: Registro detalhado por visualização**
- Um registro por acesso contendo: `shortlink`, `viewed_at`, `ip_address`, `user_agent`, `year_month`
- **Problemas:**
  - Alto volume de dados: ~100 milhões de registros/mês (baseado nas estimativas)
  - Crescimento linear do banco de dados
  - Custo de storage praticamente dobraria
  - Queries de agregação lentas (COUNT, SUM)
  - Maior complexidade de manutenção e backup

**✅ Abordagem adotada: Contadores agregados mensais**
- Um registro por paste por mês contendo: `shortlink`, `year_month`, `view_count`
- **Vantagens:**
  - Economia massiva de storage: ~360 registros em 3 anos por paste (vs. milhões)
  - Queries de analytics extremamente rápidas (leitura direta)
  - Operação atômica de incremento (`UPDATE ... SET view_count = view_count + 1`)
  - Escalabilidade horizontal facilitada
  - Menor overhead de I/O no banco de dados

**📊 Comparação de Volume de Dados (3 anos):**

| Abordagem              | Registros por paste | Storage estimado | Query performance |
|------------------------|---------------------|------------------|-------------------|
| Registro detalhado     | ~3.6 milhões        | ~450 MB          | Lento (agregação)|
| Contadores agregados   | ~36                 | ~4.5 KB          | Instantâneo      |

**🔄 Fluxo de Incremento:**
1. Evento de visualização é publicado no RabbitMQ (sem IP/User-Agent)
2. Consumer tenta incrementar contador do mês atual
3. Se registro não existe, cria novo com `view_count = 1`
4. Race condition tratada com verificação de duplicidade

**💡 Motivação para este estudo:**

Para os propósitos deste projeto educacional de **System Design**, dados agregados são suficientes para demonstrar:
- Arquitetura assíncrona com mensageria
- Escalabilidade e otimização de queries
- Trade-offs entre detalhamento de dados e performance

Em um cenário real de produção, dados detalhados (IP, user-agent, timestamp exato) seriam valiosos para:
- Detecção de fraudes e bots
- Análise de comportamento de usuários
- Geolocalização e dispositivos
- Compliance (LGPD/GDPR)

Porém, isso exigiria infraestrutura adicional como:
- Data lake ou data warehouse separado
- Pipeline de ETL
- Retenção com políticas de arquivamento
- Maior investimento em storage e processamento

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
- RabbitMQ na porta 15672

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
  "expiration_length_in_minutes": 60 // opcional
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

### Consultar Analytics de um Paste

**Total de visualizações:**
```http
GET /api/v1/analytics/{shortlink}/total
```

**Resposta:**
```json
{
  "shortlink": "foobar",
  "total_views": 1523
}
```

**Visualizações mensais:**
```http
GET /api/v1/analytics/{shortlink}/monthly
```

**Resposta:**
```json
{
  "shortlink": "foobar",
  "monthly_views": [
    {"month": 202601, "views": 450},
    {"month": 202602, "views": 1073}
  ]
}
```

**Visualizações de um mês específico:**
```http
GET /api/v1/analytics/{shortlink}/month/202602
```

**Resposta:**
```json
{
  "shortlink": "foobar",
  "year_month": 202602,
  "views": 1073
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

### Tabela `paste_analytics`

| Campo      | Tipo    | Descrição                                      |
|------------|---------|------------------------------------------------|
| id         | BIGINT  | Chave primária, auto incremento                |
| shortlink  | CHAR(7) | Referência ao paste (FK)                       |
| year_month | INT     | Mês/ano no formato YYYYMM (ex: 202602)         |
| view_count | BIGINT  | Contador de visualizações neste mês            |

**Índices:**
- Unique constraint em `(shortlink, year_month)` para garantir um registro por paste por mês
- FK index em `shortlink` para joins com tabela `pastes`

## 📊 Processamento de Analytics

### Arquitetura de Mensageria

O sistema utiliza **RabbitMQ** para processar analytics de forma assíncrona:

1. **Producer**: Quando um paste é visualizado, um evento `PasteViewEvent` é publicado no exchange
2. **Queue**: A mensagem é armazenada na queue `bitroute.analytics.queue` com TTL de 24 horas
3. **Consumer**: Um worker processa as mensagens assincronamente e incrementa contadores no banco
4. **Benefits**: 
   - Não impacta o tempo de resposta da visualização
   - Tolerância a falhas (mensagens persistentes)
   - Escalabilidade horizontal (múltiplos consumers)
   - Operações atômicas de incremento evitam inconsistências

### Fluxo de Analytics

```
Usuário visualiza paste
        ↓
PasteService retorna conteúdo
        ↓
Publica PasteViewEvent no RabbitMQ (async)
        ↓
Usuário recebe resposta imediata
        ↓
[Em paralelo]
Consumer processa evento
        ↓
AnalyticsService incrementa contador mensal
        ↓
UPDATE paste_analytics SET view_count = view_count + 1
```

### Operação de Incremento (Thread-Safe)

```java
// 1. Tenta incrementar contador existente
int updated = incrementViewCount(shortlink, yearMonth);

// 2. Se não existe, cria novo registro
if (updated == 0) {
    PasteAnalytics analytics = new PasteAnalytics();
    analytics.setViewCount(1L);
    save(analytics);
}
```

## 🔧 Melhorias Futuras

- [ ] **Object Storage**: Integração com S3/MinIO para armazenamento de conteúdo
- [ ] **Cache Layer**: Implementar Redis para cache de pastes populares
- [ ] **CDN**: Distribuição de conteúdo estático
- [ ] **Load Balancer**: Múltiplos servidores web
- [ ] **Sharding de Banco de Dados**: Para escalabilidade horizontal
- [ ] **Autenticação de Usuários**: Gerenciamento de pastes por usuário
- [ ] **Edição de Pastes**: Permitir edição de pastes existentes
- [ ] **API Rate Limiting**: Controle de taxa de requisições
- [ ] **Analytics Avançado**: Gráficos, geolocalização, dispositivos, etc

## 📚 Referências

Este projeto foi desenvolvido seguindo princípios de System Design baseados em:

- [System Design Primer - Pastebin Design](https://github.com/donnemartin/system-design-primer)
- Conceitos de arquitetura distribuída e escalabilidade
- Padrões de design de sistemas de alta disponibilidade

## 📝 Licença

Este é um projeto educacional desenvolvido para fins de estudo e prática de conceitos de System Design.

## 👤 Autor

**Gustavo de Moura**