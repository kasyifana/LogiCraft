package Controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ProviderMenuController {

    @FXML
    private Label menuLabel;

    @FXML
    private Button manageQueueButton;

    @FXML
    private Button manageDataPengajarButton;

    @FXML
    private Button manageDataMataPelajaranButton;

    public void initialize() {
    	manageDataMataPelajaranButton.setOnAction(event -> openManageDataPelajaranPage());
    	manageDataPengajarButton.setOnAction(event -> openManageDataPengajarButton());
    	manageQueueButton.setOnAction(event -> openManageQueueButton());
    }
    
    private void openManageQueueButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageClassQueue.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Manage Antrian Kelas");
            registerStage.show();

            Stage loginStage = (Stage) manageDataMataPelajaranButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openManageDataPelajaranPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageMataPelajaran.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Manage Mata Pelajaran");
            registerStage.show();

            Stage loginStage = (Stage) manageDataMataPelajaranButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openManageDataPengajarButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ManageDataPengajar.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Manage Data Pengajar");
            registerStage.show();

            Stage loginStage = (Stage) manageDataMataPelajaranButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
