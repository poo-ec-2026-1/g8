package com.poo;

import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

public class RegistroAtendimentoUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @Test
    public void deveExibirErroAoTentarRegistrarSemSelecionarConsulta() {
        // Abre a tela limpa
        interact(() -> MainApp.irParaRegistroAtendimento());

        // Clica no botão sem selecionar nada no ComboBox
        clickOn("#btnRegistrar");

        // Verifica a validação local
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("Selecione uma consulta."));
    }

    @Test
    public void deveExibirErroSePacienteDaConsultaNaoTiverProntuario() {
        // INJETA NO BANCO PRIMEIRO (CPFs validados)
        interact(() -> {
            try {
                Medico m = new Medico("Dr. Sem Prontuario", "123.456.789-09", "Clínica Geral", "123");
                Cliente c = new Cliente("Paciente Sem Pasta", "718.905.727-72", "10/10/2000"); // Sem prontuário

                MainApp.getMedicoRepo().create(m);
                MainApp.getClienteRepo().create(c);

                Consulta consulta = new Consulta(0, "01/01/2026", "10:00", m, c);
                MainApp.getConsultaRepo().create(consulta);
            } catch (Exception e) {
                System.out.println("Erro ao preparar banco: " + e.getMessage());
            }
        });

        // AGORA ABRE A TELA (O ComboBox vai ler o banco atualizado!)
        interact(() -> MainApp.irParaRegistroAtendimento());

        // Localiza essa consulta específica no ComboBox e seleciona ela
        interact(() -> {
            ComboBox<Consulta> cb = lookup("#cbConsulta").queryComboBox();
            Consulta alvo = cb.getItems().stream()
                    .filter(x -> x.getCliente() != null && x.getCliente().getNome().equals("Paciente Sem Pasta"))
                    .findFirst().orElse(null);
            cb.getSelectionModel().select(alvo);
        });

        // Tenta registrar o atendimento
        clickOn("#btnRegistrar");

        // Verifica a trava de segurança
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText(
                "Este paciente ainda não possui prontuário. Crie um na tela \"Atualizar Prontuário\" antes de registrar o atendimento."
        ));
    }

    @Test
    public void deveRegistrarAtendimentoComSucesso() {
        // INJETA NO BANCO PRIMEIRO (Cliente COM Prontuário)
        interact(() -> {
            try {
                Prontuario p = new Prontuario("Pressão alta");
                MainApp.getProntuarioRepo().create(p);

                Medico m = new Medico("Dr. Perez", "529.982.247-25", "Cardiologia", "123");
                Cliente c = new Cliente("Paciente Com Pasta", "111.444.777-35", 0, p, "02/02/1980");

                MainApp.getMedicoRepo().create(m);
                MainApp.getClienteRepo().create(c);

                Consulta consulta = new Consulta(0, "02/02/2026", "11:00", m, c);
                MainApp.getConsultaRepo().create(consulta);
            } catch (Exception e) {
                System.out.println("Erro ao preparar banco: " + e.getMessage());
            }
        });

        // AGORA ABRE A TELA
        interact(() -> MainApp.irParaRegistroAtendimento());

        // Seleciona no ComboBox
        interact(() -> {
            ComboBox<Consulta> cb = lookup("#cbConsulta").queryComboBox();
            Consulta alvo = cb.getItems().stream()
                    .filter(x -> x.getCliente() != null && x.getCliente().getNome().equals("Paciente Com Pasta"))
                    .findFirst().orElse(null);
            cb.getSelectionModel().select(alvo);
        });

        // Digita as observações e clica em registrar
        clickOn("#fObs").write("Paciente relatou melhora. Receitado repouso.");
        clickOn("#btnRegistrar");

        // Verifica se registrou
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText(
                "✔ Atendimento registrado! Médico adicionado ao histórico do prontuário de Paciente Com Pasta."
        ));
    }

    @Test
    public void deveBotaoVoltarRetornarParaODashboard() {
        interact(() -> MainApp.irParaRegistroAtendimento());
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