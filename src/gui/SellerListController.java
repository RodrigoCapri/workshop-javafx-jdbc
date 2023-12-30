package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import db.DbIntegrityException;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {

	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // Classe identidade, tipo da coluna especificada
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnRemove;

	@FXML
	private Button btNew;

	private SellerService service;
	private ObservableList<Seller> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);

		Seller obj = new Seller();

		this.createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	public void updateTableView() {
		if (this.service == null) {
			throw new IllegalStateException("Service was null!");
		}

		List<Seller> list = this.service.findAll();
		this.obsList = FXCollections.observableArrayList(list);

		this.tableViewSeller.setItems(obsList);

		this.initEditButtons();
		this.initRemoveButtons();
	}

	private void initializeNodes() {
		this.tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		this.tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		this.tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		this.tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(this.tableColumnBirthDate, "dd/MM/yyyy");
		this.tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(this.tableColumnBaseSalary, 2); //Numero de casas decimais

		Stage stage = (Stage) Main.getMainScene().getWindow();
		// Macete para fazer a table view acompanhar a altura da janela
		this.tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	// Cria um formulario de dialogo para edição ou novo registro de Departamento
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		try {

			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(absoluteName)); // Carrega o cenário da fxml
																							// informada
			Pane pane = loader.load(); // Adiciona o cenario em um Pane

			// Pega o controlador da tela qua acabou de carregar acima
			SellerFormController controller = loader.getController();
			// Passando o objeto Seller para o Formulário
			controller.setSeller(obj);
			controller.setSellerService(new SellerService());
			// Se increve no evento
			controller.subscribeDataChangeListener(this);
			controller.updateFormDate();

			Stage dialogStage = new Stage(); // Nova cena para aparecer na frente de outra cena
			dialogStage.setTitle("Enter Seller data"); // Definindo o titulo
			dialogStage.setScene(new Scene(pane)); // Adiciona o Pane na cena
			dialogStage.setResizable(false); // Define como não redimensionavel
			dialogStage.initOwner(parentStage); // Quem é o init pai dessa janela
			// Modality.WINDOW_MODAL -> Enquando você não fechar essa janela, não poderá
			// mexer na tela anterior
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait(); // Mostra a tela enquanto aguarda

		} catch (IOException ex) {
			Alerts.showAlert("IO EXception", "Error load view!", ex.getMessage(), AlertType.ERROR);
		}
	}

	// Método que tem como função de adicionar um botão para edição do departamento
	// Em cada registro da tabela
	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {

			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnRemove.setCellFactory(param -> new TableCell<Seller, Seller>() {
			
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	//Metodo que procede a remoção de um Departamento
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if ( result.get() == ButtonType.OK ) {
			if ( service == null)
				throw new IllegalStateException("Service was null!");
			
			try {
				
				this.service.remove(obj);
				this.updateTableView();
				
			}catch(DbIntegrityException ex) {
				Alerts.showAlert("Error removing objet", null, ex.getMessage(), AlertType.ERROR);
			}catch(DbException ex) {
				Alerts.showAlert("Error removing objet", null, ex.getMessage(), AlertType.ERROR);
			}
		}
			
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
