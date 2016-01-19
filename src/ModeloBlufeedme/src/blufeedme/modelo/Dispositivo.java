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

import java.util.ArrayList;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class Dispositivo {
    private Long id;
    private String mac;
    private String pin;
    private String URL_servicio;
    
    private ArrayList<Categoria> categorias;

    public static final long ID_NULL = 0;

    /**
     * Constructor
     */
    public Dispositivo() {
        pin = null;
        id = ID_NULL;
        mac = null;
        categorias = new ArrayList<Categoria>();
        URL_servicio = null;
    }

    /**
     * Constructor
     * @param id identificador del dispositivo.
     * @param mac dirección MAC del dispositivo movil.
     * @param pin pin del dispositivo.
     * @param c Categorías a las que esta vinculado el dispositivo.
     * @param URL_servicio dirección del servicio para el envío de datos.
     */
    public Dispositivo(Long id, String mac, String pin, ArrayList<Categoria> c, String URL_servicio) {
        if (id > 0) this.id = id;
        else this.id = ID_NULL;
        this.mac = mac;
        this.pin = pin;
        this.categorias = c;
        this.URL_servicio = URL_servicio;
    }

    /**
     * Asigna un identificador al dispositivo
     * @param id identificador
     */
    public void setId(Long id){
        this.id = id;
    }

    /**
     * vincula el dispositivo a una lista de categorías
     * @param categorias lista con las categorías a las que será vinculado el dispositivo
     */
    public void setCategorias(ArrayList<Categoria> categorias) {
        this.categorias = categorias;
    }

    /**
     * Asigna una dirección MAC al dispositivo.
     * @param mac dirección MAC
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * Asigna un pin al dispositivo
     * @param pin pin
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     * Asigna una dirección url de servicio al dispositivo.
     * @param URL_servicio dirección del servicio.
     */
    public void setURL_servicio(String URL_servicio) {
        this.URL_servicio = URL_servicio;
    }

    /**
     * Obtiene el identificador de un dispositivo
     * @return identificador
     */
    public Long getId(){
        return this.id;
    }

    /**
     * Obtiene la lista de categorías a las que está vinculado el dispositivo
     * @return lista de categorías
     */
    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    /**
     * Obtiene la dirección MAC del dispositivo
     * @return dirección MAC
     */
    public String getMac() {
        return mac;
    }

    /**
     * Obtiene el pin del dispositivo
     * @return pin
     */
    public String getPin() {
        return pin;
    }

    /**
     * Obtiene la dirección de servicio del dispositivo
     * @return url de servicio
     */
    public String getURL_servicio() {
        return URL_servicio;
    }

    /**
     * Vincula el dispositivo a una categoría
     * @param c categoría
     * @return TRUE si la operación a sido realizada con éxito, FALSE en caso contrario
     */
    public boolean addCategoria(Categoria c){
        return this.categorias.add(c);
    }

    /**
     * Desvincula el dispositivo de una categoría
     * @param c categoría
     * @return TRUE si la operación a sido realizada con éxito, FALSE en caso contrario
     */
    public boolean deleteCategoria(Categoria c){
        return this.categorias.remove(c);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Dispositivo other = (Dispositivo) obj;
        if ((this.mac == null) ? (other.mac != null) : !this.mac.equals(other.mac)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.mac != null ? this.mac.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String g = "Id:"+this.id+"\tMAC:"+this.mac+"\tURL:"+this.URL_servicio;

        g += "\nCategorias:\n";

        if(this.categorias != null){
            for(Categoria cat:this.categorias) g += cat.toString() + "\n";
        }

        return g;
    }
}
