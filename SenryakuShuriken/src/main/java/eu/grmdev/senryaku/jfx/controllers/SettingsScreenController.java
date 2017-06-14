package eu.grmdev.senryaku.jfx.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import eu.grmdev.senryaku.Config;
import eu.grmdev.senryaku.Main;
import eu.grmdev.senryaku.core.config.Configuration;
import eu.grmdev.senryaku.core.map.LevelManager;
import eu.grmdev.senryaku.jfx.FxGui;
import javafx.collections.FXCollections;
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
	@FXML
	private ChoiceBox<Integer> cbMapiId;
	@FXML
	private Button btnChangeMap;
	@FXML
	private RadioButton rbCamSel1, rbCamSel2;
	@FXML
	private Slider sldCamPosX, sldCamPosY, sldCamPosZ;
	@FXML
	private Button btnResetCamPos;
	
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
		
		List<Integer> mapIds = IntStream.range(1, Config.LAST_MAP_ID.<Integer> get() + 1).boxed().collect(Collectors.toList());
		cbMapiId.setItems(FXCollections.observableArrayList(mapIds));
		btnChangeMap.setOnAction(event -> {
			LevelManager lm = LevelManager.getInstance();
			if (lm != null) {
				try {
					lm.goTo(cbMapiId.getSelectionModel().selectedItemProperty().get());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		boolean cc = Config.CURRENT_CAMERA.<Integer> get() == 1;
		rbCamSel1.setSelected(cc);
		rbCamSel2.setSelected(!cc);
		sldCamPosX.setValue((float) Config.CAMERA_POS_X.get());
		sldCamPosY.setValue((float) Config.CAMERA_POS_Y.get());
		sldCamPosZ.setValue((float) Config.CAMERA_POS_Z.get());
		btnResetCamPos.setOnAction(event -> {
			sldCamPosX.setValue(0);
			sldCamPosY.setValue(0);
			sldCamPosZ.setValue(0);
		});
	}
	
	private void applySettings() {
		applyMainSettings();
		applyLightSettings();
		applyMapSettings();
		applyCameraSettings();
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
		
	}
	
	private void applyCameraSettings() {
		Configuration.change(Config.CURRENT_CAMERA, rbCamSel1.isSelected() ? 1 : 2);
		Configuration.change(Config.CAMERA_POS_X, (float) sldCamPosX.getValue());
		Configuration.change(Config.CAMERA_POS_Y, (float) sldCamPosY.getValue());
		Configuration.change(Config.CAMERA_POS_Z, (float) sldCamPosZ.getValue());
	}
}
