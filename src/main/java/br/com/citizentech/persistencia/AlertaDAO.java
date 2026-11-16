package br.com.citizentech.persistencia;


import java.sql.*;
import java.util.*;
import br.com.citizentech.dominio.*;

public class AlertaDAO {
    private final Banco banco;
    public AlertaDAO(Banco banco) { this.banco = banco; }
    public void salvar(Alerta alerta) {
        try (Connection c = banco.conectar(); PreparedStatement ps = c.prepareStatement("INSERT INTO alerta(sensor,nivel,previsto,limite,justificativa,instante) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, alerta.sensor().name()); ps.setString(2, alerta.nivel().name());
            ps.setDouble(3, alerta.previsto()); ps.setDouble(4, alerta.limite());
            ps.setString(5, alerta.justificativa()); ps.setTimestamp(6, Timestamp.valueOf(alerta.instante())); ps.executeUpdate();
        } catch (SQLException e) { throw new IllegalStateException("Falha ao gravar alerta: " + e.getMessage(), e); }
    }
    public List<Alerta> listar() {
        List<Alerta> dados = new ArrayList<>();
        try (Connection c = banco.conectar(); PreparedStatement ps = c.prepareStatement("SELECT * FROM alerta ORDER BY instante, id"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) dados.add(new Alerta(TipoSensor.valueOf(rs.getString("sensor")), NivelAlerta.valueOf(rs.getString("nivel")), rs.getDouble("previsto"), rs.getDouble("limite"), rs.getString("justificativa"), rs.getTimestamp("instante").toLocalDateTime()));
        } catch (SQLException e) { throw new IllegalStateException("Falha ao listar alertas: " + e.getMessage(), e); }
        return dados;
    }
}
