package br.com.estudante.projeto.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}