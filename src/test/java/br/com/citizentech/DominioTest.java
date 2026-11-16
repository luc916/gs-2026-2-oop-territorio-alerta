package br.com.citizentech;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import br.com.citizentech.dominio.*;
import br.com.citizentech.excecao.*;
import br.com.citizentech.servico.FabricaPrevisao;
import static org.junit.jupiter.api.Assertions.*;

class DominioTest {
    private List<Medicao> historico(double... valores) {
        List<Medicao> lista = new ArrayList<>();
        for (double valor : valores) lista.add(new Medicao(TipoSensor.CHUVA, valor, LocalDateTime.now()));
        return lista;
    }
    @Test void rejeitaMedicaoNegativa() {
        // Arrange
        TipoSensor tipo = TipoSensor.CHUVA;
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> new Medicao(tipo, -1, LocalDateTime.now()));
    }
    @Test void rejeitaNaN() {
        // Arrange
        TipoSensor tipo = TipoSensor.CHUVA;
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> tipo.validar(Double.NaN));
    }
    @Test void mediaUsaSomenteUltimasCinco() {
        // Arrange
        var dados = historico(100, 2, 4, 6, 8, 10);
        var estrategia = new PrevisaoMediaMovel();
        // Act
        double previsto = estrategia.prever(dados);
        // Assert
        assertEquals(6, previsto, 0.0001);
    }
    @Test void comparacaoExtrapolaVariacaoEntreBlocos() {
        // Arrange
        var dados = historico(10,10,10,10,10, 20,20,20,20,20);
        var estrategia = new PrevisaoComparacao();
        // Act
        double previsto = estrategia.prever(dados);
        // Assert
        assertEquals(30, previsto, 0.0001);
    }
    @Test void semHistoricoSuficienteNaoPreve() {
        // Arrange
        var dados = historico(1,2,3,4);
        var estrategia = new PrevisaoMediaMovel();
        // Act + Assert
        assertThrows(FonteIndisponivelException.class, () -> estrategia.prever(dados));
    }
    @Test void protegeAlteracaoDeLimiares() {
        // Arrange
        var territorio = new DadosTerritorio("Regiao teste", 100, "2468");
        var novos = new Limiares(10,20,30);
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> territorio.alterarLimiares(TipoSensor.CHUVA, novos, "errado"));
        assertEquals(50, territorio.getLimiares(TipoSensor.CHUVA).critico());
    }
    @Test void rejeitaPopulacaoNegativa() {
        // Arrange
        var territorio = new DadosTerritorio("Regiao teste", 100, "2468");
        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> territorio.setPopulacao(-1));
        assertEquals(100, territorio.getPopulacao());
    }
    @Test void sensorDesligadoLancaExcecao() {
        // Arrange
        var sensor = new SensorChuva(); sensor.setDisponivel(false);
        // Act + Assert
        assertThrows(FonteIndisponivelException.class, sensor::lerValor);
    }
    @Test void fabricaCriaEstrategiasDiferentes() {
        // Arrange
        var fabrica = new FabricaPrevisao();
        // Act
        var media = fabrica.criar("1"); var comparacao = fabrica.criar("2");
        // Assert
        assertEquals(PrevisaoMediaMovel.class, media.getClass());
        assertEquals(PrevisaoComparacao.class, comparacao.getClass());
    }
}
