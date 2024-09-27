package br.com.estudante.projeto;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.estudante.projeto.principal.Principal;

@SpringBootApplication
public class ProjetoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ProjetoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n\n\nRafael faça cada vez melhor S2\n\n");
		Principal principal = new Principal();
		principal.exibeMenu();
	}

}
