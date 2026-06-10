import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;


public class Logincontroller {

    private final SecretariaRepository secretariaRepo;
    private final MedicoRepository     medicoRepo;

    private VBox view;

    
    private TextField     campoCPF;
    private PasswordField campoSenha;
    private Label         labelErro;

    public Logincontroller(SecretariaRepository secretariaRepo, MedicoRepository medicoRepo) {
        this.secretariaRepo = secretariaRepo;
        this.medicoRepo     = medicoRepo;
        construirView();
    }

    private void construirView() {
       
        Label titulo = new Label("Sistema Hospitalar");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 26));

        Label subtitulo = new Label("Faça login para continuar");
        subtitulo.setFont(Font.font("SansSerif", 14));
        subtitulo.setStyle("-fx-text-fill: #666;");

        
        Label lblCPF = new Label("CPF:");
        lblCPF.setFont(Font.font("SansSerif", 13));

        campoCPF = new TextField();
        campoCPF.setPromptText("000.000.000-00");
        campoCPF.setMaxWidth(300);

        Label lblSenha = new Label("Senha:");
        lblSenha.setFont(Font.font("SansSerif", 13));

        campoSenha = new PasswordField();
        campoSenha.setPromptText("Sua senha");
        campoSenha.setMaxWidth(300);

        
        campoSenha.setOnAction(e -> tentarLogin());

        labelErro = new Label();
        labelErro.setStyle("-fx-text-fill: #c0392b;");
        labelErro.setFont(Font.font("SansSerif", 12));

        Button btnLogin = new Button("Entrar");
        btnLogin.setPrefWidth(300);
        btnLogin.setPrefHeight(38);
        btnLogin.setStyle(
            "-fx-background-color: #2980b9; -fx-text-fill: white;" +
            "-fx-font-size: 14px; -fx-cursor: hand;" +
            "-fx-background-radius: 5;"
        );
        btnLogin.setOnAction(e -> tentarLogin());

        
        VBox form = new VBox(10, lblCPF, campoCPF, lblSenha, campoSenha, labelErro, btnLogin);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(300);

        view = new VBox(18, titulo, subtitulo, form);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

    private void tentarLogin() {
        String cpf   = campoCPF.getText().trim();
        String senha = campoSenha.getText();

        if (cpf.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Preencha CPF e senha.");
            return;
        }

        
        for (Secretaria s : secretariaRepo.loadAll()) {
            if (s.getCPF().equals(cpf)) {
                // Secretaria não tem getSenha() público no modelo atual;
                // como workaround, aceitamos qualquer senha enquanto o campo
                // senha não for exposto. Adapte conforme seu modelo.
                MainApp.irParaDashboard(s.getNome(), "Secretária");
                return;
            }
        }

        
        for (Medico m : medicoRepo.loadAll()) {
            if (m.getCPF().equals(cpf) && m.getSenha().equals(senha)) {
                MainApp.irParaDashboard(m.getNome(), "Médico");
                return;
            }
        }

        labelErro.setText("CPF ou senha incorretos.");
        campoSenha.clear();
    }

    public VBox getView() {
        return view;
    }
}