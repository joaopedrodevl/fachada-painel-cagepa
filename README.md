# 🚰 Sistema de Gestão de Hidrometros - CAGEPA

> Sistema de gerenciamento de clientes e hidrômetros utilizando padrões de projeto e arquitetura limpa.

### Progresso Geral - Changelog

```
██░░░░░░░░░░░░░░░░░░ 10% (Funcionalidades Core)
░░░░░░░░░░░░░░░░░░░░ 0% (Testes)
```

#### [29/11/2025]
- Cadastro de cliente implementado na fachada. Porém ainda falta a regra para só administradores conseguirem cadastrar um cliente.
- 90% da implementação do subsistema de Gestão de Clientes concluída.
- Implementado o padrão Factory Method para criação de Clientes e Endereços.
- Início do desenvolvimento dos subsistemas de Monitoramento e OCR.

#### [25/11/2025] 
- Parcialmente implementado o subsistema de Gestão de Clientes, faltando as regras de negócio.

## Padrões de Projeto (GoF) Implementados

### 1. **Factory Method Pattern**

**Localização no Código:**
```
src/main/java/com/cagepa/painel/fachada_painel_cagepa/domain/enterprise/factories/
├── ClienteFactory.java          ← Implementação do Factory
├── EnderecoFactory.java         ← Implementação do Factory
└── interfaces/
    ├── IClienteFactory.java     ← Interface do Factory
    └── IEnderecoFactory.java    ← Interface do Factory
```