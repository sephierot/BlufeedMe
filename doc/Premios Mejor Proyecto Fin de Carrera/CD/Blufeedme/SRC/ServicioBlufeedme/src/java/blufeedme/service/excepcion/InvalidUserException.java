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

package blufeedme.service.excepcion;

/**
 * @author Ángel Daniel Sanjuán Espejo,David Armenteros Escabias
 *  angdanni@gmail.com, davidarmesc@gmail.com
 * http://blufeedme.wordpress.com/
 */

public class InvalidUserException extends Exception {

    /**
     * Creates a new instance of <code>InvalidUserException</code> without detail message.
     */
    public InvalidUserException() {
    }


    /**
     * Constructs an instance of <code>InvalidUserException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidUserException(String msg) {
        super(msg);
    }
}
