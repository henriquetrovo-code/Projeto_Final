package controller;

import dao.VendaDAO;
import model.ItemVenda;
import model.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

public class CancelarVendaController {

    @FXML private TextField txtBuscaCliente;
    @FXML private TextField txtCodVendedor;
    @FXML private TextArea txtMotivoCancelamento;
    @FXML private TableView<ItemVenda> tabelaVendasCancelamento;
    @FXML private TableColumn<ItemVenda, String> colIdVenda, colProduto; 
    @FXML private TableColumn<ItemVenda, Double> colTotal;

    // 1. Verifique se essa linha está exatamente assim
    private VendaDAO vendaDao = new VendaDAO();

    @FXML
    public void initialize() {
        configurarTabela();
    }

    private void configurarTabela() {
        colIdVenda.setCellValueFactory(new PropertyValueFactory<>("codigoReferencia"));
        colProduto.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    @FXML 
    public void buscarComprasDoCliente() { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuscaCliente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (Sessao.getClienteSelecionado() != null) {
                txtBuscaCliente.setText(Sessao.getClienteSelecionado().getNome());
                int idCliente = Sessao.getClienteSelecionado().getId();
                
                // 2. Se aqui estiver vermelho, você PRECISA salvar o arquivo VendaDAO.java
                List<ItemVenda> lista = vendaDao.listarItensParaCancelamento(idCliente);
                tabelaVendasCancelamento.setItems(FXCollections.observableArrayList(lista));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void confirmarCancelamento() {
        ItemVenda selecionado = tabelaVendasCancelamento.getSelectionModel().getSelectedItem();

        if (selecionado == null || txtCodVendedor.getText().isEmpty() || txtMotivoCancelamento.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha tudo!").show();
            return;
        }

        Alert confirma = new Alert(Alert.AlertType.CONFIRMATION, "Deseja estornar o item?");
        Optional<ButtonType> result = confirma.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 3. Se aqui estiver vermelho, o método no DAO tem que ter esses 3 parâmetros
            boolean sucesso = vendaDao.processarEstorno(
                selecionado, 
                Integer.parseInt(txtCodVendedor.getText()), 
                txtMotivoCancelamento.getText()
            );

            if (sucesso) {
                new Alert(Alert.AlertType.INFORMATION, "Estorno OK!").show();
                limparTela();
            }
        }
    }

    private void limparTela() {
        txtBuscaCliente.clear();
        txtCodVendedor.clear();
        txtMotivoCancelamento.clear();
        tabelaVendasCancelamento.getItems().clear();
        Sessao.setClienteSelecionado(null);
    }
}