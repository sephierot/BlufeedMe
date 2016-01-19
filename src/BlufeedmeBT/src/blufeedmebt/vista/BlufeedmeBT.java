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

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;


/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class BlufeedmeBT extends SingleFrameApplication {
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        show(new BlufeedmeBTView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of BlufeedmeBT
     */
    public static BlufeedmeBT getApplication() {
        return Application.getInstance(BlufeedmeBT.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        //Lanzamos la interfaz grafica
        launch(BlufeedmeBT.class, args);
    }
}

        
