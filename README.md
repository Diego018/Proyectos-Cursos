### Testing: 5 Puntos Críticos del Smoke Test para el Backend de Cultivapp
Esta sección documenta los 5 puntos críticos del smoke test para el backend de Cultivapp. Estos tests se ejecutan en cada despliegue para verificar que las funcionalidades principales del servidor están operativas.

 # 1. Login (Endpoint de Autenticación)
¿Qué flujo cubre y por qué debe hacer parte del smoke test?

Este test cubre el endpoint de autenticación que valida las credenciales del usuario y genera un token JWT. Cuando se recibe una petición POST a /api/auth/login con email y contraseña, el sistema verifica las credenciales contra la base de datos, y si son correctas, genera y retorna un token JWT válido junto con la información básica del usuario. Este endpoint es la puerta de entrada al sistema y sin él funcionando, ningún usuario puede acceder a la aplicación.

¿Cómo se ejecuta?

Enviar una petición POST a /api/auth/login con el body:

JSON
{
  "email": "productor@cultivapp.com",
  "password": "password"
}
Verificar que la respuesta tiene status 200 OK.

Verificar que la respuesta contiene:

Un token JWT válido.

Información del usuario (id, email, rol).

No contiene la contraseña en la respuesta.

Verificar que el token generado tiene la estructura correcta (header.payload.signature).

Opcionalmente, verificar que con credenciales inválidas retorna 401 Unauthorized.

¿Qué tipo de test lo prueba?

Integration: Prueba la integración entre el controller de autenticación, el servicio de usuarios, el repositorio, la base de datos y la generación de JWT.

Se puede complementar con tests Unit para las funciones individuales de validación de contraseñas y generación de tokens.

# 2. CRUD de Cultivos - Creación y Consulta
¿Qué flujo cubre y por qué debe hacer parte del smoke test?

Este test cubre los endpoints de creación y consulta de cultivos, que son las operaciones más críticas del sistema. La creación permite insertar un nuevo cultivo asociado a un usuario y una especie, mientras que la consulta permite obtener la lista de cultivos. Sin estas operaciones, la funcionalidad core de Cultivapp no existe. Este test verifica que el backend puede recibir datos de cultivos, validarlos, persistirlos en la base de datos, y posteriormente recuperarlos.

¿Cómo se ejecuta?

Creación de cultivo:

Obtener un token JWT válido (autenticarse primero).

Enviar una petición POST a /api/cultivos con headers de autorización:

Authorization: Bearer {token}

Body de la petición:

JSON
{
  "nombre": "Tomates Cherry Test",
  "especieId": 1,
  "fechaSiembra": "2024-11-01",
  "area": 50.5,
  "ubicacion": "Parcela Norte"
}
Verificar que la respuesta tiene status 201 Created.

Verificar que retorna el objeto del cultivo creado con su ID asignado.

Consulta de cultivos:

Enviar una petición GET a /api/cultivos con el token de autorización.

Verificar que la respuesta tiene status 200 OK.

Verificar que retorna un array de cultivos.

Verificar que cada cultivo contiene las propiedades esperadas.

Verificar que solo retorna los cultivos del usuario autenticado.

¿Qué tipo de test lo prueba?

Integration: Prueba la integración completa desde el controller hasta la base de datos, incluyendo validaciones, seguridad (JWT) y persistencia.

Puede complementarse con tests Unit para las validaciones de negocio específicas.

# 3. Validación de Tokens JWT
¿Qué flujo cubre y por qué debe hacer parte del smoke test?

Este test verifica que el sistema de validación de tokens JWT funciona correctamente. Incluye verificar que tokens válidos permiten acceso a recursos protegidos, que tokens expirados son rechazados, y que tokens manipulados o inválidos no otorgan acceso. La seguridad de toda la aplicación depende de este mecanismo, ya que protege todos los endpoints que requieren autenticación. .

¿Cómo se ejecuta?

Escenario 1 - Token válido: Acceder a un endpoint protegido (GET /api/cultivos) con un token válido.

Verificar que la respuesta es 200 OK y retorna los datos solicitados.

Escenario 2 - Token inválido/manipulado: Intentar acceder con un token falso o modificado.

Verificar que la respuesta es 401 Unauthorized y que el mensaje de error indica token inválido.

Escenario 3 - Sin token: Intentar acceder sin incluir el header Authorization.

Verificar que la respuesta es 401 Unauthorized.

Escenario 4 - Token expirado: Intentar acceder con un token que ha expirado.

Verificar que la respuesta es 401 Unauthorized y que el mensaje indica que el token ha expirado.

¿Qué tipo de test lo prueba?

Unit/Integration: Tests unitarios para las funciones de validación de JWT, y tests de integración para verificar que el filtro/middleware de seguridad funciona correctamente en el flujo completo de peticiones.

# 4. Catálogo de Especies
¿Qué flujo cubre y por qué debe hacer parte del smoke test?

Este test cubre el endpoint que retorna el catálogo completo de especies de plantas disponibles en el sistema. Las especies son entidades maestras fundamentales porque definen las características y requerimientos de cada tipo de cultivo. Sin acceso a este catálogo, no se pueden crear cultivos válidos. Este test verifica que el endpoint responde correctamente y que la base de datos contiene las especies necesarias.

¿Cómo se ejecuta?

Autenticarse y obtener un token JWT válido.

Enviar una petición GET a /api/especies con el token de autorización:

Authorization: Bearer {token}

Verificar que la respuesta tiene status 200 OK.

Verificar que retorna un array de especies.

Verificar que cada especie contiene las propiedades esperadas: id, nombreComun, nombreCientifico, diasCosecha, requerimientosAgua, y otras características según el modelo.

Verificar que hay al menos una especie en el sistema (datos iniciales cargados).

Opcional - Test de creación (solo para rol ADMIN): Autenticarse como ADMIN, enviar POST a /api/especies con datos, y verificar respuesta 201 Created.

¿Qué tipo de test lo prueba?

Integration: Prueba la integración entre el controller, el servicio de especies, el repositorio y la base de datos.

Puede incluir tests Unit para validaciones específicas de los datos de especies.

# 5. Conexión a Base de Datos (PostgreSQL)
¿Qué flujo cubre y por qué debe hacer parte del smoke test?

Este test verifica que la conexión a la base de datos PostgreSQL está establecida y funcionando correctamente. Sin conexión a base de datos, ninguna operación de persistencia funciona y la aplicación es completamente inútil. Este test debe ejecutarse al inicio del smoke test para fallar rápido si hay problemas de infraestructura básica.

¿Cómo se ejecuta?

Opción 1 - Health Check endpoint (Recomendada):

Crear un endpoint simple (/health o /api/health) que verifique la conexión.

Enviar una petición GET a /health. El endpoint debe ejecutar una query simple a la BD (ejemplo: SELECT 1).

Verificar que la respuesta es 200 OK.

Verificar que el body indica que la BD está conectada:

JSON
{
  "status": "UP",
  "database": "CONNECTED",
  "timestamp": "2024-11-10T20:00:00Z"
}
Opción 2 - Verificación mediante operación real:

Ejecutar cualquier consulta simple a la base de datos (ejemplo: contar usuarios) usando el repositorio directamente en el test:

Java
@Test
void testDatabaseConnection() {
  long count = userRepository.count(); 
  assertTrue(count >= 0, "Database connection failed");
}
Verificar que la operación se completa sin excepciones.

Verificaciones adicionales: Verificar que las tablas principales existen y que los datos iniciales se cargaron correctamente.

¿Qué tipo de test lo prueba?

Integration: Prueba la integración entre Spring Boot, JPA/Hibernate y la base de datos PostgreSQL. Es fundamentalmente un test de infraestructura que debe ejecutarse primero.

Puede complementarse con tests Unit para las configuraciones de conexión.

# Notas de Implementación
Estructura de Tests

Los tests de integración deben usar @SpringBootTest o @WebMvcTest según el alcance.

Los tests unitarios deben usar mocks con Mockito.

Se recomienda usar @Sql para cargar datos de prueba específicos.

Ejecución y Ambiente

Todos los smoke tests deben ser independientes y no depender del orden de ejecución.

El tiempo total de ejecución de los smoke tests debe ser menor a 10 minutos.

Usar perfiles de Spring (@ActiveProfiles("test")) para aislar la configuración de pruebas.

La base de datos utilizada es PostgreSQL
