/* ============================= */
/*      CREATE DATABASE          */
/* ============================= */

CREATE DATABASE HomeElectroDB;
GO

USE HomeElectroDB;
GO


/* ============================= */
/*            USERS              */
/* ============================= */

CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,         -- Lưu dạng hash (bcrypt)
    fullname NVARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',        -- admin / user
    status BIT DEFAULT 1,
    createdDate DATETIME DEFAULT GETDATE()
);


/* ============================= */
/*     USER ADDRESSES            */
/* ============================= */

CREATE TABLE UserAddresses (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    fullAddress NVARCHAR(255) NOT NULL,
    receiverName NVARCHAR(100),
    receiverPhone VARCHAR(20),
    isDefault BIT DEFAULT 0,
    FOREIGN KEY (userId) REFERENCES Users(id)
);


/* ============================= */
/*          CATEGORIES           */
/* ============================= */

CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    slug VARCHAR(150),
    icon VARCHAR(255),
    status BIT DEFAULT 1
);


/* ============================= */
/*            BRANDS             */
/* ============================= */

CREATE TABLE Brands (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    logo VARCHAR(255),
    country NVARCHAR(100),
    status BIT DEFAULT 1
);


/* ============================= */
/*            PRODUCTS           */
/* ============================= */

CREATE TABLE Products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    slug VARCHAR(255),
    description NVARCHAR(MAX),
    price DECIMAL(18,2) NOT NULL,
    stock INT DEFAULT 0,
    sold INT DEFAULT 0,                     -- Số lượng đã bán
    image VARCHAR(255),                     -- Ảnh đại diện
    discount DECIMAL(5,2) DEFAULT 0,        -- % giảm giá
    warranty INT DEFAULT 0,                 -- Bảo hành (tháng)
    isFeatured BIT DEFAULT 0,
    status BIT DEFAULT 1,
    createdDate DATETIME DEFAULT GETDATE(),
    categoryId INT,
    brandId INT,

    FOREIGN KEY (categoryId) REFERENCES Categories(id),
    FOREIGN KEY (brandId) REFERENCES Brands(id)
);


/* ============================= */
/*        PRODUCT IMAGES         */
/* ============================= */

CREATE TABLE ProductImages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    productId INT NOT NULL,
    imageUrl VARCHAR(255) NOT NULL,
    sortOrder INT DEFAULT 0,
    FOREIGN KEY (productId) REFERENCES Products(id)
);


/* ============================= */
/*             CART              */
/* ============================= */

CREATE TABLE Cart (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT DEFAULT 1,
    addedDate DATETIME DEFAULT GETDATE(),

    CONSTRAINT UQ_Cart_UserProduct UNIQUE (userId, productId),
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (productId) REFERENCES Products(id)
);


/* ============================= */
/*           WISHLIST            */
/* ============================= */

CREATE TABLE Wishlist (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    productId INT NOT NULL,
    addedDate DATETIME DEFAULT GETDATE(),

    CONSTRAINT UQ_Wishlist_UserProduct UNIQUE (userId, productId),
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (productId) REFERENCES Products(id)
);


/* ============================= */
/*             ORDERS            */
/* ============================= */

CREATE TABLE Orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    orderDate DATETIME DEFAULT GETDATE(),
    totalAmount DECIMAL(18,2),
    shippingAddress NVARCHAR(255),
    phoneReceiver VARCHAR(20),
    receiverName NVARCHAR(100),
    note NVARCHAR(500),
    paymentMethod VARCHAR(50),              -- COD / Online
    paymentStatus VARCHAR(50) DEFAULT 'Unpaid',  -- Unpaid / Paid
    status NVARCHAR(50) DEFAULT 'Pending',
    -- Pending / Processing / Shipping / Completed / Cancelled
    updatedDate DATETIME,

    FOREIGN KEY (userId) REFERENCES Users(id)
);


/* ============================= */
/*         ORDER DETAILS         */
/* ============================= */

CREATE TABLE OrderDetails (
    id INT IDENTITY(1,1) PRIMARY KEY,
    orderId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(18,2) NOT NULL,           -- Giá tại thời điểm mua
    discount DECIMAL(5,2) DEFAULT 0,

    FOREIGN KEY (orderId) REFERENCES Orders(id),
    FOREIGN KEY (productId) REFERENCES Products(id)
);


/* ============================= */
/*            REVIEWS            */
/* ============================= */

CREATE TABLE Reviews (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    productId INT NOT NULL,
    orderId INT,                            -- Chỉ review sau khi đã mua
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(500),
    status BIT DEFAULT 1,                   -- Ẩn/hiện review
    createdDate DATETIME DEFAULT GETDATE(),

    CONSTRAINT UQ_Review_UserProduct UNIQUE (userId, productId),
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (productId) REFERENCES Products(id),
    FOREIGN KEY (orderId) REFERENCES Orders(id)
);


/* ============================= */
/*         COUPONS / VOUCHER     */
/* ============================= */

CREATE TABLE Coupons (
    id INT IDENTITY(1,1) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discountType VARCHAR(20) DEFAULT 'percent', -- percent / fixed
    discountValue DECIMAL(18,2) NOT NULL,
    minOrderAmount DECIMAL(18,2) DEFAULT 0,
    maxUsage INT DEFAULT 1,
    usedCount INT DEFAULT 0,
    startDate DATETIME,
    endDate DATETIME,
    status BIT DEFAULT 1
);


/* ============================= */
/*        SAMPLE DATA            */
/* ============================= */

-- Categories
INSERT INTO Categories (name, slug) VALUES
(N'Tivi', 'tivi'),
(N'Tủ lạnh', 'tu-lanh'),
(N'Máy giặt', 'may-giat'),
(N'Máy lạnh', 'may-lanh'),
(N'Nhà bếp', 'nha-bep'),
(N'Gia dụng', 'gia-dung'),
(N'Phụ kiện', 'phu-kien');


-- Brands
INSERT INTO Brands (name, country) VALUES
(N'Samsung', N'Hàn Quốc'),
(N'LG', N'Hàn Quốc'),
(N'Sony', N'Nhật Bản'),
(N'Panasonic', N'Nhật Bản'),
(N'Toshiba', N'Nhật Bản');


-- Admin account (password nên hash bằng bcrypt trong thực tế)
INSERT INTO Users (username, password, fullname, role)
VALUES ('admin', '123456', N'Quản trị viên', 'admin');

-- Sample user
INSERT INTO Users (username, password, fullname, email, phone, role)
VALUES ('user01', '123456', N'Nguyễn Văn A', 'user01@gmail.com', '0901234567', 'user');


-- Sample products
INSERT INTO Products (name, slug, description, price, stock, categoryId, brandId, discount, warranty, isFeatured)
VALUES
(N'Samsung Smart TV 55 inch 4K', 'samsung-smart-tv-55-inch', N'Tivi 4K UHD, HDR, Smart TV', 15000000, 10, 1, 1, 5, 24, 1),
(N'LG Tủ lạnh Inverter 300L', 'lg-tu-lanh-inverter-300l', N'Tủ lạnh tiết kiệm điện, ngăn đá dưới', 12000000, 8, 2, 2, 0, 24, 1),
(N'Máy giặt Sony 9kg cửa trước', 'may-giat-sony-9kg', N'Máy giặt cửa trước, inverter, 9kg', 9000000, 12, 3, 3, 10, 12, 0),
(N'Panasonic Máy lạnh 1.5HP', 'panasonic-may-lanh-1-5hp', N'Máy lạnh tiết kiệm điện, inverter', 11000000, 5, 4, 4, 0, 12, 1),
(N'Toshiba Lò vi sóng 25L', 'toshiba-lo-vi-song-25l', N'Lò vi sóng đa năng, 25 lít', 2500000, 20, 5, 5, 15, 12, 0);


/* ============================= */
/*        STATISTICS QUERIES     */
/* ============================= */

-- Tổng doanh thu (đơn đã hoàn thành)
SELECT SUM(totalAmount) AS TongDoanhThu 
FROM Orders 
WHERE status = 'Completed';

-- Tổng số đơn hàng theo trạng thái
SELECT status, COUNT(*) AS SoLuong 
FROM Orders 
GROUP BY status;

-- Doanh thu theo tháng
SELECT 
    YEAR(orderDate) AS Nam,
    MONTH(orderDate) AS Thang,
    SUM(totalAmount) AS DoanhThu
FROM Orders
WHERE status = 'Completed'
GROUP BY YEAR(orderDate), MONTH(orderDate)
ORDER BY Nam, Thang;

-- Giá cao nhất / thấp nhất / trung bình
SELECT 
    MAX(price) AS GiaCaoNhat,
    MIN(price) AS GiaThapNhat,
    AVG(price) AS GiaTrungBinh
FROM Products WHERE status = 1;

-- Top 5 sản phẩm bán chạy nhất
SELECT TOP 5 
    p.id, p.name, SUM(od.quantity) AS TongBan
FROM OrderDetails od
JOIN Products p ON od.productId = p.id
GROUP BY p.id, p.name
ORDER BY TongBan DESC;

-- Top 5 khách hàng mua nhiều nhất
SELECT TOP 5 
    u.id, u.fullname, u.email,
    COUNT(o.id) AS SoDon,
    SUM(o.totalAmount) AS TongChiTieu
FROM Orders o
JOIN Users u ON o.userId = u.id
WHERE o.status = 'Completed'
GROUP BY u.id, u.fullname, u.email
ORDER BY TongChiTieu DESC;

-- Đánh giá trung bình từng sản phẩm
SELECT 
    p.id, p.name,
    COUNT(r.id) AS SoLuotDanhGia,
    CAST(AVG(CAST(r.rating AS FLOAT)) AS DECIMAL(3,1)) AS DiemTrungBinh
FROM Products p
LEFT JOIN Reviews r ON p.id = r.productId
GROUP BY p.id, p.name
ORDER BY DiemTrungBinh DESC;

-- Sản phẩm sắp hết hàng (stock <= 5)
SELECT id, name, stock 
FROM Products 
WHERE stock <= 5 AND status = 1
ORDER BY stock ASC;
GO