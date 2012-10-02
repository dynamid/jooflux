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
import java.awt.event.ActionListener;

public class MyGUI extends JFrame {

    MyJButton myFirstJButton = new MyJButton(" Go ! ");
    MyJLabel myJLabel = new MyJLabel(" 0 ");
    //    ActionListener myActionListener = new MyCounterActionListener(myJLabel);
//    ActionListener myActionListener2 = new MyPictureActionListener(myJLabel);
    ActionListener myActionListener = new MyActionListener(myJLabel);

    public MyGUI() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout());
        add(myFirstJButton);
        add(myJLabel);
        myFirstJButton.addActionListener(myActionListener);
        pack();
        setVisible(true);
    }

    public static void main(String... args) {
        new MyGUI();
    }
}
