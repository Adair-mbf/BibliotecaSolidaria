package br.com.bibliotecasolidaria.config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton com os parâmetros de negócio. Ordem de precedência de cada chave:
 * propriedade de sistema, variável de ambiente {@code BIBLIOTECA_<CHAVE>}, arquivo
 * {@code configuracao.properties} e, por fim, o valor padrão.
 */
public final class ConfiguracaoSistema {
    public static final String DIAS_EMPRESTIMO = "emprestimo.dias";
    public static final String MAX_RENOVACOES = "emprestimo.maxRenovacoes";
    public static final String VALOR_MULTA_DIA = "multa.valorDia";
    public static final String DIAS_AVISO_VENCIMENTO = "notificacao.diasAvisoVencimento";
    public static final String ESTRATEGIA_MULTA = "multa.estrategia";

    private static final Map<String, String> PADROES = Map.of(
            DIAS_EMPRESTIMO, "14",
            MAX_RENOVACOES, "2",
            VALOR_MULTA_DIA, "1.00",
            DIAS_AVISO_VENCIMENTO, "3",
            ESTRATEGIA_MULTA, "fixa");

    private static ConfiguracaoSistema instancia;

    private final boolean isolada;
    private final Map<String, String> sobrescritas;
    private int diasEmprestimo;
    private int maxRenovacoes;
    private BigDecimal valorMultaDia;
    private int diasAvisoVencimento;
    private Map<String, String> valores = new HashMap<>();

    private ConfiguracaoSistema(boolean isolada, Map<String, String> sobrescritas) {
        this.isolada = isolada;
        this.sobrescritas = sobrescritas;
        carregar();
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema(false, Map.of());
        }
        return instancia;
    }

    /** Cria uma configuração independente (sem ambiente nem arquivo), útil em testes e injeção. */
    public static ConfiguracaoSistema criar(Map<String, String> sobrescritas) {
        return new ConfiguracaoSistema(true, Map.copyOf(sobrescritas));
    }

    /** Relê todas as fontes de configuração e valida os valores. */
    public final synchronized void carregar() {
        Properties arquivo = new Properties();
        if (!isolada) {
            try (InputStream in = ConfiguracaoSistema.class.getResourceAsStream("/configuracao.properties")) {
                if (in != null) {
                    arquivo.load(in);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler configuracao.properties.", e);
            }
        }
        Map<String, String> novos = new HashMap<>();
        for (Map.Entry<String, String> padrao : PADROES.entrySet()) {
            String chave = padrao.getKey();
            String valor = sobrescritas.get(chave);
            if (valor == null && !isolada) {
                valor = System.getProperty(chave);
                if (valor == null) {
                    valor = System.getenv("BIBLIOTECA_" + chave.toUpperCase().replace('.', '_'));
                }
                if (valor == null) {
                    valor = arquivo.getProperty(chave);
                }
            }
            novos.put(chave, valor == null ? padrao.getValue() : valor.trim());
        }
        int dias = inteiroPositivo(novos, DIAS_EMPRESTIMO, 1);
        int renovacoes = inteiroPositivo(novos, MAX_RENOVACOES, 0);
        int aviso = inteiroPositivo(novos, DIAS_AVISO_VENCIMENTO, 0);
        BigDecimal multa = decimal(novos, VALOR_MULTA_DIA);
        this.valores = novos;
        this.diasEmprestimo = dias;
        this.maxRenovacoes = renovacoes;
        this.diasAvisoVencimento = aviso;
        this.valorMultaDia = multa;
    }

    public String get(String chave) { return valores.get(chave); }

    public int getDiasEmprestimo() { return diasEmprestimo; }

    public int getMaxRenovacoes() { return maxRenovacoes; }

    public BigDecimal getValorMultaDia() { return valorMultaDia; }

    public int getDiasAvisoVencimento() { return diasAvisoVencimento; }

    public String getEstrategiaMulta() { return valores.get(ESTRATEGIA_MULTA); }

    private static int inteiroPositivo(Map<String, String> v, String chave, int minimo) {
        try {
            int valor = Integer.parseInt(v.get(chave));
            if (valor < minimo) {
                throw new IllegalArgumentException("A configuração '" + chave + "' deve ser >= " + minimo + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("A configuração '" + chave + "' deve ser um número inteiro.", e);
        }
    }

    private static BigDecimal decimal(Map<String, String> v, String chave) {
        try {
            BigDecimal valor = new BigDecimal(v.get(chave));
            if (valor.signum() < 0) {
                throw new IllegalArgumentException("A configuração '" + chave + "' não pode ser negativa.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("A configuração '" + chave + "' deve ser um número decimal.", e);
        }
    }
}
