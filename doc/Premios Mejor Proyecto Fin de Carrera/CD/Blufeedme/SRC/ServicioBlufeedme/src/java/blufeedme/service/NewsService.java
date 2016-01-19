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

import blufeedme.basedatos.impl.BdBlufeedmeMySql;
import blufeedme.modelo.Categoria;
import blufeedme.modelo.Dispositivo;
import blufeedme.service.excepcion.InvalidParamException;
import blufeedme.service.excepcion.InvalidUserException;
import blufeedme.service.util.MiSHA;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author David Armenteros Escabias, Ángel Daniel Sanjuán Espejo
 * davidarmesc@gmail.com, angdanni@gmail.com
 * http://blufeedme.wordpress.com/
 */

@WebService(name="NewsService")
public class NewsService {
    private static final String bdFile = "BDMySQL.prop";

    public static final int DATEERROR = 201;
    public static final int CATEGORYERROR = 202;
    public static final int NEWERROR = 203;
    public static final int DISPERROR = 204;
    public static final int LOGINERROR = 205;

    public static final Long CATEGORIA_PANTALLA = new Long(1);
    //blufeedme.modelo.basedatos.impl.BdBlufeedmePostgres bd = new blufeedme.modelo.basedatos.impl.BdBlufeedmePostgres("jdbc:postgresql://localhost/blufeedme_db", "root_blufeedme_db", "1234");
    
    blufeedme.basedatos.impl.BdBlufeedmeMySql bd;// = new blufeedme.basedatos.impl.BdBlufeedmeMySql("jdbc:mysql://localhost/blufeedme_db", "root_blufeedme", "1234");

    public NewsService(){
        InputStream is = NewsService.class.getResourceAsStream("/blufeedme/conf/" + bdFile);
        Properties cfg = new Properties();
        try {
            cfg.load(is);
        } catch (IOException ex) {
            Logger.getLogger(BdBlufeedmeMySql.class.getName()).log(Level.SEVERE, null, ex);
        }

        String urlBd = cfg.getProperty("URL");
        String user = cfg.getProperty("user");
        String password = cfg.getProperty("password");
        

        this.bd = new blufeedme.basedatos.impl.BdBlufeedmeMySql(urlBd, user, password);
    }
    /**
     * Añade una noticia al sistema
     * @param usuario usuario creador de la noticia
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario + contraseña + noticia.autor + noticia.categoria + noticia.titulo + noticia.texto + noticia.subtitulo + noticia.fechaPubli + noticia.fechaCaducidad
     *        donde noticia.categoria es el nombre de la categoria a la que pertenece, y las fechas siguen el formato "yyyy-MM-dd'T'HH:mm:ss".
     * @param noticia noticia a crear en el sistema
     * @return identificador de la noticia creada en caso de éxito, null en caso contrario
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "addNoticia")
    public Long addNoticia(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma, @WebParam(name = "noticia")
    NoticiaWeb noticia) throws InvalidUserException, InvalidParamException {
    //Conectamos con la bd
        if(bd.connect()){
            blufeedme.modelo.Noticia noticiaM = new blufeedme.modelo.Noticia();
            blufeedme.modelo.Categoria c = bd.getCategoria(noticia.getCategoria());

            if(c != null){
                blufeedme.modelo.Gestor g = bd.getGestor(c.getIdGestor());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                /*Comprobamos que la firma enviada es correcta*/
                String cadena = g.getNombre() + g.getContrasenia() +
                                noticia.getAutor() +
                                noticia.getCategoria() +
                                noticia.getTitulo() +
                                noticia.getTexto()+
                                noticia.getSubtitulo()+
                                sdf.format(noticia.getFechaPubli().getTime())+
                                sdf.format(noticia.getFechaCaducidad().getTime());

                String f = new String();
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*Si la firma es correcta introducimos la noticia*/
                if(f.equals(firma)){
                    //Comprobamos que la fecha de publicacion es menor que la fecha de caducidad
                    if(noticia.getFechaPubli().getTimeInMillis()<noticia.getFechaCaducidad().getTimeInMillis()){
                        noticiaM.setIdCategoria(c.getId());

                        //Inicializamos la noticia a insertar en la bd
                        noticiaM.setAutor(noticia.getAutor());
                        noticiaM.setTitulo(noticia.getTitulo());
                        noticiaM.setSubtitulo(noticia.getSubtitulo());
                        noticiaM.setTexto(noticia.getTexto());
                        noticiaM.setFechaPubli(noticia.getFechaPubli());
                        noticiaM.setFechaCaducidad(noticia.getFechaCaducidad());


                        noticiaM.setIdCategoria(c.getId());
                        noticiaM.setFirma(f);
                        if(bd.insert(noticiaM)){
                            bd.disconnect();
                            return noticiaM.getId();
                        }
                        else{
                            bd.disconnect();
                            return null;
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("La fecha de publicacion debe ser menor que la fecha de caducidad.", DATEERROR);
                    }

                }//Si el gestor no es correcto
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{ //Si el nombre de la categoria indicada no existe
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidParamException("Categoria " + noticia.getCategoria() + " no existe", CATEGORYERROR);
            }
        }
        else //Si no podemos conectar*/
            return null;
    }

    /**
     * Elimina una noticia del sistema
     * @param usuario usuario que desea eliminar la noticia y por tanto gestor de la categoría de la noticia a eliminar
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenación de
     *        usuario + contraseña + id_noticia + sello de tiempo.
     * @param id_noticia identificador de la noticia a eliminar
     * @return TRUE en caso de éxito, FALSE en caso contrario
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "deleteNoticia")
    public Boolean deleteNoticia(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma, @WebParam(name = "id_noticia")
    Long id_noticia) throws InvalidUserException, InvalidParamException {
    if(bd.connect()){
            blufeedme.modelo.Noticia noticia = bd.getNoticia(id_noticia);

            if(noticia != null){
                blufeedme.modelo.Categoria c = bd.getCategoria(noticia.getIdCategoria());
                blufeedme.modelo.Gestor g = bd.getGestor(c.getIdGestor());

                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH");

                Calendar now = Calendar.getInstance();

                /*Obtenemos la firma*/
                String cadena = g.getNombre() + g.getContrasenia() + id_noticia +
                            sdf2.format(now.getTime());

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }

                /*Comprobamos la firma*/
                if(f.equals(firma)){
                    Boolean aux = bd.deleteNoticia(id_noticia);
                    bd.disconnect();
                    return aux;
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidParamException("id_Noticia " + id_noticia + " no existe.", NEWERROR);
            }
        }
        else{
            return false;
        }
    }

    /**
     * Modifica la información relativa a una noticia del sistema
     * @param usuario usuario que desea modificar la noticia y por tanto gestor de la categoría de la noticia.
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario + contraseña + id_noticia + new_noticia.autor + new_noticia.categoria + new_noticia.titulo + new_noticia.texto + new_noticia.subtitulo + new_noticia.fechaPubli + new_noticia.fechaCaducidad + selloTiempo
     *        donde new_noticia.categoria es el nombre de la categoria a la que pertenecerá la nueva noticia, y las fechas siguen el formato "yyyy-MM-dd'T'HH:mm:ss". El sello consiste en una marca de tiempo en el que se envia la peticion al webservice.
     *        El formato debe ser "yyyy-MM-dd'T'HH".
     * @param id_noticia identificador de la noticia a modificar
     * @param new_noticia noticia con la nueva información actualizada.
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "updateNoticia")
    public Boolean updateNoticia(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma, @WebParam(name = "id_noticia")
    Long id_noticia, @WebParam(name = "new_noticia")
    NoticiaWeb new_noticia) throws InvalidUserException, InvalidParamException {
        if(bd.connect()){
            blufeedme.modelo.Noticia noticiaM = bd.getNoticia(id_noticia);

            //Comprobamos que exista la noticia
            if(noticiaM!=null){
                blufeedme.modelo.Categoria cAnt = bd.getCategoria(noticiaM.getIdCategoria());
                blufeedme.modelo.Categoria cNueva = new blufeedme.modelo.Categoria();
                
                //Si el usuario ha introducido categoria
                if(!new_noticia.getCategoria().isEmpty()){
                    cNueva = bd.getCategoria(new_noticia.getCategoria());
                    if(cNueva != null){
                        if(cNueva.getIdGestor()!=cAnt.getIdGestor()){
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("Las Categorias indicadas no son gestionadas por el mismo gestor", CATEGORYERROR);
                        }
                    }
                    else{//Si la categoria introducida no es correcta
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("Categoria " + new_noticia.getCategoria() + " de new_noticia incorrecta", NEWERROR);
                    }
                }
                else
                    cNueva = cAnt;

                blufeedme.modelo.Gestor g = bd.getGestor(cAnt.getIdGestor());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH");

                Calendar now = Calendar.getInstance();


                String cadena = g.getNombre() + g.getContrasenia() + id_noticia +
                            new_noticia.getAutor() + new_noticia.getCategoria() +
                            new_noticia.getTitulo() + new_noticia.getTexto() +
                            new_noticia.getSubtitulo() + sdf.format(new_noticia.getFechaPubli().getTime()) +
                            sdf.format(new_noticia.getFechaCaducidad().getTime())+
                            sdf2.format(now.getTime());


                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Es el gestor de la categoría por tanto puede modificar la noticia
                if(f.equals(firma)){
                    //autor
                    if(!new_noticia.getAutor().isEmpty())
                        noticiaM.setAutor(new_noticia.getAutor());

                    //Categoria
                    noticiaM.setIdCategoria(cNueva.getId());

                    noticiaM.setId(id_noticia);
                    //Titulo
                    if(!new_noticia.getTitulo().isEmpty())
                        noticiaM.setTitulo(new_noticia.getTitulo());

                    //Subtitulo
                    if(!new_noticia.getSubtitulo().isEmpty())
                        noticiaM.setSubtitulo(new_noticia.getSubtitulo());

                    //Texto
                    if(!new_noticia.getTexto().isEmpty())
                        noticiaM.setTexto(new_noticia.getTexto());

                    //fecha publicacion noticia
                    if(new_noticia.getFechaPubli() != null)
                        noticiaM.setFechaPubli(new_noticia.getFechaPubli());

                    //fecha caducidad noticia
                    if(new_noticia.getFechaCaducidad()!=null)
                        noticiaM.setFechaCaducidad(new_noticia.getFechaCaducidad());

                    noticiaM.setFirma(f);
                    //Comprobamos que la fecha de publicación sea menor que la fecha de caducidad
                    if(noticiaM.getFechaPubli() != null && noticiaM.getFechaCaducidad() != null){
                        if(noticiaM.getFechaPubli().getTimeInMillis()<noticiaM.getFechaCaducidad().getTimeInMillis()){
                            if(bd.update(noticiaM)){
                                bd.disconnect();
                                return true;
                            }
                            else{//Si no se ha podido modificar
                                bd.disconnect();
                                return false;
                            }
                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("La fecha de publicacion debe ser menor a la fecha de caducidad de la noticia.", DATEERROR);
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("Fecha de publicacion y fecha de caducidad no pueden ser igual a NULL", DATEERROR);
                    }
                }
                else{//si el gestor no es correcto
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }                
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidParamException("id_Noticia " + id_noticia.toString() + " no existe", NEWERROR);
            }
        }
        else //Si no conecta la bd
            return false;
    }

    /**
     * Obtiene las noticias correspondientes a la categoria/as que gestiona el usuario indicado.
     * @param usuario usuario del que se obtendran las noticias
     * @param categoria categoría de la que se desea obtener las noticias.
     * @param firma firma del mensaje. La firma consite en el sha1 de la cadena resultante al concatenar
     *        usuario + contraseña + categoria
     *        donde categoria es el nombre de la categoria de la que se desean obtener las noticias.
     * @return lista de noticias de la categoría indicada. En caso de que categoría sea null se devuelven todas las noticias de todas las categorías gestionadas por el usuario indicado. En caso de error devuelve null.
     * @throws InvalidUserException
     */
    @WebMethod(operationName = "getNoticias")
    public ArrayList<blufeedme.modelo.Noticia> getNoticias(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "categoria")
    String categoria, @WebParam(name = "firma")
    String firma) throws InvalidUserException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);

            if(g != null){
                //Obtenemos la firma
                String Cadena = g.getNombre() + g.getContrasenia() + categoria;

                String f = null;
                try {
                    f = MiSHA.SHA1(Cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Comprobamos la firma
                if(f.equals(firma)){
                    ArrayList<blufeedme.modelo.Noticia> resultado = new ArrayList<blufeedme.modelo.Noticia>();
                    ArrayList<blufeedme.modelo.Noticia> ln = new ArrayList<blufeedme.modelo.Noticia>();
                    ArrayList<blufeedme.modelo.Categoria> lc = new ArrayList<blufeedme.modelo.Categoria>();

                    //Si no se ha indicado categoria se muestran todas las noticias
                    if(categoria.isEmpty()){
                        //Obtenemos las categorias del gestor indicado
                        lc = g.getCategorias();
                    }
                    else{
                        blufeedme.modelo.Categoria c = bd.getCategoria(categoria);
                        if(c!=null) lc.add(c);
                    }
                    
                    //Obtenemos las noticias de cada categoria
                    for(int i=0; i<lc.size();i++){
                        //Obtenemos las noticias para la categoria
                        ln = bd.getNoticias(lc.get(i).getId());
                        resultado.addAll(ln);
                    }

                    bd.disconnect();
                    return resultado;
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                 bd.disconnect();
                 throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
            }
        }else
            return null;
    }

    /**
     * Obtiene las categorías gestionadas por el usuario indicado.
     * @param usuario usuario del que se desean obtener las categorías
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario + contraseña.
     * @return lista de categorías. En caso de error devuelve null.
     * @throws InvalidUserException
     */
    @WebMethod(operationName = "getCategorias")
    public ArrayList<blufeedme.modelo.Categoria> getCategorias(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma)throws InvalidUserException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);

            if(g != null){
                String cadena = g.getNombre() + g.getContrasenia();

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                if(f.equals(firma)){
                    ArrayList<blufeedme.modelo.Categoria> lc = new ArrayList<blufeedme.modelo.Categoria>();

                    //Obtenemos las categorias del gestor indicado
                    lc = g.getCategorias();
                    bd.disconnect();
                    return lc;
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        else{
            return null;
        }
    }

    /**
     * Asocia un dispositivo a una categoría. Una asociación proboca que se envien las noticias de la categoría al dispositivo.
     * @param usuario usuario gestor de la categoría.
     * @param MAC dirección MAC del dispositivo
     * @param categoria categoría a asociar.
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + mac + categoria + sello de tiempo
     *        donde categoria es el nombre de la misma.
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "asociar")
    public Boolean asociar(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "MAC")
    String MAC, @WebParam(name = "categoria")
    String categoria, @WebParam(name = "firma")
    String firma) throws InvalidUserException, InvalidParamException {
       if(bd.connect()){
            //Comprobamos si existe el usuario indicado
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g != null){
                String cadena = g.getNombre() + g.getContrasenia() + MAC + categoria;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH");

                Calendar now = Calendar.getInstance();

                cadena =  cadena + sdf.format(now.getTime());

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    Categoria c = null;
                    Dispositivo d = bd.getDispositivo(MAC);
                    if(d!=null){
                        if(!(categoria.isEmpty())){
                            c = bd.getCategoria(categoria);
                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("El nombre de la categoria no puede ser cadena vacia.", CATEGORYERROR);
                        }
                        if(c!=null){
                            //Comprobamos que el usuario que desea realizar la asociación es el gestor de la categoria a asociar
                            if(c.getIdGestor() == g.getId()){
                                Boolean resultado = bd.asociar(d, c);
                                bd.disconnect();
                                return resultado;
                            }
                            else{
                                bd.disconnect();
                                throw new blufeedme.service.excepcion.InvalidParamException("La categoria " + c.getNombre() + " no esta asociada al gestor indicado", CATEGORYERROR);
                            }

                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("No existe la categoria " + categoria, CATEGORYERROR);
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("El dispositivo " + MAC + " no existe.",DISPERROR);
                    }
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        else{
            return false;
        }
    }

    /**
     * Deshace la asociación entre un dispositivo y una categoría. Una vez desasociados el dispositivo no recibirá noticas de la categoría.
     * @param usuario usuario gestor de la categoría.
     * @param MAC dirección MAC del dispositivo.
     * @param categoria categoría a desasociar.
     * @param firma firma del mensaje.La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + mac + categoria + sello de tiempo
     *        donde categoria es el nombre de la misma.
     * @return TRUE en caso de éxito,FALSE en caso contrario.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "desasociar")
    public Boolean desasociar(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "MAC")
    String MAC, @WebParam(name = "Categoria")
    String categoria, @WebParam(name = "firma")
    String firma) throws InvalidUserException, InvalidParamException {
        if(bd.connect()){
            //Comprobamos si existe el usuario indicado
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g != null){
                String cadena = g.getNombre() + g.getContrasenia() + MAC + categoria;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH");

                Calendar now = Calendar.getInstance();

                cadena =  cadena + sdf.format(now.getTime());
                
                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    Categoria c = null;
                    Dispositivo d = bd.getDispositivo(MAC);
                    if(d!=null){
                        if(!(categoria.isEmpty())){
                            c = bd.getCategoria(categoria);
                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("El nombre de la categoria no puede ser cadena vacia.", CATEGORYERROR);
                        }
                        if(c!=null){
                            //Comprobamos que el usuario que desea realizar la desasociación es el gestor de la categoria a desasociar
                            if(c.getIdGestor() == g.getId()){
                                Boolean resultado = bd.desasociar(d, c);
                                bd.disconnect();
                                return resultado;
                            }
                            else{
                                bd.disconnect();
                                throw new blufeedme.service.excepcion.InvalidParamException("La categoría " + c.getNombre() + " no esta asociada al gestor indicado", CATEGORYERROR);
                            }

                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("No existe la categoria " + categoria, CATEGORYERROR);
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("El dispositivo " + MAC + " no existe.", DISPERROR);
                    }
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        else{
            return false;
        }
    }

    /**
     * Da de alta un dispositivo en el sistema.
     * @param usuario usuario que da de alta el dispositivo. Debe ser el gestor de todas las categorías a las que esta asociado el dispositivo.
     * @param dispositivo dispositivo a dar de alta en el sistema.
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + mac + pin
     *        donde mac y pin son correspondientes al dispositivo pasado como parametro.
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     * @throws InvalidUserException
     */
    @WebMethod(operationName = "addDispositivoMovil")
    public Long addDispositivoMovil(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "dispositivo")
    Dispositivo dispositivo, @WebParam(name = "firma")
    String firma) throws InvalidUserException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            blufeedme.modelo.Dispositivo d = new blufeedme.modelo.Dispositivo();

            if(g!=null){
                String cadena = g.getNombre() + g.getContrasenia();
                cadena += dispositivo.getMac() + dispositivo.getPin();
                
                for(blufeedme.modelo.Categoria c : dispositivo.getCategorias()){
                    cadena += c.getId().toString()+
                            c.getNombre()+ c.getDescripcion() + c.getIdGestor();

                }

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    boolean cat_pantalla = false;
                    //Comprobamos que el usuario sea el gestor de las categorias indicadas en el dispositivo
                    for(blufeedme.modelo.Categoria c:d.getCategorias()){
                        if(c.getId() != CATEGORIA_PANTALLA){
                            if(c.getIdGestor()!= g.getId()){
                                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto. "
                                    + "No es el gestor de las categorias indicadas a asociar al dispositivo");
                            }
                        }
                        else
                            cat_pantalla = true;
                    }

                    d.setMac(dispositivo.getMac());
                    d.setPin(dispositivo.getPin());
                    d.setCategorias(new ArrayList<Categoria>());
                    //Si entre las categorias del dispositivo no se encuentra la categoría de pantalla la añadimos.
                    if(!cat_pantalla) d.addCategoria(bd.getCategoria(CATEGORIA_PANTALLA));
                    
                    //insertamos el dispositivo
                    if(bd.insert(d)){
                        bd.disconnect();
                        return d.getId();
                    }
                    else{
                        bd.disconnect();
                        return null;
                    }
                }
                else {
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        else
            return null;

    }

    /**
     * Eliminar un dispositivo del sistema. El dispositivo no puede tener ninguna categoría asociada.
     * @param usuario usuario registrado en el sistema que elimina el dispositivo.
     * @param MAC dirección MAC del dispositivo.
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + mac + sello de tiempo
     *        donde mac es la MAC pasada como parametro.
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "deleteDispositivoMovil")
    public Boolean deleteDispositivoMovil(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "MAC")
    String MAC, @WebParam(name = "firma")
    String firma) throws InvalidUserException, InvalidParamException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g != null){
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH");

                Calendar now = Calendar.getInstance();

                String cadena = g.getNombre() + g.getContrasenia() + MAC + sdf2.format(now.getTime());
                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    blufeedme.modelo.Dispositivo d = bd.getDispositivo(MAC);
                    
                    if(d!=null){
                        //Comprobamos que no tenga categorias asociadas
                        if(d.getCategorias().isEmpty()){
                            Boolean r = bd.deleteDispositivo(MAC);
                            bd.disconnect();
                            return r;
                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("El dispositivo no puede tener ninguna categoría asociada", DISPERROR);
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("El dispositivo" + MAC + " no existe.", DISPERROR);
                    }
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        return false;
    }

    /**
     * Actualiza la información relativa a un dispositivo.
     * @param usuario usuario que modifica el dispositvo. Debe de ser el gestor de al menos una de las categorías a las que esta asociado el movil.
     * @param MAC dirección MAC del dispositivo a modificar
     * @param MACN nueva dirección MAC del dispositivo.
     * @param pinN nuevo número pin del dispositivo.
     * @param firma firma del mensaje.La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + MAC + MACN + pinN +  selloTiempo.
     *        correspondientes al resto de parametros. selloTiempo es una marca temporal del momento en el que se realiza la llamada a la función del webservice.
     *        El sello de tiempo debe tener el formato "yyyy-MM-dd'T'HH".
     * @return TRUE en caso de éxito, FALSE en caso contrario.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "updateDispositivoMovil")
    public Boolean updateDispositivoMovil(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "MAC")
    String MAC, @WebParam(name = "MACN")
    String MACN, @WebParam(name = "pinN")
    String pinN, @WebParam(name = "firma")
    String firma) throws InvalidUserException, InvalidParamException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g!=null){
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH");
                Calendar now = Calendar.getInstance();

                String cadena = g.getNombre() + g.getContrasenia() + MAC + MACN
                        + pinN
                        + sdf2.format(now.getTime());
                
                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    blufeedme.modelo.Dispositivo dispositivo = bd.getDispositivo(MAC);
                    if(dispositivo!=null){
                        Boolean permitidaOperacion = false;

                        if(dispositivo.getCategorias().size()==0) permitidaOperacion = true;
                        else
                            for(blufeedme.modelo.Categoria c : dispositivo.getCategorias()){
                                if(c.getIdGestor() == g.getId()){
                                     permitidaOperacion = true;
                                     break;
                                }
                            }

                        if(permitidaOperacion){
                            dispositivo.setMac(MACN);
                            dispositivo.setPin(pinN);
                            Boolean r = bd.update(dispositivo);
                            bd.disconnect();
                            return r;
                        }

                        bd.disconnect();
                        return false;
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("El dispositivo " + MAC + " no existe.", DISPERROR);
                    }
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        return false;

    }

    /**
     * Obtiene los todos los dispositivos registrados en la BD.
     * @param usuario usuario con el que se va a realizar la consulta.
     * @param firma firma del mensaje enviado.La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña.
     * @return lista de dispositivos registrados en el sistema.
     * @throws InvalidUserException
     * @throws InvalidParamException
     */
    @WebMethod(operationName = "getDispositivos")
    public ArrayList<Dispositivo> getDispositivos(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma) throws InvalidUserException, InvalidParamException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g!=null){
                String cadena = g.getNombre() + g.getContrasenia();

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    ArrayList<Dispositivo> ld= bd.getDispositivos();
                    return ld;
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        return null;
    }

    /**
     * Obtiene los dispositivos registrados en la base de datos asociados a las categorias gestionadas por el usuario que realiza la consulta
     * @param usuario usuario con el que se realiza la consulta
     * @param firma firma del mensaje.La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña.
     * @return lista de dispositivos
     * @throws InvalidUserException
     */
    @WebMethod(operationName = "getMyDispositivos")
    public ArrayList<Dispositivo> getMyDispositivos(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "firma")
    String firma) throws InvalidUserException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g!=null){
                String cadena = g.getNombre() + g.getContrasenia();

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    return bd.getDispositivos(g);
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        return null;
    }

    /**
     *
     * @param usuario usuario que va a realizar la consulta.
     * @param categoria categoria de la que se desean obtener los dispositivos asociados.
     * @param firma firma del mensaje. La firma consiste en el sha1 de la cadena formada por la concatenacion de
     *        usuario +  contraseña + categoria.
     *        donde categoria es la categoria indicada como parametro.
     * @return lista de dispositivos asociados a la categoria indicada como parametro
     * @throws InvalidParamException
     * @throws InvalidUserException
     */
    @WebMethod(operationName = "getDispositivosWhereCategoria")
    public ArrayList<Dispositivo> getDispositivosWhereCategoria(@WebParam(name = "usuario")
    String usuario, @WebParam(name = "categoria")
    String categoria, @WebParam(name = "firma")
    String firma) throws InvalidParamException, InvalidUserException {
        if(bd.connect()){
            blufeedme.modelo.Gestor g = bd.getGestor(usuario);
            if(g!=null){
                String cadena = g.getNombre() + g.getContrasenia() + categoria;

                String f = null;
                try {
                    f = MiSHA.SHA1(cadena);
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(NewsService.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Usuario y password correctos
                if(f.equals(firma)){
                    if(!categoria.isEmpty()){
                        Categoria c = bd.getCategoria(categoria);

                        if(c.getIdGestor() == g.getId()){
                            return bd.getDispositivos(c);
                        }
                        else{
                            bd.disconnect();
                            throw new blufeedme.service.excepcion.InvalidParamException("La categoría indicada no pertenece al usuario que efectua la consulta", CATEGORYERROR);
                        }
                    }
                    else{
                        bd.disconnect();
                        throw new blufeedme.service.excepcion.InvalidParamException("No se ha indicado ninguna categoría", CATEGORYERROR);
                    }
                }
                else{
                    bd.disconnect();
                    throw new blufeedme.service.excepcion.InvalidUserException("usuario/password incorrectos");
                }
            }
            else{
                bd.disconnect();
                throw new blufeedme.service.excepcion.InvalidUserException("usuario incorrecto");
            }
        }
        return null;
    }
}
