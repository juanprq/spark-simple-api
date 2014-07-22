package com.drimersion.spark_simple_api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import spark.Spark;

import com.drimersion.spark_simple_api.model.User;
import com.drimersion.spark_simple_api.response_transformer.JsonTransformer;
import com.drimersion.spark_simple_api.util.HttpStatusCode;
import com.drimersion.spark_simple_api.util.SparkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * 
 * Clase que contiene el método de inicio del hilo principal y contiene la
 * aplicación de manera general.
 * 
 * @author Juan pablo ramírez 21/07/2014 (juan.ramirez.q@gmail.com)
 */
public class App {

	/**
	 * 
	 * Inicia la aplicación, configura el comportamiento y describe cada uno de
	 * los routes para la misma.
	 * 
	 * @param args
	 *            arreglo de argumentos empleados para el inicio de la
	 *            aplicación.
	 */
	public static void main(String[] args) {
		/* Referencia a los usuarios del sistema */
		Map<Integer, User> users = initializeUsers();
		/* Inicialización del objeto Gson para el parseo de formato json */
		Gson gson = new Gson();

		/**
		 * Configuración de toda respuesta
		 */
		Spark.after((request, response) -> {
			response.header("Request-Id", UUID.randomUUID().toString());
			response.header("Accept",
					"application/drimersion.test+json; version=1");
			response.type("application/json; charset=UTF-8");
		});

		/**
		 * Servicio que responde la colección de usuarios del sistema.
		 */
		Spark.get("/users", (request, response) -> {
			/* Se retorna la colección de usuarios */
			return users;
		}, new JsonTransformer());

		/**
		 * Servicio que responde la representación de un recurso usuario en
		 * particular.
		 */
		Spark.get(
				"/users/:id",
				(request, response) -> {
					/* Inicialización del cuerpo del mensaje con un string vacio */
					Object body = new String();

					try {
						/*
						 * Se intenta parsear el indetificador del recurso como
						 * entero
						 */
						Integer id = Integer.parseInt(request.params(":id"));

						if (users.get(id) == null) {
							/* Si el recurso no existe se pone el status 404 */
							body = SparkUtils.buildResponseWithErrors(response,
									HttpStatusCode._404);
						} else {
							/* Si el recurso existe se carga el */
							body = users.get(id);
						}
					} catch (NumberFormatException e) {
						/*
						 * Si el identificador no es un entero se asume que el
						 * recurso no existe
						 */
						body = SparkUtils.buildResponseWithErrors(response,
								HttpStatusCode._404);
					}

					return body;
				}, new JsonTransformer());

		/**
		 * Servicio que recibe un usuario en formato json y lo ingresa en el
		 * listado de usuarios del sistema.
		 */
		Spark.post(
				"/users",
				(request, response) -> {
					/* Inicialización del cuerpo del mensaje. */
					Object body = new String();

					try {
						/* Se parsea el contenido del body. */
						JsonObject data = gson.fromJson(request.body(),
								JsonObject.class);

						/* se obtiene el identificador. */
						Integer id = data.get("id").getAsInt();

						/* si el valor ya existe se responde con 409. */
						if (users.get(id) == null) {
							/* se parsean los datos para el usuario. */
							User user = gson.fromJson(request.body(),
									User.class);
							users.put(id, user);

							response.header("Location",
									"/users/" + id.intValue());
							response.status(201);
							body = user;
						} else {
							/*
							 * Se responde con el código de conflicto ya que
							 * existe un recurso con el mismo identificador.
							 */
							body = SparkUtils.buildResponseWithErrors(response,
									HttpStatusCode._409);
						}
					} catch (Exception e) {
						/*
						 * El request es válido pero contiene errores semánticos
						 * y no puede ser procesado por el servidor.
						 */
						body = SparkUtils.buildResponseWithErrors(response,
								HttpStatusCode._422);
					}

					return body;
				}, new JsonTransformer());

		/**
		 * Servicio que recibe un usuario para ser actualizado en el sistema.
		 */
		Spark.put(
				"/users/:id",
				(request, response) -> {
					/* Se inicializa el cuerpo del mensaje vacio. */
					Object body = new String();
					try {
						/*
						 * Se obtiene el valor entero del indentificador del
						 * recurso.
						 */
						Integer id = Integer.parseInt(request.params(":id"));
						/* Se parsea el contenido de la petición. */
						User user = gson.fromJson(request.body(), User.class);

						/* Se garantiza que el recurso si exista. */
						if (users.get(id) == null) {
							/* En caso que el recurso no exista se responde 404 */
							body = SparkUtils.buildResponseWithErrors(response,
									HttpStatusCode._404);
						} else {
							/*
							 * En caso que el recurso exista se actualizan sus
							 * valores.
							 */
							users.put(id, user);
							/*
							 * Se asigna el usuario a la respuesta que se va a
							 * dar en el cuerpo del mensaje.
							 */
							body = user;
						}
					} catch (JsonSyntaxException e) {
						/*
						 * En caso de no poder parsear el contenido del mensaje
						 * en json, se responde con el error 422 indicando que
						 * hay errores semánticos.
						 */
						body = SparkUtils.buildResponseWithErrors(response,
								HttpStatusCode._422);
					} catch (NumberFormatException e) {
						/* En caso que el identificador no exista. */
						body = SparkUtils.buildResponseWithErrors(response,
								HttpStatusCode._404);
					}

					return body;
				}, new JsonTransformer());

		/**
		 * Servicio que remueve el usuario del sistema con el idenrificador
		 * específicado.
		 */
		Spark.delete(
				"/users/:id",
				(request, response) -> {
					/* Inicialización del cuerpo de la respuesta. */
					Object body = new String();

					try {
						/*
						 * Se obtiene el valor entero del identificador del
						 * recurso.
						 */
						Integer id = Integer.parseInt(request.params(":id"));

						/* Se verifica si el recurso existe. */
						if (users.get(id) == null) {
							/*
							 * El recurso no existe y se responde con el código
							 * 404.
							 */
							body = SparkUtils.buildResponseWithErrors(response,
									HttpStatusCode._404);
						} else {
							/* Si el recurso existe se elimina. */
							body = users.remove(id);
						}
					} catch (NumberFormatException e) {
						/*
						 * En caso de no poder parsea el identificador a entero
						 * se responde 404.
						 */
						body = SparkUtils.buildResponseWithErrors(response,
								HttpStatusCode._404);
					}
					return body;
				}, new JsonTransformer());
	}

	/**
	 * 
	 * Realiza la inicialización del {@link List} de usuarios para el sistema.
	 * 
	 * @return {@link List} con usuarios creados por defecto para el sistema.
	 */
	private static Map<Integer, User> initializeUsers() {
		/* Inicialización del arreglo de usuarios */
		Map<Integer, User> users = new HashMap<>();

		/* se agrega cada uno de los usuarios de prueba */
		users.put(1, new User("Juan", "Ramírez", "1094891516"));
		users.put(2, new User("Daniel", "Arbelaez", "1094673845"));
		users.put(3, new User("José", "Ortiz", "1094627938"));
		users.put(4, new User("Carlos", "Ariza", "1090341289"));
		users.put(5, new User("Yamit", "Ospina", "1087649032"));

		return users;
	}

}
