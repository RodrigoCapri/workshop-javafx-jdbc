package model.services;

import java.util.List;

import model.dao.Dao;
import model.dao.DaoFactory;
import model.entities.Department;

//Clase de serviço do Department
public class DepartmentService {
	
	private Dao<Department> dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Department obj) {
		if( obj.getId() == null ) { //Um novo departamento
			dao.insert(obj);
		}else { //Atualizar o departamento
			dao.update(obj);
		}
	}
	
	//Metodo que remove um departamento numa base de dados
	public void remove(Department obj) {
		dao.deleteById(obj.getId());
	}
	
}
