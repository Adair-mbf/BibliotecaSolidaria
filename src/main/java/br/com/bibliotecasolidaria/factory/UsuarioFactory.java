package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.UsuarioRepositorio;
import br.com.bibliotecasolidaria.util.SenhaUtil;
import br.com.bibliotecasolidaria.util.Textos;

import java.util.regex.Pattern;

/**
 * Factory Method (Creator): {@link #cadastrar} valida, cria o usuário do tipo concreto e o persiste.
 * Subclasses decidem apenas qual {@link Usuario} instanciar.
 */
public abstract class UsuarioFactory {
    public static final int TAMANHO_MINIMO_SENHA = 6;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepositorio usuarios;

    protected UsuarioFactory(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    public abstract TipoUsuario tipoSuportado();

    protected abstract Usuario criarUsuario(DadosUsuario dados, String senhaHash);

    public final Usuario cadastrar(DadosUsuario dados) {
        validar(dados);
        Usuario usuario = criarUsuario(dados, SenhaUtil.gerarHash(dados.senha()));
        return usuarios.salvar(usuario);
    }

    protected void validar(DadosUsuario dados) {
        if (dados == null) {
            throw new ValidacaoException("Os dados do usuário são obrigatórios.");
        }
        if (dados.tipo() != tipoSuportado()) {
            throw new ValidacaoException("Esta fábrica cadastra apenas usuários do tipo " + tipoSuportado() + ".");
        }
        if (Textos.emBranco(dados.nome())) {
            throw new ValidacaoException("O nome é obrigatório.");
        }
        validarEmail(dados.email());
        if (dados.senha() == null || dados.senha().length() < TAMANHO_MINIMO_SENHA) {
            throw new ValidacaoException("A senha deve ter ao menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (usuarios.buscarPorEmail(dados.email().trim()).isPresent()) {
            throw new ConflitoException("Já existe um usuário cadastrado com o e-mail " + dados.email().trim() + ".");
        }
    }

    public static void validarEmail(String email) {
        if (Textos.emBranco(email) || !EMAIL.matcher(email.trim()).matches()) {
            throw new ValidacaoException("Informe um e-mail válido.");
        }
    }

    protected static String obrigatorio(DadosUsuario dados, String chave, String rotulo) {
        String valor = Textos.normalizar(dados.atributo(chave));
        if (valor == null) {
            throw new ValidacaoException("O campo '" + rotulo + "' é obrigatório.");
        }
        return valor;
    }
}
