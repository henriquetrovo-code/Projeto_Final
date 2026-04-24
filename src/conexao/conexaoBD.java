package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexaoBD {
    
    // Caminho do banco de dados (usando eletrotech em minúsculo conforme o script SQL padrão)
    private static final String URL = "jdbc:mysql://localhost:3306/eletrotech";
    private static final String USUARIO = "root"; 
    private static final String SENHA = ""; // Deixado em branco conforme a sua configuração
    
    /**
     * Método responsável por estabelecer a conexão com o banco de dados.
     * @return Connection
     */
    public Connection getConnection() {
        try {
            // Tenta estabelecer a conexão
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            // Caso dê erro, lança uma exceção parando o programa e mostrando o motivo
            throw new RuntimeException("Erro crítico: Não foi possível conectar ao banco de dados EletroTech. Detalhes: " + e.getMessage(), e);
        }
    }
    
    // Método rápido apenas para você testar se a conexão deu certo hoje
    public static void main(String[] args) {
        try {
            new conexaoBD().getConnection();
            System.out.println("Sucesso! Conexão com o banco EletroTech estabelecida perfeitamente.");
        } catch (Exception e) {
            System.out.println("Falha na conexão.");
            e.printStackTrace();
        }
    }
}