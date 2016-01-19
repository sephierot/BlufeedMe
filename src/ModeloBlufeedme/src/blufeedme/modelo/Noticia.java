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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class Noticia {

    private Long id;
    private String titulo;
    private String subtitulo;
    private String autor;
    private String texto;
    private Calendar fecha;
    private Calendar fechaPubli;
    private Calendar fechaCaducidad;
    private Long idCategoria;
    private String firma;


    public static final long ID_NULL = 0;

    /**
     * Constructor
     */
    public Noticia() {
        this.id = ID_NULL;
        this.titulo = "";
        this.subtitulo = "";
        this.autor = "";
        this.texto = "";
        this.fecha = Calendar.getInstance();
        this.fechaPubli = Calendar.getInstance();
        this.fechaCaducidad = null;
        this.idCategoria = ID_NULL;
        this.firma = "";
    }

    /**
     * Constructor
     * @param id identificador de la noticia
     * @param titulo titulo de la noticia
     * @param subtitulo subtitulo de la noticia
     * @param autor autor de la noticia
     * @param texto texto de la noticia
     * @param fecha fecha de creación de la noticia
     * @param fechaP fecha de publicación de la noticia
     * @param fechaC fecha de caducidad de la noticia
     * @param idCategoria identificador de la categoría a la que pertenece la noticia
     * @param firma firma de creación de la noticia
     */
    public Noticia(Long id, String titulo, String subtitulo, String autor, String texto, Calendar fecha, Calendar fechaP, Calendar fechaC, Long idCategoria, String firma) {
        if(id > 0) this.id = id;
        else this.id = ID_NULL;
        
        if(titulo != null) this.titulo = titulo;
        else this.titulo = "";

        if(subtitulo != null) this.subtitulo = subtitulo;
        else this.subtitulo = "";

        if(autor != null) this.autor = autor;
        else this.autor = "";

        if(texto != null) this.texto = texto;
        else this.texto = "";

        if(fecha != null) this.fecha = fecha;
        else this.fecha = Calendar.getInstance(); //Fecha actual

        if(fechaP != null) this.fechaPubli = fechaP;
        else this.fechaPubli = Calendar.getInstance(); //Fecha actual

        if(fechaC != null) this.fechaCaducidad = fechaC;
        else this.fechaCaducidad = null;

        if(firma != null) this.firma = firma;
        else this.firma = "";

        if(idCategoria > 0) this.idCategoria = idCategoria;
        else this.idCategoria = ID_NULL;
    }

    /**
     * Asigna un autor a la noticia
     * @param autor autor
     */
    public void setAutor(String autor) {
        if(autor != null) this.autor = autor;
    }

    /**
     * Asigna una fecha de creación a la noticia
     * @param fecha fecha de creación
     */
    public void setFecha(Calendar fecha) {
        if(fecha != null) this.fecha = fecha;
    }

    /**
     * Asigna una fehca de caducidad a la noticia tras la cual dejará de aparecer publicada
     * @param fechaCaducidad fecha de caducidad
     */
    public void setFechaCaducidad(Calendar fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    /**
     * Asigna una fecha a partir de la cual la noticia será publicada
     * @param fechaPubli fecha de publicación
     */
    public void setFechaPubli(Calendar fechaPubli) {
        this.fechaPubli = fechaPubli;
    }

    /**
     * Asigna un identificador a la noticia
     * @param id identificador
     */
    public void setId(Long id) {
        if(id > 0 || id == ID_NULL) this.id = id;
    }

    /**
     * Asigna un identificador de categoría a la que pertenece la noticia
     * @param idCategoria identificador de categoría
     */
    public void setIdCategoria(Long idCategoria) {
        if(idCategoria > 0 || idCategoria == ID_NULL) this.idCategoria = idCategoria;
    }

    /**
     * Asigna un subtitulo a la noticia
     * @param subtitulo subtitulo
     */
    public void setSubtitulo(String subtitulo) {
        if(subtitulo != null) this.subtitulo = subtitulo;
    }

    /**
     * Asigna un texto (cuerpo) a la noticia
     * @param texto texto
     */
    public void setTexto(String texto) {
        if(texto != null) this.texto = texto;
    }

    /**
     * Asigna un titulo a la noticia
     * @param titulo titulo
     */
    public void setTitulo(String titulo) {
        if(titulo != null) this.titulo = titulo;
    }

    /**
     * Asigna una firma a la noticia que se usa como mecanismo de seguridad
     * @param firma firma
     */
    public void setFirma(String firma) {
        this.firma = firma;
    }

    /**
     * Obtiene el autor de la noticia
     * @return autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Obtiene la fecha de creación de la noticia
     * @return fecha de creación
     */
    public Calendar getFecha() {
        return fecha;
    }

    /**
     * Obtiene la fecha de caducidad de la noticia
     * @return fecha de caducidad
     */
    public Calendar getFechaCaducidad() {
        return fechaCaducidad;
    }

    /**
     * Obtiene la fecha de publicación de la noticia
     * @return fecha de publicación
     */
    public Calendar getFechaPubli() {
        return fechaPubli;
    }

    /**
     * Obtiene el identificador de la noticia
     * @return identificador
     */
    public Long getId() {
        return id;
    }

    /**
     * Obtiene la categoría a la que pertenece la noticia
     * @return categoría
     */
    public Long getIdCategoria() {
        return idCategoria;
    }

    /**
     * Obtienen el subtitulo de la noticia
     * @return
     */
    public String getSubtitulo() {
        return subtitulo;
    }

    /**
     * Obtiene el texto de la noticia
     * @return texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Obtiene el título de la noticia
     * @return título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtiene la firma de la noticia
     * @return firma
     */
    public String getFirma() {
        return firma;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Noticia other = (Noticia) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.firma == null) ? (other.firma != null) : !this.firma.equals(other.firma)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 17 * hash + (this.firma != null ? this.firma.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        String n = "Id:"+this.id+"\tTitulo:"+this.titulo+"\tSubtitulo:"+this.subtitulo+"\tAutor:"+this.autor+"\tFecha:";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        n += sdf.format(this.fecha.getTime()) + "\tFecha publicación:" + sdf.format(this.fechaPubli.getTime()) +
                        "\t Fecha caducidad: " + sdf.format(this.fechaCaducidad.getTime()) +
                    "\tCategoria:"+this.idCategoria+"\nTexto:"+this.texto;

        return n;
    }
}
