package br.com.citizentech.persistencia;


import java.sql.*;
import java.util.*;
import br.com.citizentech.dominio.*;

public class MedicaoDAO {
    private final Banco banco;
    public MedicaoDAO(Banco banco) { this.banco = banco; }
    public void salvar(Medicao medicao) {
        try (Connection c = banco.conectar(); PreparedStatement ps = c.prepareStatement("INSERT INTO medicao(sensor, valor, instante) VALUES (?, ?, ?)")) {
            ps.setString(1, medicao.sensor().name()); ps.setDouble(2, medicao.valor());
            ps.setTimestamp(3, Timestamp.valueOf(medicao.instante())); ps.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Falha ao gravar medicao: " + e.getMessage(), e); }
    }
    public List<Medicao> buscarHistoricoPorSensor(TipoSensor sensor) {
        List<Medicao> dados = new ArrayList<>();
        try (Connection c = banco.conectar(); PreparedStatement ps = c.prepareStatement("SELECT sensor, valor, instante FROM medicao WHERE sensor = ? ORDER BY instante, id")) {
            ps.setString(1, sensor.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) dados.add(new Medicao(TipoSensor.valueOf(rs.getString("sensor")), rs.getDouble("valor"), rs.getTimestamp("instante").toLocalDateTime()));
            }
        } catch (SQLException e) { throw new IllegalStateException("Falha ao ler historico: " + e.getMessage(), e); }
        return dados;
    }
}
