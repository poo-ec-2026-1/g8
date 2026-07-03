package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.api.FxAssert;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.base.NodeMatchers;

public class LoginUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) {
        new MainApp().start(stage);
    }

    @Test
    public void deveExibirErroQuandoCpfEstiverVazio() {
        // O teste clica no botão de entrar, mas sem digitar nada
        clickOn("#btnLogin");

        // Verifica se a label de erro exibiu a mensagem certa
        FxAssert.verifyThat("#labelErro", LabeledMatchers.hasText("Informe o CPF."));
    }

    @Test
    public void deveExibirErroComCpfNaoCadastrado() {
        // O teste clica no campo de CPF e digita um valor inválido
        clickOn("#campoCPF").write("999.999.999-99");

        // Clica em entrar
        clickOn("#btnLogin");

        // Verifica se acusou CPF não encontrado
        FxAssert.verifyThat("#labelErro", LabeledMatchers.hasText("CPF não encontrado."));
    }

    @Test
    public void deveFazerLoginComoSecretariaAdminEIrParaDashboard() {
        // Esse CPF é gerado pelo "popularDadosIniciais()" do MainApp
        clickOn("#campoCPF").write("111.444.777-35");

        // Clica em entrar
        clickOn("#btnLogin");

        // Como o login tesm que dar certo, a tela deve mudar para o Dashboard e o teste vai procurar os elementos do Dashboard para confirmar a navegação

        // Verifica se a label de perfil diz que é Secretária
        FxAssert.verifyThat("#lblTipo", LabeledMatchers.hasText("Perfil: Secretária"));

        // Verifica se o botão "Cadastrar Paciente", que é exclusivo da secretária, apareceu na tela
        FxAssert.verifyThat("#btnCadastro", NodeMatchers.isVisible());

        // O teste clica em "Sair" para fechar tudo depois
        clickOn("#btnSair");
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