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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyPictureActionListener implements ActionListener {

    int index = 0;
    MyJLabel myJLabel;

    public MyPictureActionListener(MyJLabel myJLabel) {
        this.myJLabel = myJLabel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        pictureSwitch();
    }

    public void pictureSwitch() {
        index++;
        myJLabel.setText("");
        myJLabel.setIcon(new ImageIcon("images/duke" + index % 2 + ".jpg"));
    }
}
