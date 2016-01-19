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
    var $Database = "blufeedme_db"; // BD a la que vamos a acceder
    var $User = "root_blufeedme";        // usuario con privilegios para esa BD
    var $Password = "1234";   // contraseña de acceso

    var $Halt_On_Error = "yes"; // ¿abortar si hay algún error?
	
	function DB_Blufeedme($host, $database, $user, $pass){
		parent::DB_Blufeedme();
		$this->Host = $host;
		$this->Database = $database;
		$this->User = $user;
		$this->Password = $pass;
	}
	
	function getNoticiasPublicadas(){
		//Nos quedamos solo con las noticias con categoria 1 que son las que se mostrarán por pantalla.
		$consulta = "SELECT n.titulo, n.subtitulo, n.autor, n.fecha_publi, n.texto, c.nombre
				 FROM noticia n, categoria c
				 WHERE  n.id_categoria = c.id_categoria
				 AND c.id_categoria = 1
				 AND n.fecha_caducidad >= NOW()
				 AND n.fecha_publi <= NOW()
				 ORDER BY n.fecha_publi";

		try{
    		$this->query($consulta);
		}catch(Exception $e){
			echo $e->getMessage();
			exit(1); 	
		}
	
    	// recorremos todos los resultados y los almacenamos en un vector
    	while ( $this->next_record() ){
			$noticia = new stdClass();
			$noticia ->autor = $this->f("autor");
			$noticia ->categoria = $this->f("categoria"); 
			$noticia ->titulo = $this->f("titulo"); 
			$noticia ->texto = $this->f("texto");
			$noticia ->subtitulo = $this->f("subtitulo");
			$noticia ->fechaPubli = $this->f("fecha_publi");
		
			$noticias[] = $noticia;
    	}
		return $noticias;
	}
	
 }

 ?>
