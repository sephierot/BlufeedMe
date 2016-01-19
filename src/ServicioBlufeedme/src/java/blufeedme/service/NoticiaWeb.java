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

package blufeedme.service;

import java.util.Calendar;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */

public class NoticiaWeb {
    private String titulo;
    private String subtitulo;
    private String autor;
    private String texto;
    private Calendar fechaPubli;
    private Calendar fechaCaducidad;
    private String categoria;

    /**
     * Constructor.
     */
    public NoticiaWeb() {
        this.titulo = "";
        this.subtitulo = "";
        this.autor = "";
        this.texto = "";
        this.fechaPubli = Calendar.getInstance();
        this.fechaCaducidad = null;
        this.categoria = "";
    }
    
    /**
     * Constructor
     * @param titulo titulo de la noticia
     * @param subtitulo subtitulo de la noticia
     * @param autor autor de la noticia
     * @param texto texto de la noticia
     * @param fechaPubli fecha de publicación de la noticia
     * @param fechaCaducidad fecha en la que la noticia dejará de estar publicada
     * @param categoria nombre de la categoría a la que pertenece la noticia.
     */
    public NoticiaWeb(String titulo, String subtitulo, String autor, String texto, Calendar fechaPubli, Calendar fechaCaducidad, String categoria) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.autor = autor;
        this.texto = texto;
        this.fechaPubli = fechaPubli;
        this.fechaCaducidad = fechaCaducidad;
        this.categoria = categoria;
    }

    /**
     * Asigna un autor a la noticia
     * @param autor autor a asignar.
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Asigna una categoría a la noticia
     * @param categoria nombre de la categoría a asignar a la noticia.
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Asigna una fecha de caducidad a la noticia.
     * @param fechaCaducidad fecha de caducidad a asignar.
     */
    public void setFechaCaducidad(Calendar fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Asigna una fecha de publicación a la noticia.
     * @param fechaPubli fecha de publicacion a asignar.
     */
    public void setFechaPubli(Calendar fechaPubli) {
        this.fechaPubli = fechaPubli;
    }

    /**
     * Asigna un subtitulo a la noticia
     * @param subtitulo subtitulo a asignar.
     */
    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    /**
     * Asigna un texto a la noticia.
     * @param texto texto a asignar.
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * Asigna un titulo a la noticia
     * @param titulo titulo a asignar.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el autor de la noticia.
     * @return autor de la noticia.
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Obtiene el nombre de la categoría a la que pertenece la noticia
     * @return nombre de la categoría.
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Obtiene la fecha de caducidad de la noticia.
     * @return fecha de caducidad, null en caso de no tener fecha de caducidad asignada.
     */
    public Calendar getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     * Obtiene la fecha de publicación de la noticia.
     * @return fecha de publicación, null en caso de no tener fecha de publicación asignada.
     */
    public Calendar getFechaPubli() {
        return fechaPubli;
    }

    /**
     * Obtiene el subtitulo de la noticia.
     * @return subtitulo de la noticia.
     */
    public String getSubtitulo() {
        return subtitulo;
    }

    /**
     * Obtiene el texto de la noticia.
     * @return texto de la noticia.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Obtiene el título de la noticia.
     * @return título de la noticia.
     */
    public String getTitulo() {
        return titulo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoticiaWeb other = (NoticiaWeb) obj;
        if ((this.titulo == null) ? (other.titulo != null) : !this.titulo.equals(other.titulo)) {
            return false;
        }
        if ((this.subtitulo == null) ? (other.subtitulo != null) : !this.subtitulo.equals(other.subtitulo)) {
            return false;
        }
        if ((this.autor == null) ? (other.autor != null) : !this.autor.equals(other.autor)) {
            return false;
        }
        if ((this.texto == null) ? (other.texto != null) : !this.texto.equals(other.texto)) {
            return false;
        }
        if (this.fechaPubli != other.fechaPubli && (this.fechaPubli == null || !this.fechaPubli.equals(other.fechaPubli))) {
            return false;
        }
        if (this.fechaCaducidad != other.fechaCaducidad && (this.fechaCaducidad == null || !this.fechaCaducidad.equals(other.fechaCaducidad))) {
            return false;
        }
        if ((this.categoria == null) ? (other.categoria != null) : !this.categoria.equals(other.categoria)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.titulo != null ? this.titulo.hashCode() : 0);
        hash = 29 * hash + (this.subtitulo != null ? this.subtitulo.hashCode() : 0);
        hash = 29 * hash + (this.autor != null ? this.autor.hashCode() : 0);
        hash = 29 * hash + (this.texto != null ? this.texto.hashCode() : 0);
        hash = 29 * hash + (this.fechaPubli != null ? this.fechaPubli.hashCode() : 0);
        hash = 29 * hash + (this.fechaCaducidad != null ? this.fechaCaducidad.hashCode() : 0);
        hash = 29 * hash + (this.categoria != null ? this.categoria.hashCode() : 0);
        return hash;
    }

    

}
