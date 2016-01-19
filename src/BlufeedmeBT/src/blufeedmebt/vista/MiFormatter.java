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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class MiFormatter extends Formatter {

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy,HH:mm:ss");

    public MiFormatter() {
        super();
    }

    @Override
    public String format(LogRecord record) {
        //Crear un StringBuilder para que contenga el registro formateado
        StringBuilder sb = new StringBuilder();

        //Obtener el nombre de nivel y añadirlo al string
        sb.append(record.getLevel().getName());
        sb.append(": (");

        //Obtener la fecha de LogRecord y añadirla al string
        Date fecha = new Date(record.getMillis());
        sb.append(this.formatoFecha.format(fecha));
        sb.append(")         ");

        // Obtener el mensaje formateado (incluye la localización
        // y sustitución de parámetros) y añadirlo al almacenamiento intermedio
        sb.append(this.formatMessage(record));

        return sb.toString();
    }

    @Override
    public synchronized String formatMessage(LogRecord record) {
        String format = record.getMessage();
        
        try {
            Object parameters[] = record.getParameters();
            if (parameters == null || parameters.length == 0) {
                return format;
            }

            if (format.indexOf("{0") >= 0 || format.indexOf("{1") >= 0
                    || format.indexOf("{2") >= 0 || format.indexOf("{3") >= 0) {
                return java.text.MessageFormat.format(format, parameters);
            }
            return format;
        } catch (Exception ex) {
            return format;
        }
    }
}
