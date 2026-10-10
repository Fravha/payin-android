# Diagnóstico técnico — Container QR Project

## Alcance y evidencia

Este documento se elaboró a partir del código fuente disponible en el repositorio, especialmente el módulo `app`. No se infirieron requisitos ni comportamientos que no estén representados en ese código. La compilación de verificación se inició con `:app:assembleDebug`, pero no concluyó dentro del tiempo de análisis, por lo que este documento no certifica una ejecución en dispositivo.

## Resumen ejecutivo

La aplicación es un contenedor local de imágenes de códigos QR para cobros. El cajero selecciona una **actividad** y luego un **tipo de QR**; la aplicación busca y muestra la imagen asociada a esa combinación. La base de datos inicial crea dos actividades (`Librería` y `Cafetería`) y dos tipos (`Con factura` y `Sin factura`), lo que soporta las cuatro combinaciones descritas. Sin embargo, las imágenes de los cuatro QR no vienen incluidas: deben cargarse desde la administración.

Incluye una administración protegida por contraseña que permite crear, editar, activar o desactivar actividades y tipos de QR, y cargar o reemplazar la imagen de un QR para cada combinación activa. La contraseña inicial `admin321` está almacenada como texto plano en SQLite. Aunque hay un botón para cambiarla y un DAO capaz de actualizar valores de configuración, ese flujo no está implementado.

## Comportamiento actual

### Flujo de caja

1. `MainActivity` consulta las actividades activas en SQLite y crea un botón para cada una.
2. Al elegir una actividad, `TipoQrActivity` lista todos los tipos de QR activos.
3. Al elegir un tipo, `MostrarQrActivity` busca un registro QR activo por `actividad_id` y `tipo_qr_id`.
4. Si encuentra una ruta de imagen válida, la carga en un `ImageView`; si no, muestra un mensaje de que no hay QR configurado o que falta la imagen.

El uso no está limitado de forma rígida a cuatro QR: desde administración pueden agregarse más actividades o tipos y, por ello, más combinaciones. Tampoco se genera, decodifica ni escanea contenido QR; la app solamente selecciona y muestra archivos de imagen que representan QR.

### Datos iniciales

Al crear por primera vez la base local `container_qr.db`, `DBHelper` registra:

- Actividades activas: `Librería` y `Cafetería`.
- Tipos activos: `Con factura` y `Sin factura`.
- Configuración `password_configuracion` con valor `admin321`.

No inserta registros QR ni imágenes predeterminadas.

## Funcionalidades implementadas

| Área | Implementado |
| --- | --- |
| Selección de actividad | Lista dinámica de actividades activas. |
| Selección de tipo de QR | Lista dinámica de tipos activos. |
| Visualización | Muestra una imagen almacenada como URI para la combinación elegida. |
| Acceso administrativo | Diálogo de contraseña desde la pantalla inicial y validación contra SQLite. |
| Actividades | Alta, edición, activación y desactivación lógica. |
| Tipos de QR | Alta, edición, activación y desactivación lógica. |
| Imágenes QR | Selección con el selector de documentos de Android, vista previa y guardado/actualización por combinación actividad–tipo. |
| Persistencia de imágenes | Se intenta conservar el permiso de lectura de la URI seleccionada mediante `OpenDocument` y `takePersistableUriPermission`. |

## Administración y configuraciones disponibles

`ConfigActivity` ofrece tres secciones operativas:

- **Administrar actividades:** nombre obligatorio, descripción opcional, edición y cambio de estado activo/inactivo.
- **Administrar tipos de QR:** nombre obligatorio, descripción opcional, edición y cambio de estado activo/inactivo.
- **Administrar imágenes QR:** selección de actividad y tipo activos, carga de una imagen, vista previa y creación/actualización del registro QR.

También muestra **Cambiar contraseña**, pero actualmente solo presenta el mensaje temporal “Cambiar contraseña”; no solicita ni persiste una nueva clave. Tampoco se encontró un flujo de “olvidé mi contraseña”, restablecimiento de fábrica, exportación, importación ni copia de seguridad controlada por la aplicación. Por tanto, el comportamiento de reseteo total descrito como contexto no está implementado explícitamente en el código revisado.

## Estructura técnica

### Plataforma y dependencias

- Un solo módulo Android: `:app`.
- Lenguaje principal: Java; interfaces declaradas en XML clásico, no Jetpack Compose.
- Paquete y `applicationId`: `com.bylaba.containerqr`.
- SDK mínimo 24, SDK objetivo 36, versión de app `1.0` / código 1.
- Dependencias principales: AndroidX AppCompat, Activity KTX, ConstraintLayout y Material Components.
- Persistencia: SQLite nativo con `SQLiteOpenHelper`; no se usa Room, red, API remota ni autenticación externa.

### Pantallas (Activities)

| Clase | Layout | Responsabilidad |
| --- | --- | --- |
| `MainActivity` | `activity_main.xml` | Inicio, listado de actividades y entrada a administración. |
| `TipoQrActivity` | `activity_tipo_qr.xml` | Listado de tipos para la actividad recibida por `Intent`. |
| `MostrarQrActivity` | `activity_mostrar_qr.xml` | Consulta y presentación de la imagen QR. |
| `ConfigActivity` | `activity_config.xml` | Menú de administración. |
| `GestionActividadesActivity` | `activity_gestion_actividades.xml` | CRUD parcial de actividades (sin borrado físico). |
| `GestionTiposQrActivity` | `activity_gestion_tipos_qr.xml` | CRUD parcial de tipos (sin borrado físico). |
| `GestionQrsActivity` | `activity_gestion_qrs.xml` | Asociación y actualización de imagen QR por combinación. |

No hay fragments, adaptadores propios, servicios, receptores, ViewModels ni repositorios.

### Modelos y datos

Los modelos son POJO simples en `models/`:

- `Actividad`: `id`, `nombre`, `descripcion`, `activo`.
- `TipoQr`: `id`, `nombre`, `descripcion`, `activo`.
- `Qr`: `id`, `actividadId`, `tipoQrId`, `nombre`, `rutaImagen`, `activo`.
- `Configuracion`: `id`, `clave`, `valor`. Está definido, aunque el DAO de configuración trabaja directamente con cadenas y no devuelve este modelo.

Las tablas SQLite y sus relaciones son:

| Tabla | Datos | Reglas destacables |
| --- | --- | --- |
| `actividades` | Nombre, descripción y estado. | `nombre` obligatorio. |
| `tipos_qr` | Nombre, descripción y estado. | `nombre` obligatorio. |
| `qrs` | Actividad, tipo, nombre, URI de imagen y estado. | Claves foráneas declaradas y unicidad por `(actividad_id, tipo_qr_id)`. |
| `configuraciones` | Pares clave/valor. | `clave` única; hoy guarda la contraseña. |

Los DAOs `ActividadDAO`, `TipoQrDAO`, `QrDAO` y `ConfiguracionDAO` concentran las consultas y actualizaciones SQLite. Se usan desactivaciones lógicas para actividades, tipos y QR; la interfaz no expone la desactivación de QR.

### Recursos

- Siete layouts de pantalla en `res/layout`.
- Recursos de tema, colores mínimos e iconos de launcher estándar.
- `strings.xml` contiene solamente el nombre de la aplicación; gran parte de los textos, colores y tamaños están escritos directamente en XML o Java.
- Hay reglas estándar de backup y extracción de datos, pero no una política funcional personalizada para la base o las imágenes.

## Observaciones, riesgos y limitaciones

### Seguridad y recuperación

- La contraseña inicial `admin321` es visible en el código (`DBHelper`) y se persiste/compara en texto plano. No hay hash, sal, protección del almacenamiento, límite de intentos ni auditoría.
- El cambio de contraseña no está conectado al método existente `ConfiguracionDAO.actualizarValor`; por ahora la clave no puede ser modificada desde la app.
- Las pantallas administrativas no verifican una sesión o autorización propia al abrirse. El acceso normal desde `MainActivity` sí pide contraseña y las actividades no están exportadas, pero una autorización centralizada haría el control más resistente y verificable.
- No existe recuperación de contraseña. Tampoco hay restablecimiento controlado ni mecanismo para conservar/exportar la configuración antes de perderla.

### Integridad y evolución de datos

- `DBHelper.onUpgrade()` elimina las cuatro tablas y las recrea. Al incrementar la versión de base para una futura actualización, se perderían actividades, tipos, contraseñas y rutas de QR existentes.
- Aunque las claves foráneas se declaran en SQL, no se habilita explícitamente su aplicación con `setForeignKeyConstraintsEnabled(true)`. Por ello no se puede asegurar desde el código que SQLite esté haciendo cumplir esas relaciones en tiempo de ejecución.
- `QrDAO.guardarOActualizarQr()` solo busca un QR existente que esté activo. Si existe uno inactivo para esa combinación, intentará insertarlo de nuevo y puede chocar con la restricción única `(actividad_id, tipo_qr_id)`.
- Desactivar una actividad o tipo no modifica los QR relacionados. Esto no rompe el flujo normal porque solo se listan los activos, pero deja registros asociados sin una política explícita de cascada o reactivación.
- La imagen se guarda como URI de un proveedor de documentos externo, no se copia al almacenamiento propio de la app. Si el archivo se elimina, cambia o el permiso deja de ser válido, el QR no se puede mostrar y debe cargarse de nuevo. El código contempla algunos errores al cargar imágenes existentes, pero no todos los posibles fallos al solicitar el permiso persistente.

### Calidad técnica y experiencia de uso

- Todas las consultas SQLite y cargas de imagen se realizan desde actividades, previsiblemente en el hilo principal. Con pocos datos es simple; con más datos o archivos grandes puede afectar la interfaz.
- La interfaz genera listas completas como vistas programáticas dentro de `LinearLayout`; no escala ni recicla elementos como lo haría `RecyclerView`.
- Hay duplicación entre la gestión de actividades y la de tipos QR, junto con imports no usados o duplicados. La lógica y presentación están muy acopladas a cada `Activity`.
- Faltan validaciones de nombres duplicados, longitudes, estados de spinners vacíos y mensajes de error más estructurados. Los nombres no tienen restricciones únicas en la base.
- La pantalla principal usa `ConstraintLayout`, pero el botón de configuración no tiene restricciones de `ConstraintLayout`; su colocación visual no está definida por restricciones y puede superponerse o ubicarse de forma no deseada.
- Se activa modo edge-to-edge en varias pantallas, pero solo `MainActivity` ajusta explícitamente los insets de barras del sistema. El resto puede mostrar contenido bajo las barras según el dispositivo/tema.
- Los textos visibles contienen caracteres que aparecen codificados incorrectamente al leer algunos archivos (por ejemplo, `ConfiguraciÃ³n`). Debe verificarse la codificación UTF-8 real en el editor y en la APK para evitar textos corruptos.
- Los dos tests presentes son los ejemplos iniciales de Android Studio (`2 + 2 = 4` y comprobación del paquete); no validan el flujo de negocio, DAOs, permisos ni interfaz.

## Recomendaciones para continuar sin romper lo existente

1. **Primero preservar los datos.** Antes de subir `DATABASE_VERSION`, sustituir el `onUpgrade()` destructivo por migraciones incrementales y probarlas con una base que ya contenga los cuatro QR configurados. Definir además exportación/importación o una copia de seguridad para instalación nueva, cambio de equipo y recuperación.
2. **Completar la administración de contraseña de manera segura.** Implementar cambio de clave con validación de clave actual, confirmación y almacenamiento derivado con hash/sal; centralizar la comprobación de acceso para las pantallas administrativas. Definir explícitamente el procedimiento de recuperación antes de implementar un reseteo.
3. **Estabilizar la gestión de QR.** Mantener una sola fila por combinación incluso si está inactiva, corregir la búsqueda para actualizar/reactivar ese registro y decidir una política clara para QR ligados a actividades o tipos inactivos. Evaluar copiar las imágenes a almacenamiento privado de la app, o documentar y comprobar de forma robusta el permiso persistente de URI.
4. **Separar responsabilidades gradualmente.** Conservar los modelos y el esquema actual como contrato inicial, pero introducir repositorios/capa de casos de uso y `ViewModel` antes de crecer en más pantallas. Room puede adoptarse mediante una migración planificada, no reemplazando SQLite de golpe.
5. **Mejorar interfaz y mantenibilidad sin alterar el flujo.** Mover textos, colores y dimensiones a recursos; corregir constraints e insets; reemplazar listas dinámicas por `RecyclerView` cuando haya más registros; y unificar la gestión repetida de entidades activables.
6. **Crear una red mínima de pruebas.** Agregar pruebas de creación inicial, búsqueda/actualización por combinación, activación/desactivación, migración de base, validación de contraseña y casos de URI inválida. Ejecutarlas antes de cada cambio en la capa de datos.
7. **Definir los límites funcionales siguientes.** El código no determina si el negocio requerirá escanear QR, generar QR desde un valor de pago, múltiples sucursales, usuarios, historial o sincronización. Conviene decidirlo antes de rediseñar la persistencia, porque actualmente la app es solo un visor local configurable.

## Conclusión

El núcleo actual cumple el modelo de selección **actividad → tipo → imagen QR** y está orientado correctamente al caso de Librería/Cafetería con factura/sin factura. La base es funcional como prototipo local configurable, pero la seguridad de administración, la preservación de datos en actualizaciones, la robustez de las URIs de imagen y las pruebas son los puntos prioritarios antes de distribuirla o ampliar su alcance.
