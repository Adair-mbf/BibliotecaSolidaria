package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;

public class UsuarioController extends BaseController {
    private static final String[] ATRIBUTOS = {"documento", "pessoaJuridica", "telefone", "endereco", "matricula", "cargo"};

    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    /** Campos: nome, email, senha, tipo + atributos do tipo (documento/pessoaJuridica, telefone/endereco, matricula/cargo). */
    public Resposta cadastrar(Requisicao req) {
        return executar(() -> {
            DadosUsuario dados = new DadosUsuario(req.texto("nome"), req.texto("email"), req.texto("senha"),
                    req.enumeracao("tipo", TipoUsuario.class), atributos(req));
            return Resposta.criado("Usuário cadastrado com sucesso.", usuarios.cadastrar(dados));
        });
    }

    public Resposta autenticar(Requisicao req) {
        return executar(() -> Resposta.ok("Autenticação realizada.",
                usuarios.autenticar(req.texto("email"), req.texto("senha"))));
    }

    public Resposta buscar(Requisicao req) {
        return executar(() -> Resposta.ok("Usuário encontrado.", usuarios.buscar(req.longo("id"))));
    }

    /** Campo opcional: tipo. */
    public Resposta listar(Requisicao req) {
        return executar(() -> {
            TipoUsuario tipo = req.enumeracaoOpcional("tipo", TipoUsuario.class);
            return lista("usuário(s)", tipo == null ? usuarios.listar() : usuarios.listarPorTipo(tipo));
        });
    }

    /** Campos: id + qualquer um de nome, email, senha, ativo e dos atributos; o que faltar é mantido. */
    public Resposta atualizar(Requisicao req) {
        return executar(() -> {
            DadosUsuario dados = new DadosUsuario(req.textoOpcional("nome"), req.textoOpcional("email"),
                    req.textoOpcional("senha"), null, atributos(req));
            return Resposta.ok("Usuário atualizado.",
                    usuarios.atualizar(req.longo("id"), dados, req.booleanoOpcional("ativo")));
        });
    }

    public Resposta remover(Requisicao req) {
        return executar(() -> {
            usuarios.remover(req.longo("id"));
            return Resposta.ok("Usuário removido.", null);
        });
    }

    private static Map<String, String> atributos(Requisicao req) {
        Map<String, String> m = new HashMap<>();
        for (String a : ATRIBUTOS) {
            if (req.todos().containsKey(a)) {
                m.put(a, req.todos().get(a));
            }
        }
        return m;
    }
}
