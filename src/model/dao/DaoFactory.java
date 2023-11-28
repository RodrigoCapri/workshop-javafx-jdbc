package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;
import model.entities.Department;
import model.entities.Seller;

//Classe auxliar para instanciar as conexões
//Métodos estaticos para fins de não instanciação
public class DaoFactory {
	
	//Cria uma instanciação do SellerDaoJDBC passando como parametro a conexão como banco ja estabelecida
	public static Dao<Seller> createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	//Cria uma instanciação do DepartmentDaoJDBC passando como parametro a conexão como banco ja estabelecida
	public static Dao<Department> createDepartmentDao(){
		return new DepartmentDaoJDBC(DB.getConnection());
	}
	
}
