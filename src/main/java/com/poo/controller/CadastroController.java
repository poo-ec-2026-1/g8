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

import javafx.collections.FXCollections;
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
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setId("btnVoltar");
        btnVoltar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;" +
                "-fx-font-size: 13px; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.setStyle("-fx-background-color: #ecf0f1;");

        TabPane abas = new TabPane();
        abas.setId("tabPaneCadastro");
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        abas.getTabs().addAll(criarAbaCliente(), criarAbaMedico(), criarAbaSecretaria());

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(abas);
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

    // -------------------------------------------------------------------------
    // Aba: Cliente
    // -------------------------------------------------------------------------
    private Tab criarAbaCliente() {
        Label lblNome  = new Label("Nome completo:");
        TextField fNome = campo("Ex: Maria Silva");
        fNome.setId("fNomeCliente");

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfCliente");
        MascaraCpf.aplicar(fCPF);

        Label lblNasc  = new Label("Data de nascimento:");
        TextField fNasc = campo("DD/MM/AAAA");
        fNasc.setId("fNascCliente");

        Label lblAviso = new Label(
                "O prontuário do paciente é criado posteriormente, na tela " +
                        "\"Atualizar Prontuário\" (disponível para o médico)."
        );
        lblAviso.setWrapText(true);
        lblAviso.setMaxWidth(380);
        lblAviso.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusCliente");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = botaoAcao("Cadastrar Cliente", "#27ae60");
        btnSalvar.setId("btnSalvarCliente");
        btnSalvar.setOnAction(e -> {
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
        });

        VBox conteudo = new VBox(10,
                lblNome, fNome,
                lblCPF, fCPF,
                lblNasc, fNasc,
                lblAviso,
                btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        ScrollPane scroll = new ScrollPane(conteudo);
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
        TextField fNome = campo("Ex: Dr. João Souza");
        fNome.setId("fNomeMedico");

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfMedico");
        MascaraCpf.aplicar(fCPF);

        Label lblEsp   = new Label("Especialidade:");
        TextField fEsp  = campo("Ex: Cardiologia");
        fEsp.setId("fEspMedico");

        Label lblSenha = new Label("Senha de acesso:");
        PasswordField fSenha = new PasswordField();
        fSenha.setId("fSenhaMedico");
        fSenha.setPromptText("Crie uma senha");
        fSenha.setMaxWidth(350);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusMedico");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = botaoAcao("Cadastrar Médico", "#2980b9");
        btnSalvar.setId("btnSalvarMedico");
        btnSalvar.setOnAction(e -> {
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
        });

        VBox conteudo = new VBox(10,
                lblNome, fNome,
                lblCPF, fCPF,
                lblEsp, fEsp,
                lblSenha, fSenha,
                btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        Tab aba = new Tab("🩺  Novo Médico", conteudo);
        aba.setId("abaMedico");
        return aba;
    }

    // -------------------------------------------------------------------------
    // Aba: Secretária
    // -------------------------------------------------------------------------
    private Tab criarAbaSecretaria() {
        Label lblNome  = new Label("Nome completo:");
        TextField fNome = campo("Ex: Ana Paula Ferreira");
        fNome.setId("fNomeSecretaria");

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");
        fCPF.setId("fCpfSecretaria");
        MascaraCpf.aplicar(fCPF);

        Label lblSenha = new Label("Senha de acesso:");
        PasswordField fSenha = new PasswordField();
        fSenha.setId("fSenhaSecretaria");
        fSenha.setPromptText("Crie uma senha");
        fSenha.setMaxWidth(350);

        Label lblConfirma = new Label("Confirmar senha:");
        PasswordField fConfirma = new PasswordField();
        fConfirma.setId("fConfirmaSecretaria");
        fConfirma.setPromptText("Repita a senha");
        fConfirma.setMaxWidth(350);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatusSecretaria");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = botaoAcao("Cadastrar Secretária", "#e67e22");
        btnSalvar.setId("btnSalvarSecretaria");
        btnSalvar.setOnAction(e -> {
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
        });

        VBox conteudo = new VBox(10,
                lblNome,     fNome,
                lblCPF,      fCPF,
                lblSenha,    fSenha,
                lblConfirma, fConfirma,
                btnSalvar,   lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        Tab aba = new Tab("🗂  Nova Secretária", conteudo);
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

    private Button botaoAcao(String texto, String cor) {
        Button btn = new Button(texto);
        btn.setPrefWidth(220);
        btn.setPrefHeight(36);
        btn.setStyle("-fx-background-color: " + cor + "; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-background-radius: 5; -fx-cursor: hand;");
        return btn;
    }

    private void estilo(Label label, String mensagem, boolean sucesso) {
        label.setText(mensagem);
        label.setStyle("-fx-text-fill: " + (sucesso ? "#27ae60" : "#c0392b") + ";");
    }

    public BorderPane getView() {
        return view;
    }
}