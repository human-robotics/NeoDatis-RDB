package org.neodatis.rdb.query;


import java.util.List;
import java.util.ArrayList;

import org.neodatis.rdb.DBColumn;
import org.neodatis.rdb.DbObjectMapping;
import org.neodatis.rdb.UpdateQuery;
import org.neodatis.rdb.Where;

/** An object that contain all data to build the update query
* The object containing data - must implements DbObjectMapping
@version 10/07/2002 - Olivier : creation
*/

public class DefaultUpdateQuery implements UpdateQuery
{
    /** The Object that contains data*/
    DbObjectMapping _object;

    /** The Where to make object*/
    Where _where;

    /** A list of Db fields to update - can be null */
    List _columnList;

    public DefaultUpdateQuery(DbObjectMapping _object)
    {
        this._object = _object;
        _columnList = null;
    }

    public DefaultUpdateQuery(DbObjectMapping in_object, Where in_where) {
        this(in_object);
        _where = in_where;
    }

    public DbObjectMapping getObject() {
        return _object;
    }

    public Where getWhere() {
        return _where;
    }

    /** To add a colum to be updated
     * @param The column to be added
     */
    public DefaultUpdateQuery addColumnToUpdate( DBColumn in_column )
    {
        if( _columnList == null )
        {
            _columnList = new ArrayList();
        }

        _columnList.add( in_column );

        return this;
    }

    /** To get the column number - if has been specified
     * @return The number of columns
     */
    public int getCustomColumnNumber()
    {
        if( _columnList == null )
        {
            return 0;
        }

        return _columnList.size();
    }

    /** To get the column of the specified index
     * @return A DBColumn
     * @exeption NullPointerException if colimn list has not been initialized
     */
    public DBColumn getCustomColumnToUpdate(int in_nIndex)
    {
        return (DBColumn) _columnList.get(in_nIndex);
    }

    /** To indicate if custom fields have been defined
     * @return true if yes, false if not
     */
    public boolean hasCustomFieldsToUpdate()
    {
        return getCustomColumnNumber() > 0 ;
    }
    /** To checks if has the specified custom field
     * @param The name of the custom field
     * @return true if it has, false if not
     */
    public boolean canUpdateField( String in_sFieldName )
    {
        if( hasCustomFieldsToUpdate() )
        {
            for( int i = 0 ; i < getCustomColumnNumber() ; i ++ )
            {
                if( getCustomColumnToUpdate(i).getName().equals(in_sFieldName) )
                {
                    return true;
                }
            }

            // has custom field defined, but not this one => can not update it
            return false;
        }

        // Do not have any custom field defined => can update all
        return true;
    }
}
