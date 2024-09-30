package br.com.estudante.projeto.model;

public enum Categoria {
    ACAO("Action"),
    ROMANCE("Romance"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime");

    // ! para adicionar uma correspondência entre o ENUM,e o Omdb private String
    // categoriaOmdb;
    private String categoriaOmdb;

    // ? e logo depois e necessario criar um contrustor que vai recebe categoriaOmdb
    Categoria(String categoriaOmdb) {
        this.categoriaOmdb = categoriaOmdb;
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
}
