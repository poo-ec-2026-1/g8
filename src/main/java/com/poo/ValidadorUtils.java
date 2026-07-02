package com.poo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidadorUtils {

    //Validar cpf
    public static boolean isCpfValido(String cpf) {
        if (cpf == null) return false;

        //Remove caracteres não numéricos
        cpf = cpf.replaceAll("\\D", "");

        //Verificar se cpf digitado tem 11 digitos
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;

        try {
            //Cálculo do 1º Dígito Verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int resto = 11 - (soma % 11);
            int digito1 = (resto == 10 || resto == 11) ? 0 : resto;

            //Cálculo do 2º Dígito Verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            resto = 11 - (soma % 11);
            int digito2 = (resto == 10 || resto == 11) ? 0 : resto;

            //Verificar se os digitos batem com o CPF informado
            return digito1 == Character.getNumericValue(cpf.charAt(9)) &&
                   digito2 == Character.getNumericValue(cpf.charAt(10));

        } catch (Exception e) {
            return false;
        }
    }

    //Validar datas
    //Verifica se o formato da String é (dd/MM/yyyy)
    public static boolean isDataValida(String dataStr) {
        if (dataStr == null) return false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate.parse(dataStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    //Não permitir agendamentos em datas passadas
    public static boolean isDataConsultaValida(LocalDate dataConsulta) {
        if (dataConsulta == null) return false;

        return !dataConsulta.isBefore(LocalDate.now());
    }

    //Validar horário no formato HH:mm (00:00 a 23:59)
    public static boolean isHorarioValido(String horario) {
        if (horario == null) return false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime.parse(horario, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
