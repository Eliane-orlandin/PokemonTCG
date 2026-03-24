package com.pokemontcg.controller;

import com.pokemontcg.service.ExportService;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.File;
import java.nio.file.Path;

/**
 * Controlador da tela de Exportação.
 * Gerencia a interação do usuário para salvar o catálogo em JSON ou CSV.
 */
public class ExportController {

    private final ExportService exportService;

    public ExportController() {
        this.exportService = new ExportService();
    }

    @FXML
    public void handleExportJson() {
        exportToFile("JSON", "*.json");
    }

    @FXML
    public void handleExportCsv() {
        exportToFile("CSV", "*.csv");
    }

    private void exportToFile(String format, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Catálogo como " + format);
        fileChooser.setInitialFileName("meu_catalogo_pokemon." + extension.replace("*.", ""));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(format + " Files", extension));
        
        // Pega o estágio (Stage) atual
        Stage stage = (Stage) javafx.stage.Window.getWindows().filtered(w -> w.isFocused()).get(0);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Path path = file.toPath();
                if (format.equalsIgnoreCase("JSON")) {
                    exportService.exportCatalogToJson(path);
                } else {
                    exportService.exportCatalogToCsv(path);
                }
                
                showSuccessAlert(format, file.getName());
            } catch (Exception e) {
                showErrorAlert("Erro ao exportar", e.getMessage());
            }
        }
    }

    private void showSuccessAlert(String format, String fileName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportação Concluída");
        alert.setHeaderText(null);
        alert.setContentText("Catálogo exportado com sucesso como " + format + "!\nArquivo: " + fileName);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
