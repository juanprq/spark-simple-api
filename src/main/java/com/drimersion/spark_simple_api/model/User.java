package com.drimersion.spark_simple_api.model;

import java.io.Serializable;

/**
 * 
 * Clase que mapea la entidad de usuario en el sistema.
 * 
 * @author Juan pablo ramírez 21/07/2014 (juan.ramirez.q@gmail.com)
 */
public class User implements Serializable {

	/**
	 * Constante de serialización.
	 */
	private static final long serialVersionUID = -5995361080484501616L;

	/**
	 * Nombre del usuario.
	 */
	private String name;

	/**
	 * Apellido del usuario.
	 */
	private String lastName;

	/**
	 * Documento del usuario.
	 */
	private String document;

	/**
	 * Constructor sin parámetros.
	 */
	public User() {
	}

	/**
	 * 
	 * Constructor usando los campos.
	 * 
	 * @param name 
	 * @param lastName
	 * @param document
	 */
	public User(String name, String lastName, String document) {
		super();
		this.name = name;
		this.lastName = lastName;
		this.document = document;
	}

	/**
	 *
	 * Retorna el valor de la variable de instancia name
	 *
	 * @return valor para name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * Asigna el valor que llega por parámetro a la variable de instancia name
	 *
	 * @param name
	 *            para asignar a name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 *
	 * Retorna el valor de la variable de instancia lastName
	 *
	 * @return valor para lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 *
	 * Asigna el valor que llega por parámetro a la variable de instancia
	 * lastName
	 *
	 * @param lastName
	 *            para asignar a lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 *
	 * Retorna el valor de la variable de instancia document
	 *
	 * @return valor para document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 *
	 * Asigna el valor que llega por parámetro a la variable de instancia
	 * document
	 *
	 * @param document
	 *            para asignar a document
	 */
	public void setDocument(String document) {
		this.document = document;
	}

}
