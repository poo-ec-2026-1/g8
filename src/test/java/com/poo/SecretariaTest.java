package com.poo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SecretariaTest {

    @Test
    public void deveCriarSecretariaEValidarHeranca() {
        Secretaria sec = new Secretaria("Ana Clara Souza", "529.982.247-25", "senha123");

        assertEquals("Ana Clara Souza", sec.getNome());
        assertEquals("529.982.247-25", sec.getCPF());
    }

    @Test
    public void deveBloquearSecretariaComCpfInvalido() {
        // Fica verde, validando a correção feita
        assertThrows(IllegalArgumentException.class, () -> {
            new Secretaria("Ana Clara Souza", "111.111.111-11", "senha123");
        }, "O sistema deve bloquear a criação de secretária com CPF falso");
    }

    @Test
    public void devePossuirConstrutorVazioParaO_ORMLite() {
        assertDoesNotThrow(() -> new Secretaria(), "O ORMLite exige um construtor vazio.");
    }
}