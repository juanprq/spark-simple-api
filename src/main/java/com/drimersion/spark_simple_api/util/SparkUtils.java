package com.drimersion.spark_simple_api.util;

import java.util.HashMap;
import java.util.Map;

import spark.Response;

/**
 * 
 * Clase de utilidaes para ser empleadas en la construcción de aplicaciones con
 * Sprak.
 * 
 * @author Juan pablo ramírez 22/07/2014 (juan.ramirez.q@gmail.com)
 */
public class SparkUtils {

	/**
	 * 
	 * Construye la respuesta basada en un código de error que se pasa por
	 * parámetro.
	 * 
	 * @param response
	 *            de la ruta que se está ejecutando en el momento
	 * @param errorCode
	 *            código de error para específicar el mensaje
	 * @return el cuerpo del mensaje de la respuesta
	 */
	public static Object buildResponseWithErrors(Response response,
			HttpStatusCode errorCode) {
		/* Se cambia el código de respuesta */
		response.status(errorCode.getCode());

		/* Se construye un body para la respuesta con error */
		Map<String, String> message = new HashMap<>();
		message.put("id", errorCode.getId());
		message.put("message", errorCode.getMessage());

		return message;
	}

}
