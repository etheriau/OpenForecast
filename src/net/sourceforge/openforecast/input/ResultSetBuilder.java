//
//  OpenForecast - open source, general-purpose forecasting package.
//  Copyright (C) 2002-2011  Steven R. Gould
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package net.sourceforge.openforecast.input;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;

/**
 * Defines a Builder that can be used to construct a DataSet from a ResultSet.
 * This class makes for a quick and easy "import" of data from a JDBC data
 * source such as an Oracle database.
 *
 * <p><strong>WARNING: This class has not been tested. It has been made
 * available as a starting point for anyone interested in implementing this
 * kind of Builder. It <em>should</em> work fine but, like I said, has not
 * been tested.</strong> If you use this class and fine it works, as is, let
 * me know (and perhaps I can remove this message). Alternatively, if you
 * find any changes are necessary, please submit them for inclusion in the
 * project.
 *
 * <p>Each record in the result set is assumed to define one data point. The
 * last value on each row is assumed to represent the dependent variable. For
 * example, if the independent variables are represented by x1, x2, x3 and so
 * on, and the dependent variable is represented by y, then a row should be
 * of the form:
 *
 * <pre>
 *  x1, x2, ..., xi, y
 * </pre>
 *
 * <p>For example, the following represents data points (1,3), (2,5), (3,6),
 * and (4,7):
 *
 * <pre>
 *  1, 3
 *  2, 5
 *  3, 6
 *  4, 7
 * </pre>
 *
 * <p>where the values 3, 5, 6 and 7 are the observed values of the dependent
 * variable corresponding to the associated values of the independent variables
 * with the values 1, 2, 3, and 4 respectively. The independent variables will
 * be given the column names as defined by the query.
 *
 * <p>For example, consider the following query:
 *
 * <pre>
 *  SELECT period, sales_revenue FROM sales_summary
 * </pre>
 *
 * This would create a series of data points with the single independent
 * variable, <code>period</code>. The <code>sales_revenue</code> column is
 * assumed to represent the dependent variable (for which we don't currently
 * use the name).
 * @author Steven R. Gould
 * @since 0.4
 */
public class ResultSetBuilder extends AbstractBuilder
{
     /**
      * Stores the result set from which this builder is to read its data.
      */
     private ResultSet rs;
     
     /**
      * Constructs a new ResultSetBuilder that reads its input from the given
      * ResultSet. The fields will be named according to the column labels
      * defined by the ResultSet's meta data.
      * @param resultSet the ResultSet containing data to be used to build the
      * DataSet.
      */
     public ResultSetBuilder( ResultSet resultSet )
     {
          this.rs = resultSet;
     }
     
     /**
      * Retrieves a DataSet - a collection of DataPoints - from the current
      * input source. The DataSet should contain all DataPoints defined by
      * the input source.
      *
      * <p>In general, build will attempt to convert all rows in the ResultSet
      * to data points. In this implementation, all columns are assumed to
      * contain numeric data. This restriction may be relaxed at a later date.
      * @return a DataSet built from the current input source.
      * @throws SQLException if a database access error occurs.
      */
     public DataSet build()
          throws SQLException
     {
          DataSet dataSet = new DataSet();
          
          setColumnNames();
          
          // Make sure we're on the first record
          if ( !rs.isBeforeFirst() )
                rs.beforeFirst();
          
          // Iterate through ResultSet,
          //  creating new DataPoint instance for each row
          while ( rs.next() )
                {
                     DataPoint dp = build( rs );
                     dataSet.add( dp );
                }
          
          return dataSet;
     }
     
     /**
      * Builds a DataPoint from the given row in the ResultSet. Assumes the
      * ResultSet has been positioned on the required row. This method does not
      * change the ResultSet, or the cursor within the ResultSet. The row is
      * expected/assumed to be made up of numeric fields only.
      * @param rs the ResultSet from which a row is to be read and used to
      * construct a new DataPoint.
      * @return a DataPoint object with values as specified by the current row
      * in the given ResultSet.
      * @throws SQLException if a database access error occurs.
      */
     private DataPoint build( ResultSet rs )
          throws SQLException
     {
          Observation dataPoint = new Observation( 0.0 );
          
          int n = getNumberOfVariables();
          for ( int column=0; column<n; column++ )
                {
                     // Treat all columns as numeric data
                     double value = rs.getDouble( column );
                     
                     // If this is the last value on the line, treat it
                     //  as the dependent variable value
                     if ( column == n )
                          dataPoint.setDependentValue( value );
                     else
                          dataPoint.setIndependentValue( getVariableName(column),
                                                                    value );
                }
          
          return dataPoint;
     }
     
     /**
      * Reads the column names from the meta data associated with the current
      * ResultSet, and initializes this object using those names.
      * @throws SQLException if a database access error occurs.
      */
     private void setColumnNames()
          throws SQLException
     {
          ResultSetMetaData metaData = rs.getMetaData();
          
          // Store names of independent variables. Last column is assumed
          //  to be the dependent variable value (we don't actually use that)
          int n = metaData.getColumnCount()-1;
          for ( int column=0; column<n; column++ )
                setVariableName( column, metaData.getColumnLabel(column) );
     }
}
// Local Variables:
// tab-width: 4
// End:
