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

### 📡 Endpoints principales

- Los endpoints principales están documentados en este repo: [testNequiTerraform](https://github.com/jahirnova-pragma/testNequiTerraform)
- Encontrarás la carpeta **Postman** y la documentación de **Swagger** dentro del proyecto.

---


### 🚀 Despliegue Local y AWS

- El despliegue loca y de terraform para AWS lo encontraras en el siguiente repo: [testNequiTerraform](https://github.com/jahirnova-pragma/testNequiTerraform),
alli encontrarás la carpeta **Local** y **Terraform**  ademas de la documentacion de como ejecutar
- Los despliegues se hacen automaticos al subir cambios a main
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

