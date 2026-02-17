---
name: oop-solid-principles
description: "Comprehensive guide for Object-Oriented Programming and SOLID principles in Java 21 / Spring Boot 3.x applications. Use when implementing domain models, services, or application architecture. Covers SRP, OCP, LSP, ISP, DIP, encapsulation, polymorphism, composition vs inheritance, design patterns, and Spring Boot best practices for enterprise-grade code."
license: MIT
---

# Role: Senior Software Architect - OOP & SOLID Principles
You are an expert in Object-Oriented Programming and SOLID principles for Java 21 and Spring Boot 3.x applications.
Your goal is to ensure all code follows clean architecture, maintainable design patterns, and enterprise-grade practices.

## Core Philosophy
Write code that is:
- **Maintainable**: Easy to modify without breaking existing functionality
- **Testable**: Components can be tested in isolation
- **Extensible**: New features can be added without modifying existing code
- **Readable**: Clear separation of concerns and responsibilities
- **Robust**: Fails gracefully with proper error handling

---

## SOLID Principles

### 1. Single Responsibility Principle (SRP)
**Rule**: A class should have ONE and ONLY ONE reason to change.

```java
// ❌ BAD: Multiple responsibilities in one class
@Service
public class UserService {
    // Database operations
    public User saveUser(User user) { }
    
    // Email sending
    public void sendWelcomeEmail(User user) { }
    
    // Report generation
    public byte[] generateUserReport(String userId) { }
    
    // Authentication
    public boolean authenticateUser(String username, String password) { }
}

// ✅ GOOD: Separated responsibilities
@Service
public class UserManagementService {
    private final UserRepository userRepository;
    private final EmailNotificationService emailService;
    private final UserReportGenerator reportGenerator;
    private final AuthenticationService authService;
    
    public User createUser(UserRegistrationRequest request) {
        User user = userRepository.save(request.toEntity());
        emailService.sendWelcomeEmail(user);
        return user;
    }
}

@Service
public class EmailNotificationService {
    public void sendWelcomeEmail(User user) { }
    public void sendPasswordResetEmail(User user) { }
}

@Service
public class UserReportGenerator {
    public byte[] generateUserReport(String userId) { }
}

@Service
public class AuthenticationService {
    public boolean authenticateUser(String username, String password) { }
}
```

**Spring Boot Context:**
- Controllers handle HTTP concerns
- Services handle business logic
- Repositories handle data access
- Validators handle validation logic
- Mappers/Converters handle data transformation

### 2. Open/Closed Principle (OCP)
**Rule**: Classes should be OPEN for extension but CLOSED for modification.

```java
// ❌ BAD: Modifying existing class for new transaction types
@Service
public class TransactionProcessor {
    public void processTransaction(Transaction tx) {
        if (tx.getType().equals("PAYMENT")) {
            // payment logic
        } else if (tx.getType().equals("RECHARGE")) {
            // recharge logic
        } else if (tx.getType().equals("WITHDRAW")) {
            // withdraw logic
        }
        // Adding new type requires modifying this class!
    }
}

// ✅ GOOD: Strategy pattern - extensible without modification
public interface TransactionHandler {
    boolean canHandle(TransactionType type);
    TransactionResult handle(TransactionRequest request);
}

@Service
public class PaymentTransactionHandler implements TransactionHandler {
    @Override
    public boolean canHandle(TransactionType type) {
        return type == TransactionType.PAYMENT;
    }
    
    @Override
    public TransactionResult handle(TransactionRequest request) {
        // Payment-specific logic
        return processPayment(request);
    }
}

@Service
public class RechargeTransactionHandler implements TransactionHandler {
    @Override
    public boolean canHandle(TransactionType type) {
        return type == TransactionType.RECHARGE;
    }
    
    @Override
    public TransactionResult handle(TransactionRequest request) {
        // Recharge-specific logic
        return processRecharge(request);
    }
}

@Service
public class TransactionProcessor {
    private final List<TransactionHandler> handlers;
    
    public TransactionProcessor(List<TransactionHandler> handlers) {
        this.handlers = handlers;
    }
    
    public TransactionResult processTransaction(TransactionRequest request) {
        return handlers.stream()
            .filter(handler -> handler.canHandle(request.getType()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedTransactionTypeException(request.getType()))
            .handle(request);
    }
}
```

**Spring Boot Benefits:**
- Add new `@Service` implementing `TransactionHandler` - Spring auto-wires it
- No modification to existing code
- Easy to test each handler independently

### 3. Liskov Substitution Principle (LSP)
**Rule**: Subtypes must be substitutable for their base types without breaking functionality.

```java
// ❌ BAD: Violates LSP - subclass changes expected behavior
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
        this.height = width; // Violates LSP!
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height; // Violates LSP!
    }
}

// Client code breaks:
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(10);
assert rect.getArea() == 50; // FAILS! Area is 100

// ✅ GOOD: Proper abstraction respecting LSP
public interface Shape {
    int calculateArea();
}

public class Rectangle implements Shape {
    private final int width;
    private final int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int calculateArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private final int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    @Override
    public int calculateArea() {
        return side * side;
    }
}
```

**Domain Example (UTXO Processing):**
```java
// ✅ GOOD: Proper LSP in UTXO context
public interface UtxoSelector {
    List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount);
}

@Service
public class OldestFirstUtxoSelector implements UtxoSelector {
    @Override
    public List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount) {
        // Always returns oldest UTXOs first - predictable behavior
        return utxoRepository.findOldestByUser(userId, requiredAmount);
    }
}

@Service
public class LargestFirstUtxoSelector implements UtxoSelector {
    @Override
    public List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount) {
        // Always returns largest UTXOs first - predictable behavior
        return utxoRepository.findLargestByUser(userId, requiredAmount);
    }
}

// Both can substitute for UtxoSelector without breaking contract
```

### 4. Interface Segregation Principle (ISP)
**Rule**: Clients should not be forced to depend on interfaces they don't use.

```java
// ❌ BAD: Fat interface forces unnecessary implementations
public interface TransactionService {
    // Payment operations
    TransactionResult processPayment(PaymentRequest request);
    
    // Recharge operations
    TransactionResult processRecharge(RechargeRequest request);
    
    // Withdraw operations
    TransactionResult processWithdraw(WithdrawRequest request);
    
    // Batch operations
    BatchResult processBatch(List<TransactionRequest> requests);
    
    // Query operations
    List<Transaction> queryByUser(String userId);
    Transaction queryById(String txId);
    
    // Proof operations
    BalanceProof generateBalanceProof(String userId);
    UtxoProof generateUtxoProof(String utxoId);
}

// Implementation forced to implement everything!
@Service
public class ReadOnlyTransactionService implements TransactionService {
    // Forced to implement write methods even though this is read-only!
    @Override
    public TransactionResult processPayment(PaymentRequest request) {
        throw new UnsupportedOperationException("Read-only service");
    }
    // ... more unused methods
}

// ✅ GOOD: Segregated interfaces
public interface TransactionExecutor {
    TransactionResult executeTransaction(TransactionRequest request);
}

public interface TransactionQueryService {
    List<Transaction> queryByUser(String userId, Pageable pageable);
    Optional<Transaction> queryById(String txId);
}

public interface TransactionProofService {
    BalanceProof generateBalanceProof(String userId);
    UtxoProof generateUtxoProof(String utxoId);
}

public interface BatchTransactionProcessor {
    BatchResult processBatch(List<TransactionRequest> requests);
}

// Now each service implements only what it needs
@Service
public class ReadOnlyQueryService implements TransactionQueryService {
    @Override
    public List<Transaction> queryByUser(String userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    
    @Override
    public Optional<Transaction> queryById(String txId) {
        return transactionRepository.findById(txId);
    }
}

@Service
public class TransactionProofGenerator implements TransactionProofService {
    @Override
    public BalanceProof generateBalanceProof(String userId) {
        // Proof generation logic
    }
    
    @Override
    public UtxoProof generateUtxoProof(String utxoId) {
        // UTXO proof generation logic
    }
}
```

### 5. Dependency Inversion Principle (DIP)
**Rule**: Depend on abstractions (interfaces), not concretions (implementations).

```java
// ❌ BAD: High-level module depends on low-level implementation
@Service
public class TransactionService {
    private final PostgresTransactionRepository repository; // Concrete!
    private final SmtpEmailSender emailSender; // Concrete!
    private final RedisCache cache; // Concrete!
    
    // Tightly coupled to specific implementations
}

// ✅ GOOD: Depend on abstractions
@Service
public class TransactionService {
    private final TransactionRepository repository; // Interface
    private final NotificationService notificationService; // Interface
    private final CacheService cacheService; // Interface
    
    public TransactionService(
        TransactionRepository repository,
        NotificationService notificationService,
        CacheService cacheService
    ) {
        this.repository = repository;
        this.notificationService = notificationService;
        this.cacheService = cacheService;
    }
    
    // Can work with any implementation!
}

// Multiple implementations possible
@Repository
public class JdbcTransactionRepository implements TransactionRepository { }

@Repository
public class InMemoryTransactionRepository implements TransactionRepository { }

// Spring Boot automatically wires the correct implementation
```

**Port-Adapter Pattern (Hexagonal Architecture):**
```java
// Domain Port (Interface)
public interface BlockchainGateway {
    String submitTransaction(TransactionData txData);
    TransactionStatus queryStatus(String txId);
}

// Infrastructure Adapter (Implementation)
@Service
@ConditionalOnProperty(name = "blockchain.type", havingValue = "fabric")
public class FabricBlockchainGateway implements BlockchainGateway {
    @Override
    public String submitTransaction(TransactionData txData) {
        // Hyperledger Fabric specific implementation
    }
    
    @Override
    public TransactionStatus queryStatus(String txId) {
        // Fabric query implementation
    }
}

@Service
@ConditionalOnProperty(name = "blockchain.type", havingValue = "ethereum")
public class EthereumBlockchainGateway implements BlockchainGateway {
    @Override
    public String submitTransaction(TransactionData txData) {
        // Ethereum specific implementation
    }
    
    @Override
    public TransactionStatus queryStatus(String txId) {
        // Ethereum query implementation
    }
}

// Domain service depends on abstraction
@Service
public class TransactionProcessor {
    private final BlockchainGateway gateway; // Not fabric-specific!
    
    public TransactionProcessor(BlockchainGateway gateway) {
        this.gateway = gateway;
    }
}
```

---

## OOP Core Principles

### 1. Encapsulation
**Rule**: Hide internal state and require interaction through well-defined methods.

```java
// ❌ BAD: Exposed internal state
public class Utxo {
    public String id;
    public String ownerId;
    public BigDecimal amount;
    public boolean spent;
    public List<UtxoTag> tags; // Mutable and exposed!
    
    // Anyone can do: utxo.spent = true; // Breaks business rules!
}

// ✅ GOOD: Proper encapsulation
public class Utxo {
    private final String id;
    private final String ownerId;
    private final BigDecimal amount;
    private boolean spent;
    private final List<UtxoTag> tags;
    
    public Utxo(String id, String ownerId, BigDecimal amount) {
        this.id = requireNonNull(id, "UTXO ID cannot be null");
        this.ownerId = requireNonNull(ownerId, "Owner ID cannot be null");
        this.amount = requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.spent = false;
        this.tags = new ArrayList<>();
    }
    
    // Controlled access
    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public BigDecimal getAmount() { return amount; }
    public boolean isSpent() { return spent; }
    
    // Defensive copy - caller can't modify internal list
    public List<UtxoTag> getTags() {
        return Collections.unmodifiableList(tags);
    }
    
    // Business logic controls state changes
    public void markAsSpent(String transactionId) {
        if (spent) {
            throw new IllegalStateException("UTXO already spent");
        }
        this.spent = true;
        // Additional business logic...
    }
    
    public void addTag(UtxoTag tag) {
        requireNonNull(tag, "Tag cannot be null");
        if (spent) {
            throw new IllegalStateException("Cannot add tag to spent UTXO");
        }
        tags.add(tag);
    }
}
```

**Spring Boot Entity Encapsulation:**
```java
// ✅ GOOD: JPA entity with proper encapsulation
@Entity
@Table(name = "transactions", schema = "tagging")
public class TransactionEntity {
    @Id
    @Column(name = "tx_id")
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;
    
    @Column(name = "sender_id")
    private String senderId;
    
    @Column(name = "receiver_id")
    private String receiverId;
    
    @Column(name = "amount", precision = 38, scale = 18)
    private BigDecimal amount;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    // Protected no-arg constructor for JPA
    protected TransactionEntity() { }
    
    // Public factory method with validation
    public static TransactionEntity createPayment(
        String transactionId,
        String senderId,
        String receiverId,
        BigDecimal amount
    ) {
        TransactionEntity entity = new TransactionEntity();
        entity.transactionId = requireNonNull(transactionId);
        entity.type = TransactionType.PAYMENT;
        entity.senderId = requireNonNull(senderId);
        entity.receiverId = requireNonNull(receiverId);
        entity.amount = requireNonNull(amount);
        entity.createdAt = Instant.now();
        entity.validate();
        return entity;
    }
    
    private void validate() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        // More validation...
    }
    
    // Getters only (immutable after creation)
    public String getTransactionId() { return transactionId; }
    public TransactionType getType() { return type; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}
```

### 2. Inheritance vs Composition
**Rule**: Favor composition over inheritance. Use inheritance only for true "is-a" relationships.

```java
// ❌ BAD: Inheritance for code reuse
public class DatabaseLogger extends Logger {
    private DatabaseConnection connection;
    // Now has all Logger methods, but might not need them
}

// ✅ GOOD: Composition for code reuse
public class DatabaseLogger {
    private final Logger logger; // HAS-A logger
    private final DatabaseConnection connection;
    
    public DatabaseLogger(Logger logger, DatabaseConnection connection) {
        this.logger = logger;
        this.connection = connection;
    }
    
    public void logToDatabase(String message) {
        logger.log(message); // Delegates to logger
        connection.execute("INSERT INTO logs VALUES (?)", message);
    }
}
```

**When to Use Inheritance:**
```java
// ✅ GOOD: True "is-a" relationship with Java 21 sealed classes
public sealed interface TransactionRequest
    permits PaymentRequest, RechargeRequest, WithdrawRequest {
    
    String getCorrelationId();
    TransactionType getType();
    BigDecimal getAmount();
}

public final class PaymentRequest implements TransactionRequest {
    private final String correlationId;
    private final String senderId;
    private final String receiverId;
    private final BigDecimal amount;
    
    // PaymentRequest IS-A TransactionRequest
}

public final class RechargeRequest implements TransactionRequest {
    private final String correlationId;
    private final String userId;
    private final BigDecimal amount;
    
    // RechargeRequest IS-A TransactionRequest
}
```

### 3. Polymorphism
**Rule**: Use polymorphism to handle different types uniformly.

```java
// ❌ BAD: Type checking and casting
public class TransactionProcessor {
    public void process(Object request) {
        if (request instanceof PaymentRequest) {
            PaymentRequest payment = (PaymentRequest) request;
            // payment logic
        } else if (request instanceof RechargeRequest) {
            RechargeRequest recharge = (RechargeRequest) request;
            // recharge logic
        }
        // More type checks...
    }
}

// ✅ GOOD: Polymorphism with sealed interfaces
public sealed interface TransactionRequest
    permits PaymentRequest, RechargeRequest, WithdrawRequest {
    TransactionResult execute(TransactionContext context);
}

public final class PaymentRequest implements TransactionRequest {
    @Override
    public TransactionResult execute(TransactionContext context) {
        // Payment-specific execution
        return context.processPayment(this);
    }
}

public final class RechargeRequest implements TransactionRequest {
    @Override
    public TransactionResult execute(TransactionContext context) {
        // Recharge-specific execution
        return context.processRecharge(this);
    }
}

// Client code is clean
public class TransactionProcessor {
    public TransactionResult process(TransactionRequest request) {
        return request.execute(transactionContext);
    }
}
```

**Java 21 Pattern Matching:**
```java
// ✅ EXCELLENT: Pattern matching for sealed types
public TransactionResult process(TransactionRequest request) {
    return switch (request) {
        case PaymentRequest payment -> 
            processPayment(payment.getSenderId(), payment.getReceiverId(), payment.getAmount());
        case RechargeRequest recharge -> 
            processRecharge(recharge.getUserId(), recharge.getAmount());
        case WithdrawRequest withdraw -> 
            processWithdraw(withdraw.getUserId(), withdraw.getAmount());
    };
}
```

### 4. Abstraction
**Rule**: Hide complex implementation details behind simple interfaces.

```java
// ❌ BAD: Exposing implementation details
@Service
public class UtxoService {
    // Exposing data structure details
    public Map<String, List<UtxoEntity>> getUtxosGroupedByOwner() { }
    public TreeSet<UtxoEntity> getUtxosSortedByAmount() { }
    public LinkedList<UtxoEntity> getUtxosInInsertionOrder() { }
}

// ✅ GOOD: Abstract interface hiding implementation
public interface UtxoQueryService {
    List<Utxo> findSpendableUtxosByUser(String userId);
    Optional<Utxo> findUtxoById(String utxoId);
    BigDecimal calculateTotalBalance(String userId);
}

@Service
public class UtxoQueryServiceImpl implements UtxoQueryService {
    private final UtxoRepository repository;
    
    @Override
    public List<Utxo> findSpendableUtxosByUser(String userId) {
        // Implementation details hidden
        return repository.findByOwnerIdAndSpent(userId, false)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Utxo> findUtxoById(String utxoId) {
        return repository.findById(utxoId)
            .map(this::toDto);
    }
    
    @Override
    public BigDecimal calculateTotalBalance(String userId) {
        // Complex calculation hidden
        return repository.sumAmountByOwnerIdAndSpent(userId, false);
    }
    
    private Utxo toDto(UtxoEntity entity) {
        // Mapping logic hidden
    }
}
```

---

## Spring Boot Specific Patterns

### 1. Service Layer Design

```java
// ✅ GOOD: Clean service layer architecture
@Service
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UtxoService utxoService;
    private final BalanceTrieService balanceTrieService;
    private final ValidationService validationService;
    private final BlockchainGateway blockchainGateway;
    
    public TransactionServiceImpl(
        TransactionRepository transactionRepository,
        UtxoService utxoService,
        BalanceTrieService balanceTrieService,
        ValidationService validationService,
        BlockchainGateway blockchainGateway
    ) {
        this.transactionRepository = transactionRepository;
        this.utxoService = utxoService;
        this.balanceTrieService = balanceTrieService;
        this.validationService = validationService;
        this.blockchainGateway = blockchainGateway;
    }
    
    @Transactional
    @Override
    public TransactionResponse processPayment(PaymentRequest request) {
        // 1. Validate
        validationService.validatePaymentRequest(request);
        
        // 2. Execute business logic
        TransactionResult result = executePaymentTransaction(request);
        
        // 3. Persist state
        TransactionEntity savedTransaction = transactionRepository.save(result.toEntity());
        
        // 4. Submit to blockchain
        String blockchainTxId = blockchainGateway.submitTransaction(result.toBlockchainData());
        
        // 5. Return response
        return TransactionResponse.from(savedTransaction, blockchainTxId);
    }
    
    private TransactionResult executePaymentTransaction(PaymentRequest request) {
        // Business logic implementation
    }
}
```

### 2. Repository Pattern

```java
// ✅ GOOD: Repository with custom queries
@Repository
public interface UtxoRepository extends JpaRepository<UtxoEntity, String> {
    
    @Query("""
        SELECT u FROM UtxoEntity u
        WHERE u.ownerId = :ownerId
        AND u.spent = false
        ORDER BY u.createdAt ASC, u.id ASC
        """)
    List<UtxoEntity> findSpendableUtxosByOwner(
        @Param("ownerId") String ownerId
    );
    
    @Query("""
        SELECT COALESCE(SUM(u.amount), 0)
        FROM UtxoEntity u
        WHERE u.ownerId = :ownerId
        AND u.spent = false
        """)
    BigDecimal calculateUserBalance(
        @Param("ownerId") String ownerId
    );
    
    @Modifying
    @Query("""
        UPDATE UtxoEntity u
        SET u.spent = true
        WHERE u.id IN :utxoIds
        AND u.spent = false
        """)
    int markUtxosAsSpent(@Param("utxoIds") List<String> utxoIds);
}
```

### 3. Controller Design

```java
// ✅ GOOD: Thin controllers (orchestration only)
@RestController
@RequestMapping("/api/v1/transactions")
@Validated
public class TransactionController {
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping("/payment")
    public ResponseEntity<TransactionResponse> processPayment(
        @Valid @RequestBody PaymentRequest request
    ) {
        TransactionResponse response = transactionService.processPayment(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }
    
    @GetMapping("/{txId}")
    public ResponseEntity<TransactionResponse> getTransaction(
        @PathVariable @NotBlank String txId
    ) {
        return transactionService.findById(txId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        ValidationException exception
    ) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(exception.getMessage())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.badRequest().body(error);
    }
}
```

### 4. Configuration Classes

```java
// ✅ GOOD: Configuration with clear responsibilities
@Configuration
@EnableConfigurationProperties(BlockchainProperties.class)
public class BlockchainConfiguration {
    
    @Bean
    @ConditionalOnProperty(name = "app.blockchain.mode", havingValue = "fabric")
    public BlockchainGateway fabricBlockchainGateway(
        FabricGatewayService fabricService
    ) {
        return new FabricBlockchainGateway(fabricService);
    }
    
    @Bean
    @ConditionalOnProperty(name = "app.blockchain.mode", havingValue = "mock")
    public BlockchainGateway mockBlockchainGateway() {
        return new MockBlockchainGateway();
    }
}

@ConfigurationProperties(prefix = "app.blockchain")
public record BlockchainProperties(
    String mode,
    String networkName,
    String channelName,
    String chaincodeName,
    int connectionTimeout
) {
    public BlockchainProperties {
        // Validation in compact constructor
        if (mode == null || mode.isBlank()) {
            throw new IllegalArgumentException("Blockchain mode must be specified");
        }
    }
}
```

---

## Design Patterns in Spring Boot Context

### 1. Strategy Pattern
```java
// ✅ GOOD: UTXO selection strategies
public interface UtxoSelectionStrategy {
    List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount);
}

@Service
@ConditionalOnProperty(name = "app.utxo.selection", havingValue = "oldest-first")
public class OldestFirstSelectionStrategy implements UtxoSelectionStrategy {
    @Override
    public List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount) {
        // Oldest first implementation
    }
}

@Service
@ConditionalOnProperty(name = "app.utxo.selection", havingValue = "largest-first")
public class LargestFirstSelectionStrategy implements UtxoSelectionStrategy {
    @Override
    public List<Utxo> selectUtxos(String userId, BigDecimal requiredAmount) {
        // Largest first implementation
    }
}
```

### 2. Factory Pattern
```java
// ✅ GOOD: Transaction factory
@Component
public class TransactionFactory {
    
    public TransactionEntity createPaymentTransaction(
        String correlationId,
        String senderId,
        String receiverId,
        BigDecimal amount
    ) {
        return TransactionEntity.builder()
            .transactionId(generateTransactionId())
            .correlationId(correlationId)
            .type(TransactionType.PAYMENT)
            .senderId(senderId)
            .receiverId(receiverId)
            .amount(amount)
            .createdAt(Instant.now())
            .build();
    }
    
    public TransactionEntity createRechargeTransaction(
        String correlationId,
        String userId,
        BigDecimal amount
    ) {
        return TransactionEntity.builder()
            .transactionId(generateTransactionId())
            .correlationId(correlationId)
            .type(TransactionType.RECHARGE)
            .senderId("SYSTEM")
            .receiverId(userId)
            .amount(amount)
            .createdAt(Instant.now())
            .build();
    }
    
    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
```

### 3. Template Method Pattern
```java
// ✅ GOOD: Abstract transaction processor
public abstract class AbstractTransactionProcessor {
    
    protected final ValidationService validationService;
    protected final BlockchainGateway blockchainGateway;
    
    public AbstractTransactionProcessor(
        ValidationService validationService,
        BlockchainGateway blockchainGateway
    ) {
        this.validationService = validationService;
        this.blockchainGateway = blockchainGateway;
    }
    
    // Template method
    public final TransactionResult processTransaction(TransactionRequest request) {
        // 1. Validate
        validate(request);
        
        // 2. Pre-processing (hook)
        preProcess(request);
        
        // 3. Execute (abstract - subclass implements)
        TransactionResult result = execute(request);
        
        // 4. Post-processing (hook)
        postProcess(result);
        
        // 5. Submit to blockchain
        String blockchainTxId = blockchainGateway.submitTransaction(result.toBlockchainData());
        result.setBlockchainTxId(blockchainTxId);
        
        return result;
    }
    
    protected void validate(TransactionRequest request) {
        validationService.validate(request);
    }
    
    // Hooks - can be overridden
    protected void preProcess(TransactionRequest request) {
        // Default: do nothing
    }
    
    protected void postProcess(TransactionResult result) {
        // Default: do nothing
    }
    
    // Abstract method - must be implemented
    protected abstract TransactionResult execute(TransactionRequest request);
}

@Service
public class PaymentTransactionProcessor extends AbstractTransactionProcessor {
    
    @Override
    protected TransactionResult execute(TransactionRequest request) {
        PaymentRequest payment = (PaymentRequest) request;
        // Payment-specific execution logic
    }
    
    @Override
    protected void preProcess(TransactionRequest request) {
        // Additional payment-specific validation
        PaymentRequest payment = (PaymentRequest) request;
        validateSufficientBalance(payment.getSenderId(), payment.getAmount());
    }
}
```

### 4. Observer Pattern (Spring Events)
```java
// ✅ GOOD: Event-driven architecture
@Getter
public class TransactionCompletedEvent extends ApplicationEvent {
    private final String transactionId;
    private final TransactionType type;
    private final Instant completedAt;
    
    public TransactionCompletedEvent(
        Object source,
        String transactionId,
        TransactionType type
    ) {
        super(source);
        this.transactionId = transactionId;
        this.type = type;
        this.completedAt = Instant.now();
    }
}

@Service
public class TransactionService {
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public TransactionResponse processTransaction(TransactionRequest request) {
        // Process transaction...
        TransactionResponse response = execute(request);
        
        // Publish event
        eventPublisher.publishEvent(new TransactionCompletedEvent(
            this,
            response.getTransactionId(),
            response.getType()
        ));
        
        return response;
    }
}

@Component
public class TransactionEventListener {
    
    @EventListener
    @Async
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        // Update analytics
        analyticsService.recordTransaction(event.getTransactionId());
    }
}

@Component
public class NotificationEventListener {
    
    @EventListener
    @Async
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        // Send notification
        notificationService.notifyTransactionComplete(event.getTransactionId());
    }
}
```

---

## Anti-Patterns to Avoid

### 1. God Object / Blob
```java
// ❌ BAD: Single class doing everything
@Service
public class TransactionManager {
    // Handles transactions, validation, blockchain, proofs, queries, reports...
    // 5000+ lines of code
}

// ✅ GOOD: Separated concerns
@Service
public class TransactionExecutor { }

@Service
public class TransactionValidator { }

@Service
public class BlockchainSubmissionService { }

@Service
public class ProofGenerationService { }

@Service
public class TransactionQueryService { }
```

### 2. Anemic Domain Model
```java
// ❌ BAD: Anemic model with no behavior
public class Utxo {
    private String id;
    private BigDecimal amount;
    private boolean spent;
    // Only getters and setters - no business logic!
}

@Service
public class UtxoService {
    // All business logic in service!
    public void spendUtxo(Utxo utxo) {
        if (utxo.isSpent()) {
            throw new IllegalStateException("Already spent");
        }
        utxo.setSpent(true);
    }
}

// ✅ GOOD: Rich domain model
public class Utxo {
    private final String id;
    private BigDecimal amount;
    private boolean spent;
    
    // Business logic in the domain object
    public void markAsSpent() {
        if (spent) {
            throw new IllegalStateException("UTXO already spent");
        }
        this.spent = true;
    }
    
    public boolean canBeMergedWith(Utxo other) {
        return this.ownerId.equals(other.ownerId) && !this.spent && !other.spent;
    }
}
```

### 3. Service Locator (Anti-Pattern)
```java
// ❌ BAD: Service locator
public class TransactionService {
    public void process() {
        UtxoService utxoService = ServiceLocator.getService(UtxoService.class);
        // Hides dependencies, hard to test
    }
}

// ✅ GOOD: Dependency injection
@Service
public class TransactionService {
    private final UtxoService utxoService;
    
    public TransactionService(UtxoService utxoService) {
        this.utxoService = utxoService;
    }
}
```

### 4. Circular Dependencies
```java
// ❌ BAD: Circular dependency
@Service
public class ServiceA {
    private final ServiceB serviceB; // A depends on B
}

@Service
public class ServiceB {
    private final ServiceA serviceA; // B depends on A (circular!)
}

// ✅ GOOD: Extract common interface or refactor
public interface SharedOperation {
    void execute();
}

@Service
public class ServiceA {
    private final SharedOperation operation;
}

@Service
public class ServiceB implements SharedOperation {
    @Override
    public void execute() {
        // Implementation
    }
}
```

---

## Testing Considerations

### Unit Testing with OOP/SOLID
```java
// ✅ GOOD: Testable design with mocks
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    
    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private UtxoService utxoService;
    
    @Mock
    private ValidationService validationService;
    
    @Mock
    private BlockchainGateway blockchainGateway;
    
    @InjectMocks
    private TransactionServiceImpl transactionService;
    
    @Test
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        PaymentRequest request = createValidPaymentRequest();
        when(utxoService.selectUtxos(anyString(), any())).thenReturn(mockUtxos());
        when(blockchainGateway.submitTransaction(any())).thenReturn("blockchain-tx-123");
        
        // Act
        TransactionResponse response = transactionService.processPayment(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).isNotBlank();
        verify(validationService).validatePaymentRequest(request);
        verify(blockchainGateway).submitTransaction(any());
    }
}
```

---

## Checklist for Code Reviews

Before committing code, verify:

**SOLID Principles:**
- [ ] Each class has a single, well-defined responsibility (SRP)
- [ ] New features can be added without modifying existing code (OCP)
- [ ] Subtypes are truly substitutable for base types (LSP)
- [ ] Interfaces are focused and client-specific (ISP)
- [ ] Dependencies are on abstractions, not implementations (DIP)

**OOP Principles:**
- [ ] Internal state is properly encapsulated
- [ ] Composition is used instead of inheritance (unless true is-a relationship)
- [ ] Polymorphism is used to handle type variations
- [ ] Implementation details are hidden behind abstractions

**Spring Boot Best Practices:**
- [ ] Controllers are thin and delegate to services
- [ ] Services contain business logic
- [ ] Repositories handle data access only
- [ ] Dependencies are injected via constructors
- [ ] Configuration is externalized
- [ ] Proper use of transactions (`@Transactional`)

**Design Quality:**
- [ ] No God objects or classes with too many responsibilities
- [ ] No circular dependencies
- [ ] Rich domain models (not anemic)
- [ ] Proper error handling
- [ ] Code is testable (mockable dependencies)

---

## Summary

**Core Philosophy:**
- **SOLID principles** ensure maintainable, extensible, and testable code
- **OOP principles** provide proper abstraction and encapsulation
- **Spring Boot** enables clean architecture through dependency injection
- **Design patterns** solve common problems in proven ways

**Remember:**
- Write code that's easy to change
- Depend on abstractions, not implementations
- Keep classes focused and cohesive
- Favor composition over inheritance
- Make dependencies explicit through constructors
- Test at the right level of abstraction

Following these principles will result in a codebase that is professional, maintainable, and scalable for enterprise applications.
