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
package blufeedmebt.eventos;

import blufeedme.modelo.Dispositivo;
import blufeedme.modelo.Noticia;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */
public class EventoDispositivo extends EventObject {

    private Dispositivo dispositivo;
    private String nombreDispositivo;
    private int tipoEvento;
    private ArrayList<Noticia> noticias;
    private int noticias_enviadas;
    public static final int NO_VALIDO = -1;
    public static final int NUEVA_BUSQUEDA = 0;
    public static final int DISPOSITIVO_REGISTRADO_ENCONTRADO = 1;
    public static final int DISPOSITIVO_ENCONTRADO = 2;
    public static final int SERVICIO_ENCONTRADO = 3;
    public static final int NOTICIAS_A_ENVIAR = 4;
    public static final int NOTICIAS_ENVIADAS = 5;

    public EventoDispositivo(Object source, Dispositivo dispositivo, String nombreDispositivo, int tipoEvento) {
        super(source);
        this.dispositivo = dispositivo;
        this.nombreDispositivo = nombreDispositivo;
        if (tipoEvento < 0 || tipoEvento > 5) {
            this.tipoEvento = this.NO_VALIDO;
        } else {
            this.tipoEvento = tipoEvento;
        }
        this.noticias = null;
        this.noticias_enviadas = 0;
    }

    public EventoDispositivo(Object source, Dispositivo dispositivo, String nombreDispositivo, int tipoEvento, ArrayList<Noticia> noticias) {
        super(source);
        this.dispositivo = dispositivo;
        this.nombreDispositivo = nombreDispositivo;

        if (tipoEvento == this.NOTICIAS_A_ENVIAR) {
            this.tipoEvento = this.NOTICIAS_A_ENVIAR;
            this.noticias = noticias;
        } else {
            this.tipoEvento = this.NO_VALIDO;
            this.noticias = null;
        }

        this.noticias_enviadas = 0;
    }

    public EventoDispositivo(Object source, Dispositivo dispositivo, String nombreDispositivo, int tipoEvento, ArrayList<Noticia> noticias, int noticias_enviadas) {
        super(source);
        this.dispositivo = dispositivo;
        this.nombreDispositivo = nombreDispositivo;

        if (tipoEvento == this.NOTICIAS_ENVIADAS && noticias_enviadas <= noticias.size()) {
            this.tipoEvento = this.NOTICIAS_ENVIADAS;
            this.noticias = noticias;
            this.noticias_enviadas = noticias_enviadas;
        }else {
            this.tipoEvento = this.NO_VALIDO;
            this.noticias = null;
            this.noticias_enviadas = 0;
        }
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public int getTipoEvento() {
        return tipoEvento;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public ArrayList<Noticia> getNoticias() {
        return noticias;
    }

    public int getNoticias_enviadas() {
        return noticias_enviadas;
    }
}

