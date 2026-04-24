package controller;

import dao.ProdutoDAO;
import model.Produto;
import model.Sessao; // Assumindo que você tem uma classe para controlar o login
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class ProdutosController {

    @FXML private TextField txtDescricao, txtCodigoBarras, txtCategoria, txtPrecoCusto, txtMargem, txtPrecoVenda, txtEstoque, txtMinimo;
    
    // --- COMPONENTES DA TABELA ---
    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colDescricao;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;

    private ProdutoDAO dao = new ProdutoDAO();

    @FXML
    public void initialize() {
        // Configura colunas da tabela se houver
        if (tabelaProdutos != null) {
            configurarColunas();
            atualizarTabela();

            // 1. Detectar Clique Duplo
            tabelaProdutos.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && tabelaProdutos.getSelectionModel().getSelectedItem() != null) {
                    verificarPermissaoEAbrir();
                }
            });

            // 2. Detectar Tecla ENTER
            tabelaProdutos.setOnKeyPressed((KeyEvent event) -> {
                if (event.getCode() == KeyCode.ENTER && tabelaProdutos.getSelectionModel().getSelectedItem() != null) {
                    verificarPermissaoEAbrir();
                    event.consume();
                }
            });
        }
    }

    private void verificarPermissaoEAbrir() {
        System.out.println(">>> EVENTO DE CLIQUE DETECTADO! <<<");
        
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            System.out.println(">>> Produto Selecionado: " + selecionado.getDescricao() + " <<<");
            abrirDetalhesProduto();
        } else {
            System.out.println(">>> NENHUM PRODUTO SELECIONADO NA TABELA <<<");
        }
    }

    private void abrirDetalhesProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        
        try {
            // Log para saber se o Java achou o arquivo
            java.net.URL fxmlLocation = getClass().getResource("/view/DetalhesProduto.fxml");
            if (fxmlLocation == null) {
                System.err.println("ERRO: O arquivo DetalhesProduto.fxml não foi encontrado na pasta /view/");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load(); // Se falhar aqui, o catch vai pegar o motivo real

            DetalheProdutoController controller = loader.getController();
            controller.carregarDados(selecionado);

            Stage stage = new Stage();
            stage.setTitle("HyperTech - Editando: " + selecionado.getDescricao());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            atualizarTabela(); 
            
        } catch (Exception e) {
            System.err.println("------ ERRO DE CARREGAMENTO JAVAFX ------");
            System.err.println("Mensagem: " + e.getMessage());
            System.err.println("Causa: " + e.getCause());
            e.printStackTrace(); // Isso vai mostrar a linha exata do erro
            System.err.println("-----------------------------------------");
        }
    }
    private void configurarColunas() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colPreco != null) colPreco.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        if (colEstoque != null) colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
    }

    public void atualizarTabela() {
        if (tabelaProdutos != null) {
            tabelaProdutos.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        }
    }

    @FXML
    public void calcularPrecoVenda() {
        try {
            if (!txtPrecoCusto.getText().isEmpty() && !txtMargem.getText().isEmpty()) {
                double custo = Double.parseDouble(txtPrecoCusto.getText().replace(",", "."));
                double margem = Double.parseDouble(txtMargem.getText().replace(",", "."));
                double venda = custo + (custo * (margem / 100));
                txtPrecoVenda.setText(String.format("%.2f", venda).replace(",", "."));
            }
        } catch (NumberFormatException e) { }
    }

    @FXML
    public void salvarProduto() {
        try {
            Produto p = new Produto();
            p.setDescricao(txtDescricao.getText());
            p.setCodigoBarras(txtCodigoBarras.getText());
            p.setCategoria(txtCategoria.getText());
            p.setPrecoCusto(Double.parseDouble(txtPrecoCusto.getText().replace(",", ".")));
            p.setMargemLucro(Double.parseDouble(txtMargem.getText().replace(",", ".")));
            p.setPrecoVenda(Double.parseDouble(txtPrecoVenda.getText().replace(",", ".")));
            p.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));
            p.setEstoqueMinimo(Integer.parseInt(txtMinimo.getText()));

            dao.salvar(p);
            exibirMensagem("Sucesso", "Produto cadastrado!", AlertType.INFORMATION);
            limparCampos();
            atualizarTabela();
        } catch (Exception e) {
            exibirMensagem("Erro", "Verifique os campos numéricos.", AlertType.ERROR);
        }
    }

    private void limparCampos() {
        txtDescricao.clear(); txtCodigoBarras.clear(); txtCategoria.clear();
        txtPrecoCusto.clear(); txtMargem.clear(); txtPrecoVenda.clear();
        txtEstoque.clear(); txtMinimo.clear();
    }

    private void exibirMensagem(String titulo, String msg, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}