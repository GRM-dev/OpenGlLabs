package eu.grmdev.senryaku.jfx.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.config.Configuration;
import eu.grmdev.senryaku.jfx.FxGui;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class SettingsScreenController implements Initializable {
	@FXML
	private Button btnClose, btnSave, btnApply, btnDefs;
	@FXML
	private CheckBox cbDebugEnabled;
	@FXML
	private Slider sldSpotLight, sldzFar, sldzNear, sldTps, sldFps;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnClose.setOnAction(event -> FxGui.getInstance().openSettings(false));
		btnApply.setOnAction(event -> applySettings());
		btnSave.setOnAction(event -> Main.getC().saveToFile());
		btnDefs.setOnAction(event -> Main.getC().loadDefaults());
		
		cbDebugEnabled.setSelected(Config.SHOW_DEBUG_INFO.get());
		sldzFar.setValue((float) Config.Z_FAR.get());
		sldzNear.setValue((float) Config.Z_NEAR.get());
		sldTps.setValue((int) Config.TARGET_UPS.get());
		sldFps.setValue((int) Config.TARGET_FPS.get());
	}
	
	private void applySettings() {
		applyMainSettings();
		applyLightSettings();
		applyMapSettings();
		applyCameraSettings();
		Configuration.setChanged(true);
	}
	
	private void applyMainSettings() {
		Configuration.change(Config.SHOW_DEBUG_INFO, cbDebugEnabled.isSelected());
		Configuration.change(Config.Z_FAR, (float) sldzFar.getValue());
		Configuration.change(Config.Z_NEAR, (float) sldzNear.getValue());
		Configuration.change(Config.TARGET_UPS, (int) sldTps.getValue());
		Configuration.change(Config.TARGET_FPS, (int) sldFps.getValue());
	}
	
	private void applyLightSettings() {
		// TODO Auto-generated method stub
		
	}
	
	private void applyMapSettings() {
		// TODO Auto-generated method stub
		
	}
	
	private void applyCameraSettings() {
		// TODO Auto-generated method stub
		
	}
	
}
