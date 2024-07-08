package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Helpers.AlertHelper;
import Helpers.UserSession;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UploadVideoController implements Initializable {

    @FXML
    private ComboBox<String> chooseClassComboBox;

    @FXML
    private Label uploadVideoLabel;

    @FXML
    private Label videoTitleLabel;

    @FXML
    private Label videoDescriptionLabel;

    @FXML
    private Label classLabel;

    @FXML
    private Label videoFileLabel;

    @FXML
    private TextField videoTitleField;

    @FXML
    private TextArea videoDescriptionField;

    @FXML
    private TextField videoFileField;

    @FXML
    private Button uploadButton;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button backButton;

    private static final String KELAS_FILE = "database/kelas.csv";
    private static final String VIDEO_FILE = "database/video.csv";
    private static final String UPLOADED_VIDEO_FOLDER = "uploaded_video";
    private Map<String, String> kelasMap = new HashMap<>();
    private String selectedVideoFilePath = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBox();
        videoFileField.setDisable(true);
        backButton.setOnAction(event -> backButtonAction());
    }

    private void backButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageVideo.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Manage Video");
            registerStage.show();

            Stage loginStage = (Stage) videoDescriptionField.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeComboBox() {
        loadKelas();
        chooseClassComboBox.getItems().addAll(kelasMap.values());
        chooseClassComboBox.getSelectionModel().selectFirst();
    }

    private void loadKelas() {
        try (BufferedReader br = new BufferedReader(new FileReader(KELAS_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 4 && values[3].equals(UserSession.getCurrentUserID())) {
                    kelasMap.put(values[0], values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChooseFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.flv", "*.avi", "*.mov")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            selectedVideoFilePath = selectedFile.getAbsolutePath();
            videoFileField.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleUploadButtonAction() {
        String videoTitle = videoTitleField.getText();
        String videoDescription = videoDescriptionField.getText();
        String selectedClass = chooseClassComboBox.getValue();
        String idKelas = getKelasIdByName(selectedClass);
        String videoFileName = generateUniqueFileName(Paths.get(selectedVideoFilePath).getFileName().toString());

        saveVideoData(videoTitle, videoDescription, idKelas, videoFileName);
        saveVideoFile(videoFileName);

        AlertHelper.showSuccessAlert("Upload Berhasil", "Video berhasil diunggah.");
        handleBackButtonAction();
        clearFields();
    }

    private String getKelasIdByName(String className) {
        for (Map.Entry<String, String> entry : kelasMap.entrySet()) {
            if (entry.getValue().equals(className)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return "video_" + System.currentTimeMillis() + extension;
    }

    private void saveVideoData(String title, String description, String classId, String videoFileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VIDEO_FILE, true))) {
            writer.print("\n"  + generateVideoID() + "," + title + "," + description + "," + classId + "," + UPLOADED_VIDEO_FOLDER + "/" + videoFileName);
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Error", "Gagal menyimpan data video.");
        }
    }

    private String generateVideoID() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); 
        return "VID" + randomNumber;
    }
    
    private void saveVideoFile(String videoFileName) {
        File destDir = new File(UPLOADED_VIDEO_FOLDER);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try {
            Files.copy(Paths.get(selectedVideoFilePath), Paths.get(UPLOADED_VIDEO_FOLDER, videoFileName));
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showErrorAlert("Error", "Gagal mengunggah file video.");
        }
    }

    private void clearFields() {
        videoTitleField.setText("");
        videoDescriptionField.setText("");
        videoFileField.setText("");
        chooseClassComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleBackButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageVideo.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Video");
            stage.show();

            Stage currentStage = (Stage) videoDescriptionLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
