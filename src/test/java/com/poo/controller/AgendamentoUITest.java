package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgendamentoUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @BeforeEach
    public void irParaTelaDeAgendamento() {
        // INJETA DADOS PRIMEIRO (pois o @AfterEach limpou tudo!)
        interact(() -> {
            try {
                // Cria um médico e cliente usando CPFs válidos
                Medico m = new Medico("Dr. Agendamento", "123.456.789-09", "Geral", "123");
                Cliente c = new Cliente("Paciente Agendamento", "718.905.727-72", "01/01/2000");
                MainApp.getMedicoRepo().create(m);
                MainApp.getClienteRepo().create(c);
            } catch (Exception e) {
                System.out.println("Erro ao preparar banco: " + e.getMessage());
            }

            // AGORA abre a tela (para o ComboBox ler os dados frescos)
            MainApp.irParaAgendamento();
        });
    }

    @Test
    public void deveExibirErroAoTentarAgendarComCamposVazios() {
        // Tenta salvar direto, mas garantimos que as datas estão vazias
        clickOn("#btnSalvar");

        // Verifica a validação do frontend
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("Preencha todos os campos."));
    }

    @Test
    public void deveAgendarConsultaComSucesso() {
        interact(() -> {
            ComboBox<?> cbMedico = lookup("#cbMedico").queryComboBox();
            cbMedico.getSelectionModel().selectFirst();

            ComboBox<?> cbCliente = lookup("#cbCliente").queryComboBox();
            cbCliente.getSelectionModel().selectFirst();
        });

        clickOn("#fData").write("25/12/2026");
        clickOn("#fHorario").write("10:00");

        clickOn("#btnSalvar");

        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("✔ Consulta agendada com sucesso!"));
    }

    @Test
    public void deveExibirErroDeConflitoDeHorario() {
        // PRIMEIRO AGENDAMENTO
        interact(() -> {
            ComboBox<?> cbMedico = lookup("#cbMedico").queryComboBox();
            cbMedico.getSelectionModel().selectFirst();

            ComboBox<?> cbCliente = lookup("#cbCliente").queryComboBox();
            cbCliente.getSelectionModel().selectFirst();
        });

        clickOn("#fData").write("10/10/2026");
        clickOn("#fHorario").write("14:30");
        clickOn("#btnSalvar");

        // TENTATIVA DE AGENDAR POR CIMA
        interact(() -> {
            // Seleciona de novo
            ComboBox<?> cbMedico = lookup("#cbMedico").queryComboBox();
            cbMedico.getSelectionModel().selectFirst();

            ComboBox<?> cbCliente = lookup("#cbCliente").queryComboBox();
            cbCliente.getSelectionModel().selectFirst();

            // LIMPA OS CAMPOS DE TEXTO PARA NÃO FICAR UM POR CIMA DO OUTRO!
            lookup("#fData").queryTextInputControl().clear();
            lookup("#fHorario").queryTextInputControl().clear();
        });

        clickOn("#fData").write("10/10/2026");
        clickOn("#fHorario").write("14:30");
        clickOn("#btnSalvar");

        // Verifica se o controller detectou o conflito e impediu o salvamento
        FxAssert.verifyThat("#lblStatus", LabeledMatchers.hasText("⚠ Horário já ocupado para este médico."));
    }

    @Test
    public void deveFormatarHorarioComMascaraAoDigitarSoOsDigitos() {
        // O usuário digita só os números; a MascaraHorario insere o ':' sozinho.
        clickOn("#fHorario").write("1430");

        interact(() -> {
            TextInputControl campo = lookup("#fHorario").queryTextInputControl();
            assertEquals("14:30", campo.getText(),
                    "A máscara deve transformar 1430 em 14:30 automaticamente.");
        });
    }

    @Test
    public void deveBotaoVoltarRetornarParaODashboard() {
        clickOn("#btnVoltar");
        FxAssert.verifyThat("#lblTitulo", LabeledMatchers.hasText("Bem-vindo(a), null!"));
    }

    @org.junit.jupiter.api.AfterEach
    public void limparBancoDeDadosE_Sessao() {
        try {
            for (Consulta c : MainApp.getConsultaRepo().loadAll()) {
                MainApp.getConsultaRepo().delete(c);
            }
            for (Cliente c : MainApp.getClienteRepo().loadAll()) {
                if (c.getProntuario() != null) {
                    MainApp.getProntuarioRepo().delete(c.getProntuario());
                }
                MainApp.getClienteRepo().delete(c);
            }
            for (Medico m : MainApp.getMedicoRepo().loadAll()) {
                MainApp.getMedicoRepo().delete(m);
            }
            javafx.application.Platform.runLater(() -> {
                MainApp.irParaLogin();
            });
        } catch (Exception e) {
            System.out.println("Erro ao limpar banco de dados após o teste: " + e.getMessage());
        }
    }
}
