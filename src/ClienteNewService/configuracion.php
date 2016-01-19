<?php
/*
 
  Copyright 2010 Ángel Daniel Sanjuán Espejo, David Armenteros Escabias.
 
  This file is part of BluFeedMe.
  
  BluFeedMe is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */
 // inclusión de bibliotecas PHPLIB
 require_once ("prepend.php");

 //datos de conexión a la BD
 class BD_Blufeedme extends DB_Sql {
    var $Host = "localhost";    // host donde está ubicado el SGBD
    var $Database = "clientblufeedme_bd"; // BD a la que vamos a acceder
    var $User = "cliente";        // usuario con privilegios para esa BD
    var $Password = "1234";   // contraseña de acceso

    var $Halt_On_Error = "report";
 }
 
 class Blufeedme_CT_Sql extends CT_Sql {
   	var $database_class = "BD_Blufeedme";     // nombre de la BD
   	var $database_table = "active_sessions"; // tabla con datos de sesiones
 }
 
 class Sesion_Blufeedme extends Session {
	var $classname = "Sesion_Blufeedme"; //el propio nombre
	
	var $cookiename     = "";            //nombre de la "cookie". Por defecto es $classname
	var $magic          = "Patadecabra"; //valor aleatorio para introducir "ruido"
	var $mode           = "cookie";      //modo de propagar las sesiones
	var $fallback_mode  = "get";         //modo auxiliar por si no tenemos "cookies"
	var $lifetime       = 0;             //tiempo de vida 0=hasta cerrar el navegador
	var $that_class     = "Blufeedme_CT_Sql";  //clase contenedora
	var $gc_probability = 5;             //probabilidad de borrar sesiones obsoletas
	var $allowcache     = "no";          //¿permitir caché? ("public", "private", ó "no")
 }

?>