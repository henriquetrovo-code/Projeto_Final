package controller;

import model.Sessao;
import model.Usuario;
import dao.RelatorioDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    @FXML private AnchorPane painelCentral; 
    @FXML private Button btnCadUsuarios, btnFaturar, btnEstoque, btnCancelarVenda, btnRelatorios, btnPdv, btnClientes, btnReceber;
    @FXML private TitledPane paneCaixa, paneCadastros;

    @FXML
    public void initialize() {
        System.out.println("HyperTech - Menu Principal Carregado.");
        configurarPermissoes();

        Platform.runLater(() -> {
            if (painelCentral != null && painelCentral.getScene() != null) {
                Stage stage = (Stage) painelCentral.getScene().getWindow();
                stage.setMaximized(true);
            }
        });
    }

    private void configurarPermissoes() {
        Usuario logado = Sessao.getUsuario();
        if (logado == null) return;

        String perfil = logado.getPerfil();

        if (perfil.equalsIgnoreCase("Vendedor")) {
            if (btnReceber != null) btnReceber.setVisible(false);
            if (btnFaturar != null) btnFaturar.setVisible(false);
            if (btnRelatorios != null) btnRelatorios.setVisible(false);
            if (btnCancelarVenda != null) btnCancelarVenda.setVisible(false);
            if (paneCaixa != null) paneCaixa.setVisible(false);
            if (btnCadUsuarios != null) btnCadUsuarios.setVisible(false);
        }

        if (perfil.equalsIgnoreCase("Caixa")) {
            if (btnEstoque != null) btnEstoque.setVisible(false);
            if (btnCadUsuarios != null) btnCadUsuarios.setVisible(false);
        }
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    @FXML 
    public void irReceber() { 
        carregarTela("/view/receber.fxml"); 
    }

    @FXML public void irPedido() { carregarTela("/view/Pedido.fxml"); }
    @FXML public void irFaturar() { carregarTela("/view/Faturar.fxml"); }
    @FXML public void irEstoque() { carregarTela("/view/Estoque.fxml"); }
    @FXML public void irClientes() { carregarTela("/view/cliente.fxml"); }
    @FXML public void irRelatorios() { carregarTela("/view/relatorio.fxml"); }
    @FXML public void irCancelarVenda() { carregarTela("/view/CancelarVenda.fxml"); }

    @FXML public void abrirEntradaCaixa() { processarMovimentacao("ABERTURA"); }
    @FXML public void abrirSaidaCaixa() { processarMovimentacao("RETIRADA"); }
    
    @FXML public void irCadastroProduto() { carregarTela("/view/CadastroProdutos.fxml"); }
    @FXML public void irCadastroCliente() { carregarTela("/view/CadastroClientes.fxml"); }
    @FXML public void irCadastroUsuario() { carregarTela("/view/CadastroUsuario.fxml"); }

    @FXML public void sair() { System.exit(0); }

    private void carregarTela(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Node tela = loader.load();
            
            // Força atualização da tabela se carregar Faturar ou Receber
            if (fxml.toLowerCase().contains("faturar") || fxml.toLowerCase().contains("receber")) {
                Object controller = loader.getController();
                if (controller instanceof FaturarController) {
                    ((FaturarController) controller).atualizarTabela();
                }
            }

            if (painelCentral != null) {
                painelCentral.getChildren().setAll(tela);
                AnchorPane.setTopAnchor(tela, 0.0);
                AnchorPane.setBottomAnchor(tela, 0.0);
                AnchorPane.setLeftAnchor(tela, 0.0);
                AnchorPane.setRightAnchor(tela, 0.0);
            }
        } catch (IOException e) {
            System.err.println("ERRO: Arquivo FXML não encontrado -> " + fxml);
            e.printStackTrace();
        }
    }

    private void processarMovimentacao(String tipo) {
        TextInputDialog dialog = new TextInputDialog("0.00");
        dialog.setTitle("Movimentação de Caixa");
        dialog.setContentText("Valor R$:");
        dialog.showAndWait().ifPresent(v -> {
            try {
                double valor = Double.parseDouble(v.replace(",", "."));
                new RelatorioDAO().registrarMovimentacaoCaixa(tipo, valor, Sessao.getUsuario().getId(), "Manual");
                new Alert(Alert.AlertType.INFORMATION, "Sucesso!").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Valor inválido").show();
            }
        });
    }
}