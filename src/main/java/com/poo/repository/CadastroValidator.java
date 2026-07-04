package com.poo.repository;

import com.poo.util.ValidadorUtils;

import java.sql.SQLException;

/**
 * Garante a unicidade GLOBAL de CPF no sistema: um mesmo CPF não pode ser
 * cadastrado como Cliente, Médico ou Secretária ao mesmo tempo.
 *
 * A regra é global (cruza as três tabelas de usuários), então precisa conhecer
 * os três repositórios. Por isso ela vive isolada aqui, numa classe própria,
 * em vez de espalhada dentro de cada *Repository — o que acoplaria os
 * repositórios entre si. A comparação usa o CPF normalizado (só dígitos),
 * o mesmo critério já usado no login/busca (ValidadorUtils.normalizarCpf).
 */
public final class CadastroValidator {

    private CadastroValidator() {
    }

    public static void garantirCpfDisponivel(String cpf) throws SQLException {
        String normalizado = ValidadorUtils.normalizarCpf(cpf);
        if (normalizado.isEmpty()) {
            return; // CPF vazio/nulo é barrado pelas validações de formato de cada repo.
        }
        if (ClienteRepository.existeComCpf(normalizado)
                || MedicoRepository.existeComCpf(normalizado)
                || SecretariaRepository.existeComCpf(normalizado)) {
            throw new IllegalArgumentException("Impossível persistir: já existe um cadastro com este CPF.");
        }
    }
}
