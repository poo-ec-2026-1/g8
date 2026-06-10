import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


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

        // Inicializa o banco de dados e repositórios
        database        = new Database("hospital.db");
        clienteRepo     = new ClienteRepository(database);
        medicoRepo      = new MedicoRepository(database);
        secretariaRepo  = new SecretariaRepository(database);
        consultaRepo    = new ConsultaRepository(database);
        prontuarioRepo  = new ProntuarioRepository(database);

        primaryStage.setTitle("Sistema Hospitalar");
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);

        irParaLogin();
        primaryStage.show();
    }


    // -------------------------------------------------------------------------

    public static void irParaLogin() {
        Logincontroller login = new Logincontroller(secretariaRepo, medicoRepo);
        primaryStage.setScene(new Scene(login.getView(), 700, 500));
    }

    public static void irParaDashboard(String nomeUsuario, String tipoUsuario) {
        Dashboardcontroller dash = new Dashboardcontroller(nomeUsuario, tipoUsuario);
        primaryStage.setScene(new Scene(dash.getView(), 700, 500));
    }

    public static void irParaCadastro() {
        Cadastrocontroller cad = new Cadastrocontroller(clienteRepo, medicoRepo, prontuarioRepo);
        primaryStage.setScene(new Scene(cad.getView(), 750, 580));
    }

    public static void irParaAgendamento() {
        Agendamentocontroller ag = new Agendamentocontroller(consultaRepo, clienteRepo, medicoRepo);
        primaryStage.setScene(new Scene(ag.getView(), 750, 520));
    }

    public static void irParaAgenda() {
        Agendacontroller agenda = new Agendacontroller(consultaRepo, medicoRepo);
        primaryStage.setScene(new Scene(agenda.getView(), 800, 560));
    }

    
    public static ClienteRepository    getClienteRepo()    { return clienteRepo; }
    public static MedicoRepository     getMedicoRepo()     { return medicoRepo; }
    public static ConsultaRepository   getConsultaRepo()   { return consultaRepo; }
    public static ProntuarioRepository getProntuarioRepo() { return prontuarioRepo; }

    public static void main(String[] args) {
        launch(args);
    }
}