import com.j256.ormlite.field.DatabaseField;

public class Usuario{
    @DatabaseField
    protected String nome;
    
    @DatabaseField
    protected String CPF;
    
    @DatabaseField(generatedId = true)
    protected int id;
    
    public Usuario(String nome, String CPF, int id){
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