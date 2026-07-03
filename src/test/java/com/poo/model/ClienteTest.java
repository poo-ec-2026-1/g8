package com.poo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    public void deveCriarClienteEValidarDadosEId() {
        Prontuario prontuario = new Prontuario("Gripe");
        Cliente cliente = new Cliente("Maria Pereira da Silva", "718.905.727-72", 5, prontuario, "12/05/1990");

        assertEquals("Maria Pereira da Silva", cliente.getNome());
        assertNotNull(cliente.getProntuario());
        assertEquals("12/05/1990", cliente.getAniverssario());

        // Fica vermelho porque o construtor do backend ignora o 5 e chumba 0
        assertEquals(5, cliente.getId(), "O ID passado no construtor deve ser respeitado e não forçado a 0.");
    }

    @Test
    public void deveBloquearCriacaoComCpfOuDataInvalidos() {
        // Fica verde, provando que a validação funcionou
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente("Fulano", "111.111.111-11", 1, null, "99/99/9999");
        }, "O sistema deve bloquear a criação se o CPF ou a Data forem inválidos.");
    }

    @Test
    public void deveBloquearCriacaoDeClienteSemProntuario() {
        // Fica vermelho porque o sistema permite cliente com prontuário null
        assertThrows(IllegalArgumentException.class, () -> {
            new Cliente("Ciclano", "718.905.727-72", 2, null, "01/01/2000");
        }, "Não deveria ser possível criar um cliente passando null no lugar do Prontuário.");
    }
}
