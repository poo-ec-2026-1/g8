import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ProntuarioRepository 
{
    private static Database database;
    private static Dao<Prontuario, Integer> prontuarioDao;
    private static Dao<ProntuarioMedico, Integer> ponteDao; 
    private List<Prontuario> loadedProntuarios;
    
    public ProntuarioRepository(Database database) {
        ProntuarioRepository.setDatabase(database);
        this.loadedProntuarios = new ArrayList<Prontuario>();
    }
    
    public static void setDatabase(Database database) 
    {
        ProntuarioRepository.database = database;
        try {
            prontuarioDao = DaoManager.createDao(database.getConnection(), Prontuario.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Prontuario.class);
            
            ponteDao = DaoManager.createDao(database.getConnection(), ProntuarioMedico.class);
            TableUtils.createTableIfNotExists(database.getConnection(), ProntuarioMedico.class);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }            
    }
    
    public Prontuario create(Prontuario prontuario) throws SQLException 
    {
        try {
            prontuarioDao.create(prontuario);
            this.loadedProntuarios.add(prontuario);
            return prontuario;
        } catch (SQLException e) {
            System.err.println("Erro ao criar prontuário: " + e.getMessage());
            throw e;
        }
    }    

    public void adicionarMedicoAoHistorico(Prontuario prontuario, Medico medico) throws SQLException 
    {
        try {
            ProntuarioMedico ligacao = new ProntuarioMedico(prontuario, medico);
            ponteDao.create(ligacao);
        } catch (SQLException e) {
            System.err.println("Erro ao vincular médico ao prontuário: " + e.getMessage());
            throw e;
        }
    }

    public Prontuario loadFromId(int id) throws SQLException 
    {
        try {
            return prontuarioDao.queryForId(id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar prontuário por ID: " + e.getMessage());
            throw e;
        }
    }
    
    public void update(Prontuario prontuario) throws SQLException 
    {
        try {
            prontuarioDao.update(prontuario);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar prontuário: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Prontuario prontuario) throws SQLException 
    {
        try {
            prontuarioDao.delete(prontuario);
        } catch (SQLException e) {
            System.err.println("Erro ao deletar prontuário: " + e.getMessage());
            throw e;
        }
    } 
}
