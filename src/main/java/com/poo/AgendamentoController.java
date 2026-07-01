package com.poo;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Agendamento de Consultas.
 * Seleciona médico e cliente via ComboBox, define data e horário.
 *
 * A verificação de conflito de horário é feita aqui no frontend (consultando
 * todas as consultas via loadAll()), já que a versão atual de Consulta não
 * expõe um método de busca filtrada no repositório.
 */
public class AgendamentoController {

    private final ConsultaRepository consultaRepo;
    private final ClienteRepository  clienteRepo;
    private final MedicoRepository   medicoRepo;

    private BorderPane view;

    public AgendamentoController(ConsultaRepository consultaRepo,
                                 ClienteRepository clienteRepo,
                                 MedicoRepository medicoRepo) {
        this.consultaRepo = consultaRepo;
        this.clienteRepo  = clienteRepo;
        this.medicoRepo   = medicoRepo;
        construirView();
    }

    private void construirView() {
        Label titulo = new Label("Agendar Consulta");
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

        Label lblMedico = new Label("Médico:");
        ComboBox<Medico> cbMedico = new ComboBox<>();
        cbMedico.setId("cbMedico");
        cbMedico.setPromptText("Selecione o médico");
        cbMedico.setMaxWidth(380);
        cbMedico.setCellFactory(lv -> celulaMedico());
        cbMedico.setButtonCell(celulaMedico());

        Label lblCliente = new Label("Paciente:");
        ComboBox<Cliente> cbCliente = new ComboBox<>();
        cbCliente.setId("cbCliente");
        cbCliente.setPromptText("Selecione o paciente");
        cbCliente.setMaxWidth(380);
        cbCliente.setCellFactory(lv -> celulaCliente());
        cbCliente.setButtonCell(celulaCliente());

        Label lblData = new Label("Data (DD/MM/AAAA):");
        TextField fData = new TextField();
        fData.setId("fData");
        fData.setPromptText("Ex: 15/07/2026");
        fData.setMaxWidth(380);

        Label lblHorario = new Label("Horário (HH:MM):");
        TextField fHorario = new TextField();
        fHorario.setId("fHorario");
        fHorario.setPromptText("Ex: 14:30");
        fHorario.setMaxWidth(380);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatus");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(400);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = new Button("✔  Confirmar Agendamento");
        btnSalvar.setId("btnSalvar");
        btnSalvar.setPrefWidth(260);
        btnSalvar.setPrefHeight(38);
        btnSalvar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");

        btnSalvar.setOnAction(e -> {
            Medico  medico  = cbMedico.getValue();
            Cliente cliente = cbCliente.getValue();
            String  data    = fData.getText().trim();
            String  horario = fHorario.getText().trim();

            if (medico == null || cliente == null || data.isBlank() || horario.isBlank()) {
                estilo(lblStatus, "Preencha todos os campos.", false);
                return;
            }

            try {
                List<Consulta> todas = consultaRepo.loadAll();
                boolean conflito = todas.stream().anyMatch(c ->
                        c.getMedico() != null &&
                                c.getMedico().getId() == medico.getId() &&
                                c.getData().equals(data) &&
                                c.getHorario().equals(horario)
                );

                if (conflito) {
                    estilo(lblStatus, "⚠ Horário já ocupado para este médico.", false);
                    return;
                }

                Consulta nova = new Consulta(0, data, horario, medico, cliente);
                consultaRepo.create(nova);

                estilo(lblStatus, "✔ Consulta agendada com sucesso!", true);
                cbMedico.setValue(null);
                cbCliente.setValue(null);
                fData.clear();
                fHorario.clear();

            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao agendar consulta: " + ex.getMessage(), false);
            }
        });

        VBox form = new VBox(10,
                lblMedico,  cbMedico,
                lblCliente, cbCliente,
                lblData,    fData,
                lblHorario, fHorario,
                btnSalvar,  lblStatus
        );
        form.setPadding(new Insets(28));
        form.setMaxWidth(440);

        StackPane centro = new StackPane(form);
        centro.setAlignment(Pos.TOP_CENTER);

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(centro);
        view.setStyle("-fx-background-color: #f4f6f8;");

        // Carrega os combos de médico/cliente após montar a tela,
        // para já exibir um erro amigável se o banco falhar.
        carregarCombos(cbMedico, cbCliente, lblStatus);
    }

    private void carregarCombos(ComboBox<Medico> cbMedico, ComboBox<Cliente> cbCliente, Label lblStatus) {
        try {
            cbMedico.setItems(FXCollections.observableArrayList(medicoRepo.loadAll()));
            cbCliente.setItems(FXCollections.observableArrayList(clienteRepo.loadAll()));
        } catch (SQLException e) {
            estilo(lblStatus, "Erro ao carregar médicos/pacientes: " + e.getMessage(), false);
        }
    }

    private ListCell<Medico> celulaMedico() {
        return new ListCell<>() {
            @Override protected void updateItem(Medico m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "Dr(a). " + m.getNome() + " — " + m.getEspecialidade());
            }
        };
    }

    private ListCell<Cliente> celulaCliente() {
        return new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNome() + " — CPF: " + c.getCPF());
            }
        };
    }

    private void estilo(Label label, String msg, boolean ok) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#c0392b") + ";");
    }

    public BorderPane getView() {
        return view;
    }
}