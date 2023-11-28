package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
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
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;
	private DepartmentService service;

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
			this.entity = this.getFormData(); //Carrega os dados do formulario
			this.service.saveOrUpdate(entity);
			
			Utils.currentStage(event).close(); //Fecha a janela atual
			
		}catch(DbException ex) {
			Alerts.showAlert("Error saving object", null, ex.getMessage(), AlertType.ERROR);
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

		obj.setId(Utils.tryParseToInt(this.txtId.getText()));
		obj.setName(this.txtName.getText());

		return obj;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.initializeNodes();
	}

}
