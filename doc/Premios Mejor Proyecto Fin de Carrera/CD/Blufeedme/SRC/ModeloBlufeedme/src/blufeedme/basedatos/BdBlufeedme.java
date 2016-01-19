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

package blufeedme.basedatos;

import blufeedme.modelo.*;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 */
public interface BdBlufeedme {

    /**
     * Establece la conexión con la base de datos
     * @param f fichero con parametros de conexión
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean connect(File f);

    /**
     * Establece la conexión con la base de datos
     * @param URL_BD Dirección url de la base de datos
     * @param user usuario para conectar con la base de datos
     * @param password contraseña para conectar con la base de datos
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     */
    public boolean connect(String URL_BD, String user, String password);
    
    /**
     * Finaliza la conexión con la base de datos
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean disconnect();

    /**
     * Inserta un gestor en la base de datos
     * @param g gestor
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean insert(Gestor g);

    /**
     * Elimina un gestor de la base de datos
     * @param g gestor a eliminar
     * @return TRUE en caso de que se elimine con éxito, FALSE en caso contrario
     */
    public boolean delete(Gestor g);

    /**
     * Actualiza la información de un gestor de la base de datos
     * @param g gestor con la nueva información
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean update(Gestor g);

    /**
     * Obtiene un gestor de la base de datos
     * @param idGestor identificador del gestor
     * @return el objeto gestor en caso de éxito, null en caso de no existir o de un error
     */
    public Gestor getGestor(Long idGestor);

    /**
     * Obtiene un gestor de la base de datos
     * @param nombre nombre del gestor
     * @return el objeto gestor en caso de éxito, null en caso de no existir o de un error
     */
    public Gestor getGestor(String nombre);

    /**
     * Comprueba si existe un gestor con nombre y pass
     * @param nombre nombre del gestor
     * @param pass contraseña del gestor
     * @return el objeto gestor en caso de existir, null en caso contrario o de error
     */
    public Gestor checkGestor(String nombre, String pass);
/*
    public boolean insert(AdminPantalla a);
    public boolean delete(AdminPantalla a);
    public boolean update(AdminPantalla a);
    public AdminPantalla getAdminPatalla(Long idAdmin);
    public AdminPantalla getAdminPantalla(String nombre);
    public AdminPantalla checkAdminPantalla(String nombre, String pass);
*/

    /**
     * Inserta una categoría en la base de datos
     * @param c categoría
     * @return TRUE en caso de que la insercción tenga éxito, FALSE en caso contrario.
     */
    public boolean insert(Categoria c);

    /**
     * Elimina una categoría de la base de datos
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean delete(Categoria c);

    /**
     * Actualiza la información de una categoría en la base de datos
     * @param c categoría con la información actualizada
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean update(Categoria c);

    /**
     * Obtiene una categoría de la base de datos
     * @param idCategoria identificador de la categoría a obtener
     * @return objeto Categoría con identificador idCategoría, null en caso de no existir o de error.
     */
    public Categoria getCategoria(Long idCategoria);

    /**
     * Obtiene una categoría de la base de datos
     * @param nombre nombre de la categoría a obtener
     * @return objeto Categoría con identificador idCategoría, null en caso de no existir o de error.
     */
    public Categoria getCategoria(String nombre);

    /**
     * Inserta una noticia en la base de datos
     * @param n noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean insert(Noticia n);

    /**
     * Elimina una noticia de la base de datos
     * @param n noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean delete(Noticia n);

    /**
     * Elimina una noticia de la base de datos
     * @param idNoticia identificador de la noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean deleteNoticia(Long idNoticia);

    /**
     * Actualiza una noticia de la base de datos
     * @param n noticia con la información actualizada
     * @return TRUE en caso de que la actualización tenga éxito, FALSE en caso contrario
     */
    public boolean update(Noticia n);

    /**
     * Obtiene una noticia de la base de datos
     * @param idNoticia identificador de la noticia
     * @return objeto noticia en caso de existir, null en caso contrario o de error
     */
    public Noticia getNoticia(Long idNoticia);

    /**
     * Obtiene las noticias pertenecientes a una categoría
     * @param idCategoria identificador de la categoría
     * @return lista de noticias de la categoría, null en caso de error
     */
    public ArrayList<Noticia> getNoticias(Long idCategoria);

    /**
     * Obtiene todas las noticias de la base de datos
     * @return lista de noticias de la base de datos, null en caso de error
     */
    public ArrayList<Noticia> getNoticias();

    /**
     * Obtiene las noticias de las categorías a las que esta vinculado un dispositivo
     * @param MAC dirección MAC del dispositivo
     * @return lista de noticias, null en caso de error
     */
    public ArrayList<Noticia> getNoticias(String MAC);

    /**
     * Obtiene las noticias no enviadas aún a un dispositivo
     * @param MAC dirección MAC del dispositivo
     * @param n número de noticias a obtener
     * @return lista de noticias, null en caso de error
     */
    public ArrayList<Noticia> getNoticiasNoEnviadas(String MAC, Integer n);

/*    public boolean publicar(Long idNoticia, Long idAdmin);
    public boolean despublicar(Long idNoticia, Long idAdmin);
    public boolean updateAdminPublicacion(Long idNoticia, Long newAdmin);
    public ArrayList<Noticia> getNoticiasPublicadas();
*/

    /**
     * Inserta un dispositivo en la base de datos
     * @param d dispositivo
     * @return TRUE en caso de que la insercción tenga éxito, FALSE en caso contrario
     */
    public boolean insert(Dispositivo d);

    /**
     * Elimina un dispositivo en la base de datos
     * @param d dispositivo a eliminar
     * @return TRUE en caso de éxito, FALSE en caso de error
     */
    public boolean delete(Dispositivo d);

    /**
     * Elimina un dispositivo de la base de datos
     * @param idDispositivo identificador del dispositivo
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean deleteDispositivo(Long idDispositivo);

    /**
     * Elimina un dispositivo de la base de datos
     * @param mac dirección MAC del dispositivo
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean deleteDispositivo(String mac);

    /**
     * Actualiza un dispositivo de la base de datos
     * @param d dispositivo con la información actualizada
     * @return TRUE en caso de que la actualización se lleve a cabo, FALSE en caso contrario
     */
    public boolean update(Dispositivo d);

    /**
     * Actualiza la dirección del servicio del dispositivo
     * @param MAC dirección MAC del dispositivo
     * @param url_servicio dirección del servicio
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean updateURL(String MAC, String url_servicio);

    /**
     * Obtiene un dispositivo de la base de datos
     * @param idDispositivo identificador del dispositivo
     * @return dispositivo en caso de existir, null en caso contrario o de error
     */
    public Dispositivo getDispositivo(Long idDispositivo);

    /**
     * Obtiene un dispositivo de la base de datos
     * @param mac dirección MAC del dispositivo
     * @return objeto dispositivo en caso de existir, null en caso contrario o de error
     */
    public Dispositivo getDispositivo(String mac);

    /**
     * Obtiene la lista de dispositivos de la base de datos
     * @return lista de dispositivos o null en caso de error
     */
    public ArrayList<Dispositivo> getDispositivos();

    /**
     * Obtiene la lista de dispositivos de la base de datos asociados a categorias gestionadas por un determinado gestor.
     * @param g gestor de las categorias a obtener dispositivos
     * @return lista de dispositivos o null en caso de error
     */
    public ArrayList<Dispositivo> getDispositivos(Gestor g);

    /**
     * Obtiene la lista de dispositivos de la base de datos asociados a una categoria.
     * @param c categoria para la que se obtendran los dispositivos
     * @return lista de dispositivos o null en caso de error
     */
    public ArrayList<Dispositivo> getDispositivos(Categoria c);

    /**
     * Asocia un dispositivo a una categoría
     * @param d dispositivo
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean asociar(Dispositivo d, Categoria c);

    /**
     * Desasocia un dispositivo de una categoría
     * @param d dispositivo
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    public boolean desasociar(Dispositivo d, Categoria c);

    /**
     * Obtiene las categorías administradas por un gestor
     * @param idGestor identificador del gestor
     * @return lista de categorías, null en caso de error
     */
    public ArrayList<Categoria> getCategoriasXGestor(Long idGestor);

    /**
     * Obtiene las categorías con las que esta vinculado un dispositivo
     * @param mac dirección MAC del dispositivo
     * @return lista de categorías, null en caso de error
     */
    public ArrayList<Categoria> getCategoriasXDispositivo(String mac);

    /**
     * Registra en la base de datos el envio de una noticia a un dispositivo
     * @param MAC dirección MAC del dispositivo al que ha sido enviada la noticia
     * @param idNoticia identificador de la noticia a confirmar envio
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     */
    public boolean ConfirmarEnvioNoticia(String MAC,Long idNoticia);
}
