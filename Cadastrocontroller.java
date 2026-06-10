import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;


public class Cadastrocontroller {

    private final ClienteRepository     clienteRepo;
    private final MedicoRepository      medicoRepo;
    private final ProntuarioRepository  prontuarioRepo;

    private BorderPane view;

    public Cadastrocontroller(ClienteRepository clienteRepo,
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

   
    private Tab criarAbaCliente() {
        Label lblNome  = new Label("Nome completo:");
        TextField fNome = campo("Ex: Maria Silva");

        Label lblCPF   = new Label("CPF:");
        TextField fCPF  = campo("000.000.000-00");

        Label lblNasc  = new Label("Data de nascimento:");
        TextField fNasc = campo("DD/MM/AAAA");

        Label lblDoenca  = new Label("Diagnóstico / Doença (prontuário):");
        TextField fDoenca = campo("Ex: Hipertensão");

        Label lblStatus = new Label();
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
            } catch (Exception ex) {
                estilo(lblStatus, "Erro: " + ex.getMessage(), false);
            }
        });

        VBox conteudo = new VBox(10,
            lblNome, fNome,
            lblCPF, fCPF,
            lblNasc, fNasc,
            lblDoenca, fDoenca,
            btnSalvar, lblStatus
        );
        conteudo.setPadding(new Insets(24));
        conteudo.setMaxWidth(400);

        ScrollPane scroll = new ScrollPane(conteudo);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        Tab tab = new Tab("👤  Novo Cliente", scroll);
        return tab;
    }

    
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
            } catch (Exception ex) {
                estilo(lblStatus, "Erro: " + ex.getMessage(), false);
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
        conteudo.setMaxWidth(400);

        Tab tab = new Tab("🩺  Novo Médico", conteudo);
        return tab;
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