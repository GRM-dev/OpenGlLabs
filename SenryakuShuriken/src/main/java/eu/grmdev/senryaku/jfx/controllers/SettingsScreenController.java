package eu.grmdev.senryaku.jfx.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.config.Configuration;
import eu.grmdev.senryaku.jfx.FxGui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class SettingsScreenController implements Initializable {
	@FXML
	private Button btnClose;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnApply;
	@FXML
	private Button btnDefs;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnClose.setOnAction(event -> FxGui.getInstance().openSettings(false));
		btnApply.setOnAction(event -> Configuration.setChanged(true));
		btnSave.setOnAction(event -> Main.getC().saveToFile());
		btnDefs.setOnAction(event -> Main.getC().loadDefaults());
	}
	
}
