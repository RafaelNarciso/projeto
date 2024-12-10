package br.com.estudante.projeto.model;

import java.text.Normalizer;

public enum Categoria {
    ACAO("Action", "Ação"),
    ROMANCE("Romance", "Romance"),
    COMEDIA("Comedy", "Comédia"),
    DRAMA("Drama", "Drama"),
    CRIME("Crime", "Crime");

    // ! para adicionar uma correspondência entre o ENUM,e o Omdb private String
    // categoriaOmdb;
    private String categoriaOmdb;
    private String categoriaPortugues;

    // ? e logo depois e necessario criar um contrustor que vai recebe categoriaOmdb
    Categoria(String categoriaOmdb, String categoriaPortugues) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaPortugues = categoriaPortugues;
    }

    // * Aqui neste metodo ele verificar o que esta vindo do Omdb e converte, tudo
    // dinamicamente
    // categoria de Enum por exemplo comedy ele passa para comedy*/

    public static Categoria fromString(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }

    public static Categoria fromPortugues(String text) {
        // Remover acentos e normalizar a string para maiúsculas/minúsculas
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();

        for (Categoria categoria : Categoria.values()) {
            String normalizedCategoria = Normalizer.normalize(categoria.categoriaPortugues, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase();

            if (normalizedCategoria.equals(normalizedText)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
