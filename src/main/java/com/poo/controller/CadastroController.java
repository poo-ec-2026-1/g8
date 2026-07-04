package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Medico;
import com.poo.model.Secretaria;
import com.poo.repository.ClienteRepository;
import com.poo.repository.MedicoRepository;
import com.poo.repository.ProntuarioRepository;
import com.poo.repository.SecretariaRepository;
import com.poo.util.MascaraCpf;
import com.poo.util.MascaraData;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Cadastro.
 * Permite registrar novos Clientes e Médicos no banco via abas.
 *
 * NOVIDADE DESTA VERSÃO: ao cadastrar um Cliente, agora é possível
 * (opcionalmente) já vincular um médico responsável ao Prontuário criado,
 * usando ProntuarioRepository.adicionarMedicoAoHistorico(), que persiste
 * o relacionamento muitos-para-muitos através da tabela ponte ProntuarioMedico.
 *
 * A aba de Secretária usa apenas SecretariaRepository.create(), pois o
 * repositório atual não expõe update/delete para esse tipo.
 */
public class CadastroController {

    private final ClienteRepository    clienteRepo;
    private final MedicoRepository     medicoRepo;
    private final ProntuarioRepository prontuarioRepo;
    private final SecretariaRepository secretariaRepo;

    private BorderPane view;

    public CadastroController(ClienteRepository clienteRepo,
                              MedicoRepository medicoRepo,
                              ProntuarioRepository prontuarioRepo,
                              SecretariaRepository secretariaRepo) {
        this.clienteRepo    = clienteRepo;
        this.medicoRepo     = medicoRepo;
        this.prontuarioRepo = prontuarioRepo;
        this.secretariaRepo = secretariaRepo;
        construirView();
    }

    private void construirView() {
        Label titulo = new Label("Cadastro");
        titulo.getStyleClass().add("titulo-tela");

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setId("btnVoltar");
        btnVoltar.getStyleClass().add("btn-voltar");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.getStyleClass().add("app-header");

        TabPane abas = new TabPane();
        abas.setId("tabPaneCadastro");
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        abas.getTabs().addAll(criarAbaCliente(), criarAbaMedico(), criarAbaSecretaria());

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(abas);
        view.getStyleClass().add("app-bg");
    }

    // -------------------------------------------------------------------------
    // Aba: Cliente
    // -------------------------------------------------------------------------
    private Tab criarAbaCliente() {
        Label lblNome  = new Label("Nome completo:");
        lblNome.getStyleClass().add("label-campo");
        TextField fNome = campo("Ex: Maria Silva");
        fNome.setId("fNomeCliente");

        Label lblCPF   = new Label("CPF:");
        lblCPF.getStyleClass().add("label-campo");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfCliente");
        MascaraCpf.aplicar(fCPF);

        Label lblNasc  = new Label("Data de nascimento:");
        lblNasc.getStyleClass().add("label-campo");
        TextField fNasc = campo("DD/MM/AAAA");
        fNasc.setId("fNascCliente");
        MascaraData.aplicar(fNasc);

        Label lblAviso = new Label(
                "O prontuário do paciente é criado posteriormente, na tela " +
                        "\"Atualizar Prontuário\" (disponível para o médico)."
        );
        lblAviso.setWrapText(true);
        lblAviso.setMaxWidth(380);
        lblAviso.getStyleClass().add("texto-aviso");

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusCliente");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.getStyleClass().add("status-erro");

        Button btnSalvar = botaoAcao("Cadastrar Cliente", "botao-verde");
        btnSalvar.setId("btnSalvarCliente");

        // Extraído numa variável para reaproveitar em fNome/fCPF/fNasc —
        // assim Enter em qualquer campo da aba confirma o cadastro, igual
        // ao clique no botão.
        EventHandler<ActionEvent> acaoCadastrarCliente = e -> {
            if (fNome.getText().isBlank() || fCPF.getText().isBlank()) {
                estilo(lblStatus, "Preencha Nome e CPF.", false);
                return;
            }
            try {
                // Cliente é criado SEM prontuário vinculado (fica null).
                // O prontuário é criado depois, na tela de Atualizar Prontuário.
                Cliente cliente = new Cliente(
                        fNome.getText().trim(),
                        fCPF.getText().trim(),
                        fNasc.getText().trim()
                );
                clienteRepo.create(cliente);

                estilo(lblStatus, "✔ Cliente cadastrado com sucesso!", true);
                fNome.clear(); fCPF.clear(); fNasc.clear();

            } catch (IllegalArgumentException ex) {
                estilo(lblStatus, ex.getMessage(), false);
            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao cadastrar cliente: " + ex.getMessage(), false);
            }
        };

        btnSalvar.setOnAction(acaoCadastrarCliente);
        btnSalvar.setDefaultButton(true);
        fNome.setOnAction(acaoCadastrarCliente);
        fCPF.setOnAction(acaoCadastrarCliente);
        fNasc.setOnAction(acaoCadastrarCliente);

        VBox conteudo = new VBox(10,
                lblNome, fNome,
                lblCPF, fCPF,
                lblNasc, fNasc,
                lblAviso,
                btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        // O VBox tem maxWidth, então nem o ScrollPane nem o Tab conseguem
        // esticá-lo — ele fica no tamanho fixo. Por isso o alinhamento tem
        // que estar no StackPane (que sim se estica até preencher o espaço
        // disponível) e não no próprio VBox.
        StackPane wrapper = new StackPane(conteudo);
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        Tab aba = new Tab("👤  Novo Cliente", scroll);
        aba.setId("abaCliente");
        return aba;
    }

    // -------------------------------------------------------------------------
    // Aba: Médico
    // -------------------------------------------------------------------------
    private Tab criarAbaMedico() {
        Label lblNome  = new Label("Nome completo:");
        lblNome.getStyleClass().add("label-campo");
        TextField fNome = campo("Ex: Dr. João Souza");
        fNome.setId("fNomeMedico");

        Label lblCPF   = new Label("CPF:");
        lblCPF.getStyleClass().add("label-campo");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfMedico");
        MascaraCpf.aplicar(fCPF);

        Label lblEsp   = new Label("Especialidade:");
        lblEsp.getStyleClass().add("label-campo");
        TextField fEsp  = campo("Ex: Cardiologia");
        fEsp.setId("fEspMedico");

        Label lblSenha = new Label("Senha de acesso:");
        lblSenha.getStyleClass().add("label-campo");
        PasswordField fSenha = new PasswordField();
        fSenha.setId("fSenhaMedico");
        fSenha.setPromptText("Crie uma senha");
        fSenha.setMaxWidth(350);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusMedico");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.getStyleClass().add("status-erro");

        Button btnSalvar = botaoAcao("Cadastrar Médico", "botao-azul");
        btnSalvar.setId("btnSalvarMedico");

        // Extraído numa variável para reaproveitar nos campos — assim Enter
        // em Nome/CPF/Especialidade/Senha confirma o cadastro, igual à aba Cliente.
        EventHandler<ActionEvent> acaoCadastrarMedico = e -> {
            if (fNome.getText().isBlank() || fCPF.getText().isBlank() || fSenha.getText().isBlank()) {
                estilo(lblStatus, "Preencha Nome, CPF e Senha.", false);
                return;
            }
            try {
                Medico medico = new Medico(
                        fNome.getText().trim(),
                        fCPF.getText().trim(),
                        fEsp.getText().trim(),
                        fSenha.getText()
                );
                medicoRepo.create(medico);
                estilo(lblStatus, "✔ Médico cadastrado com sucesso!", true);
                fNome.clear(); fCPF.clear(); fEsp.clear(); fSenha.clear();
            } catch (IllegalArgumentException ex) {
                estilo(lblStatus, ex.getMessage(), false);
            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao cadastrar médico: " + ex.getMessage(), false);
            }
        };

        btnSalvar.setOnAction(acaoCadastrarMedico);
        fNome.setOnAction(acaoCadastrarMedico);
        fCPF.setOnAction(acaoCadastrarMedico);
        fEsp.setOnAction(acaoCadastrarMedico);
        fSenha.setOnAction(acaoCadastrarMedico);

        VBox conteudo = new VBox(10,
                lblNome, fNome,
                lblCPF, fCPF,
                lblEsp, fEsp,
                lblSenha, fSenha,
                btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        // Mesma questão da aba Cliente: o VBox tem maxWidth fixo, então o
        // alinhamento precisa estar no StackPane que envolve ele (que se
        // estica até preencher a aba), não no próprio VBox.
        StackPane wrapper = new StackPane(conteudo);
        wrapper.setAlignment(Pos.TOP_CENTER);

        Tab aba = new Tab("🩺  Novo Médico", wrapper);
        aba.setId("abaMedico");
        return aba;
    }

    // -------------------------------------------------------------------------
    // Aba: Secretária
    // -------------------------------------------------------------------------
    private Tab criarAbaSecretaria() {
        Label lblNome  = new Label("Nome completo:");
        lblNome.getStyleClass().add("label-campo");
        TextField fNome = campo("Ex: Ana Paula Ferreira");
        fNome.setId("fNomeSecretaria");

        Label lblCPF   = new Label("CPF:");
        lblCPF.getStyleClass().add("label-campo");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfSecretaria");
        MascaraCpf.aplicar(fCPF);

        Label lblSenha = new Label("Senha de acesso:");
        lblSenha.getStyleClass().add("label-campo");
        PasswordField fSenha = new PasswordField();
        fSenha.setId("fSenhaSecretaria");
        fSenha.setPromptText("Crie uma senha");
        fSenha.setMaxWidth(350);

        Label lblConfirma = new Label("Confirmar senha:");
        lblConfirma.getStyleClass().add("label-campo");
        PasswordField fConfirma = new PasswordField();
        fConfirma.setId("fConfirmaSecretaria");
        fConfirma.setPromptText("Repita a senha");
        fConfirma.setMaxWidth(350);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusSecretaria");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.getStyleClass().add("status-erro");

        Button btnSalvar = botaoAcao("Cadastrar Secretária", "botao-laranja");
        btnSalvar.setId("btnSalvarSecretaria");

        // Extraído numa variável para reaproveitar nos campos — assim Enter
        // em Nome/CPF/Senha/Confirmar confirma o cadastro, igual à aba Cliente.
        EventHandler<ActionEvent> acaoCadastrarSecretaria = e -> {
            String nome    = fNome.getText().trim();
            String cpf     = fCPF.getText().trim();
            String senha   = fSenha.getText();
            String confirma = fConfirma.getText();

            if (nome.isBlank() || cpf.isBlank() || senha.isBlank()) {
                estilo(lblStatus, "Preencha Nome, CPF e Senha.", false);
                return;
            }
            if (!senha.equals(confirma)) {
                estilo(lblStatus, "As senhas não coincidem.", false);
                return;
            }
            try {
                Secretaria secretaria = new Secretaria(nome, cpf, senha);
                secretariaRepo.create(secretaria);
                estilo(lblStatus, "✔ Secretária cadastrada com sucesso!", true);
                fNome.clear(); fCPF.clear(); fSenha.clear(); fConfirma.clear();
            } catch (IllegalArgumentException ex) {
                estilo(lblStatus, ex.getMessage(), false);
            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao cadastrar secretária: " + ex.getMessage(), false);
            }
        };

        btnSalvar.setOnAction(acaoCadastrarSecretaria);
        fNome.setOnAction(acaoCadastrarSecretaria);
        fCPF.setOnAction(acaoCadastrarSecretaria);
        fSenha.setOnAction(acaoCadastrarSecretaria);
        fConfirma.setOnAction(acaoCadastrarSecretaria);

        VBox conteudo = new VBox(10,
                lblNome,     fNome,
                lblCPF,      fCPF,
                lblSenha,    fSenha,
                lblConfirma, fConfirma,
                btnSalvar,   lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        StackPane wrapper = new StackPane(conteudo);
        wrapper.setAlignment(Pos.TOP_CENTER);

        Tab aba = new Tab("🗂  Nova Secretária", wrapper);
        aba.setId("abaSecretaria");
        return aba;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(350);
        return tf;
    }

    private Button botaoAcao(String texto, String classeCor) {
        Button btn = new Button(texto);
        btn.setPrefWidth(220);
        btn.setPrefHeight(36);
        btn.getStyleClass().addAll("botao", classeCor);
        return btn;
    }

    private void estilo(Label label, String mensagem, boolean sucesso) {
        label.setText(mensagem);
        label.getStyleClass().removeAll("status-erro", "status-sucesso");
        label.getStyleClass().add(sucesso ? "status-sucesso" : "status-erro");
    }

    public BorderPane getView() {
        return view;
    }
}
