package Controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Helpers.AlertHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ManageClassQueueController {

    @FXML
    private TableView<ObservableList<String>> antrianDataLabel;

    @FXML
    private TableColumn<ObservableList<String>, String> idUserColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> namaUserColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> subscriptionColumn;

    @FXML
    private TableView<ObservableList<String>> daftarKelasDataTabel;

    @FXML
    private TableColumn<ObservableList<String>, String> statusKelasColumn;

    @FXML
    private ComboBox<String> mataPelajaranStatusComboBox;

    @FXML
    private ComboBox<String> mataPelajaranBaruComboBox;

    @FXML
    private TextField idSiswaField;

    @FXML
    private TextField namaSiswaField;

    @FXML
    private TextField idKelasField;

    @FXML
    private TextField namaKelasField;

    @FXML
    private TextField namaKelasBaruField;

    @FXML
    private Button assignButton;

    @FXML
    private Button buatKelasBaruButton;

    @FXML
    private Button backButton;

    ObservableList<ObservableList<String>> classData = FXCollections.observableArrayList();
    private Map<String, String> mataPelajaranMap = new HashMap<>();
    private Map<String, String> pengajarMap = new HashMap<>();

    private static final String SUBSCRIPTION_FILE = "database/subscription.csv";
    private static final String MATA_PELAJARAN_FILE = "database/mata_pelajaran.csv";
    private static final String KELAS_FILE = "database/kelas.csv";
    private static final String PENGAJAR_FILE = "database/pengajar.csv";
    private static final String USER_FILE = "database/user.csv";
    private static final String USER_KELAS_RELATION_FILE = "database/user_kelas_relation.csv";
    private static final int MAX_CLASS_SIZE = 20;

    @FXML
    private TableColumn<ObservableList<String>, String> idKelasColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> namaKelasColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> mataPelajaranColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> namaGuruColumn;

    @FXML
    private TableColumn<ObservableList<String>, String> jumlahSiswaColumn;

    
    public void initialize() {
        initializeTables();
        loadMataPelajaran();
        loadPengajar();
        loadAntrianData();
        initializeComboBoxes();
        addTableSelectionListeners();
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

            Stage loginStage = (Stage) buatKelasBaruButton.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeTables() {
        idUserColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(0)));
        namaUserColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(1)));
        subscriptionColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(2)));

        idKelasColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(0)));
        namaKelasColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(1)));
        mataPelajaranColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(2)));
        namaGuruColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(3)));
        jumlahSiswaColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().get(4)));
    }

    private void initializeComboBoxes() {
        mataPelajaranStatusComboBox.getItems().addAll(mataPelajaranMap.values());
        mataPelajaranBaruComboBox.getItems().addAll(mataPelajaranMap.values());

        mataPelajaranStatusComboBox.setOnAction(event -> loadKelasData());
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

    private void loadPengajar() {
        try (BufferedReader br = new BufferedReader(new FileReader(PENGAJAR_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
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

    private void loadAntrianData() {
        ObservableList<ObservableList<String>> userData = FXCollections.observableArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBSCRIPTION_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 7 && values[6].equalsIgnoreCase("false")) {
                    String idUser = values[0];
                    String idMataPelajaran = values[1];
                    String namaUser = getUserNameById(idUser);
                    String namaMataPelajaran = getMataPelajaranNameById(idMataPelajaran);

                    if (!namaUser.isEmpty() && !namaMataPelajaran.isEmpty()) {
                        userData.add(FXCollections.observableArrayList(idUser, namaUser, namaMataPelajaran));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        antrianDataLabel.setItems(userData);
    }


    private String getMataPelajaranNameById(String mataPelajaranId) {
        try (BufferedReader br = new BufferedReader(new FileReader(MATA_PELAJARAN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2 && values[0].equals(mataPelajaranId)) {
                    return values[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    private void loadKelasData() {
    	classData.clear();
        
        String selectedMataPelajaran = mataPelajaranStatusComboBox.getValue();
        String selectedMataPelajaranId = getMataPelajaranId(selectedMataPelajaran);

        try (BufferedReader br = new BufferedReader(new FileReader(KELAS_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5 && values[2].equals(selectedMataPelajaranId)) {
                    String namaMataPelajaran = mataPelajaranMap.getOrDefault(values[2], "Unknown");
                    String namaGuru = pengajarMap.getOrDefault(values[3], "Unknown");
                    classData.add(FXCollections.observableArrayList(values[0], values[1], namaMataPelajaran, namaGuru, values[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        daftarKelasDataTabel.setItems(classData);
    }



    private String getMataPelajaranId(String namaMataPelajaran) {
        for (Map.Entry<String, String> entry : mataPelajaranMap.entrySet()) {
            if (entry.getValue().equals(namaMataPelajaran)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void addTableSelectionListeners() {
        antrianDataLabel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idSiswaField.setText(newValue.get(0));
                namaSiswaField.setText(getUserNameById(newValue.get(0)));
            }
        });

        daftarKelasDataTabel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                idKelasField.setText(newValue.get(0));
                namaKelasField.setText(newValue.get(1));
            }
        });
    }

    private String getUserNameById(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2 && values[0].equals(userId)) {
                    return values[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @FXML
    private void handleAssignButtonAction() {
        String idKelas = idKelasField.getText();
        int jumlahSiswa = getJumlahSiswa(idKelas);

        if (jumlahSiswa >= MAX_CLASS_SIZE) {
            AlertHelper.showErrorAlert("Gagal", "Kelas Sudah Penuh");
            return;
        }

        updateSubscriptionStatus(idSiswaField.getText());
        addUserToClass(idSiswaField.getText(), idKelas);
        updateJumlahSiswa(idKelas);
        loadAntrianData();
        loadKelasData();
        
        AlertHelper.showSuccessAlert("Berhasil", "Berhasil mendaftarkan siswa ke kelas!");
        clearFields();
    }

    private void clearFields() {
    	idSiswaField.setText("");
        namaSiswaField.setText("");
        idKelasField.setText("");
        namaKelasField.setText("");
    }
    
    private int getJumlahSiswa(String idKelas) {
        try (BufferedReader br = new BufferedReader(new FileReader(KELAS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && values[0].equals(idKelas)) {
                    return Integer.parseInt(values[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateSubscriptionStatus(String userId) {
        try {
            File inputFile = new File(SUBSCRIPTION_FILE);
            File tempFile = new File("temp_subscription.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 6 && values[0].equals(userId)) {
                    values[6] = "true";
                    line = String.join(",", values);
                }
                writer.write(line + "\n");
            }

            writer.close();
            reader.close();

            if (!inputFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addUserToClass(String userId, String classId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_KELAS_RELATION_FILE, true))) {
            writer.write("\n" + userId + "," + classId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateJumlahSiswa(String classId) {
        try {
            File inputFile = new File(KELAS_FILE);
            File tempFile = new File("temp_kelas.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && values[0].equals(classId)) {
                    values[4] = String.valueOf(Integer.parseInt(values[4]) + 1);
                    line = String.join(",", values);
                }
                writer.write(line + "\n");
            }

            writer.close();
            reader.close();

            if (!inputFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuatKelasBaruButtonAction() {
        String namaKelasBaru = namaKelasBaruField.getText();
        String mataPelajaranId = getMataPelajaranId(mataPelajaranBaruComboBox.getValue());
        String pengajarId = getPengajarIdByMataPelajaranId(mataPelajaranId);
        String newClassId = generateClassId();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(KELAS_FILE, true))) {
            writer.write("\n" + newClassId + "," + namaKelasBaru + "," + mataPelajaranId + "," + pengajarId + ",0");
            daftarKelasDataTabel.getItems().clear(); 
            loadKelasData(); 
            namaKelasBaruField.setText("");
            AlertHelper.showSuccessAlert("Berhasil", "Kelas berhasil dibuat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateClassId() {
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000);
        return "KELAS" + randomNumber;
    }

    private String getPengajarIdByMataPelajaranId(String mataPelajaranId) {
        try (BufferedReader br = new BufferedReader(new FileReader("database/guru_mata_pelajaran_relation.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 2 && values[1].equals(mataPelajaranId)) {
                    return values[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}