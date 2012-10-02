/*
 * JooFlux
 *
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.jooflux.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static java.lang.invoke.MethodHandles.filterArguments;
import static java.lang.invoke.MethodHandles.filterReturnValue;


public class Aspects {

    public static MethodHandle before(MethodHandle target, MethodHandle beforeFilter) {
        MethodType targetType = target.type();
        MethodType filterType = beforeFilter.type();
        int parameterCount = targetType.parameterCount();

        if (filterType.parameterCount() != 1 && !filterType.parameterType(0).equals(Object[].class)) {
            throw new IllegalArgumentException("beforeFilter must have a single argument of type Object[]");
        }
        if (!filterType.returnType().equals(Object[].class)) {
            throw new IllegalArgumentException("beforeFilter must have a Object[] return type");
        }

        MethodHandle spreader = target.asSpreader(Object[].class, parameterCount);
        MethodHandle filter = filterArguments(spreader, 0, beforeFilter);
        MethodHandle collector = filter.asCollector(Object[].class, parameterCount);

        return collector.asType(targetType);

    }

    public static MethodHandle after(MethodHandle target, MethodHandle afterFilter) {
        MethodType targetType = target.type();
        MethodType filterType = afterFilter.type();

        if (filterType.parameterCount() != 1 && !filterType.parameterType(0).equals(Object.class)) {
            throw new IllegalArgumentException("afterFilter must have a single argument of type Object");
        }
        if (!filterType.returnType().equals(Object.class)) {
            throw new IllegalArgumentException("afterFilter must have a Object return type");
        }

        MethodHandle asObject = target.asType(targetType.changeReturnType(Object.class));
        MethodHandle filter = filterReturnValue(asObject, afterFilter);

        return filter.asType(targetType);
    }
}
