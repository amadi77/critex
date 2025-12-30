# Critex - Dynamic Query Generator

Critex is a powerful Java library designed to simplify the generation of dynamic queries in Spring Boot applications using **Spring Data JPA Specifications**. It allows developers to build complex, filterable, and paginated queries with ease, reducing boilerplate code in the service and repository layers.

## Project Structure

The project is organized as a multi-module Maven project:

- **`core`**: Contains the main library logic, including specification generators and abstract service implementations.
- **`test-module`**: A sample Spring Boot application demonstrating how to integrate and use the `core` library with entities like `User`, `Post`, and `Comment`.

## Core Module Features

The `core` module provides a set of tools to transform DTOs or filter objects into JPA Specifications automatically.

### Logic Behind the Library

The library's core philosophy is to decouple query definitions from technical JPA implementation.

1.  **Dynamic Specification Building**: `SpecificationGenerator` recursively traverses `ReportCondition` and its `JoinReport` children. It uses the `CriteriaBuilder` API to create `Predicate` objects for each filter based on the `Operator` enum.
2.  **Join and Fetch Management**: It distinguishes between `Join` (filtering on related entities) and `Fetch` (eagerly loading related entities to avoid N+1 problems).
3.  **Generic Type Extraction**: The `Parameterized<T>` class uses reflection to automatically determine the entity type `T` at runtime, ensuring that Specifications are correctly typed without manual configuration.
4.  **Fluent API**: `ReportCondition` and `ReportFilter` provide a fluent interface for building complex logical trees (AND/OR) that map directly to SQL `WHERE` clauses.

### Key Classes

#### 1. `SpecificationGenerator<T>`
An abstract base class that handles the heavy lifting of converting `ReportCondition` and `ReportFilter` objects into JPA `Predicate` and `Specification`.
- Supports complex joins (inner and outer).
- Supports fetching (lazy to eager conversion for reports).
- Handles a wide range of operators (Equality, String matching, Numeric comparisons, Null checks, Collections, etc.).

#### 2. `AbstractService<T, R>`
A generic base service class that extends `SpecificationGenerator`. It provides standard CRUD and search operations:
- `findAll(ReportCondition, PageRequestParam)`: Retrieves entities based on filters and pagination.
- `findAllPage(ReportCondition, PageRequestParam)`: Returns a `Page<T>` for Spring Data compatibility.
- `getEntityById(Object id, Collection<String> joins)`: Retrieves a single entity with optional eager fetching of associations.
- `exists(ReportCondition)`: Checks for existence based on dynamic filters.
- `search(String searchText, List<String> searchFields, ...)`: Performs global search across multiple fields.

#### 3. `ReportCondition` & `ReportFilter`
Fluent DTOs used to define query criteria:
- **`ReportFilter`**: A container for `ConditionParameter` objects (field, value, operator). Supports `AND` and `OR` logic.
- **`ReportCondition`**: The top-level object containing the main filter, joins, sorting, and pagination details.

#### 4. `Operator` Enum
Defines the supported operations, including:
- `EQUALS`, `NOT_EQUALS`, `LIKE`, `CONTAINS`, `STARTS_WITH`, `ENDS_WITH` (with Case-Insensitive variants).
- `GREATER_THAN`, `LESS_THAN`, `BETWEEN`, `IN`, `NOT_IN`.
- `NULL`, `NOT_NULL`, `IS_EMPTY`, `IS_NOT_EMPTY`.
- `SIZE_EQUALS`, `SIZE_GREATER_THAN`, etc. (for collection size).

## Usage Example

### 1. Repository Setup
Your repository should extend both `JpaRepository` and `JpaSpecificationExecutor`.

```java
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {}
```

### 2. Service Implementation
Extend `AbstractService` and implement a method to map your Filter DTO to a `ReportCondition`.

```java
@Service
public class UserService extends AbstractService<User, UserRepository> {
    public UserService(UserRepository repository) {
        super(repository);
    }

    public ReportCondition generateReport(UserFilter filter) {
        ReportCondition condition = new ReportCondition();
        condition.addContainsIgnoreCase("username", filter.getUsername());
        condition.addEqual("email", filter.getEmail());
        
        // Dynamic Join and filtering on associated entities
        if (filter.getPostTitle() != null) {
            JoinReport postJoin = JoinReport.of("posts", JoinType.INNER, false);
            postJoin.getFilter().addLike("title", filter.getPostTitle());
            condition.addJoinReport(postJoin);
        }
        return condition;
    }
    
    // Wrap protected methods for public access
    public Page<User> getAll(ReportCondition condition, PageRequestParam pageRequest) {
        return findAllPage(condition, pageRequest);
    }
}
```

### 3. Controller Integration
Use the service to handle incoming requests with filtering and pagination.

```java
@GetMapping
public List<UserResponse> list(@ParameterObject UserFilter filter, @ParameterObject PageRequestParam pageRequest) {
    ReportCondition condition = userService.generateReport(filter);
    Page<User> page = userService.getAll(condition, pageRequest);
    return page.map(UserResponse::new).getContent();
}
```

## Packaging and Dependencies

The library is packaged as a JAR and requires the following dependencies (provided by `spring-boot-starter-data-jpa`):
- Spring Data JPA
- Jakarta Persistence API
- Hibernate (or any JPA provider)
- Lombok (for boilerplate reduction)

## How to Add to Your Project

To use Critex in your own Spring Boot project:

1.  **Install to Local Maven Repository**:
    ```bash
    mvn clean install -pl core
    ```

2.  **Add Dependency**:
    Add the following to your `pom.xml`:
    ```xml
    <dependency>
        <groupId>critex.core</groupId>
        <artifactId>critex-core</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```

## How to Run and Test

### Prerequisites
- Java 21 or higher
- Maven 3.9+

### Building the Project
Build the entire project and run unit tests:
```bash
mvn clean install
```

### Running the Test Module
The `test-module` is a functional Spring Boot application that uses an H2 in-memory database. You can start it using:
```bash
mvn spring-boot:run -pl test-module
```
Once running, you can access the Swagger UI to interact with the API:
`http://localhost:8080/swagger-ui.html`

### Running Integration Tests
To verify the library's functionality through the integration tests in `test-module`:
```bash
mvn test -pl test-module
```
This will execute the `UserControllerIT`, `PostControllerIT`, etc., which cover CRUD and dynamic filtering scenarios.
