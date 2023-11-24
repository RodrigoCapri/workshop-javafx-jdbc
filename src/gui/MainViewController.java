package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	private synchronized void loadView(String absoluteName) {
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(absoluteName));
		
		try {
			
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ( (ScrollPane) mainScene.getRoot() ).getContent(); //Pega o primeiro elemento da view
			
			Node mainMenu = mainVBox.getChildren().get(0); //Pega o menu bar
			mainVBox.getChildren().clear(); //Limpa todos os filhos do mainVBox
			
			mainVBox.getChildren().add(mainMenu); //Adiciona o menu bar
			mainVBox.getChildren().addAll(newVBox.getChildren()); //Adiciona os elementos da tela About
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view!", e.getMessage(), AlertType.ERROR);
		}
		
	}
	
}
