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

-- phpMyAdmin SQL Dump
-- version 3.2.4
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 15-10-2010 a las 13:16:45
-- Versión del servidor: 5.1.41
-- Versión de PHP: 5.3.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `Blufeedme_DB`
--

DROP DATABASE `Blufeedme_DB`;

CREATE DATABASE `Blufeedme_DB` DEFAULT CHARACTER SET utf8 COLLATE utf8_spanish_ci;
USE `Blufeedme_DB`;

-- Borrando 'root_blufeedme'@'localhost' ...
DROP USER 'root_blufeedme'@'localhost';

CREATE USER 'root_blufeedme'@'localhost' IDENTIFIED BY '1234';

GRANT USAGE ON * . * TO 'root_blufeedme' IDENTIFIED BY '1234' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

GRANT ALL PRIVILEGES ON `Blufeedme_DB` . * TO 'root_blufeedme';
-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Asociacion`
--
-- Creación: 15-10-2010 a las 13:04:02
--

DROP TABLE IF EXISTS `Asociacion`;
CREATE TABLE IF NOT EXISTS `Asociacion` (
  `id_dispositivo` bigint(20) unsigned NOT NULL,
  `id_categoria` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_dispositivo`,`id_categoria`),
  KEY `id_dispositivo` (`id_dispositivo`),
  KEY `id_categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Categoria`
--
-- Creación: 14-10-2010 a las 12:47:29
--

DROP TABLE IF EXISTS `Categoria`;
CREATE TABLE IF NOT EXISTS `Categoria` (
  `id_categoria` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) COLLATE utf8_spanish_ci NOT NULL,
  `descripcion` text COLLATE utf8_spanish_ci,
  `id_gestor` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `id_categoria` (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`),
  KEY `id_gestor` (`id_gestor`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=12 ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Dispositivo`
--
-- Creación: 14-10-2010 a las 12:51:18
--

DROP TABLE IF EXISTS `Dispositivo`;
CREATE TABLE IF NOT EXISTS `Dispositivo` (
  `id_dispositivo` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `mac` varchar(12) COLLATE utf8_spanish_ci NOT NULL,
  `pin` varchar(7) COLLATE utf8_spanish_ci NOT NULL,
  `url` varchar(200) COLLATE utf8_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id_dispositivo`),
  UNIQUE KEY `mac` (`mac`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=3 ;

--
-- Estructura de tabla para la tabla `Gestor`
--
-- Creación: 14-10-2010 a las 12:47:29
--

DROP TABLE IF EXISTS `Gestor`;
CREATE TABLE IF NOT EXISTS `Gestor` (
  `id_gestor` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  `contrasenia` varchar(20) COLLATE utf8_spanish_ci NOT NULL,
  PRIMARY KEY (`id_gestor`),
  UNIQUE KEY `id_gestor` (`id_gestor`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=8 ;


--
-- Estructura de tabla para la tabla `Historial`
--
-- Creación: 06-11-2010 a las 12:57:24
--

DROP TABLE IF EXISTS `Historial`;
CREATE TABLE IF NOT EXISTS `Historial` (
  `id_dispositivo` bigint(20) unsigned NOT NULL,
  `id_noticia` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id_dispositivo`,`id_noticia`),
  KEY `id_noticia` (`id_noticia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcar la base de datos para la tabla `Historial`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Noticia`
--
-- Creación: 14-10-2010 a las 12:47:30
--

DROP TABLE IF EXISTS `Noticia`;
CREATE TABLE IF NOT EXISTS `Noticia` (
  `id_noticia` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `titulo` varchar(100) COLLATE utf8_spanish_ci NOT NULL,
  `subtitulo` varchar(100) COLLATE utf8_spanish_ci DEFAULT NULL,
  `autor` varchar(80) COLLATE utf8_spanish_ci NOT NULL,
  `texto` text COLLATE utf8_spanish_ci NOT NULL,
  `fecha` datetime NOT NULL,
  `fecha_publi` datetime NOT NULL,
  `fecha_caducidad` datetime NOT NULL,
  `id_categoria` bigint(20) unsigned NOT NULL,
  `firma` varchar(200) COLLATE utf8_spanish_ci NOT NULL,
  PRIMARY KEY (`id_noticia`),
  UNIQUE KEY `id_noticia` (`id_noticia`),
  UNIQUE KEY `firma` (`firma`), 
  KEY `id_categoria` (`id_categoria`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci AUTO_INCREMENT=3 ;

--
-- Filtros para las tablas descargadas (dump)
--

--
-- Filtros para la tabla `Asociacion`
--
ALTER TABLE `Asociacion`
  ADD CONSTRAINT `Asociacion_ibfk_2` FOREIGN KEY (`id_categoria`) REFERENCES `Categoria` (`id_categoria`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Asociacion_ibfk_1` FOREIGN KEY (`id_dispositivo`) REFERENCES `Dispositivo` (`id_dispositivo`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `Categoria`
--
ALTER TABLE `Categoria`
  ADD CONSTRAINT `Categoria_ibfk_1` FOREIGN KEY (`id_gestor`) REFERENCES `Gestor` (`id_gestor`) ON DELETE CASCADE;

--
-- Filtros para la tabla `Historial`
--
ALTER TABLE `Historial`
  ADD CONSTRAINT `Historial_ibfk_2` FOREIGN KEY (`id_noticia`) REFERENCES `Noticia` (`id_noticia`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Historial_ibfk_1` FOREIGN KEY (`id_dispositivo`) REFERENCES `Dispositivo` (`id_dispositivo`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `Noticia`
--
ALTER TABLE `Noticia`
  ADD CONSTRAINT `Noticia_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `Categoria` (`id_categoria`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
