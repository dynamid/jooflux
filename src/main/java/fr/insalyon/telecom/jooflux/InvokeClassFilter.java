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

package fr.insalyon.telecom.jooflux;

public class InvokeClassFilter {

    private static final String[] filteredPackages = new String[]{
            "java/",
            "sun/",
            "com/sun/",
            "apple/",
            "com/apple/",
            "javax/",
            "fr/insalyon/telecom/jooflux/internal/",
            "fr/insalyon/telecom/jooflux/tcp/",
            "org/json/simple/",
            "org/pmw/tinylog/"
    };


    public static boolean isAllowed(String owner) {
        if (owner.startsWith("[")) {
            return false;
        }
        for (String filter : filteredPackages) {
            if (owner.startsWith(filter)) {
                return false;
            }
        }
        return true;
    }

}
