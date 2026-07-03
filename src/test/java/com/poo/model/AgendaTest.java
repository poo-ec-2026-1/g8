package com.poo.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AgendaTest {

    @Test
    public void deveLancarErroAoTentarAgendamentoDuplicado() {
        Agenda agenda = new Agenda(new ArrayList<>());
        Medico m = new Medico("Dr. Anderson Silva", "123.456.789-09", "Cardiologia", "senha321");
        Cliente c = new Cliente("Júlia Tereza", "718.905.727-72", "01/01/2000");

        agenda.novaConsulta(new Consulta(1, "20/10/2026", "14:00", m, c));

        // Fica vermelho porque a Agenda avisa no System.out, mas não lança Exception.
        assertThrows(RuntimeException.class, () -> {
            agenda.novaConsulta(new Consulta(2, "20/10/2026", "14:00", m, c));
        }, "A Agenda deve lançar uma Exception quando o horário já estiver ocupado, para avisar o Frontend.");
    }

    @Test
    public void deveLancarErroNaExibicaoDaAgendaComSenhaIncorreta() {
        Agenda agenda = new Agenda(new ArrayList<>());
        Medico medico = new Medico("Dr. Fulano", "123.456.789-09", "Ortopedia", "123456");

        // Fica vermelho porque o metodo exibirAgenda apenas dá System.out e retorna void.
        assertThrows(RuntimeException.class, () -> {
            agenda.exibirAgenda(medico, "senha_errada");
        }, "Deve lançar Exception de segurança quando a senha estiver incorreta.");
    }
}