package br.com.citizentech.dominio;


import java.time.LocalDateTime;
import br.com.citizentech.excecao.DadosInvalidosException;

public record Medicao(TipoSensor sensor, double valor, LocalDateTime instante) {
    public Medicao {
        if (sensor == null || instante == null) throw new DadosInvalidosException("Sensor e instante obrigatorios.");
        sensor.validar(valor);
    }
}
