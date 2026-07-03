package com.poo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProntuarioTest {

    @Test
    public void deveCriarProntuarioERecuperarDados() {
        Prontuario prontuario = new Prontuario("Febre");
        prontuario.setId(15);

        assertEquals("Febre", prontuario.getDoença());
        assertEquals(15, prontuario.getId());
    }

    @Test
    public void deveBloquearDoencaVaziaEIdNegativo() {
        Prontuario prontuario = new Prontuario("Febre");

        // Fica vermelho, o setId aceita negativo
        assertThrows(IllegalArgumentException.class, () -> {
            prontuario.setId(-5);
        }, "O método setId() não deveria aceitar números negativos.");

        // Fica vermelho, o construtor aceita vazio
        assertThrows(IllegalArgumentException.class, () -> {
            new Prontuario("");
        }, "Não deveria ser possível criar um prontuário com a doença vazia.");
    }

    @Test
    public void deveRetornarListaVaziaSeHistoricoForNulo() {
        Prontuario prontuario = new Prontuario("Enxaqueca");
        assertNotNull(prontuario.getHistorico());
        assertTrue(prontuario.getHistorico().isEmpty());
    }
}