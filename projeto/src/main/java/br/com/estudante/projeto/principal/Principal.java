package br.com.estudante.projeto.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.estudante.projeto.model.Categoria;
import br.com.estudante.projeto.model.DadosSerie;
import br.com.estudante.projeto.model.DadosTemporada;
import br.com.estudante.projeto.model.Episodio;
import br.com.estudante.projeto.model.Serie;
import br.com.estudante.projeto.repository.SerieRepository;
import br.com.estudante.projeto.service.ConsumoApi;
import br.com.estudante.projeto.service.ConverteDados;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=d0bc9998";

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio; // foi criado essa instacia da serieRepository
    private List<Serie> series = new ArrayList<>();

    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {// esse contrutor vem classe principal onde recebe o @Autowired
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por titulo
                    5 - Buscar séries por ator
                    6 - Top 5 séries
                    7 - Buscar séries por categoria
                    8 - Buscar número maximo de temporada por série
                    9 - Buscar episódio por trecho
                    10- top 5 episodios  por série
                    11- Buscar episodios a patir de uma data 
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpsodioPorTrecho();
                    break;
                case 10:
                    topEpsodioPorSerie();
                    break;
                case 11:
                    buscarEpisodioDepoisDeUmaData();
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }

    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados); // Aqui foi feito a instancia para poder informa onde será salvo os dados
        // dadosSeries.add(dados);
        repositorio.save(serie);// Agora e possivel salvar no banco Postgree
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {

        listarSeriesBuscadas();

        System.out.println("Escolha um série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        // ! Aqui estou trabalhando com relacionamento Bidirecional
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(
                        ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada !!!!");
        }
    }

    private void listarSeriesBuscadas() {

        // ? Para buscar dados do banco
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha um série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
       
        if (serieBusca.isPresent()) {
            System.out.println("\n\n\tDados da série:\n" + serieBusca.get());
        } else {
            System.out.println("Série não localizada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Escolha um série pelo nome do ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("Avaliação a partir de qual valor ? ");
        var avalicao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio
                .findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avalicao);
        System.out.println("\n\nSéries em que " + nomeAtor + " Trabalhou :");

        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " Avalição : " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {

        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine().trim();
        try {
            Categoria categoria = Categoria.fromPortugues(nomeGenero);
            List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

            System.out.println("Séries da categoria " + nomeGenero);
            seriesPorCategoria.forEach(System.out::println);
        } catch (IllegalArgumentException e) {
            System.out.println("Categoria não encontrada: " + nomeGenero);
            System.out.println("Categorias disponíveis: Ação, Romance, Comédia, Drama, Crime.");
        }
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> filtroSeries = repositorio.seriePorTemporadaEAvalicao(totalTemporadas, avaliacao);

        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s -> System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpsodioPorTrecho() {
        System.out.println("Informe um trecho do episodio");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrado = repositorio.episodiosPorTrecho(trechoEpisodio);
        System.out.println("\n\n");

        episodiosEncontrado.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(),
                e.getNumeroEpisodio(), e.getTitulo()));
        System.out.println("\n\n");
    }

    private void topEpsodioPorSerie(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
           Serie serie = serieBusca.get();
           List <Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
           topEpisodios.forEach(e -> System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
           e.getSerie().getTitulo(), e.getTemporada(),
           e.getNumeroEpisodio(), e.getTitulo(),e.getAvaliacao()));
            System.out.println("\n\n");
        }
    }
    private void buscarEpisodioDepoisDeUmaData(){
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodioPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(System.out::println);
        }
    }
}