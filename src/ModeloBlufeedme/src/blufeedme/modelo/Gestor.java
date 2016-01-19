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
public class Gestor extends Usuario{

    private ArrayList<Categoria> categorias;

    /**
     * Constructor
     */
    public Gestor() {
        this.categorias = new ArrayList<Categoria>();
    }

    /**
     * Constructor
     * @param id_usuario identificador de gestor
     * @param nombre nombre del usuario
     * @param contrasenia contraseña del gestor
     * @param categorias categorías que administra el gestor
     */
    public Gestor(long id_usuario, String nombre, String contrasenia, ArrayList<Categoria> categorias) {
        super(id_usuario,nombre,contrasenia);
        
        this.categorias = new ArrayList<Categoria>();
        if(categorias != null){
            for(Categoria c:categorias) this.aniadeCategoria(c);
        }
    }

    /**
     * Asigna la lista de categorías que administra el gestor
     * @param categorias lista de categorías
     */
    public void setCategorias(ArrayList<Categoria> categorias) {
        if(categorias == null) this.categorias = null;
        else{
            this.categorias = new ArrayList<Categoria>();
            for(Categoria c:categorias) this.aniadeCategoria(c);
        }
    }

    /**
     * Añade una categoria a la lista de categorías administradas por el gestor
     * @param categoria categoría
     * @return TRUE si la operación se ha realizado con éxito, FALSE en caso contrario.
     */
    public boolean aniadeCategoria(Categoria categoria){
        if(categoria != null){
            if(this.categorias.contains(categoria)) return false;
            else{
                this.categorias.add(categoria);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina una categoría de la lista de categorías administradas por el gestor
     * @param categoria categoría a eliminar
     * @return TRUE si la operación tiene éxito, FALSE en caso contrario
     */
    public boolean quitaCategoria(Categoria categoria){
        if(categoria != null){
            if(this.categorias.contains(categoria))
                return this.categorias.remove(categoria);
            else return false;
        }
        return false;        
    }

    /**
     * Comprueba si el gestor administra una categoría
     * @param categoria categoría a comprobar
     * @return TRUE en caso de que la categoría sea administrada por el gestor, FALSE en caso contrario.
     */
    public boolean contieneCategoria(Categoria categoria){
        if(categoria != null && this.categorias.contains(categoria)) return true;
      
        return false;
    }

    /**
     * Obtiene el número de categorías administradas por el gestor.
     * @return número de categorías.
     */
    public int NCategorias(){
        return this.categorias.size();
    }

    /**
     * Obtiene las categorías administradas por el gestor
     * @return lista de categorías
     */
    public ArrayList<Categoria> getCategorias() {
        return this.categorias;
    }

    @Override
    public String toString() {
        String g = super.toString() +"\tCategorias:";
        if(this.categorias != null){
            for(Categoria cat:this.categorias) g += cat.getId() + " ";
        }
        
        return g;
    }


}
