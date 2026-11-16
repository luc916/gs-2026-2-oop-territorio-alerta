package br.com.citizentech.dominio;


import java.time.LocalDateTime;

public record Alerta(TipoSensor sensor, NivelAlerta nivel, double previsto, double limite,
                     String justificativa, LocalDateTime instante) { }
