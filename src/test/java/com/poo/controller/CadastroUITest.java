package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;

public class CadastroUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @BeforeEach
    public void irParaTelaDeCadastro() {
        interact(() -> MainApp.irParaCadastro());
    }

    // Teste da aba: CLIENTE
    @Test
    public void deveExibirErroQuandoCamposDoClienteEstiveremVazios() {
        // Tenta salvar o cliente sem preencher nada nele
        clickOn("#btnSalvarCliente");

        // Verifica se a label de status capturou o bloqueio local do controller
        FxAssert.verifyThat("#lblStatusCliente", LabeledMatchers.hasText("Preencha Nome e CPF."));
    }

    @Test
    public void deveCadastrarClienteComSucesso() {
        // Preenche os dados usando um CPF que sabemos que o ValidadorUtils aceita
        clickOn("#fNomeCliente").write("Mateus Augusto Guimarães");
        clickOn("#fCpfCliente").write("718.905.727-72");
        clickOn("#fNascCliente").write("12/12/1992");

        // Clica em salvar
        clickOn("#btnSalvarCliente");

        // Verifica a mensagem de sucesso na tela
        FxAssert.verifyThat("#lblStatusCliente", LabeledMatchers.hasText("✔ Cliente cadastrado com sucesso!"));
    }

    // Teste da aba: MÉDICO
    @Test
    public void deveExibirErroQuandoCamposDoMedicoEstiveremVazios() {
        // Clica explicitamente no título da aba de Médicos para alternar a tela
        clickOn("🩺  Novo Médico");

        // Tenta salvar sem dados
        clickOn("#btnSalvarMedico");

        FxAssert.verifyThat("#lblStatusMedico", LabeledMatchers.hasText("Preencha Nome, CPF e Senha."));
    }

    @Test
    public void deveCadastrarMedicoComSucesso() {
        clickOn("🩺  Novo Médico");

        clickOn("#fNomeMedico").write("Dr. Roberto Carlos");
        clickOn("#fCpfMedico").write("123.456.789-09");
        clickOn("#fEspMedico").write("Pediatria");
        clickOn("#fSenhaMedico").write("med123");

        clickOn("#btnSalvarMedico");

        FxAssert.verifyThat("#lblStatusMedico", LabeledMatchers.hasText("✔ Médico cadastrado com sucesso!"));
    }

    @Test
    public void deveCadastrarMedicoAoPressionarEnter() {
        clickOn("🩺  Novo Médico");

        clickOn("#fNomeMedico").write("Dra. Enter Silva");
        clickOn("#fCpfMedico").write("529.982.247-25");
        clickOn("#fEspMedico").write("Neurologia");
        clickOn("#fSenhaMedico").write("med123");

        // Enter no último campo (com foco em #fSenhaMedico) deve confirmar
        // o cadastro sem precisar clicar no botão.
        push(KeyCode.ENTER);

        FxAssert.verifyThat("#lblStatusMedico", LabeledMatchers.hasText("✔ Médico cadastrado com sucesso!"));
    }

    // Teste da aba: SECRETÁRIA
    @Test
    public void deveExibirErroQuandoSenhasDaSecretariaNaoForemAMesma() {
        // Muda para a aba de Secretárias
        clickOn("🗂  Nova Secretária");

        clickOn("#fNomeSecretaria").write("Clara Rodriguez");
        clickOn("#fCpfSecretaria").write("529.982.247-25");
        clickOn("#fSenhaSecretaria").write("sec123");
        clickOn("#fConfirmaSecretaria").write("sec321"); // Senha diferente da primeira

        clickOn("#btnSalvarSecretaria");

        // Verifica se o controller barrou o fluxo antes de enviar ao repositório
        FxAssert.verifyThat("#lblStatusSecretaria", LabeledMatchers.hasText("As senhas não coincidem."));
    }

    @Test
    public void deveDispararCadastroDaSecretariaAoPressionarEnter() {
        // Prova que Enter aciona o mesmo handler do botão na aba Secretária,
        // usando a via de validação (senhas diferentes) para NÃO persistir —
        // o SecretariaRepository não expõe delete, então o @AfterEach não
        // limpa secretárias e criar uma de verdade sujaria o banco permanentemente.
        clickOn("🗂  Nova Secretária");

        clickOn("#fNomeSecretaria").write("Sec Enter Teste");
        clickOn("#fCpfSecretaria").write("529.982.247-25");
        clickOn("#fSenhaSecretaria").write("sec123");
        clickOn("#fConfirmaSecretaria").write("sec999"); // diferente de propósito

        // Enter com foco em #fConfirmaSecretaria deve disparar o handler.
        push(KeyCode.ENTER);

        FxAssert.verifyThat("#lblStatusSecretaria", LabeledMatchers.hasText("As senhas não coincidem."));
    }

    @Test
    public void deveOBotaoVoltarRetornarParaODashboard() {
        // Testa a ação de navegação
        clickOn("#btnVoltar");

        // Se voltar com sucesso, o teste vai poder ler o título de boas-vindas do Dashboar, mas como não logamos de forma formal, o nome de usuário vai estar null na sessão, validamos o padrão
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
