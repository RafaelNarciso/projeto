package br.com.estudante.projeto.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

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
        
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase()
                        .contains(nomeSerie.toLowerCase()))
                .findFirst();
        if(serie.isPresent()){
            var serieEncontrada = serie.get();

                

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
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
        }else{
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

}