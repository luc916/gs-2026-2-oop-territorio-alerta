package br.com.citizentech.dominio;


public interface Sensor {
    double lerValor();
    boolean verificarFuncionamento();
    TipoSensor retornarTipo();
}
