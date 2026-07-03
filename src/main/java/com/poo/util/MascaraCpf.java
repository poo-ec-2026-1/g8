package com.poo.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Aplica uma máscara de CPF (XXX.XXX.XXX-XX) a um TextField: o usuário digita
 * apenas os números e a pontuação é inserida automaticamente enquanto ele
 * digita. A lógica de formatação fica isolada aqui para ser reutilizada pelos
 * vários campos de CPF do sistema (login, cadastro, busca de prontuário).
 */
public class MascaraCpf {

    public static void aplicar(TextField campo) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            // Ignora mudanças que não alteram o texto (ex.: só mover o cursor).
            if (!change.isContentChange()) {
                return change;
            }

            // Mantém no máximo 11 dígitos, descartando qualquer pontuação.
            String digitos = change.getControlNewText().replaceAll("\\D", "");
            if (digitos.length() > 11) {
                digitos = digitos.substring(0, 11);
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
            if (i == 3 || i == 6) sb.append('.');
            if (i == 9) sb.append('-');
            sb.append(digitos.charAt(i));
        }
        return sb.toString();
    }
}
