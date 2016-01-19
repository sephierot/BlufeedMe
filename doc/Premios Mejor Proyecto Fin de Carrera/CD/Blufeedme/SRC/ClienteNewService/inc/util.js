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
var nav = -1;

function ReplaceEnters(texto, conbr) {
 if (conbr) {
 pat = new RegExp(String.fromCharCode(13),"g")
 pat2 = new RegExp(String.fromCharCode(10),"g")
  texto = texto.replace(pat, "");
   texto = texto.replace(pat2, "<br>");
 } else {
  texto = texto.replace(/<br>/g, String.fromCharCode(13));
 }
 return texto
}

function showResponse(){
	$('#accordion').accordion();
}

function cargando(){
	$('#central').html("<div id=\"loading\">  <img src=\"images/cargando.gif\"/>  </div>");
}
function asignarHandlerEnlaces(){
	$('.entradamenu a').each(function(){
		var href = $(this).attr("href");                    //Guardamos el valor de title de cada enlace.
		$(this).click(function(){	
				$.ajax({
					type: "GET",
					url: href,
					beforeSend: cargando,
					success: function(data){
						$('#central').html(data);
						
						$('#datepickerP').datepicker();
						$('#datepickerC').datepicker();
						$('#datepickerP').datepicker("option", "dateFormat", "yy-mm-dd");
						$('#datepickerC').datepicker("option", "dateFormat", "yy-mm-dd");
						
						var options = { target: '#central',
										beforeSubmit: validate,
										success: showResponse};
																
						// Enlazamos el handler del submit 
						$('#form').submit(function() { 
							$(this).ajaxSubmit(options); 
							return false; 
						}); 
						
						//Enlazamos el handler del combo box de id_noticia
						$('#idM').change(function(){
							var id = $('select option:selected').val();
							var n;
							if(id != 0){
								for(n in noticias){
									if(noticias[n].id == id){ 
										$('#t1').val(noticias[n].titulo);
										$('#t2').val(noticias[n].subtitulo);
										$('#t3').val(noticias[n].autor);
										$('#datepickerP').val(noticias[n].fechaPubli);
										$('#datepickerC').val(noticias[n].fechaCaducidad);
										$('#t4').attr("value",noticias[n].categoria);
										$('#t5').val(noticias[n].texto);
										break;
									}
								}
								
							}
							else{
								$('#t1').val("");
								$('#t2').val("");
								$('#t3').val("");
								$('#datepickerP').val("");
								$('#datepickerC').val("");
								$('#t4').attr("value","");
								$('#t5').val("");
							}
							
						});

						//Enlazamos el handler del combo box de id_noticia
						$('#idE').change(function(){
							var id = $('select option:selected').val();
							var n;
							if(id != 0){
								for(n in noticias){
									if(noticias[n].id == id){ 
										var html = '<h3>' + noticias[n].titulo + ' (ID: ' + noticias[n].id + ')</h3> <h5> ' + noticias[n].subtitulo  + ' ( ' + noticias[n].categoria  + ' )</h5></a></h1>' +
										 '<div>' +
											'<div class = "subheader">' + 
												'<label style="float:left"> Autor: ' + noticias[n].autor + ' </label>' +
												'<label style="float:right"> Fecha publicación:'+ noticias[n].fechaPubli + 
											'</div>' +
											'<div>' +
												'<p>' + ReplaceEnters(noticias[n].texto,true) + '</p>' +
											'</div>' +
									 '</div>';

										$('#info').html(html);
										break;
									}
								}	
							}
							else{
								$('#info').html("");
							}
							
						});
		
					}
				});
						
				return false;
		});
	});	
	$('.entradamenuL a').each(function(){
		var href = $(this).attr("href");                    //Guardamos el valor de title de cada enlace.
		$(this).click(function(){
				$.ajax({
					type: "GET",
					url: href,
					beforeSend: cargando,
					success: function(data){
						$('#central').html(data);
						
						var options = { target: '#central',
										beforeSubmit: validate,
										success: showResponse};
						
						$('#datepickerP').datepicker();
						$('#datepickerC').datepicker();
						$('#datepickerP').datepicker( "option", "dateFormat", "yy-mm-dd");
						$('#datepickerC').datepicker( "option", "dateFormat", "yy-mm-dd");
						
						// Enlazamos el handler del submit 
						$('#form').submit(function() { 
							$(this).ajaxSubmit(options); 
							return false; 
						}); 
												
						
						$('#macd').change(function(){
							var mac = $('select option:selected').val();
							
							if(mac.length==12){
								for(d in dispositivos){
									if(dispositivos[d].mac.toUpperCase() == mac.toUpperCase()){
										//borramos las entradas del combo box de las categorias
										$('#catd').children().remove();
										
										var i;
										$('#catd').append('<option value=""> </option>');
										for(i=0; i<dispositivos[d].categorias.length; i++){
											$('#catd').append('<option value="' + dispositivos[d].categorias[i].nombre 
																+ '">' + dispositivos[d].categorias[i].nombre + '</option>');
										}
										break;	
									}
								
								}
							}					
						});

					}
				});
				
				return false;
		});
	});
};


function cargaMenu(opcion){
	if(nav != opcion){
		var url = "";
		switch(opcion){
			case 1:
				url = 'menuN.html';
			break;
			case 2:
				url = 'menuB.html';
			break;
			case 3:
				url = 'menuC.html';
			break;
		};

		$('.nav').fadeOut('fast',function(){
				$(this).load(url,function(){
						$(this).fadeIn('slow');
						//$('#central').html("");
						nav = opcion;
						asignarHandlerEnlaces();									
				});		
		});
	}
};

function noticia(id,titulo,subtitulo,autor,fechaPubli,fechaCaducidad, texto, Categoria){
	this.id = new Number(id);
	this.titulo = new String(titulo);
	this.subtitulo = new String(subtitulo);
	this.autor = new String(autor);
	this.fechaPubli = new String(fechaPubli);
	this.fechaCaducidad = new String(fechaCaducidad);
	this.texto = new String(texto);
	this.categoria = new String(Categoria);
};

function categoria(nombre){
	this.nombre = new String(nombre);
};

function dispositivo(id,mac,categorias){
	this.id = new Number(id);
	this.mac = new String(mac);
//	this.categorias = new Number(categorias.length);
	this.categorias = new Array(categorias.length);
	
	var i;
	for(i = 0 ; i<categorias.length ; i++){
		this.categorias[i] = new categoria(categorias[i].nombre);
	}
}