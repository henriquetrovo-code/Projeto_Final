package dao;

import conexao.conexaoBD;
import model.log;
import java.sql.*;

public class LogDAO {
    public void registrarLog(log log) {
        String sql = "INSERT INTO logs_sistema (venda_id, produto_id, vendedor_codigo, tipo_acao, quantidade, motivo_cancelamento) VALUES (?,?,?,?,?,?)";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getVendaId());
            stmt.setInt(2, log.getProdutoId());
            stmt.setInt(3, log.getVendedorCodigo());
            stmt.setString(4, log.getTipoAcao());
            stmt.setInt(5, log.getQuantidade());
            stmt.setString(6, log.getMotivoCancelamento());
            stmt.execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}