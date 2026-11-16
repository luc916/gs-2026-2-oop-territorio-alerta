package br.com.citizentech.dominio;


import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {
    public PrevisaoComparacao() { super("Comparacao de dois blocos de 5 leituras", 10); }
    @Override public double prever(List<Medicao> historico) {
        validarHistorico(historico);
        int n = historico.size();
        double atual = media(historico, n - 5, n);
        double anterior = media(historico, n - 10, n - 5);
        // Extrapolacao didatica: nivel atual + variacao entre os blocos.
        return Math.max(0, Math.min(historico.get(0).sensor().getMaximo(), atual + (atual - anterior)));
    }
}
