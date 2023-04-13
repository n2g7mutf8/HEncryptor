module team.ttc.hencryptor {
    requires javafx.controls;
    requires javafx.fxml;

    opens team.ttc.hencryptor to javafx.fxml;
    exports team.ttc.hencryptor;
}