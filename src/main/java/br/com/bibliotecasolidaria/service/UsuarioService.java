package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.factory.FabricaRegistry;
import br.com.bibliotecasolidaria.factory.UsuarioFactory;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Funcionario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;
import br.com.bibliotecasolidaria.util.SenhaUtil;
import br.com.bibliotecasolidaria.util.Textos;

import java.util.List;

public class UsuarioService {
    private final FabricaRegistry fabricas;
    private final UsuarioRepositorio usuarios;

    public UsuarioService(FabricaRegistry fabricas, UsuarioRepositorio usuarios) {
        this.fabricas = fabricas;
        this.usuarios = usuarios;
    }

    public Usuario cadastrar(DadosUsuario dados) {
        if (dados == null) {
            throw new ValidacaoException("Os dados do usuário são obrigatórios.");
        }
        return fabricas.obter(dados.tipo()).cadastrar(dados);
    }

    /** Autentica por e-mail e senha; a mensagem é genérica para não revelar se o e-mail existe. */
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = Textos.emBranco(email) ? null : usuarios.buscarPorEmail(email.trim()).orElse(null);
        if (usuario == null || !usuario.autenticar(senha)) {
            throw new NegocioException("E-mail ou senha inválidos.");
        }
        if (!usuario.isAtivo()) {
            throw new NegocioException("Usuário inativo.");
        }
        return usuario;
    }

    public Usuario buscar(long id) {
        return usuarios.buscarPorId(id).orElseThrow(() -> new NaoEncontradoException("Usuário " + id + " não encontrado."));
    }

    public Doador buscarDoador(long id) {
        if (buscar(id) instanceof Doador d) {
            return d;
        }
        throw new NegocioException("O usuário " + id + " não é um doador.");
    }

    public UsuarioSite buscarUsuarioSite(long id) {
        if (buscar(id) instanceof UsuarioSite u) {
            return u;
        }
        throw new NegocioException("O usuário " + id + " não é um usuário do site.");
    }

    public List<Usuario> listar() {
        return usuarios.listarTodos();
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarios.listarPorTipo(tipo);
    }

    /**
     * Atualiza apenas os campos informados (nulos/vazios são mantidos). O tipo do usuário não muda.
     * Atributos específicos: documento, pessoaJuridica, telefone, endereco, matricula, cargo.
     */
    public Usuario atualizar(long id, DadosUsuario dados, Boolean ativo) {
        Usuario u = buscar(id);
        if (!Textos.emBranco(dados.nome())) {
            u.setNome(dados.nome().trim());
        }
        if (!Textos.emBranco(dados.email()) && !dados.email().trim().equalsIgnoreCase(u.getEmail())) {
            UsuarioFactory.validarEmail(dados.email());
            if (usuarios.buscarPorEmail(dados.email().trim()).isPresent()) {
                throw new ConflitoException("Já existe um usuário com o e-mail " + dados.email().trim() + ".");
            }
            u.setEmail(dados.email().trim().toLowerCase());
        }
        if (!Textos.emBranco(dados.senha())) {
            if (dados.senha().length() < UsuarioFactory.TAMANHO_MINIMO_SENHA) {
                throw new ValidacaoException("A senha deve ter ao menos " + UsuarioFactory.TAMANHO_MINIMO_SENHA
                        + " caracteres.");
            }
            u.setSenhaHash(SenhaUtil.gerarHash(dados.senha()));
        }
        if (ativo != null) {
            u.setAtivo(ativo);
        }
        aplicarAtributos(u, dados);
        return usuarios.salvar(u);
    }

    public Usuario definirAtivo(long id, boolean ativo) {
        Usuario u = buscar(id);
        u.setAtivo(ativo);
        return usuarios.salvar(u);
    }

    public void remover(long id) {
        buscar(id);
        usuarios.remover(id);
    }

    private static void aplicarAtributos(Usuario u, DadosUsuario d) {
        if (u instanceof Doador doador) {
            if (!Textos.emBranco(d.atributo("documento"))) doador.setDocumento(d.atributo("documento").trim());
            if (!Textos.emBranco(d.atributo("pessoaJuridica"))) {
                doador.setPessoaJuridica(Boolean.parseBoolean(d.atributo("pessoaJuridica").trim()));
            }
        } else if (u instanceof UsuarioSite site) {
            if (!Textos.emBranco(d.atributo("telefone"))) site.setTelefone(d.atributo("telefone").trim());
            if (!Textos.emBranco(d.atributo("endereco"))) site.setEndereco(d.atributo("endereco").trim());
        } else if (u instanceof Funcionario f) {
            if (!Textos.emBranco(d.atributo("matricula"))) f.setMatricula(d.atributo("matricula").trim());
            if (!Textos.emBranco(d.atributo("cargo"))) f.setCargo(d.atributo("cargo").trim());
        }
    }
}
