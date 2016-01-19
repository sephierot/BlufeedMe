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
	require_once ("configuracion.php");
	require_once ("UI.php");
	
	page_open(array("sess" => "Sesion_Blufeedme"));
	
	//registramos las variables para usuario, password y cliente
	if(!session_is_registered('userp')){
		$sess->register('userp');
		$userp ="";
	}

	if(!session_is_registered('passp')){
		$sess->register('passp');
		$passp ="";
	}
	
	if(!session_is_registered('categoriasS')){
		$sess->register('categoriasS');
		$categoriasS = array();
	}
	
	if(!session_is_registered('noticiasS')){
		$sess->register('noticiasS');
		$noticiasS = array();
	}

	if(!session_is_registered('dispositivosS')){
		$sess->register('dispositivosS');
		$noticiasS = array();
	}
	if(!session_is_registered('myDispositivosS')){
		$sess->register('myDispositivosS');
		$noticiasS = array();
	}		
	page_close();
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="es" lang="es">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<title>Blufeedme</title>

<link href="css/estilos.css" rel="stylesheet" type="text/css"/>
<link href="css/login.css" rel="stylesheet" type="text/css"/>
<link href="css/mbExtruder.css" media="all" rel="stylesheet" type="text/css"/>
<link href="css/ui-darkness/jquery-ui-1.8.6.custom.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="inc/jquery-1.4.4.min.js"></script>
<script type="text/javascript" src="inc/jquery.hoverIntent.min.js"></script>
<script type="text/javascript" src="inc/jquery.metadata.js"></script>
<script type="text/javascript" src="inc/jquery.mb.flipText.js"></script>
<script type="text/javascript" src="inc/mbExtruder.js"></script>
<script type="text/javascript" src="inc/jquery.form.js"></script>
<script type="text/javascript" src="inc/validate.js"></script>
<script type="text/javascript" src="inc/util.js"></script>
<script type="text/javascript" src="inc/jquery-ui-1.8.6.custom.min.js"></script>

<script type="text/javascript">

$(function(){
	var noticias = Array();
	var i = 0;

	$('#cerrarSesion').click(function(){
		var href = $(this).attr("href");
		$.ajax({
					type: "GET",
					url: href,
					success: function(data){
						$('#estado').html("");
						}
				});		
		return false});
	
	$('#iNoticia').hover(function(){ 
									$(this).animate({opacity: 1.0},'slow',function(){});
									$('#iBluetooth').animate({opacity: 0.55},'fast',function(){});
									$('#iCategoria').animate({opacity: 0.55},'fast',function(){});
									cargaMenu(1);}
									
						,function(){});
	$('#iBluetooth').hover(function(){
							$(this).animate({opacity: 1.0},'slow',function(){});
							$('#iCategoria').animate({opacity: 0.55},'fast',function(){});
							$('#iNoticia').animate({opacity: 0.55},'fast',function(){});
							cargaMenu(2);}
						,function(){});
	$('#iCategoria').hover(function(){
						$(this).animate({opacity: 1.0},'slow',function(){});
						$('#iBluetooth').animate({opacity: 0.55},'fast',function(){});
						$('#iNoticia').animate({opacity: 0.55},'fast',function(){});
						cargaMenu(3);}
						
						,function(){});

      $("#extruderLeft2").buildMbExtruder({
        position:"left",
        width:280,
        positionFixed:false,
        top:170,
        extruderOpacity:.8,
        onExtOpen:function(){},
        onExtContentLoad:function(){},
        onExtClose:function(){}
      });
	
	var options = { target: '#estado',
					beforeSend: cargando,
					success: function(){}
					};
																
	// Enlazamos el handler del submit 
	$('#formulario').submit(function() { 
		$(this).ajaxSubmit(options); 
		return false; 
	});
	
 });
</script>
  
</head>

<body>
<div class="container">
	<div class="header">
    	<div class="menu">
        	<div class="rootmenu">
              	<ul> 
                	<li id="iNoticia"> <a href="#"></a> </li>
                	<li id="iBluetooth"> <a href="#"></a> </li>
                    <li id="iCategoria"> <a href="#"></a> </li>
                </ul>        	
            </div>
            <div class="submenu">
            	<div class="nav">
                </div>
            </div>
        </div>
    </div>
    
	<div id="estado">
<?php
	if(isset($userp) && isset($passp) && !empty($userp) && !empty($passp)){
		showEstado($userp);
	}
?>
    </div>
    
    <div class="cont" id="central" style="position:relative;">

    </div>
	
    <div class="pie">
		<div class="izq">
    		<p>Diseñada y programada por Ángel Daniel Sanjuán Espejo, David Armenteros Escabias</p>
            <p><strong>Copyright ©2010 Blufeedme | Todos los derechos reservados.</strong></p>
    	</div>
   		 <div class="derch">
                <a href="http://validator.w3.org/check?uri=referer"><img
                  src="http://www.w3.org/Icons/valid-xhtml10"
                  alt="Valid XHTML 1.0 Strict" height="31" width="88" /></a>
                <a href="http://jigsaw.w3.org/css-validator/check/referer">
                    <img style="border:0;width:88px;height:31px"
                        src="http://jigsaw.w3.org/css-validator/images/vcss"
                        alt="¡CSS Válido!" />
        		</a>

    	</div>

    </div>    
</div>

<div id="extruderLeft2" class="{title:'LOGIN'}" >
   
    <div id="bloqueLogin">
	   <form id="formulario" action="login.php" method="post">   
    	    <p><label for="usuario"> Login
        	  	<span class="small">Introduce tu login</span>
        	</label>
        	<input class="campo" id="usuario" name="usuario" type="text" value="<?php echo $userp?>" /></p>         
        	<label for="password">Password
            	<span class="small">Introduce tu password</span>
 			</label>
  			<input class="campo" id="password" name="password" type="password" value="<?php echo $passp?>"/>

            <input class="boton" type="submit" value="Login"/>

      	</form>
	</div>
</div>

</body>
</html>
