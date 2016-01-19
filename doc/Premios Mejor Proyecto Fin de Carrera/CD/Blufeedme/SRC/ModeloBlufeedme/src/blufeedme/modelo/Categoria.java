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
public class Categoria {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long idGestor;

    public static final long ID_NULL = 0;

    /**
     * Constructor
     */
    public Categoria() {
        this.id = ID_NULL;
        this.nombre = "";
        this.descripcion = "";
        this.idGestor = ID_NULL;
    }

    /**
     * Constructor
     * @param id Identificador de la categoría
     * @param nombre Nombre de la categoría
     * @param descripcion Descripción de la categoría
     * @param idGestor Identificador del gestor de la categoría con identificador 'id'
     */
    public Categoria(Long id, String nombre, String descripcion, Long idGestor) {
        if(id > 0) this.id = id;
        else this.id = ID_NULL;
        
        if(nombre != null) this.nombre = nombre;
        else this.nombre = "";

        if(descripcion != null) this.descripcion = descripcion;
        else this.descripcion = "";

        if(idGestor > 0) this.idGestor = idGestor;
        else this.idGestor = ID_NULL;
    }

    /**
     * Constructor
     * @param id Identificador de la categoría
     * @param nombre Nombre de la categoría
     * @param descripcion Descripción de la categoría
     */
    public Categoria(Long id, String nombre, String descripcion) {
        if(id > 0) this.id = id;
        
        if(nombre != null) this.nombre = nombre;
        else this.nombre = "";

        if(descripcion != null) this.descripcion = descripcion;
        else this.descripcion = "";

        this.idGestor = ID_NULL;
    }

    /**
     * Obtiene la descripción de la categoría.
     * @return descripción de la categoría.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el identificador de una categoría
     * @return identificador de la categoría
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene el identificador del gestor que administra la categoría.
     * @return identificador del gestor
     */
    public Long getIdGestor() {
        return idGestor;
    }

    /**
     * Obtiene el nombre de la categoría.
     * @return nombre de la categoría.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna una descripción a la categoría.
     * @param descripcion descripción será asignada a la categoría.
     */
    public void setDescripcion(String descripcion) {
        if(descripcion != null) this.descripcion = descripcion;
    }

    /**
     * Asigna un identificador a la categoría
     * @param id identificador que será asignado.
     */
    public void setId(Long id) {
        if(id > 0 || id == ID_NULL) this.id = id;
    }

    /**
     * Asigna un gestor a la categoría
     * @param idGestor identificador del gestor.
     */
    public void setIdGestor(Long idGestor) {
        if(idGestor > 0 || idGestor == ID_NULL) this.idGestor = idGestor;
    }

    /**
     * Asigna un nombre a la categoría
     * @param nombre nombre que se asignará a la categoría
     */
    public void setNombre(String nombre) {
        if(nombre != null) this.nombre = nombre;
    }

    /**
     * Establece el valor del gestor que administra la categoría a ID_NULL.
     */
    public void unsetGestor(){
        this.idGestor = ID_NULL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Categoria other = (Categoria) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        String c = "Id:"+this.id+"\tNombre:"+this.nombre+"\tDescripcion:"+this.descripcion+"\tGestor:"+this.idGestor;

        return c;
    }
}

