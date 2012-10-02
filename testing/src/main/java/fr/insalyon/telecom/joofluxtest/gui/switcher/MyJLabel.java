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

public class MyJLabel extends JLabel {

    public MyJLabel() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setSize();
        setFontOne();
    }

    public MyJLabel(Icon icon) {
        super(icon);
        setHorizontalAlignment(JLabel.CENTER);
        setSize();
        setFontOne();
    }

    public MyJLabel(String s) {
        super(s);
        setHorizontalAlignment(JLabel.CENTER);
        setSize();
        setFontOne();
    }

    public void setFontOne() {
        setFont(new Font("SansSerif", Font.PLAIN, 250));
        setForeground(Color.BLACK);
    }

    public void setFontTwo() {
        setFont(new Font("Palatino", Font.ITALIC | Font.BOLD, 250));
        setForeground(new Color(215, 42, 12));
    }

    public void setSize() {
        setSize(500, 500);
        setMinimumSize(new Dimension(500, 500));
    }
}
