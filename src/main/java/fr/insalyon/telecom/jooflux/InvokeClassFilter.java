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
            "[Ljava/", "java/",
            "[Lsun/", "sun/",
            "[Lcom/sun/", "com/sun/",
            "[Lapple/", "apple/",
            "[Lcom/apple/", "com/apple/",
            "[Ljavax/", "javax/",
            "[Lfr/insalyon/telecom/jooflux/internal/", "fr/insalyon/telecom/jooflux/internal/",
            "[Lorg/pmw/tinylog/", "org/pmw/tinylog/"
    };


    public static boolean isAllowed(String owner) {
        for (String filter : filteredPackages) {
            if (owner.startsWith(filter)) {
                return false;
            }
        }
        return true;
    }

}
