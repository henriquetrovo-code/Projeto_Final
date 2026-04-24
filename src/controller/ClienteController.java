package controller;

import dao.ClienteDAO;
import model.Cliente;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ClienteController {

    @FXML private TextField txtNome, txtCpfCnpj, txtEmail, txtTelefone, txtCep, txtFiltroCliente;
    @FXML private TextArea txtEndereco;
    @FXML private ComboBox<String> cbTipo;
    
    @FXML private TableView<Cliente> tabelaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome, colCpfCnpj, colTelefone;

    private ClienteDAO dao = new ClienteDAO();
    private Cliente clienteSelecionadoParaEdicao = null;

    @FXML
    public void initialize() {
        configurarColunas();
        if (cbTipo != null) cbTipo.getItems().addAll("Fisica", "Juridica");
        atualizarTabela();

        tabelaClientes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tabelaClientes.getSelectionModel().getSelectedItem() != null) {
                abrirFichaDetalhada();
            }
        });
    }

    // --- MÉTODOS DE BUSCA E FILTRO ---

    @FXML
    public void filtrarClientes() {
        String busca = txtFiltroCliente.getText();
        List<Cliente> resultado = (busca.isEmpty()) ? dao.listarTodos() : dao.buscarPorNome(busca);
        tabelaClientes.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    public void verificarEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == txtFiltroCliente) {
                filtrarClientes();
            } else if (event.getSource() == txtNome) {
                txtCpfCnpj.requestFocus();
            }
        }
    }

    // --- MÉTODOS DE AÇÃO (SALVAR / EDITAR / EXCLUIR) ---

    @FXML
    public void salvarCliente() {
        try {
            // Limpa formatação para validação e banco
            String doc = txtCpfCnpj.getText().replaceAll("\\D", "");
            String tipo = cbTipo.getValue();

            // 1. TRAVA DE CAMPOS OBRIGATÓRIOS
            if (txtNome.getText().trim().isEmpty() || tipo == null || doc.isEmpty() || txtTelefone.getText().trim().isEmpty()) {
                exibirMensagem("Campos Obrigatórios", "Atenção! Nome, Tipo, CPF/CNPJ e Telefone devem ser preenchidos.", AlertType.WARNING);
                return;
            }

            // 2. TRAVA DE CPF/CNPJ DUPLICADO (Apenas para novos cadastros)
            if (clienteSelecionadoParaEdicao == null) {
                if (dao.existeCpfCnpj(doc)) {
                    exibirMensagem("Documento Duplicado", "Já existe um cliente cadastrado com este CPF/CNPJ: " + txtCpfCnpj.getText() + "\nVerifique os dados ou use a busca.", AlertType.ERROR);
                    return;
                }
            }

            // 3. VALIDAÇÃO DE FORMATO
            if (tipo.equals("Fisica") && !validarCPF(doc)) {
                exibirMensagem("Erro de Validação", "O CPF digitado é inválido. Verifique os números.", AlertType.ERROR);
                return;
            } else if (tipo.equals("Juridica") && !validarCNPJ(doc)) {
                exibirMensagem("Erro de Validação", "O CNPJ digitado é inválido ou incompleto.", AlertType.ERROR);
                return;
            }

            // --- PROCESSO DE SALVAMENTO ---
            Cliente c = (clienteSelecionadoParaEdicao == null) ? new Cliente() : clienteSelecionadoParaEdicao;
            c.setNome(txtNome.getText().trim());
            c.setTipoPessoa(tipo);
            c.setCpfCnpj(doc);
            c.setEmail(txtEmail.getText());
            c.setTelefone(txtTelefone.getText());
            c.setCep(txtCep.getText());
            c.setEnderecoCompleto(txtEndereco.getText());

            if (clienteSelecionadoParaEdicao == null) {
                if(dao.salvar(c)) {
                    exibirMensagem("Sucesso", "Novo cliente cadastrado com sucesso!", AlertType.INFORMATION);
                } else {
                    exibirMensagem("Erro", "Falha técnica ao salvar no banco de dados.", AlertType.ERROR);
                    return;
                }
            } else {
                dao.atualizar(c); 
                clienteSelecionadoParaEdicao = null;
                exibirMensagem("Sucesso", "Dados do cliente atualizados!", AlertType.INFORMATION);
            }

            limparCampos();
            atualizarTabela();
            
        } catch (Exception e) {
            exibirMensagem("Erro Crítico", "Erro ao processar: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    public void prepararEdicao() {
        clienteSelecionadoParaEdicao = tabelaClientes.getSelectionModel().getSelectedItem();
        if (clienteSelecionadoParaEdicao != null) {
            txtNome.setText(clienteSelecionadoParaEdicao.getNome());
            cbTipo.setValue(clienteSelecionadoParaEdicao.getTipoPessoa());
            txtCpfCnpj.setText(clienteSelecionadoParaEdicao.getCpfCnpj());
            txtEmail.setText(clienteSelecionadoParaEdicao.getEmail());
            txtTelefone.setText(clienteSelecionadoParaEdicao.getTelefone());
            txtCep.setText(clienteSelecionadoParaEdicao.getCep());
            txtEndereco.setText(clienteSelecionadoParaEdicao.getEnderecoCompleto());
        } else {
            exibirMensagem("Aviso", "Selecione um cliente na tabela para editar!", AlertType.WARNING);
        }
    }

    @FXML
    public void excluirCliente() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirMensagem("Aviso", "Selecione um cliente para excluir.", AlertType.WARNING);
            return;
        }

        // Simulação de verificação de permissão (como você pediu antes)
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Autorização do Gerente");
        dialog.setHeaderText("Excluir Cliente: " + selecionado.getNome());
        dialog.setContentText("Digite a senha do Gerente:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get().equals("1234")) { 
                dao.excluir(selecionado.getId());
                atualizarTabela();
                exibirMensagem("Sucesso", "Cliente removido do sistema.", AlertType.INFORMATION);
            } else {
                exibirMensagem("Erro", "Senha de autorização incorreta!", AlertType.ERROR);
            }
        }
    }

    // --- VALIDAÇÃO DE DOCUMENTOS ---

    private boolean validarCPF(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) return false;
        try {
            int sm = 0, weight = 10;
            for (int i = 0; i < 9; i++) sm += (Character.getNumericValue(cpf.charAt(i)) * weight--);
            int r = 11 - (sm % 11);
            int d1 = (r > 9) ? 0 : r;
            sm = 0; weight = 11;
            for (int i = 0; i < 10; i++) sm += (Character.getNumericValue(cpf.charAt(i)) * weight--);
            r = 11 - (sm % 11);
            int d2 = (r > 9) ? 0 : r;
            return (d1 == Character.getNumericValue(cpf.charAt(9)) && d2 == Character.getNumericValue(cpf.charAt(10)));
        } catch (Exception e) { return false; }
    }

    private boolean validarCNPJ(String cnpj) {
        return cnpj.length() == 14; 
    }

    // --- AUXILIARES ---

    private void abrirFichaDetalhada() {
        Cliente selecionado = tabelaClientes.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DetalhesClientes.fxml"));
            Parent root = loader.load();
            DetalheClienteController controller = loader.getController();
            controller.carregarDados(selecionado);
            Stage stage = new Stage();
            stage.setTitle("Ficha: " + selecionado.getNome());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCpfCnpj.setCellValueFactory(new PropertyValueFactory<>("cpfCnpj"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
    }

    public void atualizarTabela() {
        tabelaClientes.setItems(FXCollections.observableArrayList(dao.listarTodos()));
    }

    @FXML
    public void limparCampos() {
        clienteSelecionadoParaEdicao = null;
        txtNome.clear(); txtCpfCnpj.clear(); txtEmail.clear();
        txtTelefone.clear(); txtCep.clear(); txtEndereco.clear();
        if (cbTipo != null) cbTipo.getSelectionModel().clearSelection();
        txtNome.requestFocus();
    }

    private void exibirMensagem(String titulo, String msg, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}