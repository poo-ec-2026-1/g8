package com.poo;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class SecretariaRepository {
    private static Database database;
    private static Dao<Secretaria, Integer> dao;
    
    private List<Secretaria> loadedSecretarias;
    private Secretaria loadedSecretaria; 
    
    public SecretariaRepository(Database database) {
        SecretariaRepository.setDatabase(database);
        this.loadedSecretarias = new ArrayList<Secretaria>();
    }
    
    public static void setDatabase(Database database) {
        SecretariaRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Secretaria.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Secretaria.class);
        } catch(SQLException e) {
            throw new RuntimeException("Erro fatal ao inicializar o DAO de Secretaria", e);
        }            
    }
    
    public Secretaria create(Secretaria secretaria) throws SQLException, IllegalArgumentException {
        if (secretaria != null && !ValidadorUtils.isCpfValido(secretaria.getCPF())) {
            throw new IllegalArgumentException("Impossível persistir: CPF da secretária é inválido.");
        }
        try {
            int nrows = dao.create(secretaria);
            if (nrows == 0) {
                throw new SQLException("Erro: A tabela recusou a gravação da secretária.");
            }
            this.loadedSecretaria = secretaria;
            this.loadedSecretarias.add(secretaria);
            return secretaria;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar secretária: " + e.getMessage());
            throw e; 
        }
    }    

    public Secretaria loadFromId(int id) throws SQLException {
        try {
            this.loadedSecretaria = dao.queryForId(id);
            if (this.loadedSecretaria != null) {
                this.loadedSecretarias.add(this.loadedSecretaria);
            }
            return this.loadedSecretaria;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar id " + id + " em Secretárias: " + e.getMessage());
            throw e; 
        }
    }    
    
    public List<Secretaria> loadAll() throws SQLException {
        try {
            this.loadedSecretarias = dao.queryForAll();
            if (this.loadedSecretarias.size() != 0) {
                this.loadedSecretaria = this.loadedSecretarias.get(0);
            }
            return this.loadedSecretarias;
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as secretárias: " + e.getMessage());
            throw e; 
        }
    }
}