package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

//Classe subject, classe que eminte o evento
public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<DataChangeListener>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		if(service == null) {
			throw new IllegalStateException("Service was null!");
		}
		
		try {
			//Esta chamada pode lançar uma exceção
			this.entity = this.getFormData(); //Carrega os dados do formulario
			this.service.saveOrUpdate(entity);
			
			notifyDataChangeListeners();
			
			Utils.currentStage(event).close(); //Fecha a janela atual
			
		}catch(DbException ex) {
			Alerts.showAlert("Error saving object", null, ex.getMessage(), AlertType.ERROR);
		}catch(ValidationException ex) {
			this.setErrorMessages(ex.getErrors());
		}
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close(); //Fecha a janela atual
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void updateFormDate() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity was null!");
		}
		this.txtId.setText(String.valueOf(this.entity.getId()));
		this.txtName.setText(this.entity.getName());
	}

	private Department getFormData() {
		Department obj = new Department();

		//Instancia uma exceção
		ValidationException exception = new ValidationException("Validation error!");
		
		obj.setId(Utils.tryParseToInt(this.txtId.getText()));
		if(this.txtName.getText() == null || this.txtName.getText().trim().equals("") ) {
			exception.addError("name", "Field can't be empty"); //O campo não pode ser vazio
		}
		obj.setName(this.txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}
	
	//Adiciona uma inscrição a chamada do evento
	public void subscribeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}
	
	//Chama todos os eventos
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : this.dataChangeListeners) {
			listener.onDataChanged();
		}
	}
	
	//Vai carregar os erros e preencher os erros nas caixinha de erro
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) { //Verifica se há algum erro do field Name
			this.labelErrorName.setText(errors.get("name")); //Seta o label com o erro lançado
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.initializeNodes();
	}

}
