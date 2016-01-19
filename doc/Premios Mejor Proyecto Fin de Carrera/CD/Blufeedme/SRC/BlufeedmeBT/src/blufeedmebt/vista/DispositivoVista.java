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

import blufeedme.modelo.Categoria;
import blufeedme.modelo.Noticia;
import java.util.ArrayList;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class DispositivoVista {
    private String mac;
    private String nombre;
    private boolean registrado;
    private String pin;
    private String URL_servicio;
    private ArrayList<Categoria> categorias;
    private ArrayList<Noticia> noticias;

    public DispositivoVista(String mac, String nombre, boolean registrado, String pin, String URL_servicio, ArrayList<Categoria> categorias, ArrayList<Noticia> noticias) {
        this.mac = mac;
        this.nombre = nombre;
        this.registrado = registrado;
        this.pin = pin;
        this.URL_servicio = URL_servicio;
        this.categorias = categorias;
        this.noticias = noticias;
    }

    
    public void setURL_servicio(String URL_servicio) {
        this.URL_servicio = URL_servicio;
    }

    public void setCategorias(ArrayList<Categoria> categorias) {
        this.categorias = categorias;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNoticias(ArrayList<Noticia> noticias) {
        this.noticias = noticias;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }

    public String getURL_servicio() {
        return URL_servicio;
    }

    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    public String getMac() {
        return mac;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    public String getPin() {
        return pin;
    }

    public boolean getRegistrado() {
        return registrado;
    }

    @Override
    public String toString() {
        return mac.substring(0,2)+":"+mac.substring(2,4)+":"+mac.substring(4,6)+":"+mac.substring(6,8)+":"+mac.substring(8,10)+":"+mac.substring(10,12);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DispositivoVista other = (DispositivoVista) obj;
        if ((this.mac == null) ? (other.mac != null) : !this.mac.equals(other.mac)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.mac != null ? this.mac.hashCode() : 0);
        return hash;
    }
}
