package model.exceptions;

import java.util.HashMap;
import java.util.Map;


//Exceção personalizada que carrega todos os erros possiveis
public class ValidationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	//Pares chave valor -> primeiro tipo é o chave e o segundo é o valor
	//Primeira string vai indicar o nome do campo
	//Segunda string vai indicar a msg de erro
	private Map<String, String> errors = new HashMap<>();
	
	
	public ValidationException(String msg) {
		super(msg);
	}
	
	//Retorna a lista de errors
	public Map<String, String> getErrors(){
		return this.errors;
	}

	//Metodo para adicionar um novo erro
	public void addError(String fieldName, String errorMessage) {
		this.errors.put(fieldName, errorMessage);
	}
	
}
