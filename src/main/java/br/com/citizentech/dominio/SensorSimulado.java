package br.com.citizentech.dominio;


import java.util.Random;
import br.com.citizentech.excecao.FonteIndisponivelException;

public abstract class SensorSimulado implements Sensor {
    private final Random random = new Random();
    private final TipoSensor tipo;
    private final double minimo;
    private final double amplitude;
    private boolean disponivel = true;
    protected SensorSimulado(TipoSensor tipo, double minimo, double maximo) {
        this.tipo = tipo; this.minimo = minimo; this.amplitude = maximo - minimo;
    }
    public double lerValor() {
        if (!verificarFuncionamento()) throw new FonteIndisponivelException(tipo.getDescricao() + ": fonte simulada indisponivel.");
        double valor = minimo + random.nextDouble() * amplitude;
        tipo.validar(valor);
        return valor;
    }
    public boolean verificarFuncionamento() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public TipoSensor retornarTipo() { return tipo; }
}
