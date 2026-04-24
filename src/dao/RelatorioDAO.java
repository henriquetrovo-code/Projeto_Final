package dao;

import conexao.conexaoBD;
import model.Venda;
import model.ItemVenda;
import model.Parcela;

import java.sql.*;
import java.util.*;

public class RelatorioDAO {

    // 1. RESUMO FINANCEIRO (Para a aba de Resumo)
    public Map<String, Double> buscarTotaisFinanceiros() {
        Map<String, Double> totais = new HashMap<>();
        // Query adaptada para as suas colunas: vendedor_codigo e bandeira_cartao
        String sql = "SELECT " +
            "(SELECT COALESCE(SUM(valor),0) FROM controle_caixa WHERE tipo='ABERTURA' AND DATE(data_operacao) = CURDATE()) as abertura, " +
            "(SELECT COALESCE(SUM(total_venda),0) FROM vendas WHERE bandeira_cartao='N/A' AND status_venda='Finalizada' AND DATE(data_venda) = CURDATE()) as dinheiro, " +
            "(SELECT COALESCE(SUM(total_venda),0) FROM vendas WHERE status_venda='Finalizada' AND (bandeira_cartao LIKE '%DEBITO%' OR bandeira_cartao LIKE '%DÉBITO%') AND DATE(data_venda) = CURDATE()) as debito, " +
            "(SELECT COALESCE(SUM(total_venda),0) FROM vendas WHERE status_venda='Finalizada' AND (bandeira_cartao LIKE '%CREDITO%' OR bandeira_cartao LIKE '%CRÉDITO%') AND DATE(data_venda) = CURDATE()) as credito";

        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totais.put("abertura", rs.getDouble("abertura"));
                totais.put("dinheiro", rs.getDouble("dinheiro"));
                totais.put("debito", rs.getDouble("debito"));
                totais.put("credito", rs.getDouble("credito"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return totais;
    }

    // 2. DETALHAMENTO DE BANDEIRAS
    public String getResumoBandeiras() {
        String sql = "SELECT bandeira_cartao, SUM(total_venda) as total FROM vendas " +
                     "WHERE bandeira_cartao != 'N/A' AND status_venda='Finalizada' AND DATE(data_venda) = CURDATE() " +
                     "GROUP BY bandeira_cartao";
        StringBuilder sb = new StringBuilder();
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sb.append(rs.getString("bandeira_cartao")).append(": R$ ").append(String.format("%.2f", rs.getDouble("total"))).append(" | ");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return sb.length() > 0 ? sb.toString() : "Sem vendas em cartão hoje.";
    }

    // 3. EXTRATO DE CLIENTE (Vendas Finalizadas)
    public List<Venda> buscarExtratoPorCliente(int idCliente) {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT v.*, u.nome as vendedor_nome FROM vendas v " +
                     "JOIN usuarios u ON v.vendedor_codigo = u.id " +
                     "WHERE v.cliente_id = ? AND v.status_venda = 'Finalizada' " +
                     "ORDER BY v.data_venda DESC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id"));
                v.setDataVenda(rs.getTimestamp("data_venda"));
                v.setTotalVenda(rs.getDouble("total_venda"));
                v.setBandeira(rs.getString("bandeira_cartao"));
                v.setNsu(rs.getString("nsu_comprovante"));
                v.setNomeVendedor(rs.getString("vendedor_nome"));
                lista.add(v);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // 4. ITENS DETALHADOS DA VENDA
    public List<ItemVenda> buscarItensDaVenda(int idVenda) {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT p.codigo_barras, p.descricao, iv.quantidade, iv.preco_unitario " +
                     "FROM itens_venda iv JOIN produtos p ON iv.produto_id = p.id WHERE iv.venda_id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idVenda);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                itens.add(new ItemVenda(rs.getString("codigo_barras"), rs.getString("descricao"), rs.getInt("quantidade"), rs.getDouble("preco_unitario")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return itens;
    }

    // 5. MOVIMENTAÇÃO DE CAIXA (Sincronizado com MainController)
    public void registrarMovimentacaoCaixa(String tipo, double valor, int vendedorId, String obs) {
        String sql = "INSERT INTO controle_caixa (tipo, valor, vendedor_id, observacao) VALUES (?, ?, ?, ?)";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            stmt.setDouble(2, valor);
            stmt.setInt(3, vendedorId);
            stmt.setString(4, obs);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public String gerarResumoVendasDoDia() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT v.id, c.nome as cliente, u.nome as vendedor, v.total_venda, v.bandeira_cartao " +
                     "FROM vendas v " +
                     "LEFT JOIN clientes c ON v.cliente_id = c.id " +
                     "LEFT JOIN usuarios u ON v.vendedor_codigo = u.id " +
                     "WHERE v.status_venda = 'Finalizada' AND DATE(v.data_venda) = CURDATE() " +
                     "ORDER BY v.id ASC";

        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            sb.append("=== LISTAGEM DE PEDIDOS FINALIZADOS (HOJE) ===\n\n");
            while (rs.next()) {
                sb.append(String.format("PEDIDO #%04d | CLIENTE: %-15s | PGTO: %-10s | TOTAL: R$ %.2f\n", 
                    rs.getInt("id"), 
                    rs.getString("cliente"), 
                    rs.getString("bandeira_cartao"), 
                    rs.getDouble("total_venda")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        return sb.toString();
    }
    
    public List<Parcela> listarBoletosPorCliente(int idCliente) {
        List<Parcela> lista = new ArrayList<>();
        String sql = "SELECT p.* FROM pagamentos p " +
                     "JOIN vendas v ON p.venda_id = v.id " +
                     "WHERE v.cliente_id = ? AND p.forma_pagamento = 'Boleto' AND p.status_pagamento = 'Pendente' " +
                     "ORDER BY p.data_vencimento ASC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Parcela(
                    rs.getInt("id"),
                    rs.getInt("numero_parcela"),
                    rs.getDouble("valor"),
                    rs.getDate("data_vencimento"),
                    rs.getString("status_pagamento")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean baixarParcela(int idParcela) {
        String sql = "UPDATE pagamentos SET status_pagamento = 'Pago' WHERE id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParcela);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}