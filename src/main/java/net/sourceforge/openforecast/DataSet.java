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

package net.sourceforge.openforecast;


import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Represents a collection of data points. Data points are either observations
 * of past data (including both the values of the independent variables and the
 * observed value of the dependent variable), or forecasts or estimates of the
 * dependent variable (for a given set of independent variable values).
 *
 * <p>Generally when trying to forecast future values you'll use two data sets.
 * The first data set contains all of the observations, or historical data.
 * This data set is used to help initialize the selected forecasting model,
 * the details of which depend on the specific forecasting model. A second
 * data set is then created and initialized with data points describing the
 * values of the independent variables that are to be used to predict or
 * forecast values of the dependent variable.
 *
 * <p>When defining any data set it is important to provide as much information
 * as possible about the data. While on the surface it may seem trivial, the
 * more information you can provide about a data set (such as whether it is a
 * time-based series, the name of the independent variable representing time,
 * the number of data points/periods in a year), the better the forecasting
 * model will be able to model the data. This is because some models need this
 * type of data to even be applicable.
 * @author Steven R. Gould
 */
public class DataSet extends AbstractCollection<DataPoint>
{
    /**
     * Maintains the list of data points in this data set.
     */
    private Collection<DataPoint> dataPoints = new ArrayList<DataPoint>();
    
    /**
     * If this data set is a time-based series, then for best results the
     * timeVariable should be initialized to contain the name of the time
     * variable. If set to null, the data set is treated as being non
     * time-based.
     */
    private String timeVariable;
    
    /**
     * For time-series data, periodsPerYear should be initialized to the
     * number of periods - or data points - in a years worth of data.
     */
    private int periodsPerYear;
    
    /**
     * Constructs a new empty data set.
     */
    public DataSet()
    {
        timeVariable = null;
        periodsPerYear = 0;
    }
    
    /**
     * Copy constructor: constructs a new data set object by copying the given
     * data set.
     * @param dataSet the data set to copy from to initialize the new data set.
     */
    public DataSet( DataSet dataSet )
    {
        this( dataSet.getTimeVariable(),
              dataSet.getPeriodsPerYear(),
              dataSet.dataPoints );
    }
    
    /**
     * Constructs a new time-based data set with the named time variable, the
     * given number of data points in a year, and the given Collection of data
     * points. This is equivalent to using the default constructor, then
     * calling setTimeVariable, setPeriodsPerYear and addAll to initialize it.
     * @param timeVariable the name of the independent variable representing
     *        time.
     * @param periodsPerYear the number of periods - data points - in one years
     *        worth of data.
     * @param c a Collection of data points to initialize this data set with.
     * @see #setTimeVariable
     * @see #setPeriodsPerYear
     * @see #addAll
     */
    public DataSet( String timeVariable, int periodsPerYear, Collection<DataPoint> c )
    {
        this.timeVariable = timeVariable;
        this.periodsPerYear = periodsPerYear;
        
        addAll( c );
    }
    
    /**
     * Adds the given data point object to this data set.
     * @param obj the data point object to add to this set.
     * @return true if this collection changed as a result of the call. This is
     * consistent with the add method in the java.util.Collection class.
     * @throws ClassCastException if the specified object does not implement
     * the DataPoint interface.
     * @throws NullPointerException if the specified collection contains
     * one or more null elements.
     */
    public boolean add( DataPoint obj )
    {
        if ( obj == null )
            throw new NullPointerException("DataSet does not support addition of null DataPoints");
        
        return dataPoints.add( new Observation( (DataPoint)obj ) );
    }
    
    /**
     * Adds a collection of data points to this data set. All elements of the
     * collection must be DataPoints.
     * @param c a collection of data points to add to this data set.
     * @return true if this collection changed as a result of the call. This is
     * consistent with the add method in the java.util.Collection class.
     */
    public boolean addAll( Collection<? extends DataPoint> c )
    {
        // iterate through all elements in Collection
        //  adding a copy of each DataPoint to this DataSet
        Iterator<?> it = c.iterator();
        while ( it.hasNext() )
            add( (DataPoint)it.next() );
        
        return true;
    }
    
    /**
     * Removes all of the data points from this data set. This data set will
     * be empty after this method returns unless it throws an exception.
     */
    public void clear()
    {
        dataPoints.clear();
    }
    
    /**
     * Returns true if this data set contains no data points. Otherwise returns
     * false.
     * @return true if this data set is empty.
     */
    public boolean isEmpty()
    {
        return dataPoints.isEmpty();
    }
    
    /**
     * Returns true if this data set contains the given data point object; or
     * false otherwise. This data set is said to contain the given data point
     * iff <code>dataPoint.equals(dp)</code> returns true for some DataPoint
     * object, <code>dp</code>, within the set of data points.
     * @param obj the data point object to search for in this data set.
     * @return true if this data set contains dataPoint.
     * @throws ClassCastException if the specified object does not implement
     * the DataPoint interface.
     * @throws NullPointerException if the specified collection contains
     * one or more null elements.
     */
    public boolean contains( Object obj )
    {
        if ( obj == null )
            throw new NullPointerException("DataSet does not support null DataPoint objects");
        
        DataPoint dataPoint = (DataPoint)obj;
        
        Iterator<DataPoint> it = this.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                if ( dataPoint.equals(dp) )
                    return true;
            }
        
        return false;
    }
    
    /**
     * Returns true if this DataSet contains all of the DataPoints in the
     * specified collection.
     * @param c collection to be checked for containment in this collection.
     * @return true if this DataSet contains all of the DataPoints in the
     * specified collection.
     * @throws ClassCastException if the types of one or more elements in
     * the specified collection do not implement the DataPoint interface.
     * @throws NullPointerException if the specified collection contains
     * one or more null elements.
     */
    public boolean containsAll( Collection<?> c )
        throws ClassCastException, NullPointerException
    {
        Iterator<?> it = c.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = (DataPoint)it.next();
                if ( !this.contains(dp) )
                    return false;
            }
        
        return true;
    }
    
    /**
     * Removes a single instance of the specified data point object from this
     * data set, if it is present. Returns true if this collection contained
     * the specified element (or equivalently, if this collection changed as
     * a result of the call). 
     * @param obj the data point object to remove from this data set.
     * @return true if this collection changed as a result of the call. This
     * is consistent with the add method in the java.util.Collection class.
     * @throws ClassCastException if the specified object does not implement
     * the DataPoint interface.
     * @throws NullPointerException if the specified collection contains
     * one or more null elements.
     */
    public boolean remove( Object obj )
    {
        if ( obj == null )
            throw new NullPointerException("DataSet does not support null DataPoint objects");
        
        return dataPoints.remove( (DataPoint)obj );
    }
    
    /**
     * Returns the number of data points in this data set. If this data
     * contains more than Integer.MAX_VALUE elements, returns
     * Integer.MAX_VALUE.
     * @return the number of data points in this data set.
     */
    public int size()
    {
        return dataPoints.size();
    }
    
    /**
     * Returns an iterator over the data points in this data set. There are
     * no guarantees concerning the order in which the elements are returned.
     * @return an iterator over the points in this data set.
     */
    public Iterator<DataPoint> iterator()
    {
        return dataPoints.iterator();
    }
    
    /**
     * Sorts the given data set according to increasing value of the named
     * independent variable. The initial implementation of this sort method
     * appears a little cumbersome - it may be more efficient later to
     * implement a small quicksort routine here instead.
     * @param independentVariable the name of the independent variable to
     *        set by. The resulting data set will be sorted in increasing
     *        value of this variable.
     */
    public void sort( String independentVariable )
    {
        // TODO: check that independentVariable is even defined for this set
        
        // Create a new sorted map using the given comparator
        SortedMap<Double,DataPoint> sortedMap = new TreeMap<Double,DataPoint>( new Comparator<Double>()
            {
                public int compare( Double o1, Double o2 )
                    {
                        return o1.compareTo(o2);
                    }
            } );
        
        // Add each element in the array list to the sorted map.
        Iterator<DataPoint> it = dataPoints.iterator();
        while ( it.hasNext() )
            {
                // By putting each DataPoint in the list, it will
                // automatically sort them by key
                DataPoint dp = it.next();
                sortedMap.put(
                              new Double(dp.getIndependentValue(independentVariable)),
                              dp );
            }
        
        // Clear dataPoints
        dataPoints.clear();
        
        // Add all values from sorted map back into list in sorted order
        dataPoints.addAll( sortedMap.values() );
    }
    
    /**
     * Returns an ordered array of all independent variable names used in this
     * data set. The array is guaranteed not to contain duplicate names.
     * @return a sorted array of unique independent variable names for this
     *         data set.
     */
    public String[] getIndependentVariables()
    {
        ArrayList<String> variables = new ArrayList<String>();
        Iterator<DataPoint> it = dataPoints.iterator();
        while ( it.hasNext() )
            {
                DataPoint dp = it.next();
                String[] names = dp.getIndependentVariableNames();
                
                for ( int i=0; i<names.length; i++ )
                    if ( !variables.contains( names[i] ) )
                        variables.add( names[i] );
            }
        
        // Sort list
        Collections.sort( variables );
        
        // Convert the ArrayList to a String[]
        int count = variables.size();
        String names[] = new String[count];
        for ( int i=0; i<count; i++ )
            names[i] = (String)(variables.get(i));
        
        return names;
    }
    
    /**
     * Sets the name of the time variable for this data set. If this is not
     * set, then the data set will be treated as being non time-based. In
     * addition to setting the time variable for time series data, it is
     * strongly recommended that you also initialize the number of periods per
     * year with a call to setPeriodsPerYear.
     * @param timeVariable the name of the independent variable that represents
     *        the time data component. For example, this may be something like
     *        "t", "month", "period", "year", and so on.
     * @see #setPeriodsPerYear
     */
    public void setTimeVariable( String timeVariable )
    {
        this.timeVariable = timeVariable;
    }
    
    /**
     * Returns the time variable associated with this data set, or
     * <code>null</code> if no time variable has been defined.
     * @return the time variable associated with this data set.
     */
    public String getTimeVariable()
    {
        return timeVariable;
    }
    
    /**
     * Sets the number of periods - or data points - in a years worth of data
     * for time-series data. If this is not set, then no seasonality effects
     * will be considered when forecasting using this data set.
     *
     * <p>In addition to setting the number of periods per year, you must also
     * set the time variable otherwise any forecasting model will not be able
     * to consider the potential effects of seasonality.
     * @param periodsPerYear the number of periods in a years worth of data.
     * @see #setTimeVariable
     */
    public void setPeriodsPerYear( int periodsPerYear )
    {
        if ( periodsPerYear < 1 )
            throw new IllegalArgumentException( "periodsPerYear parameter must be at least 1" );
        
        this.periodsPerYear = periodsPerYear;
    }
    
    /**
     * Returns the number of periods - or data points - in a years worth of
     * data for time-series data. If this has not been set, then a value of 0
     * will be returned.
     * @return the number of periods in a years worth of data.
     */
    public int getPeriodsPerYear()
    {
        return periodsPerYear;
    }
    
    /**
     * Not currently implemented - always throws UnsupportedOperationException.
     * Removes all this DataSet's elements that are also contained in the
     * specified collection of DataPoint objects. After this call returns,
     * this DataSet will contain no elements in common with the specified
     * collection.
     * @param c DataPoint objects to be removed from this collection.
     * @return true if this DataSet changed as a result of the call.
     * @throws UnsupportedOperationException if the removeAll method is not
     * supported by this collection.
     * @throws ClassCastException if the types of one or more elements in the
     * specified DataSet are not DataPoint objects.
     * @throws NullPointerException if the specified collection contains one
     * or more null elements.
     */
    public boolean removeAll( Collection<?> c )
        throws UnsupportedOperationException
    {
        // TODO: Implement DataSet.removeAll
        throw new UnsupportedOperationException("DataSet.removeAll not yet supported");
    }
    
    /**
     * Not currently implemented - always throws UnsupportedOperationException.
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation). In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection. 
     * @param c elements to be retained in this collection.
     * @return true if this collection changed as a result of the call.
     * @throws UnsupportedOperationException if the retainAll method is not
     * supported by this collection.
     * @throws ClassCastException if the types of one or more elements in the
     * specified DataSet are not DataPoint objects.
     * @throws NullPointerException if the specified collection contains one
     * or more null elements.
     */
    public boolean retainAll( Collection<?> c )
        throws UnsupportedOperationException
    {
        // TODO: Implement DataSet.retainAll
        throw new UnsupportedOperationException("DataSet.retainAll not yet supported");
    }
    
    /**
     * Returns the hash code value for this collection, based on the
     * underlying Collection of DataPoints.
     * @return the hash code value for this collection.
     */
    public int hashCode()
    {
        return dataPoints.size()*100
            + getIndependentVariables().length;
    }
    
    /**
     * Indicates whether some other object, obj, is "equal to" this one.
     * Returns true if the Object, obj, represents another DataSet for which
     * {@link #equals(DataSet)} returns true; otherwise false.
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false
     * otherwise.
     * @see #equals(DataSet)
     */
    public boolean equals( Object obj )
    {
        if ( obj == null )
            return false;
        
        if ( !(obj instanceof DataSet) )
            return false;
        
        return this.equals( (DataSet)obj );
    }
    
    /**
     * Indicates whether some other DataSet is "equal to" this one. Returns
     * true if the DataSet, dataSet, represents another DataSet containing
     * exactly the same data points as this DataSet. Note that neither the
     * DataPoint objects, or the DataSet objects have to refer to the same
     * instance. They just must refer to a collection of DataPoints with the
     * same values for the independent and dependent variables.
     * @param dataSet the reference object with which to compare.
     * @return true if this object is the same as the dataSet argument; false
     * otherwise.
     */
    public boolean equals( DataSet dataSet )
    {
        if ( dataSet == null )
            return false;
        
        if ( this.size() != dataSet.size() )
            return false;
        
        // Iterate through all data points in dataSet,
        //  checking that the same DataPoint exists in this DataSet
        Iterator<DataPoint> it = dataSet.iterator();
        while ( it.hasNext() )
            {
                DataPoint dataPoint = it.next();
                if ( !this.contains( dataPoint ) )
                    return false;
            }
        
        return true;
    }
    
    /**
     * Overrides the default toString method. Lists all data points in this
     * data set. Note that if there are a large number of data points in this
     * data set, then the String returned could be very long.
     * @return a string representation of this data set.
     */
    public String toString()
    {
        String lineSeparator = System.getProperty("line.separator");
        String result = "( " + lineSeparator;
        
        Iterator<DataPoint> it = dataPoints.iterator();
        while ( it.hasNext() )
            {
                result += "  " + it.next().toString()
                    + lineSeparator;
            }
        
        return result + ")";
    }
}
// Local variables:
// tab-width: 4
// End:
