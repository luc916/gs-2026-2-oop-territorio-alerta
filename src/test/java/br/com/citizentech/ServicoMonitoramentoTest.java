package br.com.citizentech;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.stream.IntStream;
import br.com.citizentech.dominio.*;
import br.com.citizentech.persistencia.*;
import br.com.citizentech.servico.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicoMonitoramentoTest {
    @Test void previsaoCriticaGravaAlertaComJustificativaSemBanco() {
        // Arrange
        MedicaoDAO medicoes = mock(MedicaoDAO.class);
        AlertaDAO alertas = mock(AlertaDAO.class);
        var dados = new DadosTerritorio("Teste", 100, "2468");
        when(medicoes.buscarHistoricoPorSensor(TipoSensor.CHUVA)).thenReturn(
            IntStream.range(0,5).mapToObj(i -> new Medicao(TipoSensor.CHUVA, 60, LocalDateTime.now())).toList());
        var servico = new ServicoMonitoramento(medicoes, alertas, dados);
        // Act
        var resultado = servico.prever(TipoSensor.CHUVA, new PrevisaoMediaMovel());
        // Assert
        var captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertas).salvar(captor.capture());
        assertEquals(NivelAlerta.CRITICO, captor.getValue().nivel());
        assertEquals(60, resultado.valor(), 0.001);
        assertEquals(50, captor.getValue().limite(), 0.001);
        assertTrue(captor.getValue().justificativa().contains("Chuva"));
        assertTrue(resultado.alerta().isPresent());
    }
    @Test void abaixoDoLimiarNaoGravaAlerta() {
        // Arrange
        var medicoes = mock(MedicaoDAO.class); var alertas = mock(AlertaDAO.class);
        when(medicoes.buscarHistoricoPorSensor(TipoSensor.CHUVA)).thenReturn(
            IntStream.range(0,5).mapToObj(i -> new Medicao(TipoSensor.CHUVA, 5, LocalDateTime.now())).toList());
        var servico = new ServicoMonitoramento(medicoes, alertas, new DadosTerritorio("Teste", 0, "2468"));
        // Act
        var resultado = servico.prever(TipoSensor.CHUVA, new PrevisaoMediaMovel());
        // Assert
        assertTrue(resultado.alerta().isEmpty());
        verifyNoInteractions(alertas);
    }
    @Test void medicaoValidaEGravada() {
        // Arrange
        var medicoes = mock(MedicaoDAO.class); var alertas = mock(AlertaDAO.class);
        Sensor sensor = mock(Sensor.class);
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.retornarTipo()).thenReturn(TipoSensor.CHUVA);
        when(sensor.lerValor()).thenReturn(12.5);
        var servico = new ServicoMonitoramento(medicoes, alertas, new DadosTerritorio("Teste", 0, "2468"));
        // Act
        Medicao medicao = servico.coletar(sensor);
        // Assert
        assertEquals(12.5, medicao.valor(), 0.001);
        verify(medicoes).salvar(medicao);
    }
}
