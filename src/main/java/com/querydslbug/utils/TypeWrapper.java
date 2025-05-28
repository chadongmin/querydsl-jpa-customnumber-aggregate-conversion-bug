package com.querydslbug.utils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Visitor;

import java.util.List;
import java.util.function.Function;

public class TypeWrapper<S, T> implements FactoryExpression<T> {
    private final Class<T> valueClass;
    private final Function<S, T> factory;
    private final List<Expression<?>> args;

    public TypeWrapper(Expression<S> arg, Class<T> valueClass, Function<S, T> factory) {
        this.valueClass = valueClass;
        this.factory = factory;
        this.args = List.of(arg);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public Class<? extends T> getType() {
        return valueClass;
    }

    @Override
    public List<Expression<?>> getArgs() {
        return args;
    }

    @Override
    public T newInstance(Object... args) {
        System.out.println("TypeWrapper.newInstance called!");
        S arg = (S) args[0];
        return factory.apply(arg);
    }
}