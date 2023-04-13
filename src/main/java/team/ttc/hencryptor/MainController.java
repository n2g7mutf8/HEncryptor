package team.ttc.hencryptor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import team.ttc.hencryptor.exception.CipherException;
import team.ttc.hencryptor.utils.CipherUtils;
import team.ttc.hencryptor.utils.PasswordUtils;

import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    private Alert alert = new Alert(Alert.AlertType.ERROR);

    @FXML
    private TextField iFileField;

    @FXML
    private TextField oFileField;

    @FXML
    private PasswordField pField;

    @FXML
    private Button iChooserButton;

    @FXML
    private Button oChooserButton;

    @FXML
    private Button randomButton;

    @FXML
    private Button startButton;

    @FXML
    private Label requirementLabel;

    @FXML
    private ChoiceBox<String> typeChoiceBox;

    @FXML
    private ChoiceBox<String> modeChoiceBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> compatible = Arrays.stream(Security.getProviders())
                .flatMap(provider -> provider.getServices().stream())
                .filter(service -> "Cipher".equals(service.getType()))
                .map(Provider.Service::getAlgorithm)
                .collect(Collectors.toList());

        compatible.removeIf(a -> a.toUpperCase().contains("PADDING"));
        compatible.removeIf(a -> a.toUpperCase().contains("WRAP"));

        typeChoiceBox.getItems().addAll(compatible);
        modeChoiceBox.getItems().addAll("Mã Hóa", "Giải Mã");
    }

    private void registerEvent() {
        EventHandler<ActionEvent> iButtonAction = actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(BaseApplication.getInstance().getStage());

            if (file == null) {
                alert.setTitle("Chọn tệp");
                alert.setContentText("Không thể tìm thấy tệp được chọn!");
                alert.showAndWait();
                return;
            }

            if (Objects.equals(file.getAbsolutePath(), oFileField.getText())) {
                alert.setTitle("Chọn Tệp");
                alert.setContentText("Tệp đầu vào và đầu ra không được phép giống nhau!");
                alert.showAndWait();
                return;
            }

            iFileField.setText(file.getAbsolutePath());
        };
        iChooserButton.addEventHandler(ActionEvent.ACTION, iButtonAction);

        EventHandler<ActionEvent> oButtonAction = actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(BaseApplication.getInstance().getStage());

            if (file == null) {
                alert.setTitle("Chọn tệp");
                alert.setContentText("Không thể tìm thấy tệp được chọn!");
                alert.showAndWait();
                return;
            }
            if (Objects.equals(file.getAbsolutePath(), iFileField.getText())) {
                alert.setTitle("Chọn Tệp");
                alert.setContentText("Tệp đầu vào và đầu ra không được phép giống nhau!");
                alert.showAndWait();
                return;
            }

            oFileField.setText(file.getAbsolutePath());
        };
        oChooserButton.addEventHandler(ActionEvent.ACTION, oButtonAction);

        EventHandler<ActionEvent> randomButtonAction = actionEvent -> {
            if (typeChoiceBox.getValue() == null) {
                alert.setTitle("Phương thức bảo mật");
                alert.setContentText("Phương thức bảo mật không được phép để trống!");
                alert.showAndWait();
                return;
            }
            pField.setText(PasswordUtils.generatePassword(PasswordUtils.getAlgorithmRequirement(typeChoiceBox.getValue())));
        };
        randomButton.addEventHandler(ActionEvent.ACTION, randomButtonAction);

        EventHandler<ActionEvent> checker = actionEvent -> {
            if (typeChoiceBox.getValue() == null) {
                alert.setTitle("Phương thức bảo mật");
                alert.setContentText("Phương thức bảo mật không được phép để trống!");
                alert.showAndWait();
                return;
            }

            if (!PasswordUtils.isValid(pField.getText(), PasswordUtils.getAlgorithmRequirement(typeChoiceBox.getValue()))) {
                requirementLabel.setText("Yêu cầu phải có " + PasswordUtils.getAlgorithmRequirement(typeChoiceBox.getValue()) +
                        " kí tự và có chữ cái viết hoa, viết thường, chữ số và các kí tự đặc biệt! ");
                requirementLabel.setTextFill(Color.RED);
                return;
            }

            requirementLabel.setText("Mật khẩu hợp lệ!");
            requirementLabel.setTextFill(Color.GREEN);
        };
        pField.addEventHandler(ActionEvent.ACTION, checker);

        EventHandler<ActionEvent> startButtonAction = actionEvent -> {
            if (typeChoiceBox.getValue() == null) {
                alert.setTitle("Phương thức bảo mật");
                alert.setContentText("Phương thức bảo mật không được phép để trống!");
                alert.showAndWait();
                return;
            }

            if (!PasswordUtils.isValid(pField.getText(), PasswordUtils.getAlgorithmRequirement(typeChoiceBox.getValue()))) {
                alert.setTitle("Mật khẩu");
                alert.setContentText(("Yêu cầu phải có " + PasswordUtils.getAlgorithmRequirement(typeChoiceBox.getValue()) +
                        " kí tự và có chữ cái viết hoa, viết thường, chữ số và các kí tự đặc biệt! "));
                alert.showAndWait();
                return;
            }

            if (iFileField.getText() == null || oFileField.getText() == null) {
                alert.setTitle("Vị trí");
                alert.setContentText("Tệp đầu vào và đầu ra không được để trống!");
                alert.showAndWait();
                return;
            }

            if (typeChoiceBox.getValue() == null) {
                alert.setTitle("Chế Độ");
                alert.setContentText("Chế độ không được phép để trống!");
                alert.showAndWait();
                return;
            }

            File iFile = new File(iFileField.getText());
            File oFile = new File(oFileField.getText());

            switch (modeChoiceBox.getValue()) {
                case "Mã Hóa" -> {
                    if (typeChoiceBox.getValue().contains("PBE")) {
                        try {
                            CipherUtils.encrypt(pField.getText(), iFile, oFile, typeChoiceBox.getValue(),
                                    pField.getText().getBytes(), 10,
                                    new IvParameterSpec(new byte[]
                                            {-57, 115, 33, -116, 126, -56, -18, -103, -56, 116, 34, -115, 127, -55, -17, -102}));
                            alert.setTitle("Mã hóa");
                            alert.setContentText("Mã hóa thành công!");
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.showAndWait();
                        } catch (CipherException e) {
                            alert.setTitle("Mã hóa");
                            alert.setContentText("Mã hóa thất bại!");
                            alert.showAndWait();
                        }
                        break;
                    }
                    try {
                        CipherUtils.encrypt(pField.getText(), iFile, oFile, typeChoiceBox.getValue());
                        alert.setTitle("Mã hóa");
                        alert.setContentText("Mã hóa thành công!");
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.showAndWait();
                    } catch (CipherException e) {
                        alert.setTitle("Mã hóa");
                        alert.setContentText("Mã hóa thất bại!");
                        alert.showAndWait();
                    }
                }
                case "Giải Mã" -> {
                    if (typeChoiceBox.getValue().contains("PBE")) {
                        try {
                            CipherUtils.decrypt(pField.getText(), iFile, oFile, typeChoiceBox.getValue(),
                                    pField.getText().getBytes(), 10,
                                    new IvParameterSpec(new byte[]
                                            {-57, 115, 33, -116, 126, -56, -18, -103, -56, 116, 34, -115, 127, -55, -17, -102}));
                            alert.setTitle("Giải Mã");
                            alert.setContentText("Giải mã thành công!");
                            alert.setAlertType(Alert.AlertType.INFORMATION);
                            alert.showAndWait();
                        } catch (CipherException e) {
                            alert.setTitle("Giải Mã");
                            alert.setContentText("Giải mã thất bại!");
                            alert.showAndWait();
                        }
                        break;
                    }
                    try {
                        CipherUtils.decrypt(pField.getText(), iFile, oFile, typeChoiceBox.getValue());
                        alert.setTitle("Giải Mã");
                        alert.setContentText("Giải mã thành công!");
                        alert.setAlertType(Alert.AlertType.INFORMATION);
                        alert.showAndWait();
                    } catch (CipherException e) {
                        alert.setTitle("Giải Mã");
                        alert.setContentText("Giải mã thất bại!");
                        alert.showAndWait();
                    }
                }
            }
        };
        startButton.addEventHandler(ActionEvent.ACTION, startButtonAction);
    }
}