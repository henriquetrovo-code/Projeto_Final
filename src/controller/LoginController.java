package controller;

import dao.UsuarioDAO;
import model.Usuario;
import model.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;
    private UsuarioDAO dao = new UsuarioDAO();

    @FXML
    public void fazerLogin() {
        Usuario user = dao.autenticarUsuario(txtLogin.getText(), txtSenha.getText());
        if (user != null) {
            Sessao.setUsuario(user);
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
                Stage stage = (Stage) txtLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            new Alert(Alert.AlertType.ERROR, "Login ou Senha incorretos!").show();
        }
    }

    @FXML
    public void verificarTecla(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (event.getSource() == txtLogin) {
                txtSenha.requestFocus(); // Pula para a senha
            } else {
                fazerLogin(); // Só aqui ele entra
            }
        }
    }
}