package com.drimersion.spark_simple_api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import spark.Spark;

import com.drimersion.spark_simple_api.model.User;
import com.drimersion.spark_simple_api.util.HttpStatusCode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jcabi.http.Request;
import com.jcabi.http.Response;
import com.jcabi.http.request.JdkRequest;

/**
 * 
 * Clase que contiene las pruebas para la aplicación.
 * 
 * @author Juan pablo ramírez 23/07/2014 (juan.ramirez.q@gmail.com)
 */
public class AppTest {

	/**
	 * Host donde está ejecutandose el proceso, normalmente localhost.
	 */
	private static String HOST = "localhost";

	/**
	 * Puerto donde está escuchando peticiones el servidor.
	 */
	private static Integer PORT = 4567;

	/**
	 * Procesos que se realizan para configurar la aplicación antes de iniciar
	 * las pruebas, requeridos para levantar el servidor.
	 * 
	 * @throws InterruptedException
	 *             Si no puede hacer dormir el hilo
	 */
	@BeforeClass
	public static void setup() throws InterruptedException {
		/* Se ejecuta el main para sacar las rutas principales */
		App.main(null);
		Thread.sleep(500);
	}

	/**
	 * 
	 * Procesos para desmantelar la aplicación una vez finalizadas las pruebas.
	 * 
	 */
	@AfterClass
	public static void tearDown() {
		/* Finaliza el proceso del servidor y limpia todas las rutas. */
		Spark.stop();
	}

	/**
	 * Verifica si es el sistema está listando usuarios correctamente.
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 */
	@Test
	public void canListUsers() throws IOException {
		/* Se realiza la petición al servicio. */
		Request request = new JdkRequest(buildUrl("/users"))
				.method(JdkRequest.GET);
		Response response = request.fetch();

		/*
		 * Se validan diferentes propiedades de la respuesta dada por el
		 * servicio.
		 */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del response es nulo", response.body());
		Assert.assertTrue("El cuerpo del mensaje está vacio", response.body()
				.length() > 0);
		Assert.assertEquals("El código de status no es 200", 200,
				response.status());
		Assert.assertTrue("El contenido no es correcto", response.body()
				.contains("Juan"));
	}

	/**
	 * 
	 * Verifica si el sistema está retornardo la representación correcta de un
	 * recurso.
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 * 
	 */
	@Test
	public void canGetEspecificUser() throws IOException {
		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users/1"))
				.method(JdkRequest.GET);
		Response response = request.fetch();

		/* Se verifican las propiedades dadas por la respuesta */
		Assert.assertNotNull("El response es nulo", response);

		String body = response.body();
		Assert.assertNotNull("El cuerpo del response es nulo", body);
		Assert.assertTrue("El cuerpo del response está vacio",
				body.length() > 0);
		Assert.assertEquals("El código de status no es el esperado", 200,
				response.status());

		/* Se verifican los valores que retorna la respuesta */
		Gson gson = new Gson();
		/* Se parsea el contenido del body. */
		User user = gson.fromJson(body, User.class);
		Assert.assertEquals("El name del usuario no es el esperado", "Juan",
				user.getName());
		Assert.assertEquals("El lastName usuario no es el esperado", "Ramírez",
				user.getLastName());
		Assert.assertEquals("El document del usuario no es el esperado",
				"1094891516", user.getDocument());
	}

	/**
	 * 
	 * Verifica que el servicio responda de manera correcta al pedir un usuario
	 * que no existe
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 */
	@Test
	public void canGetEspecificUserError() throws IOException {
		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users/30"))
				.method(JdkRequest.GET);
		Response response = request.fetch();

		/* se verifican propiedades de la respuesta */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertEquals("El código de estatus es incorrecto", 404,
				response.status());

		/* se verifican las propiedades del mensaje que se respondió */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._404.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._404.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * Verifica el servicio pueda crear un usuario de manera correcta.
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 */
	@Test
	public void canCreateUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{\"id\" : 7, \"name\" : \"juan\", \"lastName\" : \"test\", \"document\" : \"123\"}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users"))
				.method(JdkRequest.POST);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Validación de datos del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 201,
				response.status());
		Assert.assertNotNull("El header Location es nulo", response.headers()
				.get("Location"));
		Assert.assertEquals("El header Location no es correcto", "/users/7",
				response.headers().get("Location").get(0));

		/* Validación de los datos que se respondieron */
		Gson gson = new Gson();
		User user = gson.fromJson(response.body(), User.class);
		Assert.assertEquals("La propiedad name es incorrecta", "juan",
				user.getName());
		Assert.assertEquals("La propiedad lastName es incorrecta", "test",
				user.getLastName());
		Assert.assertEquals("La propiedad document es incorrecta", "123",
				user.getDocument());
	}

	/**
	 * 
	 * Valida la respuesta del servicio en caso de intentar crear un usuario
	 * mediante json malformado.
	 * 
	 * @throws IOException
	 *             Si no se puede realizar la petición
	 */
	@Test
	public void canCreateMalformedJsonUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{malformed-json}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users"))
				.method(JdkRequest.POST);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Validación de datos del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 422,
				response.status());

		/* Validación del mensaje de respuesta */
		/* se verifican las propiedades del mensaje que se respondió */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._422.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._422.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * 
	 * Verifica la respuesta del servidor al tratar de crear un usuario repetido
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 */
	@Test
	public void canCreateRepeatedUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{\"id\" : 1, \"name\" : \"Valentina\", \"lastName\" : \"Guarrado\", \"document\" : \"52345\"}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users"))
				.method(JdkRequest.POST);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Validación de datos del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 409,
				response.status());

		/* Validación de los datos que se respondieron */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._409.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._409.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * 
	 * Valida la respuesta del servicio para actualizar un usuario.
	 * 
	 * @throws IOException
	 *             en caso de no poder realizar la petición
	 */
	@Test
	public void canUpdateUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{\"name\" : \"Updated\", \"lastName\" : \"New Lastname\", \"document\" : \"567\"}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users/2"))
				.method(JdkRequest.PUT);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Verifica de datos generales del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 200,
				response.status());

		/* Verifica la información de la respuesta */
		Gson gson = new Gson();
		User user = gson.fromJson(response.body(), User.class);
		Assert.assertEquals("La propiedad name es incorrecta", "Updated",
				user.getName());
		Assert.assertEquals("La propiedad lastName es incorrecta",
				"New Lastname", user.getLastName());
		Assert.assertEquals("La propiedad document es incorrecta", "567",
				user.getDocument());
	}

	/**
	 * 
	 * Verifica la respuesta del sistema al tratar de moficiar un usuario que no
	 * existe.
	 * 
	 * @throws IOException
	 *             Si no es posible realizar la petición
	 */
	@Test
	public void canUpdateNonExistentUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{\"name\" : \"Updated\", \"lastName\" : \"New Lastname\", \"document\" : \"567\"}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users/50"))
				.method(JdkRequest.PUT);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Verifica de datos generales del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 404,
				response.status());

		/* se verifican las propiedades del mensaje que se respondió */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._404.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._404.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * Verifica la respuesta del servicio al tratar de actualizar un usuario con
	 * json mal formado.
	 * 
	 * @throws IOException
	 *             Si no es posible realizar la petición
	 */
	@Test
	public void canUpdateMalformedJsonUser() throws IOException {
		/* Json para la creación del usuario */
		String jsonData = "{malformed-json}";

		/* Se realiza la petición */
		Request request = new JdkRequest(buildUrl("/users/2"))
				.method(JdkRequest.PUT);
		InputStream stream = new ByteArrayInputStream(
				jsonData.getBytes(StandardCharsets.UTF_8));
		Response response = request.fetch(stream);

		/* Verifica de datos generales del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 422,
				response.status());

		/* se verifican las propiedades del mensaje que se respondió */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._422.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._422.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * 
	 * Verifica la respuesta del servidor al tratar de eliminar un usuario.
	 * 
	 * @throws IOException
	 *             Si la petición no se puede realizar
	 * 
	 */
	@Test
	public void canDeleteUser() throws IOException {
		/* Se realiza la petición al servicio */
		Request request = new JdkRequest(buildUrl("/users/3"))
				.method(JdkRequest.DELETE);
		Response response = request.fetch();

		/* Verifica de datos generales del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 200,
				response.status());

		/* Verifica la información de la respuesta */
		Gson gson = new Gson();
		User user = gson.fromJson(response.body(), User.class);
		Assert.assertEquals("La propiedad name es incorrecta", "José",
				user.getName());
		Assert.assertEquals("La propiedad lastName es incorrecta", "Ortiz",
				user.getLastName());
		Assert.assertEquals("La propiedad document es incorrecta",
				"1094627938", user.getDocument());
	}

	/**
	 * Verifica la respuesta del servicio al tratar de eliminar un usuario que
	 * no existe.
	 * 
	 * @throws IOException
	 *             Si no puede realizar la petición
	 */
	@Test
	public void canDeleteNonExistentUser() throws IOException {
		/* Se realiza la petición al servicio */
		Request request = new JdkRequest(buildUrl("/users/40"))
				.method(JdkRequest.DELETE);
		Response response = request.fetch();

		/* Verifica de datos generales del response */
		Assert.assertNotNull("El response es nulo", response);
		Assert.assertNotNull("El cuerpo del mensaje es nulo", response.body());
		Assert.assertNotNull("El cuerpo del mensaje está vacio", response
				.body().length() > 0);
		Assert.assertEquals("El código de estatus no es el correcto", 404,
				response.status());

		/* se verifican las propiedades del mensaje que se respondió */
		Gson gson = new Gson();
		JsonObject data = gson.fromJson(response.body(), JsonObject.class);
		Assert.assertEquals("El identificador del mensaje no es correcto",
				HttpStatusCode._404.getId(), data.get("id").getAsString());
		Assert.assertEquals("El mensaje de respuesta no es correcto",
				HttpStatusCode._404.getMessage(), data.get("message")
						.getAsString());
	}

	/**
	 * 
	 * Construye la url para realizar la petición.
	 * 
	 * @param relativeUrl
	 *            url relativa donde se quiere acceder al recurso.
	 * @return url completa sobre la que se realizará la acción.
	 */
	private static String buildUrl(String relativeUrl) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://");
		sb.append(HOST);
		sb.append(":");
		sb.append(PORT);
		sb.append(relativeUrl);
		return sb.toString();
	}

}
