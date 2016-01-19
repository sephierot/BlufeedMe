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
require_once ("prepend.php");
require_once ("configuracion.php");
require_once ("UI.php");
require_once("clienteNewsService.php");

page_open(array("sess" => "Sesion_Blufeedme"));
	
global $userp;
global $passp;
global $categoriasS;
global $noticiasS;

if(isset($_POST['usuario']) && isset($_POST['password']) && !empty($_POST['usuario']) && !empty($_POST['password'])){
	try{
		$cliente = new ClienteNewsService($_POST['usuario'],$_POST['password']);	
	}catch(Exception $e){
		echo "<script type=\"text/javascript\"> 
				$.ajax({
						type: \"POST\",
						data: \"error=WebService no disponible\",
						url: 'alerta.php',
						success: function(data){
							$('#central').html(data);
						}
					});
			</script>";
		exit(1);		
	}
	
	try{   
		$cat = $cliente->getCategory();
		unset($categoriasS);
		if(!is_null($cat)){
			if(is_array($cat)){
				if(count($cat)){
					foreach($cat as $c){
						$categoriasS[$c->id] = $c;
					}
				}
			}
		}else{
			echo "<script type=\"text/javascript\"> 
				$.ajax({
						type: \"POST\",
						data: \"error= Error conexión con BD. Web Service no disponible\",
						url: 'alerta.php',
						success: function(data){
							$('#central').html(data);
						}
					});
			</script>";			
		}
	}catch(Exception $e){
		echo "<script type=\"text/javascript\"> 
				$.ajax({
						type: \"POST\",
						data: \"error=" . $e->getMessage() ."\",
						url: 'alerta.php',
						success: function(data){
							$('#central').html(data);
						}
					});
			</script>";
		exit(1);			
	}
	
	try{
		
		$noticias = $cliente->getNews("");
		unset($noticiasS);
		if(!is_null($noticias)){
			if(is_array($noticias)){
				if(count($noticias)>0){
					foreach($noticias as $n){
						$noticiasS[$n->id] = $n;
					}
				}
			}
		}else{
			echo "<script type=\"text/javascript\"> 
				$.ajax({
						type: \"POST\",
						data: \"error= Error conexión con BD. Web Service no disponible\",
						url: 'alerta.php',
						success: function(data){
							$('#central').html(data);
						}
					});
			</script>";			
		}
	}catch(Exception $e){
		echo "<script type=\"text/javascript\"> 
				$.ajax({
						type: \"POST\",
						data: \"error=" . $e->getMessage() ."\",
						url: 'alerta.php',
						success: function(data){
							$('#central').html(data);
						}
					});
			</script>";
		exit(1);	
	}
	
	if(!is_null($cat) && !is_null($noticias)){
		$userp = $_POST['usuario'];
		$passp = $_POST['password'];
		
		showEstado($userp);
		
		echo "<script type=\"text/javascript\"> 
					$('#central').html(\"\"); 
					$('#cerrarSesion').click(function(){
						$.ajax({
							type: \"GET\",
							url: href,
							success: function(data){}
						});						
						return false});
				</script>";
		}
}

page_close();
?>

