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
 */
public class CadastroController {

    private final ClienteRepository    clienteRepo;
    private final MedicoRepository     medicoRepo;
    private final ProntuarioRepository prontuarioRepo;

    private BorderPane view;

    public CadastroController(ClienteRepository clienteRepo,
                               MedicoRepository medicoRepo,
                               ProntuarioRepository prontuarioRepo) {
        this.clienteRepo    = clienteRepo;
        this.medicoRepo     = medicoRepo;
        this.prontuarioRepo = prontuarioRepo;
        construirView();
    }

    private void construirView() {
        Label titulo = new Label("Cadastro");
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
        abas.getTabs().addAll(criarAbaCliente(), criarAbaMedico());

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

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");

        Label lblNasc  = new Label("Data de nascimento:");
        TextField fNasc = campo("DD/MM/AAAA");

        Label lblDoenca  = new Label("Diagnóstico / Doença (prontuário):");
        TextField fDoenca = campo("Ex: Hipertensão");

        Label lblMedicoResp = new Label("Médico responsável (opcional):");
        ComboBox<Medico> cbMedico = new ComboBox<>();
        cbMedico.setPromptText("Nenhum médico vinculado");
        cbMedico.setMaxWidth(350);
        cbMedico.setCellFactory(lv -> celulaMedico());
        cbMedico.setButtonCell(celulaMedico());
        carregarMedicosNoCombo(cbMedico);

        Label lblStatus = new Label();
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = botaoAcao("Cadastrar Cliente", "#27ae60");
        btnSalvar.setOnAction(e -> {
            if (fNome.getText().isBlank() || fCPF.getText().isBlank()) {
                estilo(lblStatus, "Preencha Nome e CPF.", false);
                return;
            }
            try {
                Prontuario prontuario = new Prontuario(fDoenca.getText().trim());
                prontuarioRepo.create(prontuario);

                Medico medicoSelecionado = cbMedico.getValue();
                if (medicoSelecionado != null) {
                    prontuarioRepo.adicionarMedicoAoHistorico(prontuario, medicoSelecionado);
                }

                Cliente cliente = new Cliente(
                    fNome.getText().trim(),
                    fCPF.getText().trim(),
                    0,
                    prontuario,
                    fNasc.getText().trim()
                );
                clienteRepo.create(cliente);

                estilo(lblStatus, "✔ Cliente cadastrado com sucesso!", true);
                fNome.clear(); fCPF.clear(); fNasc.clear(); fDoenca.clear();
                cbMedico.setValue(null);

            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao cadastrar cliente: " + ex.getMessage(), false);
            }
        });

        VBox conteudo = new VBox(10,
            lblNome, fNome,
            lblCPF, fCPF,
            lblNasc, fNasc,
            lblDoenca, fDoenca,
            lblMedicoResp, cbMedico,
            btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(420);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        return new Tab("👤  Novo Cliente", scroll);
    }

    // -------------------------------------------------------------------------
    // Aba: Médico
    // -------------------------------------------------------------------------
    private Tab criarAbaMedico() {
        Label lblNome  = new Label("Nome completo:");
        TextField fNome = campo("Ex: Dr. João Souza");

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");

        Label lblEsp   = new Label("Especialidade:");
        TextField fEsp  = campo("Ex: Cardiologia");

        Label lblSenha = new Label("Senha de acesso:");
        PasswordField fSenha = new PasswordField();
        fSenha.setPromptText("Crie uma senha");
        fSenha.setMaxWidth(350);

        Label lblStatus = new Label();
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(380);
        lblStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = botaoAcao("Cadastrar Médico", "#2980b9");
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

        return new Tab("🩺  Novo Médico", conteudo);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private void carregarMedicosNoCombo(ComboBox<Medico> cb) {
        try {
            List<Medico> medicos = medicoRepo.loadAll();
            cb.setItems(FXCollections.observableArrayList(medicos));
        } catch (SQLException e) {
            // Se falhar ao carregar médicos, deixa o combo vazio mas não trava a tela.
            MainApp.mostrarErroBanco(e);
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
