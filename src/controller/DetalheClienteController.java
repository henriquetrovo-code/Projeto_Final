package controller;

import dao.VendaDAO;
import dao.ClienteDAO;
import model.Cliente;
import model.Venda;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
	
public class DetalheClienteController {

    @FXML private TextField lblId, lblNome, lblCpf, lblTelefone, lblEndereco;
    @FXML private Label lblStatus; 
    @FXML private TableView<Venda> tabelaHistorico;
    @FXML private TableColumn<Venda, String> colData, colVendedor, colStatusVenda, colProdutos;
    @FXML private TableColumn<Venda, Double> colTotal;

    public void carregarDados(Cliente c) {
        lblId.setText(String.valueOf(c.getId()));
        lblNome.setText(c.getNome());
        lblCpf.setText(c.getCpfCnpj()); 
        lblTelefone.setText(c.getTelefone());
        lblEndereco.setText(c.getEnderecoCompleto());

        ClienteDAO cdao = new ClienteDAO();
        String statusReal = cdao.verificarStatusPendencia(c.getId());
        
        atualizarVisualStatus(statusReal);
        configurarTabela(c.getId());
    }

    private void atualizarVisualStatus(String status) {
        if (status != null && status.equalsIgnoreCase("Inativo")) {
            lblStatus.setText("INATIVO - POSSUI PENDÊNCIAS NO SISTEMA");
            lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-background-color: #fdeaea; -fx-padding: 5;");
        } else {
            lblStatus.setText("ATIVO - CONTA REGULAR");
            lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-background-color: #eafdea; -fx-padding: 5;");
        }
    }

    private void configurarTabela(int clienteId) {
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenda"));
        colStatusVenda.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Nomes sincronizados com listarVendasPorCliente no VendaDAO antigo
        colVendedor.setCellValueFactory(new PropertyValueFactory<>("nomeVendedor"));
        colProdutos.setCellValueFactory(new PropertyValueFactory<>("resumoItens"));

        VendaDAO vdao = new VendaDAO();
        // Nota: Certifique-se que o listarVendasPorCliente ainda existe no seu VendaDAO completo
        // Caso tenha deletado sem querer, ele deve ser adicionado novamente.
        // tabelaHistorico.setItems(FXCollections.observableArrayList(vdao.listarVendasPorCliente(clienteId)));
    }

    @FXML
    public void fecharJanela() {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }
}