Database v2

Database v2 is a simple database layer.

It is composed of two parts :
1) Automatic class generation
2) Use of database access


jconcept.database generates for you all the classes you will need to insert,delete,update and select data.


1) Aquitetura

Você só manipula classes de dados tipo java bean. Essas classes são geradas automaticamente para você. 

2) Gerar as classes

A única coisa que deve ser feito é criar um arquivo contendo a lista das tebelas a serem mapeadas com o banco de dados.
Esse arquivo tem o seguinte padrão :
<Nome da tabela> = <pacote da classe a ser gerado> , <Nome da classe> , <Nome do atributo Primary key> , <Nome da classe mãe - opcional>

exemplo :
Para mapear uma Tabela CLIENTE(com primeira key id) numa classe Cliente no pacote com.minhaempresa.dblayer

CLIENTE=com.minhaempresa.dblayer,Cliente,id

3) Usa a API





Step by Step

Here is a step by step explanation of how to install and configure jconcept.database.



1) Script SQL

Here is a single ansi sql script that creates 3 single tables that all following examples will use.

	create table CLIENT
	(
	  CLIENT_ID		  INTEGER,	
	  NAME            CHAR(50),
	  BIRTH_DATE      DATE,
	  STREET          CHAR(50),
	  STREET_NUMBER   INTEGER,
	  DEBIT           FLOAT,
	  CREDIT          FLOAT
	);


create table PRODUCT
(
  PRODUCT_ID      INTEGER,
  NAME            CHAR(50),
  DESCRIPTION     CHAR(250),
  UNIT_PRICE      FLOAT
);

create table SALE
(
	SALE_ID INTEGER,
	CLIENT_ID INTEGER,
	PRODUCT_ID INTEGER,
	SALE_DATE DATE,
	QUANTITY INTEGER
);

You can now create these tables in your database.

2) Mapping configuration file

The class generation step just needs a configuration file in which your have to tell which tables you want
to map and how (in which class and package)


This configuration file has the following format :
<TABLE_NAME>=<package of class to be generated> , < class name > , <Primary key name>

For example, to generate the classes to map the 3 described tables in package 'com.mycompany.myobjects', the configuration file
would be :

CLIENT=com.mycompany.myobjects,Client,CLIENT_ID
PRODUCT=com.mycompany.myobjects,Product,PRODUCT_ID
SALE=com.mycompany.myobjects,Sale,SALE_ID

Put this in tables.properties (Or other file name)


3) Adapt the simple build_db.bat script to your needs

Now that you have the configuration file, you only need to execute the class generator.
You have an example in the build_db.bat file.

You need to put in your classpath :
* the database driver
* the jconcept.jar file

The class that generates the classes is : br/com/jconcept/database/util/generation/JavaClassBuilderFromFile

This class needs two parameters :
* The first one is the mapping configuration file
* The second one is the database type : can be jdbc or oracle.

Use oracle if your acessing Oracle database. Use jdbc for other database.

The class database api uses an internal Connection pool which uses one configuration file :
ConnectionPool.properties. This contains 6 Fields(Only 4 are mandatory):
* driver (mandatory): the djdbc atabase driver 
	Example : 
		* for oracle : driver = oracle.jdbc.driver.OracleDriver
		* for mysql : driver = org.gjt.mm.mysql.Driver

* url (mandatory): the jdbc url
	Example : 
		* for oracle : url = jdbc:oracle:thin:@<server>:1521:<instance> (check if your are using the 1521 port!)
		* for mysql : url = jdbc:MySql://<server>/<database name>
* user (mandatory) : The user
* password (mandatory) : The password
* pool (mandatory) = <connection number>		

An example of this file is present in this distribution(in the root directory : ConncetionPool.properties)

This configuration needs to be done for class generating and for later use.
This file must be present in classpath.

This example suppose that you are accessing a local mysql database (with name mydb) using root user without password!

So, the call can be :

java -classpath .;classes12.zip;jconcept_simple.jar br/com/jconcept/database/util/generation/JavaClassBuilderFromFile /tables.properties oracle

If you want, you can easy copy files to the right directory :

mkdir src
cd src
mkdir com
cd com
mkdir mycompany
cd mycompany
mkdir myobjects
cd ..\..\..
copy *.java .\src\com\mycompany\myobjects

Now let's compile all java code.

cd src
javac -classpath .;classes12.zip;mm.mysql-2.0.4-bin.jar;jconcept_simple.jar com/mycompany/myobjects/*.java com/mycompany/example/*.java

4) Now you have your objects, let's start!

Use the 4 examples to get started.

* The example1 shows how to insert, select and update data.
* The example2 shows how to select with join

