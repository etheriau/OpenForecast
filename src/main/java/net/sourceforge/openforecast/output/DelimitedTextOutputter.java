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

package net.sourceforge.openforecast.output;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;

/**
 * Defines an Outputter that can be used to write a DataSet in any text
 * delimited format. For example, space or tab delimited, comma-delimited
 * (also known as CSV, Comma Separated Values, format). The default delimiter
 * is the comma, so the resulting output will be in CSV format. This can be
 * over-ridden using the {@link #setDelimiter} method. Using the default
 * delimiter (the comma), this class provides for a quick and easy "export" of
 * forecast data points to a variety of other applications such as Microsoft
 * Excel or any other spreadsheet application.
 *
 * <p>The last value on each row is assumed to represent the dependent
 * variable. For example, if the independent variables are represented by x1,
 * x2, x3 and so on, and the dependent variable is represented by y, then an
 * output row will be of the form:
 *
 * <pre>
 *  x1, x2, ..., xi, y
 * </pre>
 *
 * <p>For example, the following output would be produced by the data points
 * (1,3), (2,5), (3,6), and (4,7):
 *
 * <pre>
 *  1,3
 *  2,5
 *  3,6
 *  4,7
 * </pre>
 *
 * <p>where the values 3, 5, 6 and 7 are the forecast - or possibly the
 * observed - values of the dependent variable corresponding to the
 * associated values of the independent variables with the values 1, 2, 3,
 * and 4 respectively. The order of the independent variables is determined
 * by the order of the variables returned by
 * {@link DataPoint#getIndependentVariableNames}. By default, a header row
 * containing these variable names will be output as the first line of output.
 * To disable this behavior, use {@link #setOutputHeaderRow} to disable this
 * option.
 *
 * <p>Using the previous example, if the x<sub>1</sub> represented time, the
 * output would be:
 *
 * <pre>
 *  time,dependent value
 *  1,3
 *  2,5
 *  3,6
 *  4,7
 * </pre>
 * @author Steven R. Gould
 * @since 0.4
 */
public class DelimitedTextOutputter implements Outputter
{
    /**
     * Defines the character string used to separate values.
     */
    private String delimiter = ",";
    
    /**
     * Set to true only if the first line/row of the output is to be treated
     * as a header row. That is, whether the first row should contain a list
     * of variable names.
     */
    private boolean outputHeaderRow = false;
    
    /**
     * Defines the name to be used for the dependent variable when outputing
     * a header row including variable names. By default, this is simply
     * "dependent value", but can be overridden using
     * {@link #setDependentVariableName}.
     */
    private String dependentVariableName = "dependent value";
    
    /**
     * Stores the file writer to which output should be written by the output
     * methods.
     */
    private BufferedWriter out;
    
    /**
     * Constructs a new DelimitedTextOutputter that writes its output to the
     * named file.
     * @param filename the name of the file to write the data points to.
     * @throws IOException if the specified file is not found or if some other
     * I/O error occurs.
     */
    public DelimitedTextOutputter( String filename )
        throws IOException
    {
        this( new FileWriter(filename) );
    }
    
    /**
     * Constructs a new DelimitedTextOutputter that writes its output to the
     * named file. If the second argument is true, then output will be
     * appended to the end of the file rather over-writing the file.
     * @param filename the name of the file to write the data points to.
     * @param append if true, then output will be appended to the end of the
     * file.
     * @throws IOException if the specified file is not found or if some other
     * I/O error occurs.
     */
    public DelimitedTextOutputter( String filename, boolean append )
        throws IOException
    {
        this( new FileWriter(filename, append) );
    }
    
    /**
     * Constructs a new DelimitedTextOutputter that writes its output to the
     * given file.
     * @param file the File object specifying the file to write the data points
     * to.
     * @throws IOException if the specified file is not found or if some other
     * I/O error occurs.
     */
    public DelimitedTextOutputter( File file )
        throws IOException
    {
        this( new FileWriter(file) );
    }
    
    /**
     * Constructs a new DelimitedTextOutputter that writes its output to the
     * given file. If the second argument is true, then output will be
     * appended to the end of the file rather over-writing the file.
     * @param file the File object specifying the file to write the data points
     * to.
     * @param append if true, then output will be appended to the end of the
     * file.
     * @throws IOException if the specified file is not found or if some other
     * I/O error occurs.
     */
    public DelimitedTextOutputter( File file, boolean append )
        throws IOException
    {
        this( new FileWriter(file.getPath(), append) );
    }
    
    /**
     * Constructs a new DelimitedTextOutputter that writes its output to the
     * given Writer object.
     * @param writer the Writer object to which the data points should be
     * output.
     */
    public DelimitedTextOutputter( Writer writer )
    {
        this.out = new BufferedWriter( writer );
    }
    
    /**
     * Returns the current field delimiter. By default, this is set to be a
     * comma. The value used can be overridden using the {@link #setDelimiter}
     * method, <em>before</em> invoking output.
     * @return the current delimiter that will be used to separate fields.
     */
    public String getDelimiter()
    {
        return delimiter;
    }
    
    /**
     * Sets the field delimiter to the given String.
     * @param delimiter the new delimiter to use to separate fields.
     */
    public void setDelimiter( String delimiter )
    {
        this.delimiter = delimiter;
    }
    
    /**
     * Writes a DataSet - a collection of DataPoints - to the current writer.
     * The DataSet should contain all DataPoints to be output.
     *
     * <p>Depending on the setting of outputHeaderRow, a header row containing
     * the variable names of the data points will be output. To enable/disable
     * this feature, use the {@link #setOutputHeaderRow} method.</li>
     * @param dataSet the DataSet to be output to the current writer.
     * @throws IOException if an I/O error occurs.
     */
    public void output( DataSet dataSet )
        throws IOException
    {
        if ( outputHeaderRow )
            writeHeader( dataSet.getIndependentVariables() );
        
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                output( dataPoint );
            }
        
        out.flush();
    }
    
    /**
     * Outputs the given array of variable names as comma-separated values
     * on a single line.
     * @param varNames the variable names to output.
     */
    private void writeHeader( String[] varNames )
        throws IOException
    {
        // Output each column/variable name
        for ( int var=0; var<varNames.length; var++ )
            {
                // If string contains a comma, we'll need to quote it
                //  and escape any quotes within the string
                if ( varNames[var].indexOf(delimiter) >= 0 )
                    {
                        out.write( '"' );
                        
                        // Requires J2SDFK 1.4 or later
                        //out.write( varNames[var].replaceAll("\"","\\\"") );
                        
                        // J2SDK 1.3 version
                        out.write( varNames[var] );
                        
                        out.write( '"' );
                    }
                else
                    out.write( varNames[var] );
                
                out.write( delimiter );
            }
        
        // Output dependent variable name
        out.write( dependentVariableName );
        
        out.newLine();
    }
    
    /**
     * Outputs the given DataPoint to the current Writer.
     * @param dataPoint the DataPoint to output to the current Writer.
     * @throws IOException if an I/O error occurs.
     */
    public void output( DataPoint dataPoint )
        throws IOException
    {
        String[] varNames = dataPoint.getIndependentVariableNames();
        
        // Output each column/variable name
        for ( int var=0; var<varNames.length; var++ )
            out.write( ""+dataPoint.getIndependentValue(varNames[var])
                       +delimiter );
        
        // Output dependent variable name
        out.write( ""+dataPoint.getDependentValue() );
        
        out.newLine();
    }
    
    /**
     * Close the DelimitedTextOutputter, flushing it first. Once a
     * DelimitedTextOutputter has been closed, further output invocations will
     * cause an IOException to be thrown. Closing a previously-closed stream,
     * however, has no effect.
     * @throws IOException if an I/O error occurs.
     */
    public void close()
        throws IOException
    {
        out.flush();
        out.close();
    }
    
    /**
     * Returns the current name to be used as the dependent variable name
     * when outputting a header row. By default, this is simply
     * "dependent value", but can be overridden using
     * {@link #setDependentVariableName}.
     * @return the current name to be used as the dependent variable name
     * when outputting a header row.
     */
    public String getDependentVariableName()
    {
        return dependentVariableName;
    }
    
    /**
     * Sets the name/label to be used as the dependent variable name when
     * outputting a header row. Note that if no header row is output, then
     * setting this value will have no impact on the output.
     * @param name the name/label to be use for the dependent variable name
     * in any subsequent output.
     */
    public void setDependentVariableName( String name )
    {
        this.dependentVariableName = name;
    }
    
    /**
     * Enables/Disables the output of the header row containing column/field
     * names. When the {@link #output(DataSet)} method is invoked, the first
     * row/line will contain a comma-separated list of the column names if this
     * option is enabled before calling the output method.
     * @param outputHeaderRow set to true if the header row is to be output;
     * or false to suppress output of the header row.
     */
    public void setOutputHeaderRow( boolean outputHeaderRow )
    {
        this.outputHeaderRow = outputHeaderRow;
    }
}
// Local Variables:
// tab-width: 4
// End:
