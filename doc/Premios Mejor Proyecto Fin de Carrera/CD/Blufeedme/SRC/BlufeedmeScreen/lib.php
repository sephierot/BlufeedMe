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
 
require_once("prepend.php");
require_once("configuracion.php");

class GestorNoticias{
	var $bd;
		
	function GestorNoticias(){
		$this->bd = new BD_Blufeedme();
	}
	
	function getXMLNoticias(){
		$noticias = $this->bd->getNoticiasPublicadas();
		
		$xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
				<noticias>";
		
		if(is_array($noticias)){
			foreach($noticias as $n){
				$xml = $xml . "<noticia> <titulo>" . utf8_encode($n->titulo) . "</titulo> 
									<subtitulo>" . utf8_encode($n->subtitulo) . "</subtitulo>
									<autor>" . utf8_encode($n->autor) . "</autor>
									<fecha>" . utf8_encode($n->fechaPubli) . "</fecha>
									<texto>" . utf8_encode($n->texto) . "</texto>
								</noticia>";		
			}
			$xml = $xml . "</noticias>";
		}			
		return $xml;		
	}

}

?>

