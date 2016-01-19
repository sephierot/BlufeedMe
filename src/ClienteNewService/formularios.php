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
	function formIntroducir(){
			echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=introducir\">
					<div>
						<input name=\"operacion\" type=\"hidden\" value=\"introducir\"/>
					</div>
					<div class=\"linea\">
						<label for=\"t1\"> Titulo: 
							<span class=\"small\">Introduce el titulo  (*)</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t1\" name=\"titulo\" size=\"50\" maxlength=\"255\"/>
					</div>
					
					<div class=\"linea\">
						<label for=\"t2\"> Subtitulo: 
							<span class=\"small\">Introduce el subtitulo</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t2\" name=\"subtitulo\" size=\"50\"/>					
					</div>
					<div class=\"linea\">
						<label for=\"t3\"> Autor: 
							<span class=\"small\">Introduce el autor  (*)</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t3\" name=\"autor\" size=\"50\"/>
					</div>
					
					<div class=\"linea\">
						<label for=\"datepickerP\"> Fecha publicación:
							<span class=\"small\">Introduce la Fecha </span>
						</label> 
						<input class=\"campo\" type=\"text\" id=\"datepickerP\" name=\"fechapubli\"/>
					</div>					

					<div class=\"linea\">
						<label for=\"datepickerC\"> Fecha caducidad: 
							<span class=\"small\">Introduce la Fecha  (*)</span>
						</label> 
						<input class=\"campo\" type=\"text\" id=\"datepickerC\" name=\"fechacadu\"/>
					</div>					
					
					<div class=\"linea\">
						<label for=\"t4\"> Categoria: 
							<span class=\"small\">Introduce la Categoría  (*)</span>
						</label>
						<select class=\"campo\" id=\"t4\" name=\"categoria\">";	
		global $categoriasS;						
		
		/*Obtenemos las categorías del gestor almacenado*/	
		if($categoriasS != NULL){
			foreach($categoriasS as $cat){
				try{
					echo 	"<option value=\"". addslashes($cat->nombre) ."\">" . addslashes($cat->nombre) . "</option>";
				}catch(Exception $e){}
			}
		}
		else ;			
			echo		"</select>
					</div>

					<div class=\"linea\">
						<p>
							<label for=\"t5\"> Texto: 
								<span class=\"small\">Introduce el texto de la noticia (*)</span>
							</label>
						</p>
						<textarea style=\"margin-left:10px\" name=\"texto\" id=\"t5\" rows=\"10\" cols=\"60\"></textarea>
					</div>
				";
				
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\"Introducir\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";		

	}
	
	function formEliminar(){
		global $noticiasS;
		echo "<div id=\"bloqformulario\">
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=eliminar\">
					<div> <input name=\"operacion\" type=\"hidden\" value=\"eliminar\"/> </div>
					<div class=\"linea\">
						<label for=\"idE\"> ID Noticia: 
							<span class=\"small\">Introduce el identificador de la noticia</span>
						</label>
						<select class=\"campo\" id=\"idE\" name=\"id_noticia\">";
		echo 				'<option value="0"></option>';
						
		/*Obtenemos las noticias del gestor almacenado*/
		if($noticiasS != NULL){
		
			foreach($noticiasS as $n){
				try{
					$fechap = new DateTime($n->fechaPubli, new DateTimeZone("Europe/Madrid"));
					$fechac = new DateTime($n->fechaCaducidad, new DateTimeZone("Europe/Madrid"));
				}catch(Exception $e){}
				
				echo 	'<option value="'. $n->id .'">'. $n->id . '</option>';
			}
		}
		
			echo		"</select>
					</div>
					<div>
						<input class=\"boton\" type=\"submit\" value=\"Eliminar\"/>
					</div>";

		echo	"</form>
			</div>";

		echo "<div id=\"info\" style=\"heigth:30px\"> </div>";
		
		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";
	}
	

	function formModificar(){
		global $noticiasS;
		echo "<div id=\"bloqformulario\">
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=modificar\">
					<div><input name=\"operacion\" type=\"hidden\" value=\"modificar\"/></div>
					<div class=\"linea\">
						<label for=\"idM\"> ID Noticia: 
							<span class=\"small\">Introduce el identificador de la noticia a modificar (*)</span>
						</label>
						<select class=\"campo\" id=\"idM\" name=\"id_noticia\">";
		echo 				'<option value="0"></option>';	
		 

		/*Obtenemos las noticias del gestor almacenado*/
		if($noticiasS != NULL){
			foreach($noticiasS as $n){
					echo 	'<option value="'. $n->id .'">'. $n->id . '</option>';
			}
		}
		
			echo		"</select>
					</div>
					<div class=\"linea\">
						<label for=\"t1\"> Titulo: 
							<span class=\"small\">Introduce el nuevo titulo (*)</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t1\" name=\"titulo\" size=\"50\" maxlength=\"255\"/> 
					</div>
					
					<div class=\"linea\">
						<label for=\"t2\"> Subtitulo: 
							<span class=\"small\">Introduce el nuevo subtitulo</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t2\" name=\"subtitulo\" size=\"50\"/>						
					</div>
					
					<div class=\"linea\">
						<label for=\"t3\"> Autor: 
							<span class=\"small\">Introduce el nuevo autor (*)</span>
						</label>
						<input class=\"campo\" type=\"text\" id=\"t3\" name=\"autor\" size=\"50\"/>
					</div>
					
					<div class=\"linea\">
						<label for=\"datepickerP\"> Fecha publicación: 
							<span class=\"small\">Introduce la nueva fecha de publicación (*)</span>
						</label> 
						<input class=\"campo\" type=\"text\" id=\"datepickerP\" name=\"fechapubli\"/>
					</div>					

					<div class=\"linea\">
						<label for=\"datepickerC\"> Fecha caducidad: 
							<span class=\"small\">Introduce la nueva fecha de caducidad (*)</span>
						</label> 
						<input class=\"campo\" type=\"text\" id=\"datepickerC\" name=\"fechacadu\"/>
					</div>					
						
					<div class=\"linea\">
						<label for=\"t4\"> Categoria: 
							<span class=\"small\">Introduce la nueva categoría (*)</span>
						</label>
						<select class=\"campo\" id=\"t4\" name=\"categoria\">";
		echo 				'<option value="">  </option>';
								
		global $categoriasS;
		/*Obtenemos las categorías del gestor almacenado*/
		if($categoriasS != NULL){
			foreach($categoriasS as $cat){
				try{
					echo 	"<option value=\"". addslashes($cat->nombre) ."\">". addslashes($cat->nombre) . "</option>";
				}catch(Exception $e){}
			}
		}
		else ;			
			echo		"</select>

					</div>
					
					<div class=\"linea\">
						<p>
							<label for=\"t5\"> Texto: 
								<span class=\"small\">Introduce el nuevo texto (*)</span>
							</label>
						</p>
						<textarea style=\"margin-left:10px\" name=\"texto\" id=\"t5\" rows=\"10\" cols=\"60\"></textarea>
					</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\"Modificar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";
	}


	function formNoticias(){
		echo "<div id=\"bloqformulario\">
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=listanot\">
					<div class=\"linea\">
						<label for=\"t1\"> Categoria: 
							<span class=\"small\">Introduce la categoria de las noticias </span>
						</label>
						<select class=\"campo\" id=\"t4\" name=\"categoria\">
							<option value=\"\"> Todas </option>";
							
		global $categoriasS;
		/*Obtenemos las categorías del gestor almacenado*/
		if($categoriasS != NULL){
			foreach($categoriasS as $cat){
				try{
					echo 	"<option value=\"". addslashes($cat->nombre) ."\">". addslashes($cat->nombre) . "</option>";
				}catch(Exception $e){}
			}
		}
		else ;			
			echo		"</select>

					</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Aceptar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div style=\"clear:both\" class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";
	}
	
	
	function formCategorias(){
		echo "<div id=\"bloqformulario\">
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=listacat\">";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Aceptar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";
	}

	function formIntroducirB(){
			echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=introducirB\">
						<div><input name=\"operacion\" type=\"hidden\" value=\"introducirB\"/></div>
						<div class=\"linea\">
							<label for=\"t1\"> MAC: 
								<span class=\"small\">Introduce la MAC (*)</span>
							</label>
							<input class=\"campo\" type=\"text\" id=\"t1\" name=\"mac\" size=\"50\" maxlength=\"12\"/>
						</div>
						<div class=\"linea\">
							<label for=\"t2\"> PIN: 
								<span class=\"small\">Introduce el PIN (*)</span>
							</label>
							<input class=\"campo\" type=\"password\" id=\"t2\" name=\"pin\" size=\"50\"/>					
						</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Registrar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto;\"> 
					</div>
				</div>";
		
	}
	
	function formEliminarB(){
			echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=eliminarB\">
						<div><input name=\"operacion\" type=\"hidden\" value=\"eliminarB\"/></div>
						<div class=\"linea\">
							<label for=\"t1\"> MAC: 
								<span class=\"small\">Introduce la MAC (*)</span>
							</label>
								<select class=\"campo\" id=\"t1\" name=\"mac\">
									<option value=\"\"> </option>";
		
		/*Obtenemos los dispositivos del gestor almacenados*/
		global $dispositivosS;
		if($dispositivosS != NULL){
			foreach($dispositivosS as $d){
				try{
					echo 	"<option value=\"". addslashes($d->mac) ."\">". showMAC($d->mac) . "</option>";
				}catch(Exception $e){
				}
			}
		}
		
			echo				"</select>";									
			echo		"</div>";					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Eliminar\"/>
					</div>";
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto; margin-top:20px;\"> 
					</div>
				</div>";	
	}
	function formModificarB(){
		echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=modificarB\">
						<div><input name=\"operacion\" type=\"hidden\" value=\"modificarB\"/></div>
						<div class=\"linea\">
							<label for=\"t1\"> MAC: 
								<span class=\"small\">Introduce la MAC del dispositivo(*)</span>
							</label>					
							<select class=\"campo\" id=\"t1\" name=\"mac\">
								<option value=\"\"> </option>";
		
		/*Obtenemos los dispositivos del gestor almacenados*/
		global $myDispositivosS;
		if($myDispositivosS != NULL){
			if(is_array($myDispositivosS) && count($myDispositivosS)>0){
				foreach($myDispositivosS as $d){
					try{
						echo 	"<option value=\"". addslashes($d->mac) ."\">". showMAC($d->mac) . "</option>";
					}catch(Exception $e){
					}
				}
			}
		}
		
		echo				"</select>";								
		echo		   "</div>

						<div class=\"linea\">
							<label for=\"t2\"> Nueva MAC: 
								<span class=\"small\">Introduce la nueva MAC (*)</span>
							</label>
							<input class=\"campo\" type=\"text\" id=\"t2\" name=\"macN\" size=\"50\" maxlength=\"12\"/>
						</div>
						<div class=\"linea\">
							<label for=\"t3\"> Nuevo pin: 
								<span class=\"small\">Introduce el nuevo PIN (*)</span>
							</label>
							<input class=\"campo\" type=\"password\" id=\"t3\" name=\"pinN\" size=\"50\" maxlength=\"255\"/>
						</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Modificar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto; margin-top:20px;\"> 
					</div>
				</div>";
	}
	
	function formAsociar(){
			echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=asociar\">
						<div><input name=\"operacion\" type=\"hidden\" value=\"asociar\"/></div>
						<div class=\"linea\">
							<label for=\"t1\"> MAC: 
								<span class=\"small\">Introduce la MAC del dispositivo(*)</span>
							</label>
							<select class=\"campo\" id=\"t1\" name=\"mac\">
								<option value=\"\"> </option>";
		
		/*Obtenemos los dispositivos del gestor almacenados*/
		global $dispositivosS;
		if($dispositivosS != NULL){
			foreach($dispositivosS as $d){
				try{
					echo 	"<option value=\"". addslashes($d->mac) ."\">". showMAC($d->mac) . "</option>";
				}catch(Exception $e){
				}
			}
		}
		
		echo				"</select>";								

		echo			"</div>
						<div class=\"linea\">
							<label for=\"t2\"> Categoria: 
								<span class=\"small\">Introduce la categoría(*)</span>
							</label>
						<select class=\"campo\" id=\"t2\" name=\"categoria\">
								<option value=\"\"> </option>";						
		global $categoriasS;

		if($categoriasS != NULL){
			foreach($categoriasS as $cat){
				try{
					echo 	"<option value=\"". addslashes($cat->nombre) ."\">". addslashes($cat->nombre) . "</option>";
				}catch(Exception $e){}
			}
		}
		else ;			
			echo		"</select>

						</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Asociar\"/>
					</div>";
		
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto; margin-top:20px;\"> 
					</div>
				</div>";	
		
	}
	
	function formDesasociar(){
			echo "<div id=\"bloqformulario\">				
				<form id=\"form\" method=\"post\" action=\"action.php?operacion=desasociar\">
						<div><input name=\"operacion\" type=\"hidden\" value=\"desasociar\"/></div>
						<div class=\"linea\">
							<label for=\"macd\"> MAC: 
								<span class=\"small\">Introduce la MAC del dispositivo(*)</span>
							</label>
							<select class=\"campo\" id=\"macd\" name=\"mac\">
								<option value=\"\"> </option>";
		
		/*Obtenemos los dispositivos del gestor almacenados*/
		global $myDispositivosS;
		if($myDispositivosS != NULL){
			foreach($myDispositivosS as $d){
				try{
					echo 	"<option value=\"". addslashes($d->mac) ."\">". showMAC($d->mac) . "</option>";
				}catch(Exception $e){
				}
			}
		}
		
		echo				"</select>";			
		echo		   "</div>
						<div class=\"linea\">
							<label for=\"catd\"> Categoria: 
								<span class=\"small\">Introduce la categoría(*)</span>
							</label>
						<select class=\"campo\" id=\"catd\" name=\"categoria\">
						<option value=\"\"> </option>";		
			echo		"</select>

						</div>";
					
			echo "	<div>
						<input class=\"boton\" type=\"submit\" value=\" Desasociar\"/>
					</div>";
		echo	"</form>
			</div>";

		echo "<div class=\"ui-widget\">
					<div id=\"mensaje\" class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto; margin-top:20px;\"> 
					</div>
				</div>";
	}
?>