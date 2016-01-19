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
 
	require_once ("formularios.php");
	require_once ("prepend.php");  
	require_once ("configuracion.php");
	require_once("clienteNewsService.php");
	require_once ("UI.php");	
	
	page_open(array("sess" => "Sesion_Blufeedme"));	
	
	global $userp;
	global $passp;
	global $categoriasS;
	global $noticiasS;
    global $dispositivosS;
	
	if(isset($_GET['operacion'])){
		//si se han introducido usuario y password
		if(!empty($userp) && !empty($passp)){
			try{
				$cliente = new ClienteNewsService($userp,$passp);	
			}catch(Exception $e){
				showAlert("WebService no disponible");
				exit(-1);
			}
			try{   
				$cat = $cliente->getCategory();
				if(is_array($cat)){
					if(count($cat)>0){
						foreach($cat as $c){
							$categoriasS[$c->id] = $c;
						}
					}
				}
			}catch(Exception $e){
				showAlert($e->getMessage());
				exit(1);			
			}
	
			try{
				$noticias = $cliente->getNews("");
				unset($noticiasS);
				if(is_array($noticias)){
					if(count($noticias)>0){
						foreach($noticias as $n){
							$noticiasS[$n->id] = $n;
						}
					}
				}
			}catch(Exception $e){
				showAlert($e->getMessage());
				exit(1);	
			}
			
			try{
				$dispositivosS = $cliente->getDispositivos();

			}catch(Exception $e){
				showAlert($e->getMessage());
				exit(1);					
			}
			try{
				$myDispositivosS = $cliente->getMyDispositivos();
			}catch(Exception $e){
				showAlert($e->getMessage());
				exit(1);					
			}
			
			if(is_array($myDispositivosS)){
				if(count($myDispositivosS)>0){
					
			//Creamos un vector con los dispositivos del usuario en javascript
			echo 	"<script type=\"text/javascript\">
						dispositivos = new Array(" . count($myDispositivosS) . ");
						i = 0;";

					//Recorremos todos los dispositivos para ir almacenandolos en el vector
					foreach($myDispositivosS as $d){
						try{
							//Nos aseguramos de que categorias sea un array
							if(count($d->categorias)<2) $categorias = array($d->categorias);
							else  $categorias = $d->categorias;

							//Creamos un array de categorias para el dispositivo que estamos creando.
							echo 	"categorias = new Array(".count($d->categorias).");";
							echo 	"j = 0;";
							foreach($categorias as $c){
								echo	'categorias[j] = new categoria("'. addslashes($c->nombre) . '");';
								echo	"j = j + 1;";	
							}
					
							echo	'dispositivos[i] = new dispositivo('. $d->id .
																		',"'.addslashes($d->mac) .
																		'", categorias);
									 i = i + 1;';
						}catch(Exception $e){
							showAlert($e->getMessage());
							exit(1);	
						}	
					}
				}
			}
			echo " </script>";

			echo	"<script type=\"text/javascript\"> 			
							noticias = new Array(". count($noticiasS) .");
							i = 0;";
							
			$sustituye = array("(\r\n)", "(\n\r)", "(\n)", "(\r)");
			if(is_array($noticiasS)){
				if(count($noticiasS)>0){
					foreach($noticiasS as $n){
						try{
							$fechap = new DateTime($n->fechaPubli, new DateTimeZone("Europe/Madrid"));
							$fechac = new DateTime($n->fechaCaducidad, new DateTimeZone("Europe/Madrid"));
						}catch(Exception $e){}
						
						try{
							//Pasamos las noticias al array para usarlo en javascript
							echo 	'noticias[i] = new noticia('. $n->id .
																',"'. addslashes($n->titulo) . 
																'","' . addslashes($n->subtitulo) . 
																'","'. addslashes($n->autor) . 
																'","' . addslashes($fechap->format('Y-m-d')) .
																'","' . addslashes($fechac->format('Y-m-d')) .
																'","' . preg_replace($sustituye, "\\n", addslashes($n->texto)) . 
																'","'. $categoriasS[$n->idCategoria]->nombre . '");
										i = i + 1;';
						}catch(Exception $e){}
					}
				}
			}
			echo " </script>";
			
			switch ($_GET['operacion']){
				case 'introducir': 
					showTituloFormulario("Introducir Noticia");
					formIntroducir();	
					break;
	
				case 'eliminar': 
					showTituloFormulario("Eliminar Noticia");
					formEliminar();
					break;
	 
				case 'modificar': 
					showTituloFormulario("Modificar Noticia");
					formModificar();
					break;
				
				case 'listanot': 
					formNoticias();
					break;
					
				case 'listacat':
					if($categoriasS != NULL && is_array($categoriasS) && count($categoriasS)>0){
						echo "<div id=\"accordion\">";
						foreach($categoriasS as $cat){
							echo 	"<h3>";
							echo 		"<a href=\"#\">".$cat->nombre . "</a>";
							echo 	"</h3>";
							echo 	"<div>
											<p>" . $cat->descripcion . "</p>";
							echo 	"</div>";
						}
						echo "</div>";
					}
					else{ 
						showAlert("No existen categorías para el usuario indicado.");	
					}
				break;
				
				case 'introducirB':
					showTituloFormulario("Introducir Dispositivo Bluetooth");
					formIntroducirB();
					showDispositivos($dispositivosS, "Dispositivos existentes en el sistema");
				break;
				
				case 'modificarB':
					showTituloFormulario("Modificar Dispositivo Bluetooth");
					formModificarB();
				break;
				
				case 'eliminarB':
					showTituloFormulario("Eliminar Dispositivo Bluetooth");
					formEliminarB();
				break;
				
				case 'asociar':
					showTituloFormulario("Asociar DispositivoBluetooth-Categoría");
					formAsociar();
					showDispositivos($dispositivosS, "Dispositivos existentes en el sistema");
				break;
				
				case 'desasociar':
					showTituloFormulario("Desasociar DispositivoBluetooth-Categoría");
					formDesasociar();
					showDispositivos($myDispositivosS, "Mis dispositivos registrados");
				break;
				
				default:
				break;
			}
		}
		else{
			showAlert("Introduzca usuario/password antes de realizar cualquier operación.");		
		}
	}

page_close();
?>
<script type="text/javascript">
$('#accordion').accordion();
</script>