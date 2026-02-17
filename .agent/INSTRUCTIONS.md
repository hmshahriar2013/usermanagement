# Development Instructions - Spring Boot Java Project

## Table of Contents
1. [Planning Methodology](#planning-methodology)
2. [MVC Architecture](#mvc-architecture)
3. [Error Handling](#error-handling)
4. [SOLID Principles](#solid-principles)
5. [OOP Concepts](#oop-concepts)
6. [Naming Conventions](#naming-conventions)
7. [Code Examples](#code-examples)

## Planning Methodology

### MANDATORY: Always Plan Before Implementation

Every task, feature, or bug fix must start with a detailed plan. This is non-negotiable.

#### Planning Template

```markdown
## Task: [Feature/Bug Fix Name]

### 1. Objective
Clear description of what needs to be accomplished

### 2. Requirements Analysis
- Functional requirements
- Non-functional requirements
- Business rules
- Edge cases to consider

### 3. Impact Analysis
**Affected Components:**
- [ ] Controller Layer
- [ ] Service Layer
- [ ] Repository Layer
- [ ] Entity Layer
- [ ] DTO Layer
- [ ] Exception Handling
- [ ] Configuration

**Database Changes:**
- [ ] New tables
- [ ] Column modifications
- [ ] Relationships
- [ ] Indexes

**External Dependencies:**
- [ ] New libraries needed
- [ ] External API integrations
- [ ] Security considerations

### 4. Design Decisions
**Architecture:**
- Design pattern to use
- Why this approach over alternatives

**Data Flow:**
Controller → Service → Repository → Database

**Error Handling:**
- What can go wrong?
- Which error codes to use?
- Custom exceptions needed?

### 5. Step-by-Step Execution Plan

#### Step 1: Define Error Codes
**File:** `exception/ErrorCode.java`
**Action:** Add new error codes
```java
ORDER_NOT_FOUND("ERR-2001", "Order not found with id: {0}"),
INVALID_ORDER_STATUS("ERR-2002", "Invalid order status: {0}")
```

#### Step 2: Create Custom Exceptions
**Files to create:**
- `exception/custom/OrderNotFoundException.java`
- `exception/custom/InvalidOrderStatusException.java`

**Implementation:**
```java
public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.NOT_FOUND, args);
    }
}
```

#### Step 3: Create/Update Entity
**File:** `entity/Order.java`
**Action:** Create entity with proper annotations
- JPA annotations
- Lombok annotations
- Relationships
- Audit fields (extend BaseEntity)

#### Step 4: Create Repository
**File:** `repository/OrderRepository.java`
**Action:** Create repository interface
- Extend JpaRepository
- Add custom query methods
- Use proper naming conventions

#### Step 5: Create DTOs
**Files to create:**
- `dto/request/CreateOrderRequest.java`
- `dto/request/UpdateOrderRequest.java`
- `dto/response/OrderResponse.java`

**Implementation:**
- Add validation annotations
- Use Lombok @Data
- Include all necessary fields

#### Step 6: Create Mapper
**File:** `mapper/OrderMapper.java`
**Action:** Create mapper interface
- Entity to DTO mappings
- DTO to Entity mappings
- Use MapStruct if available

#### Step 7: Create Service Interface
**File:** `service/OrderService.java`
**Action:** Define service contract
- Method signatures
- Return types (always DTOs)
- JavaDoc comments

#### Step 8: Implement Service
**File:** `service/impl/OrderServiceImpl.java`
**Action:** Implement business logic
- Validate inputs
- Handle transactions
- Log operations
- Throw custom exceptions
- Return DTOs

#### Step 9: Create Controller
**File:** `controller/OrderController.java`
**Action:** Create REST endpoints
- Proper HTTP methods
- Path variables and request params
- Request/response DTOs
- Validation
- HTTP status codes

#### Step 10: Write Unit Tests
**Files to create:**
- `test/service/OrderServiceImplTest.java`
- `test/controller/OrderControllerTest.java`

**Test coverage:**
- Happy path scenarios
- Error scenarios
- Edge cases
- Validation failures

#### Step 11: Write Integration Tests
**File:** `test/integration/OrderIntegrationTest.java`
**Action:** Test end-to-end flow
- Database interactions
- Full request/response cycle
- Error handling

#### Step 12: Update Documentation
**Files to update:**
- README.md (if needed)
- API documentation
- Postman collection

### 6. Testing Strategy
**Unit Tests:**
- Service layer business logic
- Mapper transformations
- Validation logic

**Integration Tests:**
- Controller endpoints
- Database operations
- Exception handling

**Manual Testing:**
- Use Postman/cURL
- Test all scenarios
- Verify error responses

### 7. Acceptance Criteria
- [ ] All tests passing
- [ ] Code follows conventions
- [ ] Error handling complete
- [ ] Logging in place
- [ ] Documentation updated
- [ ] Code reviewed
- [ ] No code smells
- [ ] SOLID principles followed

### 8. Deployment Checklist
- [ ] Database migrations ready
- [ ] Configuration updated
- [ ] Environment variables set
- [ ] Backward compatibility verified
- [ ] Rollback plan in place
```

## MVC Architecture

### Strict Layer Separation

```
┌─────────────────────────────────────────────────────┐
│                  Controller Layer                   │
│  - HTTP Request/Response handling                   │
│  - Input validation                                 │
│  - DTO transformation                               │
│  - HTTP status codes                                │
└──────────────────┬──────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────┐
│                   Service Layer                     │
│  - Business logic                                   │
│  - Transaction management                           │
│  - Error handling                                   │
│  - Orchestration                                    │
│  - Logging                                          │
└──────────────────┬──────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────┐
│                 Repository Layer                    │
│  - Data access only                                 │
│  - CRUD operations                                  │
│  - Custom queries                                   │
│  - No business logic                                │
└──────────────────┬──────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────┐
│                   Entity Layer                      │
│  - Database mapping                                 │
│  - Relationships                                    │
│  - Constraints                                      │
│  - No business logic (except domain methods)        │
└─────────────────────────────────────────────────────┘
```

### Controller Layer Rules

**DO:**
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable @Positive Long id) {
        log.debug("Received request to get user with id: {}", id);
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request) {
        log.debug("Received request to create user with email: {}", 
                  request.getEmail());
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user));
    }
}
```

**DON'T:**
```java
// ❌ BAD - Business logic in controller
@PostMapping
public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
    // ❌ Don't validate in controller
    if (request.getEmail() == null) {
        throw new RuntimeException("Email is required");
    }
    
    // ❌ Don't create entities in controller
    User user = new User();
    user.setEmail(request.getEmail());
    
    // ❌ Don't access repository from controller
    userRepository.save(user);
    
    return ResponseEntity.ok(new UserResponse(user));
}
```

### Service Layer Rules

**DO:**
```java
public interface UserService {
    UserResponse findById(Long id);
    UserResponse createUser(CreateUserRequest request);
    List<UserResponse> findAll();
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        log.debug("Finding user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                    ErrorCode.USER_NOT_FOUND, id));
        
        return userMapper.toResponse(user);
    }
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creating user with email: {}", request.getEmail());
        
        // Business validation
        validateUniqueEmail(request.getEmail());
        
        // Business logic
        User user = userMapper.toEntity(request);
        user.setActive(true);
        
        // Persistence
        User savedUser = userRepository.save(user);
        
        // Side effects
        emailService.sendWelcomeEmail(savedUser.getEmail());
        
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }
    
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                ErrorCode.DUPLICATE_EMAIL, email);
        }
    }
}
```

**DON'T:**
```java
// ❌ BAD - No interface
@Service
public class UserService {
    
    // ❌ BAD - Field injection
    @Autowired
    private UserRepository userRepository;
    
    // ❌ BAD - No transaction
    // ❌ BAD - Returns entity instead of DTO
    public User createUser(User user) {
        // ❌ BAD - No validation
        // ❌ BAD - No error handling
        // ❌ BAD - No logging
        return userRepository.save(user);
    }
}
```

### Repository Layer Rules

**DO:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Method name query
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByActiveTrue();
    
    // Custom query
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    // Fetch join for performance
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);
}
```

**DON'T:**
```java
// ❌ BAD - Business logic in repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ❌ BAD - Don't put business logic here
    default User createUserWithDefaults(String email) {
        User user = new User();
        user.setEmail(email);
        user.setActive(true);
        return save(user);
    }
}
```

### Entity Layer Rules

**DO:**
```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String firstName;
    
    @Column(nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
    
    // Domain methods are OK
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
}

// Base entity for audit fields
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;
    
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @Version
    private Long version;
}
```

**DON'T:**
```java
// ❌ BAD - Missing annotations, poor design
public class User {
    private Long id;  // ❌ No JPA annotations
    private String name;  // ❌ Should be firstName/lastName
    
    // ❌ BAD - No Lombok, manual getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // ❌ BAD - Business logic that belongs in service
    public void sendWelcomeEmail() {
        // email sending logic
    }
}
```

## Error Handling

### Error Code System

All error codes must be stored in a separate file: `exception/ErrorCode.java`

```java
package com.company.project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.text.MessageFormat;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // ========== USER ERRORS (1000-1999) ==========
    USER_NOT_FOUND("ERR-1001", "User not found with id: {0}"),
    DUPLICATE_EMAIL("ERR-1002", "Email already exists: {0}"),
    INVALID_USER_DATA("ERR-1003", "Invalid user data provided"),
    USER_ALREADY_ACTIVE("ERR-1004", "User with id {0} is already active"),
    USER_ALREADY_INACTIVE("ERR-1005", "User with id {0} is already inactive"),
    
    // ========== ORDER ERRORS (2000-2999) ==========
    ORDER_NOT_FOUND("ERR-2001", "Order not found with id: {0}"),
    INVALID_ORDER_STATUS("ERR-2002", "Invalid order status: {0}"),
    ORDER_ALREADY_CANCELLED("ERR-2003", "Order {0} is already cancelled"),
    CANNOT_CANCEL_COMPLETED_ORDER("ERR-2004", "Cannot cancel completed order: {0}"),
    INSUFFICIENT_STOCK("ERR-2005", "Insufficient stock for product: {0}"),
    
    // ========== AUTHENTICATION ERRORS (3000-3999) ==========
    UNAUTHORIZED("ERR-3001", "Unauthorized access"),
    INVALID_TOKEN("ERR-3002", "Invalid or expired token"),
    INSUFFICIENT_PERMISSIONS("ERR-3003", "Insufficient permissions for this operation"),
    INVALID_CREDENTIALS("ERR-3004", "Invalid username or password"),
    ACCOUNT_LOCKED("ERR-3005", "Account is locked"),
    
    // ========== VALIDATION ERRORS (4000-4999) ==========
    VALIDATION_ERROR("ERR-4001", "Validation failed"),
    INVALID_INPUT("ERR-4002", "Invalid input: {0}"),
    MISSING_REQUIRED_FIELD("ERR-4003", "Required field missing: {0}"),
    INVALID_FORMAT("ERR-4004", "Invalid format for field: {0}"),
    VALUE_OUT_OF_RANGE("ERR-4005", "Value out of acceptable range: {0}"),
    
    // ========== PAYMENT ERRORS (5000-5999) ==========
    PAYMENT_FAILED("ERR-5001", "Payment processing failed"),
    INSUFFICIENT_FUNDS("ERR-5002", "Insufficient funds"),
    INVALID_PAYMENT_METHOD("ERR-5003", "Invalid payment method"),
    PAYMENT_GATEWAY_ERROR("ERR-5004", "Payment gateway error: {0}"),
    
    // ========== SYSTEM ERRORS (9000-9999) ==========
    INTERNAL_SERVER_ERROR("ERR-9001", "Internal server error occurred"),
    DATABASE_ERROR("ERR-9002", "Database operation failed"),
    EXTERNAL_SERVICE_ERROR("ERR-9003", "External service call failed: {0}"),
    CONFIGURATION_ERROR("ERR-9004", "Configuration error: {0}");
    
    private final String code;
    private final String message;
    
    /**
     * Format error message with provided arguments
     * @param args Arguments to format message with
     * @return Formatted error message
     */
    public String formatMessage(Object... args) {
        return MessageFormat.format(message, args);
    }
}
```

### Custom Exception Hierarchy

```java
// Base exception class
package com.company.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    private final HttpStatus httpStatus;
    
    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, Object... args) {
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }
    
    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, 
                          Throwable cause, Object... args) {
        super(errorCode.formatMessage(args), cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.args = args;
    }
}

// Not Found exceptions
package com.company.project.exception.custom;

import com.company.project.exception.BaseException;
import com.company.project.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.NOT_FOUND, args);
    }
}

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.NOT_FOUND, args);
    }
}

// Conflict exceptions
public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.CONFLICT, args);
    }
}

// Bad Request exceptions
public class ValidationException extends BaseException {
    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.BAD_REQUEST, args);
    }
}

public class InvalidOrderStatusException extends BaseException {
    public InvalidOrderStatusException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.BAD_REQUEST, args);
    }
}

// Unauthorized exceptions
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.UNAUTHORIZED, args);
    }
}

// Forbidden exceptions
public class InsufficientPermissionsException extends BaseException {
    public InsufficientPermissionsException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.FORBIDDEN, args);
    }
}
```

### Global Exception Handler

```java
package com.company.project.exception.handler;

import com.company.project.dto.response.ApiResponse;
import com.company.project.exception.BaseException;
import com.company.project.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
        log.error("Business exception: {} - {}", 
                  ex.getErrorCode().getCode(), ex.getMessage(), ex);
        
        ApiResponse<Void> response = ApiResponse.error(
            ex.getErrorCode().getCode(),
            ex.getMessage()
        );
        
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
            ErrorCode.VALIDATION_ERROR.getCode(),
            "Validation failed",
            errors
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ApiResponse<Void> response = ApiResponse.error(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            "An unexpected error occurred. Please contact support."
        );
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
```

### API Response Wrapper

```java
package com.company.project.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
```

## SOLID Principles

### Single Responsibility Principle (SRP)

**Definition:** A class should have only one reason to change.

**Bad Example:**
```java
// ❌ Multiple responsibilities
@Service
public class UserService {
    
    public void registerUser(UserRequest request) {
        // 1. Validate user data
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        
        // 2. Save to database
        User user = new User();
        user.setEmail(request.getEmail());
        userRepository.save(user);
        
        // 3. Send email
        String emailContent = "Welcome " + user.getName();
        emailSender.send(user.getEmail(), emailContent);
        
        // 4. Log activity
        System.out.println("User registered: " + user.getId());
    }
}
```

**Good Example:**
```java
// ✅ Single responsibility per class
@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {
    
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;
    
    public UserResponse registerUser(CreateUserRequest request) {
        log.debug("Registering user with email: {}", request.getEmail());
        
        // Delegate validation
        userValidator.validateNewUser(request);
        
        // Delegate persistence
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        
        // Delegate email sending
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName());
        
        log.info("User registered successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }
}

@Component
public class UserValidator {
    public void validateNewUser(CreateUserRequest request) {
        // Validation logic only
    }
}

@Service
public class EmailService {
    public void sendWelcomeEmail(String email, String name) {
        // Email sending logic only
    }
}
```

### Open/Closed Principle (OCP)

**Definition:** Classes should be open for extension but closed for modification.

**Bad Example:**
```java
// ❌ Must modify class to add new payment method
@Service
public class PaymentService {
    
    public void processPayment(Payment payment, String method) {
        if ("CREDIT_CARD".equals(method)) {
            // Process credit card
        } else if ("PAYPAL".equals(method)) {
            // Process PayPal
        } else if ("BITCOIN".equals(method)) {
            // Process Bitcoin - NEW, had to modify class!
        }
    }
}
```

**Good Example:**
```java
// ✅ Open for extension, closed for modification
public interface PaymentProcessor {
    void processPayment(Payment payment);
    PaymentMethod getSupportedMethod();
}

@Service
public class CreditCardPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) {
        // Credit card processing logic
    }
    
    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.CREDIT_CARD;
    }
}

@Service
public class PayPalPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) {
        // PayPal processing logic
    }
    
    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.PAYPAL;
    }
}

// Can add new payment methods without modifying existing code
@Service
public class BitcoinPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(Payment payment) {
        // Bitcoin processing logic
    }
    
    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.BITCOIN;
    }
}

// Service uses strategy pattern
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final List<PaymentProcessor> paymentProcessors;
    
    public void processPayment(Payment payment) {
        PaymentProcessor processor = paymentProcessors.stream()
                .filter(p -> p.getSupportedMethod() == payment.getMethod())
                .findFirst()
                .orElseThrow(() -> new UnsupportedPaymentMethodException(
                    ErrorCode.INVALID_PAYMENT_METHOD, payment.getMethod()));
        
        processor.processPayment(payment);
    }
}
```

### Liskov Substitution Principle (LSP)

**Definition:** Subtypes must be substitutable for their base types.

**Bad Example:**
```java
// ❌ Violates LSP
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // ❌ Breaks expected behavior
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;  // ❌ Breaks expected behavior
    }
}

// This will fail for Square!
public void testRectangle(Rectangle rect) {
    rect.setWidth(5);
    rect.setHeight(10);
    assert rect.getArea() == 50;  // ❌ Fails for Square (will be 100)
}
```

**Good Example:**
```java
// ✅ Follows LSP
public interface Shape {
    double calculateArea();
    double calculatePerimeter();
}

public class Rectangle implements Shape {
    private final double width;
    private final double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
}

public class Square implements Shape {
    private final double side;
    
    public Square(double side) {
        this.side = side;
    }
    
    @Override
    public double calculateArea() {
        return side * side;
    }
    
    @Override
    public double calculatePerimeter() {
        return 4 * side;
    }
}

// Works correctly for all shapes
public double getTotalArea(List<Shape> shapes) {
    return shapes.stream()
            .mapToDouble(Shape::calculateArea)
            .sum();
}
```

### Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on interfaces they don't use.

**Bad Example:**
```java
// ❌ Fat interface
public interface UserOperations {
    void createUser(User user);
    void deleteUser(Long id);
    void updateUser(Long id, User user);
    void exportUsersToCSV();
    void exportUsersToPDF();
    void generateUserReport();
    void sendBulkEmail();
    void importUsersFromCSV();
}

// ❌ UserController forced to implement methods it doesn't need
public class UserController implements UserOperations {
    // Has to implement all methods even if it only needs create/update/delete
}
```

**Good Example:**
```java
// ✅ Segregated interfaces
public interface UserManagement {
    void createUser(CreateUserRequest request);
    void updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}

public interface UserExport {
    byte[] exportToCSV();
    byte[] exportToPDF();
}

public interface UserReporting {
    UserReport generateReport(ReportCriteria criteria);
}

public interface UserImport {
    void importFromCSV(byte[] csvData);
}

public interface UserNotification {
    void sendBulkEmail(EmailRequest request);
}

// Classes implement only what they need
@Service
public class UserServiceImpl implements UserManagement {
    // Only user management methods
}

@Service
public class UserExportService implements UserExport {
    // Only export methods
}

@Service
public class UserReportService implements UserReporting {
    // Only reporting methods
}
```

### Dependency Inversion Principle (DIP)

**Definition:** Depend on abstractions, not concretions.

**Bad Example:**
```java
// ❌ Depends on concrete implementations
@Service
public class OrderService {
    
    // ❌ Direct dependency on concrete class
    private MySQLOrderRepository orderRepository = new MySQLOrderRepository();
    private SmtpEmailService emailService = new SmtpEmailService();
    private StripePaymentService paymentService = new StripePaymentService();
    
    public void placeOrder(Order order) {
        orderRepository.save(order);
        paymentService.charge(order.getTotal());
        emailService.send(order.getUserEmail(), "Order confirmed");
    }
}
```

**Good Example:**
```java
// ✅ Depends on abstractions (interfaces)
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}

public interface EmailService {
    void send(String to, String subject, String body);
}

public interface PaymentService {
    PaymentResult charge(BigDecimal amount, PaymentMethod method);
}

@Service
@RequiredArgsConstructor  // Constructor injection
public class OrderServiceImpl implements OrderService {
    
    // ✅ Depend on interfaces (abstractions)
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final PaymentService paymentService;
    
    @Override
    public OrderResponse placeOrder(CreateOrderRequest request) {
        Order order = buildOrder(request);
        Order savedOrder = orderRepository.save(order);
        
        PaymentResult result = paymentService.charge(
            order.getTotal(), 
            request.getPaymentMethod()
        );
        
        if (result.isSuccessful()) {
            emailService.send(
                order.getUserEmail(),
                "Order Confirmed",
                buildEmailBody(order)
            );
        }
        
        return toResponse(savedOrder);
    }
}

// Concrete implementations can be swapped easily
@Repository
public class JpaOrderRepository implements OrderRepository {
    // JPA implementation
}

@Service
public class SendGridEmailService implements EmailService {
    // SendGrid implementation
}

@Service
public class StripePaymentService implements PaymentService {
    // Stripe implementation
}
```

## OOP Concepts

### Encapsulation

```java
// ✅ Good encapsulation
@Entity
@Getter  // Only getters exposed
public class BankAccount extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String accountNumber;
    
    @Column(nullable = false)
    private BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    
    // Private setter - controlled access
    private void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    // Public methods provide controlled access to internal state
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT, 
                "Deposit amount must be positive"
            );
        }
        
        if (status != AccountStatus.ACTIVE) {
            throw new InvalidOperationException(
                ErrorCode.INVALID_OPERATION,
                "Cannot deposit to inactive account"
            );
        }
        
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                ErrorCode.INVALID_INPUT,
                "Withdrawal amount must be positive"
            );
        }
        
        if (status != AccountStatus.ACTIVE) {
            throw new InvalidOperationException(
                ErrorCode.INVALID_OPERATION,
                "Cannot withdraw from inactive account"
            );
        }
        
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                ErrorCode.INSUFFICIENT_FUNDS
            );
        }
        
        this.balance = this.balance.subtract(amount);
    }
    
    public void activate() {
        this.status = AccountStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.status = AccountStatus.INACTIVE;
    }
}
```

### Inheritance

```java
// Base class for common behavior
@MappedSuperclass
@Getter
@Setter
public abstract class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private BigDecimal basePrice;
    
    // Template method pattern
    public final BigDecimal calculateFinalPrice() {
        BigDecimal price = basePrice;
        price = applyDiscounts(price);
        price = applyTaxes(price);
        return price;
    }
    
    // Abstract methods for subclasses to implement
    protected abstract BigDecimal applyDiscounts(BigDecimal price);
    protected abstract BigDecimal applyTaxes(BigDecimal price);
}

@Entity
@Table(name = "digital_products")
public class DigitalProduct extends Product {
    
    @Column
    private String downloadUrl;
    
    @Override
    protected BigDecimal applyDiscounts(BigDecimal price) {
        // Digital products get 10% discount
        return price.multiply(new BigDecimal("0.90"));
    }
    
    @Override
    protected BigDecimal applyTaxes(BigDecimal price) {
        // No tax on digital products in some regions
        return price;
    }
}

@Entity
@Table(name = "physical_products")
public class PhysicalProduct extends Product {
    
    @Column
    private Double weight;
    
    @Override
    protected BigDecimal applyDiscounts(BigDecimal price) {
        // No discount on physical products
        return price;
    }
    
    @Override
    protected BigDecimal applyTaxes(BigDecimal price) {
        // 10% tax on physical products
        return price.multiply(new BigDecimal("1.10"));
    }
}
```

### Polymorphism

```java
// Interface defining contract
public interface NotificationSender {
    void send(String recipient, String message);
    NotificationType getType();
}

// Multiple implementations
@Service
public class EmailNotificationSender implements NotificationSender {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void send(String recipient, String message) {
        // Email sending logic
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(recipient);
        mail.setText(message);
        mailSender.send(mail);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }
}

@Service
public class SmsNotificationSender implements NotificationSender {
    
    private final SmsClient smsClient;
    
    @Override
    public void send(String recipient, String message) {
        // SMS sending logic
        smsClient.sendSms(recipient, message);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }
}

@Service
public class PushNotificationSender implements NotificationSender {
    
    private final FirebaseMessaging firebaseMessaging;
    
    @Override
    public void send(String recipient, String message) {
        // Push notification logic
        Message pushMessage = Message.builder()
                .setToken(recipient)
                .setNotification(Notification.builder()
                        .setBody(message)
                        .build())
                .build();
        firebaseMessaging.send(pushMessage);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.PUSH;
    }
}

// Service uses polymorphism
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final List<NotificationSender> notificationSenders;
    
    public void sendNotification(
            String recipient, 
            String message, 
            NotificationType type) {
        
        log.debug("Sending {} notification to: {}", type, recipient);
        
        NotificationSender sender = notificationSenders.stream()
                .filter(s -> s.getType() == type)
                .findFirst()
                .orElseThrow(() -> new UnsupportedNotificationTypeException(
                    ErrorCode.INVALID_NOTIFICATION_TYPE, type));
        
        sender.send(recipient, message);
        
        log.info("{} notification sent successfully to: {}", type, recipient);
    }
    
    public void sendMultiChannel(
            String recipient, 
            String message, 
            List<NotificationType> types) {
        
        types.forEach(type -> sendNotification(recipient, message, type));
    }
}
```

### Abstraction

```java
// High-level abstraction
public interface OrderProcessor {
    OrderResult processOrder(Order order);
}

// Abstract implementation with template method
public abstract class AbstractOrderProcessor implements OrderProcessor {
    
    @Override
    public final OrderResult processOrder(Order order) {
        validateOrder(order);
        PaymentResult payment = processPayment(order);
        
        if (!payment.isSuccessful()) {
            return OrderResult.failed("Payment failed");
        }
        
        updateInventory(order);
        sendConfirmation(order);
        
        return OrderResult.success(order);
    }
    
    // Template methods - some abstract, some with default implementation
    protected abstract void validateOrder(Order order);
    protected abstract PaymentResult processPayment(Order order);
    protected abstract void updateInventory(Order order);
    
    // Default implementation
    protected void sendConfirmation(Order order) {
        // Default confirmation logic
    }
}

// Concrete implementations
@Service
@RequiredArgsConstructor
public class StandardOrderProcessor extends AbstractOrderProcessor {
    
    private final OrderValidator orderValidator;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    
    @Override
    protected void validateOrder(Order order) {
        orderValidator.validateStandardOrder(order);
    }
    
    @Override
    protected PaymentResult processPayment(Order order) {
        return paymentService.processStandardPayment(order);
    }
    
    @Override
    protected void updateInventory(Order order) {
        inventoryService.reserveItems(order.getItems());
    }
}

@Service
@RequiredArgsConstructor
public class ExpressOrderProcessor extends AbstractOrderProcessor {
    
    private final OrderValidator orderValidator;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    
    @Override
    protected void validateOrder(Order order) {
        orderValidator.validateExpressOrder(order);
    }
    
    @Override
    protected PaymentResult processPayment(Order order) {
        return paymentService.processExpressPayment(order);
    }
    
    @Override
    protected void updateInventory(Order order) {
        inventoryService.immediatelyAllocateItems(order.getItems());
    }
    
    @Override
    protected void sendConfirmation(Order order) {
        super.sendConfirmation(order);
        // Additional notification for express orders
        notificationService.sendUrgentNotification(order);
    }
}
```

## Naming Conventions

### Comprehensive Naming Guide

```java
// ========== PACKAGES ==========
// All lowercase, no underscores
com.company.project
com.company.project.controller
com.company.project.service.impl
com.company.project.exception.custom

// ========== CLASSES ==========
// PascalCase (UpperCamelCase)
public class UserController { }
public class OrderServiceImpl { }
public class JpaUserRepository { }
public class EmailValidator { }

// ========== INTERFACES ==========
// PascalCase, descriptive name (no 'I' prefix)
public interface UserService { }
public interface PaymentProcessor { }
public interface NotificationSender { }

// ========== ENUMS ==========
// PascalCase for enum name, UPPER_SNAKE_CASE for values
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

public enum UserRole {
    ADMIN,
    USER,
    GUEST,
    SUPER_ADMIN
}

// ========== CONSTANTS ==========
// UPPER_SNAKE_CASE
public static final String API_VERSION = "v1";
public static final int MAX_LOGIN_ATTEMPTS = 3;
public static final long SESSION_TIMEOUT_MINUTES = 30;
public static final BigDecimal TAX_RATE = new BigDecimal("0.10");

// ========== VARIABLES ==========
// camelCase, descriptive
private String firstName;
private Long userId;
private List<Order> customerOrders;
private Map<String, Object> configurationSettings;
private boolean isActive;
private boolean hasPermission;
private int retryCount;

// ========== METHODS ==========
// camelCase, verb-based
public User findById(Long id) { }
public void createUser(CreateUserRequest request) { }
public boolean isEmailAvailable(String email) { }
public List<User> getAllActiveUsers() { }
public Optional<User> findByEmail(String email) { }

// Boolean methods - is/has/can/should prefix
public boolean isActive() { }
public boolean hasRole(String role) { }
public boolean canAccessResource() { }
public boolean shouldSendNotification() { }

// Collection getters - plural
public List<User> getUsers() { }
public Set<Role> getRoles() { }
public Map<String, String> getAttributes() { }

// ========== DTO NAMING ==========
public class CreateUserRequest { }
public class UpdateUserRequest { }
public class UserResponse { }
public class UserDTO { }
public class OrderSummaryDTO { }

// ========== EXCEPTION NAMING ==========
public class UserNotFoundException extends BaseException { }
public class DuplicateEmailException extends BaseException { }
public class InvalidOrderStatusException extends BaseException { }

// ========== TEST METHODS ==========
// should_expectedBehavior_when_condition
@Test
public void should_returnUser_when_validIdProvided() { }

@Test
public void should_throwException_when_userNotFound() { }

@Test
public void should_createUser_when_validRequestProvided() { }

// ========== CONFIGURATION CLASSES ==========
@Configuration
public class DatabaseConfig { }

@Configuration
public class SecurityConfig { }

@Configuration
public class SwaggerConfig { }
```

## Code Examples

### Complete Feature Implementation Example

```markdown
## Task: Implement User Management Feature

### Step 1: Error Codes
```java
// exception/ErrorCode.java (add these)
USER_NOT_FOUND("ERR-1001", "User not found with id: {0}"),
DUPLICATE_EMAIL("ERR-1002", "Email already exists: {0}"),
INVALID_USER_DATA("ERR-1003", "Invalid user data provided"),
```

### Step 2: Custom Exceptions
```java
// exception/custom/UserNotFoundException.java
package com.company.project.exception.custom;

import com.company.project.exception.BaseException;
import com.company.project.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.NOT_FOUND, args);
    }
}

// exception/custom/DuplicateEmailException.java
package com.company.project.exception.custom;

import com.company.project.exception.BaseException;
import com.company.project.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends BaseException {
    public DuplicateEmailException(ErrorCode errorCode, Object... args) {
        super(errorCode, HttpStatus.CONFLICT, args);
    }
}
```

### Step 3: Entity
```java
// entity/User.java
package com.company.project.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.USER;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
    
    // Domain methods
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
```

### Step 4: Repository
```java
// repository/UserRepository.java
package com.company.project.repository;

import com.company.project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.active = true")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);
}
```

### Step 5: DTOs
```java
// dto/request/CreateUserRequest.java
package com.company.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
}

// dto/request/UpdateUserRequest.java
package com.company.project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;
    
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;
}

// dto/response/UserResponse.java
package com.company.project.dto.response;

import com.company.project.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Step 6: Mapper
```java
// mapper/UserMapper.java
package com.company.project.mapper;

import com.company.project.dto.request.CreateUserRequest;
import com.company.project.dto.request.UpdateUserRequest;
import com.company.project.dto.response.UserResponse;
import com.company.project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    
    User toEntity(CreateUserRequest request);
    
    UserResponse toResponse(User user);
    
    List<UserResponse> toResponseList(List<User> users);
    
    void updateEntityFromRequest(
        UpdateUserRequest request, 
        @MappingTarget User user
    );
}
```

### Step 7: Service Interface
```java
// service/UserService.java
package com.company.project.service;

import com.company.project.dto.request.CreateUserRequest;
import com.company.project.dto.request.UpdateUserRequest;
import com.company.project.dto.response.UserResponse;

import java.util.List;

/**
 * Service interface for user management operations
 */
public interface UserService {
    
    /**
     * Find user by ID
     * @param id User ID
     * @return UserResponse
     * @throws UserNotFoundException if user not found
     */
    UserResponse findById(Long id);
    
    /**
     * Find all users
     * @return List of UserResponse
     */
    List<UserResponse> findAll();
    
    /**
     * Find all active users
     * @return List of active UserResponse
     */
    List<UserResponse> findAllActive();
    
    /**
     * Create new user
     * @param request CreateUserRequest
     * @return Created UserResponse
     * @throws DuplicateEmailException if email already exists
     */
    UserResponse createUser(CreateUserRequest request);
    
    /**
     * Update existing user
     * @param id User ID
     * @param request UpdateUserRequest
     * @return Updated UserResponse
     * @throws UserNotFoundException if user not found
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);
    
    /**
     * Delete user
     * @param id User ID
     * @throws UserNotFoundException if user not found
     */
    void deleteUser(Long id);
    
    /**
     * Activate user
     * @param id User ID
     * @return Updated UserResponse
     * @throws UserNotFoundException if user not found
     */
    UserResponse activateUser(Long id);
    
    /**
     * Deactivate user
     * @param id User ID
     * @return Updated UserResponse
     * @throws UserNotFoundException if user not found
     */
    UserResponse deactivateUser(Long id);
}
```

### Step 8: Service Implementation
```java
// service/impl/UserServiceImpl.java
package com.company.project.service.impl;

import com.company.project.dto.request.CreateUserRequest;
import com.company.project.dto.request.UpdateUserRequest;
import com.company.project.dto.response.UserResponse;
import com.company.project.entity.User;
import com.company.project.exception.ErrorCode;
import com.company.project.exception.custom.DuplicateEmailException;
import com.company.project.exception.custom.UserNotFoundException;
import com.company.project.mapper.UserMapper;
import com.company.project.repository.UserRepository;
import com.company.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        log.debug("Finding user by id: {}", id);
        
        User user = getUserById(id);
        
        log.debug("User found: {}", user.getEmail());
        return userMapper.toResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        log.debug("Finding all users");
        
        List<User> users = userRepository.findAll();
        
        log.debug("Found {} users", users.size());
        return userMapper.toResponseList(users);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAllActive() {
        log.debug("Finding all active users");
        
        List<User> users = userRepository.findByActiveTrue();
        
        log.debug("Found {} active users", users.size());
        return userMapper.toResponseList(users);
    }
    
    @Override
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creating user with email: {}", request.getEmail());
        
        validateEmailUniqueness(request.getEmail());
        
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }
    
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.debug("Updating user with id: {}", id);
        
        User user = getUserById(id);
        userMapper.updateEntityFromRequest(request, user);
        User updatedUser = userRepository.save(user);
        
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        
        User user = getUserById(id);
        userRepository.delete(user);
        
        log.info("User deleted successfully with id: {}", id);
    }
    
    @Override
    public UserResponse activateUser(Long id) {
        log.debug("Activating user with id: {}", id);
        
        User user = getUserById(id);
        user.activate();
        User updatedUser = userRepository.save(user);
        
        log.info("User activated successfully with id: {}", id);
        return userMapper.toResponse(updatedUser);
    }
    
    @Override
    public UserResponse deactivateUser(Long id) {
        log.debug("Deactivating user with id: {}", id);
        
        User user = getUserById(id);
        user.deactivate();
        User updatedUser = userRepository.save(user);
        
        log.info("User deactivated successfully with id: {}", id);
        return userMapper.toResponse(updatedUser);
    }
    
    // Private helper methods
    
    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                    ErrorCode.USER_NOT_FOUND, id));
    }
    
    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(
                ErrorCode.DUPLICATE_EMAIL, email);
        }
    }
}
```

### Step 9: Controller
```java
// controller/UserController.java
package com.company.project.controller;

import com.company.project.dto.request.CreateUserRequest;
import com.company.project.dto.request.UpdateUserRequest;
import com.company.project.dto.response.ApiResponse;
import com.company.project.dto.response.UserResponse;
import com.company.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable @Positive(message = "User ID must be positive") Long id) {
        log.debug("GET /api/v1/users/{}", id);
        
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.debug("GET /api/v1/users");
        
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getActiveUsers() {
        log.debug("GET /api/v1/users/active");
        
        List<UserResponse> users = userService.findAllActive();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request) {
        log.debug("POST /api/v1/users - email: {}", request.getEmail());
        
        UserResponse user = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateUserRequest request) {
        log.debug("PUT /api/v1/users/{}", id);
        
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(
            ApiResponse.success("User updated successfully", user));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable @Positive Long id) {
        log.debug("DELETE /api/v1/users/{}", id);
        
        userService.deleteUser(id);
        return ResponseEntity.ok(
            ApiResponse.success("User deleted successfully", null));
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @PathVariable @Positive Long id) {
        log.debug("PATCH /api/v1/users/{}/activate", id);
        
        UserResponse user = userService.activateUser(id);
        return ResponseEntity.ok(
            ApiResponse.success("User activated successfully", user));
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(
            @PathVariable @Positive Long id) {
        log.debug("PATCH /api/v1/users/{}/deactivate", id);
        
        UserResponse user = userService.deactivateUser(id);
        return ResponseEntity.ok(
            ApiResponse.success("User deactivated successfully", user));
    }
}
```

### Step 10: Unit Tests
```java
// test/service/UserServiceImplTest.java
package com.company.project.service;

import com.company.project.dto.request.CreateUserRequest;
import com.company.project.dto.response.UserResponse;
import com.company.project.entity.User;
import com.company.project.exception.custom.DuplicateEmailException;
import com.company.project.exception.custom.UserNotFoundException;
import com.company.project.mapper.UserMapper;
import com.company.project.repository.UserRepository;
import com.company.project.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    private CreateUserRequest createRequest;
    private UserResponse userResponse;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .active(true)
                .build();
        
        createRequest = CreateUserRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();
        
        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .active(true)
                .build();
    }
    
    @Test
    void should_returnUser_when_validIdProvided() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);
        
        // When
        UserResponse result = userService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(testUser);
    }
    
    @Test
    void should_throwException_when_userNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(999L);
        verifyNoInteractions(userMapper);
    }
    
    @Test
    void should_createUser_when_validRequestProvided() {
        // Given
        when(userRepository.existsByEmail(createRequest.getEmail()))
                .thenReturn(false);
        when(userMapper.toEntity(createRequest)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);
        
        // When
        UserResponse result = userService.createUser(createRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(createRequest.getEmail());
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository).save(testUser);
    }
    
    @Test
    void should_throwException_when_emailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(createRequest.getEmail()))
                .thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateEmailException.class);
        verify(userRepository).existsByEmail(createRequest.getEmail());
        verify(userRepository, never()).save(any());
    }
}
```

This completes the comprehensive implementation example following all the principles and guidelines!
