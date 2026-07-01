package com.poo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    public void deveCriarUsuarioComDadosValidos() {
        Usuario usuario = new Usuario("João da Silva", "123.456.789-09", 1);

        assertEquals("João da Silva", usuario.getNome());
        assertEquals("123.456.789-09", usuario.getCPF());
        assertEquals(1, usuario.getId());
    }

    @Test
    public void deveBloquearUsuarioComNomeNuloECpfVazio() {
        // Fica vermelho porque a classe base Usuario não possui validação ainda
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario(null, "", 2);
        }, "A superclasse Usuario deveria impedir valores nulos ou em branco.");
    }

    @Test
    public void deveBloquearIdNegativo() {
        // Fica vermelho porque permite ID negativo.
        assertThrows(IllegalArgumentException.class, () -> {
            new Usuario("Paciente Fulano", "123.456.789-09", -99);
        }, "A superclasse Usuario deveria impedir IDs negativos.");
    }
}