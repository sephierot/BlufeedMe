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

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class ListStreamHandler extends StreamHandler {

    private final DefaultListModel modlistaLog;
    private final JList listaLog;
    private int n_lineasMax;

    public ListStreamHandler(final JList listaLog, int nlineasMax) {
        super();
        this.listaLog = listaLog;
        this.modlistaLog = (DefaultListModel) listaLog.getModel();
        this.n_lineasMax = nlineasMax;
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        String msg = getFormatter().format(record);

        //Si se realiza una nueva búsqueda se borra el panel de log previamente
        if (this.modlistaLog.getSize() > this.n_lineasMax) {
            this.modlistaLog.clear();
        }

        this.modlistaLog.addElement(msg);
        //Hace que el scroll de la lista se mueva al último elemento insertado
        if (this.listaLog.getSelectedIndex() == -1) {
            this.listaLog.ensureIndexIsVisible(modlistaLog.size() - 1);
        }
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        int levelValue = getLevel().intValue();
        if (record.getLevel().intValue() < levelValue || levelValue == Level.OFF.intValue()) {
            return false;
        }
        Filter filter = getFilter();
        if (filter == null) {
            return true;
        }
        return filter.isLoggable(record);
    }

    public int getN_lineasMax() {
        return n_lineasMax;
    }

    public DefaultListModel getmodListaLog() {
        return modlistaLog;
    }

    public JList getListaLog() {
        return listaLog;
    }

    public boolean setN_lineasMax(int n_lineasMax) {
        if (n_lineasMax > 0) {
            this.n_lineasMax = n_lineasMax;
            return true;
        }

        return false;
    }
}
