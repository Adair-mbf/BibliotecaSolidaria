package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Funcionario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class UsuarioRepositorio extends RepositorioJdbcLong<Usuario> {
    private static final String COLUNAS = "nome, email, senha_hash, ativo, tipo_usuario, documento, "
            + "pessoa_juridica, telefone, endereco, matricula, cargo";

    public UsuarioRepositorio(SqlConnectionProvider conexoes) { super(conexoes); }

    @Override protected String tabela() { return "usuarios"; }

    @Override protected String sqlInsert() {
        return "INSERT INTO usuarios (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override protected String sqlUpdate() {
        return "UPDATE usuarios SET nome = ?, email = ?, senha_hash = ?, ativo = ?, tipo_usuario = ?, "
                + "documento = ?, pessoa_juridica = ?, telefone = ?, endereco = ?, matricula = ?, cargo = ? WHERE id = ?";
    }

    @Override protected Long getId(Usuario u) { return u.getId(); }

    @Override protected void setId(Usuario u, Long id) { u.setId(id); }

    @Override
    protected Object[] valores(Usuario u) {
        Object[] v = {u.getNome(), u.getEmail(), u.getSenhaHash(), u.isAtivo(), u.getTipo().name(),
                null, null, null, null, null, null};
        if (u instanceof Doador d) {
            v[5] = d.getDocumento();
            v[6] = d.isPessoaJuridica();
        } else if (u instanceof UsuarioSite s) {
            v[7] = s.getTelefone();
            v[8] = s.getEndereco();
        } else if (u instanceof Funcionario f) {
            v[9] = f.getMatricula();
            v[10] = f.getCargo();
        }
        return v;
    }

    @Override
    protected Usuario mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String nome = rs.getString("nome");
        String email = rs.getString("email");
        String hash = rs.getString("senha_hash");
        boolean ativo = rs.getBoolean("ativo");
        return switch (TipoUsuario.valueOf(rs.getString("tipo_usuario"))) {
            case DOADOR -> new Doador(id, nome, email, hash, ativo, rs.getString("documento"),
                    rs.getBoolean("pessoa_juridica"));
            case USUARIO_SITE -> new UsuarioSite(id, nome, email, hash, ativo, rs.getString("telefone"),
                    rs.getString("endereco"));
            case FUNCIONARIO -> new Funcionario(id, nome, email, hash, ativo, rs.getString("matricula"),
                    rs.getString("cargo"));
        };
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return consultarUm("SELECT * FROM usuarios WHERE LOWER(email) = LOWER(?)", email);
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return consultar("SELECT * FROM usuarios WHERE tipo_usuario = ? ORDER BY id", tipo.name());
    }
}
