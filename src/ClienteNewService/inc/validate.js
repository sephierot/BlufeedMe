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

// JavaScript Document
function validate(formData, jqForm, options) { 
	var operacion = $('input[name=operacion]').attr("value");
	var error = "";
	var form = jqForm[0];
		
	switch(operacion){
		case 'introducir':	
			var titulo = $('input[name=titulo]').attr("value"); 
    		var subtitulo = $('input[name=subtitulo]').attr("value");
			var autor =  $('input[name=autor]').attr("value");
			var categoria = $('select[name=categoria]').attr("value");
			var fechapubli = $('input[name=fechapubli]').attr("value");
			var fechacadu = $('input[name=fechacadu]').attr("value");
			var texto = $('textarea[name=texto]').val();

			form.titulo.style.border = 	"solid 1px #999";
			form.autor.style.border = 	"solid 1px #999";
			form.fechacadu.style.border = "solid 1px #999";
			form.fechapubli.style.border = "solid 1px #999";
			form.texto.style.border = 	"solid 1px #999";
			form.categoria.style.border = 	"solid 1px #999";	
						
			if(!titulo[0]){
				error = "Introduce el titulo"; 
				form.titulo.focus();
				form.titulo.style.border = "#F00 medium solid";
			}
			else if(!autor[0]){
				error = "Introduce el autor"; 
				form.autor.focus();
				form.autor.style.border = "#F00 medium solid";
			}
			else if(!fechapubli[0]){
				error = "Introduce una fecha de publicacion para la noticia";
				form.fechapubli.focus();
				form.fechapubli.style.border = "#F00 medium solid";
			}
			else if(!fechacadu[0]){
				error = "Introduce una fecha de caducidad para la noticia";
				form.fechacadu.focus();
				form.fechacadu.style.border = "#F00 medium solid";
			}
			else if(!texto[0]){
				error = "Introduce el texto"; 
				form.texto.focus();
				form.fechapubli.style.border = "#F00 medium solid";
			}
			else if(!categoria[0]){
				error = "Introduce la categoria"; 
				form.categoria.focus();
				form.categoria.style.border = "#F00 medium solid";
			}
		break;
		
		case 'modificar':
			var id_noticia = parseInt($('select[name=id_noticia]').attr("value"));
			var titulo = $('input[name=titulo]').attr("value");
    		var subtitulo = $('input[name=subtitulo]').attr("value");
			var autor =  $('input[name=autor]').attr("value");
			var categoria = $('select[name=categoria]').attr("value");
			var fechapubli = $('input[name=fechapubli]').attr("value");
			var fechacadu = $('input[name=fechacadu]').attr("value");
			var texto = $('textarea[name=texto]').val();
			
			form.id_noticia.style.border = 	"solid 1px #999";
			form.titulo.style.border = 	"solid 1px #999";
			form.autor.style.border = 	"solid 1px #999";
			form.fechacadu.style.border = "solid 1px #999";
			form.fechapubli.style.border = "solid 1px #999";
			form.texto.style.border = 	"solid 1px #999";
			form.categoria.style.border = 	"solid 1px #999";
	
			if(isNaN(id_noticia) || id_noticia <=0){
				error = "Introduce un id de noticia correcto (>0)";
				form.id_noticia.focus();	
				form.id_noticia.style.border = "#F00 medium solid";
			}
			else if(!titulo[0]){
				error = "Introduce un titulo"
				form.titulo.focus();	
				form.titulo.style.border = "#F00 medium solid";
			}
			else if(!autor[0]){
				error = "Introduce un autor";
				form.autor.focus();
				form.autor.style.border = "#F00 medium solid";
			}
			else if(!fechapubli[0]){
				error = "Introduce una fecha de publicacion para la noticia";
				form.fechapubli.focus();
			}
			else if(!fechacadu[0]){
				error = "Introduce una fecha de caducidad para la noticia";
				form.fechacadu.focus();
			}
			else if(!categoria[0]){
				error = "Introduce una categoria";
				form.categoria.focus();
				form.categoria.style.border = "#F00 medium solid";
			}
			else if(!texto[0]){
				error = "Introduce el texto de la noticia";
				form.texto.focus();	
				form.texto.style.border = "#F00 medium solid";
			}		
		break;
		
		case 'eliminar':
			var id_noticia = parseInt($('select[name=id_noticia]').attr("value"));

			form.id_noticia.style.border = 	"solid 1px #999";
						
			if(isNaN(id_noticia) || id_noticia <=0 ){
				error = "Introduce un id de noticia correcto (>0)";
				form.id_noticia.focus();	
				form.id_noticia.style.border = "#F00 medium solid";
			}		
		break;
		
		case 'introducirB':
			var mac = $('input[name=mac]').attr("value");
			var pin = $('input[name=pin]').attr("value");

			form.mac.style.border = "solid 1px #999";
			form.pin.style.border = "solid 1px #999";

			if(!mac[0]){
				error = "Introduce una MAC";
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}	
			else if(mac.match(/[^0-9^a-f^A-F]/)!= null || mac.length <12){
				error = "Introduce una MAC correcta (valor HEX sin separadores del tipo :,- de longitud 12 caracteres)";	
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}
			else if(!pin[0]){
				error = "Introduce un pin";
				form.pin.focus();
				form.pin.style.border = "#F00 medium solid";
			}	
			else if(pin.match(/[^0-9]/)!=null || pin.length > 7){
				error = "Introduce un pin correcto (cadena caracteres[0-9] ; de longitud menor o igual a 7)";
				form.pin.focus();   
				form.pin.style.border = "#F00 medium solid";   
      }

		break;
		
		case 'eliminarB':
			var mac = $('select[name=mac]').attr("value");
			form.mac.style.border = "solid 1px #999";

			if(!mac[0] || mac.match(/[^0-9^a-f^A-F]/)!= null){
				error = "Introduce una MAC correcta (sin separadores del tipo :,-)";
				form.mac.focus();	
				form.mac.style.border = "#F00 medium solid";
			}			
		break;
		
		case 'modificarB':
			var mac = $('select[name=mac]').attr("value");
			var macN = $('input[name=macN]').attr("value");
			var pinN = $('input[name=pinN]').attr("value");

			form.mac.style.border = "solid 1px #999";
			form.pinN.style.border = "solid 1px #999";
			form.macN.style.border = "solid 1px #999";
			
			if(!mac[0]){
				error = "Introduce una MAC";
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}	
			else if(mac.match(/[^0-9^a-f^A-F]/)!= null || mac.length <12){
				error = "Introduce una MAC correcta (valor HEX sin separadores del tipo :,- de longitud 12 caracteres)";	
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}
			else if(!macN[0]){
				error = "Introduce una nueva MAC";
				form.macN.focus();
				form.macN.style.border = "#F00 medium solid";
			}	
			else if(macN.match(/[^0-9^a-f^A-F]/)!= null || macN.length <12){
				error = "Introduce una nueva MAC correcta (valor HEX sin separadores del tipo :,- de longitud 12 caracteres)";	
				form.macN.focus();
				form.macN.style.border = "#F00 medium solid";
			}
			else if(!pinN[0]){
				error = "Introduce un nuevo pin";
				form.pinN.focus();
				form.pinN.style.border = "#F00 medium solid";
			}	
			else if(pinN.match(/[^0-9]/)!=null || pin.length > 7){
        		error = "Introduce un pin correcto (cadena caracteres[0-9] ; de longitud menor o igual a 7)";
				form.pinN.focus();     
				form.pinN.style.border = "#F00 medium solid";
      }
					
		break;
		
		case 'asociar':
		case 'desasociar':
			var mac = $('select[name=mac]').attr("value");
			var categoria = $('select[name=categoria]').attr("value");

			form.mac.style.border = "solid 1px #999";
			form.categoria.style.border = "solid 1px #999";
			
			if(!mac[0]){
				error = "Introduce una MAC";
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}	
			else if(mac.match(/[^0-9^a-f^A-F]/)!= null || mac.length <12){
				error = "Introduce una MAC correcta (valor HEX sin separadores del tipo :,- de longitud 12 caracteres)";	
				form.mac.focus();
				form.mac.style.border = "#F00 medium solid";
			}
			else if(!categoria[0]){
				error = "Introduce una categoria";
				form.categoria.focus();
				form.categoria.style.border = "#F00 medium solid";
			}							
		break;
		default:
		
		break;
	}
	
	if(error.length > 0){
		$("#mensaje").html('');
		$("#mensaje").append("<p><span class=\"ui-icon ui-icon-alert\" style=\"float: left; margin-right: .3em;\"></span><strong>Alert:</strong>" + error + "<p>");
		$("#mensaje").slideDown();
		return false;
	}
};


