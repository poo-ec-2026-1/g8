package com.poo.repository;

import com.poo.model.Consulta;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ConsultaRepositoryTest {

    @Test
    public void naoDevePersistirConsultaComHorarioInvalido() throws SQLException {
        Database database = new Database(":memory:");
        ConsultaRepository repository = new ConsultaRepository(database);

        // O construtor vazio (usado pelo ORMLite) deixa o horário null — inválido.
        // O repositório deve recusar a persistência, do mesmo modo que o
        // ClienteRepository recusa CPF/data inválidos na camada de persistência.
        Consulta semHorario = new Consulta();

        assertThrows(IllegalArgumentException.class,
                () -> repository.create(semHorario),
                "O repositório deve recusar consulta com horário inválido.");
    }
}
