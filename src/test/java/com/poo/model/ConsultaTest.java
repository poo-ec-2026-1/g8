package com.poo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConsultaTest {

    @Test
    public void deveManterOIDPassadoNoConstrutor() {
        Medico medico = new Medico("Dr. Teste", "123.456.789-09", "Cardiologia", "senha");
        Cliente cliente = new Cliente("Paciente", "718.905.727-72", "01/01/2000");
        Consulta consulta = new Consulta(10, "23/08/2026", "14:30", medico, cliente);

        // Fica vermelho porque o sistema está chumbando 0 no lugar do 10
        assertEquals(10, consulta.getId(), "O ID passado no construtor deve ser mantido.");
    }

    @Test
    public void deveBloquearConsultaSemMedicoOuClienteEHorarioInvalido() {
        // Fica vermelho porque o sistema aceita agendar fantasma e horas absurdas, em vez de lançar erro
        assertThrows(IllegalArgumentException.class, () -> {
            new Consulta(1, "12/12/2026", "25:61", null, null);
        }, "Deve lançar erro ao agendar com horas impossíveis ou sem cliente/médico.");
    }
}