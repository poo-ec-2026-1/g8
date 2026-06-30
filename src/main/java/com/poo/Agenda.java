import java.util.ArrayList;

public class Agenda 
{
    private ArrayList<Consulta> consultas;

    public Agenda(ArrayList<Consulta> consultas) 
    {
        this.consultas = consultas;
    }
    
    public void novaConsulta(Consulta nova) 
    {
        for (Consulta c : consultas) 
        {
            if (c.getData().equals(nova.getData()) && 
                c.getHorario().equals(nova.getHorario()) && 
                c.getMedico().equals(nova.getMedico())) 
                {
                
                System.out.printf("O horário requisitado está ocupado.%n");
                return; 
            }
        }
        
        this.consultas.add(nova);
        System.out.printf("Consulta agendada com sucesso.%n");
    }
    
    public void exibirAgenda(Medico medico, String senha) 
    {
        if (!medico.getSenha().equals(senha)) 
        {
            System.out.printf("Senha incorreta.%n");
            return; 
        }
        
        for (Consulta c : consultas) 
        {
            if (c.getMedico().equals(medico)) 
            {
                Cliente agendado = c.getCliente();
                System.out.printf("No dia %s o Dr. %s tem uma consulta marcada para às %s com o paciente %s.%n", 
                    c.getData(), medico.getNome(), c.getHorario(), agendado.getNome());
            }
        }
    }
}
