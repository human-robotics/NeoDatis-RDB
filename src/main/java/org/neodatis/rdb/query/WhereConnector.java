package org.neodatis.rdb.query;

/*
@version 19/07/2002 - Olivier : Creation
*/

public class WhereConnector {
    String _name;

    public static final WhereConnector AND = new WhereConnector(" AND ");
    public static final WhereConnector OR = new WhereConnector(" OR ");

    protected WhereConnector(String in_sConnectorName)
    {
        _name = in_sConnectorName;
    }
    public String toString()
    {
        return _name;
    }

}

