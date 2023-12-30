package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		this.loadView("/gui/SellerList.fxml", (SellerListController controller) -> {
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		
		//Método com expressão lambda com acção de inicialização do DepartmentListController
		this.loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	//Define como syncronized para evitar conflitos de Threads
	//Aqui vai ser uma função generica, uma função do tipo T
	//Parametrização com Consumer T
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) { //Interface Consumer para chamada da expressão lambda passada
		FXMLLoader loader = new FXMLLoader(this.getClass().getResource(absoluteName)); //Carrega o cenário da fxml informada
		
		try {
			
			VBox newVBox = loader.load(); //Carrega uma nova VBox
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ( (ScrollPane) mainScene.getRoot() ).getContent(); //Pega o primeiro elemento da view
			
			Node mainMenu = mainVBox.getChildren().get(0); //Pega o menu bar
			mainVBox.getChildren().clear(); //Limpa todos os filhos do mainVBox
			
			mainVBox.getChildren().add(mainMenu); //Adiciona o menu bar
			mainVBox.getChildren().addAll(newVBox.getChildren()); //Adiciona os elementos da tela About
			
			//o getController() vai retornar um controller do tipo da classe que foi passada na expressão lambda, DepartmentListController
			T controller = loader.getController();
			initializingAction.accept(controller); //Executa os comandos passados por expressão lambda no parametro
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view!", e.getMessage(), AlertType.ERROR);
		}
		
	}
	
}
