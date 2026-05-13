# fintech-legacy-credit — Mini-Framework de Análise de Crédito

Aplicação de análise de crédito desenvolvida com Spring Boot 4.0.3, JPA, H2 Database e DevTools.
Integra o componente `validador-core` como dependência Maven externa, demonstrando os princípios de
Desenvolvimento Baseado em Componentes (CBD) e Inversão de Controle (IoC) nas abordagens White-box e Black-box.

---

## Tecnologias

- Java 21
- Spring Boot 4.0.3 (Web MVC, Data JPA, Validation)
- H2 Database (in-memory)
- Lombok
- JUnit 5 / Mockito
- [`validador-core`](../validador-core) — componente externo (dependência Maven local)

---

## Pré-requisitos

- Java 21
- Maven 3.8+

---

## Setup

### 1. Instalar o componente base no repositório local Maven

O `fintech-legacy-credit` depende do `validador-core`. É necessário instalá-lo primeiro:

```bash
cd ../validador-core
mvn clean install
```

### 2. Compilar e executar o framework

```bash
cd ../fintech-legacy-credit
mvn clean install
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

### 3. Executar a demo de IoC (standalone, sem Spring)

```bash
mvn exec:java -Dexec.mainClass="br.com.nogueiranogueira.aularefatoracao.demo.DemoIoC"
```

---

## Estrutura do Projeto

```
src/main/java/br/com/nogueiranogueira/aularefatoracao/
├── Main.java                          ← Spring Boot entry point
├── whitebox/
│   ├── ValidadorFinanceiroWhiteBox.java  ← Abstract class (Template Method)
│   └── ValidadorPix.java                 ← Concrete hook (regras do Pix)
├── blackbox/
│   ├── RegraValidacaoFinanceira.java     ← Interface de contrato (Strategy)
│   ├── RegraValidacaoCreditoDigital.java ← Implementação concreta
│   ├── MotorValidacaoBlackBox.java       ← Motor com DI via construtor
│   └── ValidadorBlackBoxFactory.java     ← Factory
├── demo/
│   └── DemoIoC.java                      ← Runner standalone — demonstra IoC
├── adapter/                           ← Template Method para bureaus externos
├── service/                           ← Serviços de domínio
├── strategy/                          ← Estratégias de análise de crédito
├── factory/                           ← Factories de análise
├── controller/                        ← REST endpoints
└── model/                             ← Entidades JPA
```

---

## Endpoints da API

### Analisar Solicitação de Crédito
```http
POST /api/solicitacoes/analisar
```
Parâmetros: `cliente`, `valor`, `score`, `negativado`, `tipoConta`

### Processar Lote
```http
POST /api/solicitacoes/processar-lote
Content-Type: application/json
```

### Outras rotas
- `GET /api/solicitacoes/por-cliente/{cliente}`
- `GET /api/solicitacoes/aprovadas`
- `GET /api/solicitacoes/por-periodo?inicio=...&fim=...`
- `GET /api/solicitacoes/saude`

### Console H2
`http://localhost:8080/api/h2-console` — JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, senha: vazia.

---

## Testes

```bash
mvn test
mvn verify            # testes de integração
```

---

## Regras de Negócio

**Pessoa Física (PF):** score > 500, não negativado, valor > R$5k exige score > 800.
**Pessoa Jurídica (PJ):** score > 500, não negativado, valor > R$50k exige score > 700.
**Pix (White-box):** documento válido + valor ≤ R$20.000.
**Crédito Digital (Black-box):** documento válido + R$100 ≤ valor ≤ R$50.000.

---

## Defesa Arquitetural

### 1. Diferença prática: Biblioteca (`validador-core`) vs Framework (`fintech-legacy-credit`)

A diferença fundamental está em **quem controla o fluxo de execução**.

O `validador-core` é uma **biblioteca**: ele fornece classes e serviços (Sealed Interfaces,
Records, `ServicoValidadorDocumento`, `ValidadorDocumentoFactory`) que o código consumidor
chama quando quiser, da forma que quiser. O controle do fluxo pertence inteiramente ao chamador.
Isso é visível no próprio `Main.java` do core — ele instancia o serviço e decide quando invocar
cada método.

O `fintech-legacy-credit` é um **framework**: ele define o esqueleto do algoritmo e chama o
código do desenvolvedor nos momentos que determina. Isso é explícito no Template Method:
`ValidadorFinanceiroWhiteBox.validar()` é `final` — o framework controla quando logar, auditar
e executar. A subclasse (`ValidadorPix`) apenas preenche o gancho `executar()`. O desenvolvedor
não escolhe quando seu código roda; o framework decide. Esse é o **Princípio de Hollywood**:
"Não nos ligue — nós ligaremos para você."

Na prática, a equipe sentiu que construir a biblioteca foi mais imediato: a lógica de validação
de CPF/CNPJ/SSN é determinista e autocontida. Construir o framework exigiu projetar pontos de
extensão de antemão — quais partes variam (hooks), quais são fixas (Template), quais dependências
entram de fora (DI). Esse exercício de separação antecipada é a essência do design de frameworks.

---

### 2. Por que a indústria prefere Black-box (Composição/Interfaces) sobre White-box (Herança)?

O ecossistema Spring é o exemplo mais eloquente: `@Service`, `@Repository`, `@Component` não
exigem herança de nenhuma classe base do framework. Você implementa uma interface ou simplesmente
anota uma classe POJO. O Spring injeta a dependência. Isso é Black-box puro.

A razão técnica é clara: **herança cria acoplamento estrutural forte**. Quando `ValidadorPix`
estende `ValidadorFinanceiroWhiteBox`, ela está acoplada à hierarquia de classes do framework —
qualquer mudança de assinatura na classe abstrata pode quebrar todas as subclasses. Em Java não
existe herança múltipla, então uma subclasse que precisasse estender dois frameworks ao mesmo
tempo ficaria encurralada.

Composição via interface resolve isso. `MotorValidacaoBlackBox` depende de
`RegraValidacaoFinanceira` — uma abstração. Qualquer classe que implemente esse contrato pode
ser injetada sem alterar o motor. Para testes, basta passar um mock. Para um novo produto (TED,
câmbio), basta implementar a interface. O motor nunca muda.

Além disso, **composição é substituível em runtime**; herança não. Um container DI como o Spring
pode injetar implementações diferentes com base em `@Qualifier`, `@Profile` ou configuração
externa. Isso é impossível com herança — a subclasse está fixada em tempo de compilação.

Em síntese: White-box maximiza reuso de comportamento via herança, mas ao custo de acoplamento e
rigidez. Black-box maximiza flexibilidade via composição de contratos. Sistemas que precisam
crescer, evoluir e ser testados isoladamente convergem invariavelmente para a abordagem Black-box.

---

### 3. Inversão de Controle: onde exatamente ela ocorre?

#### White-box — IoC via Template Method

O Template Method inverte o controle ao tornar `validar()` final e chamar `executar()` na subclasse.
A subclasse não decide quando é chamada — o framework decide.

```java
// ValidadorFinanceiroWhiteBox.java
public final boolean validar(Documento documento, double valor) {
    log("Iniciando validação [" + getClass().getSimpleName() + "] ...");
    auditar(documento, valor);
    return executar(documento, valor);   // ← IoC: o framework chama o código da subclasse
}

// ValidadorPix.java
@Override
protected boolean executar(Documento documento, double valor) {
    // A subclasse implementa o hook, mas não controla quando este método é invocado.
    // O controle do fluxo pertence ao framework (ValidadorFinanceiroWhiteBox.validar).
    ServicoValidadorDocumento.ResultadoValidacao resultado = servicoValidador.validar(documento);
    return resultado.valido() && valor > 0 && valor <= LIMITE_PIX;
}
```

#### Black-box — IoC via Injeção de Dependência (Constructor Injection)

O motor recebe a regra como parâmetro do construtor. O controle sobre **qual** implementação
é usada pertence ao chamador — nunca ao motor.

```java
// MotorValidacaoBlackBox.java
public class MotorValidacaoBlackBox {

    // ← IoC: a dependência é fornecida de fora; o motor não instancia a regra
    private final RegraValidacaoFinanceira regra;

    public MotorValidacaoBlackBox(RegraValidacaoFinanceira regra) {
        this.regra = regra;   // ← ponto exato da inversão
    }

    public boolean executar(Documento documento, double valor) {
        return regra.validar(documento, valor);  // motor delega; não sabe qual regra é
    }
}

// DemoIoC.java — o chamador controla qual regra é injetada
RegraValidacaoFinanceira regra = new RegraValidacaoCreditoDigital();
MotorValidacaoBlackBox motor   = new MotorValidacaoBlackBox(regra);  // ← montagem externa
motor.executar(cpfValido, 5_000.00);
```

Em ambos os casos, o código de alto nível (framework / motor) não depende de implementações
concretas — depende de contratos (classe abstrata no White-box, interface no Black-box).
Isso é a inversão: quem usa não instancia; quem instancia não usa.

---

## Dívidas Técnicas Identificadas

- `AnaliseCreditoService.analisarSolicitacao()` com complexidade ciclomática elevada (7+ níveis de condicional)
- Valores de regras hardcoded (5000, 800, 50000, 700) — deveriam ser externalizados via configuração
- `Date` depreciado substituído, mas verificação de dia da semana ainda acoplada a regras de negócio
- Ausência de Observability estruturada (apenas `System.out` nos módulos whitebox/blackbox da demo)

---

## Autores

Bernardo Castellani, Cesar Pisa, Mario Wilhelms, Matheus Morilla, Mayumi Bogoni
