package br.com.bibliotecasolidaria.controller;

import br.com.bibliotecasolidaria.exception.ConflitoException;
import br.com.bibliotecasolidaria.exception.ErroPersistenciaException;
import br.com.bibliotecasolidaria.exception.NaoEncontradoException;
import br.com.bibliotecasolidaria.exception.NegocioException;
import br.com.bibliotecasolidaria.exception.ValidacaoException;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Base dos controllers: executa a ação e converte exceções em {@link Resposta}. */
abstract class BaseController {
    private static final Logger LOG = Logger.getLogger(BaseController.class.getName());

    protected Resposta executar(Supplier<Resposta> acao) {
        try {
            return acao.get();
        } catch (ValidacaoException e) {
            return Resposta.erro(400, e.getMessage());
        } catch (NaoEncontradoException e) {
            return Resposta.erro(404, e.getMessage());
        } catch (ConflitoException e) {
            return Resposta.erro(409, e.getMessage());
        } catch (NegocioException e) {
            return Resposta.erro(422, e.getMessage());
        } catch (IllegalArgumentException e) {
            return Resposta.erro(400, e.getMessage());
        } catch (ErroPersistenciaException e) {
            LOG.log(Level.SEVERE, "Erro de persistência", e);
            return Resposta.erro(500, "Erro ao acessar o banco de dados.");
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Erro inesperado", e);
            return Resposta.erro(500, "Erro interno: " + e.getMessage());
        }
    }

    protected static Resposta lista(String descricao, List<?> itens) {
        return Resposta.ok(itens.size() + " " + descricao + " encontrado(s).", itens);
    }
}
