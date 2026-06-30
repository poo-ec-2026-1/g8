import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller + View da tela de Agenda / Prontuário.
 * - Aba "Agenda": filtra consultas por médico e exibe em tabela.
 * - Aba "Prontuário": usa Prontuario.verificarSenha() (novo método do modelo)
 *   para autenticar o médico antes de exibir o histórico do paciente.
 */
public class AgendaController {

    private final ConsultaRepository consultaRepo;
    private final MedicoRepository   medicoRepo;
    private final ClienteRepository  clienteRepo;

    private BorderPane view;

    public AgendaController(ConsultaRepository consultaRepo,
                             MedicoRepository medicoRepo,
                             ClienteRepository clienteRepo) {
        this.consultaRepo = consultaRepo;
        this.medicoRepo   = medicoRepo;
        this.clienteRepo  = clienteRepo;
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

    // -------------------------------------------------------------------------
    // Aba: Agenda
    // -------------------------------------------------------------------------
    private Tab criarAbaAgenda() {
        Label lblMedico = new Label("Filtrar por médico:");
        ComboBox<Medico> cbMedico = new ComboBox<>();
        cbMedico.setPromptText("Todos os médicos");
        cbMedico.setMaxWidth(340);
        cbMedico.setCellFactory(lv -> celulaMedico());
        cbMedico.setButtonCell(celulaMedico());

        Button btnFiltrar = new Button("🔍  Buscar");
        btnFiltrar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;" +
                            "-fx-background-radius: 5; -fx-cursor: hand;");

        Label lblErroFiltro = new Label();
        lblErroFiltro.setStyle("-fx-text-fill: #c0392b;");

        HBox filtro = new HBox(10, lblMedico, cbMedico, btnFiltrar, lblErroFiltro);
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
        colMedico.setPrefWidth(220);
        colCliente.setPrefWidth(220);

        tabela.getColumns().addAll(colData, colHorario, colMedico, colCliente);

        btnFiltrar.setOnAction(e -> {
            try {
                List<Consulta> todas = consultaRepo.loadAll();
                Medico filtroMedico = cbMedico.getValue();
                if (filtroMedico != null) {
                    todas = todas.stream()
                        .filter(c -> c.getMedico() != null &&
                                     c.getMedico().getId() == filtroMedico.getId())
                        .collect(Collectors.toList());
                }
                tabela.setItems(FXCollections.observableArrayList(todas));
                lblErroFiltro.setText("");
            } catch (SQLException ex) {
                lblErroFiltro.setText("Erro ao buscar consultas: " + ex.getMessage());
            }
        });

        // Carrega médicos no combo e consultas na tabela ao abrir a aba
        try {
            cbMedico.setItems(FXCollections.observableArrayList(medicoRepo.loadAll()));
            tabela.setItems(FXCollections.observableArrayList(consultaRepo.loadAll()));
        } catch (SQLException ex) {
            lblErroFiltro.setText("Erro ao carregar dados: " + ex.getMessage());
        }

        VBox conteudo = new VBox(0, filtro, tabela);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        return new Tab("🗓  Agenda", conteudo);
    }

    // -------------------------------------------------------------------------
    // Aba: Prontuário
    // -------------------------------------------------------------------------
    private Tab criarAbaProntuario() {
        Label instrucao = new Label("Informe a senha de um médico do histórico para acessar o prontuário:");
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
        areaProntuario.setPrefHeight(220);
        areaProntuario.setFont(Font.font("Monospaced", 13));

        Label lblStatus = new Label();
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(420);
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

            try {
                // Busca o cliente pelo CPF
                Cliente encontrado = clienteRepo.loadAll().stream()
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

                // Usa o novo método do modelo: verifica se a senha pertence
                // a algum médico do histórico do prontuário.
                if (!p.verificarSenha(senha)) {
                    estilo(lblStatus, "Senha incorreta! Acesso negado.", false);
                    areaProntuario.clear();
                    return;
                }

                // Monta o texto exibido na tela reaproveitando os dados que
                // exibirProntuario() imprimiria no console.
                StringBuilder sb = new StringBuilder();
                sb.append("Paciente : ").append(encontrado.getNome()).append("\n");
                sb.append("CPF      : ").append(encontrado.getCPF()).append("\n");
                sb.append("Nasc.    : ").append(encontrado.getAniverssario()).append("\n");
                sb.append("─".repeat(40)).append("\n");
                sb.append("Doenças do paciente: ").append(p.getDoença()).append("\n\n");
                sb.append("Histórico de médicos que atenderam:\n");
                List<Medico> historico = p.getHistorico();
                if (historico.isEmpty()) {
                    sb.append("  (nenhum médico registrado)\n");
                } else {
                    for (Medico m : historico) {
                        sb.append("  - Dr(a). ").append(m.getNome()).append("\n");
                    }
                }

                areaProntuario.setText(sb.toString());
                estilo(lblStatus, "✔ Solicitação aceita. Prontuário carregado.", true);

            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao acessar prontuário: " + ex.getMessage(), false);
                areaProntuario.clear();
            }
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
        conteudo.setMaxWidth(520);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        return new Tab("📋  Prontuário", scroll);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private ListCell<Medico> celulaMedico() {
        return new ListCell<>() {
            @Override protected void updateItem(Medico m, boolean empty) {
                super.updateItem(m, empty);
                setText(empty || m == null ? null : "Dr(a). " + m.getNome() + " — " + m.getEspecialidade());
            }
        };
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
