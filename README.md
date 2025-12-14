# IEFI - Batallas (Proyecto Java Swing)

Este repositorio contiene una aplicación de batalla (Héroe vs Villano) hecha en Java con arquitectura MVC y una interfaz gráfica basada en Swing.

## Instrucciones de ejecución

Requisitos mínimos:
- JDK 11 o JDK 17 (recomendado: JDK 17 LTS). Asegúrate de que `java` y `javac` apunten a la versión instalada.
- Maven 3.x para construir el proyecto.

Dependencias:
- No se usan librerías externas aparte de las que proporciona Java SE (Swing). El proyecto utiliza Maven para gestión de compilación pero no requiere dependencias adicionales en `pom.xml` por defecto.

## Comportamiento de persistencia (Historial de partidas)

- La persistencia de batallas se realiza ahora mediante una base de datos **SQLite**.
- Al guardar una partida (`btnGuardarPartida`) en la pantalla de resultados, se inserta un registro en la tabla `historial_batallas`.
- Este registro incluye: fecha, héroe, villano, ganador, turnos, estadísticas detalladas y el **Combat Log** completo.
- Además, se actualizan las estadísticas acumuladas (victorias, derrotas, etc.) de los personajes involucrados en la tabla `personajes`.
- La pantalla de historial y los detalles (`formHistorial`) recuperan la información directamente de la base de datos utilizando `BatallaDAO`.

## Estructura del proyecto (resumen)

```
IEFI-InterfazGrafica/
├─ pom.xml
├─ README.md

├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  ├─ batalla/
│  │  │  │  ├─ controlador/         # controladores MVC
│  │  │  │  │  ├─ ControladorHistorial.java
│  │  │  │  │  ├─ ControladorResultado.java
│  │  │  │  │  └─ ...
│  │  │  │  ├─ modelo/              # clases del dominio y persistencia
│  │  │  │  │  ├─ GestorPersistencia.java
│  │  │  │  │  ├─ PartidaGuardada.java
│  │  │  │  │  ├─ Heroe.java
│  │  │  │  │  └─ Villano.java
│  │  │  │  └─ vista/               # vistas Swing (JFrame / formularios)
│  │  │  │     ├─ PantallaHistorial.java
│  │  │  │     ├─ PantallaResultado.java
│  │  │  │     └─ formHistorial.java
│  │  │  └─ batalla/MainLauncher.java
│  │  └─ resources/
│  └─ test/
└─ target/
```

## Integrantes y roles

- Tomás — Modelo y Main
- Lourdes — Controlador
- Ulises — Vistas

## Uso de IA / Herramientas externas

Durante el desarrollo se usaron las siguientes ayudas y recursos:

- GitHub Copilot
- Chat-GPT
- YouTube

Prompts Ulises: https://chatgpt.com/share/6915c3a6-3e64-8010-8ad7-d0dc58ad400e
Video Youtube Ulises: https://www.youtube.com/watch?v=L2xczUN9aI0 / https://www.youtube.com/watch?v=LP7_DlIe670

## Video explicando
- https://youtu.be/KJyrmycQ5Vc
