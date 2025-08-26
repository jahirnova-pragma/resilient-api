# 🚀 Test Nequi API

Servicio REST para la gestión de **franquicias, sucursales y productos**, desarrollado en **Java 17** con **Spring Boot 3**, utilizando **WebFlux** y principios de **Clean Architecture**.  
El servicio integra **AWS DynamoDB** para persistencia y aplica **Resilience4j** para tolerancia a fallos y resiliencia.

---

## ⚙️ Requisitos previos

Antes de iniciar el proyecto, asegúrate de tener instalado:

- Java 17
- Gradle 8+
- AWS CLI configurado
- Acceso a DynamoDB para las tablas de franquicias, sucursales y productos

---

## 🔧 Variables de entorno y configuración

El servicio utiliza **application.yml** para configuración local y variables de entorno:  

```yaml
management:
  tracing:
    sampling:
      probability: 0.0

email-validator:
  base-url: "${EMAIL_VALIDATOR_BASE_URL:https://emailvalidation.abstractapi.com/v1/}"
  api-key: "${EMAIL_VALIDATOR_API_KEY:3b1d9eb55cfe489dae5ab293b4234e0d}"
  timeout: "${EMAIL_VALIDATOR_TIMEOUT:500}"

resilience4j:
  circuitbreaker:
    instances:
      emailValidator:
        failure-rate-threshold: 50
        slow-call-rate-threshold: 50
        slow-call-duration-threshold: 2s
        sliding-window-size: 5
        minimum-number-of-calls: 5
        wait-duration-in-open-state: 25s
  retry:
    instances:
      emailValidatorRetry:
        maxAttempts: 5
        waitDuration: 1000ms
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 1.5
        maxWaitDuration: 10000ms
        initialInterval: 500ms
  bulkhead:
    instances:
      emailValidatorBulkhead:
        maxConcurrentCalls: 5
        maxWaitDuration: 1s
```

---

## ✨ Features

- 🏢 Gestión de **franquicias y sucursales**
- 📦 Gestión de **productos y stock**
- 🔄 Actualizaciones de stock y nombres con **WebFlux reactive**
- ⚡ Resiliencia con **Resilience4j** (circuit breaker, retry, bulkhead)
- 🔌 Persistencia en **AWS DynamoDB**
- 🧩 Arquitectura modular basada en **Clean Architecture**
- 📚 Documentación automática con **OpenAPI / Springdoc**

---

## 🛠️ Tecnologías

- Java 17
- Spring Boot 3 + WebFlux
- Gradle
- AWS SDK v2 (DynamoDB)
- Resilience4j
- Lombok
- MapStruct
- JUnit 5 / Mockito
- Springdoc OpenAPI

---

## 📁 Estructura del Proyecto

```bash
resilient_api/
├── 📦 domain/                  # 🧩 Entidades y modelos de negocio
├── 📦 infrastructure/          # 🔌 Adaptadores y configuración
│   ├── entrypoints/            # 🖥️ Handlers y RouterFunctions
│   ├── repository/             # 🗄️ Acceso a DynamoDB
│   └── config/                 # ⚙️ Configuraciones generales
├── 📦 application/             # 🛠️ Casos de uso y lógica de negocio
├── ⚙️ gradle/                  # 🔄 Wrapper de Gradle
├── 📄 build.gradle             # 🏗️ Configuración raíz de Gradle
├── 📄 settings.gradle          # 🗂️ Configuración de módulos
├── 📄 application.yml          # ⚙️ Configuración de Spring Boot
├── 📄 README.md                # 📚 Documentación del proyecto
└── 🚫 .gitignore               # ❌ Archivos ignorados por Git
```

---

## ⚙️ Instalación

```bash
git clone <url-del-repo>
cd resilient_api
./gradlew clean build
```

---

## ▶️ Ejecutar localmente

```bash
./gradlew bootRun
```

---

## 📡 Endpoints principales

### Franquicias

#### Crear franquicia
- **POST** `/franchises`
- **Request:**
```json
{
  "name": "Franquicia ABC"
}
```
- **Response 201:**
```json
{
  "id": "fran-001",
  "name": "Franquicia ABC",
  "sucursales": []
}
```

#### Agregar sucursal
- **POST** `/franchises/{id}/sucursales`
- **Request:**
```json
{
  "name": "Sucursal Norte"
}
```
- **Response 200:**
```json
{
  "id": "suc-001",
  "name": "Sucursal Norte"
}
```

#### Obtener producto con mayor stock
- **GET** `/franchises/{id}/max-stock`
- **Response 200:**
```json
{
  "productId": "prod-123",
  "name": "Producto X",
  "stock": 500,
  "sucursalId": "suc-001"
}
```

#### Actualizar nombre de franquicia
- **PATCH** `/franchises/{id}`
- **Request:**
```json
{
  "name": "Franquicia Actualizada"
}
```
- **Response 200:**
```json
{
  "id": "fran-001",
  "name": "Franquicia Actualizada"
}
```

---

### Productos

#### Actualizar stock de un producto
- **PUT** `/products/{productId}/stock/{stock}`
- **Response 200:**
```json
{
  "productId": "prod-123",
  "stock": 1000
}
```

#### Actualizar nombre de producto
- **PATCH** `/products/{id}`
- **Request:**
```json
{
  "name": "Producto Nuevo"
}
```
- **Response 200:**
```json
{
  "id": "prod-123",
  "name": "Producto Nuevo"
}
```

---

### Sucursales

#### Agregar producto a una sucursal
- **POST** `/sucursales/{sucursalId}/productos/{productoId}`
- **Response 200:**
```json
{
  "sucursalId": "suc-001",
  "productoId": "prod-123"
}
```

#### Eliminar producto de una sucursal
- **DELETE** `/sucursales/{sucursalId}/productos/{productoId}`
- **Response 200:**
```json
{
  "message": "Producto eliminado correctamente"
}
```

#### Actualizar nombre de sucursal
- **PATCH** `/sucursales/{id}`
- **Request:**
```json
{
  "name": "Sucursal Actualizada"
}
```
- **Response 200:**
```json
{
  "id": "suc-001",
  "name": "Sucursal Actualizada"
}
```

---

## 🧪 Ejecutar pruebas

```bash
./gradlew test
```

---

## 🐳 Docker

Construir imagen:

```bash
docker build -t resilient-api .
```

Ejecutar contenedor:

```bash
docker run -p 8080:8080 resilient-api
```

---

## 🤝 Contribuciones

1. Haz un fork del repositorio  
2. Crea una rama `feature/nueva-funcionalidad`  
3. Haz commit y push  
4. Abre un Pull Request

---

## 📄 Licencia

**Propietario** © Nequi

