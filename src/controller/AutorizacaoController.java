package controller;

import dao.UsuarioDAO;
import model.Sessao;
import model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class AutorizacaoController {

    @FXML private TextField txtUsuarioGerente;
    @FXML private PasswordField txtSenhaGerente;

    @FXML
    public void initialize() {
        // FOCO INICIAL NO CAMPO DE USUÁRIO
        txtUsuarioGerente.requestFocus();

        // NAVEGAÇÃO POR ENTER: Do Usuário para a Senha
        txtUsuarioGerente.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                txtSenhaGerente.requestFocus();
            }
        });

        // NAVEGAÇÃO por ENTER: Da Senha para a Autorização
        txtSenhaGerente.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                confirmarAutorizacao();
            }
        });
    }

    @FXML
    private void confirmarAutorizacao() {
        String login = txtUsuarioGerente.getText().trim();
        String senha = txtSenhaGerente.getText().trim();

        if (login.isEmpty() || senha.isEmpty()) {
            mostrarMensagem("Campos Vazios", "Informe o usuário e a senha do gerente.");
            return;
        }

        // 1. Instancia o seu DAO
        UsuarioDAO dao = new UsuarioDAO();
        
        // 2. Chama o método CORRETO do seu DAO: autenticarUsuario
        Usuario gerente = dao.autenticarUsuario(login, senha);

        // 3. Validação de perfil (Gerente ou Admin)
        if (gerente != null) {
            String perfil = gerente.getPerfil();
            if (perfil != null && (perfil.equalsIgnoreCase("GERENTE") || perfil.equalsIgnoreCase("ADMIN"))) {
                
                Sessao.setDescontoAutorizado(true);
                fecharJanela();
                
            } else {
                Sessao.setDescontoAutorizado(false);
                mostrarMensagem("Acesso Negado", "Este usuário não tem nível de Gerente.");
            }
        } else {
            Sessao.setDescontoAutorizado(false);
            mostrarMensagem("Erro de Login", "Usuário ou senha do gerente incorretos.");
        }
    }

    @FXML
    private void cancelar() {
        Sessao.setDescontoAutorizado(false);
        fecharJanela();
    }

    private void mostrarMensagem(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("HyperTech PDV");
        alert.setHeaderText(titulo);
        alert.setContentText(texto);
        alert.showAndWait();
        
        txtSenhaGerente.clear();
        txtUsuarioGerente.requestFocus();
    }

    private void fecharJanela() {
        Stage stage = (Stage) txtUsuarioGerente.getScene().getWindow();
        stage.close();
    }
}