package com.poo.model;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

@DatabaseTable(tableName = "prontuario_medico")
public class ProntuarioMedico {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "prontuario_id")
    private Prontuario prontuario;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "medico_id")
    private Medico medico;

    public ProntuarioMedico() {
    }

    public ProntuarioMedico(Prontuario prontuario, Medico medico) {
        this.prontuario = prontuario;
        this.medico = medico;
    }

    public int getId(){ 
        return id; 
    }
    
    public Prontuario getProntuario(){ 
        return prontuario; 
    }
    
    public Medico getMedico(){ 
        return medico; 
    }
}