package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import Helpers.AlertHelper;
import Helpers.UserSession;

public class SubscribeController implements Initializable {

    @FXML
    private ComboBox<String> pilihMataPelajaranComboBox;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private TextArea howToPayField;

    @FXML
    private TextField hargaField;

    @FXML
    private TextField masaBerlakuField;

    @FXML
    private TextField paymentProofField;
    
    @FXML
    private Button backButton;

    private static final String[] PAYMENT_METHODS = {"Transfer Bank", "E-Wallet", "Kartu Kredit"};
    private static final String MATA_PELAJARAN_FILE = "database/mata_pelajaran.csv";
    private static final String SUBSCRIPTION_FILE = "database/subscription.csv";
    private static final String UPLOADED_IMG_FOLDER = "uploaded_img";

    private Map<String, String> mataPelajaranMap = new HashMap<>();
    private String selectedMataPelajaranId = "";
    private String paymentProofFilePath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        initializeFields();
        backButton.setOnAction(event -> backButtonAction());
    }
    
    private void backButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/AccessVideo.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Tonton Video");
            registerStage.show();

            Stage loginStage = (Stage) paymentProofField.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeComboBoxes() {
        loadMataPelajaran();
        pilihMataPelajaranComboBox.getItems().addAll(mataPelajaranMap.values());
        paymentMethodComboBox.getItems().addAll(PAYMENT_METHODS);

        pilihMataPelajaranComboBox.setOnAction(event -> {
            String selectedMataPelajaran = pilihMataPelajaranComboBox.getValue();
            if (selectedMataPelajaran != null) {
                selectedMataPelajaranId = getMataPelajaranId(selectedMataPelajaran);
                int harga = getHargaMataPelajaran();
                hargaField.setText(String.valueOf(harga));

                LocalDate currentDate = LocalDate.now();
                LocalDate masaBerlaku = currentDate.plusYears(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                masaBerlakuField.setText(currentDate.format(formatter) + " sampai " + masaBerlaku.format(formatter));
            }
        });

        paymentMethodComboBox.setOnAction(event -> {
            String selectedPaymentMethod = paymentMethodComboBox.getValue();
            if (selectedPaymentMethod != null) {
                switch (selectedPaymentMethod) {
                    case "Transfer Bank":
                        howToPayField.setText("Silakan transfer ke rekening XYZ dengan nominal yang telah disepakati.");
                        break;
                    case "E-Wallet":
                        howToPayField.setText("Lakukan pembayaran melalui E-Wallet dengan ID: [ID E-Wallet].");
                        break;
                    case "Kartu Kredit":
                        howToPayField.setText("Gunakan kartu kredit untuk pembayaran.");
                        break;
                    default:
                        howToPayField.setText("");
                }
            }
        });
    }

    private void initializeFields() {
        hargaField.setEditable(false);
        masaBerlakuField.setEditable(false);
        paymentProofField.setEditable(false);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return "proof_" + System.currentTimeMillis() + extension;
    }

    
    private void loadMataPelajaran() {
        try (BufferedReader br = new BufferedReader(new FileReader(MATA_PELAJARAN_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    mataPelajaranMap.put(values[0], values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getMataPelajaranId(String namaMataPelajaran) {
        for (Map.Entry<String, String> entry : mataPelajaranMap.entrySet()) {
            if (entry.getValue().equals(namaMataPelajaran)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private int getHargaMataPelajaran() {
        Random random = new Random();
        int harga = 400000 + random.nextInt(201) * 1000;
        return harga;
    }

    @FXML
    private void handleChooseFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpeg", "*.jpg", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            paymentProofFilePath = selectedFile.getAbsolutePath();
            paymentProofField.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleSubscribeButtonAction() {
        String idUser = UserSession.getCurrentUserID();
        String harga = hargaField.getText();
        String masaBerlaku = masaBerlakuField.getText();
        String paymentMethod = paymentMethodComboBox.getValue();
        String paymentProofFileName = generateUniqueFileName(Paths.get(paymentProofFilePath).getFileName().toString());

        saveSubscription(idUser, selectedMataPelajaranId, harga, masaBerlaku, paymentMethod, paymentProofFileName);
        savePaymentProofImage();

        AlertHelper.showSuccessAlert("Pendaftaran Berhasil", "Pendaftaran telah berhasil disimpan.");
        backButtonAction();
        AlertHelper.showSuccessAlert("Sedang Diproses, ya!", "Silakan menunggu kelas dari Provider.");
    }

    private void saveSubscription(String idUser, String idMataPelajaran, String harga, String masaBerlaku, String paymentMethod, String paymentProofFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SUBSCRIPTION_FILE, true))) {
            writer.print("\n" + idUser + "," + idMataPelajaran + "," + harga + "," + masaBerlaku + "," + paymentMethod + "," + UPLOADED_IMG_FOLDER + "/" + paymentProofFileName + ",false");
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Error", "Gagal menyimpan langganan ke database.");
        }
    }

    private void savePaymentProofImage() {
        File destDir = new File(UPLOADED_IMG_FOLDER);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try {
            Files.copy(Paths.get(paymentProofFilePath), Paths.get(UPLOADED_IMG_FOLDER, generateUniqueFileName(Paths.get(paymentProofFilePath).getFileName().toString())));
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Error", "Gagal menyimpan bukti pembayaran.");
        }
    }

}
