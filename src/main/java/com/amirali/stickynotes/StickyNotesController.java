package com.amirali.stickynotes;

import com.amirali.stickynotes.model.Note;
import com.amirali.stickynotes.utils.ColorNotes;
import com.amirali.stickynotes.utils.DBManager;
import com.amirali.stickynotes.utils.OSUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StickyNotesController implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private TextArea editor;

    @FXML
    private HBox header;

    @FXML
    private Menu colorMenu;

    @FXML
    private CheckMenuItem cbLockWindow;

    @FXML
    private MenuItem notesListItem;

    private double xOffset, yOffset;
    private final List<ColorNotes> colorNotes = Arrays.asList(ColorNotes.values());
    private ColorNotes currentColor;
    private final BooleanProperty lockWindowProperty = new SimpleBooleanProperty();
    private Note noteData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (OSUtils.INSTANCE.get() == OSUtils.OS.WINDOWS)
            root.setId("note-window");

        for (ColorNotes colorNotes : colorNotes)
            colorMenu.getItems().add(createItem(colorNotes));

        lockWindowProperty.bind(cbLockWindow.selectedProperty());
        lockWindowProperty.addListener((observableValue, aBoolean, t1) -> ((Stage) root.getScene().getWindow()).setAlwaysOnTop(t1));

        if (noteData == null) {
            Random random = new Random();
            currentColor = colorNotes.get(random.nextInt(colorNotes.size()));
            header.setStyle("-fx-background-color : " + currentColor.getValue() + ";");
        }
    }

    public void close(ActionEvent event) {
        Stage window = (Stage) root.getScene().getWindow();
        window.fireEvent(new WindowEvent(window , WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void onHeaderDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && !lockWindowProperty.get()) {
            Window window = root.getScene().getWindow();
            window.setX(mouseEvent.getScreenX() + xOffset);
            window.setY(mouseEvent.getScreenY() + yOffset);
        }
    }

    public void onHeaderPressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY && !lockWindowProperty.get()) {
            Window window = root.getScene().getWindow();
            xOffset = window.getX() - mouseEvent.getScreenX();
            yOffset = window.getY() - mouseEvent.getScreenY();
        }
    }

    private CustomMenuItem createItem(ColorNotes colorNotes) {
        String value = colorNotes.getValue();
        String name = colorNotes.getColorName();

        CustomMenuItem item = new CustomMenuItem();
        item.setOnAction(event -> {
            currentColor = colorNotes;
            header.setStyle("-fx-background-color : " + value + ";");
        });
        item.setHideOnClick(true);
        HBox itemRoot = new HBox(3);
        itemRoot.setAlignment(Pos.CENTER_LEFT);
        Rectangle colorPreview = new Rectangle(10, 10, Color.valueOf(value));
        Label colorName = new Label(name);
        itemRoot.getChildren().addAll(colorPreview, colorName);
        item.setContent(itemRoot);

        return item;
    }

    public void newNote(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("StickyNotes");
        FXMLLoader loader = new FXMLLoader(StickyNotes.class.getResource("sticky-notes-view.fxml"));
        Scene scene = new Scene(loader.load());
        StickyNotesController controller = loader.getController();
        scene.getStylesheets().add(Objects.requireNonNull(StickyNotes.class.getResource("themes/light-theme.css")).toExternalForm());
        if (OSUtils.INSTANCE.get() == OSUtils.OS.WINDOWS) {
            scene.setFill(Color.TRANSPARENT);
            stage.initStyle(StageStyle.TRANSPARENT);
        }else {
            stage.initStyle(StageStyle.UNDECORATED);
        }
        stage.setOnCloseRequest(windowEvent -> controller.save());
        stage.setScene(scene);
        stage.show();
    }

    public void setNoteData(Note noteData) {
        this.noteData = noteData;
        if (noteData != null) {
            header.setStyle("-fx-background-color : " + noteData.getColor() + ";");
            editor.setText(noteData.getContent());
            currentColor = ColorNotes.Companion.findByColorValue(noteData.getColor());
        }
    }

    public void save() {
        DBManager manager = DBManager.INSTANCE.initDB();
        String text = editor.getText();
        String colorValue = currentColor.getValue();
        if (noteData == null) {
            if (!text.isEmpty())
                manager.insertNewData(colorValue, text);
        } else if (!noteData.getColor().equals(colorValue) || !noteData.getContent().equals(text)) {
            manager.updateByID(noteData.getId(), colorValue, text);
        }
    }

    public void notesList(ActionEvent event) throws IOException {
        save();

        Stage stage = new Stage();
        stage.setTitle("StickyNotes");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("notes-list-view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("themes/light-theme.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();

        ((Stage) root.getScene().getWindow()).close();
    }

    public void setNotesListItemDisableProperty(ReadOnlyBooleanProperty listShowingProperty) {
        notesListItem.disableProperty().bind(listShowingProperty);
    }
}
