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
-- Tiempo de generación: 21-11-2010 a las 16:05:17
-- Versión del servidor: 5.1.41
-- Versión de PHP: 5.3.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `clientblufeedme_bd`
--

CREATE DATABASE `clientblufeedme_bd` DEFAULT CHARACTER SET utf8 COLLATE utf8_spanish_ci;
USE `clientblufeedme_bd`;
        
-- --------------------------------------------------------
CREATE USER 'cliente'@'localhost' IDENTIFIED BY '1234';

GRANT USAGE ON * . * TO 'cliente' IDENTIFIED BY '1234' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

GRANT ALL PRIVILEGES ON `clientblufeedme_bd` . * TO 'cliente';
-- --------------------------------------------------------
--
-- Estructura de tabla para la tabla `active_sessions`
--
-- Creación: 21-11-2010 a las 12:51:06
-- Última actualización: 21-11-2010 a las 12:51:06
--

DROP TABLE IF EXISTS `active_sessions`;
CREATE TABLE IF NOT EXISTS `active_sessions` (
  `sid` varchar(32) COLLATE utf8_spanish_ci NOT NULL DEFAULT '',
  `name` varchar(32) COLLATE utf8_spanish_ci NOT NULL DEFAULT '',
  `val` text COLLATE utf8_spanish_ci,
  `changed` varchar(14) COLLATE utf8_spanish_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`name`,`sid`),
  KEY `changed` (`changed`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci;

--
-- Volcar la base de datos para la tabla `active_sessions`
--


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
