/*
 *
 * Copyright 2010 David Armenteros Escabias, Ángel Daniel Sanjuán Espejo.
 *
 * This file is part of BluFeedMe.
 *
 * BluFeedMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package blufeedmebt.vista;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class RendererListaLog extends JLabel implements ListCellRenderer {

    public RendererListaLog() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());

        Color background;
        Color foreground = Color.WHITE;

        //Si no esta seleccionado
        if (!isSelected) {
            //Dependiendo del nivel de log dibujamos con un color u otro
            if (((String) value).contains("SEVERE:")) {
                background = new Color(255, 79, 79);
            } else {
                if (((String) value).contains("WARNING:")) {
                    background = new Color(255, 237, 94);
                    foreground = Color.BLACK;
                } else {
                    background = Color.WHITE;
                    foreground = Color.BLACK;
                }
            }
        } else {
            background = new Color(41, 135, 227);
        }

        setBackground(background);
        setForeground(foreground);

        return this;
    }
}
