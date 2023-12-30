package model.services;

import java.util.List;

import model.dao.Dao;
import model.dao.DaoFactory;
import model.entities.Seller;

//Clase de serviço do Seller
public class SellerService {

	private Dao<Seller> dao = DaoFactory.createSellerDao();

	public List<Seller> findAll() {
		return dao.findAll();
	}

	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) { // Um novo departamento
			dao.insert(obj);
		} else { // Atualizar o departamento
			dao.update(obj);
		}
	}

	// Metodo que remove um departamento numa base de dados
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}

}
