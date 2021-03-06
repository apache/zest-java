/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.apache.polygene.index.sql.support.postgresql;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.polygene.api.injection.scope.Service;
import org.apache.polygene.api.injection.scope.This;
import org.apache.polygene.api.injection.scope.Uses;
import org.apache.polygene.api.service.ServiceDescriptor;
import org.apache.polygene.index.sql.support.common.GenericDatabaseExplorer;
import org.apache.polygene.index.sql.support.common.GenericDatabaseExplorer.ColumnInfo;
import org.apache.polygene.index.sql.support.common.GenericDatabaseExplorer.DatabaseProcessor;
import org.apache.polygene.index.sql.support.common.GenericDatabaseExplorer.ForeignKeyInfo;
import org.apache.polygene.index.sql.support.skeletons.SQLDBState;
import org.apache.polygene.library.sql.common.SQLUtil;
import org.apache.polygene.library.sql.generator.vendor.SQLVendor;
import org.apache.polygene.spi.query.IndexExporter;

public class PostgreSQLIndexExporter
    implements IndexExporter
{

    @This
    private SQLDBState _state;

    @Uses
    private ServiceDescriptor descriptor;

    @Service
    private DataSource _dataSource;

    private static final String SEPARATOR = "-----------------------------------------------";

    private static final Map<Integer, String> TYPE_STRINGS;

    static
    {
        TYPE_STRINGS = new HashMap<>( 16 );
        TYPE_STRINGS.put( Types.BIGINT, "BIGINT" );
        TYPE_STRINGS.put( Types.BOOLEAN, "BOOLEAN" );
        TYPE_STRINGS.put( Types.CHAR, "CHAR" );
        TYPE_STRINGS.put( Types.DATE, "DATE" );
        TYPE_STRINGS.put( Types.DECIMAL, "DECIMAL" );
        TYPE_STRINGS.put( Types.DOUBLE, "DOUBLE" );
        TYPE_STRINGS.put( Types.FLOAT, "FLOAT" );
        TYPE_STRINGS.put( Types.INTEGER, "INTEGER" );
        TYPE_STRINGS.put( Types.NULL, "NULL" );
        TYPE_STRINGS.put( Types.NUMERIC, "NUMERIC" );
        TYPE_STRINGS.put( Types.REAL, "REAL" );
        TYPE_STRINGS.put( Types.SMALLINT, "SMALLINT" );
        TYPE_STRINGS.put( Types.TIME, "TIME" );
        TYPE_STRINGS.put( Types.TIMESTAMP, "TIMESTAMP" );
        TYPE_STRINGS.put( Types.VARCHAR, "VARCHAR" );
        TYPE_STRINGS.put( Types.VARBINARY, "VARBINARY" );
    }

    @Override
    public void exportFormalToWriter( final PrintWriter out )
        throws IOException, UnsupportedOperationException
    {
        Connection connection = null;
        try
        {
            connection = this._dataSource.getConnection();
            GenericDatabaseExplorer.visitDatabaseTables( connection, null,
                                                         this._state.schemaName().get(), null, new DatabaseProcessor()
            {

                @Override
                public void endProcessColumns( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "</columns>" + "\n" );
                }

                @Override
                public void endProcessRows( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "</rows>" + "\n" );
                }

                @Override
                public void endProcessTableInfo( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "</table>" + "\n" );
                }

                @Override
                public void endProcessSchemaInfo( String schemaName )
                {
                    out.write( "</schema>" + "\n" );
                }

                @Override
                public void endProcessRowInfo( String schemaName, String tableName, Object[] rowContents )
                {
                    out.write( "</row>" + "\n" );
                }

                @Override
                public void endProcessColumnInfo( String schemaName, String tableName, ColumnInfo colInfo,
                                                  ForeignKeyInfo fkInfo )
                {
                }

                @Override
                public void beginProcessTableInfo( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "<table name=\"" + tableName + "\" remarks=\"" + tableRemarks + "\">" + "\n" );
                }

                @Override
                public void beginProcessColumns( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "<columns>" + "\n" );
                }

                @Override
                public void beginProcessColumnInfo( String schemaName, String tableName, ColumnInfo colInfo,
                                                    ForeignKeyInfo fkInfo )
                {
                    String defaultValue = colInfo.getDefaultValue();
                    if( defaultValue.startsWith( "'" ) )
                    {
                        defaultValue = defaultValue.substring( 1, defaultValue.length() - 1 );
                    }
                    out.write( "<column name=\"" + colInfo.getName() + "\" colType=\"" + colInfo.getTypeName()
                               + "\" colSize=\"" + colInfo.getSize() + "\" colScale=\""
                               + //
                        colInfo.getScale() + "\" nullable=\"" + colInfo.getNullable() + "\" default=\""
                               + defaultValue + "\" " //
                    );
                    if( fkInfo != null )
                    {
                        out.write( "refSchemaName=\"" + fkInfo.getPkSchemaName() + "\" refTableName=\""
                                   + fkInfo.getPkTableName() + "\" refPKColumnName=\""
                                   + fkInfo.getPkTablePKColumnName()
                                   + //
                            "\" onUpdate=\"" + fkInfo.getOnUpdateAction() + "\" onDelete=\""
                                   + fkInfo.getOnDeleteAction() + "\" deferrability=\"" + fkInfo.getDeferrability()
                                   + "\" " //
                        );
                    }

                    out.write( "/>" + "\n" );
                }

                @Override
                public void beginProcessSchemaInfo( String schemaName )
                {
                    out.write( "<schema name=\"" + schemaName + "\">" + "\n" );
                }

                @Override
                public void beginProcessRows( String schemaName, String tableName, String tableRemarks )
                {
                    out.write( "<rows>" + "\n" );
                }

                @Override
                public void beginProcessRowInfo( String schemaName, String tableName, Object[] rowContents )
                {
                    out.write( "<row>" + "\n" );
                    for( Integer x = 0; x < rowContents.length; ++x )
                    {
                        out.write( "<value index=\"" + x + "\" >" + rowContents[x] + "</value>" + "\n" );
                    }
                }
            }, this.descriptor.metaInfo( SQLVendor.class ) );
        }
        catch( SQLException sqle )
        {
            throw new IOException( SQLUtil.withAllSQLExceptions( sqle ) );
        }
        finally
        {
            SQLUtil.closeQuietly( connection );
        }
    }

    @Override
    public void exportReadableToStream( final PrintStream out )
        throws IOException, UnsupportedOperationException
    {
        Connection connection = null;
        try
        {
            connection = this._dataSource.getConnection();
            GenericDatabaseExplorer.visitDatabaseTables( connection, null,
                                                         this._state.schemaName().get(), null, new DatabaseProcessor()
            {

                @Override
                public void endProcessTableInfo( String schemaName, String tableName, String tableRemarks )
                {
                    out.print( "\n\n\n" );
                }

                @Override
                public void endProcessSchemaInfo( String schemaName )
                {
                    out.print( "\n\n" );
                }

                @Override
                public void endProcessRowInfo( String schemaName, String tableName, Object[] rowContents )
                {

                }

                @Override
                public void endProcessColumnInfo( String schemaName, String tableName, ColumnInfo colInfo,
                                                  ForeignKeyInfo fkInfo )
                {

                }

                @Override
                public void endProcessColumns( String schemaName, String tableName, String tableRemarks )
                {
                    out.print( SEPARATOR + "\n" + SEPARATOR + "\n" );
                }

                @Override
                public void endProcessRows( String schemaName, String tableName, String tableRemarks )
                {
                    out.print( SEPARATOR + "\n" + SEPARATOR + "\n" );
                }

                private String parseSQLType( ColumnInfo colInfo )
                {
                    String result = colInfo.getTypeName();
                    Integer sqlType = colInfo.getSQLType();
                    if( TYPE_STRINGS.containsKey( sqlType ) )
                    {
                        result = TYPE_STRINGS.get( sqlType );
                        if( sqlType == Types.DECIMAL || sqlType == Types.NUMERIC )
                        {
                            result = result + "(" + colInfo.getSize() + ", " + colInfo.getScale() + ")";
                        }
                        else if( sqlType == Types.VARCHAR || sqlType == Types.VARBINARY )
                        {
                            result = result + "(" + colInfo.getSize() + ")";
                        }
                    }
                    return result;
                }

                @Override
                public void beginProcessColumnInfo( String schemaName, String tableName, ColumnInfo colInfo,
                                                    ForeignKeyInfo fkInfo )
                {
                    out.print( colInfo.getName() + ":" + this.parseSQLType( colInfo ) + "[nullable:"
                               + colInfo.getNullable() + "; default: " + colInfo.getDefaultValue() + "]" );
                    if( fkInfo != null )
                    {
                        out.print( " -> " + fkInfo.getPkSchemaName() + "." + fkInfo.getPkTableName() + "("
                                   + fkInfo.getPkTablePKColumnName() + ")[ON UPDATE " + fkInfo.getOnUpdateAction()
                                   + ", ON DELETE " + fkInfo.getOnDeleteAction() + ", " + fkInfo.getDeferrability() + "]" );
                    }
                    out.print( "\n" );
                }

                @Override
                public void beginProcessTableInfo( String schemaName, String tableName, String tableRemarks )
                {
                    out.print( "Table: " + schemaName + "." + tableName
                               + ( tableRemarks == null ? "" : " (" + tableRemarks + ")" ) + "\n" );
                }

                @Override
                public void beginProcessSchemaInfo( String schemaName )
                {
                    out.print( //
                        "\n\n" + "Schema: " + schemaName + "\n" + //
                        SEPARATOR + "\n\n\n" //
                    );
                }

                @Override
                public void beginProcessRowInfo( String schemaName, String tableName, Object[] rowContents )
                {
                    for( Integer x = 0; x < rowContents.length; ++x )
                    {
                        Object value = rowContents[x];
                        out.print( value == null ? "NULL" : ( "\"" + value + "\"" ) );
                        if( x + 1 < rowContents.length )
                        {
                            out.print( " ; " );
                        }
                    }
                    out.print( "\n" );
                }

                @Override
                public void beginProcessColumns( String schemaName, String tableName, String tableRemarks )
                {
                    out.print( SEPARATOR + "\n" + SEPARATOR + "\n" );
                }

                @Override
                public void beginProcessRows( String schemaName, String tableName, String tableRemarks )
                {

                }
            }, this.descriptor.metaInfo( SQLVendor.class ) );
        }
        catch( SQLException sqle )
        {
            throw new IOException( SQLUtil.withAllSQLExceptions( sqle ) );
        }
        finally
        {
            SQLUtil.closeQuietly( connection );
        }
    }
}
