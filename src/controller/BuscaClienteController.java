package controller;

import dao.ClienteDAO;
import model.Cliente;
import model.Sessao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent; // Importante para o evento de teclado
import javafx.stage.Stage;
import java.util.List;

public class BuscaClienteController {

    @FXML private TextField txtBuscaNome;
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome;

    private ClienteDAO dao = new ClienteDAO();

    @FXML
    public void initialize() {
        // Mapeia as colunas para os atributos da classe Cliente
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        
        carregarTodos();

        // 1. Seleção por Duplo Clique na Tabela
        tabelaClientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                confirmarSelecao();
            }
        });
    }

    // 2. Seleção por ENTER (Tanto no campo de busca quanto na tabela)
    @FXML
    public void verificarEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            // Se o foco estiver na tabela, ele confirma a seleção do cliente
            if (event.getSource() == tabelaClientes) {
                confirmarSelecao();
            } 
            // Se o foco estiver no campo de texto, ele realiza a pesquisa
            else if (event.getSource() == txtBuscaNome) {
                pesquisar();
            }
        }
    }

    @FXML
    public void pesquisar() {
        String busca = txtBuscaNome.getText();
        // Usa o seu método do DAO que agora busca por Nome ou CPF
        List<Cliente> lista = (busca.isEmpty()) ? dao.listarTodos() : dao.buscarPorNome(busca);
        tabelaClientes.setItems(FXCollections.observableArrayList(lista));
        
        // Se a lista não estiver vazia, foca na tabela para facilitar a escolha com as setas
        if (!lista.isEmpty()) {
            tabelaClientes.requestFocus();
            tabelaClientes.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void confirmarSelecao() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Sessao.setClienteSelecionado(selecionado); // Joga na Sessão para o Pedido pegar
            
            // Fecha a janela atual
            Stage stage = (Stage) tabelaClientes.getScene().getWindow();
            stage.close();
        }
    }

    private void carregarTodos() {
        tabelaClientes.setItems(FXCollections.observableArrayList(dao.listarTodos()));
    }
}