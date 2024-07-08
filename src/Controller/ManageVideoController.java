package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import Helpers.UserSession;
import Model.VideoData;

public class ManageVideoController implements Initializable {

    @FXML
    private ComboBox<String> chooseClassComboBox;

    @FXML
    private Label chooseClassLabel;

    @FXML
    private TableView<VideoData> videoDataTable;

    @FXML
    private TableColumn<VideoData, String> idVideoColumn;

    @FXML
    private TableColumn<VideoData, String> judulVideoColumn;

    @FXML
    private TableColumn<VideoData, String> deskripsiVideoColumn;

    @FXML
    private TableColumn<VideoData, String> namaKelasColumn;

    @FXML
    private TableColumn<VideoData, String> mataPelajaranColumn;

    @FXML
    private TableColumn<VideoData, Void> aksiColumn;

    private ObservableList<VideoData> videoList = FXCollections.observableArrayList();
    private Map<String, String> classMap = new HashMap<>();
    private Map<String, String> mataPelajaranMap = new HashMap<>();

    private static final String KELAS_FILE = "database/kelas.csv";
    private static final String VIDEO_FILE = "database/video.csv";
    private static final String MATA_PELAJARAN_FILE = "database/mata_pelajaran.csv";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClasses();
        loadMataPelajaran();
        initializeComboBox();
        initializeTableView();
    }

    private void loadClasses() {
        try (BufferedReader br = new BufferedReader(new FileReader(KELAS_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5 && values[3].equals(UserSession.getCurrentUserID())) {
                    classMap.put(values[0], values[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void initializeComboBox() {
        chooseClassComboBox.getItems().addAll(classMap.values());
        chooseClassComboBox.setOnAction(event -> loadVideos());
    }

    private void loadVideos() {
        videoList.clear();
        String selectedClass = chooseClassComboBox.getValue();
        String selectedClassId = getClassId(selectedClass);

        try (BufferedReader br = new BufferedReader(new FileReader(VIDEO_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5 && values[3].equals(selectedClassId)) {
                    String mataPelajaran = getMataPelajaranName(selectedClassId);
                    videoList.add(new VideoData(values[0], values[1], values[2], selectedClass, mataPelajaran, values[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoDataTable.setItems(videoList);
    }

    private String getClassId(String className) {
        for (Map.Entry<String, String> entry : classMap.entrySet()) {
            if (entry.getValue().equals(className)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private String getMataPelajaranName(String classId) {
        try (BufferedReader br = new BufferedReader(new FileReader(KELAS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 4 && values[0].equals(classId)) {
                    return mataPelajaranMap.getOrDefault(values[2], "Unknown");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void initializeTableView() {
        idVideoColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        judulVideoColumn.setCellValueFactory(new PropertyValueFactory<>("judul"));
        deskripsiVideoColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        namaKelasColumn.setCellValueFactory(new PropertyValueFactory<>("className"));
        mataPelajaranColumn.setCellValueFactory(new PropertyValueFactory<>("mataPelajaran"));

        setupActionColumn();

        videoDataTable.setItems(videoList);
    }

    private void setupActionColumn() {
        aksiColumn.setCellFactory(col -> new TableCell<VideoData, Void>() {
            private final Button viewButton = new Button("View");
            private final Button deleteButton = new Button("Delete");

            {
                viewButton.setPrefWidth(75);
                deleteButton.setPrefWidth(75);

                viewButton.setOnAction(event -> {
                    VideoData video = getTableView().getItems().get(getIndex());
                    playVideo(video.getVideoFileLink());
                });

                deleteButton.setOnAction(event -> {
                    VideoData video = getTableView().getItems().get(getIndex());
                    deleteVideo(video);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(10, viewButton, deleteButton));
                }
            }
        });
    }

    private void playVideo(String videoFileLink) {
        File file = new File(videoFileLink);
        Media media = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        javafx.scene.Scene scene = new javafx.scene.Scene(new javafx.scene.layout.StackPane(mediaView), 800, 600);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Video Player");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();

        mediaPlayer.play();
    }
    
    @FXML
    private void handleUploadNewVideoButton() {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UploadVideo.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Upload Video");
            registerStage.show();

            Stage loginStage = (Stage) videoDataTable.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void deleteVideo(VideoData video) {
        videoList.remove(video);

        try {
            File inputFile = new File(VIDEO_FILE);
            File tempFile = new File("temp_video.csv");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5 && !values[0].equals(String.valueOf(video.getId()))) {
                    writer.write(line + "\n");
                }
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
}
