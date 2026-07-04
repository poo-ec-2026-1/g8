package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;
import com.poo.repository.ClienteRepository;
import com.poo.repository.ConsultaRepository;
import com.poo.repository.MedicoRepository;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Cancelamento de Consultas.
 * Exclusivo para o perfil Secretária (conforme diagrama de casos de uso).
 *
 * Fluxo:
 * 1. Lista todas as consultas agendadas em uma TableView.
 * 2. Secretária seleciona uma linha e clica em "Cancelar Consulta".
 * 3. Uma confirmação é exibida antes de deletar do banco.
 */
public class CancelamentoController {

    private final ConsultaRepository consultaRepo;
    private final ClienteRepository  clienteRepo;
    private final MedicoRepository   medicoRepo;

    private BorderPane view;

    public CancelamentoController(ConsultaRepository consultaRepo,
                                  ClienteRepository clienteRepo,
                                  MedicoRepository medicoRepo) {
        this.consultaRepo = consultaRepo;
        this.clienteRepo  = clienteRepo;
        this.medicoRepo   = medicoRepo;
        construirView();
    }

    private void construirView() {
        // ----- Cabeçalho -----
        Label titulo = new Label("Cancelar Consulta");
        titulo.getStyleClass().add("titulo-tela");

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setId("btnVoltar");
        btnVoltar.getStyleClass().add("btn-voltar");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.getStyleClass().add("app-header");

        // ----- Instrução -----
        Label instrucao = new Label("Selecione a consulta que deseja cancelar e clique no botão abaixo.");
        instrucao.getStyleClass().add("texto-instrucao");
        instrucao.setPadding(new Insets(10, 14, 4, 14));

        // ----- Tabela -----
        TableView<Consulta> tabela = new TableView<>();
        tabela.setId("tabelaConsultas");
        tabela.setPlaceholder(new Label("Nenhuma consulta agendada."));

        TableColumn<Consulta, String> colData    = new TableColumn<>("Data");
        TableColumn<Consulta, String> colHorario = new TableColumn<>("Horário");
        TableColumn<Consulta, String> colMedico  = new TableColumn<>("Médico");
        TableColumn<Consulta, String> colCliente = new TableColumn<>("Paciente");

        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colMedico.setCellValueFactory(cd -> {
            Medico m = cd.getValue().getMedico();
            return new javafx.beans.property.SimpleStringProperty(
                    m != null ? "Dr(a). " + m.getNome() : "—");
        });
        colCliente.setCellValueFactory(cd -> {
            Cliente c = cd.getValue().getCliente();
            return new javafx.beans.property.SimpleStringProperty(
                    c != null ? c.getNome() : "—");
        });

        colData.setPrefWidth(100);
        colHorario.setPrefWidth(90);
        colMedico.setPrefWidth(220);
        colCliente.setPrefWidth(220);

        tabela.getColumns().addAll(colData, colHorario, colMedico, colCliente);
        tabela.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // ----- Rodapé com status e botão -----
        Label lblStatus = new Label();
        lblStatus.setId("lblStatus");
        lblStatus.getStyleClass().add("status-erro");
        lblStatus.setWrapText(true);

        Button btnCancelar = new Button("❌  Cancelar Consulta Selecionada");
        btnCancelar.setId("btnCancelar");
        btnCancelar.setPrefHeight(38);
        btnCancelar.getStyleClass().addAll("botao", "botao-vermelho");

        // Extraído numa variável para poder ser disparado tanto pelo clique
        // no botão quanto pelo Enter com uma linha selecionada na tabela.
        EventHandler<ActionEvent> acaoCancelar = e -> {
            Consulta selecionada = tabela.getSelectionModel().getSelectedItem();
            if (selecionada == null) {
                estilo(lblStatus, "Selecione uma consulta na tabela.", false);
                return;
            }

            // Diálogo de confirmação
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar cancelamento");
            confirmacao.setHeaderText("Tem certeza que deseja cancelar esta consulta?");
            String info = "Data: " + selecionada.getData() +
                    "\nHorário: " + selecionada.getHorario();
            if (selecionada.getMedico() != null)
                info += "\nMédico: Dr(a). " + selecionada.getMedico().getNome();
            if (selecionada.getCliente() != null)
                info += "\nPaciente: " + selecionada.getCliente().getNome();
            confirmacao.setContentText(info);

            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.OK) {
                    try {
                        consultaRepo.delete(selecionada);
                        tabela.getItems().remove(selecionada);
                        estilo(lblStatus, "✔ Consulta cancelada com sucesso.", true);
                    } catch (SQLException ex) {
                        estilo(lblStatus, "Erro ao cancelar: " + ex.getMessage(), false);
                    }
                }
            });
        };

        btnCancelar.setOnAction(acaoCancelar);

        // Enter com uma linha selecionada na tabela abre a mesma confirmação
        // do botão (não há campo de texto nesta tela para prender o Enter).
        tabela.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                acaoCancelar.handle(new ActionEvent());
            }
        });

        Button btnAtualizar = new Button("🔄  Atualizar lista");
        btnAtualizar.setId("btnAtualizar");
        btnAtualizar.getStyleClass().addAll("botao", "botao-cinza");
        btnAtualizar.setOnAction(e -> carregarConsultas(tabela, lblStatus));

        HBox rodape = new HBox(12, btnCancelar, btnAtualizar, lblStatus);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.setPadding(new Insets(10, 14, 14, 14));

        VBox centro = new VBox(0, instrucao, tabela, rodape);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(centro);
        view.getStyleClass().add("app-bg");

        carregarConsultas(tabela, lblStatus);
    }

    private void carregarConsultas(TableView<Consulta> tabela, Label lblStatus) {
        try {
            List<Consulta> todas = consultaRepo.loadAll();
            tabela.setItems(FXCollections.observableArrayList(todas));
            lblStatus.setText("");
        } catch (SQLException e) {
            estilo(lblStatus, "Erro ao carregar consultas: " + e.getMessage(), false);
        }
    }

    private void estilo(Label label, String msg, boolean ok) {
        label.setText(msg);
        label.getStyleClass().removeAll("status-erro", "status-sucesso");
        label.getStyleClass().add(ok ? "status-sucesso" : "status-erro");
    }

    public BorderPane getView() {
        return view;
    }
}