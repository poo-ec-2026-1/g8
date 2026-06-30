import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ConsultaRepository 
{
    private static Database database;
    private static Dao<Consulta, Integer> dao;
    
    private List<Consulta> loadedConsultas;
    private Consulta loadedConsulta; 
    
    public ConsultaRepository(Database database) 
    {
        ConsultaRepository.setDatabase(database);
        this.loadedConsultas = new ArrayList<Consulta>();
    }
    
    public static void setDatabase(Database database) 
    {
        ConsultaRepository.database = database;
        try 
        {
            dao = DaoManager.createDao(database.getConnection(), Consulta.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Consulta.class);
        } catch(SQLException e) 
        {
            throw new RuntimeException(e);
        }            
    }
    
    public Consulta create(Consulta consulta) throws SQLException 
    {
        try 
        {
            int nrows = dao.create(consulta);
            if (nrows == 0) 
            {
                throw new SQLException("Erro: O banco recusou o agendamento da consulta.");
            }
            this.loadedConsulta = consulta;
            this.loadedConsultas.add(consulta);
            return consulta;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao salvar consulta: " + e.getMessage());
            throw e;
        }
    }    

    public Consulta loadFromId(int id) throws SQLException 
    {
        try {
            this.loadedConsulta = dao.queryForId(id);
            if (this.loadedConsulta != null) 
            {
                this.loadedConsultas.add(this.loadedConsulta);
            }
            return this.loadedConsulta;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao carregar consulta por ID: " + e.getMessage());
            throw e;
        }
    }    
    
    public List<Consulta> loadAll() throws SQLException 
    {
        try 
        {
            this.loadedConsultas = dao.queryForAll();
            if (this.loadedConsultas.size() != 0) 
            {
                this.loadedConsulta = this.loadedConsultas.get(0);
            }
            return this.loadedConsultas;
        } catch (SQLException e) 
        {
            System.err.println("Erro ao listar todas as consultas: " + e.getMessage());
            throw e;
        }
    }

    public void update(Consulta consulta) throws SQLException 
    {
        try 
        {
            dao.update(consulta);
        } catch (SQLException e) 
        {
            System.err.println("Erro ao atualizar dados da consulta: " + e.getMessage());
            throw e;
        }
    }

    public void delete(Consulta consulta) throws SQLException 
    {
        try 
        {
            dao.delete(consulta);
        } catch (SQLException e) 
        {
            System.err.println("Erro ao deletar consulta: " + e.getMessage());
            throw e;
        }
    }      
}
