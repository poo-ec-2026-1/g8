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
        this.id = 0;
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