-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.32-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for testing
DROP DATABASE IF EXISTS `testing`;
CREATE DATABASE IF NOT EXISTS `testing` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `testing`;

-- Dumping structure for table testing.category
DROP TABLE IF EXISTS `category`;
CREATE TABLE IF NOT EXISTS `category` (
                                          `id` bigint(20) NOT NULL DEFAULT 0,
    `name` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.category: ~6 rows (approximately)
DELETE FROM `category`;
INSERT INTO `category` (`id`, `name`) VALUES
                                          (1, 'Electronics'),
                                          (2, 'Clothing'),
                                          (3, 'Books'),
                                          (4, 'Home Appliances'),
                                          (5, 'Furniture'),
                                          (6, 'Sports Equipment');

-- Dumping structure for table testing.import_cart
DROP TABLE IF EXISTS `import_cart`;
CREATE TABLE IF NOT EXISTS `import_cart` (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `product_id` bigint(20) NOT NULL,
    `supplier_id` int(11) NOT NULL,
    `quantity` int(11) NOT NULL,
    `price` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_cart_product_idx` (`product_id`),
    KEY `fk_cart_supplier_idx` (`supplier_id`),
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_cart_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.import_cart: ~2 rows (approximately)
DELETE FROM `import_cart`;
INSERT INTO `import_cart` (`id`, `product_id`, `supplier_id`, `quantity`, `price`) VALUES
                                                                                       (1, 1, 1, 10, 299),
                                                                                       (2, 2, 2, 20, 49);

-- Dumping structure for table testing.import_detail
DROP TABLE IF EXISTS `import_detail`;
CREATE TABLE IF NOT EXISTS `import_detail` (
                                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `invoice_id` bigint(20) NOT NULL,
    `product_id` bigint(20) NOT NULL,
    `supplier_id` int(11) NOT NULL,
    `quantity` int(11) NOT NULL,
    `price` int(11) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_detail_invoice_idx` (`invoice_id`),
    KEY `fk_detail_product_idx` (`product_id`),
    KEY `fk_detail_supplier_idx` (`supplier_id`),
    CONSTRAINT `fk_detail_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `import_invoice` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_detail_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `fk_detail_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
    ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.import_detail: ~2 rows (approximately)
DELETE FROM `import_detail`;
INSERT INTO `import_detail` (`id`, `invoice_id`, `product_id`, `supplier_id`, `quantity`, `price`) VALUES
                                                                                                       (1, 1, 1, 1, 10, 299),
                                                                                                       (2, 1, 2, 2, 20, 49);

-- Dumping structure for table testing.import_invoice
DROP TABLE IF EXISTS `import_invoice`;
CREATE TABLE IF NOT EXISTS `import_invoice` (
                                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `import_date` datetime NOT NULL,
    `total_imported_products` int(11) DEFAULT NULL,
    `total_import_cost` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.import_invoice: ~1 rows (approximately)
DELETE FROM `import_invoice`;
INSERT INTO `import_invoice` (`id`, `import_date`, `total_imported_products`, `total_import_cost`) VALUES
    (1, '2024-05-05 10:00:00', 30, 1899);

-- Dumping structure for table testing.product
DROP TABLE IF EXISTS `product`;
CREATE TABLE IF NOT EXISTS `product` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `description` text DEFAULT NULL,
    `category_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `category_id` (`category_id`),
    CONSTRAINT `FK_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
    ) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.product: ~4 rows (approximately)
DELETE FROM `product`;
INSERT INTO `product` (`id`, `name`, `description`, `category_id`) VALUES
                                                                       (1, 'Smartphone', 'High-end smartphone with advanced features', 1),
                                                                       (2, 'Jeans', 'Comfortable denim jeans', 2),
                                                                       (8, 'bro', 'fds', 2),
                                                                       (9, 'bro', 'fds', 1);

-- Dumping structure for table testing.supplier
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE IF NOT EXISTS `supplier` (
                                          `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `contact_person` varchar(100) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `email` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.supplier: ~6 rows (approximately)
DELETE FROM `supplier`;
INSERT INTO `supplier` (`id`, `name`, `contact_person`, `phone`, `email`) VALUES
                                                                              (1, 'Tech Solutions Inc.', 'Michael Johnson', '+1234567890', 'michael@techsolutions.com'),
                                                                              (2, 'Fashion Trendz Ltd.', 'Emily Brown', '+9876543210', 'emily@fashiontrendz.com'),
                                                                              (3, 'Bookworm Publishers', 'David White', '+246813579', 'david@bookworm.com'),
                                                                              (4, 'Appliances Plus', 'Sarah Clark', '+135792468', 'sarah@appliancesplus.com'),
                                                                              (5, 'FurniHome', 'Robert Garcia', '+1122334455', 'robert@furnihome.com'),
                                                                              (6, 'SportsGear Co.', 'Jessica Martinez', '+9988776655', 'jessica@sportsgear.com');

-- Dumping structure for table testing.user
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table testing.user: ~2 rows (approximately)
DELETE FROM `user`;
INSERT INTO `user` (`id`, `username`, `password`) VALUES
                                                      (5, 'root', '$2a$10$XnKmZ0b2HLGqoD/faG.6L.IIFtx2b2dT5j7awFReQsttgbZG/Mcvi'),
                                                      (7, 'root1', '$2a$10$O1mVi2dNe4bLeaK2KXH4hedJ0nFasTUwMT5E7GHkrpu/FJRHzK1my');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
