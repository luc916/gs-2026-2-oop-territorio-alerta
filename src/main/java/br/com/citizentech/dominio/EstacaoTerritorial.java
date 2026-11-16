package br.com.citizentech.dominio;


import java.util.List;

public class EstacaoTerritorial extends ComponenteTerritorial {
    private final List<Sensor> sensores;
    public EstacaoTerritorial(String regiao, List<Sensor> sensores) {
        super("EST-01", "Estacao demonstrativa", regiao);
        this.sensores = List.copyOf(sensores);
    }
    @Override public boolean verificarStatus() {
        boolean funcionando = !sensores.isEmpty() && sensores.stream().allMatch(Sensor::verificarFuncionamento);
        atualizarStatus(funcionando ? "OPERACIONAL (SIMULADO)" : "FONTE INDISPONIVEL");
        return funcionando;
    }
    public List<Sensor> getSensores() { return sensores; }
}
