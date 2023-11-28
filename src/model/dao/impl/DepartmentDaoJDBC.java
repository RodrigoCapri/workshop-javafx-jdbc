package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.Dao;
import model.entities.Department;

//Classe de recurso para a entidade Department
public class DepartmentDaoJDBC implements Dao<Department>{
	
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department obj) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO department"
					+ "(Name) "
					+ "VALUES "
					+ "(?)",
					Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			int rows_affected = st.executeUpdate();
			
			if( rows_affected > 0 ) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Rows affected "+rows_affected+" id="+id);
				}
				DB.closeResultSet(rs);
			}else {
				throw new DbException("erro ao inserir registro!");
			}
			
		}catch(SQLException ex) {
			throw new DbException(ex.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		
		try {
			st= conn.prepareStatement("UPDATE department "
					+ "SET "
					+ "name= ? "
					+ "WHERE id= ?",
					Statement.RETURN_GENERATED_KEYS); //Configura pra regornar um ResultSet com o id do registro afetado
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			int rows_affected = st.executeUpdate();
			
			if(rows_affected > 0) { //Verifica se houve alguma linha afetada
				
				ResultSet rs = st.getGeneratedKeys();
				
				if(rs.next()) {
					int id = rs.getInt(1);
					System.out.println("Rows affected= "+rows_affected+" - "+id);
				}
				DB.closeResultSet(rs);
				
			}else
				throw new DbException("Erro ao atualizar registro!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		//Objeto para preparar um comando Sql
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM department WHERE id=?");
			st.setInt(1, id);
			
			int rows_affected = st.executeUpdate(); //Executa o comando e retorna o numero de linhas afetadas
			
			if(rows_affected > 0) { //Se for maior que zero, alguma linha foi executada
				System.out.println("rows affected= "+rows_affected);
			}else //Se for menor que zero, nenhuma linha foi executada, algum erro ocorreu
				throw new DbException("erro ao excluir registro!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		// TODO Auto-generated method stub
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			//Prepara um comando Sql para ser executado
			st = conn.prepareStatement("SELECT * FROM department "
					+ " WHERE id = ?");
			st.setInt(1, id); //Define os parametros necessarios para o comando
			
			rs = st.executeQuery(); //Executa comando de consulta
			
			if(rs.next()) {
				Department dp = instantiateDepartment(rs);
				return dp;
			}
			
			return null;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Department> findAll() {
		List<Department> list = new ArrayList<>();
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT * FROM department");
			rs = st.executeQuery();
			
			while(rs.next()) {
				Department obj = this.instantiateDepartment(rs);
				list.add(obj);
			}
			
			return list;
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	//Método auxiliar para recuperar o objeto do ResultSet
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department obj = new Department();
		obj.setId(rs.getInt("id"));
		obj.setName(rs.getString("name"));
		return obj;
	}

}
