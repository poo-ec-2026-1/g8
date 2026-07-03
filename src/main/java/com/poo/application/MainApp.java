package com.poo.application;

import com.poo.controller.AgendaController;
import com.poo.controller.AgendamentoController;
import com.poo.controller.AtualizarProntuarioController;
import com.poo.controller.CadastroController;
import com.poo.controller.CancelamentoController;
import com.poo.controller.DashboardController;
import com.poo.controller.LoginController;
import com.poo.controller.RegistroAtendimentoController;
import com.poo.repository.ClienteRepository;
import com.poo.repository.ConsultaRepository;
import com.poo.repository.Database;
import com.poo.repository.MedicoRepository;
import com.poo.repository.ProntuarioRepository;
import com.poo.repository.SecretariaRepository;

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

    // Única Scene da aplicação — trocamos apenas o root ao navegar entre
    // telas (em vez de criar um novo Scene a cada tela), para não resetar
    // o tamanho da janela nem sair do modo tela cheia a cada navegação.
    private static Scene scene;

    // Estado de sessão do usuário logado — evita perder nome/perfil
    // ao navegar entre telas e voltar ao Dashboard.
    private static String usuarioLogadoNome;
    private static String usuarioLogadoTipo;

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

        new DatabaseSeeder(secretariaRepo).popularDadosIniciais();

        primaryStage.setTitle("Sistema Hospitalar");
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(520);

        LoginController login = new LoginController(secretariaRepo, medicoRepo);
        scene = new Scene(login.getView(), 720, 520);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    // -------------------------------------------------------------------------
    // Métodos de navegação — chamados pelos Controllers
    //
    // Todas trocam apenas o root da Scene única (scene.setRoot(...)) em vez
    // de criar "new Scene(...)" a cada tela — é isso que preserva o tamanho
    // da janela (e o modo tela cheia) entre navegações.
    // -------------------------------------------------------------------------

    public static void irParaLogin() {
        // Limpa a sessão ao deslogar
        usuarioLogadoNome = null;
        usuarioLogadoTipo = null;
        LoginController login = new LoginController(secretariaRepo, medicoRepo);
        scene.setRoot(login.getView());
    }

    /**
     * Usado no login (e em qualquer fluxo que precise definir explicitamente
     * o usuário). Guarda nome e perfil na sessão para que telas internas
     * possam "voltar" sem perder essa informação.
     */
    public static void irParaDashboard(String nomeUsuario, String tipoUsuario) {
        usuarioLogadoNome = nomeUsuario;
        usuarioLogadoTipo = tipoUsuario;
        DashboardController dash = new DashboardController(nomeUsuario, tipoUsuario);
        scene.setRoot(dash.getView());
    }

    /**
     * Usado pelos botões "← Voltar" das telas internas. Reaproveita o
     * nome/perfil já guardados na sessão em vez de passar strings vazias.
     */
    public static void irParaDashboard() {
        irParaDashboard(usuarioLogadoNome, usuarioLogadoTipo);
    }

    public static String getUsuarioLogadoTipo() {
        return usuarioLogadoTipo;
    }

    public static void irParaCadastro() {
        CadastroController cad = new CadastroController(clienteRepo, medicoRepo, prontuarioRepo, secretariaRepo);
        scene.setRoot(cad.getView());
    }

    public static void irParaAgendamento() {
        AgendamentoController ag = new AgendamentoController(consultaRepo, clienteRepo, medicoRepo);
        scene.setRoot(ag.getView());
    }

    public static void irParaCancelamento() {
        CancelamentoController cc = new CancelamentoController(consultaRepo, clienteRepo, medicoRepo);
        scene.setRoot(cc.getView());
    }

    public static void irParaAgenda(String tipoUsuario) {
        AgendaController agenda = new AgendaController(consultaRepo, medicoRepo, clienteRepo, tipoUsuario);
        scene.setRoot(agenda.getView());
    }

    public static void irParaRegistroAtendimento() {
        RegistroAtendimentoController ra = new RegistroAtendimentoController(
            consultaRepo, clienteRepo, medicoRepo, prontuarioRepo);
        scene.setRoot(ra.getView());
    }

    public static void irParaAtualizarProntuario() {
        AtualizarProntuarioController ap = new AtualizarProntuarioController(
            clienteRepo, prontuarioRepo, medicoRepo);
        scene.setRoot(ap.getView());
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