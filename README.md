# Container QR Project — V1

Aplicación Android desarrollada para gestionar y visualizar códigos QR de cobro según una actividad y un tipo de pago.

La versión 1 implementa las funcionalidades principales de administración y consulta de códigos QR. Su interfaz corresponde a una versión inicial, anterior a las mejoras de diseño UX/UI previstas para las siguientes versiones.

## Funcionalidades

- Selección de actividades y tipos de pago.
- Visualización de códigos QR asociados a una actividad y un tipo de pago.
- Avisos cuando no existe una imagen QR configurada para la combinación seleccionada.

## Administración

Desde el módulo **Configuración** se pueden gestionar los datos utilizados por la aplicación:

- **Gestión de actividades:** crear, editar, activar y desactivar actividades.
- **Gestión de tipos de QR:** crear, editar, activar y desactivar tipos de pago.
- **Gestión de imágenes QR:** cargar, actualizar y eliminar imágenes asociadas a una combinación de actividad y tipo de pago.
- **Gestión de contraseña:** cambiar la contraseña de administración.

La contraseña inicial de administración es `admin321`. Se recomienda cambiarla durante la configuración inicial.

## Tecnología

- Android nativo con Java.
- Interfaces desarrolladas con layouts XML.
- SQLite local mediante `SQLiteOpenHelper`.
- SDK mínimo: Android 7.0 (API 24).
- Paquete: `com.bylaba.containerqr`.

## Ejecutar el proyecto

1. Abrir la carpeta del proyecto con Android Studio.
2. Esperar la sincronización de Gradle.
3. Seleccionar un emulador o dispositivo Android con API 24 o superior.
4. Ejecutar la configuración `app`.

También puede compilarse desde la terminal de Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Estructura principal

- `app/src/main/java/...`: actividades, modelos, DAOs y lógica de la aplicación.
- `app/src/main/res/layout`: interfaces XML.
- `app/src/main/java/.../database/DBHelper.java`: creación de las tablas y datos iniciales.

## Estado del proyecto

La V1 es una versión funcional que cubre las operaciones principales de consulta y administración de códigos QR. Su interfaz todavía no incorpora el rediseño UX/UI ni las mejoras de adaptación a diferentes tamaños de pantalla planteadas para la siguiente versión.
