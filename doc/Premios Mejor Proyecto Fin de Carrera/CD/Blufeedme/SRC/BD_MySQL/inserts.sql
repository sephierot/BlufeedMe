
--
-- Volcar la base de datos para la tabla `Asociacion`
--
DELETE FROM `Asociacion`;
INSERT INTO `Asociacion` (`id_dispositivo`, `id_categoria`) VALUES
(1, 1),
(1, 3),
(1, 4),
(1, 7),
(1, 11),
(2, 1),
(2, 2),
(2, 3),
(2, 6),
(2, 8),
(2, 9),
(2, 10),
(3, 1),
(3, 2),
(3, 3),
(3, 6),
(3, 9),
(4, 1),
(4, 2),
(4, 3),
(6, 1),
(6, 2),
(6, 6);



--
-- Volcar la base de datos para la tabla `Categoria`
--
DELETE FROM `Categoria`;
INSERT INTO `Categoria` (`id_categoria`, `nombre`, `descripcion`, `id_gestor`) VALUES
(1, 'UGR', 'Universidad de Granada -- www.ugr.es', 1),
(2, 'E.T.S.I.Informática', 'Escuela Técnica Superior Ingenierías Informatica y Telecomunicacion -- etsiit.ugr.es', 2),
(3, 'Secretaría E.T.S.I.', 'Secretaría de la Escuela Técnica Superior Ingenierías Informática y Telecomunicación -- etsiit.ugr.es', 2),
(4, 'LSI', 'Departamento de Lenguajes y Sistemas Informáticos -- lsi.ugr.es', 2),
(5, 'DECSAI', 'Ciencias de la computación e inteligencia artificial -- http://decsai.ugr.es', 2),
(6, 'TSTC', 'Teoría de la señal Telemática y comunicaciones -- tstc.ugr.es', 3),
(7, 'Álgebra', 'Departamento de Álgebra -- http://www.ugr.es/~algebra', 4),
(8, 'Análisis matemático', 'Departamento Análisis matemático -- http://www.ugr.es/local/dpto_am/', 5),
(9, 'ATC', 'Arquitectura y tecnología de computadores -- http://atc.ugr.es', 6),
(10, 'Derecho civil', 'Derecho civil -- http://www.ugr.es/local/dpto_dc/', 7),
(11, 'Electrónica', 'Electrónica y tecnología de computadores -- http://electronica.ugr.es/', 7);


--
-- Volcar la base de datos para la tabla `Dispositivo`
--
DELETE FROM `Dispositivo`;
INSERT INTO `Dispositivo` (`id_dispositivo`, `mac`, `pin`, `url`) VALUES
(1, '0023AFCD2F29', '1111', NULL),
(2, '4C5499064A4D', '2222', NULL),
(3, '2021A53DDB9C', '3333', NULL),
(4, '0024904D3CB1', '5555', NULL),
(5, '00265F071CF3', '0000', NULL),
(6, 'B8F9346A27D3', '0000', NULL);

-- --------------------------------------------------------



--
-- Volcar la base de datos para la tabla `Gestor`
--
DELETE FROM `Gestor`
INSERT INTO `Gestor` (`id_gestor`, `nombre`, `contrasenia`) VALUES
(1, 'gestor1', 'c_gestor1'),
(2, 'gestor2', 'c_gestor2'),
(3, 'gestor3', 'c_gestor3'),
(4, 'gestor4', 'c_gestor4'),
(5, 'gestor5', 'c_gestor5'),
(6, 'gestor6', 'c_gestor6'),
(7, 'gestor7', 'c_gestor7');

-- --------------------------------------------------------


--
-- Volcar la base de datos para la tabla `Noticia`
--


DELETE FROM `Noticia`;
INSERT INTO `Noticia` (`id_noticia`, `titulo`, `subtitulo`, `autor`, `texto`, `fecha`, `fecha_publi`, `fecha_caducidad`, `id_categoria`, `firma`) VALUES
(1, 'Solicitud de adaptación a los nuevos grados', 'Subtitulo de la noticia', 'Secretaría', 'Se ha abierto un plazo preferente para solicitar la adaptación a los nuevos grados hasta el 29 de Julio de 2010. Los alumnos de este centro interesados pueden iniciar el trámite a través de la siguiente aplicación: https://etsiit.ugr.es/app/static/SolicitadorDeCambioAGrado E-mail de contacto: jbernier@ugr.es', '2010-11-08 23:26:51', '2010-11-10 23:26:39', '2011-12-16 23:26:56', 1, 'asdfasdfa'),
(2, 'INTEL-EUROPA: Hands on Digital Prototyping - Summer School -', 'Subtítulo de la noticia', 'Pedro Jiménez', 'The summer school comprehends lectures of interrelated lessons, i. e. FPGA, Rapid Prototyping and Embedded Systems, which jointly present a detailed and actual overview on the addressed subject, i. e. Rapid Prototyping.  The lectures also give an outlook on Wireless Sensor Networks and energy harvesting applications. The educational focus is aimed at Master students and/or PhD students in Electrical Engineering, Physics, Information Technology, Computer Science, or Applied Materials Research. Basic VHDL skills will be beneficial, but are not mandatory. The summer school is optional to the regular lectures presented at the UGR. The teaching units include lectures, labs and exercises. There will be a written test at the end.\r\n\r\n\r\nStudents being interested to join our summer school in Granada are obliged to submit an application to the local organizer at the UGR in Granada.\r\n\r\n\r\nSubmission Deadline: September 24th, 2010\r\n\r\n\r\nThe number of participants is restricted to 20 students; the organizers will make a selection from incoming applications. The students selected as participants will be informed by September 26th 2010.\r\n\r\n\r\nIdioma: Español e inglés\r\n\r\n\r\nFechas del curso: 27/09/2010 ? 1/10/2010\r\n\r\nLugar de Celebración: ETSIIT\r\n\r\nProfesorado: Universidad de Munster (Alemania), Universidad de Granada\r\n\r\nPrecio: Gratuito', '2010-11-08 23:29:55', '2010-11-09 23:30:01', '2011-11-12 23:30:06', 2, 'qweuioqwe'),
(3, 'Adaptación a los nuevos grados desde las titulaciones actuales', 'El próximo 13 de Julio se abre el plazo de solicitud a los nuevos grados', 'jbernier', 'El próximo 13 de Julio se abre el plazo de solicitud de adaptación a los nuevos grados para alumnos matriculados en los títulos a extinguir.\r\n\r\nMás información en http://etsiit.ugr.es/page.php?pageid=adaptaciones\r\nE-mail de contacto: etsiitweb@ugr.es', '2010-11-01 23:35:48', '2010-11-09 23:35:54', '2011-11-25 23:36:03', 3, 'dffasdfadfafff'),
(4, 'Charla: "Empresas e Ingeniería del Software"', 'Subtítulo de la noticia', 'jlobillo', 'Charla: "Empresas e Ingeniería del Software"\r\nPonente: Germán Sánchez Mariscal. Antiguo profesor de ETSIIT. Empresa UNIT4\r\nViernes día 30 de Abril a las 12 horas.\r\nAula 0.2 de la ETSIIT\r\n\r\nTemas de los que hablará:\r\n\r\n- Uso de herramientas de Ingeniería del Software en la empresa TIC.\r\n- Estimación temporal y económica.\r\n- Gestión de Recursos Humanos y entrevistas de trabajo.\r\n- Formación de los Ingenieros Informáticos y el mercado laboral.\r\n\r\nEn la charla se podrán hacer preguntas sobre el trabajo del informático en la empresa.', '2010-11-07 23:42:22', '2010-11-08 23:42:15', '2011-11-09 23:41:58', 3, 'fadfffffadseetrav'),
(5, 'Programación Web 2.0: Desarrollo Rápido de Aplicaciones con Python y Django (5a ', 'Subtítulo de la noticia', 'jbernier', 'Tipo de curso:Centro de Formación Contínua\r\nCurso académico:2009/2010\r\nDirector:Jose Luis Bernier Villamor\r\nCoordinador:Antonio Cañas Vargas\r\nProfesores:Francisco Javier Nievas Muñoz (GARASA Esñeco)\r\nMiguel HernándezMartos (Integromics S.L.)\r\nDepartamento que lo propone:Arquitectura y Tecnología de Computadores\r\nFecha de inicio:20-09-2010\r\nFecha de fin:29-09-2010\r\nDuración (en horas):40\r\nHorario:16:00 a 21:00\r\nAula:2.6\r\nNúm. de plazas:25\r\ne-mail:jbernier@atc.ugr.es\r\nPágina web:http://oficinavirtual.ugr.es/apli/posgrado/detalle_cep.jsp?cod=10/CA/047\r\nCréditos para Ingeniería Informática:4\r\nCréditos para Ing. Téc. Inf. de Sistemas:4\r\nCréditos para Ing. Téc. Inf. de Gestión:4\r\nCréditos para Ing. de Telecomunicaciones:3\r\nNotas/Observaciones:Contenido académico\r\n\r\n    * Introduccion.\r\n    * Instalacion de Python y Django.\r\n    * Python: Sintaxis y tipos de datos basicos.\r\n    * Python: Funciones y modulos.\r\n    * Python: Objetos y clases (POO). Introspeccion.\r\n    * Django: Modelo de desarrollo de aplicaciones.\r\n    * Django: Bases de datos y mapeador objeto/relacional.\r\n    * Django: Separacion de codigo y presentacion: vistas y plantillas.\r\n    * Django: Interfaz de administracion.\r\n    * Django: Vistas genericas y formularios.\r\n    * Django: Cache de contenidos, manejo de sesiones y autentificacion.\r\n    * Javascript: Javascript avanzado.\r\n    * Javascript: Ajax y comunicaciones asincronas.\r\n    * Javascript: Componentes UI.\r\n    * Javascript: Aplicaciones de ejemplo.\r\n\r\n\r\nEste curso NO es una iniciación a la tecnología web sino que requiere que los alumnos matriculados ya tengan conocimientos previos de HTML y programación dirigida a objetos. En caso contrario, se recomienda cursar previamente "Programación Web 2.0: Servicios Interactivos con PHP y MySQL"', '2010-11-08 23:47:00', '2011-11-12 23:47:07', '2010-11-23 23:47:25', 6, 'jvmxmchbk'),
(6, 'Solicitud de adaptación a los nuevos grados', 'Subtítulo de la noticia', 'dep. atc', 'Se ha abierto un plazo preferente para solicitar la adaptación a los nuevos grados hasta el 29 de Julio de 2010. Los alumnos de este centro interesados pueden iniciar el trámite a través de la siguiente aplicación: https://etsiit.ugr.es/app/static/SolicitadorDeCambioAGrado E-mail de contacto: jbernier@ugr.es', '2010-11-11 00:23:19', '2010-11-15 00:05:27', '2011-11-24 00:05:33', 6, 'ysfasfasfgggd');


-- --------------------------------------------------------