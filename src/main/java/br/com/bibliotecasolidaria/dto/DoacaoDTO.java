package br.com.bibliotecasolidaria.dto;

import br.com.bibliotecasolidaria.model.enums.TipoDoacao;

import java.util.Map;

/**
 * Pedido de registro de doação. Chaves de {@code dadosItem} por tipo:
 * LIVRO_FISICO (titulo, autor, isbn, categoria, estado, quantidade), EBOOK (titulo, autor, arquivoUrl, formato)
 * e DINHEIRO (valor, meioPagamento, comprovante).
 */
public record DoacaoDTO(Long idDoador, TipoDoacao tipo, Map<String, Object> dadosItem) {
    public DoacaoDTO {
        dadosItem = dadosItem == null ? Map.of() : Map.copyOf(dadosItem);
    }
}
