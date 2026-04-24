package controller;

import dao.ProdutoDAO;
import dao.VendaDAO;
import model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import java.util.List;

public class EstoqueController {

    @FXML private ComboBox<String> cbCategoriaFiltro;
    @FXML private TableView<Produto> tabelaEstoque;
    @FXML private Label lblTotalItens, lblQtdTotal;
    @FXML private TableColumn<Produto, Integer> colId, colQuantidade;
    @FXML private TableColumn<Produto, String> colNome, colCategoria;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TextField txtQtdAjuste, txtCodUsuario;
    @FXML private TextArea txtMotivoAjuste;

    private ProdutoDAO dao = new ProdutoDAO();
    private VendaDAO vdao = new VendaDAO(); 

    @FXML
    public void initialize() {
        configurarColunas();
        carregarFiltroCategorias();
        atualizarTabela(dao.listarTodos()); 
    }

    @FXML public void handleEntrada() { processarMovimentacao("ENTRADA"); }
    @FXML public void handleSaida() { processarMovimentacao("SAIDA"); }

    private void processarMovimentacao(String tipo) {
        Produto selecionado = tabelaEstoque.getSelectionModel().getSelectedItem();
        if (selecionado == null || txtQtdAjuste.getText().isEmpty()) return;

        try {
            int qtd = Integer.parseInt(txtQtdAjuste.getText());
            int codUser = Integer.parseInt(txtCodUsuario.getText());
            String motivo = txtMotivoAjuste.getText();

            // Como seu registrarLogEstoqueTransacional é PRIVADO, use o processarEstorno 
            // ou peça para criar um método público de Log. 
            // Para não quebrar, vamos atualizar a quantidade via ProdutoDAO:
            int novaQtd = tipo.equals("ENTRADA") ? selecionado.getQuantidadeEstoque() + qtd : selecionado.getQuantidadeEstoque() - qtd;
            dao.atualizarQuantidadeEstoque(selecionado.getId(), novaQtd);

            txtQtdAjuste.clear();
            txtMotivoAjuste.clear();
            atualizarTabela(dao.listarTodos());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("descricao")); 
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("precoVenda")); 
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque")); 
    }

    private void carregarFiltroCategorias() {
        cbCategoriaFiltro.setItems(FXCollections.observableArrayList("Todas", "Placas Mãe", "Processadores", "Memórias RAM", "Fontes", "Placas de Vídeo", "Periféricos"));
        cbCategoriaFiltro.getSelectionModel().selectFirst();
    }

    private void atualizarTabela(List<Produto> lista) {
        tabelaEstoque.setItems(FXCollections.observableArrayList(lista));
    }
}