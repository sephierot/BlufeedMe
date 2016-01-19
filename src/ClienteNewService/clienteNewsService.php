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

class ClienteNewsService{

	private $client;
	private $user;
	private $password;
	
	function __construct($user, $password){
			$this->user = $user;
			$this->password = $password;			

			// Analizar sin secciones
			$matriz_ini = parse_ini_file("configuracion.ini");

	 		$this->client = new SoapClient($matriz_ini['URL']);
	}
	
	public function setClient($client){
		$this->client = $client;	
	}
	
	public function setUser($user){
		$this->user = $user;
	}
	
	public function setPass($password){
		$this->password = $password;	
	}
	
	public function getClient(){
		return $this->client;
	}
	
	public function getUser(){
		return $this->user;	
	}
	
	public function getPassword(){
		return $this->password;	
	}
	
	/*Introduce una nueva noticia*/
	public function addNews($news){
		/*concatenamos toda la información para obtener la firma por sha*/
		$str = $this->user . $this->password . $news->autor . $news->categoria . $news->titulo . $news->texto . $news->subtitulo;
		$str = $str . $news ->fechaPubli . $news ->fechaCaducidad;
		

		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));

		
		$param = array('usuario'=>$this->user,'firma'=>$signature,'noticia'=> $news);

		$result = $this->client->addNoticia($param);
		
		if($result->return!=NULL)
			return $result->return;
		
		return -1;
	}

	/*Elimina una noticia*/
	public function deleteNews($id_news){
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
		
		$str = $this->user . $this->password . $id_news;
		
		//Añadimos la fecha actual con hora para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');		//TESTEAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$param = array('usuario'=>$this->user,'firma'=>$signature,'id_noticia'=>$id_news);
		
		$result = $this->client->deleteNoticia($param);
	
		if($result->return!=NULL)
			return TRUE;
	
		return FALSE;
	}
	
	/*Modifica una noticia*/
	public function updateNews($id_news,$news){
		$str = $this->user . $this->password . $id_news . $news->autor . $news->categoria . $news->titulo . $news->texto . $news->subtitulo;
		
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
		
		$str = $str . $news->fechaPubli . $news->fechaCaducidad;
		
		//Añadimos la fecha actual con hora para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$param = array('usuario'=>$this->user,'firma'=>$signature,'id_noticia'=>$id_news,'new_noticia'=>$news);
	
		$result = $this->client->updateNoticia($param);
		
		if($result->return!=NULL)
			return TRUE;
		
		return FALSE;
	}

	/*Obtiene la lista de noticias para el usuario indicado*/
	public function getNews($category){

		$str = $this->user . $this->password . $category;
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user,'categoria'=>$category,'firma'=>$signature);
		
		$result = $this->client->getNoticias($params);

		if($result->return != NULL){		
			if(is_array($result->return)){
				return $result->return;
			}
			else if(is_object($result->return)){
				return array($result->return);
			}
		}
		else return;
	}
		/*Obtiene la lista de categorias para el usuario indicado*/
	public function getCategory(){
		$str = $this->user . $this->password;
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));

		$params = array('usuario'=>$this->user,'firma'=>$signature);
		
		$result = $this->client->getCategorias($params);

		if($result->return != NULL){
			if(is_array($result->return)){
				return $result->return;
			}
			else if(is_object($result->return)){
				return array($result->return);
			}
		}
		else return;
	}
	
	/*Inserta un dispositivo movil*/
	public function addB($device){
		$str = $this->user . $this->password . $device ->mac . $device ->pin;

		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user, 'dispositivo'=>$device, 'firma'=> $signature);
		
		$result = $this->client->addDispositivoMovil($params);
		
		if($result->return != NULL)
			return $result->return;
		else
			return -1;
		
	}
	
	public function deleteB($mac){
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
		
		$str = $this->user . $this->password . $mac;	
		//Añadimos la fecha actual con hora para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');		//TESTEAR!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user, 'MAC'=>$mac, 'firma'=> $signature);
		
		$result = $this->client->deleteDispositivoMovil($params);
		
		if($result->return!=NULL)
			return TRUE;
	
		return FALSE;
	}
	
	public function updateB($mac, $macN, $pinN){
		$str = $this->user . $this->password . $mac . $macN . $pinN;
		
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
				
		//Añadimos la fecha actual con hora y minuto para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user, 'MAC'=>$mac, 'MACN'=>$macN, 'pinN'=>$pinN, 'firma'=>$signature);

		$result = $this->client->updateDispositivoMovil($params);
		
		if($result->return!=NULL)
			return TRUE;
		
		return FALSE;		
	}
	
	public function associate($mac, $category){
		$str = $this->user . $this->password . $mac . $category;
		
		
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
				
		//Añadimos la fecha actual con hora y minuto para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');		
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user, 'MAC'=>$mac, 'categoria'=> $category, 'firma'=>$signature);

		$result = $this->client->asociar($params);
		
		if($result->return!=NULL)
			return TRUE;
		
		return FALSE;				
		
	}

	public function disassociate($mac, $category){
		$str = $this->user . $this->password . $mac . $category;
		
		try{
			$now = new DateTime("now", new DateTimeZone("Europe/Madrid"));
		}catch(Exception $ex){
			echo $ex->getMessage();
			exit(1);			
		}
				
		//Añadimos la fecha actual con hora y minuto para firmar el mensaje
		$str = $str . $now->format('Y-m-d\TH');		
				
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user, 'MAC'=>$mac, 'Categoria'=> $category, 'firma'=>$signature);

		$result = $this->client->desasociar($params);
		
		if($result->return!=NULL)
			return TRUE;
		
		return FALSE;				
		
	}
	
	public function getDispositivos(){
		$str = $this->user . $this->password;
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user,'firma'=>$signature);
		
		$result = $this->client->getDispositivos($params);

		if($result->return != NULL){		
			if(is_array($result->return)){
				return $result->return;
			}
			else if(is_object($result->return)){
				return array($result->return);
			}
		}
		else return;
		
	}
	
	public function getMyDispositivos(){
		$str = $this->user . $this->password;
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user,'firma'=>$signature);
		
		$result = $this->client->getMyDispositivos($params);

		if($result->return != NULL){		
			if(is_array($result->return)){
				return $result->return;
			}
			else if(is_object($result->return)){
				return array($result->return);
			}
		}
		else return;		
	}
	
	public function getDispositivosWhereCategoria($categoria){
		$str = $this->user . $this->password . $categoria;
		
		$signature = sha1(mb_convert_encoding($str, "ISO-8859-1", "UTF-8"));
		
		$params = array('usuario'=>$this->user,'categoria'=>$categoria, 'firma'=>$signature);

		$result = $this->client->getDispositivosWhereCategoria($params);
		
		if($result->return != NULL){		
			if(is_array($result->return)){
				return $result->return;
			}
			else if(is_object($result->return)){
				return array($result->return);
			}
		}
		else return;	
	}
	
}

?>