import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.dao.ForeignCollection;
import java.util.ArrayList;

@DatabaseTable(tableName = "Prontuários")
public class Prontuario{
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String doença;
    
    @ForeignCollectionField(eager = true)
    private ForeignCollection<ProntuarioMedico> historico;
    
    public Prontuario(){  
    }
    
    public Prontuario(String doença){
        this.doença = doença;
        this.id = 0;
    }
    
    public String getDoença(){
        return this.doença;
    }
    
    public void setDoença(String doença){
        this.doença = doença;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public ArrayList<Medico> getHistorico() {
        ArrayList<Medico> medicos = new ArrayList<>();
        if (historico != null) {
            for (ProntuarioMedico c : historico){
                medicos.add(c.getMedico()  );
            }
        }
        return medicos;
    }
    
    public boolean verificarSenha(String senha) {
        for (Medico m : getHistorico()) {
            if (m.getSenha().equals(senha)) {
                return true;
            }
        }
        return false;
    }
    
    public void exibirProntuario() {
        System.out.printf("Doenças do paciente: %s\n", this.doença);
        System.out.print("Histórico de médicos que atenderam o paciente:\n");
        for (Medico c : getHistorico()) {
            System.out.printf("- Dr(a).%s\n", c.getNome());
        }
    }
}