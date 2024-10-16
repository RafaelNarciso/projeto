package br.com.estudante.projeto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.estudante.projeto.principal.Principal;
import br.com.estudante.projeto.repository.SerieRepository;

@SpringBootApplication
public class ProjetoApplication implements CommandLineRunner {

	@Autowired // anotação para que a JPA possa realizar o
	private SerieRepository repositorio;

	public static void main(String[] args) {
		SpringApplication.run(ProjetoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n\nRafael faça cada vez melhor S2\n\n");

		Principal principal = new Principal(repositorio);
		principal.exibeMenu();
	}

}
