package com.poo;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @Test
    public void deveLancarUmaExcecaoSeOBancoDeDadosForNulo(){
        Database database = new Database(null);

        // Vai ficar VERDE, pois a Exception correta é lançada.
        assertThrows(SQLException.class, database::getConnection, "O sistema deve lançar uma SQLException ao tentar conectar num banco nulo.");
    }
}