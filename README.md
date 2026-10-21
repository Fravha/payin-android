# Container QR Project

Proyecto de desarrollo de **Payin**, una aplicación Android para visualizar códigos QR de cobro según una actividad y un tipo de pago.

Actualmente incluye como datos iniciales las actividades **Librería** y **Cafetería**, junto con los tipos **Con factura** y **Sin factura**. Las imágenes QR se cargan desde el módulo de administración.

## Funcionalidades principales

- Selección de actividades y tipos de pago.
- Visualización de códigos QR configurados para cada combinación.
- Zoom mediante gestos táctiles y desplazamiento de la imagen QR.
- Interfaces adaptables (*responsive*) para teléfonos y tablets.
- Avisos cuando no existe una imagen QR configurada.

## Administración

Desde el botón **Configuración** de la pantalla principal se puede:

- Crear, editar, activar o desactivar actividades.
- Crear, editar, activar o desactivar tipos de QR.
- Cargar o actualizar una imagen QR para una combinación de actividad y tipo.
- Cambiar la contraseña de administración.

La contraseña inicial de administración es `admin321`. Se recomienda cambiarla durante la configuración inicial.

## Tecnología

- Android nativo con Java y layouts XML.
- SQLite local mediante `SQLiteOpenHelper`.
- Visualización de imágenes con soporte de zoom y desplazamiento táctil.
- Diseños adaptables mediante recursos y layouts de Android.
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

- `app/src/main/java/...`: actividades, modelos, DAOs y lógica de la aplicación.
- `app/src/main/res/layout`: interfaces XML.
- `app/src/main/res/values`: recursos generales, como colores, dimensiones y estilos.
- `app/src/main/res/layout-sw600dp`: layouts adaptados a pantallas de mayor tamaño, si están presentes en el proyecto.
- `app/src/main/java/.../database/DBHelper.java`: creación de las tablas y datos iniciales.
