package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

//W wątku main powoływany jest wątek aplikacji Javafx
public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //początkowe rozstawienie elementów sceny jest odczytywane z pliku fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Scene.fxml"));
        //ustawienie opcji głównego ekranu
        Scene scene = new Scene(fxmlLoader.load(), 1160, 720);
        stage.setTitle("Loading Simulation");
        stage.setScene(scene);
        stage.setResizable(false);

        Circle circle = new Circle();
        circle.setCenterX(100);
        circle.setCenterY(100);
        circle.setVisible(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}