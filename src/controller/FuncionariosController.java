package controller;

import dao.UsuarioDAO;
import model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FuncionariosController {

    @FXML private TextField txtNome, txtLogin;
    @FXML private PasswordField txtSenha;
    @FXML private ComboBox<String> cbPerfil;

    // --- NOVOS CAMPOS PARA A TABELA ---
    @FXML private TableView<Usuario> tabelaFuncionarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNome, colLogin, colPerfil;

    private UsuarioDAO dao = new UsuarioDAO();

    @FXML
    public void initialize() {
        // Preenche o ComboBox
        if (cbPerfil != null) {
            cbPerfil.getItems().addAll("Gerente", "Vendedor", "Caixa");
        }

        // Configura o mapeamento das colunas da tabela
        // O nome entre aspas deve ser IGUAL ao atributo na classe model.Usuario
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));

        // Carrega a lista do banco ao abrir a tela
        atualizarTabela();
    }

    @FXML
    public void salvarFuncionario() {
        // Validação básica
        if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty() || cbPerfil.getValue() == null) {
            mostrarAlerta("Aviso", "Preencha todos os campos obrigatórios!", Alert.AlertType.WARNING);
            return;
        }

        Usuario u = new Usuario();
        u.setNome(txtNome.getText());
        u.setLogin(txtLogin.getText());
        u.setSenha(txtSenha.getText());
        u.setPerfil(cbPerfil.getValue());

        if (dao.salvar(u)) {
            mostrarAlerta("Sucesso", "Funcionário " + u.getNome() + " cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
            atualizarTabela(); // Já faz aparecer na lista ao lado na hora!
        }
    }

    @FXML
    public void atualizarTabela() {
        // Busca os dados atualizados do banco via DAO
        ObservableList<Usuario> lista = FXCollections.observableArrayList(dao.listarTodos());
        tabelaFuncionarios.setItems(lista);
    }

    @FXML
    public void excluirFuncionario() {
        Usuario selecionado = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um funcionário na lista para excluir!", Alert.AlertType.WARNING);
            return;
        }

        // Impede de excluir o admin master logado (opcional)
        if (selecionado.getLogin().equalsIgnoreCase("Henrique")) {
            mostrarAlerta("Erro", "Você não pode excluir o administrador do sistema!", Alert.AlertType.ERROR);
            return;
        }

        if (dao.excluir(selecionado.getId())) {
            mostrarAlerta("Sucesso", "Funcionário removido!", Alert.AlertType.INFORMATION);
            atualizarTabela();
        }
    }

    @FXML
    public void limparCampos() {
        txtNome.clear(); 
        txtLogin.clear(); 
        txtSenha.clear();
        cbPerfil.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}	