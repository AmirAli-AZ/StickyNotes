package com.amirali.stickynotes;

import com.amirali.stickynotes.model.Note;
import com.amirali.stickynotes.utils.DBManager;
import com.amirali.stickynotes.utils.OSUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class NotesListController implements Initializable {

    @FXML
    private ListView<Note> listview;

    @FXML
    private TextField searchField;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private Button createBtn;

    private FilteredList<Note> filteredList;

    private final ObservableList<Note> baseList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadNotes();
        listview.setCellFactory(noteListView -> {
            NoteCell cell = new NoteCell();
            cell.setPadding(new Insets(5));
            return cell;
        });
        searchField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (filteredList != null) {
                filteredList.setPredicate(note -> {
                    if (!note.isRemoved()) {
                        if (newValue.isEmpty())
                            return true;
                        else return note.getContent().contains(newValue.toLowerCase());
                    }
                    return false;
                });
                listview.setItems(FXCollections.observableArrayList(filteredList));
            }
        });
    }

    public void newNote(ActionEvent event) throws IOException {
        Stage window = (Stage) root.getScene().getWindow();
        Stage stage = new Stage();
        stage.setTitle("StickyNotes");
        FXMLLoader loader = new FXMLLoader(StickyNotes.class.getResource("sticky-notes-view.fxml"));
        Scene scene = new Scene(loader.load());
        StickyNotesController controller = loader.getController();
        controller.setNotesListItemDisableProperty(window.showingProperty());
        scene.getStylesheets().add(Objects.requireNonNull(StickyNotes.class.getResource("themes/light-theme.css")).toExternalForm());
        if (OSUtils.INSTANCE.get() == OSUtils.OS.WINDOWS) {
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
        } else {
            stage.initStyle(StageStyle.UNDECORATED);
        }
        stage.setOnCloseRequest(windowEvent -> {
            controller.save();
            loadNotes();
        });
        stage.setScene(scene);
        stage.show();
    }

    public void loadNotes() {
        Task<Void> task = new Task<>() {
            ArrayList<Note> notes;

            @Override
            protected Void call() throws Exception {
                DBManager manager = DBManager.INSTANCE.initDB();
                notes = manager.selectAll();

                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    baseList.clear();
                    baseList.addAll(notes);
                    listview.setItems(baseList);
                    filteredList = new FilteredList<>(baseList , note -> true);
                });
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}
