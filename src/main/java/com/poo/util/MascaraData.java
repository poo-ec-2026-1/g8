package com.poo.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Aplica uma máscara de data (DD/MM/AAAA) a um TextField: o usuário digita
 * apenas os números e as barras são inseridas automaticamente enquanto ele
 * digita. Mesma lógica de {@link MascaraCpf}, reaproveitada para os campos
 * de data do sistema (nascimento do paciente, data da consulta).
 */
public class MascaraData {

    public static void aplicar(TextField campo) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            // Ignora mudanças que não alteram o texto (ex.: só mover o cursor).
            if (!change.isContentChange()) {
                return change;
            }

            // Mantém no máximo 8 dígitos (DDMMAAAA), descartando qualquer pontuação.
            String digitos = change.getControlNewText().replaceAll("\\D", "");
            if (digitos.length() > 8) {
                digitos = digitos.substring(0, 8);
            }

            // Reescreve o campo inteiro com o valor já formatado.
            String formatado = formatar(digitos);
            change.setRange(0, change.getControlText().length());
            change.setText(formatado);
            change.setCaretPosition(formatado.length());
            change.setAnchor(formatado.length());
            return change;
        }));
    }

    private static String formatar(String digitos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digitos.length(); i++) {
            if (i == 2 || i == 4) sb.append('/');
            sb.append(digitos.charAt(i));
        }
        return sb.toString();
    }
}
