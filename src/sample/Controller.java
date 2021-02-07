package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import sample.model.Album;
import sample.model.DataSource;
import sample.model.Sing;
import sample.model.Singer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {

    @FXML
    private TableView table;

    @FXML
    private BorderPane mainFrame;

    @FXML
    public void closeApp(ActionEvent e){
        Platform.exit();
    }

    @FXML
    public void getAllSingers() {
        Task<ObservableList<Singer>> task = new getAllSingers();

        table.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void getSingersAlbums(){
        Singer selectedSinger = (Singer) table.getSelectionModel().getSelectedItem();
        if(selectedSinger == null){
            System.out.println("Singer can't selected.");
            return;
        }

        Task<ObservableList<Album>> task2 = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(DataSource.getInstance().ShowSingerAllAlbums(selectedSinger.getIDSinger()));
            }
        };

        table.itemsProperty().bind(task2.valueProperty());

        new Thread(task2).start();
    }
    @FXML//1
    public void getAlbumsSings(){
        Album selectedAlbum = (Album) table.getSelectionModel().getSelectedItem();
        if(selectedAlbum == null){
            System.out.println("Album can't found");
            return;
        }
        //3
        Task<ObservableList<Sing>> task3 = new Task<ObservableList<Sing>>() {
            @Override
            protected ObservableList<Sing> call() throws Exception {
                return FXCollections.observableArrayList(DataSource.getInstance().ShowAlbumsSings(selectedAlbum.getIDAlbum()));
            }
        };
        table.itemsProperty().bind(task3.valueProperty());

        new Thread(task3).start();
    }

    @FXML
    public void UpdateSinger(){
        //final Singer selectedSinger = (Singer) table.getItems().get(0);
        final Singer selectedSinger = (Singer) table.getSelectionModel().getSelectedItem();

        Task<Boolean> task4 = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().UpdateSingerName(selectedSinger.getIDSinger(),"new Name");
            }
        };
        task4.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                if(task4.valueProperty().get()){
                    selectedSinger.setName("new Name");
                    table.refresh();
                }
            }
        });

        new Thread(task4).start();
    }

    @FXML
    public void addItem(ActionEvent e) throws IOException {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainFrame.getScene().getWindow());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addItemDialog.fxml"));

        dialog.setTitle("Yeni Kayıt Ekle");
        dialog.getDialogPane().setContent(loader.load());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);

        Optional<ButtonType> result = dialog.showAndWait();

        if(result.get() == ButtonType.APPLY){
            AddItemController addItemController = loader.getController();
            addItemController.addNewItem();

            Singer newSinger = addItemController.sendSingerModel();
            Album newAlbum = addItemController.sendAlbumModel();
            Sing newSing = addItemController.sendSingModel();

            System.out.println("Controller: "+ newSinger.getName()+" "+newAlbum.getName()+" "+newSing.getName());

            Task<Boolean> task5 = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return DataSource.getInstance().createNewItem(newSinger,newAlbum,newSing);
                }
            };


            task5.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    if(task5.valueProperty().get()){
                        newSinger.setName(addItemController.sendSingerModel().getName());

                        newAlbum.setName(addItemController.sendAlbumModel().getName());
                        newAlbum.setAlbumSingerID(newSinger.getIDSinger());

                        newSing.setName(addItemController.sendAlbumModel().getName());
                        newSing.setSingAlbumID(newAlbum.getIDAlbum());

                        table.refresh();
                    }
                }
            });

            System.out.println("CONTROLLER IDSinger: " + newSinger.getIDSinger() +
                    " NameSinger: "+ newSinger.getName() + " IDAlbum: " + newAlbum.getIDAlbum() +
                    " NameAlbum: "+ newAlbum.getName() + " IDAlbumSinger: "+ newAlbum.getAlbumSingerID() +
                    " IDSing: "+ newSing.getIDSing() + "NameSing: "+ newSing.getName() + ""+
                    " IDSingAlbum: " + newSing.getSingAlbumID());

            new Thread(task5).start();

}}


class getAllSingers extends Task {
    @Override
    protected ObservableList<Singer> call() throws Exception {
        System.out.println("Thread: "+ Thread.currentThread().getName());
        return FXCollections.observableArrayList(DataSource.getInstance().ShowAllSingers(DataSource.ASCENDING));
    }
}
}

