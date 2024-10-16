package br.com.estudante.projeto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudante.projeto.model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {

}
