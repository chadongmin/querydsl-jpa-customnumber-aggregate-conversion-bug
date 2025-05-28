# querydsl-jpa-customnumber-aggregate-conversion-failure

This repository demonstrates a known compatibility issue in **QueryDSL + JPA** when using `NumberExpression.sum()` on **custom types that extend `Number`**, and proposes a working workaround using `FactoryExpression`.

---

## 🔥 Problem

QueryDSL cannot project the result of an aggregate operation such as `sum()` into a custom value type (e.g., `MyCustomNumber` extending `Number`) when used in JPA queries.

### Failing Code

```java
myEntity.myCustomNumber.sum()
```

### Exception

```
java.lang.IllegalArgumentException: Unsupported target type : MyCustomNumber
```

### Cause

- JPA returns the aggregate result as `BigDecimal`
- Hibernate attempts to map it directly to `MyCustomNumber`, but fails
- QueryDSL internally uses `MathUtils.cast()` which doesn't support such conversions

---

## ✅ Solution (Workaround)

We define a `TypeWrapper<S, T>` class that implements `FactoryExpression<T>`, allowing us to explicitly wrap the `sum()` result into our desired custom type.

### Working Example

```java
NumberExpression<BigDecimal> sumExpr = myEntity.myCustomNumber.sum().castToNum(BigDecimal.class);

MyCustomNumber result = new JPAQuery<>(em)
    .select(new TypeWrapper<>(
        sumExpr,
        MyCustomNumber.class,
        val -> new MyCustomNumber(val.toString())))
    .from(myEntity)
    .fetchOne();
```

This triggers `TypeWrapper.newInstance()` and successfully returns a custom value object.

---

## 🧪 Test Cases

| Test | Description | Status |
|------|-------------|--------|
| `plain_queries_call_the_converters()` | Entity fetch with @Converter | ✅ Pass |
| `simple_projection_queries_work()` | DTO projection with custom type | ✅ Pass |
| `queries_with_NumberExpression_fails()` | sum() directly → fails | ❌ Fail |
| `projection_queries_with_sum_fail()` | DTO + sum() → fails | ❌ Fail |
| `type_wrapper_projection_with_sum_should_convert_result_to_custom_type()` | sum() wrapped with TypeWrapper | ✅ Pass |

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
- Spring Boot 3.x
- Hibernate 6.x
- QueryDSL 5.x
- H2 (in-memory DB)

---

## 📝 Summary

This repository serves as:

- A minimal reproducible example of [QueryDSL Issue #261](https://github.com/OpenFeign/querydsl/issues/261)
- A working workaround using only FactoryExpression, without modifying QueryDSL internals
- A candidate for documentation or community reference

---

## 📎 References

- [QueryDSL Issue #261](https://github.com/OpenFeign/querydsl/issues/261)
- [QueryDSL FactoryExpression Docs](https://querydsl.com/static/querydsl/latest/reference/html_single/#d0e2836)
