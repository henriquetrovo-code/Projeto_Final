package controller;

import dao.ProdutoDAO;
import model.Produto;
import model.Sessao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.util.List;

public class BuscaProdutoController {

    @FXML private TextField txtBuscaProduto;
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colDescricao;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;

    private ProdutoDAO dao = new ProdutoDAO();

    @FXML
    public void initialize() {
        // Mapeia colunas com os atributos de model.Produto
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));

        carregarTodos();

        // Duplo clique para selecionar
        tabelaProdutos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) confirmarSelecao();
        });
    }

    @FXML
    public void pesquisar() {
        String busca = txtBuscaProduto.getText();
        // Usa o método potente que criamos no DAO (Busca por Nome, Barras ou ID)
        List<Produto> lista = (busca.isEmpty()) ? dao.listarTodos() : dao.buscarMultiplo(busca);
        tabelaProdutos.setItems(FXCollections.observableArrayList(lista));
        
        if (!lista.isEmpty()) {
            tabelaProdutos.requestFocus();
            tabelaProdutos.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void verificarEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == tabelaProdutos) {
                confirmarSelecao();
            } else if (event.getSource() == txtBuscaProduto) {
                pesquisar();
            }
        }
    }

    @FXML
    public void confirmarSelecao() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Sessao.setProdutoSelecionado(selecionado);
            Stage stage = (Stage) tabelaProdutos.getScene().getWindow();
            stage.close();
        }
    }

    private void carregarTodos() {
        tabelaProdutos.setItems(FXCollections.observableArrayList(dao.listarTodos()));
    }
}