# Plano de Testes Inicial - Clínica (Grupo 8)

Este documento apresenta a estratégia inicial de Garantia de Qualidade (QA) para o Sistema Integrado de Gestão de uma Clínica Médica.

## 1. Ferramentas e Integração
* **Biblioteca de Testes:** Utilizaremos o **JUnit 5 (JUnit Jupiter)**.
* **Integração:** A biblioteca será gerenciada via **Maven** através do arquivo `pom.xml`, isolando o escopo de teste (`<scope>test</scope>`) para que os testes não interfiram no código de produção do backend.
* **Ambiente Executável:** Os testes serão rodados localmente no **IntelliJ IDEA** antes de qualquer envio para o repositório remoto.

## 2. Cenários Críticos de Teste (Casos de Uso)
Para as primeiras entregas, priorizaremos a validação das seguintes regras de negócio:

1. **Bloqueio de Choque de Horários (Agendamento):** Garantir que o sistema impeça o agendamento de duas consultas para o mesmo médico no mesmo dia e horário.
2. **Validação de Registro Profissional (Médico):** Impedir que a entidade `Medico` seja instanciada ou salve receitas se o campo `CRM` estiver vazio ou inválido.
3. **Consistência de Dados Obrigatórios (Paciente):** Validar que a criação de um `Paciente` falhe caso campos sensíveis obrigatórios (como CPF) não sejam informados, atendendo a critérios básicos de integridade e LGPD.

## 3. Comunicação de Bugs
Caso um teste falhe ou um bug visual seja encontrado, a comunicação com os desenvolvedores (Backend/Frontend) será centralizada nas **Issues do GitHub**, utilizando o seguinte padrão de relatório:
* **Título:** [BUG] Descrição curta do erro (ex: [BUG] Permitindo agendamento duplicado).
* **Passos para reproduzir:** O caminho exato que gerou o erro.
* **Comportamento Esperado vs. Comportamento Atual.**
* **Prints ou logs de erro do IntelliJ.**