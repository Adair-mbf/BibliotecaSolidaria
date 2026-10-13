package br.com.bibliotecasolidaria.persistence;

import br.com.bibliotecasolidaria.model.Domain.TipoUsuario;
import br.com.bibliotecasolidaria.model.Domain.Usuario;

public final class UsuarioDao extends JdbcCrudDao<Usuario> {
    private static final String COLUMNS =
            "nome, email, senha_hash, ativo, tipo_usuario, documento, pessoa_juridica, "
                    + "telefone, endereco, matricula, cargo";

    public UsuarioDao(SqlConnectionProvider connections) {
        super(
                connections,
                "usuarios",
                "INSERT INTO usuarios (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                "UPDATE usuarios SET nome = ?, email = ?, senha_hash = ?, ativo = ?, tipo_usuario = ?, "
                        + "documento = ?, pessoa_juridica = ?, telefone = ?, endereco = ?, matricula = ?, "
                        + "cargo = ? WHERE id = ?",
                resultSet -> new Usuario(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("email"),
                        resultSet.getString("senha_hash"),
                        resultSet.getBoolean("ativo"),
                        TipoUsuario.valueOf(resultSet.getString("tipo_usuario")),
                        resultSet.getString("documento"),
                        resultSet.getObject("pessoa_juridica", Boolean.class),
                        resultSet.getString("telefone"),
                        resultSet.getString("endereco"),
                        resultSet.getString("matricula"),
                        resultSet.getString("cargo")),
                usuario -> new Object[]{
                        usuario.nome(), usuario.email(), usuario.senhaHash(), usuario.ativo(),
                        usuario.tipo().name(), usuario.documento(), usuario.pessoaJuridica(),
                        usuario.telefone(), usuario.endereco(), usuario.matricula(), usuario.cargo()
                },
                usuario -> requiredId(usuario.id()),
                (usuario, id) -> new Usuario(
                        id, usuario.nome(), usuario.email(), usuario.senhaHash(), usuario.ativo(),
                        usuario.tipo(), usuario.documento(), usuario.pessoaJuridica(), usuario.telefone(),
                        usuario.endereco(), usuario.matricula(), usuario.cargo()));
    }
}
