# Relatório Individual de Contribuição — Sistema Clínico (Grupo 8)

* **Aluno:** Gabriel Mendes Araujo (matrícula 202503235)
* **Disciplina:** Programação Orientada a Objetos
* **Repositório do grupo:** [https://github.com/poo-ec-2026-1/g8](https://github.com/poo-ec-2026-1/g8)
* **Cargo no projeto:** Desenvolvedor Frontend

## 1. Atribuição de cargo e tarefas

No planejamento inicial do grupo, ficou definido que o back-end (entidades, repositórios ORMLite e persistência em SQLite) seria responsabilidade de outros integrantes da equipe, enquanto eu fui designado como responsável pelo frontend do sistema — ou seja, pela camada de interface gráfica que os usuários (secretárias e médicos) usariam para interagir com as funcionalidades já modeladas no back-end.

As responsabilidades atribuídas a priori foram:
* Definir a tecnologia de interface gráfica a ser usada (optei por **JavaFX puro**, sem FXML, para manter consistência com o padrão já adotado em outros exercícios da disciplina).
* Construir as telas necessárias para cobrir o fluxo completo do usuário: login, menu principal, cadastro de pessoas, agendamento de consultas e visualização de agenda/prontuário.
* Integrar essas telas com as classes de repositório (`ClienteRepository`, `MedicoRepository`, `SecretariaRepository`, `ConsultaRepository`, `ProntuarioRepository`) já implementadas pelo time de back-end, sem alterar o modelo de dados.
* Garantir que a navegação entre telas fosse fluida, dentro de uma única janela (`Stage`), trocando cenas (`Scene`) conforme o usuário avança no fluxo.

Na prática, o que foi exercido seguiu essa atribuição:
* Implementei a arquitetura de navegação da aplicação (`MainApp`), responsável por inicializar o banco de dados, instanciar os repositórios uma única vez e gerenciar a troca de telas.
* Desenvolvi cinco telas completas: Login, Dashboard, Cadastro (Cliente/Médico), Agendamento de Consultas e Agenda/Prontuário.
* Apliquei um padrão visual consistente entre as telas (cores, tipografia, espaçamento), mesmo sem o uso de HTML/CSS, utilizando apenas os recursos nativos do JavaFX (inline styles via `setStyle`).

## 2. Contribuição de acordo com a atribuição

### O que foi cumprido
* **Tela de Login:** autenticação de Médicos e Secretárias por CPF e senha, com tratamento de erro para credenciais inválidas.
* **Dashboard:** menu principal pós-login, com navegação para as demais telas e botão de logout.
* **Cadastro:** formulário com abas para registrar novos Clientes (incluindo criação automática de `Prontuario` vinculado) e novos Médicos.
* **Agendamento de Consultas:** seleção de médico e paciente via `ComboBox`, validação de campos obrigatórios e verificação de conflito de horário antes de persistir a consulta.
* **Agenda/Prontuário:** visualização de consultas em tabela (`TableView`) com filtro por médico, e consulta de prontuário mediante autenticação por senha do médico responsável.

## 3. Contribuição além do atribuído

Apresentei, durante o desenvolvimento, observações sobre bugs já existentes no back-end (ex.: ausência de `return` após detecção de conflito de horário em `Agenda.novaConsulta`; `break` solto em `ControleHospitalar.verProntuario` que interrompia a busca prematuramente), repassando essas informações para os responsáveis pela camada de persistência poderem corrigir.

Além disso, ajudei na orientação do back-end reiterando a ideia central do projeto e idealizando para onde o projeto deveria percorrer.

## 4. Considerações gerais

### O que aprendi:
* Aprofundei o uso de JavaFX puro (sem FXML), incluindo `TableView`, `TabPane`, `ComboBox` com `CellFactory` customizada e troca de `Scene` dentro de um único `Stage`.
* Reforcei a importância de uma boa separação entre camadas (modelo, repositório e interface) — o fato de o back-end estar bem desacoplado facilitou bastante a integração do frontend sem retrabalho.

### Conclusão:
A atribuição de frontend foi cumprida dentro do escopo combinado: todas as telas previstas (login, dashboard, cadastro, agendamento e agenda/prontuário) foram implementadas e integradas com sucesso aos repositórios já existentes no back-end.