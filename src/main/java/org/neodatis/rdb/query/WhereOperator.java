package org.neodatis.rdb.query;

/*
@version 19/07/2002 - Olivier : Creation
*/

public class WhereOperator {
    String _name;

    public static final WhereOperator EQUAL                    = new WhereOperator(" = ");
    public static final WhereOperator NOT_EQUAL                = new WhereOperator(" != ");
    public static final WhereOperator GREATER_THAN             = new WhereOperator(" >= ");
    public static final WhereOperator STRICTLY_GREATHER_THAN   = new WhereOperator(" > ");
    public static final WhereOperator SMALLER_THAN             = new WhereOperator(" <= ");
    public static final WhereOperator STRICTLY_SMALLER_THAN    = new WhereOperator(" < ");
    public static final WhereOperator IN                       = new WhereOperator(" IN (%)");
    public static final WhereOperator NOT_IN                       = new WhereOperator(" NOT IN (%)");
    public static final WhereOperator EXISTS                   = new WhereOperator(" EXISTS ");
    public static final WhereOperator START_EXPRESSION         = new WhereOperator(" ( ");
    public static final WhereOperator END_EXPRESSION           = new WhereOperator(" ) ");
    public static final WhereOperator LIKE                     = new WhereOperator(" LIKE ");
    public static final WhereOperator IS_NULL                = new WhereOperator(" is null ");
    public static final WhereOperator IS_NOT_NULL                = new WhereOperator(" is not null ");

    protected WhereOperator(String in_sOperatorName)
    {
        _name = in_sOperatorName;
    }

    public String toString()
    {
        return _name;
    }
}
