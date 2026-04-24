package dao;

import conexao.conexaoBD;
import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    
    public boolean existeCpfCnpj(String documento) {
        String sql = "SELECT COUNT(*) FROM clientes WHERE cpf_cnpj = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean salvar(Cliente c) {
        String sql = "INSERT INTO clientes (nome, tipo_pessoa, cpf_cnpj, email, telefone, endereco_completo, cep, status_conta) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTipoPessoa());
            stmt.setString(3, c.getCpfCnpj());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getTelefone());
            stmt.setString(6, c.getEnderecoCompleto());
            stmt.setString(7, c.getCep());
            stmt.setString(8, "Ativo"); 
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public void atualizar(Cliente c) {
        String sql = "UPDATE clientes SET nome=?, tipo_pessoa=?, cpf_cnpj=?, email=?, telefone=?, endereco_completo=?, cep=? WHERE id=?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getTipoPessoa());
            stmt.setString(3, c.getCpfCnpj());
            stmt.setString(4, c.getEmail());
            stmt.setString(5, c.getTelefone());
            stmt.setString(6, c.getEnderecoCompleto());
            stmt.setString(7, c.getCep());
            stmt.setInt(8, c.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nome ASC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Cliente> buscarPorNome(String termo) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nome LIKE ? OR cpf_cnpj LIKE ? ORDER BY nome ASC";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String busca = "%" + termo + "%";
            stmt.setString(1, busca);
            stmt.setString(2, busca);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // --- LÓGICA DE STATUS POR PENDÊNCIA (Sincronizada com FaturarController) ---

    public String verificarStatusPendencia(int idCliente) {
        // Regra: Pendente = Alguma venda com status 'Pendente' (ex: Boleto não pago)
        String sql = "SELECT COUNT(*) FROM vendas WHERE cliente_id = ? AND status_venda = 'Pendente'";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();
            
            String status = (rs.next() && rs.getInt(1) > 0) ? "Inativo" : "Ativo";
            atualizarStatusNoBanco(idCliente, status);
            return status;
        } catch (SQLException e) { e.printStackTrace(); return "Ativo"; }
    }

    private void atualizarStatusNoBanco(int id, String status) {
        String sql = "UPDATE clientes SET status_conta = ? WHERE id = ?";
        try (Connection conn = new conexaoBD().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setTipoPessoa(rs.getString("tipo_pessoa"));
        c.setCpfCnpj(rs.getString("cpf_cnpj"));
        c.setEmail(rs.getString("email"));
        c.setTelefone(rs.getString("telefone"));
        c.setEnderecoCompleto(rs.getString("endereco_completo"));
        c.setCep(rs.getString("cep"));
        // Garante que o campo status_conta seja lido corretamente para o DetalheClienteController
        c.setStatusConta(rs.getString("status_conta")); 
        return c;
    }
}