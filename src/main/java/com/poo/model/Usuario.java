package com.poo.model;

import com.j256.ormlite.field.DatabaseField;

public class Usuario{
    @DatabaseField
    protected String nome;
    
    @DatabaseField
    protected String CPF;
    
    @DatabaseField(generatedId = true)
    protected int id;
    
    // Construtor vazio exigido pelo ORMLite e usado pelos construtores vazios
    // das subclasses. Não valida, pois o ORM instancia o objeto sem dados.
    public Usuario(){
    }

    public Usuario(String nome, String CPF, int id){
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Usuário inválido: o nome não pode ser nulo ou vazio.");
        }
        if (CPF == null || CPF.isBlank()) {
            throw new IllegalArgumentException("Usuário inválido: o CPF não pode ser nulo ou vazio.");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Usuário inválido: o ID não pode ser negativo.");
        }
        this.nome = nome;
        this.CPF = CPF;
        this.id = id;
    }
    
    public String getNome(){
        return this.nome;
    }
        
    public String getCPF(){
        return this.CPF;
    }
    
    public int getId(){
        return this.id;
    }
}