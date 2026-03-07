/* ============================= */
/*        INSERT CATEGORIES      */
/* ============================= */

INSERT INTO Categories (name, slug, icon) VALUES
(N'Tivi','tivi','tv'),
(N'Tủ lạnh','tu-lanh','fridge'),
(N'Máy giặt','may-giat','washer'),
(N'Máy lạnh','may-lanh','ac'),
(N'Nhà bếp','nha-bep','kitchen'),
(N'Gia dụng','gia-dung','home'),
(N'Phụ kiện','phu-kien','accessory');


/* ============================= */
/*           BRANDS              */
/* ============================= */

INSERT INTO Brands (name, country) VALUES
(N'Samsung',N'Hàn Quốc'),
(N'LG',N'Hàn Quốc'),
(N'Sony',N'Nhật Bản'),
(N'Panasonic',N'Nhật Bản'),
(N'Toshiba',N'Nhật Bản'),
(N'Aqua',N'Nhật Bản'),
(N'Sharp',N'Nhật Bản'),
(N'Daikin',N'Nhật Bản'),
(N'Electrolux',N'Thụy Điển'),
(N'Philips',N'Hà Lan'),
(N'Kangaroo',N'Việt Nam'),
(N'Sunhouse',N'Việt Nam');


/* ============================= */
/*            USERS              */
/* ============================= */

INSERT INTO Users (username,password,fullname,email,phone,role)
VALUES
('admin','123456',N'Quản trị viên','admin@gmail.com','0900000001','admin'),
('user1','123456',N'Nguyễn Văn A','a@gmail.com','0900000002','user'),
('user2','123456',N'Trần Văn B','b@gmail.com','0900000003','user'),
('user3','123456',N'Lê Văn C','c@gmail.com','0900000004','user'),
('user4','123456',N'Phạm Văn D','d@gmail.com','0900000005','user');


/* ============================= */
/*         USER ADDRESSES        */
/* ============================= */

INSERT INTO UserAddresses (userId, fullAddress, receiverName, receiverPhone, isDefault)
VALUES
(2,N'123 Nguyễn Trãi, Hà Nội',N'Nguyễn Văn A','0900000002',1),
(3,N'456 Lê Lợi, TP.HCM',N'Trần Văn B','0900000003',1),
(4,N'789 Trần Phú, Đà Nẵng',N'Lê Văn C','0900000004',1);


/* ============================= */
/*        GENERATE PRODUCTS      */
/* ============================= */

DECLARE @categoryId INT = 1
DECLARE @i INT
DECLARE @brandId INT
DECLARE @productName NVARCHAR(200)

WHILE @categoryId <= 7
BEGIN
    SET @i = 1

    WHILE @i <= 150
    BEGIN

        SET @brandId = (SELECT TOP 1 id FROM Brands ORDER BY NEWID())

        SET @productName =
        CASE
            WHEN @categoryId = 1 THEN CONCAT(N'Samsung Smart TV 4K ',@i,' inch')
            WHEN @categoryId = 2 THEN CONCAT(N'LG Inverter Refrigerator ',@i,'L')
            WHEN @categoryId = 3 THEN CONCAT(N'Panasonic Washing Machine ',@i,'kg')
            WHEN @categoryId = 4 THEN CONCAT(N'Daikin Inverter Air Conditioner ',@i,'HP')
            WHEN @categoryId = 5 THEN CONCAT(N'Panasonic Microwave Oven ',@i,'L')
            WHEN @categoryId = 6 THEN CONCAT(N'Philips Electric Kettle ',@i,'L')
            WHEN @categoryId = 7 THEN CONCAT(N'Samsung Remote Control Model ',@i)
        END

        INSERT INTO Products
        (name,slug,description,price,stock,sold,image,discount,warranty,isFeatured,categoryId,brandId)
        VALUES
        (
            @productName,
            CONCAT('product-',@categoryId,'-',@i),
            N'Sản phẩm điện máy chính hãng bảo hành toàn quốc',
            FLOOR(RAND()*20000000)+1000000,
            FLOOR(RAND()*100)+20,
            FLOOR(RAND()*50),
            'product.jpg',
            FLOOR(RAND()*20),
            12,
            CASE WHEN RAND()>0.85 THEN 1 ELSE 0 END,
            @categoryId,
            @brandId
        )

        SET @i=@i+1
    END

    SET @categoryId=@categoryId+1
END


/* ============================= */
/*        PRODUCT IMAGES         */
/* ============================= */

INSERT INTO ProductImages (productId,imageUrl,sortOrder)
SELECT id,'product1.jpg',1 FROM Products

INSERT INTO ProductImages (productId,imageUrl,sortOrder)
SELECT id,'product2.jpg',2 FROM Products

INSERT INTO ProductImages (productId,imageUrl,sortOrder)
SELECT id,'product3.jpg',3 FROM Products


/* ============================= */
/*            COUPONS            */
/* ============================= */

INSERT INTO Coupons
(code,discountType,discountValue,minOrderAmount,startDate,endDate)
VALUES
('SALE10','percent',10,1000000,GETDATE(),DATEADD(MONTH,3,GETDATE())),
('SALE20','percent',20,2000000,GETDATE(),DATEADD(MONTH,3,GETDATE())),
('SALE200K','fixed',200000,2000000,GETDATE(),DATEADD(MONTH,3,GETDATE()));


/* ============================= */
/*        GENERATE ORDERS        */
/* ============================= */

DECLARE @orderIndex INT = 1
DECLARE @userId INT

WHILE @orderIndex <= 100
BEGIN

SET @userId = (SELECT TOP 1 id FROM Users WHERE role='user' ORDER BY NEWID())

INSERT INTO Orders
(userId,totalAmount,shippingAddress,phoneReceiver,receiverName,paymentMethod,status)
VALUES
(
@userId,
FLOOR(RAND()*20000000)+2000000,
N'123 Đường Demo, Hà Nội',
'0900000000',
N'Khách hàng demo',
CASE WHEN RAND()>0.5 THEN 'COD' ELSE 'Online' END,
'Completed'
)

SET @orderIndex=@orderIndex+1
END


/* ============================= */
/*       GENERATE ORDERDETAILS   */
/* ============================= */

DECLARE @detailIndex INT = 1
DECLARE @orderId INT
DECLARE @productId INT

WHILE @detailIndex <= 500
BEGIN

SET @orderId = (SELECT TOP 1 id FROM Orders ORDER BY NEWID())
SET @productId = (SELECT TOP 1 id FROM Products ORDER BY NEWID())

INSERT INTO OrderDetails
(orderId,productId,quantity,price)
VALUES
(
@orderId,
@productId,
FLOOR(RAND()*3)+1,
(SELECT price FROM Products WHERE id=@productId)
)

SET @detailIndex=@detailIndex+1
END


/* ============================= */
/*            REVIEWS            */
/* ============================= */

INSERT INTO Reviews (userId,productId,rating,comment)
SELECT TOP 200
(SELECT TOP 1 id FROM Users WHERE role='user' ORDER BY NEWID()),
id,
FLOOR(RAND()*5)+1,
N'Sản phẩm rất tốt, giao hàng nhanh'
FROM Products
ORDER BY NEWID()


/* ============================= */
/*             CART              */
/* ============================= */

INSERT INTO Cart (userId,productId,quantity)
SELECT TOP 50
(SELECT TOP 1 id FROM Users WHERE role='user' ORDER BY NEWID()),
id,
1
FROM Products
ORDER BY NEWID()


/* ============================= */
/*           WISHLIST            */
/* ============================= */

INSERT INTO Wishlist (userId,productId)
SELECT TOP 50
(SELECT TOP 1 id FROM Users WHERE role='user' ORDER BY NEWID()),
id
FROM Products
ORDER BY NEWID()