package com.drimersion.spark_simple_api.util;

public enum HttpStatusCode {

	_404(404, "not_found",
			"El recurso al que está tratando de acceder no fué encontrado."), _409(
			409,
			"conflict",
			"Se ha encotrado un conflicto al momento de realizar la operación sobre el recurso especificado."), _422(
			422, "unprocessable_entity",
			"La petición no puede ser procesada debido a que contiene errores.");

	/**
	 * 
	 * Constructor.
	 * 
	 * @param code
	 * @param id
	 * @param message
	 */
	private HttpStatusCode(Integer code, String id, String message) {
		this.code = code;
		this.id = id;
		this.message = message;
	}

	/**
	 * 
	 */
	private Integer code;

	/**
	 * Identificador del mensaje de error.
	 */
	private String id;

	/**
	 * Mensaje legible del error.
	 */
	private String message;

	/**
	 *
	 * Retorna el valor de la variable de instancia code
	 *
	 * @return valor para code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 *
	 * Retorna el valor de la variable de instancia id
	 *
	 * @return valor para id
	 */
	public String getId() {
		return id;
	}

	/**
	 *
	 * Retorna el valor de la variable de instancia message
	 *
	 * @return valor para message
	 */
	public String getMessage() {
		return message;
	}

}
