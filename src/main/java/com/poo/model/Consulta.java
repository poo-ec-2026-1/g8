package com.poo.model;

import com.poo.util.ValidadorUtils;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "Consultas")
public class Consulta{
    @DatabaseField(generatedId = true)
    private int id;
    
    @DatabaseField
    private String data;
    @DatabaseField
    private String horario;
    
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Medico medico;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Cliente cliente;
    
    public Consulta(){
        
    }
    
    public Consulta(int id, String data, String horario, Medico medico, Cliente cliente){
        if (medico == null || cliente == null) {
            throw new IllegalArgumentException("Não foi possível criar a Consulta: médico e cliente são obrigatórios.");
        }
        if (!ValidadorUtils.isHorarioValido(horario)) {
            throw new IllegalArgumentException("Não foi possível criar a Consulta: o horário informado é inválido.");
        }

        // O ID é mantido em memória; na persistência o ORMLite (generatedId)
        // gera o valor definitivo — os fluxos que salvam passam 0.
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.medico = medico;
        this.cliente = cliente;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getData(){
        return this.data;
    }
    
    public String getHorario(){
        return this.horario;
    }
    
    public Medico getMedico(){
        return this.medico;
    }
    
    public Cliente getCliente(){
        return this.cliente;
    }
}