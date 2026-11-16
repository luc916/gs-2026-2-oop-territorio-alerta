package br.com.citizentech.dominio;


public abstract class ComponenteTerritorial {
    private final String id;
    private final String nome;
    private final String regiao;
    private String status = "NAO VERIFICADO";
    protected ComponenteTerritorial(String id, String nome, String regiao) {
        if (id == null || id.isBlank() || nome == null || nome.isBlank() || regiao == null || regiao.isBlank())
            throw new br.com.citizentech.excecao.DadosInvalidosException("Identificacao da estacao incompleta.");
        this.id = id; this.nome = nome; this.regiao = regiao;
    }
    public abstract boolean verificarStatus();
    protected void atualizarStatus(String status) { this.status = status; }
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getRegiao() { return regiao; }
    public String getStatus() { return status; }
}
