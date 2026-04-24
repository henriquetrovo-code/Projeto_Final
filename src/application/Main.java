package application; // Ajuste para o nome do seu pacote

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Começamos pela tela de login conforme o fluxo do sistema
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(root);
            
            primaryStage.setTitle("EletroTech - Sistema de Gestão");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Mantém a proporção de 800x600 que definimos
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}