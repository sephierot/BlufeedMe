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
-- Diagram Name: diseÃ±o_tablas
-- Created on: 25/03/2010 17:15:47
-- Diagram Version: 
-- =============================================================================

GRANT ALL ON SCHEMA public TO root_blufeedme_db;
ALTER SCHEMA public OWNER TO root_blufeedme_db;
REVOKE ALL ON SCHEMA public FROM postgres;
REVOKE ALL ON SCHEMA public FROM public;
     

CREATE TABLE Categoria(
	Id_categoria SERIAL NOT NULL,
	Nombre varchar(80) CONSTRAINT nombre_categoria_unico UNIQUE NOT NULL CONSTRAINT longitud_nombre_categoria CHECK(char_length(Nombre) > 2),
	Descripcion text,
	Id_gestor int4,
  
  PRIMARY KEY(Id_categoria)
)TABLESPACE blufeedme_tablespace;
ALTER TABLE Categoria OWNER TO root_blufeedme_db;


CREATE TABLE Noticia (
	Id_noticia SERIAL NOT NULL,
	Titulo varchar(80) NOT NULL CONSTRAINT longitud_titulo_noticia CHECK(char_length(Titulo) > 3),
	Subtitulo varchar(80),
	Autor varchar(80),
	Texto text NOT NULL,
	Fecha timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	Id_categoria int4 NOT NULL,
	firma varchar(300) CONSTRAINT firma_unica UNIQUE NOT NULL,
  
  PRIMARY KEY(Id_noticia)
)TABLESPACE blufeedme_tablespace;
ALTER TABLE Noticia OWNER TO root_blufeedme_db;


CREATE TABLE Gestor (
	Id_gestor SERIAL NOT NULL,
  Nombre varchar(20) CONSTRAINT nombre_gestor_unico UNIQUE NOT NULL CONSTRAINT longitud_nombre_gestor CHECK(char_length(Nombre) > 5),
	Contrasenia varchar(20) NOT NULL CONSTRAINT longitud_contrasenia_gestor CHECK(char_length(Contrasenia) > 5),
  
  PRIMARY KEY(Id_gestor)
)TABLESPACE blufeedme_tablespace;
ALTER TABLE Gestor OWNER TO root_blufeedme_db;

-- Table: adminpantalla

-- DROP TABLE adminpantalla;

CREATE TABLE adminpantalla
(
  id_administrador serial NOT NULL,
  nombre character varying(20) NOT NULL,
  contrasenia character varying(20) NOT NULL,
  CONSTRAINT cp_adminpantalla PRIMARY KEY (id_administrador),
  CONSTRAINT "nombreAdminUnico" UNIQUE (nombre)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE adminpantalla OWNER TO root_blufeedme_db;

-- Table: noticiapantalla

-- DROP TABLE noticiapantalla;

CREATE TABLE noticiapantalla
(
  id_noticia bigint NOT NULL,
  id_administrador integer NOT NULL,
  CONSTRAINT "Clave primaria noticiapantalla" PRIMARY KEY (id_noticia),
  CONSTRAINT adminpantalla FOREIGN KEY (id_administrador)
      REFERENCES adminpantalla (id_administrador) MATCH FULL
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT noticiapantalla FOREIGN KEY (id_noticia)
      REFERENCES noticia (id_noticia) MATCH FULL
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);

ALTER TABLE noticiapantalla OWNER TO root_blufeedme_db;

-- Index: fki_adminpantalla

-- DROP INDEX fki_adminpantalla;

CREATE INDEX fki_adminpantalla
  ON noticiapantalla
  USING btree
  (id_administrador);
  
--Explicacion de distintas configuraciones
-- Si se actualiza o borra una categoria se eliminan todas las noticias que sean de esa categoria(cascade)
-- Si se actualiza o borra el id de categoria da error porque dice que todavia dependen noticias de ella en otra tabla (No action)


ALTER TABLE Categoria ADD CONSTRAINT Ref_Categoria_a_Gestor FOREIGN KEY (Id_gestor)
	REFERENCES Gestor(Id_gestor)
	MATCH FULL
	ON DELETE SET NULL
	ON UPDATE CASCADE
	NOT DEFERRABLE;
-- Si eliman un gestor, las categorias controladas por este se ponen a null y no se borran (y por ende tampoco las noticias)
-- Si se actualiza el id de un gestor, las categorias pertenecientes a dicho gestor se modifican para que sigan apuntando al mismo gestor

ALTER TABLE Noticia ADD CONSTRAINT Ref_Noticia_a_Categoria FOREIGN KEY (Id_categoria)
	REFERENCES Categoria(Id_categoria)
	MATCH FULL
	ON DELETE CASCADE
	ON UPDATE CASCADE
	NOT DEFERRABLE;
-- Si se actualiza o borra una categoria se eliminan o modifican todas las noticias que sean de esa categoria(cascade)
  

