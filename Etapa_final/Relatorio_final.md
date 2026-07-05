# Relatório Individual — Desenvolvimento Backend

> Projeto prático de **Programação Orientada a Objetos (POO)** — módulo de persistência e regras de negócio do backend.

---

## 1. Atribuição de cargo e tarefas

**O que foi atribuído:**
Ficou sob minha responsabilidade atuar no desenvolvimento da camada de persistência e regras de negócio do backend para o projeto prático de POO. O escopo inicial envolvia a modelagem 
estrutural das entidades centrais do sistema e o mapeamento relacional utilizando o banco de dados SQLite.

**Responsabilidades:**
- Garantir a criação das entidades de dados básicas; 
- Configurar a estrutura de herança para usuários do sistema;
- Implementar as funções de manipulação de dados essenciais.

**O que foi exercido na prática:**
Além da modelagem básica de dados, assumi o design da arquitetura de validação de dados críticos do sistema, construindo do zero os validadores de consistência para evitar a inserção de 
dados espúrios ou mal formatados.

**Escolha da IDE:**
A princípio foi utilizado o BlueJ, porém a pedido do testador QA foi feita a migração do codigo para o VS code com Maven.

---

## 2. Contribuição de acordo com a atribuição

### O que foi cumprido

- Implementação da classe base `Usuario` utilizando anotações do framework **ORMLite** (`@DatabaseField(generatedId = true)`) para gerenciar as chaves primárias via auto-incremento no 
SQLite de forma transparente.
- Criação completa e estruturação da classe utilitária de segurança `ValidadorUtils`.
- Desenvolvimento do algoritmo matemático de validação de CPFs (`isCpfValido`), responsável por:
  - limpar caracteres não numéricos;
  - filtrar sequências inválidas conhecidas;
  - computar os dígitos verificadores.
- Desenvolvimento do método de checagem estrutural de calendários (`isDataValida`), integrado com a API de tempo do Java (`java.time.format.DateTimeFormatter`), garantindo consistência 
cronológica no padrão `dd/MM/yyyy`.

### Commits e documentos mais relevantes

 `doc`  Mapeamento inicial da entidade `Usuario` e configuração ORMLite 
 `feat`  Implementação da lógica matemática de verificação do algoritmo de CPF 
 `feat`  Construção do validador de strings e parse defensivo para datas (`dd/MM/yyyy`) 

### O que não deu para cumprir

A vinculação e o disparo automatizado das exceções customizadas para a interface gráfica da Agenda (como o bloqueio direto de horários duplicados por médicos) acabaram demandando alterações 
dinâmicas na reta final e foram centralizadas pelo líder do grupo para acelerar a entrega dos testes.

### Principais dificuldades

- A principal barreira técnica enfrentada foi a organização de pacotes no ambiente de desenvolvimento do VS Code e a implementação do Maven no projeto, solicitada pelo testador QA. 
Durante a criação da classe utilitária, houve um conflito de pacotes onde o identificador declarado (`package com.poo.utils;`) divergiu do diretório físico estruturado no projeto 
(`src/main/java/com/poo`), gerando erros de compilação na IDE que exigiram a readequação dos escopos e nomenclatura de pastas do backend, além disso algumas dificuldades iniciais 
principalmente com a utilização do git que ao commitar nem todos os arquivos subiam de forma correta para o VS code.

- Ajustar as expressões regulares (Regex) para identificar padrões de caracteres não numéricos (`\D`) e repetições de dígitos, sem impactar a performance do processamento de strings no Java.

---

## 3. Contribuição além do atribuído

**Como ajudei a equipe:**
Auxiliei os integrantes responsáveis pelo front-end em JavaFX a compreenderem como as regras defensivas do backend operavam. Atuei diretamente na ponte de integração e na 
resolução de problemas que travavam o avanço do grupo, principalmente do testador QA, que estava com dificuldades para rodar os testes no BlueJ — migrei o código para o VS Code. 
Além disso, ajudei a resolver, via comunicação direta, problemas com extensões e bibliotecas que o time de front-end estava enfrentando.

---

## 4. Considerações gerais

### O que aprendi

- Aplicação prática de herança e modificadores de acesso protegidos (`protected`) em Java;
- Funcionamento de um mapeador objeto-relacional (ORM);
- Importância de adotar a programação defensiva na validação de dados antes que eles cheguem à camada de persistência;
- Gerenciamento de dependências via Maven;
- Uso avançado de Git em ambiente colaborativo, na organização do GitHub.

### Trabalhos futuros pendentes

- Substituição completa de validações em strings genéricas por tipos de dados nativos mais robustos do Java;
- Criação de uma suíte de testes unitários automatizados com **JUnit**, focada exclusivamente na cobertura de cenários de exceção dos validadores desenvolvidos.

### Conclusões

A experiência foi de extrema importância para compreender a dinâmica de integração de software em equipe utilizando Git, e para vivenciar os desafios reais de acoplamento entre as regras 
rígidas de banco de dados e as entradas de dados flexíveis vindas de uma interface com o usuário.

## 5. Video Apresentação

* [Link da apresentação gravada.](https://drive.google.com/file/d/1TzvAb3oq2xh9lLsfSxFTAKhuJ5ttrygh/view?usp=sharing) 
