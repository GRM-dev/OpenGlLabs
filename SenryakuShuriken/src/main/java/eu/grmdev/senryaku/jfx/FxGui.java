package eu.grmdev.senryaku.jfx;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import eu.grmdev.senryaku.core.misc.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FxGui extends Application {
	
	private Stage stage;
	private Scene startScene;
	
	public void startup() {
		new Thread(() -> launch(), "FxGui Startup Thread").start();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		Platform.setImplicitExit(false);
		setUserAgentStylesheet(STYLESHEET_MODENA);
		try {
			URL startScreenUrl = getClass().getResource("/views/StartScreen.fxml");
			Parent startRoot = FXMLLoader.load(startScreenUrl);
			startScene = new Scene(startRoot);
			startScene.setFill(Color.TRANSPARENT);
			
			addCss();
			
			stage.setScene(startScene);
			stage.setTitle("Senryaku Shuriken");
			stage.sizeToScene();
			stage.setOnCloseRequest(event -> {
				event.consume();
				stage.hide();
			});
			stage.centerOnScreen();
			stage.setResizable(false);
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
			stage.show();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void addCss() throws IOException {
		URI tempStyleSheetDest = Utils.loadResourceForTemp("/styles/custom.css", "javafx_stylesheet", "a");
		startScene.getStylesheets().add(tempStyleSheetDest.toString());
		tempStyleSheetDest = Utils.loadResourceForTemp("/styles/dark_style.css", "javafx_stylesheet", "b");
		startScene.getStylesheets().add(tempStyleSheetDest.toString());
	}
	
	public void closeGui() {
		try {
			stop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Platform.exit();
	}
}