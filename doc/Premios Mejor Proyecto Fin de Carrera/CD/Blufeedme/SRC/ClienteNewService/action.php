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
 
require_once("clienteNewsService.php");
require_once ("prepend.php");
require_once ("configuracion.php");
require_once ("UI.php");

page_open(array("sess" => "Sesion_Blufeedme"));


if(isset($_GET['operacion'])){
	
	global $clienteS;
	global $categoriasS;
	global $noticiasS;
	global $userp;
	global $passp;
	
	try{
		$clienteS = new ClienteNewsService($userp,$passp);	
	}catch(Exception $e){
		showAlert($e->getMessage());
		exit(1);
	}
	
	if($clienteS!=NULL){
		
		if(isset($userp)){
			if(!empty($userp)) $usuario = $userp;
			else $mens_error="No se ha introducido ningun usuario";
		}

		
		if(isset($passp)){
			if(!empty($passp)) $password = $passp;
			else $mens_error="No se ha introducido ningun password";
		}

		switch($_GET['operacion']){
			case 'introducir':
				if(isset($_POST['titulo'])){
					$titulo = $_POST['titulo'];
					if(empty($titulo))
						if(!isset($mens_error))$mens_error="No se ha introducido ningun titulo";
				}
				
				if(isset($_POST['subtitulo'])){
					$subtitulo = $_POST['subtitulo'];
				}
				else
					$subtitulo = "";
		
		
				if(isset($_POST['texto'])){
					$texto = $_POST['texto'];
				if(empty($texto))
					if(!isset($mens_error))$mens_error="No se ha introducido ningun texto";
				}		

				if(isset($_POST['autor'])){
					$autor = $_POST['autor'];
					if(empty($autor))
						if(!isset($mens_error))$mens_error="No se ha introducido ningun autor";
		
				}

				if(isset($_POST['categoria'])){
					$categoria = $_POST['categoria'];
					if(empty($categoria))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna categoría";
				}
				
				if(isset($_POST['fechapubli'])){
					if(!empty($_POST['fechapubli'])){
						try{
							$fechapubli = new DateTime($_POST['fechapubli'], new DateTimeZone("Europe/Madrid"));
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}else{
						try{
							$fechapubli = new DateTime("now",new DateTimeZone("Europe/Madrid"));	
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}
				}
				else{ 
					try{
						$fechapubli = new DateTime("now",new DateTimeZone("Europe/Madrid"));
					}catch (Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
				}
				

				if(isset($_POST['fechacadu'])){
					if(!empty($_POST['fechacadu'])){
						try{
							$fechacadu = new DateTime($_POST['fechacadu'],new DateTimeZone("Europe/Madrid"));
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}
					else{
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna fecha de caducidad";
					}
				}
				
				if(!isset($mens_error)){//Todos los parametros son correctos
					$noticia = new stdClass();
					$noticia ->autor = $autor; //utf8_encode($autor);
					$noticia ->categoria = $categoria; 
					$noticia ->titulo = $titulo; 
					$noticia ->texto = $texto;
					$noticia ->subtitulo = $subtitulo;
					try{
						$noticia ->fechaPubli = $fechapubli->format('Y-m-d\TH:i:s');
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);
					}
					
					try{
						$noticia ->fechaCaducidad = $fechacadu->format('Y-m-d\TH:i:s');
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					
					try{
						//Ejecutamos la operacion del webservice
						$r = $clienteS->addNews($noticia);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);							
					}
					if($r!=-1){
						showMessage("La noticia ha sido introducida correctamente con ID : (". $r .")");
    				}
    				else if(!isset($mens_error))$mens_error= "La noticia NO ha sido introducida.";

				}
				
				break;
			
			case 'eliminar':
				if(isset($_POST['id_noticia'])){
					$id_noticia = $_POST['id_noticia'];
					if(empty($id_noticia))
						if(!isset($mens_error)) $mens_error="No se ha introducido ningun identificador de noticia";
				}
				
				if(!isset($mens_error)){//Todos los parametros son correctos
					try{
						$r = $clienteS->deleteNews($id_noticia);
					}catch(Exeption $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					if($r==TRUE){
    					showMessage("La noticia ha sido eliminada correctamente".$id_noticia);
    				}
					else if(!isset($mens_error)){ 
						$mens_error= "La noticia NO ha sido eliminada.";
					}
				}

				break;
			case 'modificar':
				if(isset($_POST['id_noticia'])){
					if(is_numeric($_POST['id_noticia']) && $_POST['id_noticia'] >0)
						$id_noticia = $_POST['id_noticia'];
					else
						if(!isset($mens_error))$mens_error="Se ha introducido un identificador de noticia incorrecto (>0)";
						
				}	
				
				if(isset($_POST['titulo'])){
					$titulo = $_POST['titulo'];
					if(empty($titulo))
						if(!isset($mens_error))$mens_error="No se ha introducido ningun titulo";
				}
				
				if(isset($_POST['subtitulo'])){
					$subtitulo = $_POST['subtitulo'];
				}
				else
					$subtitulo = "";
		
		
				if(isset($_POST['texto'])){
					$texto = $_POST['texto'];
				if(empty($texto))
					if(!isset($mens_error))$mens_error="No se ha introducido ningun texto";
				}		

				if(isset($_POST['autor'])){
					$autor = $_POST['autor'];
					if(empty($autor))
						if(!isset($mens_error))$mens_error="No se ha introducido ningun autor";
		
				}

				if(isset($_POST['categoria'])){
					$categoria = $_POST['categoria'];
					if(empty($categoria))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna categoría";
				}
				
				if(isset($_POST['fechapubli'])){
					if(!empty($_POST['fechapubli'])){
						try{
							$fechapubli = new DateTime($_POST['fechapubli'], new DateTimeZone("Europe/Madrid"));
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}else{
						try{
							$fechapubli = new DateTime("now",new DateTimeZone("Europe/Madrid"));	
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}
				}
				else{ 
					try{
						$fechapubli = new DateTime("now",new DateTimeZone("Europe/Madrid"));
					}catch (Exception $e){
						showAlert($e->getMessage());
						exit(1);
					}
				}
				

				if(isset($_POST['fechacadu'])){
					if(!empty($_POST['fechacadu'])){
						try{
							$fechacadu = new DateTime($_POST['fechacadu'],new DateTimeZone("Europe/Madrid"));
						}catch (Exception $e){
							showAlert($e->getMessage());
							exit(1);
						}
					}
					else{
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna fecha de caducidad";
					}
				}
				
				if(!isset($mens_error)){//Todos los parametros son correctos
										
					$noticia = new stdClass();
					$noticia ->autor = $autor; //utf8_encode($autor);
					$noticia ->categoria = $categoria; 
					$noticia ->titulo = $titulo; 
					$noticia ->texto = $texto;
					$noticia ->subtitulo = $subtitulo;
					try{
						$noticia ->fechaPubli = $fechapubli->format('Y-m-d\TH:i:s');
					}catch(Exception $e){
							showAlert($e->getMessage());
							exit(1);
					}
					
					try{
						$noticia ->fechaCaducidad = $fechacadu->format('Y-m-d\TH:i:s');
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					
					try{
						$r = $clienteS->updateNews($id_noticia,$noticia);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);							
					}
					
					if($r==TRUE){
    					showMessage("La noticia ha sido modificada correctamente");
    				}
					else if(!isset($mens_error))$mens_error= "La noticia NO ha sido modificada.";
				}
				break;	
			
			case 'listanot':
				if(isset($_POST['categoria'])){
					$categoria = $_POST['categoria'];
				}
				else
					$categoria = "";
					
				if(!isset($mens_error)){//Todos los parametros son correctos	
					try{
						$noticias = $clienteS->getNews($categoria);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					
    				if($noticias != NULL){
						echo "<div id=\"accordion\">";
						$prop = 'id';
    					foreach($noticias as $n){
							$nomCategoria = "";
							foreach($categoriasS as $c){
        						if($c->$prop == $n->idCategoria){
            						$nomCategoria = $c->nombre;
									break;
        						}
   							}
							
							echo 	"<h1>";
    						echo 		"<a href=\"#\">".$n->titulo." (ID: ". $n->id ."): ". $n->subtitulo ." ( ". $nomCategoria ." )</a>";
  							echo 	"</h1>";
							echo 	"<div>";
							echo		"<div class = \"subheader\"> 
											<label style=\"float:left\"> Autor: ". $n->autor ." </label>
											<label style=\"float:right\"> Fecha publicación:". $n->fechaPubli ."</label>
										</div>";
    						echo 		"<div>
											<p>" . $n->texto . "</p>
										</div>";
  							echo 	"</div>";

    					}
						echo "</div>";
    				}
					
					else if(!isset($mens_error))$mens_error= "No existen noticias para el usuario/categoría indicado/a.";
				}
				break;
				
			case 'listacat':
				if(!isset($mens_error)){//Todos los parametros son correctos	

					$categorias = $categoriasS;
					
					if($categorias != NULL){						 
						echo "<div id=\"accordion\">";
    					foreach($categorias as $cat){
							echo 	"<h3>";
    						echo 		"<a href=\"#\">".$cat->nombre . " (ID:" . $cat->id . ")</a>";
  							echo 	"</h3>";
							echo 	"<div>
											<p>" . $cat->descripcion . "</p>";
  							echo 	"</div>";

    					}
						echo "</div>";
    				}
					else if(!isset($mens_error))$mens_error= "No existen categorías para el usuario indicado.";			
				}
				break;
				
			case 'introducirB':
				if(isset($_POST['mac'])){
					$mac = $_POST['mac'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($mac))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($mac)<12 || preg_match($pattern,$mac)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}
				
				if(isset($_POST['pin'])){
				  $pin = $_POST['pin'];
				  $patternNoNumeric = '/[^0-9]/';
				  if(empty($pin))
				    if(!isset($mens_error)) $mens_error = "No se ha introducido ningun pin";
				  else if(strlen($pin)>7 || preg_match($patternNoNumeric,$pin)!=0){
           			 if(!isset($mens_error)) $mens_error = "PIN incorrecto (Debe tener una longigud <=7; caracteres NUMERICOS [0-9])";
          		  }
				}

				if(!isset($mens_error)){//Todos los parametros son correctos
					$dispositivo = new stdClass();
					$dispositivo ->mac = $mac; //utf8_encode($autor);
					$dispositivo ->pin = $pin; 
					
					try{
						$r = $clienteS->addB($dispositivo);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);							
					}
					
					if($r>=0){
    					showMessage("El dispositivo ha sido introducido correctamente con ID: (". $r . ")");
						
    				}
					else if(!isset($mens_error))$mens_error= "El dispositivo movil no ha sido insertado.";					
				}
		
				break;
				
			case 'eliminarB':
				if(isset($_POST['mac'])){
					$mac = $_POST['mac'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($mac))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($mac)<12 || preg_match($pattern,$mac)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}
				
				if(!isset($mens_error)){//Todos los parametros son correctos
					try{
						$r = $clienteS->deleteB($mac);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);							
					}
					
					if($r==TRUE){
    					showMessage("El dispositivo ha sido eliminado correctamente");
    				}
					else if(!isset($mens_error))$mens_error= "El dispositivo movil no ha sido eliminado.";
				}			
				break;
			case 'modificarB':
				if(isset($_POST['mac'])){
					$mac = $_POST['mac'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($mac))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($mac)<12 || preg_match($pattern,$mac)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}
				
				if(isset($_POST['macN'])){
					$macN = $_POST['macN'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($macN))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($macN)<12 || preg_match($pattern,$macN)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}

				if(isset($_POST['pinN'])){
				  $pinN = $_POST['pinN'];
				  $patternNoNumeric = '/[^0-9]/';
				  if(empty($pinN))
				    if(!isset($mens_error)) $mens_error = "No se ha introducido ningun pin";
				  else if(strlen($pinN)>7 || preg_match($patternNoNumeric,$pinN)!=0){
            		if(!isset($mens_error)) $mens_error = "PIN incorrecto (Debe tener una longigud <=7; caracteres NUMERICOS [0-9])";
          		  }
				}


				if(!isset($mens_error)){//Todos los parametros son correctos
					try{
						$r = $clienteS->updateB($mac,$macN,$pinN);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					if($r==TRUE){
						showMessage("El dispositivo ha sido modificado correctamente");
    				}
					else if(!isset($mens_error))$mens_error= "El dispositivo movil no ha sido modificado.";
				}			
			
				break;
			case 'asociar':
				if(isset($_POST['mac'])){
					$mac = $_POST['mac'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($mac))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($mac)<12 || preg_match($pattern,$mac)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}

				if(isset($_POST['categoria'])){
					$categoria = $_POST['categoria'];
					if(empty($categoria))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna categoría";
				}
				if(!isset($mens_error)){//Todos los parametros son correctos
					try{
						$r = $clienteS->associate($mac, $categoria);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					if($r==TRUE){
    					showMessage("El dispositivo (". $mac .") ha sido asociado correctamente a la categoría ".$categoria.".");
    				}
					else if(!isset($mens_error))$mens_error= "El dispositivo movil no ha sido asociado.";
				}			
			break;
			case 'desasociar':
				if(isset($_POST['mac'])){
					$mac = $_POST['mac'];
					$pattern = '/[^0-9^a-f^A-F]/';
					if(empty($mac))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna mac";
					else if(strlen($mac)<12 || preg_match($pattern,$mac)!=0){
						if(!isset($mens_error))$mens_error="MAC incorrecta (debe tener longitud 12; caracteres HEX)";
					}
				}

				if(isset($_POST['categoria'])){
					$categoria = $_POST['categoria'];
					if(empty($categoria))
						if(!isset($mens_error))$mens_error="No se ha introducido ninguna categoría";
				}
				if(!isset($mens_error)){//Todos los parametros son correctos
					
					try{
						$r = $clienteS->disassociate($mac, $categoria);
					}catch(Exception $e){
						showAlert($e->getMessage());
						exit(1);	
					}
					if($r==TRUE){
						showMessage("El dispositivo (". $mac .") ha sido desasociado correctamente de la categoría ".$categoria.".");
    				}
					else if(!isset($mens_error))$mens_error= "El dispositivo movil no ha desasociado.";
				}				
			break;
			
			case 'cerrar_sesion':

				$sess->unregister('userp');
				$sess->unregister('passp');
				$sess->unregister('clienteS');
				$sess->unregister('categoriasS');
			
				/* Redireccionar a una pagina diferente en el directorio actual de la peticion */
				$host  = $_SERVER['HTTP_HOST'];
				$uri   = rtrim(dirname($_SERVER['PHP_SELF']), '/\\');
				$extra = 'index.php';
				header("Location: http://$host$uri/$extra");
				exit;		
			break;
		}
		
		if(isset($mens_error)){
			showAlert($mens_error);
		}
	}
	else{
		showAlert("El WebService no se encuentra disponible.");	
	}
}

page_close();
?>
