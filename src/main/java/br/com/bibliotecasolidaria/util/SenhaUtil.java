package br.com.bibliotecasolidaria.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Geração e conferência de hashes de senha com PBKDF2 (formato {@code pbkdf2$iteracoes$sal$hash}). */
public final class SenhaUtil {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 100_000;
    private static final int TAMANHO_SAL = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtil() { }

    public static String gerarHash(String senha) {
        byte[] sal = new byte[TAMANHO_SAL];
        RANDOM.nextBytes(sal);
        byte[] hash = derivar(senha, sal, ITERACOES);
        return "pbkdf2$" + ITERACOES + "$" + Base64.getEncoder().encodeToString(sal)
                + "$" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        if (senha == null || armazenado == null) {
            return false;
        }
        String[] partes = armazenado.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        try {
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtido = derivar(senha, Base64.getDecoder().decode(partes[2]), Integer.parseInt(partes[1]));
            return MessageDigest.isEqual(esperado, obtido);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String senha, byte[] sal, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, iteracoes, TAMANHO_HASH_BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash indisponível.", e);
        }
    }
}
