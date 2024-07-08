package Controller;

import Model.GuruMataPelajaranRelation;
import Model.MataPelajaran;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import Helpers.AlertHelper;

public class ManageDataPengajarController implements Initializable {

    @FXML
    private Label manageDataPengajarLabel;

    @FXML
    private Button simpanButton;

    @FXML
    private Button backButton;

    @FXML
    private TableView<GuruMataPelajaranRelation> dataPengajarTable;

    @FXML
    private TableColumn<GuruMataPelajaranRelation, String> idGuruColumn;

    @FXML
    private TableColumn<GuruMataPelajaranRelation, String> namaGuruColumn;

    @FXML
    private TableColumn<GuruMataPelajaranRelation, String> mataPelajaranColumn;

    @FXML
    private TableView<MataPelajaran> dataMataPelajaranTabel;

    @FXML
    private TableColumn<MataPelajaran, String> idMataPelajaranColumn;

    @FXML
    private TableColumn<MataPelajaran, String> namaMataPelajaranColumn;

    @FXML
    private TextField idGuruField;

    @FXML
    private TextField idKelasField;

    private final ObservableList<GuruMataPelajaranRelation> dataPengajar = FXCollections.observableArrayList();
    private final ObservableList<MataPelajaran> dataMataPelajaran = FXCollections.observableArrayList();
    private final Map<String, String> pengajarMap = new HashMap<>();
    private final Map<String, String> mataPelajaranMap = new HashMap<>();
    private final Set<String> mataPelajaranAssignedSet = new HashSet<>();
    private final Set<String> pengajarAssignedSet = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTables();
        loadPengajarData();
        loadMataPelajaranData();
        loadGuruMataPelajaranRelationData();
        addTableSelectionListeners();
        backButton.setOnAction(event -> backButtonAction());
    }

    private void backButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ProviderMenu.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu");
            stage.show();

            Stage currentStage = (Stage) manageDataPengajarLabel.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTables() {
        idGuruColumn.setCellValueFactory(new PropertyValueFactory<>("idGuru"));
        namaGuruColumn.setCellValueFactory(new PropertyValueFactory<>("namaGuru"));
        mataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("namaMataPelajaran"));
        dataPengajarTable.setItems(dataPengajar);

        idMataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaMataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("nama"));
        dataMataPelajaranTabel.setItems(dataMataPelajaran);
    }

    private void loadPengajarData() {
        String filePath = "database/pengajar.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2) {
                    pengajarMap.put(values[0], values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMataPelajaranData() {
        String filePath = "database/mata_pelajaran.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
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

    private void loadGuruMataPelajaranRelationData() {
        String filePath = "database/guru_mata_pelajaran_relation.csv";
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataPengajar.clear();
        mataPelajaranAssignedSet.clear();
        pengajarAssignedSet.clear();

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
                    String idGuru = values[0];
                    String idMataPelajaran = values[1];
                    pengajarAssignedSet.add(idGuru);
                    mataPelajaranAssignedSet.add(idMataPelajaran);
                    String namaGuru = pengajarMap.getOrDefault(idGuru, "Belum Ada Mata Pelajaran");
                    String namaMataPelajaran = mataPelajaranMap.getOrDefault(idMataPelajaran, "Unknown");
                    dataPengajar.add(new GuruMataPelajaranRelation(idGuru, namaGuru, idMataPelajaran, namaMataPelajaran));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadAvailableMataPelajaran();
        loadAvailablePengajar();
    }

    private void loadAvailableMataPelajaran() {
        dataMataPelajaran.clear();
        for (Map.Entry<String, String> entry : mataPelajaranMap.entrySet()) {
            if (!mataPelajaranAssignedSet.contains(entry.getKey())) {
                dataMataPelajaran.add(new MataPelajaran(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void loadAvailablePengajar() {
        dataPengajar.clear();
        for (Map.Entry<String, String> entry : pengajarMap.entrySet()) {
            if (!pengajarAssignedSet.contains(entry.getKey())) {
                dataPengajar.add(new GuruMataPelajaranRelation(entry.getKey(), entry.getValue(), "", "Unknown"));
            }
        }
    }

    @FXML
    private void handleSimpanButtonAction() {
        String idGuru = idGuruField.getText();
        String idMataPelajaran = idKelasField.getText();

        if (idGuru.isEmpty() || idMataPelajaran.isEmpty()) {
            AlertHelper.showErrorAlert("Error", "ID Guru dan ID Mata Pelajaran tidak boleh kosong.");
            return;
        }

        saveToCSV(idGuru, idMataPelajaran);
        createNewClass(idGuru, idMataPelajaran);
        loadGuruMataPelajaranRelationData();
    }

    private void saveToCSV(String idGuru, String idMataPelajaran) {
        String filePath = "database/guru_mata_pelajaran_relation.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.print("\n" + idGuru + "," + idMataPelajaran);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewClass(String idGuru, String idMataPelajaran) {
        String namaMataPelajaran = mataPelajaranMap.getOrDefault(idMataPelajaran, "Unknown");
        String namaKelas = namaMataPelajaran + " A";
        String idKelas = generateClassId();
        String filePath = "database/kelas.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.print("\n" + idKelas + "," + namaKelas + "," + idMataPelajaran + "," + idGuru + ",0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateClassId() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        return "KELAS" + randomNumber;
    }

    private void addTableSelectionListeners() {
        dataPengajarTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idGuruField.setText(newValue.getIdGuru());
                idKelasField.setText(newValue.getIdMataPelajaran());
            }
        });

        dataMataPelajaranTabel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idKelasField.setText(newValue.getId());
            }
        });
    }
}
