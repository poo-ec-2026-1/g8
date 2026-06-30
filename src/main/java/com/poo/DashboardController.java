import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Controller + View do Dashboard principal.
 * Exibe boas-vindas e botões de navegação para as demais telas.
 * Não interage diretamente com repositórios, então não precisa tratar SQLException.
 */
public class DashboardController {

    private final String nomeUsuario;
    private final String tipoUsuario;

    private VBox view;

    public DashboardController(String nomeUsuario, String tipoUsuario) {
        this.nomeUsuario = nomeUsuario;
        this.tipoUsuario = tipoUsuario;
        construirView();
    }

    private void construirView() {
        Label titulo = new Label("Bem-vindo(a), " + nomeUsuario + "!");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 24));

        Label tipo = new Label("Perfil: " + tipoUsuario);
        tipo.setFont(Font.font("SansSerif", 14));
        tipo.setStyle("-fx-text-fill: #555;");

        Separator sep = new Separator();
        sep.setMaxWidth(500);

        Label instrucao = new Label("O que deseja fazer?");
        instrucao.setFont(Font.font("SansSerif", 15));

        Button btnCadastro    = criarBotao("👤  Cadastrar Cliente / Médico", "#27ae60");
        Button btnAgendamento = criarBotao("📅  Agendar Consulta",          "#2980b9");
        Button btnAgenda      = criarBotao("🗓  Ver Agenda / Prontuário",   "#8e44ad");
        Button btnSair        = criarBotao("🚪  Sair",                       "#c0392b");

        btnCadastro.setOnAction(e    -> MainApp.irParaCadastro());
        btnAgendamento.setOnAction(e -> MainApp.irParaAgendamento());
        btnAgenda.setOnAction(e      -> MainApp.irParaAgenda());
        btnSair.setOnAction(e        -> MainApp.irParaLogin());

        VBox botoes = new VBox(14, btnCadastro, btnAgendamento, btnAgenda, btnSair);
        botoes.setAlignment(Pos.CENTER);

        view = new VBox(18, titulo, tipo, sep, instrucao, botoes);
        view.setAlignment(Pos.CENTER);
        view.setPadding(new Insets(40));
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

    private Button criarBotao(String texto, String cor) {
        Button btn = new Button(texto);
        btn.setPrefWidth(320);
        btn.setPrefHeight(42);
        btn.setFont(Font.font("SansSerif", 14));
        btn.setStyle(
            "-fx-background-color: " + cor + "; -fx-text-fill: white;" +
            "-fx-background-radius: 6; -fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + cor + ", -15%);" +
            "-fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + cor + ";" +
            "-fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;"
        ));
        return btn;
    }

    public VBox getView() {
        return view;
    }
}
