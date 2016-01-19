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
-- Nombre: creaBD
-- Creado: 26/03/2010 14:47:55
-- Version 1.0: 
-- =============================================================================
-- Database: blufeedme_db
DROP DATABASE IF EXISTS blufeedme_db;
DROP TABLESPACE IF EXISTS "blufeedme_tablespace";
DROP ROLE IF EXISTS root_blufeedme_db;

CREATE ROLE root_blufeedme_db WITH
	SUPERUSER CREATEDB CREATEROLE INHERIT LOGIN
	ENCRYPTED PASSWORD '1234'
	CONNECTION LIMIT 0;
UPDATE pg_authid SET rolcatupdate = TRUE WHERE rolname = 'root_blufeedme_db';

CREATE TABLESPACE "blufeedme_tablespace" OWNER root_blufeedme_db LOCATION 'C:/blufeedme_tablespace';

CREATE DATABASE blufeedme_db
  WITH OWNER = root_blufeedme_db
       ENCODING = 'UTF8'
       LC_COLLATE = 'Spanish, Spain'
       LC_CTYPE = 'Spanish, Spain'
       CONNECTION LIMIT = -1
       TABLESPACE blufeedme_tablespace;


