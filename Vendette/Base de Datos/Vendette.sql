-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: vendette
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

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'b567f38e-beb0-11f0-a168-7cd30a744263:1-4655';

--
-- Table structure for table `catgastos`
--

DROP TABLE IF EXISTS `catgastos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catgastos` (
  `Id_Cat_Gastos` int(10) unsigned zerofill NOT NULL,
  `Clave` int DEFAULT NULL,
  `NombreGastos` varchar(45) DEFAULT NULL,
  `Costo` decimal(10,2) DEFAULT NULL,
  `Proveedor` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Cat_Gastos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catgastos`
--

LOCK TABLES `catgastos` WRITE;
/*!40000 ALTER TABLE `catgastos` DISABLE KEYS */;
INSERT INTO `catgastos` VALUES (0000000001,808,'Viaticos',100.00,'Soriana','Esta Disponible'),(0000000002,787,'Comida',180.00,'Bodega Aurrera','Esta Disponible'),(0000000003,765,'Gasolina',200.00,'Walmart','Esta Disponible');
/*!40000 ALTER TABLE `catgastos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `IdCliente` int NOT NULL,
  `Clave` int DEFAULT NULL,
  `Nombre` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `Correo` varchar(45) DEFAULT NULL,
  `Direccion` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdCliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
INSERT INTO `empleados` VALUES (1,'Eduardo Gutierrez','Desarrollador','5526022681','calzadaeduardo56@gmail.com','Colonia Luis Echeverria','6 meses','Interno','Esta Disponible'),(2,'Leonardo Gutierrez','Soporte Tecnico','6789858823','calzadaleonardo40@gmail.com','Colonia Azteca','1 año','Interno','Esta Disponible'),(3,'Karla Suarez','Secretaria','2345898901','karlasuarez89@gmail.com','Colonia Doctores','2 años','Interno','Esta Disponible');
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enlace`
--

DROP TABLE IF EXISTS `enlace`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enlace` (
  `Id` int NOT NULL,
  `Id_Cuenta` int DEFAULT NULL,
  `Id_Pedido` int DEFAULT NULL,
  `Id_detalle_cuenta` int DEFAULT NULL,
  `Mesero` varchar(45) DEFAULT NULL,
  `Mesa` int DEFAULT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `Cantidad` int DEFAULT NULL,
  `Precio` decimal(10,2) DEFAULT NULL,
  `Subtotal` decimal(10,2) DEFAULT NULL,
  `estatus_pedido` varchar(45) DEFAULT NULL,
  `Monto` decimal(10,2) DEFAULT NULL,
  `Fecha` datetime DEFAULT NULL,
  `Id_Venta` int DEFAULT NULL,
  `estado` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enlace`
--

LOCK TABLES `enlace` WRITE;
/*!40000 ALTER TABLE `enlace` DISABLE KEYS */;
/*!40000 ALTER TABLE `enlace` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gastos`
--

DROP TABLE IF EXISTS `gastos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gastos` (
  `IdGastos` int NOT NULL,
  `FechaRegistro` date DEFAULT NULL,
  `Referencia` varchar(45) DEFAULT NULL,
  `TipoGastos` varchar(45) DEFAULT NULL,
  `FechaPago` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `Comentarios` varchar(45) DEFAULT NULL,
  `PrecioGastos` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`IdGastos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gastos`
--

LOCK TABLES `gastos` WRITE;
/*!40000 ALTER TABLE `gastos` DISABLE KEYS */;
INSERT INTO `gastos` VALUES (1,'2026-10-19','Ninguna','Viaticos','2026-10-22','Efectivo','Ninguno',100.00),(2,'2026-10-23','Ninguna','Comida','2026-10-26','Efectivo','Ninguno',150.00),(3,'2026-10-27','Ninguna','Gasolina','2026-10-30','Efectivo','Ninguno',200.00),(4,'2026-10-19','Ninguna','Viaticos','2026-10-19','Efectivo','Ninguno',12.00);
/*!40000 ALTER TABLE `gastos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gastos1`
--

DROP TABLE IF EXISTS `gastos1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gastos1` (
  `IdGastos` int NOT NULL,
  `TipoGastos` varchar(45) DEFAULT NULL,
  `FechaPago` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `PrecioGastos` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`IdGastos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gastos1`
--

LOCK TABLES `gastos1` WRITE;
/*!40000 ALTER TABLE `gastos1` DISABLE KEYS */;
INSERT INTO `gastos1` VALUES (1,'Viaticos','2026-10-22','Efectivo',100.00),(2,'Comida','2026-10-26','Efectivo',150.00),(3,'Gasolina','2026-10-30','Efectivo',200.00),(4,'Viaticos','2026-10-19','Efectivo',12.00);
/*!40000 ALTER TABLE `gastos1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historialsurtido`
--

DROP TABLE IF EXISTS `historialsurtido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historialsurtido` (
  `IdSurtido` int NOT NULL,
  `Proveedor` varchar(45) DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `FechaCompra` date DEFAULT NULL,
  `FolioCompra` int DEFAULT NULL,
  `Codigo` int DEFAULT NULL,
  `Cantidad` int DEFAULT NULL,
  `PrecioCompra` decimal(10,2) DEFAULT NULL,
  `Stock` int DEFAULT NULL,
  `Descripcion` varchar(45) DEFAULT NULL,
  `PrecioTotal` decimal(10,2) DEFAULT NULL,
  `TipoPrecio` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdSurtido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historialsurtido`
--

LOCK TABLES `historialsurtido` WRITE;
/*!40000 ALTER TABLE `historialsurtido` DISABLE KEYS */;
INSERT INTO `historialsurtido` VALUES (1,'Soriana','Vales','2026-10-15',1000,79678,20,27.00,50,'Jabon',540.00,'Precio Alto'),(2,'Bodega Aurrera','Efectivo','2026-10-19',999,2,25,30.00,40,'Galletas',750.00,'Sin Cambios'),(3,'Walmart','Efectivo','2026-10-21',998,897,30,35.00,60,'Carne',1050.00,'Sin Cambios');
/*!40000 ALTER TABLE `historialsurtido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `histventas`
--

DROP TABLE IF EXISTS `histventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `histventas` (
  `Folio` int NOT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `FechaVenta` date DEFAULT NULL,
  `ContadorProducto` int DEFAULT NULL,
  PRIMARY KEY (`Folio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `histventas`
--

LOCK TABLES `histventas` WRITE;
/*!40000 ALTER TABLE `histventas` DISABLE KEYS */;
INSERT INTO `histventas` VALUES (265,'Jabon','2026-10-15',1),(266,'Galletas','2026-10-15',1),(267,'Carne','2026-10-15',1),(268,'Galletas','2026-10-19',2),(269,'Galletas','2026-10-19',3),(270,'Jabon','2026-10-19',2),(271,'Carne','2026-10-19',2),(277,'Jabon','2026-11-07',1),(278,'Jabon','2026-11-07',2),(281,'Jabon','2026-11-12',1),(282,'Galletas','2026-11-12',1);
/*!40000 ALTER TABLE `histventas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metdodopago`
--

DROP TABLE IF EXISTS `metdodopago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metdodopago` (
  `idMetodo` int NOT NULL,
  `NombreMetodo` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idMetodo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metdodopago`
--

LOCK TABLES `metdodopago` WRITE;
/*!40000 ALTER TABLE `metdodopago` DISABLE KEYS */;
INSERT INTO `metdodopago` VALUES (1,'Efectivo','Esta Disponible'),(2,'Transferencia','Esta Disponible'),(3,'Vales','Esta Disponible'),(4,'Tarjeta de Credito','Esta Disponible');
/*!40000 ALTER TABLE `metdodopago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nuevaventa`
--

DROP TABLE IF EXISTS `nuevaventa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nuevaventa` (
  `iddetalle` int NOT NULL AUTO_INCREMENT,
  `Folio` int DEFAULT NULL,
  `CodigoBarras` varchar(45) DEFAULT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `PrecioVenta` decimal(10,2) DEFAULT NULL,
  `Cantidad` int DEFAULT NULL,
  `TipoVenta` varchar(45) DEFAULT NULL,
  `FechaVenta` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `PrecioTotal` decimal(10,2) DEFAULT NULL,
  `ContadorProducto` int DEFAULT NULL,
  PRIMARY KEY (`iddetalle`)
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nuevaventa`
--

LOCK TABLES `nuevaventa` WRITE;
/*!40000 ALTER TABLE `nuevaventa` DISABLE KEYS */;
INSERT INTO `nuevaventa` VALUES (281,1,'9979','Jabon',55.00,2,'VN','2026-11-12','Efectivo',110.00,1),(282,2,'90999','Galletas',55.00,1,'VN','2026-11-12','Efectivo',55.00,1);
/*!40000 ALTER TABLE `nuevaventa` ENABLE KEYS */;
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
  `Perfil` varchar(45) DEFAULT NULL,
  `Usuario` varchar(45) DEFAULT NULL,
  `Contraseña` varchar(45) DEFAULT NULL,
  `Modulo` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`Id_Empleado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfilado`
--

LOCK TABLES `perfilado` WRITE;
/*!40000 ALTER TABLE `perfilado` DISABLE KEYS */;
INSERT INTO `perfilado` VALUES (1,'Eduardo Gutierrez','Admin','Admin','1234','Menu de Modulos','Esta Disponible'),(2,'Leonardo Gutierrez','Supervisor','Superv','12345','Menu del Supervisor','Esta Disponible'),(3,'Karla Suarez','Cajero','Caje','123456','Menu del Cajero','Esta Disponible');
/*!40000 ALTER TABLE `perfilado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `idproductos` int NOT NULL,
  `CodigoBarras` varchar(45) DEFAULT NULL,
  `Descripcion` varchar(45) DEFAULT NULL,
  `Proveedor` varchar(45) DEFAULT NULL,
  `Stock` double DEFAULT NULL,
  `PrecioInicial` double DEFAULT NULL,
  `PrecioFinal` double DEFAULT NULL,
  `InventarioMinimo` double DEFAULT NULL,
  `InventarioMaximo` double DEFAULT NULL,
  `AlertaInventarioMin` int DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idproductos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'9979','Jabon','Soriana',74,50,55,30,70,1,'No esta Disponible'),(2,'90999','Galletas','Soriana',79,50,55,31,70,0,'Esta Disponible');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `IdProveedores` int NOT NULL,
  `Clave` int DEFAULT NULL,
  `Nombre` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `Correo` varchar(45) DEFAULT NULL,
  `Direccion` varchar(45) DEFAULT NULL,
  `Status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdProveedores`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (1,789,'Soriana','5526022681','soriana45@gmail.com','Colonia Luis Ecehverria','Esta Disponible'),(2,900,'Bodega Aurrera','5529152080','aurrera56@gmail.com','Colonia Roma','Esta Disponible'),(3,1000,'Walmart','5523971639','walmart40@gmail.com','Colonia Doctores','Esta Disponible');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `surtido`
--

DROP TABLE IF EXISTS `surtido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surtido` (
  `IdSurtido` int NOT NULL,
  `Proveedor` varchar(45) DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `FechaCompra` date DEFAULT NULL,
  `FolioCompra` int DEFAULT NULL,
  `Codigo` int DEFAULT NULL,
  `Cantidad` int DEFAULT NULL,
  `PrecioCompra` decimal(10,2) DEFAULT NULL,
  `Stock` int DEFAULT NULL,
  `Descripcion` varchar(45) DEFAULT NULL,
  `PrecioTotal` decimal(10,2) DEFAULT NULL,
  `TipoPrecio` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`IdSurtido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surtido`
--

LOCK TABLES `surtido` WRITE;
/*!40000 ALTER TABLE `surtido` DISABLE KEYS */;
INSERT INTO `surtido` VALUES (1,'Soriana','Vales','2026-10-15',1000,79678,20,27.00,50,'Jabon',540.00,'Precio Alto'),(2,'Bodega Aurrera','Efectivo','2026-10-19',999,2,25,30.00,40,'Galletas',750.00,'Sin Cambios'),(3,'Walmart','Efectivo','2026-10-21',998,897,30,35.00,60,'Carne',1050.00,'Sin Cambios');
/*!40000 ALTER TABLE `surtido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `idVenta` int NOT NULL AUTO_INCREMENT,
  `Folio` int DEFAULT NULL,
  `CodigoBarras` varchar(45) DEFAULT NULL,
  `FechaVenta` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `TotalPagar` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`idVenta`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
INSERT INTO `ventas` VALUES (73,1,'9979','2026-11-12','Efectivo',110.00),(74,2,'90999','2026-11-12','Efectivo',55.00);
/*!40000 ALTER TABLE `ventas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas1`
--

DROP TABLE IF EXISTS `ventas1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas1` (
  `idVenta` int NOT NULL AUTO_INCREMENT,
  `Folio` int DEFAULT NULL,
  `CodigoBarras` varchar(45) DEFAULT NULL,
  `FechaVenta` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `TotalPagar` decimal(10,2) DEFAULT NULL,
  `CalculoUtilidades` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`idVenta`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas1`
--

LOCK TABLES `ventas1` WRITE;
/*!40000 ALTER TABLE `ventas1` DISABLE KEYS */;
INSERT INTO `ventas1` VALUES (5,1,'9979','2026-11-12','Efectivo',110.00,5.00),(6,2,'90999','2026-11-12','Efectivo',55.00,5.00);
/*!40000 ALTER TABLE `ventas1` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventasfecha`
--

DROP TABLE IF EXISTS `ventasfecha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventasfecha` (
  `Folio` int NOT NULL,
  `CodigoBarras` int DEFAULT NULL,
  `Producto` varchar(45) DEFAULT NULL,
  `PrecioVenta` decimal(10,2) DEFAULT NULL,
  `Cantidad` int DEFAULT NULL,
  `TipoVenta` varchar(45) DEFAULT NULL,
  `FechaVenta` date DEFAULT NULL,
  `MetodoPago` varchar(45) DEFAULT NULL,
  `PrecioTotal` decimal(10,2) DEFAULT NULL,
  `Cliente` varchar(45) DEFAULT NULL,
  `Telefono` varchar(45) DEFAULT NULL,
  `FechaInicio` date DEFAULT NULL,
  `FechaTermino` date DEFAULT NULL,
  PRIMARY KEY (`Folio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventasfecha`
--

LOCK TABLES `ventasfecha` WRITE;
/*!40000 ALTER TABLE `ventasfecha` DISABLE KEYS */;
/*!40000 ALTER TABLE `ventasfecha` ENABLE KEYS */;
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

-- Dump completed on 2026-11-16 17:56:47
