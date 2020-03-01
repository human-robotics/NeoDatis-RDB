package org.neodatis.rdb.query;

import org.neodatis.rdb.DBTable;
import org.neodatis.rdb.implementation.Util;

/*
@version 22/07/2002 - Olivier : Creation
*/

public class DefaultDBTable implements DBTable {

    /** The default prefix to automatically create alias*/
    public static final String PREFIX_ALIAS = "my";

    /** The table name*/
    String tableName;

    /** The default table alias*/
    String defaultAlias;

    /**Constructor with table name. The alias is created automatically.
     * The alias will be created as my<TableName>
     *@param The table name
     */
    public DefaultDBTable(String in_sTableName)
    {
        tableName = in_sTableName;
        if(tableName.isEmpty()) {
        	defaultAlias = "";
        } else {
        	defaultAlias = PREFIX_ALIAS+tableName;
        }
        
        
        if(defaultAlias.indexOf(".")!=-1){
        	defaultAlias = defaultAlias.replace('.', '_');
        }
    }
    /**Constructor with table name and alias
     * @param The table name
     * @param The table alias
     */
    public DefaultDBTable(String in_sTableName , String in_sTableAlias)
    {
        tableName = in_sTableName;
        defaultAlias = in_sTableAlias;
    }

    /** Return the alias of table, used for joins
     * @return The alias
     */
    public String getAlias() {
    	
        return Util.getRigthCase(defaultAlias);
    }

    /** Returns The table name
     * @return the Table name
     */
    public String getName() {
        return tableName;
    }

    public String toString()
    {
        StringBuffer sResult = new StringBuffer();
        sResult.append("TableName=").append(tableName);
        sResult.append(" / TableAlias=").append(defaultAlias);
        return sResult.toString();
    }
}
