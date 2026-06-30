import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.sql.SQLException;

/**
 * Ponto de entrada da aplicação JavaFX.
 * Responsável por iniciar o Stage principal e gerenciar a troca de telas.
 *
 * NOTA DE VERSÃO: os repositórios do back-end agora propagam SQLException
 * em todos os métodos de CRUD (create, loadFromId, loadAll, update, delete).
 * Por isso, toda a camada de frontend trata essa exceção localmente e exibe
 * um Alert amigável ao usuário em vez de deixar o programa quebrar.
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    // Instâncias dos repositórios compartilhadas por toda a aplicação
    private static Database database;
    private static ClienteRepository    clienteRepo;
    private static MedicoRepository     medicoRepo;
    private static SecretariaRepository secretariaRepo;
    private static ConsultaRepository   consultaRepo;
    private static ProntuarioRepository prontuarioRepo;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        try {
            database        = new Database("hospital.db");
            clienteRepo     = new ClienteRepository(database);
            medicoRepo      = new MedicoRepository(database);
            secretariaRepo  = new SecretariaRepository(database);
            consultaRepo    = new ConsultaRepository(database);
            prontuarioRepo  = new ProntuarioRepository(database);
        } catch (RuntimeException e) {
            // Os repositórios lançam RuntimeException dentro de setDatabase()
            // quando o DAO não consegue ser inicializado.
            mostrarErroFatal("Não foi possível conectar ao banco de dados:\n" + e.getMessage());
            return;
        }

        primaryStage.setTitle("Sistema Hospitalar");
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(520);

        irParaLogin();
        primaryStage.show();
    }

    // -------------------------------------------------------------------------
    // Métodos de navegação — chamados pelos Controllers
    // -------------------------------------------------------------------------

    public static void irParaLogin() {
        LoginController login = new LoginController(secretariaRepo, medicoRepo);
        primaryStage.setScene(new Scene(login.getView(), 720, 520));
    }

    public static void irParaDashboard(String nomeUsuario, String tipoUsuario) {
        DashboardController dash = new DashboardController(nomeUsuario, tipoUsuario);
        primaryStage.setScene(new Scene(dash.getView(), 720, 520));
    }

    public static void irParaCadastro() {
        CadastroController cad = new CadastroController(clienteRepo, medicoRepo, prontuarioRepo);
        primaryStage.setScene(new Scene(cad.getView(), 760, 600));
    }

    public static void irParaAgendamento() {
        AgendamentoController ag = new AgendamentoController(consultaRepo, clienteRepo, medicoRepo);
        primaryStage.setScene(new Scene(ag.getView(), 760, 540));
    }

    public static void irParaAgenda() {
        AgendaController agenda = new AgendaController(consultaRepo, medicoRepo, clienteRepo);
        primaryStage.setScene(new Scene(agenda.getView(), 820, 580));
    }

    // -------------------------------------------------------------------------
    // Getters de repositório (caso algum controller precise acessar diretamente)
    // -------------------------------------------------------------------------

    public static ClienteRepository    getClienteRepo()    { return clienteRepo; }
    public static MedicoRepository     getMedicoRepo()     { return medicoRepo; }
    public static SecretariaRepository getSecretariaRepo() { return secretariaRepo; }
    public static ConsultaRepository   getConsultaRepo()   { return consultaRepo; }
    public static ProntuarioRepository getProntuarioRepo() { return prontuarioRepo; }

    // -------------------------------------------------------------------------
    // Utilitário de exibição de erro — usado por todos os Controllers
    // -------------------------------------------------------------------------

    /**
     * Exibe um Alert de erro padronizado para qualquer SQLException capturada
     * nas telas. Centraliza o tratamento para manter os Controllers limpos.
     */
    public static void mostrarErroBanco(SQLException e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro de banco de dados");
        alert.setHeaderText("Não foi possível completar a operação.");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private static void mostrarErroFatal(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro fatal");
        alert.setHeaderText("A aplicação não pôde ser iniciada.");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
