CREATE TABLE IF NOT EXISTS contact(
    user_id INT(32) NOT NULL,
    name varchar(50),
    phone varchar(13),
    email varchar(50),
    company varchar(50),
    address varchar(64),
    FOREIGN KEY(user_id) REFERENCES user(id)
);

INSERT INTO `contact` (user_id,`name`,`phone`,`email`,`company`,`address`) VALUES (16200,'Warren','13229288068','ornare@liberoProin.com','Lacus Limited','Hat');
INSERT INTO `contact` (user_id,`name`,`phone`,`email`,`company`,`address`) VALUES (16200,'Lucian','18443550410','eget.massa@dignissim.ca','Ligula Aenean LLC','Katsina');
INSERT INTO `contact` (user_id,`name`,`phone`,`email`,`company`,`address`) VALUES (16200,'Silas','11034227415','Aenean.eget@sitamet.net','Nisl Arcu Iaculis Associates','Noord Braant');
INSERT INTO `contact` (user_id,`name`,`phone`,`email`,`company`,`address`) VALUES (16200,'Sariusa','15881503573','nisi.magna@ultriciesornareelit.net','Vel Mauris Integer Consulting','AS');
