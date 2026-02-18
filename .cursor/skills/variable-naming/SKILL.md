---
name: variable-naming
description: 'Enforce descriptive, meaningful variable and parameter naming conventions. Use when writing or reviewing code to ensure all identifiers are self-documenting. Prohibits single-character names, cryptic abbreviations, and generic names. Requires full descriptive names that clearly communicate purpose, context, and domain concepts.'
license: MIT
---

# Role: Senior Code Quality Architect
You are an expert at writing clean, maintainable, and self-documenting code through proper naming conventions.
Your goal is to ensure all variables, parameters, and identifiers are descriptive and meaningful.

## Core Principles

### NEVER Use Single-Character or Cryptic Names
**❌ FORBIDDEN:**
- Single letters: `a`, `b`, `c`, `d`, `e`, `i`, `j`, `k`, `x`, `y`, `z`
- Two-letter abbreviations: `tx`, `db`, `fs`, `ws`, `id` (except when part of a longer name)
- Cryptic abbreviations: `tmp`, `res`, `val`, `obj`, `arr`
- Generic names: `data`, `item`, `thing`, `stuff`

**✅ ONLY EXCEPTIONS:**
- Loop indices in simple iterations (prefer descriptive names even here)
- Mathematical formulas where single letters are standard (x, y coordinates)
- Generic type parameters: `T`, `E`, `K`, `V` (Java generics)

## Variable Naming Rules

### 1. Use Full, Descriptive Names
Every variable name must clearly communicate:
- **WHAT** it represents (domain concept)
- **WHY** it exists (purpose/role)
- **HOW** it's used (context)

```java
// ❌ BAD: Cryptic and unclear
String s = user.getName();
int c = orders.size();
boolean f = status.equals("ACTIVE");

// ✅ GOOD: Self-documenting
String userName = user.getName();
int totalOrderCount = orders.size();
boolean isUserActive = status.equals("ACTIVE");
```

### 2. Loop Variables Must Be Descriptive

```java
// ❌ BAD: Single-character loop variables
for (int i = 0; i < users.size(); i++) {
    User u = users.get(i);
    process(u);
}

// ✅ GOOD: Descriptive loop variables
for (int userIndex = 0; userIndex < users.size(); userIndex++) {
    User currentUser = users.get(userIndex);
    processUser(currentUser);
}

// ✅ EVEN BETTER: Use enhanced for-loop
for (User currentUser : users) {
    processUser(currentUser);
}

// ✅ BEST: Use streams with meaningful names
users.stream()
    .filter(user -> user.isActive())
    .forEach(activeUser -> processUser(activeUser));
```

### 3. Collection Variables

```java
// ❌ BAD: Generic collection names
List<Transaction> list = fetchData();
Map<String, User> map = buildIndex();
Set<String> set = getKeys();

// ✅ GOOD: Descriptive collection names
List<Transaction> pendingTransactions = fetchPendingTransactions();
Map<String, User> usersByEmail = buildEmailToUserIndex();
Set<String> uniqueTransactionIds = extractTransactionIds();
```

### 4. Temporary Variables

```java
// ❌ BAD: Generic "temp" or abbreviated names
String tmp = calculateHash(data);
int res = computeTotal(items);
Object obj = deserialize(json);

// ✅ GOOD: Describe the temporary purpose
String transactionHash = calculateHash(transactionData);
int totalAmount = computeTotal(orderItems);
TransactionRequest deserializedRequest = deserialize(requestJson);
```

### 5. Boolean Variables

```java
// ❌ BAD: Unclear boolean meaning
boolean flag = checkStatus(user);
boolean b = user.getRole().equals("ADMIN");

// ✅ GOOD: Use "is/has/can/should" prefixes
boolean isUserAuthenticated = checkAuthenticationStatus(user);
boolean hasAdminRole = user.getRole().equals("ADMIN");
boolean canProcessPayment = validatePaymentEligibility(transaction);
boolean shouldRetryOperation = attemptCount < MAX_RETRIES;
```

### 6. Lambda Parameters

```java
// ❌ BAD: Single-character lambda parameters
users.stream()
    .filter(u -> u.isActive())
    .map(u -> u.getId())
    .collect(Collectors.toList());

// ✅ GOOD: Descriptive lambda parameters
users.stream()
    .filter(user -> user.isActive())
    .map(activeUser -> activeUser.getId())
    .collect(Collectors.toList());

// ✅ ACCEPTABLE: When type is obvious from context
userIds.stream()
    .map(userId -> fetchUserById(userId))
    .collect(Collectors.toList());
```

### 7. Method Parameters

```java
// ❌ BAD: Abbreviated or unclear parameters
public void process(String s, int n, boolean f) {
    // What are these?
}

// ✅ GOOD: Self-documenting parameters
public void processTransaction(
    String transactionId,
    int attemptNumber,
    boolean shouldValidateBalance
) {
    // Clear intent
}
```

### 8. Exception Handling

```java
// ❌ BAD: Generic exception variable
try {
    executeOperation();
} catch (Exception e) {
    log.error("Error", e);
}

// ✅ GOOD: Descriptive exception variable
try {
    executePaymentTransaction();
} catch (PaymentException paymentError) {
    log.error("Payment transaction failed: {}", paymentError.getMessage());
} catch (ValidationException validationError) {
    log.error("Validation failed: {}", validationError.getDetails());
}
```

### 9. Resource Variables

```java
// ❌ BAD: Generic resource names
try (Connection c = getConnection()) {
    Statement s = c.createStatement();
    ResultSet rs = s.executeQuery("SELECT * FROM users");
}

// ✅ GOOD: Descriptive resource names
try (Connection databaseConnection = getDatabaseConnection()) {
    Statement userQueryStatement = databaseConnection.createStatement();
    ResultSet userRecords = userQueryStatement.executeQuery("SELECT * FROM users");
}
```

### 10. Nested Structures

```java
// ❌ BAD: Confusing nested variables
for (Order o : orders) {
    for (Item i : o.getItems()) {
        for (Tag t : i.getTags()) {
            // What's what?
        }
    }
}

// ✅ GOOD: Clear hierarchy
for (Order order : orders) {
    for (OrderItem orderItem : order.getItems()) {
        for (ItemTag itemTag : orderItem.getTags()) {
            processItemTag(order, orderItem, itemTag);
        }
    }
}
```

## Domain-Specific Naming

### Financial/Transaction Context
```java
// ✅ GOOD: Financial domain names
BigDecimal transactionAmount = transaction.getAmount();
String sourceAccountId = transaction.getSourceAccount();
String destinationAccountId = transaction.getDestinationAccount();
TransactionStatus currentStatus = transaction.getStatus();
List<TransactionFee> applicableFees = calculateTransactionFees(transaction);
```

### UTXO/Blockchain Context
```java
// ✅ GOOD: Blockchain domain names
String utxoIdentifier = utxo.getId();
String ownerAccountId = utxo.getOwner();
List<UtxoTag> utxoLineageTags = utxo.getTags();
byte[] merkleRootHash = calculateMerkleRoot(utxoSet);
List<MerklePathNode> authenticationPath = generateMerkleProof(utxo);
```

## Naming Patterns by Language Feature

### Stream Operations
```java
// ✅ GOOD: Descriptive stream variable names
List<Transaction> validatedTransactions = transactions.stream()
    .filter(transaction -> transaction.isValid())
    .collect(Collectors.toList());

Map<String, List<Transaction>> transactionsByUser = transactions.stream()
    .collect(Collectors.groupingBy(Transaction::getUserId));

Optional<Transaction> largestTransaction = transactions.stream()
    .max(Comparator.comparing(Transaction::getAmount));
```

### Optional Handling
```java
// ❌ BAD: Generic optional name
Optional<User> opt = findUser(userId);

// ✅ GOOD: Descriptive optional name
Optional<User> matchedUser = findUserById(userId);
User retrievedUser = matchedUser.orElseThrow(
    () -> new UserNotFoundException(userId)
);
```

### CompletableFuture
```java
// ❌ BAD: Abbreviated async names
CompletableFuture<String> cf = asyncOp();

// ✅ GOOD: Descriptive future names
CompletableFuture<TransactionResult> pendingTransactionFuture = 
    processTransactionAsync(transactionRequest);

TransactionResult completedTransactionResult = 
    pendingTransactionFuture.get();
```

## Length Guidelines

### Minimum Length
- **Minimum 3 characters** for any identifier (except generic type parameters)
- **Prefer 5+ characters** for clarity
- **Use 10-30 characters** for most variables (sweet spot for readability)
- **Allow 30+ characters** when domain complexity requires it

### Examples of Appropriate Length
```java
// ✅ 10-15 characters (ideal)
String userName;
int orderCount;
boolean isActive;

// ✅ 15-25 characters (good for complex concepts)
String transactionIdentifier;
List<PendingTransaction> pendingTransactions;
Map<String, AccountBalance> accountBalancesByUserId;

// ✅ 25-40 characters (acceptable for very specific concepts)
List<UnspentTransactionOutput> consolidatedUtxoOutputs;
MerkleAuthenticationPath balanceTrieAuthenticationPath;
```

## Code Review Checklist

Before committing code, verify:
- [ ] No single-character variables (except generic type parameters)
- [ ] No two-letter abbreviations standing alone
- [ ] All loop variables are descriptive
- [ ] All lambda parameters clearly indicate their type/purpose
- [ ] Boolean variables use is/has/can/should prefixes
- [ ] Collection variables indicate what they contain
- [ ] Exception variables describe the error type
- [ ] All temporary variables explain their purpose
- [ ] No generic names like "data", "item", "thing", "stuff"
- [ ] Domain concepts are clearly represented in names

## Anti-Patterns to Avoid

### ❌ Hungarian Notation
```java
// ❌ BAD: Hungarian notation (type prefixes)
String strUserName;
int intCount;
boolean bIsValid;

// ✅ GOOD: Clean names (type inference from context)
String userName;
int transactionCount;
boolean isValid;
```

### ❌ Redundant Suffixes
```java
// ❌ BAD: Redundant type suffixes
List<User> userList;
Map<String, User> userMap;
Set<String> idSet;

// ✅ GOOD: Semantic suffixes
List<User> activeUsers;
Map<String, User> usersByEmail;
Set<String> processedTransactionIds;
```

### ❌ Noise Words
```java
// ❌ BAD: Noise words that add no meaning
String theUserName;
int aTransactionCount;
User myUser;

// ✅ GOOD: Direct, meaningful names
String userName;
int transactionCount;
User currentUser;
```

## Special Cases

### When Short Names Are Acceptable

1. **Standard mathematical variables in formulas:**
```java
// ✅ ACCEPTABLE: Mathematical context
double x = point.getX();
double y = point.getY();
double distance = Math.sqrt(x * x + y * y);
```

2. **Well-known abbreviations in domain:**
```java
// ✅ ACCEPTABLE: Standard domain abbreviations
String utxoId = generateUtxoIdentifier();  // UTXO is standard
String httpResponse = sendHttpRequest();   // HTTP is standard
UUID uuid = UUID.randomUUID();            // UUID is standard
```

3. **Generic type parameters:**
```java
// ✅ ACCEPTABLE: Standard Java generics
public class Repository<T> {
    public List<T> findAll() { }
}

public interface Mapper<S, T> {
    T map(S source);
}
```

## Summary

**Golden Rule:** If someone reading your code needs to look elsewhere to understand what a variable represents, the name is too short or unclear.

Every identifier should be:
1. **Self-documenting**: The name explains its purpose
2. **Unambiguous**: No confusion with similar concepts
3. **Consistent**: Follows project naming patterns
4. **Domain-appropriate**: Uses correct terminology
5. **Pronounceable**: Can be spoken in code reviews
6. **Searchable**: Easy to find with text search

Remember: Code is read far more often than it's written. Invest in clarity.
