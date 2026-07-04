package com.poo.repository;

import com.poo.model.Cliente;
import com.poo.model.Medico;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteRepositoryTest {

    @Test
    public void deveRetornarNuloQuandoClienteNaoExistir() throws SQLException {
        Database database = new Database("banco_inexistente.db");
        ClienteRepository repository = new ClienteRepository(database);

        Cliente resultado = repository.loadFromId(999);
        assertNull(resultado, "O repositório tem que retornar null se o ID não existir no banco");
    }

    @Test
    public void naoDevePermitirCadastrarClienteComCpfDuplicado() throws SQLException {
        Database database = new Database(":memory:");
        ClienteRepository clienteRepo = new ClienteRepository(database);
        // Inicializa os DAOs estáticos que a checagem global de CPF consulta.
        new MedicoRepository(database);
        new SecretariaRepository(database);

        String cpf = "718.905.727-72";
        clienteRepo.create(new Cliente("Fulano de Tal", cpf, "01/01/2000"));

        assertThrows(IllegalArgumentException.class,
                () -> clienteRepo.create(new Cliente("Beltrano da Silva", cpf, "02/02/2000")),
                "Não deve permitir cadastrar dois clientes com o mesmo CPF.");
    }

    @Test
    public void naoDevePermitirCpfJaUsadoPorOutroTipoDeUsuario() throws SQLException {
        Database database = new Database(":memory:");
        ClienteRepository clienteRepo = new ClienteRepository(database);
        MedicoRepository medicoRepo = new MedicoRepository(database);
        new SecretariaRepository(database);

        String cpf = "123.456.789-09";
        medicoRepo.create(new Medico("Dr. Teste", cpf, "Cardiologia", "senha"));

        // Unicidade GLOBAL: um CPF já usado por um médico bloqueia o cadastro de cliente.
        assertThrows(IllegalArgumentException.class,
                () -> clienteRepo.create(new Cliente("Paciente Homônimo", cpf, "01/01/2000")),
                "CPF já cadastrado para outro tipo de usuário não pode ser reutilizado.");
    }
}
