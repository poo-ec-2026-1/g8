Relatório de Produção Individual - Desenvolvimento Backend

Aluno: Gabriel Rodrigues Fonseca - 202506839
1. Atribuição de cargo e tarefas
Atribuição a priori e responsabilidades: Fiquei encarregado de atuar estritamente como Desenvolvedor Backend no projeto de orientação a objetos. Minhas responsabilidades iniciais englobavam o design da arquitetura de persistência, modelagem das tabelas do banco de dados relacional e a programação das regras de negócio do sistema.
Exercido na prática: Na prática, assumi o gerenciamento completo do ecossistema de banco de dados e as correções lógicas de infraestrutura do sistema. Fui responsável direto pela migração e reestruturação do projeto (inicialmente desenvolvido em BlueJ) para o ambiente profissional utilizando Maven e VS Code, implementando padrões de projeto para isolamento de dados e blindando as rotas lógicas contra falhas de persistência.
2. Contribuição de acordo com a atribuição
O que foi cumprido: Entrega integral da camada de persistência com banco de dados SQLite, isolamento da lógica de acesso via padrão Repository e estabilização de regras de negócio críticas (validações de horários e algoritmos de busca).
Lista dos 3 commits mais relevantes:
0493665 — fix: correção nos repositórios e loops de busca. (Commit principal focado na engenharia do backend. Correção do loop de busca no ControleHospitalar, tratamento preventivo de concorrência de horários na classe de negócio Agenda e refatoração estrita da assinatura dos repositórios para propagação de SQLException).
8bec640 — adicionando o conteúdo correto das classes do programa. (Commit focado nas classes de domínio, garantindo que os modelos, atributos e construtores estivessem de acordo com o diagrama de classes estipulado para o backend do sistema hospitalar).
69be1e1 — migração feita para Maven e organização de pacotes recomendada pelo QA completas. (Responsável pela infraestrutura e engenharia de software do projeto, migrando a estrutura legada do BlueJ para o ecossistema Maven com a injeção automatizada das dependências do ORMLite e SQLite).
O que não deu para cumprir: Não houve pendências no escopo backend planejado; todas as funções de controle interno e segurança de dados pretendidas foram executadas com sucesso.
Principais dificuldades: O maior desafio técnico foi reestruturar a arquitetura legada vinda do BlueJ sem quebrar o fluxo do sistema, mapeando onde os erros de banco de dados estavam sendo silenciados por try-catch genéricos e convertendo a lógica para o padrão profissional de propagação de exceções.
3. Contribuição além do atribuído
Como ajudei a equipe: Atuei diretamente na ponte de integração e resolução de problemas travavam o avanço do grupo, principalmente do testador QA que estava com problemas para fazer os testes no blueJ, portanto passei o código para o VS code, além disso certos problemas com extensões e bibliotecas que o front end estava tendo eu ajudei a resolver através de comunicação.
Commits e ações extras: * dcb79ea — resolvendo conflitos de histórico com a main. (Ação extra e fundamental de gerenciamento de configuração, onde atuei resolvendo conflitos severos de histórico de commits entre a branch de desenvolvimento e a branch principal, destravando o fluxo de trabalho do restante da equipe).
4. Considerações gerais
O que aprendi: Aprofundei de forma prática os conhecimentos em persistência de dados utilizando ORM (Object-Relational Mapping), gerenciamento de dependências via Maven, controle estrito de concorrência lógica e o uso avançado de Git em ambiente colaborativo na organização do GitHub.
Trabalhos futuros pendentes: O core do backend está concluído e revisado. Como trabalho futuro, fica pendente apenas a expansão para novos módulos caso o frontend demande novos relatórios ou filtros de busca avançados.
Conclusões: A refatoração elevou o nível técnico do software. O sistema hospitalar encontra-se robusto, performático, livre de falhas ocultas no banco de dados e preparado para testes rigorosos de QA e homologação.

