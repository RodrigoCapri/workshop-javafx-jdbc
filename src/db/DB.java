package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//Classe auxiliar para carregar as propriedades e conectar ao banco de dados
//Funçoes para controlar as conexões
//Todos os métodos utilitários são estaticos para não precisar instanciar a classe
public class DB {

	private static Connection conn = null;

	// Cria uma conexão com a base de dados
	public static Connection getConnection() {
		if (conn == null) {
			try {

				Properties props = loadProperties(); // Carrega as proriedades da base de dados
				String url = props.getProperty("dburl");

				conn = DriverManager.getConnection(url, props); // Pode gerar exceção

			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}

	// Método utilitário de uma conexão com o banco
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	// Método utilitário de uma conexão com o banco
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	// Método utilitário de uma conexão com o banco
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	// Método auxiliar para carregar as propriedades da base de dados
	private static Properties loadProperties() {
		try (FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs);
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}

}
