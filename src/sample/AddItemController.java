package sample;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import sample.model.Album;
import sample.model.DataSource;
import sample.model.Sing;
import sample.model.Singer;

import java.io.IOException;
import java.util.Optional;

public class AddItemController {

    @FXML
    private TextField txtSinger;

    @FXML
    private TextField txtAlbum;

    @FXML
    private TextField txtSing;

    Singer addedSinger = new Singer();
    Album addedAlbum = new Album();
    Sing addedSing = new Sing();

    public void addNewItem() {

        this.addedSinger.setName(txtSinger.getText());

        this.addedAlbum.setName(txtAlbum.getText());

        this.addedSing.setName(txtSing.getText());

    }

    public Singer sendSingerModel(){
        return this.addedSinger;
    }

    public Album sendAlbumModel(){
        return this.addedAlbum;
    }

    public Sing sendSingModel(){
        return this.addedSing;
    }


}

