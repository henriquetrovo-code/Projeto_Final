package controller;

import model.*;
import dao.VendaDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class PedidoController {
    
    @FXML private TextField txtNomeCliente, txtNomeProduto, txtQtdProduto, txtDesconto, txtCodVendedor, txtDescontoItem;
    @FXML private Label lblTotalPedido;
    @FXML private TableView<ItemVenda> tabelaCarrinho;
    @FXML private TableColumn<ItemVenda, String> colCodigo, colDescricao;
    @FXML private TableColumn<ItemVenda, Integer> colQtd;
    @FXML private TableColumn<ItemVenda, Double> colPrecoUnitario, colSubtotal;
    
    @FXML private ComboBox<String> cbPagamento1, cbParcelas1;

    private ObservableList<ItemVenda> itens = FXCollections.observableArrayList();
    private String codigoBarrasAtual = ""; 
    private double precoBaseAtual = 0.0;   

    @FXML
    public void initialize() {
        // 1. Configura as Colunas da Tabela
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoReferencia"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPrecoUnitario.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tabelaCarrinho.setItems(itens);

        // 2. Popula Formas de Pagamento
        cbPagamento1.setItems(FXCollections.observableArrayList("Dinheiro", "Cartão Débito", "Cartão Crédito", "Boleto", "Pix"));

        // 3. Listener para mostrar parcelas e calcular valores (Boleto/Cartão)
        cbPagamento1.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                boolean deveMostrar = novo.equalsIgnoreCase("Boleto") || novo.equalsIgnoreCase("Cartão Crédito");
                cbParcelas1.setVisible(deveMostrar);
                if (deveMostrar) {
                    atualizarListaParcelas();
                }
            }
        });

        // 4. Navegação por Enter entre os campos
        txtQtdProduto.setOnAction(event -> txtDescontoItem.requestFocus());
        txtDescontoItem.setOnAction(event -> adicionarItem()); 
        txtCodVendedor.setOnAction(event -> txtDesconto.requestFocus());
        txtDesconto.setOnAction(event -> finalizarVenda());
    }

    private void atualizarListaParcelas() {
        double totalGeral = calcularTotalFinal();
        ObservableList<String> listaP = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            double valorParcela = totalGeral / i;
            listaP.add(String.format("%dx de R$ %.2f", i, valorParcela));
        }
        cbParcelas1.setItems(listaP);
        if (!listaP.isEmpty()) cbParcelas1.getSelectionModel().selectFirst();
    }

    @FXML
    public void abrirBuscaCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuscaCliente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (Sessao.getClienteSelecionado() != null) {
                txtNomeCliente.setText(Sessao.getClienteSelecionado().getNome());
                txtNomeProduto.requestFocus();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void abrirBuscaProduto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuscaProduto.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (Sessao.getProdutoSelecionado() != null) {
                Produto p = Sessao.getProdutoSelecionado();
                txtNomeProduto.setText(p.getDescricao());
                this.codigoBarrasAtual = p.getCodigoBarras(); 
                this.precoBaseAtual = p.getPrecoVenda();      
                Sessao.setProdutoSelecionado(null);
                txtQtdProduto.requestFocus();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void adicionarItem() {
        try {
            if(txtNomeProduto.getText().isEmpty()) {
                abrirBuscaProduto();
                return;
            }

            double porcDesc = Double.parseDouble(txtDescontoItem.getText().replace(",", "."));

            // Regra do Gerente: Acima de 5% exige login
            if (porcDesc > 5.0 && !Sessao.isDescontoAutorizado()) {
                abrirAutorizacaoGerente();
                if (!Sessao.isDescontoAutorizado()) {
                    new Alert(Alert.AlertType.WARNING, "Desconto acima de 5% não autorizado!").show();
                    txtDescontoItem.setText("5.0");
                    return;
                }
            }

            int qtd = Integer.parseInt(txtQtdProduto.getText());
            // Cálculo Correto: 1050 - (1050 * 0.10) = 945
            double valorDesconto = precoBaseAtual * (porcDesc / 100);
            double precoFinal = precoBaseAtual - valorDesconto;
            
            ItemVenda item = new ItemVenda(codigoBarrasAtual, txtNomeProduto.getText(), qtd, precoFinal);
            item.setSubtotal(qtd * precoFinal);
            
            itens.add(item);
            atualizarTotalGeral();
            
            // Reset campos produto
            Sessao.setDescontoAutorizado(false); 
            txtNomeProduto.clear();
            txtQtdProduto.setText("1");
            txtDescontoItem.setText("0");
            txtNomeProduto.requestFocus();
            
        } catch (Exception e) { 
            new Alert(Alert.AlertType.ERROR, "Dados inválidos!").show();
        }
    }

    private void abrirAutorizacaoGerente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AutorizacaoGerente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void atualizarTotalGeral() {
        double total = calcularTotalFinal();
        lblTotalPedido.setText(String.format("R$ %.2f", total));
        
        // Se as parcelas estiverem visíveis, atualiza os valores das parcelas em tempo real
        if (cbParcelas1.isVisible()) {
            atualizarListaParcelas();
        }
    }

    private double calcularTotalFinal() {
        double subtotal = itens.stream().mapToDouble(ItemVenda::getSubtotal).sum();
        try {
            double descGeral = Double.parseDouble(txtDesconto.getText().replace(",", "."));
            return subtotal * (1 - (descGeral / 100));
        } catch (Exception e) { 
            return subtotal;
        }
    }

    @FXML 
    public void finalizarVenda() {
        if (txtCodVendedor.getText().isEmpty() || itens.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vendedor ou Itens vazios!").show();
            return;
        }
        
        try {
            Venda v = new Venda();
            v.setClienteId((Sessao.getClienteSelecionado() != null) ? Sessao.getClienteSelecionado().getId() : 1);
            v.setVendedorCodigo(Integer.parseInt(txtCodVendedor.getText()));
            v.setTotalVenda(calcularTotalFinal());
            
            String forma = cbPagamento1.getValue() != null ? cbPagamento1.getValue() : "Dinheiro";
            v.setBandeira(forma);

            // Pega o número da parcela (ex: "3x de R$..." vira 3)
            int parcelas = 1;
            if (cbParcelas1.isVisible() && cbParcelas1.getValue() != null) {
                parcelas = Integer.parseInt(cbParcelas1.getValue().split("x")[0]);
            }
            v.setQtdParcelas(parcelas);

            // MANDA PARA O BANCO (Fica aguardando faturamento)
            new VendaDAO().salvarPedidoPendente(v, itens, forma, "N/A", parcelas);
            
            new Alert(Alert.AlertType.INFORMATION, "Pedido " + forma + " enviado para o Faturamento!").show();
            limparTela();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void limparTela() {
        itens.clear();
        txtDesconto.setText("0");
        txtNomeCliente.clear();
        txtNomeProduto.clear();
        txtCodVendedor.clear();
        lblTotalPedido.setText("R$ 0,00");
        cbParcelas1.setVisible(false);
        Sessao.setClienteSelecionado(null);
    }
}