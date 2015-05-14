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

package net.sourceforge.openforecast.tests;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.input.CSVBuilder;



public class CSVBuilderTest extends OpenForecastTestCase
{
    // Constants used to determine size of test
    private double TOLERANCE = 0.000001;
    
    /**
     * Tests the correct initialization of a DataSet from a CSV file where
     * the input is valid, yet poorly and irregularly formatted. For example,
     * the CSVBuilder is supposed to treat as a zero field two commas following
     * each other. This test will also test naming the columns and the use of
     * blank lines and comments in the input.
     */
    public void testExtremeCSVBuilder()
        throws FileNotFoundException, IOException
    {
        // Constants used to determine size of test
        double expectedValue[] = { 4,5,6,7,8 };
        int numberOfDataPoints = expectedValue.length;
        
        // Create test CSV file
        File testFile = File.createTempFile( "test", ".csv" );
        PrintStream out = new PrintStream( new FileOutputStream(testFile) );
        out.println("# This is a test CSV file with various 'peculiarities'");
        out.println(" # thrown in to try and trip it up");
        out.println("Field1, Field2, \"Field, 3\", Observation");
        out.println("-1, -2 ,-3,4");
        out.println(",,,5");
        out.println(" 1 , 2 , 3 , 6 ");
        out.println(" 2, 4, 6, 7");
        out.println("3 ,6 ,9 ,8");
        out.close();
        
        // Create CSV builder and use it to create the DataSet
        CSVBuilder builder = new CSVBuilder( testFile, true );
        DataSet dataSet = builder.build();
        
        // Verify data set contains the correct number of entries
        assertEquals( "DataSet created is of the wrong size",
                      numberOfDataPoints, dataSet.size() );
        
        // Vefify that only three independent variable names are reported
        String[] independentVariables = dataSet.getIndependentVariables();
        assertEquals( "Checking the correct number of independent variables",
                      3, independentVariables.length );
        
        // Note these will have been sorted into alphabetical order
        assertTrue( "Checking variable 0 name is as expected",
                    independentVariables[0].compareTo("Field, 3")==0 );
        assertTrue( "Checking variable 1 name is as expected",
                    independentVariables[1].compareTo("Field1")==0 );
        assertTrue( "Checking variable 2 name is as expected",
                    independentVariables[2].compareTo("Field2")==0 );
        
        // Test the data set created by the builder
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                double field1 = dataPoint.getIndependentValue("Field1");
                double field2 = dataPoint.getIndependentValue("Field2");
                double field3 = dataPoint.getIndependentValue("Field, 3");
                
                // field2 was set to twice field1
                // field3 was set to three times field1
                assertTrue( "Checking independent values are correct",
                            field2==2*field1 && field3==3*field1 );
                
                // The data was set up with this simple equation
                double expectedResult = 5.0 + field1;
                
                assertEquals("Checking data point "+dataPoint,
                             expectedResult, dataPoint.getDependentValue(),
                             TOLERANCE);
            }
        
        // Clean up - remove test file
        testFile.delete();
    }
    
    /**
     * Tests the correct initialization of a DataSet from a CSV file.
     */
    public void testCSVBuilder()
        throws FileNotFoundException, IOException
    {
        // Constants used to determine size of test
        int MAX_X1 = 10;
        int MAX_X2 = 10;
        double TOLERANCE = 0.000001;
        
        // Set up array for expected results
        double expectedValue[] = new double[ MAX_X1 * MAX_X2 ];
        
        // Create test CSV file
        File testFile = File.createTempFile( "test", ".csv" );
        PrintStream out = new PrintStream( new FileOutputStream(testFile) );
        out.println( "# This is a test CSV file" );
        int numberOfDataPoints = 0;
        for ( int x1=0; x1<MAX_X1; x1++ )
            for ( int x2=0; x2<MAX_X2; x2++ )
                {
                    expectedValue[numberOfDataPoints] = x1+2*x2+3.14;
                    out.println( x1+", "+x2+", "
                                 +expectedValue[numberOfDataPoints] );
                    numberOfDataPoints++;
                }
        out.close();
        
        // Create CSV builder and use it to create the DataSet
        CSVBuilder builder = new CSVBuilder( testFile );
        DataSet dataSet = builder.build();
        
        // Verify data set contains the correct number of entries
        assertEquals( "DataSet created is of the wrong size",
                      numberOfDataPoints, dataSet.size() );
        
        // Vefify that only two independent variable names are reported
        String[] independentVariables = dataSet.getIndependentVariables();
        assertTrue( independentVariables.length == 2 );
        assertTrue( independentVariables[0].equals("x1") );
        assertTrue( independentVariables[1].equals("x2") );
        
        // Check the data points in the data set. This may not be a good
        //  test since it is dependent on the order of the data points in
        //  the 2-d array
        checkResults( dataSet, expectedValue );
        
        // Test the data set created by the builder
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                double x1 = dataPoint.getIndependentValue("x1");
                double x2 = dataPoint.getIndependentValue("x2");
                
                double expectedResult = x1+2*x2+3.14;
                
                assertEquals("Checking data point "+dataPoint,
                             expectedResult, dataPoint.getDependentValue(),
                             TOLERANCE);
            }
        
        // Clean up - remove test file
        testFile.delete();
    }
    
    /**
     * Tests the correct initialization of a DataSet from a CSV file when the
     * CSVBuilder does not know whether or not there is a header row and the
     * CSV file contains one. This tests for the correct "auto detection" of
     * a header row.
     */
    public void testUnknownHeaderWithHeader()
        throws FileNotFoundException, IOException
    {
        // Constants used to determine size of test
        int MAX_X1 = 3;
        int MAX_X2 = 3;
        double TOLERANCE = 0.000001;
        
        // Set up array for expected results
        double expectedValue[] = new double[ MAX_X1 * MAX_X2 ];
        
        // Create test CSV file
        File testFile = File.createTempFile( "test", ".csv" );
        PrintStream out = new PrintStream( new FileOutputStream(testFile) );
        out.println( "# This is a test CSV file" );
        
        // Output header row containing column names
        for ( int x1=0; x1<MAX_X1-1; x1++ )
            out.print( "Col"+(x1+1)+", " );
        out.println( "Col"+MAX_X1 );
        
        // Output some test data
        int numberOfDataPoints = 0;
        for ( int x1=0; x1<MAX_X1; x1++ )
            for ( int x2=0; x2<MAX_X2; x2++ )
                {
                    expectedValue[numberOfDataPoints] = x1+2*x2+3.14;
                    out.println( x1+", "+x2+", "
                                 +expectedValue[numberOfDataPoints] );
                    numberOfDataPoints++;
                }
        out.close();
        
        // Create CSV builder without specifying that it has a header row,
        // then use it to create the DataSet
        CSVBuilder builder = new CSVBuilder( testFile );
        DataSet dataSet = builder.build();
        
        // Verify data set contains the correct number of entries
        assertEquals( "DataSet created is of the wrong size",
                      numberOfDataPoints, dataSet.size() );
        
        // Vefify that only two independent variable names are reported
        //  with the expected names
        String[] independentVariables = dataSet.getIndependentVariables();
        assertTrue( independentVariables.length == 2 );
        assertTrue( independentVariables[0].compareTo("Col1")==0 );
        assertTrue( independentVariables[1].compareTo("Col2")==0 );
        
        // Check the data points in the data set. This may not be a good
        //  test since it is dependent on the order of the data points in
        //  the 2-d array
        checkResults( dataSet, expectedValue );
        
        // Test the data set created by the builder
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                double x1 = dataPoint.getIndependentValue("Col1");
                double x2 = dataPoint.getIndependentValue("Col2");
                
                double expectedResult = x1+2*x2+3.14;
                
                assertEquals("Checking data point "+dataPoint,
                             expectedResult, dataPoint.getDependentValue(),
                             TOLERANCE);
            }
        
        // Clean up - remove test file
        testFile.delete();
    }
    
    
    /**
     * Tests the correct initialization of a DataSet from a CSV file when the
     * CSVBuilder does not know whether or not there is a header row and the
     * CSV file contains one. This tests for the correct "auto detection" of
     * a header row.
     */
    public void testUnknownHeaderWithoutHeader()
        throws FileNotFoundException, IOException
    {
        // Constants used to determine size of test
        int MAX_X1 = 3;
        int MAX_X2 = 3;
        double TOLERANCE = 0.000001;
        
        // Set up array for expected results
        double expectedValue[] = new double[ MAX_X1 * MAX_X2 ];
        
        // Create test CSV file
        File testFile = File.createTempFile( "test", ".csv" );
        PrintStream out = new PrintStream( new FileOutputStream(testFile) );
        out.println( "# This is a test CSV file" );
        
        // Output some test data
        int numberOfDataPoints = 0;
        for ( int x1=0; x1<MAX_X1; x1++ )
            for ( int x2=0; x2<MAX_X2; x2++ )
                {
                    expectedValue[numberOfDataPoints] = x1+2*x2+3.14;
                    out.println( x1+", "+x2+", "
                                 +expectedValue[numberOfDataPoints] );
                    numberOfDataPoints++;
                }
        out.close();
        
        // Create CSV builder without specifying that it has a header row,
        // then use it to create the DataSet
        CSVBuilder builder = new CSVBuilder( testFile );
        DataSet dataSet = builder.build();
        
        // Verify data set contains the correct number of entries
        assertEquals( "DataSet created is of the wrong size",
                      numberOfDataPoints, dataSet.size() );
        
        // Vefify that only two independent variable names are reported
        //  with default names
        String[] independentVariables = dataSet.getIndependentVariables();
        assertTrue( independentVariables.length == 2 );
        assertTrue( independentVariables[0].compareTo("x1")==0 );
        assertTrue( independentVariables[1].compareTo("x2")==0 );
        
        // Check the data points in the data set. This may not be a good
        //  test since it is dependent on the order of the data points in
        //  the 2-d array
        checkResults( dataSet, expectedValue );
        
        // Test the data set created by the builder
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                double x1 = dataPoint.getIndependentValue("x1");
                double x2 = dataPoint.getIndependentValue("x2");
                
                double expectedResult = x1+2*x2+3.14;
                
                assertEquals("Checking data point "+dataPoint,
                             expectedResult, dataPoint.getDependentValue(),
                             TOLERANCE);
            }
        
        // Clean up - remove test file
        testFile.delete();
    }
    
    public CSVBuilderTest( String name )
    {
        super(name);
    }
}
// Local variables:
// tab-width: 4
// End:
