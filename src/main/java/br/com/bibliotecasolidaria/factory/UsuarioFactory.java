package br.com.bibliotecasolidaria.factory;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.model.Domain.Usuario;
import br.com.bibliotecasolidaria.persistence.UsuarioDao;

import java.sql.SQLException;

/** Creator do Factory Method: define o fluxo comum de cadastro. */
public abstract class UsuarioFactory {
    private final UsuarioDao usuarios;

    protected UsuarioFactory(UsuarioDao usuarios) {
        this.usuarios = usuarios;
    }

    public final Usuario cadastrar(DadosUsuario dados) throws SQLException {
        validar(dados);
        return usuarios.save(criarUsuario(dados));
    }

    protected void validar(DadosUsuario dados) {
        if (dados == null) {
            throw new IllegalArgumentException("Os dados do usuário são obrigatórios.");
        }
        if (dados.nome() == null || dados.nome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (dados.email() == null || dados.email().isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (dados.senha() == null || dados.senha().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }
        if (dados.tipo() == null) {
            throw new IllegalArgumentException("O tipo de usuário é obrigatório.");
        }
        validarTipo(dados.tipo());
    }

    protected abstract Usuario criarUsuario(DadosUsuario dados);

    protected abstract TipoUsuario tipoSuportado();

    private void validarTipo(TipoUsuario tipo) {
        if (tipo != tipoSuportado()) {
            throw new IllegalArgumentException("Esta factory não cadastra usuários do tipo " + tipo + ".");
        }
    }

    protected static Usuario usuarioBase(DadosUsuario dados, TipoUsuario tipo,
                                         String documento, Boolean pessoaJuridica,
                                         String telefone, String endereco,
                                         String matricula, String cargo) {
        return new Usuario(null, dados.nome().trim(), dados.email().trim(), dados.senha(), true, tipo,
                documento, pessoaJuridica, telefone, endereco, matricula, cargo);
    }
}
