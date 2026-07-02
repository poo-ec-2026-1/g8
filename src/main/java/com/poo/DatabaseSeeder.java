package com.poo;

import java.sql.SQLException;

/**
 * Responsável por popular o banco com os dados iniciais mínimos para o
 * primeiro acesso ao sistema. Mantém a lógica de "seed" fora do MainApp,
 * separando a inicialização de dados do bootstrap da interface (SRP).
 *
 * É idempotente: só insere quando o dado ainda não existe, então pode
 * rodar em toda inicialização sem risco de duplicar.
 */
public class DatabaseSeeder {

    private final SecretariaRepository secretariaRepo;

    public DatabaseSeeder(SecretariaRepository secretariaRepo) {
        this.secretariaRepo = secretariaRepo;
    }

    /**
     * Cria uma secretária inicial apenas se não houver nenhuma no banco.
     * Serve para destravar o primeiro acesso — a partir dela, dá para
     * cadastrar médicos e outras secretárias pela tela de Cadastro.
     */
    public void popularDadosIniciais() {
        try {
            if (secretariaRepo.loadAll().isEmpty()) {
                secretariaRepo.create(new Secretaria("Secretária Admin", "111.444.777-35", "admin"));
                System.out.println("Secretária inicial criada — CPF: 111.444.777-35");
            }
        } catch (SQLException e) {
            System.err.println("Falha ao popular dados iniciais: " + e.getMessage());
        }
    }
}
