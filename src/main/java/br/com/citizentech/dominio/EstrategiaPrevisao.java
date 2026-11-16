package br.com.citizentech.dominio;


import java.util.List;
import br.com.citizentech.excecao.DadosInvalidosException;
import br.com.citizentech.excecao.FonteIndisponivelException;

public abstract class EstrategiaPrevisao {
    private final String nome;
    private final int minimoLeituras;
    protected EstrategiaPrevisao(String nome, int minimoLeituras) {
        this.nome = nome; this.minimoLeituras = minimoLeituras;
    }
    public abstract double prever(List<Medicao> historico);
    protected void validarHistorico(List<Medicao> historico) {
        if (historico == null || historico.size() < minimoLeituras)
            throw new FonteIndisponivelException(nome + ": sao necessarias " + minimoLeituras + " leituras; nenhuma previsao emitida.");
        TipoSensor tipo = historico.get(0).sensor();
        if (historico.stream().anyMatch(m -> m.sensor() != tipo))
            throw new DadosInvalidosException("Nao misture tipos de sensor em uma previsao.");
    }
    protected double media(List<Medicao> dados, int inicio, int fim) {
        return dados.subList(inicio, fim).stream().mapToDouble(Medicao::valor).average().orElseThrow();
    }
    public String getNome() { return nome; }
}
