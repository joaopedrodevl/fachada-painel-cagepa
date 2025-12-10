package com.fachada.cagepa.fachadacagepa.domain.util;

public class CpfCnpjValidator {

    /**
     * Valida um CPF verificando:
     * 1. Se tem exatamente 11 dígitos
     * 2. Se não é uma sequência de números iguais
     * 3. Se os dígitos verificadores estão corretos
     *
     * @param cpf CPF a ser validado (apenas dígitos)
     * @return true se o CPF é válido, false caso contrário
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem exatamente 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se não é uma sequência de números iguais
        if (cpf.matches("^(\\d)\\1{10}$")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int remainder = sum % 11;
        int firstDigit = remainder < 2 ? 0 : 11 - remainder;

        if (Character.getNumericValue(cpf.charAt(9)) != firstDigit) {
            return false;
        }

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        remainder = sum % 11;
        int secondDigit = remainder < 2 ? 0 : 11 - remainder;

        return Character.getNumericValue(cpf.charAt(10)) == secondDigit;
    }

    /**
     * Valida um CNPJ verificando:
     * 1. Se tem exatamente 14 dígitos
     * 2. Se não é uma sequência de números iguais
     * 3. Se os dígitos verificadores estão corretos
     *
     * @param cnpj CNPJ a ser validado (apenas dígitos)
     * @return true se o CNPJ é válido, false caso contrário
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return false;
        }

        // Remove caracteres não numéricos
        cnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifica se tem exatamente 14 dígitos
        if (cnpj.length() != 14) {
            return false;
        }

        // Verifica se não é uma sequência de números iguais
        if (cnpj.matches("^(\\d)\\1{13}$")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int[] multiplier1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * multiplier1[i];
        }
        int remainder = sum % 11;
        int firstDigit = remainder < 2 ? 0 : 11 - remainder;

        if (Character.getNumericValue(cnpj.charAt(12)) != firstDigit) {
            return false;
        }

        // Calcula o segundo dígito verificador
        int[] multiplier2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += Character.getNumericValue(cnpj.charAt(i)) * multiplier2[i];
        }
        remainder = sum % 11;
        int secondDigit = remainder < 2 ? 0 : 11 - remainder;

        return Character.getNumericValue(cnpj.charAt(13)) == secondDigit;
    }

    /**
     * Valida se a string é um CPF ou CNPJ válido
     *
     * @param cpfOrCnpj String contendo CPF ou CNPJ
     * @return true se é um CPF ou CNPJ válido, false caso contrário
     */
    public static boolean isValidCpfOrCnpj(String cpfOrCnpj) {
        if (cpfOrCnpj == null || cpfOrCnpj.isBlank()) {
            return false;
        }

        String cleaned = cpfOrCnpj.replaceAll("[^0-9]", "");

        if (cleaned.length() == 11) {
            return isValidCpf(cleaned);
        } else if (cleaned.length() == 14) {
            return isValidCnpj(cleaned);
        }

        return false;
    }

    /**
     * Remove formatação de CPF/CNPJ, mantendo apenas dígitos
     * Exemplos:
     * - "949.545.430-10" -> "94954543010"
     * - "03.142.306/0001-70" -> "03142306000170"
     * - "94954543010" -> "94954543010"
     *
     * @param cpfOrCnpj CPF ou CNPJ com ou sem formatação
     * @return CPF/CNPJ contendo apenas dígitos
     */
    public static String cleanCpfCnpj(String cpfOrCnpj) {
        if (cpfOrCnpj == null) {
            return null;
        }
        return cpfOrCnpj.replaceAll("[^0-9]", "");
    }
}

