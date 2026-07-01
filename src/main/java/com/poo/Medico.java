package com.poo;

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
        super(null, null, 0);
    }
    
    public Medico(String nome, String CPF, String especialidade, String senha){
        super(nome, CPF, 0);

        if (!ValidadorUtils.isCpfValido(CPF)) {
            throw new IllegalArgumentException("Não foi possível criar o Médico: O CPF informado é inválido.");
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

