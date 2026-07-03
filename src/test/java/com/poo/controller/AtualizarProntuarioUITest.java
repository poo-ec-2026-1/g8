package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.base.NodeMatchers;

public class AtualizarProntuarioUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @BeforeEach
    public void irParaTelaDeAtualizacao() {
        interact(() -> MainApp.irParaAtualizarProntuario());
    }

    @Test
    public void deveExibirErroAoBuscarComCpfVazio() {
        // Tenta buscar sem digitar nada
        clickOn("#btnBuscar");

        // Verifica a validação local
        FxAssert.verifyThat("#lblBuscaStatus", LabeledMatchers.hasText("Informe o CPF do paciente."));

        // Garante que o campo de doença e botão de salvar continuam desabilitados
        FxAssert.verifyThat("#campoDoenca", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#btnSalvar", NodeMatchers.isDisabled());
    }

    @Test
    public void deveExibirErroSePacienteNaoExistirNoBanco() {
        // Digita um CPF que não existe
        clickOn("#campoBuscaCpf").write("000.000.000-00");
        clickOn("#btnBuscar");

        // Verifica o aviso
        FxAssert.verifyThat("#lblBuscaStatus", LabeledMatchers.hasText("Paciente não encontrado."));

        // Garante proteção da tela
        FxAssert.verifyThat("#campoDoenca", NodeMatchers.isDisabled());
        FxAssert.verifyThat("#btnSalvar", NodeMatchers.isDisabled());
    }

    @Test
    public void deveEncontrarPacienteHabilitarCamposECriarProntuario() {
        // Coloca um paciente diretamente no banco de dados para o teste
        interact(() -> {
            try {
                Cliente clienteTeste = new Cliente("Paciente UI Teste", "718.905.727-72", "10/10/1990");
                MainApp.getClienteRepo().create(clienteTeste);
            } catch (Exception e) {
                System.out.println("Erro ao preparar banco para o teste: " + e.getMessage());
            }
        });

        // Busca o paciente criado acima
        clickOn("#campoBuscaCpf").write("718.905.727-72");
        clickOn("#btnBuscar");

        // Verifica se a tela reagiu positivamente e destravou
        FxAssert.verifyThat("#lblBuscaStatus", LabeledMatchers.hasText("Paciente encontrado. Ainda não possui prontuário — será criado um novo ao salvar."));
        FxAssert.verifyThat("#campoDoenca", NodeMatchers.isEnabled());
        FxAssert.verifyThat("#btnSalvar", NodeMatchers.isEnabled());

        // Preenche o diagnóstico
        clickOn("#campoDoenca").write("Paciente com virose.");

        // Salva o prontuário
        clickOn("#btnSalvar");

        // Verifica a mensagem de sucesso
        FxAssert.verifyThat("#lblSalvarStatus", LabeledMatchers.hasText("✔ Prontuário criado e vinculado ao paciente!"));
    }

    @Test
    public void deveBotaoVoltarRetornarParaODashboard() {
        clickOn("#btnVoltar");
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