package br.com.bibliotecasolidaria.controller;

/**
 * Resultado de uma operação de controller, independente de interface. Os códigos seguem a convenção HTTP
 * (200/201 sucesso, 400 validação, 401/404/409/422 regra de negócio, 500 erro interno), o que permite expor
 * os controllers por uma API REST sem mudanças.
 */
public record Resposta(int codigo, String mensagem, Object dados) {
    public boolean sucesso() {
        return codigo < 400;
    }

    public static Resposta ok(String mensagem, Object dados) { return new Resposta(200, mensagem, dados); }

    public static Resposta criado(String mensagem, Object dados) { return new Resposta(201, mensagem, dados); }

    public static Resposta erro(int codigo, String mensagem) { return new Resposta(codigo, mensagem, null); }
}
