# 🚰 Sistema de Gestão de Hidrometros - CAGEPA

> Sistema de gerenciamento de clientes e hidrômetros utilizando padrões de projeto e arquitetura limpa.

### Progresso Geral - Changelog

25/11/2025 - Parcialmente implementado o subsistema de Gestão de Clientes, faltando as regras de negócio.

```
█░░░░░░░░░░░░░░░░░░░ 5% (Funcionalidades Core)
░░░░░░░░░░░░░░░░░░░░ 0% (Testes)
```

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