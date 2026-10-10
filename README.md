# Container QR

Aplicación Android para mostrar códigos QR de cobro según una actividad y un tipo de pago.

Actualmente incluye como datos iniciales las actividades **Librería** y **Cafetería**, junto con los tipos **Con factura** y **Sin factura**. Las imágenes QR se cargan desde el módulo de administración.

## Flujo de uso

1. Seleccionar una actividad.
2. Seleccionar el tipo de QR.
3. Visualizar la imagen QR configurada para esa combinación.

Si aún no se cargó una imagen, la app muestra un aviso.

## Administración

Desde el botón **Configuración** de la pantalla principal se puede:

- Crear, editar, activar o desactivar actividades.
- Crear, editar, activar o desactivar tipos de QR.
- Cargar o actualizar una imagen QR para una combinación de actividad y tipo.

La contraseña inicial de administración es `admin321`.

> Recomendación: Cambiar la contraseña para proteger el cambio en la configuración.

## Tecnología

- Android nativo con Java y layouts XML.
- SQLite local mediante `SQLiteOpenHelper`.
- SDK mínimo: Android 7.0 (API 24).
- Paquete: `com.bylaba.containerqr`.

## Ejecutar el proyecto

1. Abrir la carpeta del proyecto con Android Studio.
2. Esperar la sincronización de Gradle.
3. Elegir un emulador o dispositivo Android con API 24 o superior.
4. Ejecutar la configuración `app`.

También puede compilarse desde la terminal de Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Estructura principal

- `app/src/main/java/...`: actividades, modelos, DAOs y acceso a SQLite.
- `app/src/main/res/layout`: interfaces XML.
- `app/src/main/java/.../database/DBHelper.java`: creación de las tablas y datos iniciales.
