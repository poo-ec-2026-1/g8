package com.poo.controller;

import com.poo.application.MainApp;
import com.poo.model.Cliente;
import com.poo.model.Consulta;
import com.poo.model.Medico;
import com.poo.model.Prontuario;
import com.poo.repository.ClienteRepository;
import com.poo.repository.ConsultaRepository;
import com.poo.repository.MedicoRepository;
import com.poo.repository.ProntuarioRepository;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller + View da tela de Registro de Atendimento.
 * Exclusivo para o perfil Médico (conforme diagrama de casos de uso).
 *
 * Fluxo:
 * 1. Médico seleciona uma de suas consultas agendadas via ComboBox.
 * 2. Informa observações / diagnóstico do atendimento.
 * 3. Ao confirmar, o médico é adicionado ao histórico do Prontuário
 * do paciente via ProntuarioRepository.adicionarMedicoAoHistorico().
 */
public class RegistroAtendimentoController {

    private final ConsultaRepository   consultaRepo;
    private final ClienteRepository    clienteRepo;
    private final MedicoRepository     medicoRepo;
    private final ProntuarioRepository prontuarioRepo;

    private BorderPane view;

    public RegistroAtendimentoController(ConsultaRepository consultaRepo,
                                         ClienteRepository clienteRepo,
                                         MedicoRepository medicoRepo,
                                         ProntuarioRepository prontuarioRepo) {
        this.consultaRepo   = consultaRepo;
        this.clienteRepo    = clienteRepo;
        this.medicoRepo     = medicoRepo;
        this.prontuarioRepo = prontuarioRepo;
        construirView();
    }

    private void construirView() {
        // ----- Cabeçalho -----
        Label titulo = new Label("Registrar Atendimento");
        titulo.getStyleClass().add("titulo-tela");

        Button btnVoltar = new Button("← Voltar");
        btnVoltar.setId("btnVoltar");
        btnVoltar.getStyleClass().add("btn-voltar");
        btnVoltar.setOnAction(e -> MainApp.irParaDashboard());

        HBox cabecalho = new HBox(16, btnVoltar, titulo);
        cabecalho.setAlignment(Pos.CENTER_LEFT);
        cabecalho.setPadding(new Insets(16, 24, 8, 24));
        cabecalho.getStyleClass().add("app-header");

        // ----- Formulário -----
        Label lblConsulta = new Label("Selecione a consulta realizada:");
        lblConsulta.getStyleClass().add("label-campo");
        ComboBox<Consulta> cbConsulta = new ComboBox<>();
        cbConsulta.setId("cbConsulta");
        cbConsulta.setPromptText("Escolha uma consulta");
        cbConsulta.setMaxWidth(460);
        cbConsulta.setCellFactory(lv -> celulaConsulta());
        cbConsulta.setButtonCell(celulaConsulta());

        // Painel de informações do paciente (preenchido ao selecionar consulta)
        Label lblInfoPaciente = new Label();
        lblInfoPaciente.setId("lblInfoPaciente");
        lblInfoPaciente.getStyleClass().add("texto-info");
        lblInfoPaciente.setWrapText(true);

        cbConsulta.setOnAction(e -> {
            Consulta c = cbConsulta.getValue();
            if (c != null && c.getCliente() != null) {
                Cliente cl = c.getCliente();
                lblInfoPaciente.setText(
                        "Paciente: " + cl.getNome() +
                                "   |   CPF: " + cl.getCPF() +
                                "   |   Nasc.: " + cl.getAniverssario()
                );
            } else {
                lblInfoPaciente.setText("");
            }
        });

        Label lblObs = new Label("Observações / diagnóstico do atendimento:");
        lblObs.getStyleClass().add("label-campo");
        TextArea fObs = new TextArea();
        fObs.setId("fObs");
        fObs.setPromptText("Descreva o atendimento, evolução do quadro, prescrições, etc.");
        fObs.setPrefHeight(130);
        fObs.setMaxWidth(460);
        fObs.setWrapText(true);

        Label lblStatus = new Label();
        lblStatus.setId("lblStatus");
        lblStatus.getStyleClass().add("status-erro");
        lblStatus.setWrapText(true);
        lblStatus.setMaxWidth(460);

        Button btnRegistrar = new Button("✔  Confirmar Atendimento");
        btnRegistrar.setId("btnRegistrar");
        btnRegistrar.setPrefWidth(240);
        btnRegistrar.setPrefHeight(38);
        btnRegistrar.getStyleClass().addAll("botao", "botao-verde");

        btnRegistrar.setOnAction(e -> {
            Consulta consultaSelecionada = cbConsulta.getValue();

            if (consultaSelecionada == null) {
                estilo(lblStatus, "Selecione uma consulta.", false);
                return;
            }

            Cliente paciente = consultaSelecionada.getCliente();
            Medico  medico   = consultaSelecionada.getMedico();

            if (paciente == null || medico == null) {
                estilo(lblStatus, "Dados da consulta incompletos.", false);
                return;
            }

            Prontuario prontuario = paciente.getProntuario();
            if (prontuario == null) {
                estilo(lblStatus,
                        "Este paciente ainda não possui prontuário. Crie um na tela " +
                                "\"Atualizar Prontuário\" antes de registrar o atendimento.", false);
                return;
            }

            // DIAGNÓSTICO TEMPORÁRIO — remova após confirmar a causa do bug.
            // Se "id aninhado" aparecer como 0, confirma que o objeto Prontuario
            // vindo de Consulta->Cliente->Prontuario está incompleto.
            System.out.println("[DEBUG] id do prontuário aninhado (Consulta->Cliente->Prontuario): "
                    + prontuario.getId());

            try {
                // CORREÇÃO: o objeto `prontuario` aqui vem aninhado de
                // Consulta -> Cliente -> Prontuario (via foreignAutoRefresh).
                // Em alguns casos o ORMLite popula esse objeto aninhado apenas
                // parcialmente (ex.: só o id), o que faz prontuarioRepo.update()
                // rodar um UPDATE que não afeta nenhuma linha — sem lançar
                // exceção, mas também sem persistir nada.
                //
                // Para garantir que estamos atualizando o registro real e
                // completo, buscamos o Prontuario de novo, fresco, pelo ID.
                Prontuario prontuarioReal = prontuarioRepo.loadFromId(prontuario.getId());
                if (prontuarioReal == null) {
                    estilo(lblStatus,
                            "Erro: prontuário com id=" + prontuario.getId() +
                                    " não foi encontrado no banco.", false);
                    return;
                }
                prontuario = prontuarioReal;

                // Registra o médico no histórico do prontuário
                prontuarioRepo.adicionarMedicoAoHistorico(prontuario, medico);

                // Atualiza a doença/observação no prontuário se o médico informou algo
                String obs = fObs.getText().trim();
                if (!obs.isBlank()) {
                    String diagnosticoAtual = prontuario.getDoença();
                    String novoTexto = (diagnosticoAtual == null || diagnosticoAtual.isBlank())
                            ? obs
                            : diagnosticoAtual + "\n\n[Atendimento registrado]\n" + obs;
                    prontuario.setDoença(novoTexto);
                    prontuarioRepo.update(prontuario);
                }

                estilo(lblStatus,
                        "✔ Atendimento registrado! Médico adicionado ao histórico do prontuário de "
                                + paciente.getNome() + ".", true);

                cbConsulta.setValue(null);
                lblInfoPaciente.setText("");
                fObs.clear();

            } catch (SQLException ex) {
                estilo(lblStatus, "Erro ao registrar atendimento: " + ex.getMessage(), false);
            }
        });

        VBox form = new VBox(12,
                lblConsulta,  cbConsulta,
                lblInfoPaciente,
                lblObs,       fObs,
                btnRegistrar, lblStatus
        );
        form.setPadding(new Insets(24));
        form.setMaxWidth(500);

        // O VBox tem maxWidth, então o ScrollPane não consegue esticá-lo —
        // o alinhamento precisa estar no StackPane que o envolve.
        StackPane wrapper = new StackPane(form);
        wrapper.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(wrapper);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        view = new BorderPane();
        view.setTop(cabecalho);
        view.setCenter(scroll);
        view.getStyleClass().add("app-bg");

        carregarConsultas(cbConsulta, lblStatus);
    }

    private void carregarConsultas(ComboBox<Consulta> cb, Label lblStatus) {
        try {
            List<Consulta> todas = consultaRepo.loadAll();
            cb.setItems(FXCollections.observableArrayList(todas));
        } catch (SQLException e) {
            estilo(lblStatus, "Erro ao carregar consultas: " + e.getMessage(), false);
        }
    }

    private ListCell<Consulta> celulaConsulta() {
        return new ListCell<>() {
            @Override protected void updateItem(Consulta c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                } else {
                    String medico  = c.getMedico()  != null ? c.getMedico().getNome()  : "?";
                    String cliente = c.getCliente() != null ? c.getCliente().getNome() : "?";
                    setText(c.getData() + " " + c.getHorario() +
                            " — Dr(a). " + medico + " | Paciente: " + cliente);
                }
            }
        };
    }

    private void estilo(Label label, String msg, boolean ok) {
        label.setText(msg);
        label.getStyleClass().removeAll("status-erro", "status-sucesso");
        label.getStyleClass().add(ok ? "status-sucesso" : "status-erro");
    }

    public BorderPane getView() {
        return view;
    }
}