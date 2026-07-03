package com.poo.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class ControleHospitalarTest {

    @Test
    public void deveBuscarClienteNaListaComSucesso() {
        ArrayList<Cliente> lista = new ArrayList<>();

        Prontuario p1 = new Prontuario("Febre");
        Cliente c1 = new Cliente("Rafael Santos", "718.905.727-72", 1, p1, "11/11/2011");

        Prontuario p2 = new Prontuario("Gastrite");
        Cliente c2 = new Cliente("Bruno", "111.444.777-35", 2, p2, "22/02/2002");

        lista.add(c1);
        lista.add(c2);

        ControleHospitalar controle = new ControleHospitalar(lista);

        // O teste mostra que o loop de busca procura corretamente pela lista e não quebra
        assertDoesNotThrow(() -> controle.verProntuario("senha123", "111.444.777-35"), "A busca deve percorrer toda a lista e encontrar o segundo cliente sem lançar erro.");
    }
}