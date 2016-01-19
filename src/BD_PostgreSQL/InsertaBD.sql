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

-- =============================================================================
-- Nombre: InsertaDatosBD
-- Creado: 12/07/2010 12:18:50
-- Version 1.0: 
-- =============================================================================
-- Database: blufeedme_db

DELETE FROM gestor;
DELETE FROM categoria;
DELETE FROM noticia;

INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor1','c_gestor1');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor2','c_gestor2');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor3','c_gestor3');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor4','c_gestor4');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor5','c_gestor5');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor6','c_gestor6');
INSERT INTO gestor(nombre,contrasenia) VALUES ('gestor7','c_gestor7');


INSERT INTO adminpantalla(nombre,contrasenia) VALUES ('amdin1','c_admin1');

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('UGR', 'Universidad de Granada -- www.ugr.es', 1);
                                                                    
INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('E.T.S.I.Informática', 'Escuela Técnica Superior Ingenierías Informatica y Telecomunicacion -- etsiit.ugr.es', 1);
    
INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('Secretaría E.T.S.I.', 'Secretaría de la Escuela Técnica Superior Ingenierías Informática y Telecomunicación -- etsiit.ugr.es', 1);    

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('LSI', 'Departamento de Lenguajes y Sistemas Informáticos -- lsi.ugr.es', 2);

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('DECSAI', 'Ciencias de la computación e inteligencia artificial -- http://decsai.ugr.es', 2);

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('TSTC', 'Teoría de la señal Telemática y comunicaciones -- tstc.ugr.es', 3);

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('Álgebra', 'Departamento de Álgebra -- http://www.ugr.es/~algebra', 4);

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('Análisis matemático','Departamento Análisis matemático -- http://www.ugr.es/local/dpto_am/', 5);

INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('ATC', 'Arquitectura y tecnología de computadores -- http://atc.ugr.es', 6);
INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('Derecho civil', 'Derecho civil -- http://www.ugr.es/local/dpto_dc/', 7);
INSERT INTO categoria(nombre, descripcion, id_gestor)
    VALUES ('Electrónica', 'Electrónica y tecnología de computadores -- http://electronica.ugr.es/', 7);