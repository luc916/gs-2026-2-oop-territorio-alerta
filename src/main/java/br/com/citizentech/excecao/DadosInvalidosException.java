package br.com.citizentech.excecao;

public class DadosInvalidosException extends RuntimeException {
    public DadosInvalidosException(String mensagem) { super(mensagem); }
}
