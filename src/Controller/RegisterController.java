package Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.io.File;
import java.io.BufferedWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import Model.Pengajar;
import Model.Provider;
import Model.User;
import Helpers.AlertHelper;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> loginAsComboBox;

    @FXML
    private TextField namaField;

    @FXML
    private Label linkToLoginLabel;
    
    @FXML
    public void initialize() {
        loginAsComboBox.setItems(FXCollections.observableArrayList("Pengajar", "Provider", "User"));
        
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        try {
            Files.createDirectories(Paths.get("database"));
            initializeProviderXML("database/provider.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        linkToLoginLabel.setOnMouseClicked(event -> openLoginPage());
    }

    @FXML
    private void handleRegisterButtonAction() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String nama = namaField.getText();
        String role = loginAsComboBox.getValue();
        try {
            if (role != null) {
                switch (role) {
                    case "Pengajar":
                        Pengajar pengajar = new Pengajar(generateId(role), nama, email, password, role);
                        saveToCSV("database/pengajar.csv", pengajar.toString());
                        break;
                    case "Provider":
                        Provider provider = new Provider(generateId(role), nama, email, password, role);
                        saveToXML("database/provider.xml", provider);
                        break;
                    case "User":
                        User user = new User(generateId(role), nama, email, password, role);
                        System.out.println("Saving user to CSV: " + user.toString()); 
                        saveToCSV("database/user.csv", user.toString());
                        break;
                    default:
                        break;
                }
                clearFields();
                AlertHelper.showSuccessAlert("Berhasil", "Registrasi berhasil!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Gagal", "Registrasi gagal. Silakan coba lagi!");
        }
    }

    private void saveToCSV(String fileName, String data) {
        String filePath = Paths.get(fileName).toString();
        System.out.println("Saving to CSV: " + filePath + " Data: " + data);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToXML(String fileName, Provider provider) {
        try {
            String filePath = Paths.get(fileName).toString();
            File file = new File(filePath);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc;

            if (file.exists() && file.length() != 0) {
                doc = docBuilder.parse(file);
            } else {
                doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("providers");
                doc.appendChild(rootElement);
            }

            Element rootElement = doc.getDocumentElement();

            Element providerElement = doc.createElement("provider");

            Element id = doc.createElement("id");
            id.appendChild(doc.createTextNode(provider.getId()));
            providerElement.appendChild(id);

            Element nama = doc.createElement("nama");
            nama.appendChild(doc.createTextNode(provider.getNama()));
            providerElement.appendChild(nama);

            Element email = doc.createElement("email");
            email.appendChild(doc.createTextNode(provider.getEmail()));
            providerElement.appendChild(email);

            Element password = doc.createElement("password");
            password.appendChild(doc.createTextNode(provider.getPassword()));
            providerElement.appendChild(password);

            Element role = doc.createElement("role");
            role.appendChild(doc.createTextNode(provider.getRole()));
            providerElement.appendChild(role);

            rootElement.appendChild(providerElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (ParserConfigurationException | SAXException | IOException | javax.xml.transform.TransformerException e) {
            e.printStackTrace();
        }
    }

    private void initializeProviderXML(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("<providers>\n</providers>");
            }
        }
    }

    private String generateId(String role) {
        String prefix = "";
        switch (role) {
            case "Pengajar":
                prefix = "PENGAJAR";
                break;
            case "Provider":
                prefix = "PROVIDER";
                break;
            case "User":
                prefix = "USER";
                break;
            default:
                break;
        }
        int randomNum = new Random().nextInt(9000) + 1000;
        return prefix + randomNum;
    }

    private void clearFields() {
        emailField.clear();
        passwordField.clear();
        namaField.clear();
        loginAsComboBox.getSelectionModel().clearSelection();
    }
    
    private void openLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Login.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Login");
            registerStage.show();

            Stage loginStage = (Stage) linkToLoginLabel.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
