package com.poo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MedicoTest {

    @Test
    public void deveCriarMedicoCompletoComHeranca() {
        Medico medico = new Medico("Dr. Carlos Alves", "123.456.789-09", "Neurologia", "senha123");

        assertEquals("Dr. Carlos Alves", medico.getNome());
        assertEquals("123.456.789-09", medico.getCPF());
        assertEquals("Neurologia", medico.getEspecialidade());
        assertEquals("senha123", medico.getSenha());
    }

    @Test
    public void deveCriarMedicoComConstrutorVazio() {
        assertDoesNotThrow(() -> new Medico());
    }

    @Test
    public void deveBloquearMedicoComCpfInvalido() {
        // Fica verde, pois a validação do ValidadorUtils está aqui
        assertThrows(IllegalArgumentException.class, () -> {
            new Medico("Dr. Fulano", "111.111.111-11", "Cardiologia", "1234");
        });
    }

    @Test
    public void deveBloquearMedicoComEspecialidadeOuSenhaVazia() {
        // Fica vermelho porque o sistema ainda aceita médico sem especialidade e sem senha
        assertThrows(IllegalArgumentException.class, () -> {
            new Medico("Dr. Teste", "123.456.789-09", "", "");
        }, "O construtor deve proibir Strings vazias para especialidade e senha.");
    }
}