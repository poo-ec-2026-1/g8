package com.poo;

import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.base.NodeMatchers;

public class AgendaUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    // CENÁRIO: ACESSO COMO SECRETÁRIA
    @Test
    public void deveExibirApenasAbaAgendaQuandoForSecretaria() {
        // Inicializa a tela com o perfil de Secretária
        interact(() -> MainApp.irParaAgenda("Secretária"));

        // Garante que a aba de Agenda está visível e funcional
        FxAssert.verifyThat("#abaAgenda", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnFiltrar", NodeMatchers.isVisible());

        // Garante que a aba de Prontuário não existe na árvore de componentes
        // O tryQuery() busca o elemento sem lançar erro caso não encontre, e o assertFalse confirma que ele não está presente
        Assertions.assertFalse(lookup("#abaProntuario").tryQuery().isPresent(),
                "Aba Prontuário não deveria ser injetada para Secretárias!");
    }

    @Test
    public void deveFiltrarConsultasSemErrosNaAgenda() {
        interact(() -> MainApp.irParaAgenda("Secretária"));

        // Clica no botão filtrar para carregar as consultas na tabela
        clickOn("#btnFiltrar");

        // Verifica que a label de erro de filtro permaneceu limpa (vazia)
        FxAssert.verifyThat("#lblErroFiltro", LabeledMatchers.hasText(""));
    }

    // CENÁRIO: ACESSO COMO MÉDICO
    @Test
    public void deveExibirAmbasAsAbasQuandoForMedico() {
        // Inicializa a tela com o perfil de Médico
        interact(() -> MainApp.irParaAgenda("Médico"));

        // Como Médico, as duas abas precisam estar visíveis
        FxAssert.verifyThat("#abaAgenda", NodeMatchers.isVisible());
        FxAssert.verifyThat("#abaProntuario", NodeMatchers.isVisible());
    }

    @Test
    public void deveExibirErroAoTentarAcessarProntuarioComCamposVazios() {
        interact(() -> MainApp.irParaAgenda("Médico"));

        // Clica fisicamente na aba de Prontuário para abrir o formulário
        clickOn("📋  Prontuário");

        // Clica em acessar direto com os campos em branco
        clickOn("#btnConsultar");

        // Valida se o controller impediu o avanço
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("Preencha CPF e senha."));

        // Pega o elemento TextArea e verifica o texto com o JUnit puro
        TextArea area = lookup("#areaProntuario").queryAs(TextArea.class);
        Assertions.assertEquals("", area.getText(), "O prontuário deveria continuar vazio.");
    }

    @Test
    public void deveExibirErroQuandoPacienteNaoExistirNoBanco() {
        interact(() -> MainApp.irParaAgenda("Médico"));

        clickOn("📋  Prontuário");

        // Digita um CPF fantasma e uma senha qualquer
        clickOn("#fCPF").write("000.111.222-33");
        clickOn("#fSenha").write("med123456");

        clickOn("#btnConsultar");

        // Verifica se a mensagem mapeada no controller apareceu na tela
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("Paciente não encontrado."));
    }

    @Test
    public void deveBotaoVoltarRetornarParaODashboardDoMedico() {
        // Simula que estamos vindo da sessão do Médico para não quebrar a volta
        interact(() -> MainApp.irParaDashboard("Dr. House", "Médico"));
        interact(() -> MainApp.irParaAgenda("Médico"));

        // Clica em Voltar
        clickOn("#btnVoltar");

        // Valida se retornou com sucesso mantendo a sessão do médico intacta
        FxAssert.verifyThat("#lblTitulo", LabeledMatchers.hasText("Bem-vindo(a), Dr. House!"));
        FxAssert.verifyThat("#lblTipo", LabeledMatchers.hasText("Perfil: Médico"));
    }

    @org.junit.jupiter.api.AfterEach
    public void limparBancoDeDadosE_Sessao() {
        // Roda automaticamente após cada teste para limpar a sujeira deixada no banco
        try {
            // Apaga as consultas
            for (Consulta c : MainApp.getConsultaRepo().loadAll()) {
                MainApp.getConsultaRepo().delete(c);
            }

            // Apaga os clientes e os seus respetivos prontuários
            for (Cliente c : MainApp.getClienteRepo().loadAll()) {
                if (c.getProntuario() != null) {
                    MainApp.getProntuarioRepo().delete(c.getProntuario());
                }
                MainApp.getClienteRepo().delete(c);
            }

            // Apaga os médicos
            for (Medico m : MainApp.getMedicoRepo().loadAll()) {
                MainApp.getMedicoRepo().delete(m);
            }

            // Força o deslogamento limpando a sessão no MainApp
            javafx.application.Platform.runLater(() -> {
                MainApp.irParaLogin();
            });

        } catch (Exception e) {
            System.out.println("Erro ao limpar banco de dados após o teste: " + e.getMessage());
        }
    }
}