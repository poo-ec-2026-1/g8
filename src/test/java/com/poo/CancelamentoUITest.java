package com.poo;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

public class CancelamentoUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @BeforeEach
    public void irParaTelaDeCancelamento() {
        interact(() -> MainApp.irParaCancelamento());
    }

    @Test
    public void deveExibirErroAoTentarCancelarSemSelecionarLinha() {
        // Tenta cancelar direto, sem selecionar nada na tabela
        clickOn("#btnCancelar");

        // Verifica se o controller impediu a ação e avisou o usuário
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("Selecione uma consulta na tabela."));
    }

    @Test
    public void deveCancelarConsultaComSucesso() {
        // Coloca dados reais no banco para o teste usando CPFs válidos
        interact(() -> {
            try {
                // Usa CPFs que passam no ValidadorUtils
                Medico m = new Medico("Dr. Santos", "123.456.789-09", "Geral", "12345678");
                Cliente c = new Cliente("Paciente Aleatório", "718.905.727-72", "01/01/2001");

                MainApp.getMedicoRepo().create(m);
                MainApp.getClienteRepo().create(c);

                Consulta consulta = new Consulta(0, "30/12/2026", "08:00", m, c);
                MainApp.getConsultaRepo().create(consulta);
            } catch (Exception e) {
                System.out.println("Erro ao preparar banco para o teste: " + e.getMessage());
            }
        });

        // Clica no botão de atualizar a lista para a consulta aparecer na tabela
        clickOn("#btnAtualizar");

        // Seleciona o primeiro item da TableView via Thread do JavaFX
        interact(() -> {
            TableView<?> tabela = lookup("#tabelaConsultas").queryTableView();
            tabela.getSelectionModel().selectFirst();
        });

        // Clica no botão de cancelar (isso vai abrir o pop-up do JavaFX)
        clickOn("#btnCancelar");

        // O teste vê o botão OK da janela de Alerta e clica nele
        clickOn("OK");

        // Verifica se o texto de sucesso apareceu na tela principal
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("✔ Consulta cancelada com sucesso."));
    }

    @Test
    public void deveBotaoVoltarRetornarParaODashboard() {
        clickOn("#btnVoltar");

        // Verifica se voltou com segurança
        FxAssert.verifyThat("#lblTitulo", LabeledMatchers.hasText("Bem-vindo(a), null!"));
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