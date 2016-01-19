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
package blufeedme.modelo;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class Usuario {

    private long id;
    private String nombre;
    private String contrasenia;

    public static final long ID_NULL = 0;

    /**
     * Constructor
     */
    public Usuario() {
        this.id = ID_NULL;
        this.nombre = "";
        this.contrasenia = "";
    }

    /**
     * Constructor
     * @param id_usuario identificador del usuario
     * @param nombre nombre del usuario
     * @param contrasenia contraseña del usuario
     */
    public Usuario(long id_usuario, String nombre, String contrasenia) {
        if(id_usuario > 0) this.id = id_usuario;
        else this.id = ID_NULL;

        if(nombre != null) this.nombre = nombre;
        else this.nombre = "";

        if(contrasenia != null) this.contrasenia = contrasenia;
        else this.contrasenia = "";
    }

    /**
     * Asigna un identificador al usuario
     * @param id_usuario identificador
     */
    public void setId(long id_usuario) {
        if(id_usuario > 0 || id_usuario == ID_NULL) this.id = id_usuario;
    }

    /**
     * Asigna un nombre al usuario
     * @param nombre nombre
     */
    public void setNombre(String nombre) {
        if(nombre != null) this.nombre = nombre;
    }

    /**
     * Asigna una contraseña al usuario
     * @param contrasenia contraseña
     */
    public void setContrasenia(String contrasenia) {
        if(contrasenia  != null) this.contrasenia = contrasenia;
    }

    /**
     * Obtiene el nombre de usuario
     * @return nombre
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Obtiene la contraseña del usuario
     * @return contraseña
     */
    public String getContrasenia() {
        return this.contrasenia;
    }

    /**
     * Obtiene el identificador del usuario
     * @return identificador
     */
    public long getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Usuario other = (Usuario) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        String g = "Id:"+this.id+"\tNombre:"+this.nombre+"\tContraseña:"+this.contrasenia;
        return g;
    }
}
