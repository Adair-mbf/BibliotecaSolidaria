package br.com.bibliotecasolidaria.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracaoSistema {
    private static volatile ConfiguracaoSistema instancia;
    private int diasEmprestimo = 14;
    private int maxRenovacoes = 2;
    private BigDecimal valorMultaDia = new BigDecimal("2.50");
    private int diasAvisoVencimento = 2;
    private final Map<String, String> parametros = new HashMap<>();

    private ConfiguracaoSistema() {
        carregar();
    }

    public static ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracaoSistema.class) {
                if (instancia == null) {
                    instancia = new ConfiguracaoSistema();
                }
            }
        }
        return instancia;
    }

    public void carregar() {
        parametros.put("diasEmprestimo", "14");
        parametros.put("maxRenovacoes", "2");
        parametros.put("valorMultaDia", "2.50");
        parametros.put("diasAvisoVencimento", "2");
    }

    public String get(String chave) {
        return parametros.get(chave);
    }

    public int getDiasEmprestimo() { return diasEmprestimo; }
    public int getMaxRenovacoes() { return maxRenovacoes; }
    public BigDecimal getValorMultaDia() { return valorMultaDia; }
    public int getDiasAvisoVencimento() { return diasAvisoVencimento; }
}
