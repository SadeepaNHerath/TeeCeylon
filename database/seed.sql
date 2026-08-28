-- TeeCeylon T-Shirt Shop Database Seed Script
-- Database: Tee-Ceylon

CREATE DATABASE IF NOT EXISTS `Tee-Ceylon`;
USE `Tee-Ceylon`;

-- 1. Create tables if they do not exist
CREATE TABLE IF NOT EXISTS `employees` (
  `empId` VARCHAR(255) NOT NULL,
  `empRole` VARCHAR(255) DEFAULT NULL,
  `empName` VARCHAR(255) DEFAULT NULL,
  `contactNum` VARCHAR(255) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`empId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `suppliers` (
  `supId` VARCHAR(255) NOT NULL,
  `supName` VARCHAR(255) DEFAULT NULL,
  `supAddress` VARCHAR(255) DEFAULT NULL,
  `supEmail` VARCHAR(255) DEFAULT NULL,
  `supContact` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`supId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `customers` (
  `cusId` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  `phone` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) DEFAULT NULL,
  `address` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`cusId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `products` (
  `proId` VARCHAR(255) NOT NULL,
  `sku` VARCHAR(255) UNIQUE DEFAULT NULL,
  `proName` VARCHAR(255) DEFAULT NULL,
  `proCategory` VARCHAR(255) DEFAULT NULL,
  `proStyle` VARCHAR(255) DEFAULT NULL,
  `proSize` VARCHAR(255) DEFAULT NULL,
  `proColor` VARCHAR(255) DEFAULT NULL,
  `costPrice` DOUBLE DEFAULT NULL,
  `proPrice` DOUBLE DEFAULT NULL,
  `stockQty` INT DEFAULT NULL,
  `reorderLevel` INT DEFAULT NULL,
  `sup_id` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`proId`),
  KEY `FK_products_supplier` (`sup_id`),
  CONSTRAINT `FK_products_supplier` FOREIGN KEY (`sup_id`) REFERENCES `suppliers` (`supId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `purchases` (
  `purchaseId` VARCHAR(255) NOT NULL,
  `purchaseDate` DATE DEFAULT NULL,
  `supplierInvoiceNo` VARCHAR(255) DEFAULT NULL,
  `totalAmount` DOUBLE DEFAULT NULL,
  `remarks` VARCHAR(255) DEFAULT NULL,
  `sup_id` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`purchaseId`),
  KEY `FK_purchases_supplier` (`sup_id`),
  CONSTRAINT `FK_purchases_supplier` FOREIGN KEY (`sup_id`) REFERENCES `suppliers` (`supId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `purchase_details` (
  `purchase_id` VARCHAR(255) NOT NULL,
  `pro_id` VARCHAR(255) NOT NULL,
  `qty` INT DEFAULT NULL,
  `unitCost` DOUBLE DEFAULT NULL,
  `totalCost` DOUBLE DEFAULT NULL,
  PRIMARY KEY (`purchase_id`, `pro_id`),
  KEY `FK_purchasedetails_product` (`pro_id`),
  CONSTRAINT `FK_purchasedetails_purchase` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`purchaseId`) ON DELETE CASCADE,
  CONSTRAINT `FK_purchasedetails_product` FOREIGN KEY (`pro_id`) REFERENCES `products` (`proId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `orders` (
  `ordId` VARCHAR(255) NOT NULL,
  `ordDate` DATE DEFAULT NULL,
  `ordTime` TIME DEFAULT NULL,
  `ordTotal` DOUBLE DEFAULT NULL,
  `cusName` VARCHAR(255) DEFAULT NULL,
  `cusPhone` VARCHAR(255) DEFAULT NULL,
  `cusEmail` VARCHAR(255) DEFAULT NULL,
  `cus_id` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`ordId`),
  KEY `FK_orders_customer` (`cus_id`),
  CONSTRAINT `FK_orders_customer` FOREIGN KEY (`cus_id`) REFERENCES `customers` (`cusId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order_details` (
  `ord_id` VARCHAR(255) NOT NULL,
  `pro_id` VARCHAR(255) NOT NULL,
  `proQty` INT DEFAULT NULL,
  `unitPrice` DOUBLE DEFAULT NULL,
  `proTotal` DOUBLE DEFAULT NULL,
  PRIMARY KEY (`ord_id`, `pro_id`),
  KEY `FK_orderdetails_product` (`pro_id`),
  CONSTRAINT `FK_orderdetails_order` FOREIGN KEY (`ord_id`) REFERENCES `orders` (`ordId`) ON DELETE CASCADE,
  CONSTRAINT `FK_orderdetails_product` FOREIGN KEY (`pro_id`) REFERENCES `products` (`proId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Seed Employees
INSERT INTO `employees` (`empId`, `empRole`, `empName`, `contactNum`, `address`, `email`, `password`) VALUES
('ADM001', 'ADMIN', 'Store Administrator', '0771234567', 'Colombo 03', 'admin@gmail.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918'),
('CAS001', 'CASHIER', 'Main Cashier', '0779876543', 'Nugegoda', 'cashier@gmail.com', '6074c6aa3488f3c2dddff2a7b88e9fb004e873e39f60485ab5184cc47089735a')
ON DUPLICATE KEY UPDATE `empName`=VALUES(`empName`);

-- 3. Seed Suppliers
INSERT INTO `suppliers` (`supId`, `supName`, `supContact`, `supEmail`, `supAddress`) VALUES
('SUP001', 'TexCeylon Apparel Mills', '0112345678', 'sales@texceylon.com', 'Katunayake EPZ, Katunayake'),
('SUP002', 'CottonCraft Lanka Ltd', '0118765432', 'info@cottoncraft.lk', 'Biyagama Industrial Zone'),
('SUP003', 'LankaKnits Fabricators', '0334567890', 'orders@lankaknits.com', 'Kandy Road, Kelaniya')
ON DUPLICATE KEY UPDATE `supName`=VALUES(`supName`);

-- 4. Seed Customers
INSERT INTO `customers` (`cusId`, `name`, `phone`, `email`, `address`) VALUES
('CUS0001', 'Kasun Perera', '0771112233', 'kasun.p@gmail.com', 'No 45, High Level Rd, Maharagama'),
('CUS0002', 'Anoma Fernando', '0714445566', 'anoma.f@yahoo.com', 'No 12, Galle Rd, Dehiwala'),
('CUS0003', 'Nuwan Silva', '0768889900', 'nuwan.silva@outlook.com', 'No 78, Kandy Rd, Kiribathgoda')
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 5. Seed Products (T-Shirts)
INSERT INTO `products` (`proId`, `sku`, `proName`, `proCategory`, `proStyle`, `proSize`, `proColor`, `costPrice`, `proPrice`, `stockQty`, `reorderLevel`, `sup_id`) VALUES
('PRO001', 'TEE-GEN-CRW-BLK-M', 'Classic Crew Neck Tee', 'Gents', 'Crew Neck', 'M', 'Black', 1100.00, 1850.00, 45, 10, 'SUP001'),
('PRO002', 'TEE-GEN-CRW-BLK-L', 'Classic Crew Neck Tee', 'Gents', 'Crew Neck', 'L', 'Black', 1100.00, 1850.00, 38, 10, 'SUP001'),
('PRO003', 'TEE-GEN-CRW-WHT-M', 'Classic Crew Neck Tee', 'Gents', 'Crew Neck', 'M', 'White', 1050.00, 1750.00, 50, 10, 'SUP001'),
('PRO004', 'TEE-GEN-CRW-WHT-L', 'Classic Crew Neck Tee', 'Gents', 'Crew Neck', 'L', 'White', 1050.00, 1750.00, 42, 10, 'SUP001'),
('PRO005', 'TEE-GEN-POL-NVY-M', 'Pique Polo Shirt', 'Gents', 'Polo', 'M', 'Navy Blue', 1600.00, 2650.00, 25, 8, 'SUP002'),
('PRO006', 'TEE-GEN-POL-NVY-L', 'Pique Polo Shirt', 'Gents', 'Polo', 'L', 'Navy Blue', 1600.00, 2650.00, 20, 8, 'SUP002'),
('PRO007', 'TEE-GEN-OVR-OLV-L', 'Heavyweight Oversized Tee', 'Gents', 'Oversized', 'L', 'Olive Green', 1400.00, 2400.00, 30, 8, 'SUP003'),
('PRO008', 'TEE-LAD-VNK-MRN-S', 'Fitted V-Neck Tee', 'Ladies', 'V-Neck', 'S', 'Maroon', 950.00, 1650.00, 28, 8, 'SUP001'),
('PRO009', 'TEE-LAD-VNK-MRN-M', 'Fitted V-Neck Tee', 'Ladies', 'V-Neck', 'M', 'Maroon', 950.00, 1650.00, 35, 8, 'SUP001'),
('PRO010', 'TEE-LAD-CRP-PNK-S', 'Ribbed Crop Top Tee', 'Ladies', 'Crop Top', 'S', 'Dusty Pink', 900.00, 1500.00, 22, 5, 'SUP002'),
('PRO011', 'TEE-LAD-OVR-LAV-M', 'Relaxed Boyfriend Tee', 'Ladies', 'Oversized', 'M', 'Lavender', 1250.00, 2100.00, 18, 5, 'SUP003'),
('PRO012', 'TEE-KID-CRW-RED-S', 'Kids Graphic Active Tee', 'Kids', 'Crew Neck', 'S', 'Bright Red', 700.00, 1200.00, 40, 10, 'SUP002'),
('PRO013', 'TEE-KID-CRW-BLU-M', 'Kids Everyday Cotton Tee', 'Kids', 'Crew Neck', 'M', 'Royal Blue', 700.00, 1200.00, 32, 10, 'SUP002')
ON DUPLICATE KEY UPDATE `proName`=VALUES(`proName`), `stockQty`=VALUES(`stockQty`);

-- 6. Seed Purchases (GRN)
INSERT INTO `purchases` (`purchaseId`, `purchaseDate`, `supplierInvoiceNo`, `totalAmount`, `remarks`, `sup_id`) VALUES
('PUR0001', '2026-08-15', 'TX-INV-8891', 132000.00, 'Initial Autumn Bulk Stock', 'SUP001'),
('PUR0002', '2026-08-20', 'CC-INV-4412', 78500.00, 'Polo & Crop Tops Restock', 'SUP002')
ON DUPLICATE KEY UPDATE `totalAmount`=VALUES(`totalAmount`);

INSERT INTO `purchase_details` (`purchase_id`, `pro_id`, `qty`, `unitCost`, `totalCost`) VALUES
('PUR0001', 'PRO001', 60, 1100.00, 66000.00),
('PUR0001', 'PRO003', 60, 1050.00, 63000.00),
('PUR0002', 'PRO005', 30, 1600.00, 48000.00),
('PUR0002', 'PRO010', 30, 900.00, 27000.00)
ON DUPLICATE KEY UPDATE `qty`=VALUES(`qty`);

-- 7. Seed Orders
INSERT INTO `orders` (`ordId`, `ordDate`, `ordTime`, `ordTotal`, `cusName`, `cusPhone`, `cusEmail`, `cus_id`) VALUES
('ORD0001', '2026-08-28', '11:15:00', 3700.00, 'Kasun Perera', '0771112233', 'kasun.p@gmail.com', 'CUS0001'),
('ORD0002', '2026-08-30', '15:40:00', 5050.00, 'Anoma Fernando', '0714445566', 'anoma.f@yahoo.com', 'CUS0002')
ON DUPLICATE KEY UPDATE `ordTotal`=VALUES(`ordTotal`);

INSERT INTO `order_details` (`ord_id`, `pro_id`, `proQty`, `unitPrice`, `proTotal`) VALUES
('ORD0001', 'PRO001', 2, 1850.00, 3700.00),
('ORD0002', 'PRO005', 1, 2650.00, 2650.00),
('ORD0002', 'PRO007', 1, 2400.00, 2400.00)
ON DUPLICATE KEY UPDATE `proQty`=VALUES(`proQty`);
