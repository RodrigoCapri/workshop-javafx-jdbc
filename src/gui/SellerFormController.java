package gui;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

//Classe subject, classe que eminte o evento
public class SellerFormController implements Initializable {

	private Seller entity;
	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();

	private ObservableList<Department> obsList;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null!");
		}

		try {
			// Esta chamada pode lançar uma exceção
			this.entity = this.getFormData(); // Carrega os dados do formulario
			this.service.saveOrUpdate(entity);

			notifyDataChangeListeners();

			Utils.currentStage(event).close(); // Fecha a janela atual

		} catch (DbException ex) {
			Alerts.showAlert("Error saving object", null, ex.getMessage(), AlertType.ERROR);
		} catch (ValidationException ex) {
			this.setErrorMessages(ex.getErrors());
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); // Fecha a janela atual
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		this.initializeComboBoxDepartment();
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void updateFormDate() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		this.txtId.setText(String.valueOf(this.entity.getId()));
		this.txtName.setText(this.entity.getName());
		this.txtEmail.setText(this.entity.getEmail());
		Locale.setDefault(Locale.US);
		this.txtBaseSalary.setText(String.format("%.2f", this.entity.getBaseSalary()));
		
		if (entity.getBirthDate() != null) {
			this.dpBirthDate.setValue(
					LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
		
		if(this.entity.getDepartment() != null) {
			this.comboBoxDepartment.setValue(this.entity.getDepartment());
		}else {
			this.comboBoxDepartment.getSelectionModel().selectFirst();
		}
	}

	public void loadAssociateObjects() {
		if (this.departmentService == null) {
			throw new IllegalStateException("DepartmentService was null!");
		}
		List<Department> list = this.departmentService.findAll();
		this.obsList = FXCollections.observableArrayList(list);
		this.comboBoxDepartment.setItems(obsList);
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		// Instancia uma exceção
		ValidationException exception = new ValidationException("Validation error!");

		obj.setId(Utils.tryParseToInt(this.txtId.getText()));
		if (this.txtName.getText() == null || this.txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty"); // O campo não pode ser vazio
		}
		obj.setName(this.txtName.getText());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	// Adiciona uma inscrição a chamada do evento
	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}

	// Chama todos os eventos
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : this.dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	// Vai carregar os erros e preencher os erros nas caixinha de erro
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) { // Verifica se há algum erro do field Name
			this.labelErrorName.setText(errors.get("name")); // Seta o label com o erro lançado
		}
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.initializeNodes();
	}

}
