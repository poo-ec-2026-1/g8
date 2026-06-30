import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Atualizar Prontuário.
 *
 * Substitui a antiga criação automática de Prontuario dentro do cadastro
 * de Cliente. Agora o Cliente é cadastrado sem prontuário (null) e o
 * prontuário é criado/atualizado aqui, separadamente — tipicamente pelo
 * médico durante ou após o atendimento.
 *
 * Fluxo:
 *  1. Busca o Cliente pelo CPF.
 *  2. Se o cliente não tem prontuário ainda → cria um novo (Prontuario.create)
 *     e vincula ao cliente via ClienteRepository.update().
 *  3. Se já tem prontuário → atualiza o texto da doença/diagnóstico
 *     (Prontuario.setDoença + ProntuarioRepository.update).
 *  4. Opcionalmente vincula um médico responsável ao histórico.
 */
public class AtualizarProntuarioController {

    private final ClienteRepository    clienteRepo;
    private final ProntuarioRepository prontuarioRepo;
    private final MedicoRepository     medicoRepo;

    private BorderPane view;

    // Cliente atualmente carregado na tela (após busca por CPF)
    private Cliente clienteCarregado;

    public AtualizarProntuarioController(ClienteRepository clienteRepo,
                                          ProntuarioRepository prontuarioRepo,
                                          MedicoRepository medicoRepo) {
        this.clienteRepo    = clienteRepo;
        this.prontuarioRepo = prontuarioRepo;
        this.medicoRepo     = medicoRepo;
        construirView();
    }

    private void construirView() {
        // ----- Cabeçalho -----
        Label titulo = new Label("Atualizar Prontuário");
        titulo.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9;" +
                           "-fx-font-size: 13px; -fx-cursor: hand;");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.setStyle("-fx-background-color: #ecf0f1;");

        // ----- Busca por CPF -----
        Label lblCPF = new Label("CPF do paciente:");
        TextField fCPF = campo("000.000.000-00");

        Label lblBuscaStatus = new Label();
        lblBuscaStatus.setWrapText(true);
        lblBuscaStatus.setMaxWidth(420);
        lblBuscaStatus.setFont(Font.font("SansSerif", 13));

        Button btnBuscar = new Button("🔍  Buscar Paciente");
        btnBuscar.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;" +
                           "-fx-background-radius: 5; -fx-cursor: hand;");

        // ----- Painel do prontuário (preenchido após a busca) -----
        Label lblPacienteInfo = new Label();
        lblPacienteInfo.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px;");
        lblPacienteInfo.setWrapText(true);

        Label lblDoenca = new Label("Diagnóstico / Doença:");
        TextArea fDoenca = new TextArea();
        fDoenca.setPromptText("Descreva o diagnóstico ou histórico clínico do paciente...");
        fDoenca.setPrefHeight(130);
        fDoenca.setMaxWidth(460);
        fDoenca.setWrapText(true);
        fDoenca.setDisable(true); // habilitado só depois de uma busca bem-sucedida

        Label lblMedicoResp = new Label("Vincular médico responsável (opcional):");
        ComboBox<Medico> cbMedico = new ComboBox<>();
        cbMedico.setPromptText("Nenhum médico selecionado");
        cbMedico.setMaxWidth(460);
        cbMedico.setCellFactory(lv -> celulaMedico());
        cbMedico.setButtonCell(celulaMedico());
        cbMedico.setDisable(true);

        Label lblSalvarStatus = new Label();
        lblSalvarStatus.setWrapText(true);
        lblSalvarStatus.setMaxWidth(460);
        lblSalvarStatus.setFont(Font.font("SansSerif", 13));

        Button btnSalvar = new Button("💾  Salvar Prontuário");
        btnSalvar.setPrefWidth(220);
        btnSalvar.setPrefHeight(38);
        btnSalvar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;" +
                           "-fx-font-size: 14px; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSalvar.setDisable(true);

        // ----- Ação: Buscar -----
        btnBuscar.setOnAction(e -> {
            String cpf = fCPF.getText().trim();
            if (cpf.isBlank()) {
                estilo(lblBuscaStatus, "Informe o CPF do paciente.", false);
                return;
            }
            try {
                Cliente encontrado = clienteRepo.loadAll().stream()
                    .filter(c -> c.getCPF().equals(cpf))
                    .findFirst().orElse(null);

                if (encontrado == null) {
                    estilo(lblBuscaStatus, "Paciente não encontrado.", false);
                    clienteCarregado = null;
                    lblPacienteInfo.setText("");
                    fDoenca.clear();
                    fDoenca.setDisable(true);
                    cbMedico.setDisable(true);
                    btnSalvar.setDisable(true);
                    return;
                }

                clienteCarregado = encontrado;
                lblPacienteInfo.setText(
                    "Paciente: " + encontrado.getNome() +
                    "   |   Nasc.: " + encontrado.getAniverssario()
                );

                Prontuario prontuario = encontrado.getProntuario();
                if (prontuario != null) {
                    fDoenca.setText(prontuario.getDoença() != null ? prontuario.getDoença() : "");
                    estilo(lblBuscaStatus, "Paciente encontrado. Prontuário existente carregado para edição.", true);
                } else {
                    fDoenca.clear();
                    estilo(lblBuscaStatus, "Paciente encontrado. Ainda não possui prontuário — será criado um novo ao salvar.", true);
                }

                fDoenca.setDisable(false);
                cbMedico.setDisable(false);
                btnSalvar.setDisable(false);

            } catch (SQLException ex) {
                estilo(lblBuscaStatus, "Erro ao buscar paciente: " + ex.getMessage(), false);
            }
        });

        // ----- Ação: Salvar (criar ou atualizar) -----
        btnSalvar.setOnAction(e -> {
            if (clienteCarregado == null) {
                estilo(lblSalvarStatus, "Busque um paciente antes de salvar.", false);
                return;
            }

            try {
                Prontuario prontuario = clienteCarregado.getProntuario();

                if (prontuario == null) {
                    // CRIA um novo prontuário e vincula ao cliente
                    prontuario = new Prontuario(fDoenca.getText().trim());
                    prontuarioRepo.create(prontuario);

                    clienteCarregado.setProntuario(prontuario);
                    clienteRepo.update(clienteCarregado);

                    estilo(lblSalvarStatus, "✔ Prontuário criado e vinculado ao paciente!", true);
                } else {
                    // ATUALIZA o prontuário existente
                    prontuario.setDoença(fDoenca.getText().trim());
                    prontuarioRepo.update(prontuario);

                    estilo(lblSalvarStatus, "✔ Prontuário atualizado com sucesso!", true);
                }

                // Vincula médico responsável ao histórico, se selecionado
                Medico medicoSelecionado = cbMedico.getValue();
                if (medicoSelecionado != null) {
                    prontuarioRepo.adicionarMedicoAoHistorico(prontuario, medicoSelecionado);
                    cbMedico.setValue(null);
                }

            } catch (SQLException ex) {
                estilo(lblSalvarStatus, "Erro ao salvar prontuário: " + ex.getMessage(), false);
            }
        });

        carregarMedicos(cbMedico, lblSalvarStatus);

        VBox buscaBox = new VBox(8, lblCPF, fCPF, btnBuscar, lblBuscaStatus);
        buscaBox.setPadding(new Insets(0, 0, 16, 0));

        Separator sep = new Separator();
        sep.setMaxWidth(460);

        VBox prontuarioBox = new VBox(10,
            lblPacienteInfo,
            lblDoenca, fDoenca,
            lblMedicoResp, cbMedico,
            btnSalvar, lblSalvarStatus
        );

        VBox form = new VBox(16, buscaBox, sep, prontuarioBox);
        form.setPadding(new Insets(24));
        form.setMaxWidth(500);

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(scroll);
        view.setStyle("-fx-background-color: #f4f6f8;");
    }

    private void carregarMedicos(ComboBox<Medico> cb, Label lblStatus) {
        try {
            List<Medico> medicos = medicoRepo.loadAll();
            cb.setItems(FXCollections.observableArrayList(medicos));
        } catch (SQLException e) {
            estilo(lblStatus, "Erro ao carregar médicos: " + e.getMessage(), false);
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
        tf.setMaxWidth(460);
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