package Helpers;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public final class AlertHelper {

    private AlertHelper() {
    }

    public static void showInformationAlert(String title, String header) {
        showAlert(AlertType.ERROR, title, header);
    }

    public static void showWarningAlert(String title, String header) {
        showAlert(AlertType.ERROR, title, header);
    }

    public static void showErrorAlert(String title, String header) {
        showAlert(AlertType.ERROR, title, header);
    }
    
    public static void showSuccessAlert(String title, String header) {
        showAlert(AlertType.INFORMATION, title, header);
    }

    public static void showAlert(AlertType type, String title, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);

        alert.showAndWait();
    }
}
