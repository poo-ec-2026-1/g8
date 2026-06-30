package com.poo;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "Clientes")
public class Cliente extends Usuario {
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Prontuario prontuario;
    
    @DatabaseField
    private String aniverssario;
    
    public Cliente() {
        super(null, null, 0);
    }
    
    public Cliente(String nome, String CPF, int id, Prontuario prontuario, String aniverssario) {
        super(nome, CPF, 0);
        
        if (!ValidadorUtils.isCpfValido(CPF)) {
            throw new IllegalArgumentException("Não foi possível criar o Cliente: O CPF informado é inválido.");
        }
        
        if (!ValidadorUtils.isDataValida(aniverssario)) {
            throw new IllegalArgumentException("Não foi possível criar o Cliente: A data de aniversário informada é inválida.");
        }
        
        this.prontuario = prontuario;
        this.aniverssario = aniverssario;
    }
    
    public Prontuario getProntuario() {
        return this.prontuario;
    }
    
    public String getAniverssario() {
        return this.aniverssario;
    }
}