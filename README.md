# 🧬 Mutant Detector API - Examen MercadoLibre

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring%20Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> 🎯 **Objetivo:** Desarrollar una API REST eficiente para determinar si una secuencia de ADN pertenece a un mutante, basándose en la presencia de más de una secuencia de cuatro letras iguales (A, T, C, G) de forma horizontal, vertical u oblicua.

---

## Información del Alumno

* *Estudiante:* Tiago Pínfari
* *Legajo:* 51043
* *Materia:* Desarrollo de Software
* *Año:* 3er Año


## 📋 Tabla de Contenidos

1. [Requisitos del Proyecto](#-requisitos-del-proyecto)
2. [Arquitectura y Tecnologías](#-arquitectura-y-tecnologías)
3. [Instalación y Ejecución](#-instalación-y-ejecución)
4. [Endpoints de la API](#-endpoints-de-la-api)

---

## 1. 📋 Requisitos del Proyecto

Este proyecto cumple con los tres niveles del desafío técnico:

* **Nivel 1:** Implementación de la función `boolean isMutant(String[] dna)` de forma **eficiente**.
* **Nivel 2:** Creación de una API REST con el *endpoint* `POST /mutant` que responde **200 OK** si es mutante o **403 Forbidden** si es humano.
* **Nivel 3:**
    * Uso de **H2 Database** para persistencia y deduplicación por **Hash SHA-256**.
    * Exposición del *endpoint* `GET /stats` para obtener estadísticas de verificación.
    * **Tests Automáticos** con **Code Coverage > 80%**.

---

## 2. 🏗️ Arquitectura y Tecnologías

El proyecto sigue una arquitectura de **capas** clara para separación de responsabilidades:

| Capa | Componente | Responsabilidad |
| :--- | :--- | :--- |
| **Controller** | `MutantController` | Maneja requests HTTP (200, 403, 400). |
| **Service** | `MutantService` / `StatsService` | Lógica de negocio, deduplicación por hash y cálculo de estadísticas. |
| **Algorithm** | `MutantDetector` | Implementa el algoritmo `isMutant` optimizado (Nivel 1). |
| **Repository** | `DnaRecordRepository` | Acceso a H2 Database (Spring Data JPA). |
| **Entity/DTO** | `DnaRecord` / `DnaRequest` | Modelos de datos y contratos de la API. |

### Tecnologías

* **Framework:** Spring Boot 3.3.0
* **Lenguaje:** Java 21
* **Build Tool:** Gradle 8.x
* **Base de Datos:** H2 Database (en memoria)
* **Utilidades:** Lombok, Spring Validation
* **Documentación:** SpringDoc OpenAPI (Swagger UI)
* **Testing:** JUnit 5, Mockito, JaCoCo

---

## 3. 🚀 Instalación y Ejecución

Asegúrate de tener instalado **Java JDK 21+** y **Git**.

### Paso 1: Clonar el Repositorio

git clone <https://github.com/Tiagopinfari/Integrador-Desarrollo-Software-Tiago-Pinfari.git>

cd examenmercado

### Paso 2: Ejecutar la Aplicación

El proyecto incluye un Gradle Wrapper, por lo que no necesitas tener Gradle instalado.

En Windows (PowerShell):

`.\gradlew.bat bootRun`

En Mac/Linux:

`./gradlew bootRun`

La aplicación estará corriendo en http://localhost:8080.

### Paso 3: Ejecutar los Tests

Para correr la suite completa de tests (unitarios y de integración) y generar el reporte de cobertura de JaCoCo:

En Windows (PowerShell):

`.\gradlew.bat clean test jacocoTestReport`

En Mac/Linux:

`./gradlew clean test jacocoTestReport`

Si el build termina en `BUILD SUCCESSFUL`, significa que todos los tests pasaron y la cobertura de JaCoCo fue superior al 80%.

### Paso 4: Acceder a la Documentación

Una vez iniciada, la documentación interactiva de la API (Swagger UI) está disponible en:

http://localhost:8080/swagger-ui.html

---

## 4. 🌐 Endpoints de la API

Una vez que la aplicación está corriendo, puedes acceder a los siguientes recursos:

### 1. Documentación de la API (Swagger)

Para ver todos los endpoints de forma interactiva y probarlos:

**URL**: http://localhost:8080/swagger-ui.html

### 2. Consola de la Base de Datos (H2)

Para ver los registros de ADN que se van guardando en la base de datos en memoria:

**URL**: http://localhost:8080/h2-console

**Datos de Conexión:**

**JDBC URL:** `jdbc:h2:mem:testdb`

**User Name:** `sa`

**Password:** (dejar vacío)

### 3. Endpoints Principales

**A. POST /mutant**

Verifica si un ADN es mutante.

| Atributo | Valor |
| :--- | :--- |
| **Método** | `Post` |
| **URL** | `/mutant` |
| **Body** | JSON con el array de ADN. |
| **Headers** | `Content-Type: application/json` |

**Respuestas**

| Código HTTP | Descripción |
| :--- | :--- |
| **200 OK** | El ADN pertenece a un Mutante. |
| **403 Forbidden** | El ADN pertenece a un Humano. |
| **400 Bad Request** | El formato del ADN es inválido (no NxN, caracteres erróneos, o null). |

**B. GET /stats**

Retorna las estadísticas de las verificaciones realizadas.

| Atributo | Valor |
| :--- | :--- |
| **Método** | `Get` |
| **URL** | `/stats` |
