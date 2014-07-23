Spark Simple API
================

Introducción
------------

Ejemplo de un API rest implementada bajo el framework Spark.

Dependencias
------------

Dependencias necesarias para la ejecución de la aplicación:

* spark-core: 2.0.0
* slf4j-api: 1.7.7
* slf4j-simple: 1.7.7
* gson: 2.2.4

Dependencias necesarias para la ejecución de las pruebas:

* junit: 3.8.1

Configuración para desarrollo local
-----------------------------------

Para el desarrollo local se emplea la herramienta maven para el manejo de dependencias, las cuales se encuentran descritas en el archivo *pom.xml*; el proyecto puede ser importado en eclipse y usar el plugin para maven y realizar toda la gestión necesaria para las dependencias; es necesario tener configurado en la herramienta java 1.8 ya que el código está escrito con bloques que se pasan como referencia en la especificación de rutas. 