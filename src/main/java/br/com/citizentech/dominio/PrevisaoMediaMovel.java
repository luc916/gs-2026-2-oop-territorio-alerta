package br.com.citizentech.dominio;


import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {
    public PrevisaoMediaMovel() { super("Media movel das ultimas 5 leituras", 5); }
    @Override public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        return media(historico, historico.size() - 5, historico.size());
    }
}
