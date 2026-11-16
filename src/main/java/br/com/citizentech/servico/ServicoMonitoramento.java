package br.com.citizentech.servico;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import br.com.citizentech.dominio.*;
import br.com.citizentech.persistencia.*;
import br.com.citizentech.excecao.FonteIndisponivelException;

public class ServicoMonitoramento {
    private final MedicaoDAO medicoes;
    private final AlertaDAO alertas;
    private final DadosTerritorio territorio;
    public ServicoMonitoramento(MedicaoDAO medicoes, AlertaDAO alertas, DadosTerritorio territorio) {
        this.medicoes = medicoes; this.alertas = alertas; this.territorio = territorio;
    }
    public Medicao coletar(Sensor sensor) {
        if (!sensor.verificarFuncionamento()) throw new FonteIndisponivelException("Fonte indisponivel: " + sensor.retornarTipo());
        Medicao medicao = new Medicao(sensor.retornarTipo(), sensor.lerValor(), LocalDateTime.now());
        medicoes.salvar(medicao);
        return medicao;
    }
    public ResultadoPrevisao prever(TipoSensor sensor, EstrategiaPrevisao estrategia) {
        List<Medicao> historico = medicoes.buscarHistoricoPorSensor(sensor);
        double valor = estrategia.prever(historico);
        sensor.validar(valor);
        Limiares l = territorio.getLimiares(sensor);
        NivelAlerta nivel = null;
        double limite = 0;
        if (valor >= l.critico()) { nivel = NivelAlerta.CRITICO; limite = l.critico(); }
        else if (valor >= l.alerta()) { nivel = NivelAlerta.ALERTA; limite = l.alerta(); }
        else if (valor >= l.atencao()) { nivel = NivelAlerta.ATENCAO; limite = l.atencao(); }
        Optional<Alerta> emitido = Optional.empty();
        if (nivel != null) {
            String motivo = String.format(java.util.Locale.ROOT, "%s: previsto %.2f %s >= limite %.2f %s; %s. Dados simulados; verificar o local antes de agir.",
                sensor.getDescricao(), valor, sensor.getUnidade(), limite, sensor.getUnidade(), estrategia.getNome());
            Alerta alerta = new Alerta(sensor, nivel, valor, limite, motivo, LocalDateTime.now());
            alertas.salvar(alerta);
            emitido = Optional.of(alerta);
        }
        return new ResultadoPrevisao(valor, emitido);
    }
    public record ResultadoPrevisao(double valor, Optional<Alerta> alerta) { }
}
