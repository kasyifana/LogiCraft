package Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;

import Helpers.AlertHelper;
import Helpers.UserSession;

public class LoginController {

    @FXML
    private ImageView imageViewLogin;

    @FXML
    private Label appNameLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordLabel;

    @FXML
    private ComboBox<String> loginAsComboBox;

    @FXML
    private Label loginAsLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Label linkToRegisterLabel;

    @FXML
    public void initialize() {
        loginAsComboBox.setItems(FXCollections.observableArrayList("Pengajar", "Provider", "User"));
        linkToRegisterLabel.setOnMouseClicked(event -> openRegisterPage());
        loginButton.setOnAction(event -> handleLoginButtonAction());
    }

    private void openRegisterPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Register.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Register");
            registerStage.show();

            Stage loginStage = (Stage) linkToRegisterLabel.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLoginButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = loginAsComboBox.getValue();

        if (role == null) {
            showError("Gagal", "Silakan pilih peran.");
            return;
        }

        try {
            boolean loginSuccessful = false;

            switch (role) {
                case "Pengajar":
                    loginSuccessful = checkCSVFile("database/pengajar.csv", email, password);
                    break;
                case "Provider":
                    loginSuccessful = checkXMLFile("database/provider.xml", email, password);
                    break;
                case "User":
                    loginSuccessful = checkCSVFile("database/user.csv", email, password);
                    break;
            }

            if (loginSuccessful) {
                showSuccess("Berhasil", "Login berhasil!");
                String loaderPath = "";
                String title = "";
                switch (role) {
                	case "Pengajar":
                    	loaderPath = "/View/ManageVideo.fxml";
                    	title = "Manage Video";
                    	break;
                	case "Provider":
                		loaderPath = "/View/ProviderMenu.fxml";
                		title = "Menu";
                		break;
                	case "User":
                		loaderPath = "/View/AccessVideo.fxml";
                		title = "Tonton Video";
                		break;
                }
                
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(loaderPath));
                    Parent root = loader.load();

                    Stage registerStage = new Stage();
                    registerStage.setScene(new Scene(root));
                    registerStage.setTitle(title);
                    registerStage.show();

                    Stage loginStage = (Stage) linkToRegisterLabel.getScene().getWindow();
                    loginStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showError("Gagal", "Email atau password salah.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Gagal", "Terjadi kesalahan. Silakan coba lagi.");
        }
    }

    private boolean checkCSVFile(String filePath, String email, String password) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4 && data[2].equals(email) && data[3].equals(password)) {
                    UserSession.setCurrentUserID(data[0]);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkXMLFile(String filePath, String email, String password) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            NodeList nodeList = document.getElementsByTagName("provider");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element providerElement = (Element) nodeList.item(i);
                String providerEmail = providerElement.getElementsByTagName("email").item(0).getTextContent();
                String providerPassword = providerElement.getElementsByTagName("password").item(0).getTextContent();
                if (providerEmail.equals(email) && providerPassword.equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showSuccess(String title, String message) {
        AlertHelper.showSuccessAlert(title, message);
    }

    private void showError(String title, String message) {
        AlertHelper.showErrorAlert(title, message);
    }
}
