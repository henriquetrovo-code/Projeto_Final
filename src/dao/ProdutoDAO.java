package dao;

import conexao.conexaoBD;
import model.Produto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    
    // --- NOVO MÉTODO: ATUALIZAR APENAS QUANTIDADE (Usado pelo Log de Estoque) ---
    public void atualizarQuantidadeEstoque(int id, int novaQtd) {
        String sql = "UPDATE produtos SET quantidade_estoque = ? WHERE id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, novaQtd);
            stmt.setInt(2, id);
            
            stmt.executeUpdate();
            System.out.println("HyperTech - Quantidade atualizada no banco.");
        } catch (SQLException e) { 
            System.err.println("Erro ao atualizar estoque: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    // 1. SALVAR (Cadastro de novos produtos)
    public void salvar(Produto p) {
        String sql = "INSERT INTO produtos (descricao, referencia, codigo_barras, categoria, preco_custo, margem_lucro, preco_venda, quantidade_estoque, estoque_minimo) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getDescricao());
            stmt.setString(2, p.getReferencia());
            stmt.setString(3, p.getCodigoBarras());
            stmt.setString(4, p.getCategoria());
            stmt.setDouble(5, p.getPrecoCusto());
            stmt.setDouble(6, p.getMargemLucro());
            stmt.setDouble(7, p.getPrecoVenda());
            stmt.setInt(8, p.getQuantidadeEstoque());
            stmt.setInt(9, p.getEstoqueMinimo());
            stmt.execute();
            System.out.println("HyperTech - Produto salvo.");
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
    }

    // 2. ATUALIZAR (Necessário para a tela de edição)
    public void atualizar(Produto p) {
        String sql = "UPDATE produtos SET descricao=?, referencia=?, codigo_barras=?, categoria=?, preco_custo=?, margem_lucro=?, preco_venda=?, quantidade_estoque=?, estoque_minimo=? WHERE id=?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getDescricao());
            stmt.setString(2, p.getReferencia());
            stmt.setString(3, p.getCodigoBarras());
            stmt.setString(4, p.getCategoria());
            stmt.setDouble(5, p.getPrecoCusto());
            stmt.setDouble(6, p.getMargemLucro());
            stmt.setDouble(7, p.getPrecoVenda());
            stmt.setInt(8, p.getQuantidadeEstoque());
            stmt.setInt(9, p.getEstoqueMinimo());
            stmt.setInt(10, p.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 3. BUSCAR MÚLTIPLO (Fundamental para o PDV: ID, Barras ou Nome)
    public List<Produto> buscarMultiplo(String termo) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE descricao LIKE ? OR codigo_barras = ? OR id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String buscaLike = "%" + termo + "%";
            stmt.setString(1, buscaLike);
            stmt.setString(2, termo);
            
            int idBusca;
            try {
                idBusca = Integer.parseInt(termo);
            } catch (NumberFormatException e) {
                idBusca = -1;
            }
            stmt.setInt(3, idBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(preencherProduto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 4. LISTAR TODOS
    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY descricao ASC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(preencherProduto(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 5. BUSCAR POR CATEGORIA
    public List<Produto> buscarPorCategoria(String categoria) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE categoria = ? ORDER BY descricao ASC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(preencherProduto(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // MÉTODO AUXILIAR
    private Produto preencherProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setDescricao(rs.getString("descricao"));
        p.setReferencia(rs.getString("referencia"));
        p.setCodigoBarras(rs.getString("codigo_barras"));
        p.setCategoria(rs.getString("categoria"));
        p.setPrecoCusto(rs.getDouble("preco_custo"));
        p.setMargemLucro(rs.getDouble("margem_lucro"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        p.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        return p;
    }
}