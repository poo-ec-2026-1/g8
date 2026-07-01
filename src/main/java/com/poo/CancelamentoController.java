package com.poo;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Cancelamento de Consultas.
 * Exclusivo para o perfil Secretária (conforme diagrama de casos de uso).
 *
 * Fluxo:
 *  1. Lista todas as consultas agendadas em uma TableView.
 *  2. Secretária seleciona uma linha e clica em "Cancelar Consulta".
 *  3. Uma confirmação é exibida antes de deletar do banco.
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
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;" +
                           "-fx-font-size: 13px; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.setStyle("-fx-background-color: #ecf0f1;");

        // ----- Instrução -----
        Label instrucao = new Label("Selecione a consulta que deseja cancelar e clique no botão abaixo.");
        instrucao.setFont(Font.font("SansSerif", 13));
        instrucao.setPadding(new Insets(10, 14, 4, 14));

        // ----- Tabela -----
        TableView<Consulta> tabela = new TableView<>();
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
        lblStatus.setFont(Font.font("SansSerif", 13));
        lblStatus.setWrapText(true);

        Button btnCancelar = new Button("❌  Cancelar Consulta Selecionada");
        btnCancelar.setPrefHeight(38);
        btnCancelar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;" +
                             "-fx-font-size: 13px; -fx-background-radius: 5; -fx-cursor: hand;");

        btnCancelar.setOnAction(e -> {
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
        });

        Button btnAtualizar = new Button("🔄  Atualizar lista");
        btnAtualizar.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;" +
                              "-fx-background-radius: 5; -fx-cursor: hand;");
        btnAtualizar.setOnAction(e -> carregarConsultas(tabela, lblStatus));

        HBox rodape = new HBox(12, btnCancelar, btnAtualizar, lblStatus);
        rodape.setAlignment(Pos.CENTER_LEFT);
        rodape.setPadding(new Insets(10, 14, 14, 14));

        VBox centro = new VBox(0, instrucao, tabela, rodape);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(centro);
        view.setStyle("-fx-background-color: #f4f6f8;");

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
        label.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#c0392b") + ";");
    }

    public BorderPane getView() {
        return view;
    }
}