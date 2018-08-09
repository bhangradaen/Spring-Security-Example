package com.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

public class ReflectionUtil {

    public static <E> boolean allAccessorMethodReturnValuesAreEqualBetweenInstances(
            Method[] methodsToCheck, Object object, E instance) {
        try {
            for (Method method : methodsToCheck) {
                if (isAccessorMethod(method) &&
                        methodReturnValuesDifferBetweenInstances(method, object, instance))
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean isAccessorMethod(Method method) {
        return method.getName().substring(0, 3).equals("get") && method.getParameterCount() == 0;
    }

    private static <T> boolean methodReturnValuesDifferBetweenInstances(Method method, Object object, T instance)
            throws InvocationTargetException, IllegalAccessException {
        return method.invoke(object) != method.invoke(instance);
    }

    public static <T> boolean instancesAreNotOfTheSameClass(Object object, T instance) {
        return !object.getClass().equals(instance.getClass());
    }

    public static <T> int generateHashForMethodsOfInstance(
            Method[] methods, T instance) {
        Class[] classesToBeHashed = {Integer.class, String.class, Boolean.class};
        int totalHash = 0;
        for (Class clazz: classesToBeHashed) {
            totalHash += Objects.hash(deriveMethodReturnValuesFromInstanceForHash(methods, instance, clazz));
        }
        return totalHash / 3;
    }

    private static <T> ArrayList deriveMethodReturnValuesFromInstanceForHash(
            Method[] methodsToCheck, T instance, Class clazz) {
        ArrayList valuesForHash = new ArrayList<>();
        for (Method method : methodsToCheck) {
            try {
                if (isAccessorMethod(method) && clazz.isInstance(method.invoke(instance)))
                    valuesForHash.add(method.invoke(instance));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return valuesForHash;
    }

}