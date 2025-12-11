# Fachada Painel CAGEPA

Sistema de gerenciamento de clientes, hidrometros e consumo de água para a CAGEPA (Companhia de Água e Esgotos da Paraíba).

## 📋 Visão Geral

O **Fachada Painel CAGEPA** é uma aplicação Java/Spring Boot que implementa diversos padrões de design para criar uma arquitetura robusta, escalável e bem organizada. O projeto funciona como uma fachada que integra múltiplos subsistemas (autenticação, gestão de clientes, processamento de imagens, notificações, auditoria, etc.).

---

## 🏗️ Padrões de Design Implementados

### 1. **FACADE** 🎭
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/facade/`

O padrão Facade fornece uma interface unificada para um conjunto de interfaces em um subsistema.

**Arquivos principais:**
- `PainelCagepaFacade.java` - Orquestra chamadas aos diferentes serviços
- `ConfigurationFacade.java` - Gerencia configurações do sistema

**Responsabilidades:**
- Simplificar a interação entre cliente e múltiplos serviços
- Centralizar a lógica de orquestração
- Abstrair a complexidade dos subsistemas

```
Fachada
├── ClienteService
├── AdminService
├── HidrometroService
├── ConsumoService
├── NotificacaoService
└── AuditService
```

---

### 2. **PROXY** 🔐
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/facade/proxy/`

O padrão Proxy fornece um substituto ou espaço reservado para outro objeto para controlar o acesso.

**Arquivos principais:**
- `SecurePainelCagepaFacadeProxy.java` - Proxy de segurança que envolve a Fachada
- `ISecurePainelCagepaFacadeProxy.java` - Contrato do proxy

**Responsabilidades:**
- Controlar acesso aos métodos da Fachada
- Validar autenticação/autorização
- Registrar logs de auditoria antes de operações
- Implementar transações de banco de dados

**Fluxo:**
```
Cliente (CLI)
    ↓
SecurePainelCagepaFacadeProxy (Validação + Auditoria)
    ↓
PainelCagepaFacade (Orquestração)
    ↓
Services + Repositórios
```

---

### 3. **COMMAND** 📝
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/command/`

O padrão Command encapsula uma requisição como um objeto, permitindo:

**Arquivos principais:**
- `Command.java` - Interface que define o contrato
- `CommandInvoker.java` - Executa e gerencia comandos
- `CreateClientPfCommand.java` - Implementação concreta

**Responsabilidades:**
- Registrar operações para auditoria
- Implementar histórico de undo/redo
- Fila de operações
- Execução adiada

**Benefícios:**
- Cada operação encapsulada em uma classe separada
- Rastreamento completo de execuções
- Suporte a desfazer operações

---

### 4. **STATE** 🔄
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/state/`

O padrão State permite que um objeto altere seu comportamento quando seu estado interno muda.

**Arquivos principais:**
- `ClienteState.java` - Interface que define estados possíveis
- `NovoClienteState.java` - Estado inicial
- `ValidadoClienteState.java` - Cliente validado
- `AprovadoClienteState.java` - Cliente aprovado
- `RejeitadoClienteState.java` - Cliente rejeitado
- `SuspensoClienteState.java` - Cliente suspenso
- `StateTransitionException.java` - Exceção de transição inválida

**Diagrama de Estados do Cliente:**
```
NOVO
  ├─→ VALIDADO
  │     └─→ APROVADO
  │           ├─→ SUSPENSO (pode retornar a APROVADO)
  │           └─→ REJEITADO
  └─→ REJEITADO
```

**Responsabilidades:**
- Encapsular comportamentos por estado
- Validar transições de estado
- Prevenir transições inválidas

---

### 5. **STRATEGY** 💡
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/strategy/`

O padrão Strategy define uma família de algoritmos, encapsula cada um e os torna intercambiáveis.

**Arquivos principais:**
- `HidromeImageProcessor.java` - Interface de estratégia
- `ProprietarioImageProcessor.java` - Extração de leitura de proprietário
- `ColaboradorImageProcessor.java` - Extração de leitura de colaborador
- `ImageProcessorFactory.java` - Factory para criar estratégias

**Responsabilidades:**
- Diferentes estratégias de processamento de imagens
- Seleção dinâmica de algoritmo baseado no contexto
- Fácil extensão de novos processadores

```
HidromeImageProcessor (Interface)
├── ProprietarioImageProcessor
├── ColaboradorImageProcessor
└── [Futuros processadores...]
```

---

### 6. **DECORATOR** 🎀
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/decorator/`

O padrão Decorator permite adicionar responsabilidades dinamicamente a um objeto.

**Arquivos principais:**
- `ValidatorDecorator.java` - Classe abstrata base para decorators
- `LoggingValidatorDecorator.java` - Adiciona logging à validação
- `CachingValidatorDecorator.java` - Adiciona cache à validação

**Responsabilidades:**
- Envolver validadores com funcionalidades adicionais
- Logging de validações
- Caching de resultados
- Mantém o validador original intacto

**Exemplo de Composição:**
```
CachingValidatorDecorator
  └─ LoggingValidatorDecorator
      └─ EmailValidator (original)
```

---

### 7. **TEMPLATE METHOD** 📋
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/template/`

O padrão Template Method define o esqueleto de um algoritmo em uma classe base, deixando alguns passos para serem implementados pelas subclasses.

**Arquivos principais:**
- `ImageProcessingTemplate.java` - Classe abstrata com o fluxo
- `SixDigitImageProcessor.java` - Implementação para 6 dígitos
- `ColaboradorSixDigitImageProcessor.java` - Implementação para colaborador

**Fluxo de Processamento:**
```
loadImage()
    ↓
validateImage()
    ↓
extractDigitRegion() [implementado por subclasse]
    ↓
performOCR() [implementado por subclasse]
    ↓
formatResult() [implementado por subclasse]
```

**Responsabilidades:**
- Definir algoritmo genérico de processamento
- Deixar partes específicas para subclasses
- Reutilizar código comum

---

### 8. **FACTORY** 🏭
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/factories/`

O padrão Factory fornece uma interface para criar objetos sem especificar suas classes concretas.

**Arquivos principais:**
- `ClienteFactory.java` - Cria clientes (PF/PJ)
- `EnderecoFactory.java` - Cria endereços
- `ImageProcessorFactory.java` - Cria processadores de imagem
- `LeituraHidrometroFactory.java` - Cria leituras de hidrometro
- Interfaces `IClienteFactory.java`, `IEnderecoFactory.java`

**Responsabilidades:**
- Centralizar criação de objetos complexos
- Encapsular lógica de instanciação
- Facilitar testes com mocks

---

### 9. **OBSERVER** 👀
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/observer/`

O padrão Observer define uma dependência um-para-muitos entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados automaticamente.

**Arquivos principais:**
- `ImageObserver.java` - Interface para observadores
- `ImageWatcher.java` - Monitora mudanças em diretório
- `FachadaImageObserver.java` - Observador concreto

**Responsabilidades:**
- Monitorar mudanças em diretórios de imagens
- Notificar observadores quando novas imagens chegam
- Desacoplar componentes de monitoramento

**Fluxo:**
```
ImageWatcher (Subject)
  └─ notifica →
      └─ FachadaImageObserver (Observer)
          └─ processa imagem
```

---

### 10. **COMPOSITE VALIDATOR** ✓
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/domain/enterprise/validation/`

Padrão customizado que combina múltiplos validadores.

**Arquivos principais:**
- `Validator.java` - Interface base
- `CompositeValidator.java` - Compõe múltiplos validadores
- `ValidatorBuilder.java` - Builder para construir composições
- Implementações em `/impl/`: `CpfCnpjValidValidator.java`, `EmailValidator.java`, `MinLengthValidator.java`, etc.

**Validadores Disponíveis:**
- `CpfCnpjValidValidator` - Valida CPF/CNPJ
- `EmailValidator` - Valida email
- `MaxLengthValidator` - Valida comprimento máximo
- `MinLengthValidator` - Valida comprimento mínimo
- `MaxValueValidator` - Valida valor máximo
- `MinValueValidator` - Valida valor mínimo
- `NotNullOrEmptyValidator` - Valida nulidade
- `PatternValidator` - Valida contra padrão regex

---

### 11. **ADAPTER** 🔌
**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/config/adapter/`

O padrão Adapter converte a interface de uma classe em outra esperada pelos clientes.

**Arquivos principais:**
- `TessDataPathAdapter.java` - Interface de adaptação
- `WindowsTessDataAdapter.java` - Adaptador para Windows
- `LinuxTessDataAdapter.java` - Adaptador para Linux
- `TessDataPathFactory.java` - Factory que seleciona o adaptador correto

**Responsabilidades:**
- Adaptar caminhos do Tesseract para diferentes sistemas operacionais
- Permitir portabilidade entre plataformas

---

## 🗂️ Estrutura de Pacotes

### Organização por Camadas

```
src/main/java/com/fachada/cagepa/fachadacagepa/
│
├── FachadaCagepaApplication.java (Ponto de entrada)
│
├── config/ (Configurações e Adaptadores)
│   ├── ConfigFileManager.java
│   ├── SecurityConfig.java
│   ├── SystemConfiguration.java
│   ├── TessDataPathAdapter.java
│   ├── TessDataPathFactory.java
│   └── adapter/
│       ├── WindowsTessDataAdapter.java
│       └── LinuxTessDataAdapter.java
│
├── domain/ (Camada de Domínio)
│   ├── application/
│   │   ├── dtos/ (Data Transfer Objects)
│   │   │   ├── ClientePfDTO.java
│   │   │   ├── ClientePjDTO.java
│   │   │   ├── HidrometroDTO.java
│   │   │   └── [outros DTOs...]
│   │   ├── repository/
│   │   │   └── [interfaces de repositório...]
│   │   └── services/
│   │       ├── AdminService.java
│   │       ├── AuthService.java
│   │       ├── ClienteService.java
│   │       ├── ConsumoService.java
│   │       ├── HidrometroService.java
│   │       ├── LeituraHidrometroService.java
│   │       ├── NotificacaoConsumoService.java
│   │       ├── NotificacaoPersistenciaService.java
│   │       ├── AuditService.java
│   │       ├── AuditLoggerService.java
│   │       └── JwtService.java
│   │
│   ├── enterprise/ (Padrões de Design)
│   │   ├── command/
│   │   │   ├── Command.java
│   │   │   ├── CommandInvoker.java
│   │   │   ├── CommandExecutionException.java
│   │   │   └── CreateClientPfCommand.java
│   │   │
│   │   ├── decorator/
│   │   │   ├── ValidatorDecorator.java
│   │   │   ├── LoggingValidatorDecorator.java
│   │   │   └── CachingValidatorDecorator.java
│   │   │
│   │   ├── entity/
│   │   │   └── LeituraHidrometro.java
│   │   │
│   │   ├── enums/
│   │   │   ├── TipoCliente.java
│   │   │   ├── TipoEndereco.java
│   │   │   └── [outros enums...]
│   │   │
│   │   ├── factories/
│   │   │   ├── ClienteFactory.java
│   │   │   ├── IClienteFactory.java
│   │   │   ├── EnderecoFactory.java
│   │   │   ├── IEnderecoFactory.java
│   │   │   ├── ImageProcessorFactory.java
│   │   │   └── LeituraHidrometroFactory.java
│   │   │
│   │   ├── interfaces/
│   │   │   └── [interfaces de contrato...]
│   │   │
│   │   ├── notification/
│   │   │   └── [classes de notificação...]
│   │   │
│   │   ├── observer/
│   │   │   ├── ImageWatcher.java
│   │   │   ├── FachadaImageObserver.java
│   │   │   └── interfaces/
│   │   │       └── ImageObserver.java
│   │   │
│   │   ├── state/
│   │   │   ├── ClienteState.java
│   │   │   ├── NovoClienteState.java
│   │   │   ├── ValidadoClienteState.java
│   │   │   ├── AprovadoClienteState.java
│   │   │   ├── RejeitadoClienteState.java
│   │   │   ├── SuspensoClienteState.java
│   │   │   └── StateTransitionException.java
│   │   │
│   │   ├── strategy/
│   │   │   ├── HidromeImageProcessor.java
│   │   │   ├── ProprietarioImageProcessor.java
│   │   │   └── ColaboradorImageProcessor.java
│   │   │
│   │   ├── template/
│   │   │   ├── ImageProcessingTemplate.java
│   │   │   ├── SixDigitImageProcessor.java
│   │   │   └── ColaboradorSixDigitImageProcessor.java
│   │   │
│   │   └── validation/
│   │       ├── Validator.java
│   │       ├── CompositeValidator.java
│   │       ├── ValidatorBuilder.java
│   │       ├── ValidationException.java
│   │       ├── ValidationErrorHandler.java
│   │       ├── dto/
│   │       │   └── [DTOs de validação...]
│   │       └── impl/
│   │           ├── CpfCnpjValidValidator.java
│   │           ├── EmailValidator.java
│   │           ├── MaxLengthValidator.java
│   │           ├── MinLengthValidator.java
│   │           ├── MaxValueValidator.java
│   │           ├── MinValueValidator.java
│   │           ├── NotNullOrEmptyValidator.java
│   │           └── PatternValidator.java
│   │
│   └── util/
│       ├── CpfCnpjValidator.java
│       ├── ImageDigitExtractor.java
│       └── [outras utilidades...]
│
├── facade/ (Fachada - Orquestração)
│   ├── PainelCagepaFacade.java
│   ├── ConfigurationFacade.java
│   ├── ISecurePainelCagepaFacadeProxy.java
│   └── proxy/
│       └── SecurePainelCagepaFacadeProxy.java
│
└── infra/ (Camada de Infraestrutura)
    └── persistence/
        ├── Admin.java
        ├── Cliente.java
        ├── Endereco.java
        ├── Hidrometro.java
        ├── LeituraHidrometro.java
        ├── Notificacao.java
        ├── AuditEntry.java
        ├── AuditRepository.java
        ├── IAdminJpaRepository.java
        ├── IClienteJpaRepository.java
        ├── IEnderecoJpaRepository.java
        ├── IHidrometroJpaRepository.java
        ├── ILeituraHidrometroJpaRepository.java
        ├── INotificacaoJpaRepository.java
        └── LeituraHidrometroRepositoryImpl.java
```

---

## 🔄 Fluxos Principais

### 1. Fluxo de Autenticação e Autorização
```
SecurePainelCagepaFacadeProxy.login()
    ├─→ AuthService.login()
    ├─→ JwtService.generateToken()
    ├─→ AuditLoggerService.logAcesso()
    └─→ ImageWatcher.startWatching()
```

### 2. Fluxo de Criação de Cliente
```
SecurePainelCagepaFacadeProxy.criarClientePF()
    ├─→ CompositeValidator (valida dados)
    ├─→ ClienteFactory.criarClientePessoaFisica()
    ├─→ CreateClientPfCommand (encapsula operação)
    ├─→ CommandInvoker.execute()
    ├─→ ClienteService.salvar()
    └─→ AuditLoggerService.logOperacao()
```

### 3. Fluxo de Processamento de Imagem
```
ImageWatcher.startWatching()
    ├─→ (detecta nova imagem)
    ├─→ ImageProcessorFactory.getProcessor()
    ├─→ HidromeImageProcessor.extractReading()
    │   └─→ ImageProcessingTemplate.processImage()
    │       ├─→ loadImage()
    │       ├─→ validateImage()
    │       ├─→ extractDigitRegion()
    │       ├─→ performOCR()
    │       └─→ formatResult()
    └─→ FachadaImageObserver.update()
```

### 4. Fluxo de Transição de Estado de Cliente
```
ClienteService.transicionarEstado()
    ├─→ cliente.getEstado()
    ├─→ ClienteState.aprovar() [ou rejeitar/suspender]
    ├─→ (validação de transição)
    ├─→ cliente.setEstado() (novo estado)
    └─→ (persiste no banco)
```

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 4.0.0** - Framework web
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados
- **JWT (JJWT)** - Autenticação com tokens
- **Tesseract-OCR** - Reconhecimento óptico de caracteres
- **OpenCV (JavaCV)** - Processamento de imagens
- **Lombok** - Redução de boilerplate
- **H2 Database** - Banco de dados em memória para testes
- **JUnit 5** - Framework de testes

---

## 📦 Dependências Principais

```gradle
dependencies {
    // Processamento de OCR
    implementation 'net.sourceforge.tess4j:tess4j:5.1.0'
    implementation 'org.bytedeco:javacv-platform:1.5.12'
    
    // Spring Data
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // Segurança e JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    
    // Email
    implementation 'com.resend:resend-java:+'
    
    // Banco de Dados
    runtimeOnly 'org.postgresql:postgresql'
    
    // Utilitários
    compileOnly 'org.projectlombok:lombok'
}
```

---

## 🔐 Configuração de Segurança

**Localização:** `/src/main/java/com/fachada/cagepa/fachadacagepa/config/SecurityConfig.java`

- Autenticação via JWT
- Validação de tokens em requisições
- Controle de acesso baseado em papéis
- Proxy de segurança para fachada

---

## 📝 Configurações da Aplicação

**Localização:** `/src/main/resources/application.properties`

Propriedades importantes:
- `spring.datasource.url` - URL do banco PostgreSQL
- `app.jwt.secret` - Chave secreta JWT
- `app.jwt.expiration-ms` - Expiração do token
- `tessdata.path` - Caminho dos dados do Tesseract
- `resend.api.key` - Chave API Resend (emails)
- `notificacao.limiar.percentual` - Limite de consumo para notificações

---

## 🧪 Testes

**Localização:** `/src/test/java/com/fachada/cagepa/fachadacagepa/`

Exemplos de testes:
- `AdminServiceTest.java` - Testes do serviço de admin

**Executar testes:**
```bash
./gradlew test
```

---

## 📊 Relatórios de Testes

Após executar os testes, consulte:
- `/build/reports/tests/test/index.html` - Relatório HTML completo

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21 instalado
- PostgreSQL configurado
- Tesseract OCR instalado (opcional, para processamento de imagens)

### Comando de Execução
```bash
./gradlew bootRun
```

### Via Docker Compose
```bash
docker-compose up
```

---

## 📚 Documentação UML

Diagramas UML estão disponíveis em `/docs/uml/`:
- `00-arquitetura-integrada.puml` - Visão geral da arquitetura
- `01-subsistema-autenticacao.puml` - Autenticação
- `02-subsistema-gestao-clientes.puml` - Gestão de clientes
- `03-subsistema-gestao-hidrometros.puml` - Hidrometros
- `04-subsistema-processamento-imagens.puml` - Processamento OCR
- `05-subsistema-consumo.puml` - Consumo de água
- `06-subsistema-notificacoes.puml` - Notificações
- `07-subsistema-auditoria.puml` - Auditoria
- `08-subsistema-validacao.puml` - Validação
- `09-subsistema-configuracao.puml` - Configuração
- `10-casos-de-uso-geral.puml` - Casos de uso gerais
- `16-fluxo-criar-admin.puml` - Fluxo de criação de admin

---

## 📝 Logs

- **Aplicação:** `/logs/application.log`
- **Auditoria:** `/logs/auditoria.log`
- **Configuração:** `/src/main/resources/logback.xml`

---

## 🎯 Resumo de Padrões por Camada

| Padrão | Camada | Localização | Propósito |
|--------|--------|-------------|----------|
| **Facade** | Aplicação | `/facade/` | Orquestração de serviços |
| **Proxy** | Aplicação | `/facade/proxy/` | Controle de acesso e segurança |
| **Command** | Domínio | `/domain/enterprise/command/` | Encapsulamento de operações |
| **State** | Domínio | `/domain/enterprise/state/` | Gerenciamento de estados |
| **Strategy** | Domínio | `/domain/enterprise/strategy/` | Algoritmos intercambiáveis |
| **Template Method** | Domínio | `/domain/enterprise/template/` | Estrutura de algoritmo |
| **Factory** | Domínio | `/domain/enterprise/factories/` | Criação de objetos |
| **Decorator** | Domínio | `/domain/enterprise/decorator/` | Adição de responsabilidades |
| **Observer** | Domínio | `/domain/enterprise/observer/` | Notificação de eventos |
| **Adapter** | Configuração | `/config/adapter/` | Adaptação de interfaces |
| **Composite Validator** | Domínio | `/domain/enterprise/validation/` | Validação composta |

---

## 👥 Estrutura de Papéis

- **Admin** - Gerencia usuários e configurações do sistema
- **Cliente PF** - Pessoa Física (consumidor residencial)
- **Cliente PJ** - Pessoa Jurídica (consumidor comercial)
- **Colaborador** - Agente que realiza leituras

---

## 🔄 Ciclo de Vida de um Cliente

1. **NOVO** - Cliente recém-criado
2. **VALIDADO** - Dados validados pelo sistema
3. **APROVADO** - Cliente ativo no sistema
4. **SUSPENSO** - Cliente temporariamente inativo
5. **REJEITADO** - Cliente com problemas (terminal)

---

## 📧 Sistema de Notificações

- **NotificacaoConsumoService** - Notifica sobre consumo elevado
- **NotificacaoPersistenciaService** - Persiste notificações
- **Email via Resend** - Envio de notificações por email

---

## 🔍 Auditoria

Toda operação sensível é registrada:
- Logins e logouts
- Criação/modificação de clientes
- Transições de estado
- Operações de administrador
- Processamento de imagens

**Serviços de Auditoria:**
- `AuditService.java` - Gerencia audit logs
- `AuditLoggerService.java` - Interface para logging
- `AuditEntry.java` - Entidade de auditoria
- `AuditRepository.java` - Persistência de logs

---

## 🛡️ Boas Práticas Implementadas

✅ **Separação de Responsabilidades** - Cada classe tem uma única responsabilidade  
✅ **DRY (Don't Repeat Yourself)** - Código reutilizável via padrões  
✅ **SOLID Principles** - Coesão e acoplamento otimizados  
✅ **Design Patterns** - 11 padrões implementados  
✅ **Clean Architecture** - Camadas bem definidas  
✅ **Type Safety** - Uso de tipos genéricos e Optional  
✅ **Logging Estruturado** - Rastreamento completo  
✅ **Transações ACID** - Integridade dos dados  
✅ **Testes Unitários** - Cobertura de testes  

---

## 📞 Suporte

Para dúvidas sobre os padrões implementados, consulte:
1. Os comentários nas classes de padrão
2. A documentação UML em `/docs/uml/`
3. Os testes em `/src/test/`

---

## 📄 Licença

[Especificar licença do projeto]

---

**Última atualização:** Dezembro 2025  
**Versão:** 0.0.1-SNAPSHOT

