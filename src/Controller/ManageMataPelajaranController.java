package Controller;

import Model.MataPelajaran;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.util.Random;

import Helpers.AlertHelper;

public class ManageMataPelajaranController {

    @FXML
    private Label manageMataPelajaranLabel;

    @FXML
    private Button tambahButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<MataPelajaran> mataPelajaranDataTable;

    @FXML
    private TableColumn<MataPelajaran, String> idMataPelajaranColumn;

    @FXML
    private TableColumn<MataPelajaran, String> namaMataPelajaranColumn;

    @FXML
    private TextField namaMataPelajaranField;

    @FXML
    private Label namaMataPelajaranLabel;

    @FXML
    private Label mataPelajaranBaruLabel;

    private final ObservableList<MataPelajaran> data = FXCollections.observableArrayList();

    public void initialize() {
        initializeTable();
        loadDataFromCSV();
        backButton.setOnAction(event -> backButtonAction());
    }

    private void backButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ProviderMenu.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Menu");
            registerStage.show();

            Stage loginStage = (Stage) mataPelajaranBaruLabel.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeTable() {
        idMataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaMataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        mataPelajaranDataTable.setItems(data);
    }
    
    private void loadDataFromCSV() {
        String filePath = "database/mata_pelajaran.csv";
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        data.clear(); 

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String id = values[0];
                    String nama = values[1];
                    data.add(new MataPelajaran(id, nama));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTambahButtonAction() {
        String nama = namaMataPelajaranField.getText();
        if (nama.isEmpty()) {
            AlertHelper.showErrorAlert("Error", "Nama mata pelajaran tidak boleh kosong.");
            return;
        }

        String id = generateId();
        MataPelajaran mataPelajaran = new MataPelajaran(id, nama);
        data.add(mataPelajaran);
        saveToCSV(mataPelajaran);
        namaMataPelajaranField.clear();
        loadDataFromCSV();  
    }

    private String generateId() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); 
        return "MP" + randomNumber;
    }

    private void saveToCSV(MataPelajaran mataPelajaran) {
        String filePath = "database/mata_pelajaran.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.print("\n" + mataPelajaran.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
