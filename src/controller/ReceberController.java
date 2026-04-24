package controller;

import dao.RelatorioDAO;
import dao.ClienteDAO;
import model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class ReceberController {

    @FXML private Label lblClienteNome, lblTotalPagar;
    @FXML private TableView<Parcela> tabelaPendencias; // Alterado para Parcela
    @FXML private TableColumn<Parcela, Integer> colId, colParcela;
    @FXML private TableColumn<Parcela, String> colVencimento, colStatus;
    @FXML private TableColumn<Parcela, Double> colTotal;

    private Cliente clienteSelecionado;
    private RelatorioDAO rdao = new RelatorioDAO();
    private ClienteDAO cdao = new ClienteDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colParcela.setCellValueFactory(new PropertyValueFactory<>("numeroParcela"));
        colVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabelaPendencias.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                lblTotalPagar.setText(String.format("Total Parcela: R$ %.2f", newV.getValor()));
            }
        });
    }

    @FXML
    public void selecionarCliente() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/BuscaCliente.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (Sessao.getClienteSelecionado() != null) {
                this.clienteSelecionado = Sessao.getClienteSelecionado();
                lblClienteNome.setText("Cliente: " + clienteSelecionado.getNome());
                carregarBoletos();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void carregarBoletos() {
        List<Parcela> boletos = rdao.listarBoletosPorCliente(clienteSelecionado.getId());
        tabelaPendencias.setItems(FXCollections.observableArrayList(boletos));
    }

    @FXML
    public void confirmarPagamento() {
        Parcela selecionada = tabelaPendencias.getSelectionModel().getSelectedItem();
        
        if (selecionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um boleto/parcela!").show();
            return;
        }

        // 1. Dá baixa na parcela específica
        if (rdao.baixarParcela(selecionada.getId())) {
            // 2. Verifica se o cliente ainda tem boletos pendentes. Se não tiver, ele volta a ser ATIVO.
            cdao.verificarStatusPendencia(clienteSelecionado.getId());
            
            new Alert(Alert.AlertType.INFORMATION, "Parcela " + selecionada.getNumeroParcela() + " recebida!").show();
            carregarBoletos();
        }
    }

    @FXML public void cancelar() {
        ((javafx.stage.Stage) lblClienteNome.getScene().getWindow()).close();
    }
}