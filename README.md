# Juego de Batallas: Héroes vs Villanos

Este es un juego de estrategia por turnos desarrollado en **Java** utilizando **Swing** para la interfaz gráfica y **SQLite** para la persistencia de datos. Los jugadores pueden crear personajes (Héroes o Villanos), enfrentarlos en batallas épicas y guardar el historial de sus combates.

## Características Principales

*   **Creación de Personajes**: Personaliza el nombre, apodo y estadísticas (vida, fuerza, defensa) de tus héroes y villanos.
*   **Sistema de Batalla por Turnos**: Combate estratégico donde cada personaje tiene turno para atacar.
*   **Mecánicas Avanzadas**:
    *   **Invocación de Armas**: Posibilidad de obtener armas especiales durante el combate.
    *   **Ataques Supremos**: Poderosos ataques que pueden cambiar el curso de la batalla.
*   **Historial y Ranking**:
    *   Guardado automático de estadísticas en base de datos SQLite.
    *   Visualización de historial de batallas detallado.
    *   Tabla de posiciones (Ranking) basada en victorias.
*   **Persistencia**: Todos los datos (personajes, batallas, récords) se guardan localmente en `src/main/java/batalla/database/BDjuego.db`.

## Requisitos del Sistema

Para ejecutar este proyecto correctamente necesitas:

*   **Java Development Kit (JDK) 22** o superior.
*   **Apache Maven** (para la gestión de dependencias).
*   Un IDE compatible con Java/Maven (Recomendado: NetBeans, IntelliJ IDEA, Eclipse).

## Instalación y Ejecución

### 1. Clonar o Descargar
Descarga el código fuente de este repositorio en tu computadora.

### 2. Abrir en IDE
*   Abre tu IDE de preferencia (ej. NetBeans).
*   Selecciona `File > Open Project` y navega a la carpeta root del proyecto (donde está el `pom.xml`).

### 3. Construir el Proyecto (Build)
Ejecuta la construcción con Maven para descargar la dependencia de SQLite (`sqlite-jdbc`).
*   En NetBeans: Click derecho en el proyecto -> `Clean and Build`.
*   En Terminal: `mvn clean install`

### 4. Ejecutar la Aplicación
La clase principal que inicia la aplicación es `batalla.main.Main`.
*   En el IDE: Busca el archivo `src/main/java/batalla/main/Main.java`, click derecho -> `Run File`.
*   Si el IDE te pregunta la clase principal al dar "Run Project", selecciona `batalla.main.Main`.

## Guía de Uso

1.  **Menú Principal**:
    *   **Jugar**: Inicia el flujo de una nueva batalla.
    *   **Ranking**: Muestra los personajes con más victorias.
    *   **Historial**: Muestra la lista de batallas pasadas.
    *   **Salir**: Cierra la aplicación.

2.  **Nueva Batalla**:
    *   Ingresa los datos para el **Héroe** y el **Villano**.
    *   ¡Cuidado con el **Apodo**! Es único. Si usas un apodo existente, cargarás las estadísticas de ese personaje.
    *   Configura los puntos de **Fuerza**, **Defensa** y **Vida**. Asegúrate de que los puntos totales no excedan el límite permitido (200).

3.  **Combate**:
    *   Usa el botón **"Iniciar"** para comenzar la simulación.
    *   Puedes pausar la batalla en cualquier momento.
    *   Observa el *Combat Log* para ver los eventos turno a turno.

4.  **Resultados**:
    *   Al finalizar, verás una pantalla con el ganador y las estadísticas.
    *   Haz clic en **"Guardar Partida"** para registrar la batalla en la base de datos. ¡No olvides este paso si quieres que cuente para el Ranking!

## Estructura del Proyecto

*   `src/main/java/batalla/modelo`: Clases lógicas (Personaje, Héroe, Villano, Armas).
*   `src/main/java/batalla/vista`: Ventanas e interfaz gráfica (JFrames).
*   `src/main/java/batalla/controlador`: Lógica de control que une la vista y el modelo.
*   `src/main/java/batalla/Conexion`: Gestión de base de datos (DAOs y Conexión SQLite).

## Créditos y Recursos

Durante el desarrollo se usaron las siguientes ayudas y recursos:

- Google Antigravity para testing y búsqueda de errores.
- Chat-GPT para la ayuda en el desarrollo. Link Conversación: https://chatgpt.com/share/693f8f7e-bfc4-8007-b5b2-949c9e3ddf42
- Video Explicativo https://youtu.be/1OBMvz_zkRo
