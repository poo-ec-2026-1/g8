package com.poo.repository;

import com.poo.model.Cliente;
import com.poo.util.ValidadorUtils;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ClienteRepository 
{
    private static Database database;
    private static Dao<Cliente, Integer> dao;
    
    private List<Cliente> loadedClientes;
    private Cliente loadedCliente; 
    
    public ClienteRepository(Database database) 
    {
        ClienteRepository.setDatabase(database);
        this.loadedClientes = new ArrayList<Cliente>();
    }
    
    public static void setDatabase(Database database) 
    {
        ClienteRepository.database = database;
        try 
        {
            dao = DaoManager.createDao(database.getConnection(), Cliente.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Cliente.class);
        } catch(SQLException e) 
        {
            throw new RuntimeException(e);
        }            
    }
    
    public Cliente create(Cliente cliente) throws SQLException, IllegalArgumentException
    {
        if (cliente != null) {
            if (!ValidadorUtils.isCpfValido(cliente.getCPF())) {
                throw new IllegalArgumentException("Impossível persistir: CPF do cliente é inválido.");
            }
            if (!ValidadorUtils.isDataValida(cliente.getAniverssario())) {
                throw new IllegalArgumentException("Impossível persistir: A data de aniversário informada é inválida.");
            }
        }

        int nrows = 0;
        try 
        {
            nrows = dao.create(cliente);
            if (nrows == 0) 
            {
                throw new SQLException("Erro: O cliente não foi salvo no banco.");
            }
            this.loadedCliente = cliente;
            this.loadedClientes.add(cliente);
            return cliente;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao criar cliente: " + e.getMessage());
            throw e;
        }
    }    

    public Cliente loadFromId(int id) throws SQLException 
    {
        try 
        {
            this.loadedCliente = dao.queryForId(id);
            if (this.loadedCliente != null) 
            {
                this.loadedClientes.add(this.loadedCliente);
            }
            return this.loadedCliente;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao buscar cliente por ID: " + e.getMessage());
            throw e;
        }
    }    
    
    public List<Cliente> loadAll() throws SQLException 
    {
        try 
        {
            this.loadedClientes = dao.queryForAll();
            if (this.loadedClientes.size() != 0) 
            {
                this.loadedCliente = this.loadedClientes.get(0);
            }
            return this.loadedClientes;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
            throw e;
        }
    }

    public void update(Cliente cliente) throws SQLException, IllegalArgumentException
    {
        if (cliente != null) {
            if (!ValidadorUtils.isCpfValido(cliente.getCPF())) {
                throw new IllegalArgumentException("Impossível atualizar: CPF do cliente é inválido.");
            }
            if (!ValidadorUtils.isDataValida(cliente.getAniverssario())) {
                throw new IllegalArgumentException("Impossível atualizar: A data de aniversário informada é inválida.");
            }
        }
        try
        {
            dao.update(cliente);
        } catch (SQLException e) 
        {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Cliente cliente) throws SQLException 
    {
        try 
        {
            dao.delete(cliente);
        } catch (SQLException e) 
        {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
            throw e;
        }
    }      
}
