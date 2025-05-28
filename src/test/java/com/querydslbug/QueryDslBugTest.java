package com.querydslbug;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydslbug.entity.MyCustomNumber;
import com.querydslbug.entity.MyEntity;
import com.querydslbug.entity.MyProjection;
import com.querydslbug.entity.QMyProjection;
import com.querydslbug.utils.TypeWrapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;

import static com.querydslbug.entity.QMyEntity.myEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback
public class QueryDslBugTest {
    @PersistenceContext
    private EntityManager em;

    private final MyEntity aaa111 = new MyEntity(
            "00000000-0000-0000-0000-000000000001",
            "aaa",
            new MyCustomNumber("111")
    );
    private final MyEntity aaa222 = new MyEntity(
            "00000000-0000-0000-0000-000000000002",
            "aaa",
            new MyCustomNumber("222")
    );
    private final MyEntity zzz444 = new MyEntity(
            "00000000-0000-0000-0000-000000000004",
            "zzz",
            new MyCustomNumber("444")
    );
    private final MyEntity zzz555 = new MyEntity(
            "00000000-0000-0000-0000-000000000005",
            "zzz",
            new MyCustomNumber("555")
    );

    @BeforeEach
    void beforeEach() {
        em.persist(aaa111);
        em.persist(aaa222);
        em.persist(zzz444);
        em.persist(zzz555);

        em.flush();
    }

    @Test
    void plain_queries_call_the_converters() {
        var rows = new JPAQuery<>(em)
                .select(myEntity)
                .from(myEntity)
                .fetch();

        // works as expected, values
        assertThat(rows)
                .usingElementComparator(MyEntity.comparator())
                .containsExactlyInAnyOrder(
                        aaa111,
                        aaa222,
                        zzz444,
                        zzz555
                );
    }

    @Test
    void simple_projection_queries_work() {
        var rows = new JPAQuery<>(em)
                .select(new QMyProjection(myEntity.type, myEntity.myCustomNumber))
                .from(myEntity)
                .fetch();

        assertThat(rows)
                .containsExactlyInAnyOrder(
                        new MyProjection("aaa", new MyCustomNumber("111")),
                        new MyProjection("aaa", new MyCustomNumber("222")),
                        new MyProjection("zzz", new MyCustomNumber("444")),
                        new MyProjection("zzz", new MyCustomNumber("555"))
                );
    }

    @Test
    void queries_with_NumberExpression_fails() {
        var rows = new JPAQuery<>(em)
                .select(myEntity.myCustomNumber.sum())
                .from(myEntity)
                .fetch();

        // instead throws:
        // java.lang.IllegalArgumentException: Unsupported target type : MyCustomNumber

        assertThat(rows)
                .containsExactlyInAnyOrder(
                        new MyCustomNumber("1332")
                );
    }

    @Test
    void projection_queries_with_sum_fail() {
        var rows = new JPAQuery<>(em)
                .select(new QMyProjection(myEntity.type, myEntity.myCustomNumber.sum()))
                .from(myEntity)
                .groupBy(myEntity.type)
                .fetch();

        // instead throws:
        // java.lang.IllegalArgumentException: Unsupported target type : MyCustomNumber

        assertThat(rows)
                .containsExactlyInAnyOrder(
                        new MyProjection("aaa", new MyCustomNumber("333")),
                        new MyProjection("zzz", new MyCustomNumber("999"))
                );
    }

    @Test
    void type_wrapper_projection_with_sum_should_convert_result_to_custom_type() {
        // when
        NumberExpression<BigDecimal> sumExpr = myEntity.myCustomNumber.sum().castToNum(BigDecimal.class);

        MyCustomNumber result = new JPAQuery<>(em)
                .select(new TypeWrapper<>(
                        sumExpr,
                        MyCustomNumber.class,
                        (BigDecimal val) -> new MyCustomNumber(val.toString())))
                .from(myEntity)
                .fetchOne();

        // then
        assertThat(result).isEqualTo(new MyCustomNumber("1332.0"));
    }
}
