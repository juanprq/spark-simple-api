package com.drimersion.spark_simple_api.response_transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import spark.ResponseTransformer;

/**
 * 
 * Clase que implementa el transformador de contenido de spark para generar
 * respuestas en formato json.
 * 
 * @author Juan pablo ramírez 21/07/2014 (juan.ramirez.q@gmail.com)
 */
public class JsonTransformer implements ResponseTransformer {

	/**
	 * Referencia a una instancia de la librería Gson.
	 */
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/*
	 * (non-Javadoc)
	 * 
	 * @see spark.ResponseTransformer#render(java.lang.Object)
	 */
	@Override
	public String render(Object model) throws Exception {
		return gson.toJson(model);
	}

}
