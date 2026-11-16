package br.com.citizentech.dominio;


import java.util.EnumMap;
import java.util.Map;
import br.com.citizentech.excecao.DadosInvalidosException;

public class DadosTerritorio {
    private final String regiao;
    private final String codigoAcesso;
    private int populacao;
    private final Map<TipoSensor, Limiares> limiares = new EnumMap<>(TipoSensor.class);
    public DadosTerritorio(String regiao, int populacao, String codigoAcesso) {
        if (regiao == null || regiao.isBlank() || codigoAcesso == null || codigoAcesso.isBlank())
            throw new DadosInvalidosException("Regiao e codigo de acesso sao obrigatorios.");
        this.regiao = regiao;
        this.codigoAcesso = codigoAcesso;
        setPopulacao(populacao);
        limiares.put(TipoSensor.CHUVA, new Limiares(15, 30, 50));
        limiares.put(TipoSensor.NIVEL_RIO, new Limiares(80, 120, 180));
        limiares.put(TipoSensor.SATURACAO_SOLO, new Limiares(70, 85, 95));
    }
    public void alterarLimiares(TipoSensor tipo, Limiares novos, String codigo) {
        if (!codigoAcesso.equals(codigo)) throw new DadosInvalidosException("Codigo de acesso incorreto.");
        if (tipo == null || novos == null) throw new DadosInvalidosException("Sensor e limiares obrigatorios.");
        tipo.validar(novos.critico());
        limiares.put(tipo, novos);
    }
    public void setPopulacao(int populacao) {
        if (populacao < 0) throw new DadosInvalidosException("Populacao nao pode ser negativa.");
        this.populacao = populacao;
    }
    public String getRegiao() { return regiao; }
    public int getPopulacao() { return populacao; }
    public Limiares getLimiares(TipoSensor tipo) { return limiares.get(tipo); }
}
