import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.util.List;


public class Agendamentocontroller {

    private final ConsultaRepository consultaRepo;
    private final ClienteRepository  clienteRepo;
    private final MedicoRepository   medicoRepo;

    private BorderPane view;

    public Agendamentocontroller(ConsultaRepository consultaRepo,
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
        btnVoltar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;" +
                           "-fx-font-size: 13px; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard("", ""));

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.setStyle("-fx-background-color: #ecf0f1;");

       
        Label lblMedico = new Label("Médico:");
        List<Medico> medicos = medicoRepo.loadAll();
        ComboBox<Medico> cbMedico = new ComboBox<>(FXCollections.observableArrayList(medicos));
        cbMedico.setPromptText("Selecione o médico");
        cbMedico.setMaxWidth(380);
        cbMedico.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Medico m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "Dr(a). " + m.getNome() + " — " + m.getEspecialidade());
            }
        });
        cbMedico.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Medico m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "Dr(a). " + m.getNome() + " — " + m.getEspecialidade());
            }
        });

        
        Label lblCliente = new Label("Paciente:");
        List<Cliente> clientes = clienteRepo.loadAll();
        ComboBox<Cliente> cbCliente = new ComboBox<>(FXCollections.observableArrayList(clientes));
        cbCliente.setPromptText("Selecione o paciente");
        cbCliente.setMaxWidth(380);
        cbCliente.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNome() + " — CPF: " + c.getCPF());
            }
        });
        cbCliente.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Cliente c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getNome() + " — CPF: " + c.getCPF());
            }
        });

        
        Label lblData = new Label("Data (DD/MM/AAAA):");
        TextField fData = new TextField();
        fData.setPromptText("Ex: 15/07/2025");
        fData.setMaxWidth(380);

        
        Label lblHorario = new Label("Horário (HH:MM):");
        TextField fHorario = new TextField();
        fHorario.setPromptText("Ex: 14:30");
        fHorario.setMaxWidth(380);

       
        Label lblStatus = new Label();
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = new Button("✔  Confirmar Agendamento");
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

            // Verifica conflito de horário para o mesmo médico
            boolean conflito = consultaRepo.loadAll().stream().anyMatch(c ->
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
    }

    private void estilo(Label label, String msg, boolean ok) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#c0392b") + ";");
    }

    public BorderPane getView() {
        return view;
    }
}