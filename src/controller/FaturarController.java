package controller;

import dao.*;
import model.Venda;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import java.util.Optional;

public class FaturarController {

    @FXML private TableView<Venda> tabelaPendentes;
    @FXML private TableColumn<Venda, Integer> colId;
    @FXML private TableColumn<Venda, String> colCliente, colData;
    @FXML private TableColumn<Venda, Double> colTotal;

    private VendaDAO daoVenda = new VendaDAO();
    private ClienteDAO daoCliente = new ClienteDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nomeCliente"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenda"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        // Certifique-se que o listarPedidosPendentes ainda está no VendaDAO.java
        // tabelaPendentes.setItems(FXCollections.observableArrayList(daoVenda.listarPedidosPendentes()));
    }

    @FXML
    public void confirmarFaturamento() {
        Venda selecionada = tabelaPendentes.getSelectionModel().getSelectedItem();
        if (selecionada == null) return;

        Alert resumo = new Alert(Alert.AlertType.CONFIRMATION, "Confirmar recebimento de R$ " + String.format("%.2f", selecionada.getTotalVenda()) + "?");
        Optional<ButtonType> result = resumo.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Nota: faturarPedido precisa existir no seu VendaDAO
            // daoVenda.faturarPedido(selecionada.getId());
            daoCliente.verificarStatusPendencia(selecionada.getClienteId());
            atualizarTabela();
        }
    }
}