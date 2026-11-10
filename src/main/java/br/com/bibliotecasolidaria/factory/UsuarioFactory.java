package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Usuario;

public abstract class UsuarioFactory {
    public Usuario cadastrar(DadosUsuario dados) {
        validar(dados);
        return criarUsuario(dados);
    }

    protected abstract Usuario criarUsuario(DadosUsuario dados);

    protected void validar(DadosUsuario dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Dados do usuário não podem ser nulos.");
        }
        if (dados.getNome() == null || dados.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (dados.getEmail() == null || !dados.getEmail().contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (dados.getSenha() == null || dados.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
    }
}
