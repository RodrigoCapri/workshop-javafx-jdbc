package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;
import model.entities.Seller;

//Classe auxliar para instanciar as conexões
public class DaoFactory {
	
	public static Dao<Seller> createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static Dao<Department> createDepartmentDao(){
		return new DepartmentDaoJDBC(DB.getConnection());
	}
	
}
