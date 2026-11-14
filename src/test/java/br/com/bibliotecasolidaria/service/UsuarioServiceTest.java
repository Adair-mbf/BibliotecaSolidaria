package br.com.bibliotecasolidaria.service;

import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.Funcionario;
import br.com.bibliotecasolidaria.model.Usuario;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.support.BancoTestBase;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest extends BancoTestBase {
    private DadosUsuario dados(String email, TipoUsuario tipo, Map<String, String> attrs) {
        Map<String, String> completos = new java.util.HashMap<>(attrs);
        if (tipo == TipoUsuario.USUARIO_SITE) { // exigidos pelo schema
            completos.putIfAbsent("telefone", "6299999");
            completos.putIfAbsent("endereco", "Rua 1");
        }
        return new DadosUsuario("Fulano", email, "senha123", tipo, completos);
    }

    @Test
    void cadastraOsTresTiposComAtributosEspecificos() {
        Usuario d = ctx.usuarioService.cadastrar(dados("d@x.com", TipoUsuario.DOADOR, Map.of("documento", "12345", "pessoaJuridica", "true")));
        Usuario s = ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of("telefone", "62")));
        Usuario f = ctx.usuarioService.cadastrar(dados("f@x.com", TipoUsuario.FUNCIONARIO, Map.of("matricula", "M1", "cargo", "Atendente")));
        assertTrue(d instanceof Doador doador && doador.isPessoaJuridica() && doador.getDocumento().equals("12345"));
        assertTrue(s instanceof UsuarioSite);
        assertTrue(f instanceof Funcionario func && func.getCargo().equals("Atendente"));
        assertTrue(d.isAtivo());
    }

    @Test
    void senhaEhArmazenadaComoHashENuncaEmTextoPuro() {
        Usuario u = ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        Usuario salvo = ctx.usuarioRepositorio.buscarPorId(u.getId()).orElseThrow();
        assertNotEquals("senha123", salvo.getSenhaHash());
        assertTrue(salvo.autenticar("senha123"));
    }

    @Test
    void emailDuplicadoEmQualquerCaixaEhConflito() {
        ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        assertThrows(ConflitoException.class, () -> ctx.usuarioService.cadastrar(dados("S@X.COM", TipoUsuario.DOADOR, Map.of("documento", "1"))));
    }

    @Test
    void validaCamposObrigatorios() {
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(dados("sem-arroba", TipoUsuario.USUARIO_SITE, Map.of())));
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(
                new DadosUsuario("X", "a@x.com", "123", TipoUsuario.USUARIO_SITE, Map.of())), "senha curta");
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(
                new DadosUsuario(" ", "a@x.com", "senha123", TipoUsuario.USUARIO_SITE, Map.of())), "nome vazio");
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.DOADOR, Map.of())), "documento");
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.FUNCIONARIO, Map.of("matricula", "M"))), "cargo");
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(dados("a@x.com", null, Map.of())), "tipo");
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.cadastrar(new DadosUsuario("X", "a@x.com", "senha123",
                TipoUsuario.USUARIO_SITE, Map.of("telefone", "62"))), "endereço");
    }

    @Test
    void autenticaPorEmailESenha() {
        ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        assertEquals("s@x.com", ctx.usuarioService.autenticar("S@x.com", "senha123").getEmail());
        assertThrows(NegocioException.class, () -> ctx.usuarioService.autenticar("s@x.com", "errada"));
        assertThrows(NegocioException.class, () -> ctx.usuarioService.autenticar("nao@x.com", "senha123"));
        assertThrows(NegocioException.class, () -> ctx.usuarioService.autenticar(null, "senha123"));
    }

    @Test
    void usuarioInativoNaoAutentica() {
        Usuario u = ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        ctx.usuarioService.definirAtivo(u.getId(), false);
        assertThrows(NegocioException.class, () -> ctx.usuarioService.autenticar("s@x.com", "senha123"));
    }

    @Test
    void atualizaApenasOsCamposInformados() {
        Usuario u = ctx.usuarioService.cadastrar(dados("s@x.com", TipoUsuario.USUARIO_SITE, Map.of("telefone", "62")));
        ctx.usuarioService.atualizar(u.getId(), new DadosUsuario("Novo Nome", null, "novaSenha1", null, Map.of("endereco", "Rua 2")), null);
        UsuarioSite lido = (UsuarioSite) ctx.usuarioService.buscar(u.getId());
        assertEquals("Novo Nome", lido.getNome());
        assertEquals("s@x.com", lido.getEmail());
        assertEquals("62", lido.getTelefone());
        assertEquals("Rua 2", lido.getEndereco());
        assertTrue(lido.autenticar("novaSenha1"));
    }

    @Test
    void atualizarEmailParaOdeOutroUsuarioEhConflito() {
        ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        Usuario b = ctx.usuarioService.cadastrar(dados("b@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        assertThrows(ConflitoException.class, () -> ctx.usuarioService.atualizar(b.getId(), new DadosUsuario(null, "a@x.com", null, null, Map.of()), null));
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.atualizar(b.getId(), new DadosUsuario(null, "ruim", null, null, Map.of()), null));
        assertThrows(ValidacaoException.class, () -> ctx.usuarioService.atualizar(b.getId(), new DadosUsuario(null, null, "123", null, Map.of()), null));
    }

    @Test
    void buscaListaEFiltraPorTipo() {
        ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        ctx.usuarioService.cadastrar(dados("d@x.com", TipoUsuario.DOADOR, Map.of("documento", "1")));
        assertEquals(2, ctx.usuarioService.listar().size());
        assertEquals(1, ctx.usuarioService.listarPorTipo(TipoUsuario.DOADOR).size());
        assertThrows(NaoEncontradoException.class, () -> ctx.usuarioService.buscar(999));
    }

    @Test
    void buscaTipadaRejeitaTipoErrado() {
        Usuario s = ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        assertThrows(NegocioException.class, () -> ctx.usuarioService.buscarDoador(s.getId()));
        Usuario d = ctx.usuarioService.cadastrar(dados("d@x.com", TipoUsuario.DOADOR, Map.of("documento", "1")));
        assertThrows(NegocioException.class, () -> ctx.usuarioService.buscarUsuarioSite(d.getId()));
    }

    @Test
    void removeUsuarioExistenteEFalhaParaInexistente() {
        Usuario u = ctx.usuarioService.cadastrar(dados("a@x.com", TipoUsuario.USUARIO_SITE, Map.of()));
        ctx.usuarioService.remover(u.getId());
        assertThrows(NaoEncontradoException.class, () -> ctx.usuarioService.buscar(u.getId()));
        assertThrows(NaoEncontradoException.class, () -> ctx.usuarioService.remover(u.getId()));
    }
}
