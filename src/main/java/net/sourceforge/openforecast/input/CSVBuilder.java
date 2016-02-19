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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;

/**
 * Defines a Builder that can be used to construct a DataSet from CSV (Comma
 * Separated Values) file or input stream. This class makes for a quick and
 * easy "import" of data from a variety of other applications such as
 * Microsoft Excel.
 *
 * <p>The last value on each row is assumed to represent the dependent
 * variable. For example, if the independent variables are represented by x1,
 * x2, x3 and so on, and the dependent variable is represented by y, then a
 * row should be of the form:
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
 * with the values 1, 2, 3, and 4 respectively. By default, the independent
 * variables - just one in this example - would be named "x1", ...,
 * "x<em>i</em>", etc. To override this behavior, you can specify a "header
 * row" containing names for the individual variables (the "columns" of data).
 *
 * <p>Using the previous example, if the x<sub>i</sub> represented time, we
 * could define the data input as follows:
 *
 * <pre>
 *  time, observation
 *  1, 3
 *  2, 5
 *  3, 6
 *  4, 7
 * </pre>
 *
 * <p>This would name the dependent variable in this case, "time", instead of
 * the default, "x1".
 * @author Steven R. Gould
 * @since 0.4
 */
public class CSVBuilder extends AbstractBuilder
{
    /**
     * Constant defining the character used to separate values.
     */
    private final static char SEPARATOR = ',';
    
    /**
     * Indicates that it is unknown whether or not the file has a header row.
     * @see #hasHeaderRow
     */
    private static int HAS_HEADER_ROW_UNKNOWN = -1;
    
    /**
     * Indicates that the file does not have a header row.
     * @see #hasHeaderRow
     */
    private static int HAS_HEADER_ROW_FALSE = 0;
    
    /**
     * Indicates that the file does have a header row.
     * @see #hasHeaderRow
     */
    private static int HAS_HEADER_ROW_TRUE = 1;
    
    /**
     * Set to true only if the first line/row of the current input source is
     * to be treated as a header row.
     */
    private int hasHeaderRow = HAS_HEADER_ROW_UNKNOWN;
    
    /**
     * Stores the file reader from which data is to be read by the build
     * method.
     */
    private FileReader fileReader;
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file. The
     * fields will be named "x1", "x2", "x3", etc. unless the first non-comment
     * row contains one or more non-numeric fields in which case the first row
     * will be assumed to be a header row.
     * @param filename the name of the CSV file to read the input from.
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason cannot
     * be opened for reading.
     * @throws SecurityException if a security manager exists and its
     * <code>checkRead</code> method denies read access to the file.
     */
    public CSVBuilder( String filename )
        throws FileNotFoundException
    {
        this( new FileReader(filename) );
    }
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file,
     * and treats the first row of data as a header row containing field names.
     * @param filename the name of the CSV file to read the input from.
     * @param hasHeaderRow set to true if the CSV file has a header row.
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason cannot
     * be opened for reading.
     * @throws SecurityException if a security manager exists and its
     * <code>checkRead</code> method denies read access to the file.
     */
    public CSVBuilder( String filename, boolean hasHeaderRow )
        throws FileNotFoundException
    {
        this( new FileReader(filename), hasHeaderRow );
    }
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file. The
     * fields will be named "x1", "x2", "x3", etc. unless the first non-comment
     * row contains one or more non-numeric fields in which case the first row
     * will be assumed to be a header row.
     * @param file the File object specifying the CSV file to read the input
     * from.
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason cannot
     * be opened for reading.
     * @throws SecurityException if a security manager exists and its
     * <code>checkRead</code> method denies read access to the file.
     */
    public CSVBuilder( File file )
        throws FileNotFoundException
    {
        this( new FileReader(file) );
    }
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file.
     * The fields will be named "x1", "x2", "x3", etc.
     * @param file the File object specifying the CSV file to read the input
     * from.
     * @param hasHeaderRow set to true if the CSV file has a header row.
     * @throws FileNotFoundException if the file does not exist, is a
     * directory rather than a regular file, or for some other reason cannot
     * be opened for reading.
     * @throws SecurityException if a security manager exists and its
     * <code>checkRead</code> method denies read access to the file.
     */
    public CSVBuilder( File file, boolean hasHeaderRow )
        throws FileNotFoundException
    {
        this( new FileReader(file), hasHeaderRow );
    }
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file
     * input stream. The fields will be named "x1", "x2", "x3", etc. unless the
     * first non-comment row contains one or more non-numeric fields in which
     * case the first row will be assumed to be a header row.
     * @param reader the FileReader object specifying the CSV file reader to
     * read the input from.
     */
    public CSVBuilder( FileReader reader )
    {
        this.fileReader = reader;
        hasHeaderRow = HAS_HEADER_ROW_UNKNOWN;
    }
    
    /**
     * Constructs a new CSVBuilder that reads its input from the named file
     * input stream. The fields will be named "x1", "x2", "x3", etc.
     * @param reader the FileReader object specifying the CSV file reader to
     * read the input from.
     * @param hasHeaderRow set to true if the CSV file input stream has a
     * header row.
     */
    public CSVBuilder( FileReader reader, boolean hasHeaderRow )
    {
        this.fileReader = reader;
        this.hasHeaderRow
            = hasHeaderRow ? HAS_HEADER_ROW_TRUE : HAS_HEADER_ROW_FALSE;
    }
    
    /**
     * Retrieves a DataSet - a collection of DataPoints - from the current
     * input source. The DataSet should contain all DataPoints defined by
     * the input source.
     *
     * <p>In general, build will attempt to convert all lines/rows in the CSV
     * input to data points. The exceptions are as follows:
     * <ul>
     *  <li>Blank lines (lines containing only whitespace) will be ignored,
     *      and can be used for spacing in the input.</li>
     *  <li>Lines beginning with a '#' will be treated as comments, and will
     *      be ignored.</li>
     *  <li>If a header row is included - as specified in one of the
     *      constructors - then it will be treated as containing field/variable
     *      names for use by the DataSet.</li>
     * </ul>
     * @return a DataSet built from the current input source.
     * @throws IOException if an error occurred reading from the CSV file.
     */
    public DataSet build()
        throws IOException
    {
        DataSet dataSet = new DataSet();
        
        boolean firstLineRead = false;
        
        BufferedReader reader = new BufferedReader( fileReader );
        String line;
        do
            {
                // Get next line (trimmed)
                line = reader.readLine();
                if ( line == null )
                    continue;
                
                line = line.trim();
                
                // Skip blank lines
                if ( line.length() == 0 )
                    continue;
                
                // Skip comment lines
                if ( line.startsWith( "#" ) )
                    continue;
                
                if ( !firstLineRead )
                    {
                        firstLineRead = true;
                        if ( hasHeaderRow != HAS_HEADER_ROW_FALSE )
                            {
                                try
                                    {
                                        // Treat first line as header
                                        readHeaderRow( line );
                                        continue;
                                    }
                                catch ( NoHeaderException nhex )
                                    {
                                        // No header row found, so treat it
                                        //  as the first row of data
                                    }
                            }
                        
                        // Calculate how many independent values per line
                        // TODO: Fix this to handle quoted commas
                        int n = 0;
                        for ( int pos=0;
                              (pos=line.indexOf(SEPARATOR,pos)) > 0;
                              pos++ )
                            n++;
                        setNumberOfVariables( n );
                    }
                
                DataPoint dp = build( line );
                dataSet.add( dp );
            }
        while ( line != null );   // line == null when EOF is reached
        
        return dataSet;
    }
    
    /**
     * Parses the given line to extract the variable names.
     * @param line a String representing the line to parse for variable names.
     * @throws NoHeaderException if all the "column names" appear to be
     * numeric and therefore look like data values.
     */
    private void readHeaderRow( String line )
        throws NoHeaderException
    {
        // Temporary store for the variable names
        ArrayList<String> vars = new ArrayList<String>();
        
        boolean allData = true;
        int pos = 0;
        while ( pos < line.length() )
            {
                // Get position of next quote
                int nextQuote = line.indexOf("\"", pos);
                
                // Get position of next separator
                int nextSeparator = line.indexOf(SEPARATOR, pos);
                
                // if no next separator, then we're done
                //  since we ignore the name of the independent variable
                if ( nextSeparator < 0 )
                    break;
                
                if ( nextQuote < 0
                     || nextQuote > nextSeparator )
                    {
                        // Treat chars from pos to next separator as a label
                        String name = line.substring(pos,
                                                     nextSeparator);
                        vars.add( name );
                        try
                            {
                                // Skip over next set of characters
                                Double.parseDouble( name );
                            }
                        catch ( NumberFormatException nfex )
                            {
                                allData = false;
                            }
                        
                        pos = nextSeparator+1;
                        
                        continue;
                    }
                
                // Handle quoted strings
                allData = false;
                int secondQuote = line.indexOf("\"",nextQuote+1);
                
                String name = line.substring(nextQuote+1,secondQuote);
                vars.add( name );
                
                // We actually ignore any chars outside of quotes, yet
                //  before the next separator
                pos = line.indexOf(SEPARATOR,secondQuote)+1;
            }
        
        // If header row appears to be all numeric values,
        //  then parse it as first line of data
        if ( allData )
            throw new NoHeaderException("Header line looks like first line of data values");
        
        // Add variable names extracted to this Builder's list of
        //  variable names
        int n = vars.size();
        for ( int i=0; i<n; i++ )
            addVariable( ((String)vars.get(i)).trim() );
    }
    
    /**
     * Builds a DataPoint from the given CSV line. This method should only be
     * used to parse a line that is expected to be made up of numeric data
     * only. Use {@link #readHeaderRow} to read a header row if one is expected.
     * @param line the input line of comma separated values to parse and use
     * to construct a new DataPoint.
     * @return a DataPoint object with values as specified by the given input
     * String.
     */
    private DataPoint build( String line )
        throws IOException
    {
        Observation dataPoint = new Observation( 0.0 );
        
        StreamTokenizer tokenizer
            = new StreamTokenizer( new StringReader( line ) );
        
        tokenizer.commentChar( '#' );
        tokenizer.eolIsSignificant( true );
        tokenizer.parseNumbers();
        
        int i = 0;
        int n = getNumberOfVariables();
        int lastToken = SEPARATOR;
        do
            {
                // Read next token
                tokenizer.nextToken();
                switch ( tokenizer.ttype )
                    {
                    case '\t':
                    case ' ':
                        // Skip whitespace
                        continue;
                        
                    case SEPARATOR:
                        // Check for two adjacent commas
                        if ( lastToken != SEPARATOR )
                            break;
                        
                        // Two adjacent commas. Assume 0.0 between them
                        tokenizer.nval = 0.0;
                        
                        // Fall through, and handle as a number
                        
                    case StreamTokenizer.TT_NUMBER:
                        // Handle numbers appropriately as data
                        
                        // If this is the last value on the line, treat it
                        //  as the dependent variable value
                        if ( i == n )
                            dataPoint.setDependentValue(tokenizer.nval);
                        else
                            dataPoint.setIndependentValue(getVariableName(i),
                                                          tokenizer.nval);
                        
                        i++;
                        break;
                        
                    case StreamTokenizer.TT_WORD:
                        throw new IOException( "Invalid input in CSV file. Number expected, found '"+tokenizer.sval+"'");
                        
                    case StreamTokenizer.TT_EOL:
                    case StreamTokenizer.TT_EOF:
                        break;
                        
                    default:
                    }
                
                lastToken = tokenizer.ttype;
            }
        while ( tokenizer.ttype != StreamTokenizer.TT_EOF );
        
        return dataPoint;
    }
    
    /**
     * Private exception class used to handle case when we're not sure if the
     * first row is a data row or a header row. We try to parse it as a header
     * row. If a NoHeaderException is thrown, then the first row is treated as
     * data.
     */
    private class NoHeaderException extends Exception
    {
        private static final long serialVersionUID = 500L;
        
        public NoHeaderException( String msg )
        {
            super( msg );
        }
    }
}
// Local Variables:
// tab-width: 4
// End;
