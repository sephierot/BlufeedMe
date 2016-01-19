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
package blufeedme.basedatos.impl;

import blufeedme.modelo.Categoria;
import blufeedme.modelo.Dispositivo;
import blufeedme.modelo.Gestor;
import blufeedme.modelo.Noticia;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */

public class BdBlufeedmeMySql implements blufeedme.basedatos.BdBlufeedme{

    private Connection con;
    private String URL_BD;
    private String user;
    private String password;

    public static final String TASOCIACION = "asociacion";
    public static final String TCATEGORIA = "categoria";
    public static final String TDISPOSITIVO = "dispositivo";
    public static final String TGESTOR = "gestor";
    public static final String THISTORIAL = "historial";
    public static final String TNOTICIA = "noticia";
    
    /**
     * Constructor
     */
    public BdBlufeedmeMySql() {
        con = null;
        URL_BD = "";
        user = "";
        password = "";
    }

    /**
     * Constructor
     * @param URL_BD dirección url de la base de datos
     * @param user usuario para conectar con la base de datos
     * @param password contraseña de usuario
     */
    public BdBlufeedmeMySql(String URL_BD, String user, String password) {
        if (URL_BD != null) {
            this.URL_BD = URL_BD;
        } else {
            this.URL_BD = "";
        }

        if (user != null) {
            this.user = user;
        } else {
            this.user = "";
        }

        if (password != null) {
            this.password = password;
        } else {
            this.password = "";
        }
    }
    
    /**
     * Establece la conexión con la base de datos
     * @param f fichero con parametros de conexión
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean connect(File f){
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        Properties cfg = new Properties();
        try {
            cfg.load(fis);
        } catch (IOException ex) {
            Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        this.URL_BD = cfg.getProperty("URL");
        this.user = cfg.getProperty("user");
        this.password = cfg.getProperty("password");

        return connect();
    }

    /**
     * Establece la conexión con la base de datos
     * @param URL_BD Dirección url de la base de datos
     * @param user usuario para conectar con la base de datos
     * @param password contraseña para conectar con la base de datos
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     */
    @Override
    public boolean connect(String URL_BD, String user, String password) {
        if (URL_BD != null) {
            this.URL_BD = URL_BD;
        } else {
            this.URL_BD = "";
        }

        if (user != null) {
            this.user = user;
        } else {
            this.user = "";
        }

        if (password != null) {
            this.password = password;
        } else {
            this.password = "";
        }

        return this.connect();
    }

    /**
     * Establece la conexión con la base de datos usando los atributos URL_BD, user y password
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     */
    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try {
                con = (Connection) DriverManager.getConnection(this.URL_BD, this.user, this.password);
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(con != null){
                try{
                    //Deshabilitamos el autoguardado para establecer nosotros las transacciones
                    con.setAutoCommit(false);
                    //Indicamos el tipo de las transacciones
                    con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    
                    return true;
                }catch (SQLException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
            else return false;

        } catch (java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException:");
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Finaliza la conexión con la base de datos
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean disconnect() {
        if (this.con == null) {
            return false;
        }

        try {
            this.con.commit();
            this.con.close();
        } catch (SQLException ex) {
            while (ex != null) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                    ex = ex.getNextException();
            }

            return false;
        }
        return true;
    }

    /**
     * Inserta un gestor en la base de datos
     * @param g gestor
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean insert(Gestor g) {
         if (con == null || g == null) {
            System.err.println("Insert(Gestor): Gestor nulo");
            return false;
        }

        PreparedStatement pst;
        ResultSet v_autoinc;
        String lcategorias = "";

        //Comprobamos que todas las categorias que posee el gestor existen
        if (g.NCategorias() > 0) {
            lcategorias = "Array[";
            for (Categoria c : g.getCategorias()) {
                lcategorias = lcategorias + c.getId() + ",";
            }

            //Quitamos la ultima coma sobrante y la sustituimos por el corchete
            lcategorias = lcategorias.substring(0, lcategorias.length() - 1) + "]";

            //Array() crea un array y despues este en vector columna
            String consulta = "SELECT UNNEST(" + lcategorias + ") EXCEPT SELECT id_categoria FROM " + TCATEGORIA;

            try {
                pst = this.con.prepareStatement(consulta);
                v_autoinc = pst.executeQuery();

                //Si hay filas significa que se quieren insertar categorias que no existen (Error)
                if (v_autoinc.next()) {
                    //cerramos la conexion
                    v_autoinc.close();
                    pst.close();
                    System.err.println("Insert(gestor): No existe alguna de las categorias del gestor");

                    return false;
                }

                v_autoinc.close();
                pst.close();
            } catch (SQLException ex) {
                while (ex != null) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                    ex = ex.getNextException();
                }

                return false;
            }
        }

        try{
            pst =  this.con.prepareStatement("INSERT INTO " + TGESTOR + " (nombre, contrasenia) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            //Nombre
            if(g.getNombre().isEmpty()) pst.setNull(1, java.sql.Types.VARCHAR);
            else pst.setString(1, g.getNombre());

            //Contrasenia
            if(g.getContrasenia().isEmpty()) pst.setNull(2, java.sql.Types.VARCHAR);
            else pst.setString(2, g.getContrasenia());

            //Se ejecuta la insercion
            int n_gestor = pst.executeUpdate();

            //Obtenemos el id del elemento insertado
            v_autoinc = pst.getGeneratedKeys();

            //Asignamos al objeto g el id actual en la base de datos
            if (v_autoinc.next()) {
                g.setId(v_autoinc.getLong(1));
            }

            v_autoinc.close();
            pst.close();

            //Si el gestor nose ha insertado no tenemos que hacer nada mas
            if(n_gestor == 0) return false;

            //ACTUALIZAMOS LAS CATEGORIAS EN LA BASE DE DATOS PARA INDICAR SU NUEVO GESTOR (actualizar nuevas)
            //Si no posee categorias hemos terminado
            if (g.NCategorias() == 0) {
                con.commit();
                return true;
            }

            pst = this.con.prepareStatement("UPDATE " + TCATEGORIA + " SET id_gestor=? WHERE id_categoria IN (SELECT UNNEST(" + lcategorias + "))");
            pst.setLong(1, g.getId());

            //Se ejecuta la actualizacion
            int n_cat = pst.executeUpdate();

            if (n_cat == g.NCategorias()) {
                //Guardamos ya que se han realizado ambas operaciones
                con.commit();
                return true;
            } else {
                //Deshacemos los cambios en la actualización del gestor
                con.rollback();
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());

                ex = ex.getNextException();
            }

            try {
                //Deshacemos los cambios en la actualización del gestor
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Elimina un gestor de la base de datos
     * @param g gestor a eliminar
     * @return TRUE en caso de que se elimine con éxito, FALSE en caso contrario
     */
    @Override
    public boolean delete(Gestor g) {
        if (con == null || g == null) {
            return false;
        }

        String consulta = "DELETE FROM " + TGESTOR +" WHERE id_gestor=?";
        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement(consulta);
            //Id_gestor
            pst.setLong(1, g.getId());
            int n = pst.executeUpdate();
            //Guardamos ya que se han realizado las operaciones
            con.commit();

            //cerramos la consulta
            pst.close();

            if (n > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los cambios en la actualización del gestor
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Actualiza la información de un gestor de la base de datos
     * @param g gestor con la nueva información
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean update(Gestor g) {
        if (con == null || g == null) {
            return false;
        }

        PreparedStatement pst;
        ResultSet v_autoinc;
        String lcategorias = "";

        //Comprobamos que todas las categorias que posee el gestor existen
        if (g.NCategorias() > 0) {
            lcategorias = "Array[";
            for (Categoria c : g.getCategorias()) {
                lcategorias = lcategorias + c.getId() + ",";
            }

            //Quitamos la ultima coma sobrante y la sustituimos por el corchete
            lcategorias = lcategorias.substring(0, lcategorias.length() - 1) + "]";

            //Array() crea un array y despues este en vector columna
            String consulta = "SELECT UNNEST(" + lcategorias + ") EXCEPT SELECT id_categoria FROM " + TCATEGORIA;

            try {
                pst = this.con.prepareStatement(consulta);
                v_autoinc = pst.executeQuery();

                //Si hay filas significa que se quieren insertar categorias que no existen (Error)
                if (v_autoinc.next()) {
                    //cerramos la conexion
                    v_autoinc.close();
                    pst.close();
                    return false;
                }

                v_autoinc.close();
                pst.close();
            } catch (SQLException ex) {
                while (ex != null) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                    ex = ex.getNextException();
                }

                return false;
            }
        }

        try{
            pst = this.con.prepareStatement("UPDATE "+TGESTOR+" SET nombre=?, contrasenia=? WHERE id_gestor=?");
            //Nombre
            if(g.getNombre().isEmpty()) pst.setNull(1, java.sql.Types.VARCHAR);
            else pst.setString(1, g.getNombre());

            //Contrasenia
            if(g.getContrasenia().isEmpty()) pst.setNull(2, java.sql.Types.VARCHAR);
            else pst.setString(2, g.getContrasenia());

            pst.setLong(3, g.getId());

            //Se ejecuta la actualizacion
            int n_gestor = pst.executeUpdate();
            pst.close();

            //Si el gestor no existe no tenemos que actualizar nada mas
            if(n_gestor == 0) return false;

            //ACTUALIZAMOS LAS CATEGORIAS EN LA BASE DE DATOS PARA INDICAR SU NUEVO GESTOR (eliminar antiguas y actualizar nuevas)
            pst = this.con.prepareStatement("UPDATE " + TCATEGORIA + " SET id_gestor=? WHERE id_gestor=?");
            pst.setNull(1, java.sql.Types.BIGINT);
            pst.setLong(2, g.getId());
            pst.executeUpdate();
            pst.close();

            //Si no posee categorias hemos terminado
            if (g.NCategorias() == 0) {
                con.commit();
                return true;
            }

            pst = this.con.prepareStatement("UPDATE " + TCATEGORIA + " SET id_gestor=? WHERE id_categoria IN (SELECT UNNEST(" + lcategorias + "))");
            pst.setLong(1, g.getId());

            //Se ejecuta la actualizacion
            int n_cat = pst.executeUpdate();

            if (n_cat == g.NCategorias()) {
                //Guardamos ya que se han realizado ambas operaciones
                con.commit();
                return true;
            } else {
                //Deshacemos los cambios en la actualización del gestor
                con.rollback();
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los cambios en la actualización del gestor
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Obtiene un gestor de la base de datos
     * @param idGestor identificador del gestor
     * @return el objeto gestor en caso de éxito, null en caso de no existir o de un error
     */
    @Override
    public Gestor getGestor(Long idGestor) {
        if (con == null || idGestor < 1) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TGESTOR + " WHERE id_gestor=?");
            //Id_gestor
            pst.setLong(1, idGestor);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Gestor g = new Gestor(rs.getLong("id_gestor"), rs.getString("nombre"), rs.getString("contrasenia"), null);

            rs.close();
            pst.close();

            //OBTENEMOS LAS CATEGORIAS DE LA BASE DE DATOS PARA El GESTOR ENCONTRADO
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_gestor=?");
            pst.setLong(1, idGestor);
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                g.aniadeCategoria(new Categoria(rs.getLong("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor")));
            }

            rs.close();
            pst.close();
            con.commit();
            return g;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            
            return null;
        }
    }

    /**
     * Obtiene un gestor de la base de datos
     * @param nombre nombre del gestor
     * @return el objeto gestor en caso de éxito, null en caso de no existir o de un error
     */
    @Override
    public Gestor getGestor(String nombre) {
        if (con == null || nombre.isEmpty()) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TGESTOR + " WHERE nombre=?");
            //nombre gestor
            pst.setString(1, nombre);

            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Gestor g = new Gestor(rs.getLong("id_gestor"), rs.getString("nombre"), rs.getString("contrasenia"), null);

            rs.close();
            pst.close();

            //OBTENEMOS LAS CATEGORIAS DE LA BASE DE DATOS PARA El GESTOR ENCONTRADO
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_gestor=?");
            pst.setLong(1, g.getId());
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                g.aniadeCategoria(new Categoria(rs.getLong("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor")));
            }

            rs.close();
            pst.close();
            con.commit();
            return g;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Comprueba si existe un gestor con nombre y pass
     * @param nombre nombre del gestor
     * @param pass contraseña del gestor
     * @return el objeto gestor en caso de existir, null en caso contrario o de error
     */
    @Override
    public Gestor checkGestor(String nombre, String pass) {
        if (con == null || nombre == null || pass == null){
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TGESTOR + " WHERE nombre=? AND contrasenia =?");
            //nombre gestor
            pst.setString(1, nombre);
            //pass gestor
            pst.setString(2, pass);

            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Gestor g = new Gestor(rs.getLong("id_gestor"), rs.getString("nombre"), rs.getString("contrasenia"), null);

            rs.close();
            pst.close();

            //OBTENEMOS LAS CATEGORIAS DE LA BASE DE DATOS PARA El GESTOR ENCONTRADO
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_gestor=?");
            pst.setLong(1,g.getId());
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                g.aniadeCategoria(new Categoria(rs.getLong("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor")));
            }

            rs.close();
            pst.close();
            con.commit();
            return g;

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Inserta una categoría en la base de datos
     * @param c categoría
     * @return TRUE en caso de que la insercción tenga éxito, FALSE en caso contrario.
     */
    @Override
    public boolean insert(Categoria c) {
        if (con == null || c == null) {
            return false;
        }

        PreparedStatement pst;
        ResultSet v_autoinc;

        try {
            pst = this.con.prepareStatement("INSERT INTO " + TCATEGORIA + " (nombre, descripcion, id_gestor) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            //Nombre
            if (c.getNombre().isEmpty()) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pst.setString(1, c.getNombre());
            }

            //Descripcion
            if (c.getDescripcion().isEmpty()) {
                pst.setNull(2, java.sql.Types.VARCHAR);
            } else {
                pst.setString(2, c.getDescripcion());
            }

            //Id_gestor
            if (c.getIdGestor() == Categoria.ID_NULL) {
                pst.setNull(3, java.sql.Types.BIGINT);
            } else {
                pst.setLong(3, c.getIdGestor());
            }

            //Se ejecuta la insercion
            int n = pst.executeUpdate();

            //Obtenemos el id del elemento insertado
            v_autoinc = pst.getGeneratedKeys();

            //Asignamos al objeto c el id actual en la base de datos
            if (v_autoinc.next()) {
                c.setId(v_autoinc.getLong(1));
            }

            //cerramos el resulset y la consulta
            v_autoinc.close();
            pst.close();
            con.commit();

            if (n > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Elimina una categoría de la base de datos
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean delete(Categoria c) {
       if (con == null || c == null) {
            return false;
        }

        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("DELETE FROM " + TCATEGORIA + " WHERE id_categoria=?");
            //Id_categoria
            pst.setLong(1, c.getId());
            //Se ejecuta el borrado
            int n = pst.executeUpdate();

            //cerramos la consulta
            pst.close();
            //Guardamos los datos
            con.commit();

            if (n > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Actualiza la información de una categoría en la base de datos
     * @param c categoría con la información actualizada
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean update(Categoria c) {
        if (con == null || c == null) {
            return false;
        }

        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("UPDATE " + TCATEGORIA + " SET nombre=?, descripcion=?, id_gestor=? WHERE id_categoria=?");
            //Nombre
            if (!c.getNombre().isEmpty()) {
                pst.setString(1, c.getNombre());
            } else {
                pst.setNull(1, java.sql.Types.VARCHAR);
            }

            //Descripcion
            if (!c.getDescripcion().isEmpty()) {
                pst.setString(2, c.getDescripcion());
            } else {
                pst.setNull(2, java.sql.Types.VARCHAR);
            }

            //Id_gestor
            if (c.getIdGestor() == Categoria.ID_NULL) {
                pst.setNull(3, java.sql.Types.BIGINT);
            } else {
                pst.setLong(3, c.getIdGestor());
            }

            //Id_categoria
            pst.setLong(4, c.getId());

            //Se ejecuta la actualizacion
            int n = pst.executeUpdate();
            //Guardamos ya que se han realizado todas las operaciones
            con.commit();
            //cerramos la consulta
            pst.close();

            if (n > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Obtiene una categoría de la base de datos
     * @param idCategoria identificador de la categoría a obtener
     * @return objeto Categoría con identificador idCategoría, null en caso de no existir o de error.
     */
    @Override
    public Categoria getCategoria(Long idCategoria) {
        if (idCategoria < 1) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_categoria=?");
            //Id_gestor
            pst.setLong(1, idCategoria);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Categoria c = new Categoria(idCategoria, rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor"));

            rs.close();
            pst.close();
            con.commit();

            return c;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Obtiene una categoría de la base de datos
     * @param nombre nombre de la categoría a obtener
     * @return objeto Categoría con identificador idCategoría, null en caso de no existir o de error.
     */
    @Override
    public Categoria getCategoria(String nombre) {
        if (nombre.length() < 1) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE nombre=?");
            //nombre gestor
            pst.setString(1, nombre);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Categoria c = new Categoria(rs.getLong("id_categoria"), nombre, rs.getString("descripcion"), rs.getLong("id_gestor"));

            rs.close();
            pst.close();
            con.commit();

            return c;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Inserta una noticia en la base de datos
     * @param n noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean insert(Noticia n) {
        if (con == null || n == null) {
            return false;
        }

        PreparedStatement pst;
        ResultSet v_autoinc;
        String p;
        try {
            pst = this.con.prepareStatement("INSERT INTO " + TNOTICIA + " (titulo, subtitulo, autor, texto, fecha, fecha_publi, fecha_caducidad, id_categoria, firma) VALUES (?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            //Titulo
            if (n.getTitulo().isEmpty()) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pst.setString(1, n.getTitulo());
            }

            //Subtitulo
            if (n.getSubtitulo().isEmpty()) {
                pst.setNull(2, java.sql.Types.VARCHAR);
            } else {
                pst.setString(2, n.getSubtitulo());
            }

            //Autor
            if (n.getAutor().isEmpty()) {
                pst.setNull(3, java.sql.Types.VARCHAR);
            } else {
                pst.setString(3, n.getAutor());
            }

            //Texto
            if (n.getTexto().isEmpty()) {
                pst.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pst.setString(4, n.getTexto());
            }

            //Fecha
            pst.setDate(5, new java.sql.Date(n.getFecha().getTimeInMillis()));

            //Fecha publi
            pst.setDate(6, new java.sql.Date(n.getFechaPubli().getTimeInMillis()));

            //Fecha caducidad
            if(n.getFechaCaducidad() != null){
                pst.setDate(7, new java.sql.Date(n.getFechaCaducidad().getTimeInMillis()));
            }else{
                pst.setNull(7, java.sql.Types.DATE);
            }

            //Id_categoria
            if (n.getIdCategoria() == Noticia.ID_NULL) {
                pst.setNull(8, java.sql.Types.BIGINT);
            } else {
                pst.setLong(8, n.getIdCategoria());
            }

            if (n.getFirma().isEmpty()){
                pst.setNull(9, java.sql.Types.VARCHAR);
            }else{
                pst.setString(9, n.getFirma());
            }
            //Se ejecuta la insercion
            int num = pst.executeUpdate();

            //Obtenemos el id del elemento insertado
            v_autoinc = pst.getGeneratedKeys();

            //Asignamos al objeto c el id actual en la base de datos
            if (v_autoinc.next()) {
                n.setId(v_autoinc.getLong(1));
            }

            //cerramos el resulset y la consulta
            v_autoinc.close();
            pst.close();
            con.commit();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                p = ex.getMessage();
                System.out.println(ex.getMessage());
                //Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Elimina una noticia de la base de datos
     * @param n noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean delete(Noticia n) {
        if (con == null || n == null) {
            return false;
        }

        String consulta = "DELETE FROM " + TNOTICIA + " WHERE id_noticia=?";
        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement(consulta);
            //Id_noticia
            pst.setLong(1, n.getId());
            int num = pst.executeUpdate();

            //Guardamos ya que se han realizado ambas operaciones
            con.commit();

            //cerramos la consulta
            pst.close();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Elimina una noticia de la base de datos
     * @param idNoticia identificador de la noticia
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean deleteNoticia(Long idNoticia){
        if (con == null || idNoticia == Noticia.ID_NULL) {
            return false;
        }

        String consulta = "DELETE FROM " + TNOTICIA + " WHERE id_noticia=?";
        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement(consulta);
            //Id_noticia
            pst.setLong(1, idNoticia);
            int num = pst.executeUpdate();

            //Guardamos ya que se han realizado ambas operaciones
            con.commit();

            //cerramos la consulta
            pst.close();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Actualiza una noticia de la base de datos
     * @param n noticia con la información actualizada
     * @return TRUE en caso de que la actualización tenga éxito, FALSE en caso contrario
     */
    @Override
    public boolean update(Noticia n) {
        if (con == null || n == null) {
            return false;
        }

        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("UPDATE " + TNOTICIA + " SET titulo=?, subtitulo=?, autor=?, texto=?, fecha=?, fecha_publi=?, fecha_caducidad=?, id_categoria=?, firma=? WHERE id_noticia=?");

            //Titulo
            if (n.getTitulo().isEmpty()) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pst.setString(1, n.getTitulo());
            }

            //Subtitulo
            if (n.getSubtitulo().isEmpty()) {
                pst.setNull(2, java.sql.Types.VARCHAR);
            } else {
                pst.setString(2, n.getSubtitulo());
            }

            //Autor
            if (n.getAutor().isEmpty()) {
                pst.setNull(3, java.sql.Types.VARCHAR);
            } else {
                pst.setString(3, n.getAutor());
            }

            //Texto
            if (n.getTexto().isEmpty()) {
                pst.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pst.setString(4, n.getTexto());
            }

            //Fecha
            pst.setDate(5, new java.sql.Date(n.getFecha().getTimeInMillis()));

            //Fecha_publi
            pst.setDate(6, new java.sql.Date(n.getFechaPubli().getTimeInMillis()));

            //Fecha caducidad
            if(n.getFechaCaducidad() == null)
                pst.setNull(7, java.sql.Types.DATE);
            else
                pst.setDate(7, new java.sql.Date(n.getFechaCaducidad().getTimeInMillis()));

            //Id_categoria
            if (n.getIdCategoria() == Noticia.ID_NULL) {
                pst.setNull(8, java.sql.Types.BIGINT);
            } else {
                pst.setLong(8, n.getIdCategoria());
            }

            //firma
            if (n.getFirma().isEmpty()){
                pst.setNull(9, java.sql.Types.VARCHAR);
            } else {
                pst.setString(9, n.getFirma());
            }
            //Id_noticia
            pst.setLong(10, n.getId());

            //Se ejecuta la actualizacion
            int num = pst.executeUpdate();
            //Guardamos ya que se han realizado todas las operaciones
            con.commit();
            //cerramos la consulta
            pst.close();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Obtiene una noticia de la base de datos
     * @param idNoticia identificador de la noticia
     * @return objeto noticia en caso de existir, null en caso contrario o de error
     */
    @Override
    public Noticia getNoticia(Long idNoticia) {
        if (idNoticia < 1) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TNOTICIA + " WHERE id_noticia=?");
            //Id_gestor
            pst.setLong(1, idNoticia);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            Calendar cal3 = Calendar.getInstance();

            cal.setTime(rs.getDate("fecha"));
            cal2.setTime(rs.getDate("fecha_publi"));
            cal3.setTime(rs.getDate("fecha_caducidad"));

            Noticia n = new Noticia(rs.getLong("id_noticia"), rs.getString("titulo"), rs.getString("subtitulo"), rs.getString("autor"), rs.getString("texto"), cal, cal2 , cal3 ,rs.getLong("id_categoria"), rs.getString("firma"));

            rs.close();
            pst.close();
            con.commit();

            return n;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Obtiene las noticias pertenecientes a una categoría
     * @param idCategoria identificador de la categoría
     * @return lista de noticias de la categoría, null en caso de error
     */
    @Override
    public ArrayList<Noticia> getNoticias(Long idCategoria){
        if(con == null || idCategoria < 1){
            return null;
        }
        ArrayList<Noticia> lnoticias = new ArrayList<Noticia>();

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TNOTICIA + " WHERE id_categoria=?");

            pst.setLong(1, idCategoria);
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                Calendar cal3 = Calendar.getInstance();

                cal.setTime(rs.getDate("fecha"));
                cal2.setTime(rs.getDate("fecha_publi"));
                cal3.setTime(rs.getDate("fecha_caducidad"));

                lnoticias.add(new Noticia(rs.getLong("id_noticia"),
                        rs.getString("titulo"),
                        rs.getString("subtitulo"),
                        rs.getString("autor"),
                        rs.getString("texto"),
                        cal,
                        cal2,
                        cal3,
                        idCategoria,
                        rs.getString("firma")));
            }

            return lnoticias;

        }catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            return null;
        }
    }

    /**
     * Obtiene todas las noticias de la base de datos
     * @return lista de noticias de la base de datos, null en caso de error
     */
    @Override
    public ArrayList<Noticia> getNoticias(){
        if(con == null){
            return null;
        }
        ArrayList<Noticia> lnoticias = new ArrayList<Noticia>();

        PreparedStatement pst;
        ResultSet rs;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TNOTICIA);

            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                Calendar cal3 = Calendar.getInstance();

                cal.setTime(rs.getDate("fecha"));
                cal.setTime(rs.getDate("fecha_publi"));
                cal.setTime(rs.getDate("fecha_caducidad"));

                lnoticias.add(new Noticia(rs.getLong("id_noticia"),
                        rs.getString("titulo"),
                        rs.getString("subtitulo"),
                        rs.getString("autor"),
                        rs.getString("texto"),
                        cal,
                        cal2,
                        cal3,
                        rs.getLong("id_categoria"),
                        rs.getString("firma")));
            }

            return lnoticias;

        }catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            return null;
        }
    }

    /**
     * Obtiene las noticias de las categorías a las que esta vinculado un dispositivo
     * @param MAC dirección MAC del dispositivo
     * @return lista de noticias, null en caso de error
     */
    @Override
    public ArrayList<Noticia> getNoticias(String MAC){
        if(con == null || MAC.isEmpty()){
            return null;
        }
        ArrayList<Noticia> ln = new ArrayList<Noticia>();

        PreparedStatement pst;
        ResultSet rs;

        try{
            pst = this.con.prepareStatement("SELECT id_noticia,titulo,subtitulo,autor,texto,fecha,n.id_categoria,firma "
                    + "FROM " + TDISPOSITIVO + " d," + TCATEGORIA + " c," + TNOTICIA + " n "
                    + "WHERE d.mac = ? "
                    + "AND c.id_categoria = d.id_categoria "
                    + "AND n.id_categoria = c.id_categoria");

            pst.setString(1, MAC);

            rs = pst.executeQuery();

            while(rs.next()){
                Calendar cal = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                Calendar cal3 = Calendar.getInstance();

                cal.setTime(rs.getDate("fecha"));
                cal2.setTime(rs.getDate("fecha_publi"));
                cal3.setTime(rs.getDate("fecha_caducidad"));

                ln.add(new Noticia(rs.getLong("id_noticia"),
                        rs.getString("titulo"),
                        rs.getString("subtitulo"),
                        rs.getString("autor"),
                        rs.getString("texto"),
                        cal,
                        cal2,
                        cal3,
                        rs.getLong("id_categoria"),
                        rs.getString("firma")));
            }
            pst.close();
            rs.close();
            return ln;

        }catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            return null;
        }

    }

    /**
     * Obtiene las noticias no enviadas aún a un dispositivo
     * @param MAC dirección MAC del dispositivo
     * @param n número de noticias a obtener
     * @return lista de noticias, null en caso de error
     */
    @Override
    public ArrayList<Noticia> getNoticiasNoEnviadas(String MAC, Integer n){
        if(con == null || MAC.isEmpty() || n<1){
            return null;
        }
        else{
            ArrayList<Noticia> ln = new ArrayList<Noticia>();

            PreparedStatement pst;
            ResultSet rs;
            try{

                pst = this.con.prepareStatement("SELECT n.id_noticia, n.titulo, n.subtitulo, n.autor, n.texto, n.fecha, n.fecha_publi, n.fecha_caducidad, n.id_categoria, n.firma " +
                        "FROM " + TASOCIACION + " a, " + TNOTICIA + " n ," + TDISPOSITIVO + " d " +
                        "WHERE a.id_categoria = n.id_categoria " +
                        "AND a.id_dispositivo = d.id_dispositivo " +
                        "AND d.mac = ? " +
                        "AND fecha_publi <= NOW() " +
                        "AND fecha_caducidad >= NOW() " +
                        "AND id_noticia NOT IN (SELECT id_noticia FROM " + THISTORIAL + " WHERE id_dispositivo = d.id_dispositivo) " +
                        "ORDER BY fecha_publi " +
                        "LIMIT ?");

                pst.setString(1, MAC);
                pst.setInt(2, n);

                rs = pst.executeQuery();

                while(rs.next()){
                    Calendar cal = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    Calendar cal3 = Calendar.getInstance();

                    cal.setTime(rs.getDate("fecha"));
                    cal2.setTime(rs.getDate("fecha_publi"));
                    cal3.setTime(rs.getDate("fecha_caducidad"));

                    ln.add(new Noticia(rs.getLong("id_noticia"),
                            rs.getString("titulo"),
                            rs.getString("subtitulo"),
                            rs.getString("autor"),
                            rs.getString("texto"),
                            cal,
                            cal2,
                            cal3,
                            rs.getLong("id_categoria"),
                            rs.getString("firma")));
                }
                pst.close();
                rs.close();
                return ln;

            }catch (SQLException ex) {
                while (ex != null) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                    ex = ex.getNextException();
                }
                return null;
            }
        }
    }

    /**
     * Inserta un dispositivo en la base de datos
     * @param d dispositivo
     * @return TRUE en caso de que la insercción tenga éxito, FALSE en caso contrario
     */
    @Override
    public boolean insert(Dispositivo d){
        if (con == null || d == null) {
            return false;
        }

        PreparedStatement pst = null;
        ResultSet v_autoinc = null;
        int n = 0;

        try {
            pst = this.con.prepareStatement("INSERT INTO " + TDISPOSITIVO + " (mac, pin, url) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            //mac
            if (d.getMac() == null) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                if(d.getMac().isEmpty()) pst.setNull(1, java.sql.Types.VARCHAR);
                else pst.setString(1, d.getMac());
            }

            //pin
            if (d.getPin()== null) {
                pst.setNull(2, java.sql.Types.VARCHAR);
            } else {
                if(d.getPin().isEmpty()) pst.setNull(2, java.sql.Types.VARCHAR);
                else pst.setString(2, d.getPin());
            }

            //url
            if(d.getURL_servicio()== null)
                pst.setNull(3, java.sql.Types.VARCHAR);
            else{
                if(d.getURL_servicio().isEmpty()) pst.setNull(3, java.sql.Types.VARCHAR);
                else pst.setString(3, d.getURL_servicio());
            }
            //Insertamos en la tabla dispositivos el dispositivo.
            n = pst.executeUpdate();

            //Obtenemos el id del elemento insertado
            v_autoinc = pst.getGeneratedKeys();

            //Asignamos al objeto d el id actual en la base de datos
            if (v_autoinc.next()) {
                d.setId(v_autoinc.getLong(1));
            }

            con.commit();
            
            String consulta = "INSERT INTO " + TASOCIACION + " (id_dispositivo, id_categoria) VALUES (?,?)";
            //Insertamos las asociaciones
            if (!d.getCategorias().isEmpty()) {

                for (Categoria c : d.getCategorias()) {
                    pst = con.prepareStatement(consulta);
                    pst.setLong(1, d.getId());
                    pst.setLong(2, c.getId());
                    pst.executeUpdate();

                }
            }
            
            con.commit();
            
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        finally{
            try {
                if(pst != null)pst.close();
                if(v_autoinc != null) v_autoinc.close();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }
            if(n>0)
                return true;
            else
                return false;
        }
    }

    /**
     * Elimina un dispositivo en la base de datos
     * @param d dispositivo a eliminar
     * @return TRUE en caso de éxito, FALSE en caso de error
     */
    @Override
    public boolean delete(Dispositivo d){
        if (con == null || d == null) {
            return false;
        }

        PreparedStatement pst = null;
        int num = 0;
        try {
            pst = this.con.prepareStatement("DELETE FROM " + TDISPOSITIVO + " WHERE id_dispositivo=?");
            //id_dispositivo
            pst.setLong(1, d.getId());
            //Se ejecuta el borrado
            num = pst.executeUpdate();

            
            //Guardamos los datos
            con.commit();

            
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());

            }

            return false;
        } finally{
            //cerramos la consulta
            if(pst != null){
                try {
                    pst.close();
                } catch (SQLException ex) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (num > 0) {
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * Elimina un dispositivo de la base de datos
     * @param idDispositivo identificador del dispositivo
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean deleteDispositivo(Long idDispositivo){
        if (con == null || idDispositivo <=0) {
            return false;
        }

        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("DELETE FROM " + TDISPOSITIVO + " WHERE id_dispositivo=?");
            //id_dispositivo
            pst.setLong(1, idDispositivo);
            //Se ejecuta el borrado
            int num = pst.executeUpdate();

            //cerramos la consulta
            pst.close();
            //Guardamos los datos
            con.commit();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Elimina un dispositivo de la base de datos
     * @param mac dirección MAC del dispositivo
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean deleteDispositivo(String mac) {
        if (con == null || mac==null) {
            return false;
        }

        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("DELETE FROM " + TDISPOSITIVO + " WHERE mac=?");
            //id_dispositivo
            pst.setString(1, mac);
            //Se ejecuta el borrado
            int num = pst.executeUpdate();

            //cerramos la consulta
            pst.close();
            //Guardamos los datos
            con.commit();

            if (num > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    /**
     * Actualiza un dispositivo de la base de datos
     * @param d dispositivo con la información actualizada
     * @return TRUE en caso de que la actualización se lleve a cabo, FALSE en caso contrario
     */
    @Override
    public boolean update(Dispositivo d){
        if (con == null || d == null) {
            return false;
        }

        PreparedStatement pst = null;
        int n = -1;
        try {
            pst = this.con.prepareStatement("UPDATE " + TDISPOSITIVO + " SET mac=?, pin=?, url=? WHERE id_dispositivo=?");
            //mac
            if(d.getMac()!= null){
                if (!d.getMac().isEmpty()) {
                    pst.setString(1, d.getMac());
                } else {
                    pst.setNull(1, java.sql.Types.VARCHAR);
                }
            }
            else
                pst.setNull(1, java.sql.Types.VARCHAR);

            //pin
            if(d.getPin()!= null){
                if (!d.getPin().isEmpty()) {
                    pst.setString(2, d.getPin());
                } else {
                    pst.setNull(2, java.sql.Types.VARCHAR);
                }
            }
            else
                pst.setNull(2, java.sql.Types.VARCHAR);

            //url
            if(d.getURL_servicio()!= null){
                if(!d.getURL_servicio().isEmpty()){
                    pst.setString(3, d.getURL_servicio());
                }
                else{
                    pst.setNull(3, java.sql.Types.VARCHAR);
                }
            }
            else
                pst.setNull(3,java.sql.Types.VARCHAR);

            //id_dispositivo
            pst.setLong(4, d.getId());

            //Se ejecuta la actualizacion
            n = pst.executeUpdate();


        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }
            n = -1;
            
        } finally{
            try {
                //Guardamos ya que se han realizado todas las operaciones
                con.commit();
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex1);
                }
                return false;
            }
            //cerramos la consulta
            if(pst != null) try {
                pst.close();
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            if (n > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Actualiza la dirección del servicio del dispositivo
     * @param MAC dirección MAC del dispositivo
     * @param url_servicio dirección del servicio
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean updateURL(String MAC, String url_servicio){
        if (MAC.isEmpty()) {
            return false;
        }
        PreparedStatement pst;

        try {
            pst = this.con.prepareStatement("UPDATE " + TDISPOSITIVO + " SET url=? WHERE mac=?");

            //url
            if (!url_servicio.isEmpty()) {
                pst.setString(1, url_servicio);
            } else {
                pst.setNull(1, java.sql.Types.VARCHAR);
            }

            //mac
            pst.setString(2, MAC);

            //Se ejecuta la actualizacion
            int n = pst.executeUpdate();

            //Guardamos ya que se han realizado todas las operaciones
            con.commit();

            //cerramos la consulta
            pst.close();

            if (n > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    /**
     * Obtiene un dispositivo de la base de datos
     * @param idDispositivo identificador del dispositivo
     * @return dispositivo en caso de existir, null en caso contrario o de error
     */
    @Override
    public Dispositivo getDispositivo(Long idDispositivo){
        if (con == null || idDispositivo < 1) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;
        Dispositivo d = null;
        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TDISPOSITIVO + " WHERE id_dispositivo=?");
            //Id_dispositivo
            pst.setLong(1, idDispositivo);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            d = new Dispositivo(rs.getLong("id_dispositivo"), rs.getString("mac"),
                    rs.getString("pin"), new ArrayList<Categoria>(),rs.getString("url"));

            //Obtener las categorias asociadas al dispositivo
            pst = this.con.prepareStatement("SELECT * FROM " + TASOCIACION + " a," + TCATEGORIA + " c " +
                    "WHERE id_dispositivo = ? AND " +
                    "a.id_categoria = c.id_categoria");

            pst.setLong(1, idDispositivo);

            //Obtenemos las categorias
            rs = pst.executeQuery();

            while (rs.next()) {
                d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                        rs.getString("nombre"),rs.getString("descripcion"),
                        rs.getLong("id_gestor")));
            }


            rs.close();
            pst.close();
            con.commit();

            return d;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Obtiene un dispositivo de la base de datos
     * @param mac dirección MAC del dispositivo
     * @return objeto dispositivo en caso de existir, null en caso contrario o de error
     */
    @Override
    public Dispositivo getDispositivo(String mac) {
        if (con == null || mac.isEmpty()) {
            return null;
        }

        PreparedStatement pst;
        ResultSet rs;
        Dispositivo d = null;
        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TDISPOSITIVO + " WHERE mac=?");
            //Id_dispositivo
            pst.setString(1, mac);
            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if (!rs.next()) {
                rs.close();
                pst.close();
                return null;
            }

            Long id_dispositivo = rs.getLong("id_dispositivo");
            d = new Dispositivo(id_dispositivo, rs.getString("mac"),
                    rs.getString("pin"), new ArrayList<Categoria>() ,rs.getString("url"));
            //Obtener las categorias asociadas al dispositivo

            pst = this.con.prepareStatement("SELECT * FROM " + TASOCIACION + " a," + TCATEGORIA + " c " +
                    "WHERE a.id_dispositivo = ? AND " +
                    "a.id_categoria = c.id_categoria");

            pst.setLong(1, id_dispositivo);
            //Obtenemos las categorias
            rs = pst.executeQuery();

            while (rs.next()) {
                d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                        rs.getString("nombre"),rs.getString("descripcion"),
                        rs.getLong("id_gestor")));
            }


            rs.close();
            pst.close();
            con.commit();

            return d;
        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Obtiene los dispositivos existentes en la BD
     * @return lista con los dispositivos de la BD
     */
    @Override
    public ArrayList<Dispositivo> getDispositivos(){
        if(con == null)
            return null;

        ArrayList<Dispositivo> ld = new ArrayList<Dispositivo>();

        PreparedStatement pst = null;
        ResultSet rs = null, rs2 = null;

        try {
            pst = this.con.prepareStatement("SELECT * FROM " + TDISPOSITIVO);
            //ejecutamos la consulta
            rs = pst.executeQuery();

            while(rs.next()){
                //Añadimos el dispositivo al array
                Dispositivo d = new Dispositivo();
                d.setId(rs.getLong("id_dispositivo"));
                d.setMac(rs.getString("mac"));
                d.setPin(rs.getString("pin"));
                d.setURL_servicio("url");
                d.setCategorias(new ArrayList<Categoria>());
                
                //Obtenemos las categorias asociadas
                pst = this.con.prepareStatement("SELECT c.id_categoria, c.nombre, c.descripcion, c.id_gestor FROM "
                                                + TASOCIACION + " a, " + TCATEGORIA + " c " + 
                                                "WHERE a.id_categoria = c.id_categoria " +
                                                "AND a.id_dispositivo = ?");
                pst.setLong(1, d.getId());
                
                //Ejecutamos la consulta
                rs2 = pst.executeQuery();
                //Recorremos las categorias asociadas y las añadimos al dispositivo
                while(rs2.next()){
                    d.addCategoria(new Categoria(rs2.getLong("id_categoria"),rs2.getString("nombre"),rs2.getString("descripcion"),rs2.getLong("id_gestor")));
                }

                con.commit();
                ld.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
            ld = null;
        }
        finally{
            if(pst != null) try {
                pst.close();
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            if(rs != null) try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }

            if(rs2 != null)
                try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            
            return ld;
        }
    }

    /**
     * Obtiene la lista de dispositivos de la base de datos asociados a categorias gestionadas por un determinado gestor.
     * @param g gestor de las categorias a obtener dispositivos
     * @return lista de dispositivos o null en caso de error
     */
    @Override
    public ArrayList<Dispositivo> getDispositivos(Gestor g){
        if(con == null || g == null)
            return null;
        
        ArrayList<Dispositivo> ld = new ArrayList<Dispositivo>();

        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = this.con.prepareStatement("SELECT d.id_dispositivo, d.mac, d.pin, d.url, c.id_categoria , c.nombre, c.descripcion, c.id_gestor " +
                    "FROM " + TDISPOSITIVO + " d," + TASOCIACION + " a, " + TCATEGORIA + " c " +
                    "WHERE d.id_dispositivo= a.id_dispositivo " +
                    "AND a.id_categoria = c.id_categoria " +
                    "AND c.id_gestor = ? " +
                    "ORDER BY d.mac");

            //Seleccionamos solo las categorias del gestor pasado como parametro
            pst.setLong(1, g.getId());

            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if(rs.next()){
                //Añadimos el primer elemento al array
                Dispositivo d = new Dispositivo();
                d.setId(rs.getLong("id_dispositivo"));
                d.setMac(rs.getString("mac"));
                d.setPin(rs.getString("pin"));
                d.setURL_servicio("url");
                d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                                             rs.getString("nombre"),
                                             rs.getString("descripcion"),
                                             rs.getLong("id_gestor")));

                ld.add(d);
                while (rs.next()) {
                    //Si se trata del mismo dispositivo le añadimos la categoria
                    if(rs.getString("mac").equals(ld.get(ld.size()-1).getMac())){
                        ld.get(ld.size()-1).addCategoria(new Categoria(rs.getLong("id_categoria"),
                                                                       rs.getString("nombre"),
                                                                       rs.getString("descripcion"),
                                                                       rs.getLong("id_gestor")));
                    }
                    //Si no es el mismo añadimos el nuevo dispositivo a la lista
                    else{
                        d = new Dispositivo();
                        d.setId(rs.getLong("id_dispositivo"));
                        d.setMac(rs.getString("mac"));
                        d.setPin(rs.getString("pin"));
                        d.setURL_servicio("url");
                        d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                                                     rs.getString("nombre"),
                                                     rs.getString("descripcion"),
                                                     rs.getLong("id_gestor")));

                        ld.add(d);
                    }
                }

            }
        } catch (SQLException ex) {
              while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            ld = null;
        }finally{
            try {
                if (pst != null) {
                    pst.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.commit();
                return ld;
            } catch (SQLException ex) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    con.commit();
                } catch (SQLException ex1) {
                    Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex1);
                }
                return null;
            }
        }
    }

    /**
     * Obtiene la lista de dispositivos de la base de datos asociados a una categoria.
     * @param c categoria para la que se obtendran los dispositivos
     * @return lista de dispositivos o null en caso de error
     */
    @Override
    public ArrayList<Dispositivo> getDispositivos(Categoria c){
        ArrayList<Dispositivo> ld = new ArrayList<Dispositivo>();
   /*     if(con == null || c == null)
            return null;

        PreparedStatement pst;
        ResultSet rs;
        try {
            pst = this.con.prepareStatement("SELECT d.id_dispositivo, d.mac, d.pin, d.url, c.id_categoria , c.nombre, c.descripcion, c.id_gestor " +
                    "FROM " + TDISPOSITIVO + " d," + TASOCIACION + " a, " + TCATEGORIA + " c " +
                    "WHERE d.id_dispositivo= a.id_dispositivo " +
                    "AND a.id_categoria = c.id_categoria " +
                    "AND c.id_categoria = ? " +
                    "ORDER BY d.mac");

            //Seleccionamos solo las categorias del gestor pasado como parametro
            pst.setLong(1, c.getId());

            //Se ejecuta la seleccion
            rs = pst.executeQuery();

            if(rs.next()){
                //Añadimos el primer elemento al array
                Dispositivo d = new Dispositivo();
                d.setId(rs.getLong("id_dispositivo"));
                d.setMac(rs.getString("mac"));
                d.setPin(rs.getString("pin"));
                d.setURL_servicio("url");
                d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                                             rs.getString("nombre"),
                                             rs.getString("descripcion"),
                                             rs.getLong("id_gestor")));

                ld.add(d);
                while (rs.next()) {
                    //Si se trata del mismo dispositivo le añadimos la categoria
                    if(rs.getString("mac").equals(ld.get(ld.size()-1).getMac())){
                        ld.get(ld.size()-1).addCategoria(new Categoria(rs.getLong("id_categoria"),
                                                                       rs.getString("nombre"),
                                                                       rs.getString("descripcion"),
                                                                       rs.getLong("id_gestor")));
                    }
                    //Si no es el mismo añadimos el nuevo dispositivo a la lista
                    else{
                        d = new Dispositivo();
                        d.setId(rs.getLong("id_dispositivo"));
                        d.setMac(rs.getString("mac"));
                        d.setPin(rs.getString("pin"));
                        d.setURL_servicio("url");
                        d.addCategoria(new Categoria(rs.getLong("id_categoria"),
                                                     rs.getString("nombre"),
                                                     rs.getString("descripcion"),
                                                     rs.getLong("id_gestor")));

                        ld.add(d);
                    }
                }
                pst.close();
                rs.close();
                return ld;
            }
        } catch (SQLException ex) {
              while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
*/
        return ld;

    }


    /**
     * Asocia un dispositivo a una categoría
     * @param d dispositivo
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean asociar(Dispositivo d, Categoria c) {
        if(con == null || d == null || c == null ){
            return false;
        }

        PreparedStatement pst;

        try{
            pst = this.con.prepareStatement("INSERT INTO " + TASOCIACION + " (id_dispositivo,id_categoria) VALUES (?,?)");
            pst.setLong(1, d.getId());
            pst.setLong(2, c.getId());

            //Se ejecuta la actualizacion
            int n = pst.executeUpdate();

            //cerramos la consulta
            pst.close();

            if (n > 0) {
                //Guardamos ya que se han realizado todas las operaciones
                con.commit();
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            
            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    /**
     * Desasocia un dispositivo de una categoría
     * @param d dispositivo
     * @param c categoría
     * @return TRUE en caso de éxito, FALSE en caso contrario
     */
    @Override
    public boolean desasociar(Dispositivo d, Categoria c) {
        if(con == null || d == null || c == null ){
            return false;
        }

        PreparedStatement pst;

        try{
            pst = this.con.prepareStatement("DELETE FROM " + TASOCIACION +
                    " WHERE id_dispositivo = ? " +
                    " AND " +
                    "id_categoria = ?");
            
            pst.setLong(1, d.getId());
            pst.setLong(2, c.getId());

            //Se ejecuta la actualizacion
            int n = pst.executeUpdate();

            //Guardamos ya que se han realizado todas las operaciones
            con.commit();

            //cerramos la consulta
            pst.close();

            if (n > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }
            try {
                //Deshacemos los posibles cambios en la bd
                con.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    /**
     * Obtiene las categorías administradas por un gestor
     * @param idGestor identificador del gestor
     * @return lista de categorías, null en caso de error
     */
    @Override
    public ArrayList<Categoria> getCategoriasXGestor(Long idGestor) {
        if (con == null || idGestor < 1) {
            return null;
        }

        ArrayList<Categoria> lcategorias = new ArrayList<Categoria>();

        PreparedStatement pst;
        ResultSet rs;

        try {
            //OBTENEMOS LAS CATEGORIAS DE LA BASE DE DATOS PARA El GESTOR ENCONTRADO
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_gestor=?");
            pst.setLong(1, idGestor);
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                lcategorias.add(new Categoria(rs.getLong("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor")));
            }

            pst.close();
            rs.close();
            con.commit();

            if (lcategorias.isEmpty()) {
                return null;
            }
            return lcategorias;

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Obtiene las categorías con las que esta vinculado un dispositivo
     * @param mac dirección MAC del dispositivo
     * @return lista de categorías, null en caso de error
     */
    @Override
    public ArrayList<Categoria> getCategoriasXDispositivo(String mac) {
        if (con == null || mac.isEmpty()) {
            return null;
        }

        ArrayList<Categoria> lcategorias = new ArrayList<Categoria>();

        PreparedStatement pst;
        ResultSet rs;

        try {
            //OBTENEMOS LAS CATEGORIAS DE LA BASE DE DATOS PARA El GESTOR ENCONTRADO
            pst = this.con.prepareStatement("SELECT * FROM " + TCATEGORIA + " WHERE id_categoria IN "
                    + "(SELECT id_categoria FROM " + TASOCIACION + " WHERE id_dispositivo=(SELECT id_dispositivo FROM " + TDISPOSITIVO + " WHERE mac=?))");
            pst.setString(1, mac);
            //Se ejecuta la consulta
            rs = pst.executeQuery();

            while (rs.next()) {
                lcategorias.add(new Categoria(rs.getLong("id_categoria"), rs.getString("nombre"), rs.getString("descripcion"), rs.getLong("id_gestor")));
            }

            pst.close();
            rs.close();
            con.commit();

            if (lcategorias.isEmpty()) {
                return null;
            }
            return lcategorias;

        } catch (SQLException ex) {
            while (ex != null) {
                Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                ex = ex.getNextException();
            }

            return null;
        }
    }

    /**
     * Registra en la base de datos el envio de una noticia a un dispositivo
     * @param MAC dirección MAC del dispositivo al que ha sido enviada la noticia
     * @param idNoticia identificador de la noticia a confirmar envio
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     */
    @Override
    public boolean ConfirmarEnvioNoticia(String MAC, Long idNoticia){
        if(this.con == null || MAC.isEmpty() || idNoticia < 1){
            return false;
        }
        else{
            Dispositivo d = this.getDispositivo(MAC);
            
            if(d!=null){
                PreparedStatement pst;

                try{
                    pst = this.con.prepareStatement("INSERT INTO " + THISTORIAL + " (id_dispositivo,id_noticia) VALUES (?,?)");
                
                    pst.setLong(1, d.getId());
                    pst.setLong(2, idNoticia);

                    //Se ejecuta la actualizacion
                    int n = pst.executeUpdate();

                    //cerramos la consulta
                    pst.close();

                    if (n > 0) {
                        //Guardamos ya que se han realizado todas las operaciones
                        con.commit();
                        return true;
                    } else {
                        return false;
                    }

                } catch (SQLException ex) {
                    while (ex != null) {
                        Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex.getMessage());
                        ex = ex.getNextException();
                    }
            
                    try {
                    //Deshacemos los posibles cambios en la bd
                        con.rollback();
                    } catch (SQLException ex1) {
                        Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, ex1.getMessage());
                    }
                    return false;
                }
            }
            
            return false;
        }
    }

}
