package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import Helpers.UserSession;

public class AccessVideoController implements Initializable {

    @FXML
    private VBox rootVBox;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView cornerImageView;

    @FXML
    private Label watchVideoLabel;

    @FXML
    private ImageView logoImageView;

    @FXML
    private Label daftarKelasBerlanggananLabel;

    @FXML
    private ScrollPane scrollPaneDaftarKelas;

    @FXML
    private ListView<String> listViewDaftarKelas;

    @FXML
    private Label videoKelasLabel;

    @FXML
    private ScrollPane scrollPaneDaftarVideo;

    @FXML
    private Button subscribeNewClassButton;
    
    @FXML
    private Button backButton;

    private static final String SUBSCRIPTION_FILE = "database/subscription.csv";
    private static final String USER_KELAS_RELATION_FILE = "database/user_kelas_relation.csv";
    private static final String KELAS_FILE = "database/kelas.csv";
    private static final String MATA_PELAJARAN_FILE = "database/mata_pelajaran.csv";
    private static final String VIDEO_FILE = "database/video.csv";

    private Map<String, String> mataPelajaranMap = new HashMap<>();
    private Map<String, String> kelasMap = new HashMap<>();
    private Map<String, String> videoMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMataPelajaran();
        loadKelas();
        initializeListView();
        subscribeNewClassButton.setOnAction(event -> openSubscribeNewClass());
        listViewDaftarKelas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> loadVideos(newValue));
    }
    
    private void openSubscribeNewClass() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Subscribe.fxml"));
            Parent root = loader.load();

            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(root));
            registerStage.setTitle("Subscription Form");
            registerStage.show();

            Stage loginStage = (Stage) subscribeNewClassButton.getScene().getWindow();
            loginStage.close();
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
                if (values.length >= 4) {
                    kelasMap.put(values[0], values[2]); // Storing id_kelas and id_mata_pelajaran
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeListView() {
        listViewDaftarKelas.getItems().clear();
        String currentUserId = UserSession.getCurrentUserID();
        try (BufferedReader br = new BufferedReader(new FileReader(SUBSCRIPTION_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 7 && values[0].equals(currentUserId) && values[6].equalsIgnoreCase("true")) {
                    String mataPelajaranId = values[1];
                    String mataPelajaranName = mataPelajaranMap.get(mataPelajaranId);
                    if (mataPelajaranName != null) {
                        listViewDaftarKelas.getItems().add(mataPelajaranName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadVideos(String mataPelajaranName) {
        if (mataPelajaranName == null) {
            return;
        }

        String mataPelajaranId = mataPelajaranMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(mataPelajaranName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (mataPelajaranId == null) {
            return;
        }

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        String currentUserId = UserSession.getCurrentUserID();
        try (BufferedReader br = new BufferedReader(new FileReader(USER_KELAS_RELATION_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 2 && values[0].equals(currentUserId)) {
                    String idKelas = values[1];
                    if (kelasMap.get(idKelas).equals(mataPelajaranId)) {
                        loadVideoThumbnails(idKelas, gridPane);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPaneDaftarVideo.setContent(gridPane);
    }

    private void loadVideoThumbnails(String idKelas, GridPane gridPane) {
        int rowIndex = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(VIDEO_FILE))) {
            String line;
            boolean firstLineSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!firstLineSkipped) {
                    firstLineSkipped = true;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length >= 5 && values[3].equals(idKelas)) {
                    ImageView thumbnailImageView = new ImageView(new Image(getClass().getResource("/assets/thumbnail_icon.png").toExternalForm()));
                    thumbnailImageView.setFitWidth(150);
                    thumbnailImageView.setFitHeight(100);
                    thumbnailImageView.setPreserveRatio(true);
                    thumbnailImageView.setSmooth(true);
                    thumbnailImageView.setCache(true);

                    Label titleLabel = new Label(values[1]);
                    titleLabel.setStyle("-fx-font-weight: bold;");

                    VBox videoBox = new VBox(thumbnailImageView, titleLabel);
                    videoBox.setSpacing(5);
                    videoBox.setPadding(new Insets(5, 5, 5, 5));
                    videoBox.setStyle("-fx-background-color: #CCCCCC; -fx-alignment: center;");

                    videoBox.setOnMouseClicked(event -> playVideo(values[4]));

                    gridPane.add(videoBox, rowIndex % 3, rowIndex / 3);
                    rowIndex++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void playVideo(String videoFileLink) {
        File file = new File(videoFileLink);
        Media media = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        Stage stage = new Stage();
        Scene scene = new Scene(new StackPane(mediaView), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Video Player");
        stage.show();

        mediaPlayer.play();
    }
}
