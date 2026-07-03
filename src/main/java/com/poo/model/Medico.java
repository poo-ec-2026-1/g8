package com.poo.model;

import com.poo.util.ValidadorUtils;

import java.util.ArrayList;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "Médicos")
public class Medico extends Usuario{
    @DatabaseField
    private String especialidade;
    
    @DatabaseField
    private String senha;
    
    public Medico(){
        super();
    }
    
    public Medico(String nome, String CPF, String especialidade, String senha){
        super(nome, CPF, 0);

        if (!ValidadorUtils.isCpfValido(CPF)) {
            throw new IllegalArgumentException("Não foi possível criar o Médico: O CPF informado é inválido.");
        }
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("Não foi possível criar o Médico: a especialidade não pode ser vazia.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Não foi possível criar o Médico: a senha não pode ser vazia.");
        }

        this.especialidade = especialidade;
        this.senha = senha;
    }
    
    public String getEspecialidade(){
        return this.especialidade;
    }
    
    public String getSenha(){
        return this.senha;
    }
    
}

