package model.services;

import java.util.List;

import model.dao.Dao;
import model.dao.DaoFactory;
import model.entities.Department;

public class DepartmentService {
	
	private Dao<Department> dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return dao.findAll();
	}
	
}
