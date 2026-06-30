import java.util.ArrayList;

public class ControleHospitalar 
{
    private ArrayList<Cliente> listaClientes = new ArrayList<>();
    
    public ControleHospitalar(ArrayList<Cliente> listaClientes) 
    {
        this.listaClientes = listaClientes;
    }
    
    public void cadastrarCliente(Cliente c) 
    {
        this.listaClientes.add(c);
    }
    
    public void verProntuario(String senha, String CPF) 
    {
        boolean clienteEncontrado = false;

        for (Cliente c : listaClientes) 
        {
            if (c.getCPF().equals(CPF)) 
            {
                clienteEncontrado = true;
                Prontuario requisitado = c.getProntuario();
                
                if (requisitado.verificarSenha(senha)) 
                { 
                    System.out.printf("Solicitação aceita.%n");
                    requisitado.exibirProntuario();
                    }else 
                    {
                    System.out.printf("Senha incorreta! Acesso negado.%n");
                }
                break; 
            }
        }
        
        if (!clienteEncontrado) 
        {
            System.out.printf("Paciente com o CPF %s não foi encontrado.%n", CPF);
        }
    }
}
