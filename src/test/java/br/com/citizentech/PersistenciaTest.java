package br.com.citizentech;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.time.LocalDateTime;
import br.com.citizentech.dominio.*;
import br.com.citizentech.persistencia.*;
import static org.junit.jupiter.api.Assertions.*;

class PersistenciaTest {
    @TempDir Path pasta;
    @Test void historicoEAlertasSobrevivemAReaberturaDoBanco() throws Exception {
        // Arrange: banco em arquivo, nao em memoria.
        String url = "jdbc:h2:file:" + pasta.resolve("teste").toAbsolutePath();
        Banco primeiro = new Banco(url); primeiro.inicializar();
        var instante = LocalDateTime.of(2026,11,11,12,0);
        new MedicaoDAO(primeiro).salvar(new Medicao(TipoSensor.CHUVA, 60, instante));
        new AlertaDAO(primeiro).salvar(new Alerta(TipoSensor.CHUVA, NivelAlerta.CRITICO, 60, 50, "Teste de persistencia", instante));
        // Act: todas as conexoes anteriores estao fechadas; novo acesso ao arquivo.
        Banco segundo = new Banco(url); segundo.inicializar();
        var medicoes = new MedicaoDAO(segundo).buscarHistoricoPorSensor(TipoSensor.CHUVA);
        var alertas = new AlertaDAO(segundo).listar();
        // Assert
        assertEquals(1, medicoes.size()); assertEquals(60, medicoes.get(0).valor(), 0.001);
        assertEquals(1, alertas.size()); assertEquals(NivelAlerta.CRITICO, alertas.get(0).nivel());
        assertTrue(new MedicaoDAO(segundo).buscarHistoricoPorSensor(TipoSensor.NIVEL_RIO).isEmpty());
    }
}
