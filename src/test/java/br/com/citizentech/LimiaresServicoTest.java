package br.com.citizentech;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import br.com.citizentech.dominio.*;
import br.com.citizentech.excecao.*;
import br.com.citizentech.persistencia.*;
import br.com.citizentech.servico.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LimiaresServicoTest {
    @ParameterizedTest
    @CsvSource({"14.99,SEM_ALERTA", "15,ATENCAO", "29.99,ATENCAO", "30,ALERTA", "49.99,ALERTA", "50,CRITICO"})
    void comparaOsTresNiveisInclusiveNoLimiteExato(double valor, String esperado) {
        // Arrange
        var medicoes = mock(MedicaoDAO.class);
        var alertas = mock(AlertaDAO.class);
        when(medicoes.buscarHistoricoPorSensor(TipoSensor.CHUVA)).thenReturn(
            IntStream.range(0,5).mapToObj(i -> new Medicao(TipoSensor.CHUVA, valor, LocalDateTime.now())).toList());
        var servico = new ServicoMonitoramento(medicoes, alertas, new DadosTerritorio("Teste", 100, "2468"));
        // Act
        var resultado = servico.prever(TipoSensor.CHUVA, new PrevisaoMediaMovel());
        // Assert
        assertEquals(esperado, resultado.alerta().map(a -> a.nivel().name()).orElse("SEM_ALERTA"));
        if (resultado.alerta().isPresent()) verify(alertas).salvar(resultado.alerta().get());
        else verifyNoInteractions(alertas);
    }
    @Test void leituraInvalidaNaoEGravada() {
        // Arrange
        var medicoes = mock(MedicaoDAO.class);
        var alertas = mock(AlertaDAO.class);
        var sensor = mock(Sensor.class);
        when(sensor.verificarFuncionamento()).thenReturn(true);
        when(sensor.retornarTipo()).thenReturn(TipoSensor.CHUVA);
        when(sensor.lerValor()).thenReturn(-1.0);
        var servico = new ServicoMonitoramento(medicoes, alertas, new DadosTerritorio("Teste", 0, "2468"));
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.coletar(sensor));
        verifyNoInteractions(medicoes, alertas);
    }
    @Test void fonteIndisponivelNaoEConsultadaNemGravada() {
        // Arrange
        var medicoes = mock(MedicaoDAO.class);
        var alertas = mock(AlertaDAO.class);
        var sensor = mock(Sensor.class);
        when(sensor.verificarFuncionamento()).thenReturn(false);
        var servico = new ServicoMonitoramento(medicoes, alertas, new DadosTerritorio("Teste", 0, "2468"));
        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.coletar(sensor));
        verify(sensor, never()).lerValor();
        verifyNoInteractions(medicoes, alertas);
    }
}
