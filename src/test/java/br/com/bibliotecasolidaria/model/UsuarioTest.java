package br.com.bibliotecasolidaria.model;

import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.util.SenhaUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    private final String hash = SenhaUtil.gerarHash("segredo1");

    @Test
    void autenticaApenasComASenhaCorreta() {
        Doador d = new Doador(1L, "Ana", "ana@x.com", hash, true, "123", false);
        assertTrue(d.autenticar("segredo1"));
        assertFalse(d.autenticar("errada"));
        assertFalse(d.autenticar(null));
    }

    @Test
    void cadaSubclasseInformaSeuTipo() {
        assertEquals(TipoUsuario.DOADOR, new Doador(1L, "a", "a@a", hash, true, "1", false).getTipo());
        assertEquals(TipoUsuario.USUARIO_SITE, new UsuarioSite(1L, "a", "a@a", hash, true, null, null).getTipo());
        assertEquals(TipoUsuario.FUNCIONARIO, new Funcionario(1L, "a", "a@a", hash, true, "M1", "Bibliotecário").getTipo());
    }

    @Test
    void usuarioInativoNaoPodeEmprestar() {
        UsuarioSite u = new UsuarioSite(1L, "a", "a@a", hash, true, null, null);
        assertTrue(u.podeEmprestar());
        u.setAtivo(false);
        assertFalse(u.podeEmprestar());
    }

    @Test
    void toStringNaoExpoeOHashDaSenha() {
        assertFalse(new Doador(1L, "Ana", "ana@x.com", hash, true, "123", false).toString().contains(hash));
    }
}
