package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.factory.FabricaRegistry;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.repository.UsuarioRepositorio;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepositorio usuarioRepo;
    private final FabricaRegistry fabricaRegistry;

    public UsuarioService(UsuarioRepositorio repo, FabricaRegistry registry) {
        this.usuarioRepo = repo;
        this.fabricaRegistry = registry;
    }

    public Usuario cadastrar(DadosUsuario dados) {
        Usuario usuario = fabricaRegistry.obter(dados.getTipo()).cadastrar(dados);
        return usuarioRepo.salvar(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepo.buscarPorId(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepo.buscarPorEmail(email);
    }
}
