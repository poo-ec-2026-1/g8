package com.poo;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "Secretárias")
public class Secretaria extends Usuario{
    @DatabaseField
    private String senha;
    
    public Secretaria(){
        super(null, null, 0);
    }
    
    public Secretaria(String nome, String CPF, String senha){
        super(nome, CPF, 0);

        if (!ValidadorUtils.isCpfValido(CPF)) {
            throw new IllegalArgumentException("Não foi possível criar a Secretária: O CPF informado é inválido.");
        }

        this.senha = senha;
    }
}