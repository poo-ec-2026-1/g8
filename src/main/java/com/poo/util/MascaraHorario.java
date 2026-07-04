package com.poo.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Aplica uma máscara de horário (HH:MM) a um TextField: o usuário digita
 * apenas os números e o ':' é inserido automaticamente enquanto ele digita.
 * Mesma lógica de {@link MascaraCpf} e {@link MascaraData}, reaproveitada para
 * o campo de horário da consulta (AgendamentoController).
 */
public class MascaraHorario {

    public static void aplicar(TextField campo) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            // Ignora mudanças que não alteram o texto (ex.: só mover o cursor).
            if (!change.isContentChange()) {
                return change;
            }

            // Mantém no máximo 4 dígitos (HHMM), descartando qualquer pontuação.
            String digitos = change.getControlNewText().replaceAll("\\D", "");
            if (digitos.length() > 4) {
                digitos = digitos.substring(0, 4);
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
            if (i == 2) sb.append(':');
            sb.append(digitos.charAt(i));
        }
        return sb.toString();
    }
}
