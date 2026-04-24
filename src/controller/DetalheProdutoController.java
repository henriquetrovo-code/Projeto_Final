package controller;

import dao.ProdutoDAO;
import model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DetalheProdutoController {

    // Removi o txtReferencia daqui porque ele não existe no seu FXML
    @FXML private TextField txtId, txtDescricao, txtCodigoBarras, txtCategoria;
    @FXML private TextField txtPrecoCusto, txtMargem, txtPrecoVenda, txtEstoque, txtMinimo;

    private ProdutoDAO dao = new ProdutoDAO();
    private Produto produtoAtual;

    public void carregarDados(Produto p) {
        this.produtoAtual = p;
        
        // Preenchimento dos campos que existem no seu FXML
        if (txtId != null) txtId.setText(String.valueOf(p.getId()));
        if (txtDescricao != null) txtDescricao.setText(p.getDescricao());
        if (txtCodigoBarras != null) txtCodigoBarras.setText(p.getCodigoBarras());
        if (txtCategoria != null) txtCategoria.setText(p.getCategoria());
        if (txtPrecoCusto != null) txtPrecoCusto.setText(String.valueOf(p.getPrecoCusto()));
        if (txtMargem != null) txtMargem.setText(String.valueOf(p.getMargemLucro()));
        if (txtPrecoVenda != null) txtPrecoVenda.setText(String.valueOf(p.getPrecoVenda()));
        if (txtEstoque != null) txtEstoque.setText(String.valueOf(p.getQuantidadeEstoque()));
        if (txtMinimo != null) txtMinimo.setText(String.valueOf(p.getEstoqueMinimo()));
        
        // Nota: Removi o txtReferencia.setText() para evitar o NullPointerException
    }

    @FXML
    public void recalcularPreco() {
        try {
            double custo = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
            double margem = Double.parseDouble(txtMargem.getText().replace(",", "."));
            double venda = custo + (custo * (margem / 100));
            txtPrecoVenda.setText(String.format("%.2f", venda).replace(",", "."));
        } catch (Exception e) { 
            // Silencia erro se os campos estiverem vazios durante a digitação
        }
    }

    @FXML
    public void salvarAlteracoes() {
        try {
            produtoAtual.setDescricao(txtDescricao.getText());
            produtoAtual.setCodigoBarras(txtCodigoBarras.getText());
            produtoAtual.setCategoria(txtCategoria.getText());
            produtoAtual.setPrecoCusto(Double.parseDouble(txtPrecoCusto.getText().replace(",", ".")));
            produtoAtual.setMargemLucro(Double.parseDouble(txtMargem.getText().replace(",", ".")));
            produtoAtual.setPrecoVenda(Double.parseDouble(txtPrecoVenda.getText().replace(",", ".")));
            produtoAtual.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));
            produtoAtual.setEstoqueMinimo(Integer.parseInt(txtMinimo.getText()));

            dao.atualizar(produtoAtual);
            fecharJanela(); // Chama o método de fechar após salvar
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    @FXML 
    public void fecharJanela() {
        // Pega a janela atual através de qualquer componente e fecha
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.close();
    }
}