# querydsl-jpa-customnumber-aggregate-conversion-failure

This repository demonstrates a known compatibility issue in **QueryDSL 6.11 + JPA** when using `NumberExpression.sumAggregate()` on **custom types extending `Number`**, and provides a working workaround using `FactoryExpression`.

---

## 🔥 Problem

QueryDSL cannot automatically project the result of aggregate functions like `sumAggregate()` into custom value types (e.g., `MyCustomNumber extends Number`) when using JPA.

### ❌ Failing Code

```java
myEntity.myCustomNumber.sumAggregate()
```

### 💥 Exception

```
java.lang.IllegalArgumentException: Unsupported target type : MyCustomNumber
```

### 🧠 Root Cause

- The aggregate function returns `BigDecimal` at runtime.
- QueryDSL attempts to cast this `BigDecimal` into the declared custom type (`MyCustomNumber`) via its internal `MathUtils.cast()` logic.
- Since `MathUtils.cast()` only supports standard number types, it fails to handle arbitrary subclasses like `MyCustomNumber`.

---

## ✅ Solution (Workaround)

To work around this, we introduce a `TypeWrapper<S, T>` class that implements `FactoryExpression<T>`. This allows us to explicitly convert aggregate results (e.g., `BigDecimal`) into custom value objects.

### ✅ Working Code

```java
NumberExpression<BigDecimal> sumExpr = myEntity.myCustomNumber.sumAggregate().castToNum(BigDecimal.class);

MyCustomNumber result = new JPAQuery<>(em)
    .select(new TypeWrapper<>(
        sumExpr,
        MyCustomNumber.class,
        val -> new MyCustomNumber(val.toString())
    ))
    .from(myEntity)
    .fetchOne();
```

This approach ensures the value is properly wrapped using a constructor or conversion logic you define.

---

## 🧪 Test Coverage

| Test Name                                                                          | Description                                   | Status |
|------------------------------------------------------------------------------------|-----------------------------------------------|--------|
| `plain_queries_call_the_converters()`                                              | Basic entity fetch with `@Converter`          | ✅     |
| `simple_projection_queries_work()`                                                 | DTO projection with a custom type field       | ✅     |
| `queries_with_NumberExpression_fails()`                                            | `sumAggregate()` directly → cast failure      | ❌     |
| `projection_queries_with_sum_fail()`                                               | DTO + `sumAggregate()` → constructor failure  | ❌     |
| `type_wrapper_projection_with_sum_should_convert_result_to_custom_type()`          | `sumAggregate()` with `TypeWrapper` → works   | ✅     |

---

## 📦 Project Structure

```
src/
 └── main/
     └── java/
         └── com.querydslbug/
             ├── entity/           # MyEntity, MyCustomNumber, etc.
             ├── utils/            # TypeWrapper class
             └── QueryDslBugTest   # All test cases
```

---

## 🧭 Environment

- Java 17  
- Spring Boot 3.5.x  
- Hibernate 6.x  
- QueryDSL 6.11  
- H2 (in-memory DB)  

---

## 📝 Summary

This repository serves as:

- A minimal reproducible example of [QueryDSL Issue #261](https://github.com/OpenFeign/querydsl/issues/261)  
- A working workaround using `FactoryExpression`, without modifying QueryDSL internals  
- A candidate for documentation or community reference  

---

## 📎 References

- [QueryDSL Issue #261](https://github.com/OpenFeign/querydsl/issues/261)  
- [QueryDSL FactoryExpression Docs](https://querydsl.com/static/querydsl/latest/reference/html_single/#d0e2836)  
