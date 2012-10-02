/*
 * Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 * Copyright (c) 2012 Julien Ponge, INSA-Lyon
 * Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package fr.insalyon.telecom.joofluxtest.gui.switcher;

import javax.swing.*;
import java.awt.*;

public class MyJButton extends JButton {

    public MyJButton(String s) {
        super(s);
        setFontOne();
    }

    public void setFontOne() {
        setFont(new Font("SansSerif", Font.BOLD, 100));
        updateUI();
    }
}
