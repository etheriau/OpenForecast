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

import java.util.Iterator;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;
import net.sourceforge.openforecast.Observation;


/**
 * Implements a variety of tests of the DataSet class.
 */
public class DataSetTest extends OpenForecastTestCase
{
    private static final int SIZE = 10;
    private DataSet dataSet1 = new DataSet();
    private DataSet dataSet2 = new DataSet();
    private DataSet dataSet3 = new DataSet();
    private DataSet dataSet4 = new DataSet(); // Different data set
    
    public DataSetTest( String name )
    {
        super(name);
    }
    
    /**
     * Creates four simple DataSet for use by the tests. The first three
     * DataSets are created to contain the same data (though different
     * DataPoint objects), whereas the fourth DataSet is the same size but
     * contains different data as the others.
     */
    public void setUp()
    {
        dataSet1 = new DataSet();
        dataSet2 = new DataSet();
        dataSet3 = new DataSet();
        dataSet4 = new DataSet(); // Different data set
        
        for ( int count=0; count<SIZE; count++ )
            {
                DataPoint dp1 = new Observation( (double)count );
                DataPoint dp2 = new Observation( (double)count );
                DataPoint dp3 = new Observation( (double)count );
                DataPoint dp4 = new Observation( (double)count );
                
                dp1.setIndependentValue( "x", count );
                dp2.setIndependentValue( "x", count );
                dp3.setIndependentValue( "x", count );
                dp4.setIndependentValue( "x", count+1 );
                
                dataSet1.add( dp1 );
                dataSet2.add( dp2 );
                dataSet3.add( dp3 );
                dataSet4.add( dp4 );
            }
        
        // Verify data set contains the correct number of entries
        assertTrue("Checking dataSet1 contains correct number of data points",
                   dataSet1.size() == SIZE );
        assertTrue("Checking dataSet2 contains correct number of data points",
                   dataSet2.size() == SIZE );
        assertTrue("Checking dataSet3 contains correct number of data points",
                   dataSet3.size() == SIZE );
        assertTrue("Checking dataSet4 contains correct number of data points",
                   dataSet4.size() == SIZE );
    }
    
    /**
     * Resets all DataSets between tests.
     */
    public void tearDown()
    {
        dataSet1.clear();
        dataSet2.clear();
        dataSet3.clear();
        dataSet4.clear();
        
        dataSet1 = null;
        dataSet2 = null;
        dataSet3 = null;
        dataSet4 = null;
    }
    
    /**
     * Tests the correct initialization of a DataSet.
     */
    public void testDataSet()
    {
        DataSet data = new DataSet( dataSet1 );
        
        // Verify data set contains the correct number of entries
        assertTrue( data.size() == dataSet1.size() );
        
        // Vefify that only one independent variable name is reported
        String[] independentVariables = data.getIndependentVariables();
        assertTrue( independentVariables.length == 1 );
        assertTrue( independentVariables[0].equals("x") );
        
        // Verify the dependent values stored
        Iterator<DataPoint> it = data.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                double value = dp.getDependentValue();
                double TOLERANCE = 0.001;
                assertTrue( value>-TOLERANCE && value<SIZE+TOLERANCE );
            }
    }
    
    /**
     * Tests the equals method of the DataSet class. Checks that the DataSet
     * equals method is reflexive, symmetric, transitive, and consistent.
     */
    public void testDataSetEqualsMethod()
    {
        assertTrue("Checking DataSet is reflexive: dataSet1.equals(dataSet1)",
                   dataSet1.equals(dataSet1) );
        
        assertTrue("Checking DataSet is symmetric: dataSet1.equals(dataSet2)",
                   dataSet1.equals(dataSet2) && dataSet2.equals(dataSet1)
                   && ( !dataSet1.equals(dataSet4)
                        && !dataSet4.equals(dataSet1) ) );
        
        assertTrue("Checking DataSet is transitive: x==y && y==z => x==z",
                   ( dataSet1.equals(dataSet2)
                     && dataSet2.equals(dataSet3)
                     == dataSet1.equals(dataSet3) )
                   && ( dataSet1.equals(dataSet2)
                        && dataSet2.equals(dataSet4)
                        == dataSet1.equals(dataSet4) ) );
        
        assertTrue("Checking DataSet is consistent: x==y or x!=y consistently",
                   dataSet1.equals(dataSet2) == dataSet1.equals(dataSet2)
                   && dataSet1.equals(dataSet4) == dataSet1.equals(dataSet4) );
        
        assertFalse("Checking DataSet.equals() handles nulls",
                    dataSet1.equals(null) );
    }
    
    /**
     * Implements a test of the DataSet contains and containsAll methods.
     */
    public void testContains()
    {
        assertTrue("Checking for self-containment",
                   dataSet1.containsAll(dataSet1));
        
        assertTrue("Checking for containment of similar data sets (1)",
                   dataSet1.containsAll(dataSet2));
        
        assertTrue("Checking for containment of similar data sets (2)",
                   dataSet2.containsAll(dataSet1));
        
        assertFalse("Checking for non-containment",
                    dataSet1.containsAll(dataSet4));
        
        Iterator<DataPoint> it = dataSet1.iterator();
        assertTrue("Checking for valid iterator",
                   it.hasNext());
        
        DataPoint dp = (DataPoint)it.next();
        assertTrue("Checking for individual DataPoint containment",
                   dataSet1.contains(dp));
        assertFalse("Checking for individual DataPoint non-containment",
                    dataSet4.contains(dp));
    }
    
    /**
     * Validates the hash code contract. In the java.lang.Object javadocs, it
     * states that the general contract is:
     * <ul>
     *  <li>Whenever it is invoked on the same object more than once during
     *  an execution of a Java application, the hashCode method must
     *  consistently return the same integer, provided no information used in
     *  equals comparisons on the object is modified. This integer need not
     *  remain consistent from one execution of an application to another
     *  execution of the same application.</li>
     *
     *  <li>If two objects are equal according to the equals(Object) method,
     *  then calling the hashCode method on each of the two objects must
     *  produce the same integer result.</li>
     *
     *  <li>It is not required that if two objects are unequal according to
     *  the equals(Object) method, then calling the hashCode method on each
     *  of the two objects must produce distinct integer results.</li>
     * </ul>
     * <p>This test case tests each of these.
     */
    public void testHashCode()
    {
        assertEquals("Checking hash code function returns consistent value",
                     dataSet1.hashCode(), dataSet1.hashCode());
        
        assertTrue("Checking ds1.equals(ds2) => hash codes are equal",
                   (dataSet1.equals(dataSet2)
                    && dataSet1.hashCode()==dataSet2.hashCode())
                   || !(dataSet1.equals(dataSet2)) );
    }
    
    /**
     * Implements a test of the DataSet remove and removeAll methods.
     */
    public void testRemove()
    {
        Iterator<DataPoint> it = dataSet1.iterator();
        assertTrue("Checking for valid iterator",
                   it.hasNext());
        
        // Save initial size of data set
        int initialSize = dataSet1.size();
        
        DataPoint dp = (DataPoint)it.next();
        assertTrue("Checking dataSet1 contains data point",
                   dataSet1.contains(dp));
        
        // Remove a single data point
        dataSet1.remove( dp );
        assertEquals("Checking size of data set decreased after removing a data point",
                     initialSize-1, dataSet1.size());
        
        try
            {
                assertTrue("Checking data set changed after removeAll",
                           dataSet1.removeAll(dataSet1));
                
                assertTrue("Checking size of data set after removeAll",
                           dataSet1.isEmpty() && dataSet1.size()==0);
                
                fail("removeAll is not supposed to be supported yet!");
            }
        catch ( UnsupportedOperationException ex )
            {
                // Expected result - at least until removeAll is implemented
            }
    }
    
    /**
     * Implements a test of the DataSet retainAll method.
     */
    public void testRetainAll()
    {
        assertEquals("Checking dataSet1.equals(dataSet2)",
                     dataSet1, dataSet2);
        
        // Remove half of the DataPoints from dataSet2
        for ( int i=0; i<SIZE/2; i++ )
            {
                Iterator<DataPoint> it = dataSet2.iterator();
                assertTrue("Checking for valid iterator",
                           it.hasNext());
                
                dataSet2.remove( (DataPoint)it.next() );
            }
        
        int expectedSize = dataSet2.size();
        
        try
            {
                // Use retainAll to remove data points from dataSet1
                dataSet1.retainAll( dataSet2 );
                assertEquals("Checking size of dataSet1 after retaingAll",
                             expectedSize, dataSet1.size());
                
                assertEquals("Checking size of dataSet1 and dataSet2 after retainAll",
                             dataSet1.size(), dataSet2.size());
                
                fail("retainAll is not supposed to be supported yet!");
            }
        catch ( UnsupportedOperationException ex )
            {
                // Expected result - at least until retainAll is implemented
            }
    }
    
    /**
     * Tests the correct sorting of a DataSet.
     */
    public void testDataSetSorting()
    {
        // Does not use any of the DataSets created by setUp
        
        DataSet data = new DataSet();
        
        for ( int count=0; count<10; count++ )
            {
                DataPoint dp = new Observation( (double)count );
                dp.setIndependentValue( "x", 10-count );
                
                data.add( dp );
            }
        
        // Sort the data set
        data.sort( "x" );
        
        // Verify the data set is sorted in ascending order of variable "x"
        Iterator<DataPoint> it = data.iterator();
        double lastX = -1.0;
        while ( it.hasNext() )
            {
                DataPoint dp = (DataPoint)it.next();
                double valueX = dp.getIndependentValue("x");
                assertTrue( valueX > lastX );
                lastX = valueX;
            }
    }
}
// Local variables:
// tab-width: 4
// End:
