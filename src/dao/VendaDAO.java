package dao;

import conexao.conexaoBD;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public boolean salvarPedidoPendente(Venda v, List<ItemVenda> itens, String bandeira, String nsu, int qtdParcelas) {
        // AJUSTADO: vendedor_codigo (conforme seu SQL) e status_venda como 'Pendente'
        String sqlVenda = "INSERT INTO vendas (cliente_id, vendedor_codigo, total_venda, status_venda, bandeira_cartao, nsu_comprovante) VALUES (?, ?, ?, 'Pendente', ?, ?)";
        String sqlItens = "INSERT INTO itens_venda (venda_id, produto_id, quantidade, preco_unitario, subtotal) VALUES (?, (SELECT id FROM produtos WHERE codigo_barras = ? LIMIT 1), ?, ?, ?)";

        try (Connection conn = new conexaoBD().getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement stVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                stVenda.setInt(1, v.getClienteId());
                stVenda.setInt(2, v.getVendedorCodigo());
                stVenda.setDouble(3, v.getTotalVenda());
                stVenda.setString(4, bandeira);
                stVenda.setString(5, nsu);
                stVenda.executeUpdate();

                ResultSet rs = stVenda.getGeneratedKeys();
                if (rs.next()) {
                    int vendaId = rs.getInt(1);
                    
                    try (PreparedStatement stItem = conn.prepareStatement(sqlItens)) {
                        for (ItemVenda item : itens) {
                            stItem.setInt(1, vendaId);
                            stItem.setString(2, item.getCodigoReferencia());
                            stItem.setInt(3, item.getQuantidade());
                            stItem.setDouble(4, item.getPrecoUnitario());
                            stItem.setDouble(5, item.getSubtotal());
                            stItem.addBatch();
                        }
                        stItem.executeBatch();
                    }
                    
                    // Se for boleto, você pode inserir na tabela pagamentos aqui ou no faturamento.
                    // Para o pedido aparecer no faturar, o insert acima já basta.
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MÉTODOS DE ESTORNO E LOG (Ajustados para vendedor_id conforme sua tabela log_estoque) ---

    public boolean processarEstorno(ItemVenda item, int user, String mot) {
        String sql = "UPDATE produtos SET quantidade_estoque = quantidade_estoque + ? WHERE codigo_barras = ?";
        try (Connection conn = new conexaoBD().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st = conn.prepareStatement(sql)) {
                st.setInt(1, item.getQuantidade());
                st.setString(2, item.getCodigoReferencia());
                st.executeUpdate();
                
                registrarLogEstoqueTransacional(conn, item.getCodigoReferencia(), user, "ENTRADA", item.getQuantidade(), "ESTORNO: " + mot);
                
                conn.commit();
                return true;
            } catch (SQLException e) { 
                conn.rollback(); 
                return false; 
            }
        } catch (SQLException e) { return false; }
    }

    private void registrarLogEstoqueTransacional(Connection conn, String ref, int user, String tipo, int qtd, String mot) throws SQLException {
        // Aqui sua tabela usa vendedor_id, então mantemos vendedor_id
        String sql = "INSERT INTO log_estoque (produto_id, vendedor_id, tipo_movimentacao, quantidade, data_hora, motivo) " +
                     "VALUES ((SELECT id FROM produtos WHERE codigo_barras = ? LIMIT 1), ?, ?, ?, NOW(), ?)";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, ref);
            st.setInt(2, user);
            st.setString(3, tipo);
            st.setInt(4, qtd);
            st.setString(5, mot);
            st.executeUpdate();
        }
    }

    public List<ItemVenda> listarItensParaCancelamento(int idCliente) {
        List<ItemVenda> lista = new ArrayList<>();
        String sql = "SELECT p.codigo_barras, p.descricao, iv.quantidade, iv.preco_unitario " +
                     "FROM itens_venda iv " +
                     "JOIN vendas v ON iv.venda_id = v.id " +
                     "JOIN produtos p ON iv.produto_id = p.id " +
                     "WHERE v.cliente_id = ? AND v.status_venda = 'Finalizada'";
        try (Connection conn = new conexaoBD().getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemVenda i = new ItemVenda(rs.getString("codigo_barras"), rs.getString("descricao"), rs.getInt("quantidade"), rs.getDouble("preco_unitario"));
                i.setSubtotal(rs.getInt("quantidade") * rs.getDouble("preco_unitario"));
                lista.add(i);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}