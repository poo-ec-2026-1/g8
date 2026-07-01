# Notas para a Migração Estrutural

Pendências acordadas para a etapa de correção estrutural do projeto
(migrar organização do BlueJ → padrão VS Code/Maven).

## 1. Declaração de `package` em todas as classes — ✅ FEITO (30/06/2026)

**Problema (resolvido):** as classes de `main` estavam no default package (sem
`package`), enquanto as de `test` já declaravam `package com.poo`. Isso causava
dois sintomas: (a) `mvn clean install` falhava com 81 erros `cannot find symbol`
nos testes, e (b) `mvn javafx:run` quebrava com `ClassNotFoundException: MainApp`,
porque o javafx-maven-plugin sobe a app no module-path e o Java não carrega
classe do default package.

**O que foi aplicado:**
- `package com.poo;` adicionado no topo das 24 classes de `src/main/java/com/poo/`
  (as de `test` já tinham).
- `mainClass` do `javafx-maven-plugin` alterado para `com.poo.MainApp`.

**PENDÊNCIA — conferir o UML:** a introdução formal do pacote `com.poo` é uma
mudança estrutural. Revisar o diagrama UML (organização/pacote das classes) para
refletir isso quando formos mexer na documentação.

## 2. Extrair o "seed" de dados iniciais do MainApp

**Contexto:** hoje o primeiro acesso foi destravado com um método
`popularDadosIniciais()` dentro de `MainApp.start()`, que cria uma secretária
inicial (CPF `000.000.000-00`, senha em branco no login) apenas se o banco
estiver vazio (idempotente). Isso resolve o ovo-e-galinha do login, mas mistura
responsabilidade de inicialização de dados na classe de bootstrap da UI.

**Correção sugerida (padrão de projeto real):**
- Mover a lógica de seed para uma classe/serviço dedicado, ex.:
  `com.poo.DatabaseSeeder` ou `com.poo.InicializadorDados`, com um método
  `popularSeInicial(repos...)`.
- `MainApp` apenas chama o seeder, sem conhecer os detalhes de quais entidades
  criar — reduz acoplamento e melhora coesão (SRP).
- Manter a idempotência (só insere se `loadAll().isEmpty()`).

## Observações gerais

- Projeto está dentro do OneDrive (`Desktop\...`), o que causa travas de build
  (OneDrive sincroniza `target/`). Considerar mover para fora do OneDrive
  (ex.: `C:\dev\g8`) na migração.
- `target/` e `bin/` são geradas — já estão no `.gitignore`.
