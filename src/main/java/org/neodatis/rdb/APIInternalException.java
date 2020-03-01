package org.neodatis.rdb;

/**
 * A exception to encapsulate all api exceptions
@version 01/08/2002 - Olivier : Creation
*/

public class APIInternalException extends Exception{


    public APIInternalException(Exception e)
    {
        super(e);

    }

    public APIInternalException(Exception e , String message)
    {
        super(message,e);
    }

    public APIInternalException(String message)
    {
        this( null , message );
    }
}
