package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Agenda;
import com.poo.model.Consulta;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Controller + View do Dashboard principal.
 *
 * Os botões exibidos variam de acordo com o perfil do usuário logado,
 * seguindo o Diagrama de Casos de Uso do projeto:
 *
 * Secretária → Cadastrar Paciente, Cancelar Consulta, Agendar Consulta,
 * Visualizar Agenda
 *
 * Médico     → Visualizar Agenda, Acessar Prontuário, Registrar Atendimento
 */
public class DashboardController {

    private final String nomeUsuario;
    private final String tipoUsuario;

    private VBox view;

    public DashboardController(String nomeUsuario, String tipoUsuario) {
        this.nomeUsuario = nomeUsuario;
        this.tipoUsuario = tipoUsuario;
        construirView();
    }

    private void construirView() {
        // ----- Cabeçalho -----
        Label titulo = new Label("Bem-vindo(a), " + nomeUsuario + "!");
        titulo.setId("lblTitulo");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));

        Label lblTipo = new Label("Perfil: " + tipoUsuario);
        lblTipo.setId("lblTipo");
        lblTipo.setFont(Font.font("SansSerif", 14));
        lblTipo.setStyle("-fx-text-fill: #555;");

        Separator sep = new Separator();
        sep.setMaxWidth(500);

        Label instrucao = new Label("O que deseja fazer?");
        instrucao.setFont(Font.font("SansSerif", 15));

        // ----- Botões por perfil -----
        VBox botoes = new VBox(14);
        botoes.setAlignment(Pos.CENTER);

        if ("Secretária".equals(tipoUsuario)) {
            Button btnCadastro    = criarBotao("👤  Cadastrar Paciente",   "#27ae60");
            btnCadastro.setId("btnCadastro");

            Button btnAgendar     = criarBotao("📅  Agendar Consulta",     "#2980b9");
            btnAgendar.setId("btnAgendar");

            Button btnCancelar    = criarBotao("❌  Cancelar Consulta",    "#e74c3c");
            btnCancelar.setId("btnCancelar");

            Button btnAgenda      = criarBotao("🗓  Visualizar Agenda",    "#8e44ad");
            btnAgenda.setId("btnAgendaSecretaria");

            btnCadastro.setOnAction(e -> MainApp.irParaCadastro());
            btnAgendar.setOnAction(e  -> MainApp.irParaAgendamento());
            btnCancelar.setOnAction(e -> MainApp.irParaCancelamento());
            btnAgenda.setOnAction(e   -> MainApp.irParaAgenda(tipoUsuario));

            botoes.getChildren().addAll(btnCadastro, btnAgendar, btnCancelar, btnAgenda);

        } else if ("Médico".equals(tipoUsuario)) {
            Button btnAgenda      = criarBotao("🗓  Visualizar Agenda",          "#2980b9");
            btnAgenda.setId("btnAgendaMedico");

            Button btnProntuario  = criarBotao("📋  Acessar Prontuário",         "#8e44ad");
            btnProntuario.setId("btnProntuario");

            Button btnAtendimento = criarBotao("✏️  Registrar Atendimento",      "#27ae60");
            btnAtendimento.setId("btnAtendimento");

            Button btnAtualizarProntuario = criarBotao("📝  Atualizar Prontuário", "#16a085");
            btnAtualizarProntuario.setId("btnAtualizarProntuario");

            btnAgenda.setOnAction(e      -> MainApp.irParaAgenda(tipoUsuario));
            btnProntuario.setOnAction(e  -> MainApp.irParaAgenda(tipoUsuario));   // abre na aba prontuário
            btnAtendimento.setOnAction(e -> MainApp.irParaRegistroAtendimento());
            btnAtualizarProntuario.setOnAction(e -> MainApp.irParaAtualizarProntuario());

            botoes.getChildren().addAll(btnAgenda, btnProntuario, btnAtendimento, btnAtualizarProntuario);
        }

        // Botão Sair sempre visível
        Button btnSair = criarBotao("🚪  Sair", "#7f8c8d");
        btnSair.setId("btnSair");

        btnSair.setOnAction(e -> MainApp.irParaLogin());
        botoes.getChildren().add(btnSair);

        view = new VBox(18, titulo, lblTipo, sep, instrucao, botoes);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

    private Button criarBotao(String texto, String cor) {
        Button btn = new Button(texto);
        btn.setPrefWidth(320);
        btn.setPrefHeight(42);
        btn.setFont(Font.font("SansSerif", 14));
        btn.setStyle(
                "-fx-background-color: " + cor + "; -fx-text-fill: white;" +
                        "-fx-background-radius: 6; -fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: derive(" + cor + ", -15%);" +
                        "-fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + cor + ";" +
                        "-fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"
        ));
        return btn;
    }

    public VBox getView() {
        return view;
    }
}