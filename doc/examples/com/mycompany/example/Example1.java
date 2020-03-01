
package com.mycompany.example;

import br.com.jconcept.database.DatabaseService;
import br.com.jconcept.database.SelectQuery;
import br.com.jconcept.database.query.*;
import br.com.jconcept.database.*;
import br.com.jconcept.database.service.DefaultDatabaseService;
import br.com.jconcept.tools.Tracer;
import br.com.jconcept.tools.StopWatch;
import com.mycompany.myobjects.*;
import com.mycompany.dbmapping.*;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.math.BigDecimal;


/* A simple class that shows the use of jconcept.database package
@version 16/07/2002 - Olivier : Creation
*/

public class Example1 {



    /** Insert a client, a product and a sale
     *
     */
    public void insert() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();


        // Create a client object
        Client client = new Client();

        // Create a Product object
        Product product = new Product();

        // Create a Sale object
        Sale sale = new Sale();

        // Populate the client object
        //client.setClientId(getNextId());
        client.setBirthDate(new Date());
        client.setName("Olivier smadja");
        client.setStreet("Rue de rennes");
        client.setStreetNumber(new Long(151) );
        //client.setCredit(new BigDecimal(10));
        //client.setDebit(new BigDecimal(120));

        // Populate the product object
        //product.setProductId( getNextId() );
        product.setName("Palm Pilot V");
        //product.setUnitPrice(new BigDecimal(500.50));
        product.setDescription("A very nice palm computer!");

        // Populate the sale object
        //sale.setSaleId( getNextId() );
        //sale.setClientId( client.getClientId() );
        //sale.setProductId( product.getProductId() );
        sale.setQuantity( new Long(1) );
        sale.setSaleDate( new Date() );

        // Now inserts object into database
        dbService.executeUpdate( new DefaultInsertQuery(client) );
        dbService.executeUpdate( new DefaultInsertQuery(product) );

        // Now objects have ids
        sale.setClientId( client.getClientId() );
        sale.setProductId( product.getProductId() );

        dbService.executeUpdate( new DefaultInsertQuery(sale) );

        System.out.println("Client with id " + client.getClientId() + " has been inserted");
        System.out.println("Sale with id " + sale.getSaleId() + " has been inserted");
        System.out.println("Product with id " + product.getProductId() + " has been inserted");

        System.out.println();

        System.out.println("Client " + client);
        System.out.println("Product " + product);
        System.out.println("Sale " + sale);

    }


    /** Select objects from database
     *
     */
    public void selectAll() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        // Selects all clients
        QueryResult clientResult = dbService.executeQuery( new DefaultSelectQuery(Client.class) );
        System.out.println("Client list : \n" + clientResult.toString() );

        // Selects all products
        QueryResult productResult = dbService.executeQuery( new DefaultSelectQuery(Product.class) );
        System.out.println("Product list : \n" + productResult.toString() );

        // Selects all sales
        QueryResult saleResult = dbService.executeQuery( new DefaultSelectQuery(Sale.class) );
        System.out.println("Sale list : \n" + saleResult.toString() );

        // Displays the one client
        Client client = null;

        if( clientResult.hasObjects() ) {
            client = (Client) clientResult.getObject(0);

            System.out.println("One client : \n" + client.toString() );
        }else {
            System.out.println("No client found");
        }
    }


    /** Select objects from database
     *
     */
    public void selectSalesOfOneCLient() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        // first selects one client
        Client client = (Client) dbService.executeQuery( new DefaultSelectQuery(Client.class)).getObject(0);


        Where where = new DefaultWhere(SaleDBHelper.CLIENT_ID , WhereOperator.EQUAL , client.getClientId() );
        System.out.println("Where " + where.getSql(true));

        // Selects all sales
        QueryResult saleResult = dbService.executeQuery( new DefaultSelectQuery(Sale.class , where ) );
        System.out.println("Sale list : \n" + saleResult.toString() );

    }

    /** Select objects from database with join
     *
     */
    public void selectWithJoin() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        Where where = new DefaultWhere(SaleDBHelper.CLIENT_ID , WhereOperator.EQUAL , ClientDBHelper.CLIENT_ID);

        DefaultSelectQuery selectQuery = new  DefaultSelectQuery( );

        selectQuery.addSingleTableSelect( new DefaultSingleTableSelectQuery(Client.class));
        selectQuery.addSingleTableSelect( new DefaultSingleTableSelectQuery(Sale.class));

        selectQuery.setWhere( where );

        // Selects all sales
        QueryResult result = dbService.executeQuery( selectQuery );
        System.out.println("Result Type = " + result.getResultType());
        System.out.println("Sale list : \n" + result.toString() );

        if( result.hasObjects() )
        {
            System.out.println("First sale is " + result.getObjectOfType(0 , Sale.class ));
        }

    }


    /** Deletes objects from database
     *
     */
    public void deleteSalesOfOneCLient() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        Where where = new DefaultWhere( SaleDBHelper.CLIENT_ID , WhereOperator.EQUAL , "1");
        // Selects all sales
        int nNbDeleted = dbService.executeUpdate( new DefaultDeleteQuery(Sale.class , where ) );
        System.out.println(nNbDeleted + " sales were deleted!" );

    }

    /** Update objects from database
     *
     */
    public void updateClient() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        QueryResult result = dbService.executeQuery(new DefaultSelectQuery(Client.class , new DefaultWhere(ClientDBHelper.NAME , WhereOperator.LIKE  , "Oli%")));

        Client client = null;

        if( result.hasObjects() )
        {
            client = (Client) result.getObject(0);

            //client.setDebit(new BigDecimal(Math.random()*1000));
            client.setStreetNumber(new Long((int)(Math.random()*1000)));
            // Updates the client
            int nNbUpdates = dbService.executeUpdate( new DefaultUpdateQuery(client) );
            System.out.println(nNbUpdates + " clients were updated!" );
        }
        else
        {
            System.out.println("No client found!");
        }
    }


    /** Deletes objects from database
     *
     */
    public void customSelect() throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        CustomSelectQuery selectQuery = new CustomSelectQuery("select count(*)+2 count , max(client_id) max from CLIENT");

        // Selects
        QueryResult result = dbService.executeQuery( selectQuery );

        System.out.println("Result Custom Select = ");
        System.out.println(result );

        System.out.println("Count is " + result.getFieldValue(0 , "count") );
        System.out.println("max is " + result.getFieldValue(0 , "max") );

        // Or use the specific Query Result
        CustomSelectQueryResult customResult = (CustomSelectQueryResult) result;

        System.out.println("Columns are : " + Arrays.asList(customResult.getColumnNames()) );
        System.out.println("Types are : " + Arrays.asList(customResult.getColumnTypes()) );

    }


    void countingObjects() throws Exception{
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();

        System.out.println("Number of Clients = " + dbService.count(new DefaultSelectQuery(Client.class)) );

    }



    /** Insert a client, a product and a sale
     *
     */
    public void insertQuantity(int in_nQuantity) throws Exception {
        // Creates a database service
        DatabaseService dbService = new DefaultDatabaseService();


        // Create a client object
        Client client = new Client();

        // Create a Product object
        Product product = new Product();

        // Create a Sale object
        Sale sale = new Sale();

        StopWatch sw = new StopWatch();
        sw.start();
        for( int i = 0 ; i < in_nQuantity ; i ++ )
        {
            // Populate the client object
            //client.setClientId(getNextId());
            client.setBirthDate(new Date());
            client.setName("Olivier smadja");
            client.setStreet("Rue de rennes");
            client.setStreetNumber(new Long(151) );
            //client.setCredit(new BigDecimal(10));
            //client.setDebit(new BigDecimal(120));

            // Populate the product object
            //product.setProductId( getNextId() );
            product.setName("Palm Pilot V");
            //product.setUnitPrice(new BigDecimal(500.50));
            product.setDescription("A very nice palm computer!");

            // Populate the sale object
            //sale.setSaleId( getNextId() );
            sale.setClientId( client.getClientId() );
            sale.setProductId( product.getProductId() );
            sale.setQuantity( new Long(1) );
            sale.setSaleDate( new Date() );

            // Now inserts object into database
            dbService.executeQuery( new DefaultInsertQuery(client) );
            dbService.executeQuery( new DefaultInsertQuery(product) );
            dbService.executeQuery( new DefaultInsertQuery(sale) );
        }
        sw.end();

        System.out.println(in_nQuantity + " loops : Time for each loop = " + sw.getDurationInMiliseconds()/in_nQuantity);
    }


    /** This should return the id of a sequence....
     *
     */
    public void getNextId() throws Exception
    {
        DatabaseService dbService = new DefaultDatabaseService();
        Client client = new Client();

        Long nNextId = dbService.getNextId(client);

        System.out.println("Next id for client is " + nNextId );

    }



    public static void main(String[] args) throws Exception {

        boolean bPerformanceTest = false;

        //Tracer.traceAll(true);
        Example1 example1 = new Example1();

        if( !bPerformanceTest )
        {

            System.out.println("\n\n ** DELETING SALES OF ONE CLIENT ** \n\n");
            example1.deleteSalesOfOneCLient();

            System.out.println("\n\n ** INSERTING OBJECTS ** \n\n");
            example1.insert();

            System.out.println("\n\n ** SELECTING OBJECTS ** \n\n");
            example1.selectAll();

            System.out.println("\n\n ** SELECTING SALES OF ONE CLIENT ** \n\n");
            example1.selectSalesOfOneCLient();


            System.out.println("\n\n ** COUNTING Clients ** \n\n");
            example1.countingObjects();


            System.out.println("\n\n ** SELECTING WITH JOIN ** \n\n");
            example1.selectWithJoin();

            System.out.println("\n\n ** Updating client ** \n\n");
            example1.updateClient();

            System.out.println("\n\n ** Custom select ** \n\n");
            example1.customSelect();

            System.out.println("\n\n ** Get Next id ** \n\n");
            example1.getNextId();


        }
        else
        {
            System.out.println("\n\n ** inserting 1000*3 objects ** \n\n");
            example1.insertQuantity(10);
        }

    }

}
