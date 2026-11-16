package br.com.citizentech.dominio;


import br.com.citizentech.excecao.DadosInvalidosException;

public enum TipoSensor {
    CHUVA("Chuva", "mm/h", 300), NIVEL_RIO("Nivel do rio", "cm", 1000), SATURACAO_SOLO("Saturacao do solo", "%", 100);
    private final String descricao;
    private final String unidade;
    private final double maximo;
    TipoSensor(String descricao, String unidade, double maximo) {
        this.descricao = descricao; this.unidade = unidade; this.maximo = maximo;
    }
    public String getDescricao() { return descricao; }
    public String getUnidade() { return unidade; }
    public double getMaximo() { return maximo; }
    public void validar(double valor) {
        if (!Double.isFinite(valor) || valor < 0 || valor > maximo)
            throw new DadosInvalidosException(descricao + ": valor deve estar entre 0 e " + maximo + " " + unidade);
    }
}
