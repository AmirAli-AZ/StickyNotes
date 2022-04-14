package com.amirali.stickynotes;

import com.amirali.stickynotes.model.Note;
import com.amirali.stickynotes.utils.DBManager;
import com.amirali.stickynotes.utils.OSUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class NoteCell extends ListCell<Note> {

    @FXML
    private Label content;

    @FXML
    private Label date;

    @FXML
    private StackPane root;

    private FXMLLoader loader;

    @Override
    protected void updateItem(Note note, boolean b) {
        super.updateItem(note, b);

        setText(null);

        if (b || note == null) {
            setGraphic(null);
        }else {
            if (loader == null) {
                try {
                    loader = new FXMLLoader(getClass().getResource("list-cell.fxml"));
                    loader.setController(this);
                    loader.load();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            createMenu(note);
            root.getStylesheets().add(Objects.requireNonNull(getClass().getResource("themes/list-cell-light-theme.css")).toExternalForm());

            date.setText(formatDate(note.getDate()));
            content.setText(note.getContent());
            root.setStyle("-fx-background-color : " + note.getColor() + ";");

            setGraphic(root);
        }
    }

    private String formatDate(long dateMillis) {;
        Date date = new Date(dateMillis);
        Calendar currentDateMinusYear = Calendar.getInstance();
        currentDateMinusYear.add(Calendar.YEAR , -1);
        String pattern;
        if (date.before(currentDateMinusYear.getTime()))
            pattern = "dd/MM/yyyy";
        else
            pattern = "MMMM d";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    private void createMenu(Note note) {
        ContextMenu menu = new ContextMenu();

        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(event -> {
            try {
                Stage stage = new Stage();
                stage.setTitle("StickyNotes");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("sticky-notes-view.fxml"));
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("themes/light-theme.css")).toExternalForm());
                if (OSUtils.INSTANCE.get() == OSUtils.OS.WINDOWS) {
                    scene.setFill(Color.TRANSPARENT);
                    stage.initStyle(StageStyle.TRANSPARENT);
                }else {
                    stage.initStyle(StageStyle.UNDECORATED);
                }
                StickyNotesController controller = loader.getController();
                controller.setNoteData(note);
                stage.setOnCloseRequest(windowEvent -> controller.save());
                stage.setScene(scene);
                stage.show();

                ((Stage) getScene().getWindow()).close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(event -> {
            note.remove(true);
            DBManager manager = DBManager.INSTANCE.initDB();
            manager.deleteByID(note.getId());
            getListView().getItems().remove(note);
        });

        menu.getItems().addAll(edit , delete);

        root.setOnContextMenuRequested(contextMenuEvent -> menu.show(root , contextMenuEvent.getScreenX() , contextMenuEvent.getScreenY()));
        root.setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && menu.isShowing())
                menu.hide();
        });
    }
}
