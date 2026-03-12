# Fintech Legacy Credit - Análise de Crédito com Spring Boot

Aplicação de análise de crédito desenvolvida com Spring Boot 4.0.3, JPA, H2 Database e DevTools.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.0.3**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Validation
- **H2 Database** (Banco de dados em memória para testes e desenvolvimento)
- **Spring Boot DevTools** (Hot reload)
- **Lombok** (Redução de boilerplate)
- **JUnit 5** (Testes)
- **Mockito** (Mock de dependências)

## 📋 Pré-requisitos

- Java 21 instalado
- Maven 3.8+ instalado
- Git (opcional)

## 🔧 Configuração

### 1. Clonar ou extrair o projeto

```bash
cd fintech-legacy-credit
```

### 2. Instalar dependências

```bash
mvn clean install
```

### 3. Executar a aplicação

#### Opção 1: Via Maven
```bash
mvn spring-boot:run
```

#### Opção 2: Via IDE (IntelliJ IDEA)
1. Clique com botão direito em `Main.java`
2. Selecione "Run 'Main'"

#### Opção 3: Compilar e executar JAR
```bash
mvn clean package
java -jar target/fintech-legacy-credit-1.0-SNAPSHOT.jar
```

## 🌐 Acessar a Aplicação

A aplicação estará disponível em: **http://localhost:8080**

### Console H2 Database
Acesse o console do banco de dados em: **http://localhost:8080/api/h2-console**
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User**: `sa`
- **Password**: (deixe em branco)

## 📚 Endpoints da API

### 1. Analisar Solicitação de Crédito
```http
POST http://localhost:8080/api/solicitacoes/analisar
```

**Parâmetros:**
- `cliente` (String): Nome do cliente
- `valor` (Double): Valor solicitado
- `score` (Integer): Score de crédito (0-1000)
- `negativado` (Boolean, opcional): Cliente negativado? (padrão: false)
- `tipoConta` (String, opcional): PF ou PJ (padrão: PF)

**Exemplo:**
```bash
curl -X POST "http://localhost:8080/api/solicitacoes/analisar?cliente=João%20Silva&valor=5000&score=750&negativado=false&tipoConta=PF"
```

**Resposta:**
```json
{
  "cliente": "João Silva",
  "valor": 5000.0,
  "score": 750,
  "aprovado": true,
  "mensagem": "Solicitação aprovada"
}
```

### 2. Processar Lote de Solicitações
```http
POST http://localhost:8080/api/solicitacoes/processar-lote
Content-Type: application/json

["Cliente1", "Cliente2", "Cliente3"]
```

**Resposta:**
```json
{
  "mensagem": "Lote processado com sucesso",
  "totalClientes": "3"
}
```

### 3. Obter Solicitações por Cliente
```http
GET http://localhost:8080/api/solicitacoes/por-cliente/{cliente}
```

**Exemplo:**
```bash
curl "http://localhost:8080/api/solicitacoes/por-cliente/João%20Silva"
```

### 4. Obter Solicitações Aprovadas
```http
GET http://localhost:8080/api/solicitacoes/aprovadas
```

### 5. Obter Solicitações Reprovadas
```http
GET http://localhost:8080/api/solicitacoes/reprovadas
```

### 6. Obter Total de Aprovados por Tipo
```http
GET http://localhost:8080/api/solicitacoes/total-aprovados/{tipoConta}
```

**Exemplo:**
```bash
curl "http://localhost:8080/api/solicitacoes/total-aprovados/PF"
```

### 7. Obter Solicitações por Período
```http
GET http://localhost:8080/api/solicitacoes/por-periodo?inicio=2024-01-01T00:00:00&fim=2024-12-31T23:59:59
```

### 8. Health Check
```http
GET http://localhost:8080/api/solicitacoes/saude
```

**Resposta:**
```json
{
  "status": "ok",
  "mensagem": "Aplicação funcionando corretamente"
}
```

## 🧪 Executar Testes

### Testes Unitários
```bash
mvn test
```

### Testes de Integração
```bash
mvn verify
```

### Testes com Coverage
```bash
mvn clean test jacoco:report
```

## 📊 Estrutura do Projeto

```
fintech-legacy-credit/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/becastellani/aularefatoracao/
│   │   │       ├── Main.java (Spring Boot Application)
│   │   │       ├── controller/
│   │   │       │   └── SolicitacaoCreditoController.java
│   │   │       ├── service/
│   │   │       │   └── AnaliseCreditoService.java
│   │   │       ├── repository/
│   │   │       │   └── SolicitacaoCreditoRepository.java
│   │   │       └── model/
│   │   │           └── SolicitacaoCredito.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── br.com.nogueiranogueira.aularefatoracao.TestAnaliseCreditoService.java
│           └── SolicitacaoCreditoIntegrationTest.java
└── pom.xml
```

## 🔐 Regras de Negócio

### Critérios de Aprovação

**Pessoa Física (PF):**
- Score mínimo: 500
- Não pode estar negativado
- Se valor > R$ 5.000, score deve ser > 800
- Não aprovado em finais de semana (requer aprovação manual)

**Pessoa Jurídica (PJ):**
- Score mínimo: 500
- Não pode estar negativado
- Se valor > R$ 50.000, score deve ser > 700

## 📝 Logs

Os logs são configurados em diferentes níveis:
- **INFO**: Informações gerais da aplicação
- **DEBUG**: Informações detalhadas para debug
- **WARN**: Avisos e solicitações reprovadas
- **ERROR**: Erros de processamento

Veja `application.properties` para configurar os níveis de log.

## 🆘 Troubleshooting

### Porta 8080 em uso
```bash
# Mude a porta no application.properties
server.port=8081
```

### Erro de dependências Maven
```bash
mvn clean install -U
```

### Limpar cache do Maven
```bash
mvn clean install
```

## 📄 Licença

Este projeto é parte de um exercício de refatoração de código legado.

## 👨‍💻 Autor
Bernardo Castellani, Cesar Pisa, Mario Wilhelms, Matheus Morilla, Mayumi Bogoni

## Análise Técnica e Dívidas Técnicas

**AnaliseCreditoService.java:**
- Método `analisarSolicitacao()` com complexidade ciclomática alta - 7 níveis
- Aninhamento profundo de estruturas condicionais (if dentro de if dentro de if)
- Uso de `Date` depreciado (`new Date().getDay()`)
- Falta de separação de responsabilidades
- Hardcoded valores de regras de negócio (5000, 800, 50000, 700, etc.)
- Ausência de validação de entrada
- Lógica duplicada entre PF e PJ

**ProcessadorVendaService.java:**
- Verificar existência e implementação desta classe
- Regras que podem ser separadas
- Método `analisarSolicitacao()` com complexidade ciclomática alta - 7 níveis
- Separar em enums para tipos de conta
- Separar as 'palavras soltas' como imposto, valor, frete...