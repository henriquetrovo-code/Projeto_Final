module EletroTech {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
	requires javafx.graphics;
	requires javafx.base;

    // abre pacotes para reflexão do FXML
    opens application to javafx.graphics, javafx.fxml;
    opens controller to javafx.fxml;
    opens model to javafx.fxml;
    opens view to javafx.fxml;

    // exporta apenas pacotes Java
    exports application;
    exports controller;
    exports model;
}
