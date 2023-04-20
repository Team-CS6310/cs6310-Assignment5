Use db20236310;


CREATE TABLE Orders (
  orderID INT,
  customer INT,
  drone INT,
  store INT,
  weight INT,
  cost INT
);


CREATE TABLE Stores (
  storeName VARCHAR(255),
  revenue INT,
  purchases INT,
  overloads INT,
  locationx INT,
  locationy INT
);


CREATE TABLE Items (
  storeName VARCHAR(255),
  itemName VARCHAR(255),
  itemWeight INT
);


CREATE TABLE Drones (
  storeName VARCHAR(255),
  droneID VARCHAR(255),
  capacity INT,
  fuel INT,
  numOrders INT,
  remainingCap INT,
  locationx INT,
  locationy INT
);


CREATE TABLE AngriBirds (
  birdID VARCHAR(255),
  locationx INT,
  locationy INT,
  attackProbability double,
  changeFrequency INT,
  storeName VARCHAR(255),
  customerID VARCHAR(255),
  flySpeed double,
  changeCounter INT
);


CREATE TABLE Pilots (
  pilotAccount VARCHAR(255),
  firstName VARCHAR(255),
  lastName VARCHAR(255),
  phoneNumber VARCHAR(255),
  taxID VARCHAR(255),
  licenseID VARCHAR(255),
  successfulDelivery INT
);


CREATE TABLE Customers (
  customerID VARCHAR(255),
  firstName VARCHAR(255),
  lastName VARCHAR(255),
  address VARCHAR(255),
  phoneNumber VARCHAR(255),
  rating INT,
  credit INT,
  pendingCredit INT
);


CREATE TABLE Users (
  userID VARCHAR(255),
  password VARCHAR(255)
);
