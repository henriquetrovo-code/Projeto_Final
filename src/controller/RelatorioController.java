package controller;

import dao.RelatorioDAO;
import model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import conexao.conexaoBD;

public class RelatorioController {

    // TABELAS
    @FXML private TableView<Venda> tabelaVendas;
    @FXML private TableColumn<Venda, Integer> colId;
    @FXML private TableColumn<Venda, String> colData, colVendedor;
    @FXML private TableColumn<Venda, Double> colTotal;
    @FXML private TableView<ItemVenda> tabelaItens; 
    @FXML private TableColumn<ItemVenda, String> colRef, colDesc;
    @FXML private TableColumn<ItemVenda, Integer> colQtd;
    @FXML private TableColumn<ItemVenda, Double> colSub;

    // FINANCEIRO
    @FXML private Label lblSaldoInicial, lblVendasDinheiro, lblCartaoDebito, lblCartaoCredito, lblBandeiras, lblSugestaoRetirada;
    @FXML private TextArea areaLogs;
    @FXML private Button btnVerComprovante;

    private RelatorioDAO relatorioDao = new RelatorioDAO();

    @FXML
    public void initialize() {
        configurarTabelas();
        carregarLogsAoVivo();
        atualizarResumoFinanceiro();
        
        Usuario logado = Sessao.getUsuario();
        if (logado != null && logado.getPerfil().equalsIgnoreCase("Vendedor")) {
            if (btnVerComprovante != null) btnVerComprovante.setVisible(false);
        }
    }

    private void configurarTabelas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));
        colVendedor.setCellValueFactory(new PropertyValueFactory<>("nomeVendedor"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalVenda"));

        colRef.setCellValueFactory(new PropertyValueFactory<>("codigoReferencia"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSub.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tabelaVendas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) carregarItensDaVenda(newV.getId());
        });
    }

    private void carregarItensDaVenda(int idVenda) {
        List<ItemVenda> itens = relatorioDao.buscarItensDaVenda(idVenda);
        tabelaItens.setItems(FXCollections.observableArrayList(itens));
    }

    @FXML
    public void atualizarResumoFinanceiro() {
        Map<String, Double> totais = relatorioDao.buscarTotaisFinanceiros();
        double abertura = totais.getOrDefault("abertura", 0.0);
        double dinheiro = totais.getOrDefault("dinheiro", 0.0);
        
        lblSaldoInicial.setText(String.format("Saldo Inicial (Cobertura): R$ %.2f", abertura));
        lblVendasDinheiro.setText(String.format("Vendas em Dinheiro: R$ %.2f", dinheiro));
        lblCartaoDebito.setText(String.format("Cartão Débito: R$ %.2f", totais.getOrDefault("debito", 0.0)));
        lblCartaoCredito.setText(String.format("Cartão Crédito: R$ %.2f", totais.getOrDefault("credito", 0.0)));
        lblBandeiras.setText("Bandeiras: " + relatorioDao.getResumoBandeiras());

        // Sugestão de Retirada = O que tem que ter fisicamente (Abertura + Vendas Dinheiro)
        lblSugestaoRetirada.setText(String.format("Sugestão de Retirada Final: R$ %.2f", (abertura + dinheiro)));
    }

    @FXML
    public void carregarLogsAoVivo() {
        String sql = "SELECT data_hora, tipo_movimentacao, motivo FROM log_estoque ORDER BY data_hora DESC LIMIT 100";
        StringBuilder sb = new StringBuilder();
        try (Connection conn = new conexaoBD().getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                sb.append("[").append(rs.getTimestamp("data_hora")).append("] ")
                  .append(rs.getString("tipo_movimentacao")).append(" - ").append(rs.getString("motivo")).append("\n");
            }
            areaLogs.setText(sb.toString());
            areaLogs.setScrollTop(Double.MAX_VALUE);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    public void gerarRelatorioDetalhado() {
        // 1. Pega os dados financeiros que já calculamos
        String financeiro = "--- RESUMO FINANCEIRO ---\n" +
                            lblSaldoInicial.getText() + "\n" +
                            lblVendasDinheiro.getText() + "\n" +
                            lblCartaoDebito.getText() + " | " + lblCartaoCredito.getText() + "\n" +
                            lblBandeiras.getText() + "\n" +
                            "--------------------------\n" +
                            lblSugestaoRetirada.getText() + "\n\n";

        // 2. Pega a listagem de pedidos do dia
        String pedidos = relatorioDao.gerarResumoVendasDoDia();

        // 3. Exibe em um Alert grande com área de texto (para poder copiar)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Relatório Detalhado HyperTech");
        alert.setHeaderText("Fechamento Operacional e Financeiro - " + java.time.LocalDate.now());

        TextArea textArea = new TextArea(financeiro + pedidos);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(400);
        textArea.setPrefWidth(600);

        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.showAndWait();
    }

    @FXML
    public void buscarExtrato() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuscaCliente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (Sessao.getClienteSelecionado() != null) {
                Cliente c = Sessao.getClienteSelecionado();
                List<Venda> vendas = relatorioDao.buscarExtratoPorCliente(c.getId());
                tabelaVendas.setItems(FXCollections.observableArrayList(vendas));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void verComprovante() {
        Venda v = tabelaVendas.getSelectionModel().getSelectedItem();
        if (v != null) {
            new Alert(Alert.AlertType.INFORMATION, "Venda #" + v.getId() + "\nNSU: " + v.getNsu() + "\nBandeira: " + v.getBandeira()).show();
        }
    }
}