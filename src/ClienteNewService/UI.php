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
function showAlert($error){
	echo "<div class=\"ui-widget\">
			<div class=\"ui-state-error ui-corner-all\" style=\"padding: 0 .7em; width:80%; margin:auto; margin-top:20px;\"> 
				<p><span class=\"ui-icon ui-icon-alert\" style=\"float: left; margin-right: .3em;\"></span> 
				<strong>Alert:</strong> ". $error . " </p>
			</div>
		</div>";
}

function showMessage($mensaje){
	echo "<div class=\"ui-widget\">
				<div class=\"ui-state-highlight ui-corner-all\" style=\"margin:auto; margin-top: 20px; width:80%; padding: 0 .7em;\"> 
					<p><span class=\"ui-icon ui-icon-info\" style=\"float: left; margin-right: .3em;\"></span>
					<strong>OK</strong>". $mensaje ."</p>
				</div>
			</div>";
	
}

function showEstado($user){
		echo "<label> Usuario: </label>" . $user;
		echo "<a id=\"cerrarSesion\" href=\"action.php?operacion=cerrar_sesion\"> cerrar sesión <a>";	
}

function showDispositivos($dispositivos, $titulo){
	echo "<div class=\"tabla\">";
		echo "<h3 style=\"color:#333\">" . $titulo ."</h3>";
		if(is_array($dispositivos)){
			if(count($dispositivos)>0){
				foreach($dispositivos as $d){
					echo "<div class=\"cabecera\">";
					echo showMAC($d->mac);
					echo "</div>";
					echo "<div class=\"filas\">";

					if(count($d->categorias)<2) $categorias = array($d->categorias);
					else  $categorias = $d->categorias;

					$i = 0;
					foreach($categorias as $c){
						echo "<div class = \"fila". $i%2 ."\">";
						echo addslashes($c->nombre)."  (". addslashes($c->descripcion) .")";
						echo "</div>";
						$i++;
					} 
					echo "</div>";
				}
			}
		}
	echo "</div>";	
}

function showMAC($mac){
	return substr(addslashes($mac),0,2) . ":". substr(addslashes($mac),2,2) . ":". substr(addslashes($mac),4,2) . ":" . substr(addslashes($mac),6,2). ":" . substr(addslashes($mac),8,2). ":" . substr(addslashes($mac),10,2);
}

function showTituloFormulario($titulo){
	echo "<div class=\"titform\">";
	echo $titulo;
	echo "</div>";	
}
?>