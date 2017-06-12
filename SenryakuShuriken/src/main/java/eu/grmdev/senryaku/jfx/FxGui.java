package eu.grmdev.senryaku.jfx;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import eu.grmdev.senryaku.core.IGame;
import eu.grmdev.senryaku.core.misc.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.*;
import lombok.Getter;

public class FxGui extends Application {
	private Stage primaryStage;
	private Scene startScene;
	private Scene settingsScene;
	private Stage settingsStage;
	private static @Getter FxGui instance;
	private static IGame game;
	
	public static void init(IGame game) {
		FxGui.game = game;
	}
	
	@Override
	public synchronized void start(Stage primaryStage) throws Exception {
		instance = this;
		this.primaryStage = primaryStage;
		Thread.currentThread().setName("FxGui Startup Thread");
		Platform.setImplicitExit(false);
		setUserAgentStylesheet(STYLESHEET_MODENA);
		try {
			URL startScreenUrl = getClass().getResource("/views/StartScreen.fxml");
			Parent startRoot = FXMLLoader.load(startScreenUrl);
			startScene = new Scene(startRoot);
			startScene.setFill(Color.TRANSPARENT);
			
			settingsStage = new Stage(StageStyle.UTILITY);
			settingsStage.setTitle("Settings");
			settingsStage.setAlwaysOnTop(true);
			URL settingsScreenUrl = getClass().getResource("/views/SettingsScreen.fxml");
			Parent settingsRoot = FXMLLoader.load(settingsScreenUrl);
			settingsScene = new Scene(settingsRoot);
			settingsStage.setScene(settingsScene);
			settingsStage.sizeToScene();
			
			addCss();
			
			primaryStage.setScene(startScene);
			primaryStage.setTitle("Senryaku Shuriken");
			primaryStage.sizeToScene();
			primaryStage.setOnCloseRequest(event -> {
				event.consume();
				primaryStage.hide();
			});
			primaryStage.centerOnScreen();
			primaryStage.setResizable(false);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
			primaryStage.show();
			
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
	
	public void openSettings(boolean open) {
		System.out.println("Launcher: Settings open: " + open);
		Platform.runLater(() -> {
			if (open) {
				settingsStage.show();
				
				Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
				double screenRightEdge = primScreenBounds.getMaxX();
				
				double xp = screenRightEdge - settingsStage.getWidth();
				double yp = primScreenBounds.getMinY() + settingsScene.getHeight() / 2;
				
				settingsStage.setX(xp);
				settingsStage.setY(yp);
			} else {
				settingsStage.hide();
			}
		});
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