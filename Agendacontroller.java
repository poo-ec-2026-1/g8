import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.util.List;
import java.util.stream.Collectors;


public class Agendacontroller {

    private final ConsultaRepository consultaRepo;
    private final MedicoRepository   medicoRepo;

    private BorderPane view;

    public Agendacontroller(ConsultaRepository consultaRepo, MedicoRepository medicoRepo) {
        this.consultaRepo = consultaRepo;
        this.medicoRepo   = medicoRepo;
        construirView();
    }

    private void construirView() {
       
        Label titulo = new Label("Agenda & Prontuários");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;" +
                           "-fx-font-size: 13px; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard("", ""));

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.setStyle("-fx-background-color: #ecf0f1;");

        
        TabPane abas = new TabPane();
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        abas.getTabs().addAll(criarAbaAgenda(), criarAbaProntuario());

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(abas);
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

  
    @SuppressWarnings("unchecked")
    private Tab criarAbaAgenda() {
        List<Medico> medicos = medicoRepo.loadAll();

        Label lblMedico = new Label("Filtrar por médico:");
        ComboBox<Medico> cbMedico = new ComboBox<>(FXCollections.observableArrayList(medicos));
        cbMedico.setPromptText("Todos os médicos");
        cbMedico.setMaxWidth(340);
        cbMedico.setCellFactory(lv -> celulaMediaco());
        cbMedico.setButtonCell(celulaMediaco());

        Button btnFiltrar = new Button("🔍  Buscar");
        btnFiltrar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;" +
                            "-fx-background-radius: 5; -fx-cursor: hand;");

        HBox filtro = new HBox(10, lblMedico, cbMedico, btnFiltrar);
        filtro.setAlignment(Pos.CENTER_LEFT);
        filtro.setPadding(new Insets(14, 14, 6, 14));

        
        TableView<Consulta> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhuma consulta encontrada."));

        TableColumn<Consulta, String> colData    = new TableColumn<>("Data");
        TableColumn<Consulta, String> colHorario = new TableColumn<>("Horário");
        TableColumn<Consulta, String> colMedico  = new TableColumn<>("Médico");
        TableColumn<Consulta, String> colCliente = new TableColumn<>("Paciente");

        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));

        
        colMedico.setCellValueFactory(cd -> {
            Medico m = cd.getValue().getMedico();
            return new javafx.beans.property.SimpleStringProperty(
                m != null ? "Dr(a). " + m.getNome() : "—"
            );
        });
        colCliente.setCellValueFactory(cd -> {
            Cliente c = cd.getValue().getCliente();
            return new javafx.beans.property.SimpleStringProperty(
                c != null ? c.getNome() : "—"
            );
        });

        colData.setPrefWidth(100);
        colHorario.setPrefWidth(90);
        colMedico.setPrefWidth(200);
        colCliente.setPrefWidth(200);

        tabela.getColumns().addAll(colData, colHorario, colMedico, colCliente);

        btnFiltrar.setOnAction(e -> {
            List<Consulta> todas = consultaRepo.loadAll();
            Medico filtroMedico = cbMedico.getValue();
            if (filtroMedico != null) {
                todas = todas.stream()
                    .filter(c -> c.getMedico() != null &&
                                 c.getMedico().getId() == filtroMedico.getId())
                    .collect(Collectors.toList());
            }
            tabela.setItems(FXCollections.observableArrayList(todas));
        });

        // Carrega tudo ao abrir
        tabela.setItems(FXCollections.observableArrayList(consultaRepo.loadAll()));

        VBox conteudo = new VBox(0, filtro, tabela);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        return new Tab("🗓  Agenda", conteudo);
    }

    private ListCell<Medico> celulaMediaco() {
        return new ListCell<>() {
            @Override protected void updateItem(Medico m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "Dr(a). " + m.getNome() + " — " + m.getEspecialidade());
            }
        };
    }

   
    private Tab criarAbaProntuario() {
        Label instrucao = new Label("Informe a senha do médico responsável para acessar o prontuário:");
        instrucao.setFont(Font.font("SansSerif", 13));
        instrucao.setWrapText(true);

        Label lblCPF = new Label("CPF do paciente:");
        TextField fCPF = campo("000.000.000-00");

        Label lblSenha = new Label("Senha do médico:");
        PasswordField fSenha = new PasswordField();
        fSenha.setPromptText("Senha de acesso");
        fSenha.setMaxWidth(350);

        TextArea areaProntuario = new TextArea();
        areaProntuario.setEditable(false);
        areaProntuario.setPromptText("O prontuário aparecerá aqui...");
        areaProntuario.setPrefHeight(200);
        areaProntuario.setFont(Font.font("Monospaced", 13));

        Label lblStatus = new Label();
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnConsultar = new Button("🔓  Acessar Prontuário");
        btnConsultar.setPrefWidth(220);
        btnConsultar.setPrefHeight(36);
        btnConsultar.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;" +
                              "-fx-background-radius: 5; -fx-cursor: hand;");

        btnConsultar.setOnAction(e -> {
            String cpf   = fCPF.getText().trim();
            String senha = fSenha.getText();

            if (cpf.isBlank() || senha.isBlank()) {
                estilo(lblStatus, "Preencha CPF e senha.", false);
                areaProntuario.clear();
                return;
            }

           
            boolean senhaValida = medicoRepo.loadAll().stream()
                .anyMatch(m -> m.getSenha().equals(senha));

            if (!senhaValida) {
                estilo(lblStatus, "Senha inválida.", false);
                areaProntuario.clear();
                return;
            }

            
            Cliente encontrado = MainApp.getClienteRepo().loadAll().stream()
                .filter(c -> c.getCPF().equals(cpf))
                .findFirst().orElse(null);

            if (encontrado == null) {
                estilo(lblStatus, "Paciente não encontrado.", false);
                areaProntuario.clear();
                return;
            }

            
            Prontuario p = encontrado.getProntuario();
            if (p == null) {
                estilo(lblStatus, "Este paciente não possui prontuário.", false);
                areaProntuario.clear();
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Paciente : ").append(encontrado.getNome()).append("\n");
            sb.append("CPF      : ").append(encontrado.getCPF()).append("\n");
            sb.append("Nasc.    : ").append(encontrado.getAniverssario()).append("\n");
            sb.append("─".repeat(40)).append("\n");
            sb.append("Diagnóstico: ").append(p.getDoença()).append("\n\n");
            sb.append("Histórico de médicos que atenderam:\n");
            if (p.getHistorico().isEmpty()) {
                sb.append("  (nenhum médico registrado)\n");
            } else {
                for (Medico m : p.getHistorico()) {
                    sb.append("  • Dr(a). ").append(m.getNome())
                      .append(" — ").append(m.getEspecialidade()).append("\n");
                }
            }
            areaProntuario.setText(sb.toString());
            estilo(lblStatus, "✔ Prontuário carregado.", true);
        });

        VBox conteudo = new VBox(10,
            instrucao,
            lblCPF, fCPF,
            lblSenha, fSenha,
            btnConsultar, lblStatus,
            new Label("Prontuário:"),
            areaProntuario
        );
        conteudo.setPadding(new Insets(20));
        conteudo.setMaxWidth(500);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        return new Tab("📋  Prontuário", scroll);
    }

 
    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(350);
        return tf;
    }

    private void estilo(Label label, String msg, boolean ok) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: " + (ok ? "#27ae60" : "#c0392b") + ";");
    }

    public BorderPane getView() {
        return view;
    }
}