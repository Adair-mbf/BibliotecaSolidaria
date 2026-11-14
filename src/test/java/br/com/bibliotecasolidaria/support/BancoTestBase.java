package br.com.bibliotecasolidaria.support;

import br.com.bibliotecasolidaria.config.ConfiguracaoSistema;
import br.com.bibliotecasolidaria.config.ContextoAplicacao;
import br.com.bibliotecasolidaria.dto.DadosUsuario;
import br.com.bibliotecasolidaria.model.Doador;
import br.com.bibliotecasolidaria.model.ExemplarFisico;
import br.com.bibliotecasolidaria.model.Livro;
import br.com.bibliotecasolidaria.model.UsuarioSite;
import br.com.bibliotecasolidaria.model.enums.CategoriaLivro;
import br.com.bibliotecasolidaria.model.enums.EstadoConservacao;
import br.com.bibliotecasolidaria.model.enums.StatusItem;
import br.com.bibliotecasolidaria.model.enums.TipoUsuario;
import br.com.bibliotecasolidaria.persistence.ConexaoBanco;
import br.com.bibliotecasolidaria.persistence.InicializadorSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Base dos testes de integração: H2 em memória (modo PostgreSQL) com o schema real, recriado a cada teste. */
public abstract class BancoTestBase {
    protected RelogioMutavel relogio;
    protected ConexaoBanco conexao;
    protected ContextoAplicacao ctx;
    protected List<String> emailsEnviados;

    @BeforeEach
    void prepararBanco() throws Exception {
        relogio = new RelogioMutavel();
        emailsEnviados = new ArrayList<>();
        conexao = ConexaoBanco.novaInstancia("jdbc:h2:mem:biblioteca;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "sa");
        try (Connection c = conexao.getConexao(); Statement st = c.createStatement()) {
            st.execute("DROP ALL OBJECTS");
        }
        InicializadorSchema.executar(conexao);
        ctx = ContextoAplicacao.criar(conexao, ConfiguracaoSistema.criar(Map.of()), relogio, emailsEnviados::add);
    }

    @AfterEach
    void fecharBanco() {
        conexao.fechar();
    }

    protected LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    protected UsuarioSite novoUsuarioSite(String email) {
        return (UsuarioSite) ctx.usuarioService.cadastrar(new DadosUsuario("Usuário " + email, email, "senha123",
                TipoUsuario.USUARIO_SITE, Map.of("telefone", "6299999", "endereco", "Rua 1")));
    }

    protected Doador novoDoador(String email, boolean pessoaJuridica) {
        return (Doador) ctx.usuarioService.cadastrar(new DadosUsuario("Doador " + email, email, "senha123",
                TipoUsuario.DOADOR, Map.of("documento", "123", "pessoaJuridica", String.valueOf(pessoaJuridica))));
    }

    protected Livro novoLivro(String titulo, String isbn) {
        return ctx.acervoService.cadastrarLivro(new Livro(null, titulo, "Autor", "Editora", isbn,
                CategoriaLivro.DIDATICO, 2000));
    }

    protected ExemplarFisico novoExemplar(long livroId, String tombo) {
        return (ExemplarFisico) ctx.acervoService.cadastrarItem(new ExemplarFisico(null, livroId,
                StatusItem.DISPONIVEL, tombo, EstadoConservacao.BOM, "Estante 1"));
    }
}
