package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{

	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId; //Classe identidade, tipo da coluna especificada
	@FXML
	private TableColumn<Department, String> tableColumnName;
	@FXML
	private TableColumn<Department, Department> tableColumnEdit;
	
	@FXML
	private Button btNew;
	
	private DepartmentService service;
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		
		Department obj = new Department();
		
		this.createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void updateTableView() {
		if( this.service == null ) {
			throw new IllegalStateException("Service was null!");
		}
		
		List<Department> list = this.service.findAll();
		this.obsList = FXCollections.observableArrayList(list);
		
		this.tableViewDepartment.setItems(obsList);
		
		this.initEditButtons();
	}

	private void initializeNodes() {
		this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		//Macete para fazer a table view acompanhar a altura da janela
		this.tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//Cria um formulario de dialogo para edição ou novo registro de Departamento
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(absoluteName)); //Carrega o cenário da fxml informada
			Pane pane = loader.load(); //Adiciona o cenario em um Pane
			
			//Pega o controlador da tela qua acabou de carregar acima
			DepartmentFormController controller = loader.getController();
			//Passando o objeto Department para o Formulário
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			//Se increve no evento
			controller.subscribeDataChangeListener(this);
			controller.updateFormDate();
			
			Stage dialogStage = new Stage(); //Nova cena para aparecer na frente de outra cena
			dialogStage.setTitle("Enter Department data"); //Definindo o titulo
			dialogStage.setScene(new Scene(pane)); //Adiciona o Pane na cena
			dialogStage.setResizable(false); //Define como não redimensionavel
			dialogStage.initOwner(parentStage); //Quem é o init pai dessa janela
			//Modality.WINDOW_MODAL -> Enquando você não fechar essa janela, não poderá mexer na tela anterior
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait(); //Mostra a tela enquanto aguarda
			
		}catch(IOException ex) {
			Alerts.showAlert("IO EXception", "Error load view!", ex.getMessage(), AlertType.ERROR);
		}
	}
	
	//Método que tem como função de adicionar um botão para edição do departamento
	//Em cada registro da tabela
	private void initEditButtons() { 
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Department, Department>() {
			
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction( event -> createDialogForm(obj, "/gui/DepartmentForm.fxml", Utils.currentStage(event)) );
			}
		});
	}
	
	@Override
	public void onDataChanged() {
		// Atualiza os dados na tabela
		this.updateTableView();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		initializeNodes();
	}

}
