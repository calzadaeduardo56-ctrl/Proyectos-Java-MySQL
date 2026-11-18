-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: heldes
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'b567f38e-beb0-11f0-a168-7cd30a744263:1-4397';

--
-- Table structure for table `agenda`
--

DROP TABLE IF EXISTS `agenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agenda` (
  `idAgenda` int NOT NULL,
  `Nombre_Cliente` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `Fecha` varchar(45) DEFAULT NULL,
  `Horarios` float DEFAULT NULL,
  `Disponibilidad` varchar(45) DEFAULT NULL,
  `Tipo` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idAgenda`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agenda`
--

LOCK TABLES `agenda` WRITE;
/*!40000 ALTER TABLE `agenda` DISABLE KEYS */;
INSERT INTO `agenda` VALUES (1,'Luis','5567899','2025-03-10',9.1,'Estoy disponible','Personal'),(2,'Ricardo','5567899','2025-03-10',9.1,'No estoy disponible','Empresarial'),(3,'Luisa','5567899','2025-03-10',9.1,'Estoy disponible','Empresarial'),(4,'Araceli','5567899','2025-03-10',9.1,'No estoy disponible','Empresarial'),(5,'Eduardo','5567899','2025-03-10',9.11,'Estoy disponible','Empresarial'),(6,'Carlos','55678900','2025-03-19',2.3,'No estoy disponible','Empresarial'),(9,'Eduardo','5567899','2025-03-10',9.11,'Estoy disponible','Empresarial'),(10,'Karla','566666','2025-03-20',9.1,'Estoy disponible','Personal'),(11,'Eduardo','5567899','2025-03-10',9.11,'No estoy disponible','Empresarial'),(12,'Eduardo','5567899','2025-03-10',9.11,'No estoy disponible','Empresarial'),(13,'Eduardo','5567899','2025-03-10',9.11,'No estoy disponible','Personal'),(14,'Carlos','566666','2025-03-20',9.11,'Estoy disponible','Personal'),(15,'Carlos','55678900','2025-03-19',2.3,'Estoy disponible','Empresarial');
/*!40000 ALTER TABLE `agenda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alta_ticket`
--

DROP TABLE IF EXISTS `alta_ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alta_ticket` (
  `Id_Ticket` int NOT NULL,
  `Nombre_Cliente` varchar(45) DEFAULT NULL,
  `Tipo_Poliza` varchar(45) DEFAULT NULL,
  `Nombre_Problematica` varchar(100) DEFAULT NULL,
  `Nombre_Producto` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Ticket`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alta_ticket`
--

LOCK TABLES `alta_ticket` WRITE;
/*!40000 ALTER TABLE `alta_ticket` DISABLE KEYS */;
/*!40000 ALTER TABLE `alta_ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asignacion`
--

DROP TABLE IF EXISTS `asignacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignacion` (
  `Id_Asignacion` int NOT NULL,
  `Nombre_Cliente` varchar(45) DEFAULT NULL,
  `Nombre_Empleado` varchar(45) DEFAULT NULL,
  `Fecha_Asignacion` date DEFAULT NULL,
  `Hora_Asignacion` time DEFAULT NULL,
  `Prioridad` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Asignacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asignacion`
--

LOCK TABLES `asignacion` WRITE;
/*!40000 ALTER TABLE `asignacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `asignacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `idasistencia` int NOT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `Num_Etapas` varchar(45) DEFAULT NULL,
  `Actividades` varchar(45) DEFAULT NULL,
  `Responsable` varchar(45) DEFAULT NULL,
  `Empleado` varchar(45) DEFAULT NULL,
  `Empresa` varchar(45) DEFAULT NULL,
  `Fecha_Inicial` date DEFAULT NULL,
  `Fecha_Final` date DEFAULT NULL,
  `Fecha_Real` date DEFAULT NULL,
  `Dias_Retraso` int DEFAULT NULL,
  `Monitoreo` varchar(45) DEFAULT NULL,
  `Observaciones` varchar(45) DEFAULT NULL,
  `Opcion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idasistencia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `IdCliente` int NOT NULL,
  `Empresa` varchar(45) DEFAULT NULL,
  `Sucursal` varchar(45) DEFAULT NULL,
  `Cliente_1` varchar(45) DEFAULT NULL,
  `Cliente_2` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `Correo` varchar(45) DEFAULT NULL,
  `Direccion` varchar(100) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdCliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados` (
  `Id_Empleado` int NOT NULL,
  `Nombre` varchar(45) DEFAULT NULL,
  `Puesto` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `Correo` varchar(45) DEFAULT NULL,
  `Direccion` varchar(45) DEFAULT NULL,
  `Tiempo_Laboral` varchar(45) DEFAULT NULL,
  `Tipo` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Empleado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finanzas`
--

DROP TABLE IF EXISTS `finanzas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `finanzas` (
  `idasistencia` int NOT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `Num_Etapas` int DEFAULT NULL,
  `Actividades` varchar(45) DEFAULT NULL,
  `Empleado` varchar(45) DEFAULT NULL,
  `Empresa` varchar(45) DEFAULT NULL,
  `Dias_Retraso` int DEFAULT NULL,
  `Monitoreo` varchar(45) DEFAULT NULL,
  `Costo_1` decimal(10,2) DEFAULT NULL,
  `Costo_2` decimal(10,2) DEFAULT NULL,
  `Costo_3` decimal(10,2) DEFAULT NULL,
  `Total` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`idasistencia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finanzas`
--

LOCK TABLES `finanzas` WRITE;
/*!40000 ALTER TABLE `finanzas` DISABLE KEYS */;
/*!40000 ALTER TABLE `finanzas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gastos`
--

DROP TABLE IF EXISTS `gastos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gastos` (
  `IdGasto` int NOT NULL,
  `NombreGastos` varchar(45) COLLATE utf8mb4_general_ci NOT NULL,
  `Status` varchar(45) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`IdGasto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gastos`
--

LOCK TABLES `gastos` WRITE;
/*!40000 ALTER TABLE `gastos` DISABLE KEYS */;
/*!40000 ALTER TABLE `gastos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gastos2`
--

DROP TABLE IF EXISTS `gastos2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gastos2` (
  `idasistencia` int NOT NULL,
  `Producto` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Num_Etapas` int DEFAULT NULL,
  `Actividades` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Empresa` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Gasto_1` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Costo_1` decimal(10,2) DEFAULT NULL,
  `Gasto_2` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Costo_2` decimal(10,2) DEFAULT NULL,
  `Gasto_3` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Costo_3` decimal(10,2) DEFAULT NULL,
  `Gasto_4` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `Costo_4` decimal(10,2) DEFAULT NULL,
  `Total` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`idasistencia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gastos2`
--

LOCK TABLES `gastos2` WRITE;
/*!40000 ALTER TABLE `gastos2` DISABLE KEYS */;
/*!40000 ALTER TABLE `gastos2` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `implementacion`
--

DROP TABLE IF EXISTS `implementacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `implementacion` (
  `idimplementacion` int NOT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `Num_Etapas` varchar(45) DEFAULT NULL,
  `Actividades` varchar(45) DEFAULT NULL,
  `Responsable` varchar(45) DEFAULT NULL,
  `Empleado` varchar(45) DEFAULT NULL,
  `Dias_Actividad` varchar(45) DEFAULT NULL,
  `Gastos` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  `Observaciones` varchar(45) DEFAULT NULL,
  `Tipo` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idimplementacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `implementacion`
--

LOCK TABLES `implementacion` WRITE;
/*!40000 ALTER TABLE `implementacion` DISABLE KEYS */;
INSERT INTO `implementacion` VALUES (1,'Vendette','Etapa Trece','Junta inicial','Digisoft','Carlos','8','0','Inactivo','ninguna','GYM'),(2,'Control de Asistencia','Etapa Dos','Instalacion','Digisoft','Eduardo','3','0','Activo','ninguna','Comedores'),(3,'Control de Asistencia','Etapa Tres','Layout','Digisoft','Omar','3','0','Activo','ninguna','Restaurante'),(4,'Control de Asistencia','Etapa Cuatro','Turnos Horarios','Conjunto','Eduardo','3','0','Activo','ninguna','GYM'),(5,'Control de Asistencia','Etapa Cinco','Periodos Catalogos','Digisoft','Carlos','2','0','Activo','ninguna','Vendette'),(6,'Control de Asistencia','Etapa Seis','Uso lector','Conjunto','Eduardo','1','0','Activo','ninguna','GYM'),(7,'Control de Asistencia','Etapa Siete','Instala Lector','Digisoft','Omar','1','0','Activo','ninguna','Asistencia'),(8,'Control de Asistencia','Etapa Uno','Junta inicial','Conjunto','Omar','1','0','Activo','ninguna','GYM'),(9,'Control de Asistencia','Etapa Nueve','Reportes','Digisoft','Omar','2','0','Activo','ninguna','Vendette'),(10,'Control de Asistencia','Etapa Diez','Web','Digisoft','Eduardo','1','0','Activo','ninguna','Asistencia'),(11,'Control de Asistencia','Etapa Once','Junta inicial','Digisoft','Omar','3','0','Activo','ninguna','Comedores'),(12,'Control de Asistencia','Etapa Once','Junta inicial','Digisoft','Omar','3','0','Activo','ninguna','Restaurante');
/*!40000 ALTER TABLE `implementacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monitoreo`
--

DROP TABLE IF EXISTS `monitoreo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monitoreo` (
  `Id_ticket` int NOT NULL,
  `Fecha` date DEFAULT NULL,
  `Dias_Retraso` int DEFAULT NULL,
  `Empresa` varchar(45) DEFAULT NULL,
  `Empleado` varchar(45) DEFAULT NULL,
  `Prioridad` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_ticket`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monitoreo`
--

LOCK TABLES `monitoreo` WRITE;
/*!40000 ALTER TABLE `monitoreo` DISABLE KEYS */;
INSERT INTO `monitoreo` VALUES (1,'2025-06-20',3,'Barcardi','Armando','Baja'),(2,'2025-04-22',62,'Yakult','Karla','Alta'),(3,'2025-06-16',7,'Barcardi','Eduardo','Media'),(4,'2025-04-21',63,'Barcardi','Eduardo','Alta'),(6,'2025-05-29',25,'Secretaria de Comunicaciones','Armando','Alta'),(7,'2025-05-20',34,'Secretaria de Comunicaciones','Omar','Alta'),(8,'2025-05-15',39,'YAMILA\'S GARDEN','Kimberly','Alta'),(10,'2025-04-21',63,'Secretaria de Comunicaciones','Karla','Alta'),(11,'2025-05-11',43,'YAMILA\'S GARDEN','Omar','Alta'),(12,'2025-05-22',32,'INTEC','Armando','Alta'),(13,NULL,0,'Barcardi','Omar',''),(14,NULL,0,'Bacardi','',''),(17,'2025-05-20',0,'INTEC','Omar Desarrollo','Ninguna'),(18,NULL,0,'INTEC','Eduardo Desarrollo',''),(19,NULL,0,'Barcardi','Eduardo Desarrollo',''),(20,NULL,0,'Barcardi','Omar Desarrollo',''),(21,NULL,0,'Barcardi','Carlos Lider del Proyecto',''),(22,NULL,0,'Barcardi','Eduardo Desarrollo',''),(23,NULL,0,'Barcardi','Carlos Lider del Proyecto',''),(24,NULL,0,'Barcardi','','');
/*!40000 ALTER TABLE `monitoreo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfilado`
--

DROP TABLE IF EXISTS `perfilado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfilado` (
  `Id_Empleado` int NOT NULL,
  `Nombre` varchar(45) DEFAULT NULL,
  `Puesto` varchar(45) DEFAULT NULL,
  `Empresa` varchar(45) DEFAULT NULL,
  `Usuario` varchar(45) DEFAULT NULL,
  `Contraseña` varchar(45) DEFAULT NULL,
  `Modulo` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Empleado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfilado`
--

LOCK TABLES `perfilado` WRITE;
/*!40000 ALTER TABLE `perfilado` DISABLE KEYS */;
INSERT INTO `perfilado` VALUES (1,'Casandra Lopez','Director','Summer','Admin','1234','Menu de Modulos','Esta Disponible'),(2,'Juan perez','Supervisor','Summer','Superv','907','Menu del Supervisor','Esta Disponible'),(3,'Adan Flores','Capturista','Summer','Capt','788','Menu del Capturista','Esta Disponible'),(4,'Leonardo Gutierrez','Soporte Tecnico','Summer','Supp','567','Menu de Soporte Tecnico','Esta Disponible'),(5,'Eduardo Gutierrez','Contador','Summer','Conta','0001','Menu del Contador','Esta Disponible'),(6,'Juan perez','Contador','Summer','Conta4','0003','Reporte de Gastos a Detalle','Esta Disponible'),(7,'Juan perez','Contador','SEP','Conta4','0003','Reporte de Gastos a Detalle','Esta Disponible');
/*!40000 ALTER TABLE `perfilado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `poliza`
--

DROP TABLE IF EXISTS `poliza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `poliza` (
  `idPoliza` int NOT NULL,
  `Cliente` varchar(45) DEFAULT NULL,
  `Fecha` date DEFAULT NULL,
  `Tipo` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idPoliza`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `poliza`
--

LOCK TABLES `poliza` WRITE;
/*!40000 ALTER TABLE `poliza` DISABLE KEYS */;
/*!40000 ALTER TABLE `poliza` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `problematicas`
--

DROP TABLE IF EXISTS `problematicas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problematicas` (
  `idproblematicas` int NOT NULL,
  `Problematicas` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idproblematicas`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problematicas`
--

LOCK TABLES `problematicas` WRITE;
/*!40000 ALTER TABLE `problematicas` DISABLE KEYS */;
/*!40000 ALTER TABLE `problematicas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `idproductos` int NOT NULL,
  `Marca` varchar(45) DEFAULT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `Proveedor` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idproductos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seguimiento`
--

DROP TABLE IF EXISTS `seguimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seguimiento` (
  `idCliente` int NOT NULL,
  `Nombre_Cliente` varchar(45) DEFAULT NULL,
  `Asunto_Tratar` varchar(45) DEFAULT NULL,
  `Fecha` date DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  `Conclusion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idCliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seguimiento`
--

LOCK TABLES `seguimiento` WRITE;
/*!40000 ALTER TABLE `seguimiento` DISABLE KEYS */;
INSERT INTO `seguimiento` VALUES (1,'Luis','llllllllll','2025-03-25','Inactivo','kkkkkkkkkkkkkkkkkkkkkkk'),(3,'Luisa','hhhhhh','2025-03-27','Activo','mmmmmmmmmmmmm');
/*!40000 ALTER TABLE `seguimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solucion`
--

DROP TABLE IF EXISTS `solucion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solucion` (
  `Id_Solucion` int NOT NULL,
  `Nombre_Empleado` varchar(45) DEFAULT NULL,
  `Nombre_Problematica` varchar(100) DEFAULT NULL,
  `Descripcion_Solucion` varchar(100) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Solucion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solucion`
--

LOCK TABLES `solucion` WRITE;
/*!40000 ALTER TABLE `solucion` DISABLE KEYS */;
/*!40000 ALTER TABLE `solucion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tomar`
--

DROP TABLE IF EXISTS `tomar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tomar` (
  `Id_Monitoreo` int NOT NULL,
  `Nombre_Cliente` varchar(45) DEFAULT NULL,
  `Nombre_Empleado` varchar(45) DEFAULT NULL,
  `Tipo_Poliza` varchar(45) DEFAULT NULL,
  `Nombre_Producto` varchar(45) DEFAULT NULL,
  `Fecha_Asignacion` date DEFAULT NULL,
  `Hora_Asignacion` time DEFAULT NULL,
  `Fecha_Termino` date DEFAULT NULL,
  `Horas_Retraso` int DEFAULT NULL,
  `Minutos_Retraso` int DEFAULT NULL,
  `Dias_Retraso` int DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Monitoreo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tomar`
--

LOCK TABLES `tomar` WRITE;
/*!40000 ALTER TABLE `tomar` DISABLE KEYS */;
/*!40000 ALTER TABLE `tomar` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-04 13:52:52
