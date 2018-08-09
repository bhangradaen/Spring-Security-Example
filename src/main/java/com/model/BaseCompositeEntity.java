package com.model;

import com.util.ReflectionUtil;

import java.lang.reflect.Method;

public abstract class BaseCompositeEntity {

    @Override
    public boolean equals(Object object) {
        if (ReflectionUtil.instancesAreNotOfTheSameClass(object, this)) return false;
        Method[] methodsToCheck = this.getClass().getMethods();
        return ReflectionUtil.allAccessorMethodReturnValuesAreEqualBetweenInstances(
                methodsToCheck, object, this);
    }

    @Override
    public int hashCode() {
        Method[] methodsToCheck = this.getClass().getMethods();
        return ReflectionUtil.generateHashForMethodsOfInstance(
                methodsToCheck, this);
    }

}