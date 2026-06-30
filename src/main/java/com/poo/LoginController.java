import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Login.
 * Autentica Secretárias (por CPF) e Médicos (por CPF + senha).
 *
 * NOTA: a classe Secretaria do back-end atual não expõe um getter de senha,
 * então a autenticação de secretárias é feita apenas pelo CPF. Se o back-end
 * vier a expor getSenha() em Secretaria, basta validar a senha aqui também
 * (ver comentário marcado mais abaixo).
 */
public class LoginController {

    private final SecretariaRepository secretariaRepo;
    private final MedicoRepository     medicoRepo;

    private VBox view;

    private TextField     campoCPF;
    private PasswordField campoSenha;
    private Label         labelErro;

    public LoginController(SecretariaRepository secretariaRepo, MedicoRepository medicoRepo) {
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
        campoCPF = new TextField();
        campoCPF.setPromptText("000.000.000-00");
        campoCPF.setMaxWidth(300);

        Label lblSenha = new Label("Senha:");
        campoSenha = new PasswordField();
        campoSenha.setPromptText("Sua senha (médicos) ou deixe em branco (secretárias)");
        campoSenha.setMaxWidth(300);
        campoSenha.setOnAction(e -> tentarLogin());

        labelErro = new Label();
        labelErro.setStyle("-fx-text-fill: #c0392b;");
        labelErro.setWrapText(true);
        labelErro.setMaxWidth(300);

        Button btnLogin = new Button("Entrar");
        btnLogin.setPrefWidth(300);
        btnLogin.setPrefHeight(38);
        btnLogin.setStyle(
            "-fx-background-color: #2980b9; -fx-text-fill: white;" +
            "-fx-font-size: 14px; -fx-cursor: hand; -fx-background-radius: 5;"
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

        if (cpf.isEmpty()) {
            labelErro.setText("Informe o CPF.");
            return;
        }

        try {
            // Tenta autenticar como Médico primeiro (CPF + senha)
            List<Medico> medicos = medicoRepo.loadAll();
            for (Medico m : medicos) {
                if (m.getCPF().equals(cpf)) {
                    if (m.getSenha().equals(senha)) {
                        MainApp.irParaDashboard(m.getNome(), "Médico");
                        return;
                    } else {
                        labelErro.setText("Senha incorreta para este médico.");
                        campoSenha.clear();
                        return;
                    }
                }
            }

            // Tenta autenticar como Secretária (apenas CPF, modelo atual
            // não expõe senha de Secretaria via getter público)
            List<Secretaria> secretarias = secretariaRepo.loadAll();
            for (Secretaria s : secretarias) {
                if (s.getCPF().equals(cpf)) {
                    MainApp.irParaDashboard(s.getNome(), "Secretária");
                    return;
                }
            }

            labelErro.setText("CPF não encontrado.");
            campoSenha.clear();

        } catch (SQLException e) {
            MainApp.mostrarErroBanco(e);
        }
    }

    public VBox getView() {
        return view;
    }
}
