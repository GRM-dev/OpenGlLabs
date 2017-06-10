package eu.grmdev.senryaku.jfx.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import eu.grmdev.senryaku.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class StartScreenController implements Initializable {
	@FXML
	private Button startButton;
	@FXML
	private Button settingsButton;
	@FXML
	private Button closeButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startButton.setOnAction(event -> start(event));
		settingsButton.setOnAction(event -> settings(event));
		closeButton.setOnAction(event -> close(event));
	}
	
	private void start(ActionEvent event) {
		System.out.println("Launcher: Starting Game");
		Main.startGame();
	}
	
	private void settings(ActionEvent event) {
		System.out.println("Launcher: Settings");
		
	}
	
	private void close(ActionEvent event) {
		System.out.println("Launcher: Close App");
		Main.closeApp(true);
	}
}
