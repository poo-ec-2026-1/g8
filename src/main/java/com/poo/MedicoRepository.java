package com.poo;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class MedicoRepository {
    private static Database database;
    private static Dao<Medico, Integer> dao;
    
    private List<Medico> loadedMedicos;
    private Medico loadedMedico; 
    
    public MedicoRepository(Database database) {
        MedicoRepository.setDatabase(database);
        this.loadedMedicos = new ArrayList<Medico>();
    }
    
    public static void setDatabase(Database database) {
        MedicoRepository.database = database;
        try {
            dao = DaoManager.createDao(database.getConnection(), Medico.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Medico.class);
        } catch(SQLException e) {
            throw new RuntimeException("Erro fatal ao inicializar o DAO de Medico", e);
        }            
    }
    
    public Medico create(Medico medico) throws SQLException, IllegalArgumentException {
        if (medico != null && !ValidadorUtils.isCpfValido(medico.getCPF())) {
            throw new IllegalArgumentException("Impossível persistir: CPF do médico é inválido.");
        }
        try {
            int nrows = dao.create(medico);
            if (nrows == 0) {
                throw new SQLException("Erro: O médico não foi salvo.");
            }
            this.loadedMedico = medico;
            this.loadedMedicos.add(medico);
            return medico;
        } catch (SQLException e) {
            System.err.println("Erro ao criar médico: " + e.getMessage());
            throw e; 
        }
    }    

    public Medico loadFromId(int id) throws SQLException {
        try {
            this.loadedMedico = dao.queryForId(id);
            if (this.loadedMedico != null) {
                this.loadedMedicos.add(this.loadedMedico);
            }
            return this.loadedMedico;
        } catch (SQLException e) {
            System.err.println("Erro ao buscar médico por ID: " + e.getMessage());
            throw e;
        }
    }    
    
    public List<Medico> loadAll() throws SQLException {
        try {
            this.loadedMedicos = dao.queryForAll();
            if (this.loadedMedicos.size() != 0) {
                this.loadedMedico = this.loadedMedicos.get(0);
            }
            return this.loadedMedicos;
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os médicos: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(Medico medico) throws SQLException, IllegalArgumentException {
        if (medico != null && !ValidadorUtils.isCpfValido(medico.getCPF())) {
            throw new IllegalArgumentException("Impossível atualizar: CPF do médico é inválido.");
        }
        try {
            dao.update(medico);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar médico: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Medico medico) throws SQLException {
        try {
            dao.delete(medico);
        } catch (SQLException e) {
            System.err.println("Erro ao deletar médico: " + e.getMessage());
            throw e;
        }
    } 
}