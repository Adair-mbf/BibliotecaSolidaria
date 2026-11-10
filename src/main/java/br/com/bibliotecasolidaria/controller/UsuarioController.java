package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.service.UsuarioService;

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    public record Resposta<T>(int status, String mensagem, T dados) {}

    public Resposta<Usuario> cadastrar(DadosUsuario req) {
        try {
            Usuario u = service.cadastrar(req);
            return new Resposta<>(201, "Usuário cadastrado com sucesso", u);
        } catch (Exception e) {
            return new Resposta<>(400, e.getMessage(), null);
        }
    }

    public Resposta<Usuario> autenticar(String email, String senha) {
        return service.buscarPorEmail(email)
                .filter(u -> u.autenticar(senha))
                .map(u -> new Resposta<>(200, "Autenticado com sucesso", u))
                .orElseGet(() -> new Resposta<>(401, "Credenciais inválidas", null));
    }
}
