package br.com.citizentech.dominio;


import br.com.citizentech.excecao.DadosInvalidosException;

public record Limiares(double atencao, double alerta, double critico) {
    public Limiares {
        if (!Double.isFinite(atencao) || !Double.isFinite(alerta) || !Double.isFinite(critico)
                || atencao <= 0 || alerta <= atencao || critico <= alerta)
            throw new DadosInvalidosException("Limiares devem ser finitos e obedecer: 0 < atencao < alerta < critico.");
    }
}
