package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.model.DataSource;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.getAllSingers();

        primaryStage.setTitle("Music Database");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }
    @Override
    public void init() throws Exception {
        super.init();
        if(!DataSource.getInstance().OpenDB()){
            System.out.println("Can't connect to DB.");
            Platform.exit();
        }
    }
    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.getInstance().CloseDB();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
