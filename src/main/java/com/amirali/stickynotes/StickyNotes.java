package com.amirali.stickynotes;

import com.amirali.stickynotes.utils.OSUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class StickyNotes extends Application {

    @Override
    public void start(Stage stage) throws Exception {
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

    public static void main(String[] args) {
        launch(args);
    }
}
