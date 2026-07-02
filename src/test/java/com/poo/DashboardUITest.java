package com.poo;

import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.base.NodeMatchers;

public class DashboardUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @Test
    public void deveExibirBotoesDeSecretariaEOcultarDeMedico() {
        // Força a inicialização da tela diretamente como Secretária
        interact(() -> MainApp.irParaDashboard("Ana Paula", "Secretária"));

        // Verifica os cabeçalhos dinâmicos
        FxAssert.verifyThat("#lblTitulo", LabeledMatchers.hasText("Bem-vindo(a), Ana Paula!"));
        FxAssert.verifyThat("#lblTipo", LabeledMatchers.hasText("Perfil: Secretária"));

        // Garante que os botões de Secretária foram colocados e estão visíveis
        FxAssert.verifyThat("#btnCadastro", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnAgendar", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnCancelar", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnAgendaSecretaria", NodeMatchers.isVisible());

        // Garante que os botões exclusivos do Médico não existem na árvore da tela
        // O tryQuery() previne o erro de componente não encontrado do TestFX
        Assertions.assertFalse(lookup("#btnAtendimento").tryQuery().isPresent(),
                "Botão de atendimento não deve aparecer para a secretária!");
        Assertions.assertFalse(lookup("#btnAtualizarProntuario").tryQuery().isPresent(),
                "Botão de atualizar prontuário não deve aparecer para a secretária!");
    }

    @Test
    public void deveExibirBotoesDeMedicoEOcultarDeSecretaria() {
        // Força a inicialização da tela diretamente como Médico
        interact(() -> MainApp.irParaDashboard("Dr. Gregory House", "Médico"));

        // Verifica os cabeçalhos dinâmicos
        FxAssert.verifyThat("#lblTitulo", LabeledMatchers.hasText("Bem-vindo(a), Dr. Gregory House!"));
        FxAssert.verifyThat("#lblTipo", LabeledMatchers.hasText("Perfil: Médico"));

        // Garante que os botões do Médico foram injetados e estão visíveis
        FxAssert.verifyThat("#btnAgendaMedico", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnProntuario", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnAtendimento", NodeMatchers.isVisible());
        FxAssert.verifyThat("#btnAtualizarProntuario", NodeMatchers.isVisible());

        // Garante que os botões exclusivos da Secretária NÃO existem na árvore da tela
        Assertions.assertFalse(lookup("#btnCadastro").tryQuery().isPresent(),
                "Botão de cadastro não deve aparecer para o médico!");
        Assertions.assertFalse(lookup("#btnAgendar").tryQuery().isPresent(),
                "Botão de agendamento não deve aparecer para o médico!");
        Assertions.assertFalse(lookup("#btnCancelar").tryQuery().isPresent(),
                "Botão de cancelamento não deve aparecer para o médico!");
    }

    @Test
    public void deveFazerLogoutAoClicarEmSair() {
        // Inicializa o Dashboard
        interact(() -> MainApp.irParaDashboard("Usuário Teste", "Secretária"));

        // Clica no botão de Sair
        clickOn("#btnSair");

        // Verifica se a tela retornou para o Login lendo um elemento da tela de login
        FxAssert.verifyThat("#btnLogin", NodeMatchers.isVisible());
        FxAssert.verifyThat("#campoCPF", NodeMatchers.isVisible());
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