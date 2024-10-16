package br.com.estudante.projeto.model;

import java.util.OptionalDouble;

import br.com.estudante.projeto.service.ConsultaChatGPT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity // Aqui estamos relacionando essa classe com o banco de dados do Postgre
@Table(name = "series") // estou renomeando o a tabela no banco com o nome series agora
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Aqui estou criando as chaves pk
    private Long id;

    @Column(unique = true) // aqui informando que o titulo não pode se repitir
    private String titulo;
    private Integer totalTemporadas;
    private Double avaliacao;

    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @Transient // ! com essa anotação a Jpa ira transitar entre as classes
    private List<Episodio> episodios = new ArrayList<>(); // ? para poder relacionar a classe serie com a episodio e
                                                          // necessario realizar
    // uma instancia

    public Serie() {

    }

    public Serie(DadosSerie dadosSerie) {
        this.titulo = dadosSerie.titulo();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.avaliacao = OptionalDouble.of(Double.valueOf(dadosSerie.avaliacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = ConsultaChatGPT.obterTraducao(dadosSerie.sinopse()).trim();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }

    @Override
    public String toString() {
        return "\n\tSerie\n" +
                "Genero = " + genero + "\n" +
                "Titulo = " + titulo + "\n" +
                "TotalTemporadas=" + totalTemporadas + "\n" +
                "Avaliacao = " + avaliacao + "\n" +
                "Atores = '" + atores + "\n" +
                "Poster = '" + poster + "\n" +
                "Sinopse ='" + sinopse + "\n";
    }

}
