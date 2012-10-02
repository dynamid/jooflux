/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.aspects;

import java.util.Arrays;

public class Dumpers {

    public static Object[] onCall(Object[] args) {
        System.out.println(">>> " + Arrays.toString(args));
        return args;
    }

    public static Object onReturn(Object retval) {
        System.out.println("<<< " + retval);
        return retval;
    }
}
