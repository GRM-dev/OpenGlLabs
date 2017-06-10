package eu.grmdev.senryaku.jfx;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import eu.grmdev.senryaku.core.IGame;
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
import lombok.Getter;

public class FxGui extends Application {
	private Stage stage;
	private Scene startScene;
	private static @Getter FxGui instance;
	private static IGame game;
	
	public static void init(IGame game) {
		FxGui.game = game;
	}
	
	@Override
	public synchronized void start(Stage primaryStage) throws Exception {
		this.stage = primaryStage;
		Thread.currentThread().setName("FxGui Startup Thread");
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
			
			instance = this;
			synchronized (game) {
				game.notifyAll();
			}
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