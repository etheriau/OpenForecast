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


import java.util.Enumeration;
import java.util.Hashtable;


/**
 * Represents a single observation or data point, consisting of one value
 * of a dependent variable, and one or more values of independent variables.
 * Note that an Observation can refer to a previously observed data value,
 * or a future forecast value (an expected Observation).
 * @author Steven R. Gould
 * @since 0.3
 */
public class Observation implements DataPoint
{
    /**
     * Stores the dependent value for this observation.
     */
    private double dependentValue;
    
    /**
     * A collection of name-value pairs representing the independent variables
     * associated with the current dependentValue. In each name-value pair, the
     * name is a string representation of the name of the independent variable,
     * and the value is its value corresponding to the current value of the
     * dependent variable.
     */
    private Hashtable<String,Double> independentValues;
    
    /**
     * Initializes the current Observation with the given value of the
     * dependent variable. Note that this is provided as somewhat of a
     * convenience - it will be necessary to call setIndependentValue for
     * each independent variable name and value associated with this data
     * point
     * @param dependentValue the initial value of the dependent variable.
     */
    public Observation( double dependentValue )
    {
        this.dependentValue = dependentValue;
        independentValues = new Hashtable<String,Double>();
    }
    
    /**
     * Like a copy constructor, but constructs a new Observation object by
     * making a copy of the values from the given data point.
     * @param dataPoint the data point that is to be copied.
     */
    public Observation( DataPoint dataPoint )
    {
        this.dependentValue = dataPoint.getDependentValue();
        
        String[] varNames = dataPoint.getIndependentVariableNames();
        int numberOfVars = varNames.length;
        this.independentValues = new Hashtable<String,Double>( numberOfVars );
        for ( int dp=0; dp<numberOfVars; dp ++ )
            independentValues.put( varNames[dp],
                                   new Double(dataPoint.getIndependentValue(varNames[dp])));
    }
    
    /**
     * Sets the dependent variables' value to the given value.
     * @param value the new value for the dependent variable.
     */
    public void setDependentValue( double value )
    {
        this.dependentValue = value;
    }
    
    /**
     * Returns the current value assigned to the dependent variable. This value
     * can be changed by calling setDependentValue.
     * @return the current value of the dependent variable.
     */
    public double getDependentValue()
    {
        return dependentValue;
    }
    
    /**
     * Sets the named independent variables' value to the given value. Each
     * Observation can have one or more name-value pairs that represent the
     * independent variables and their associated values.
     * @param value the new value for the dependent variable.
     */
    public void setIndependentValue( String name, double value )
    {
        independentValues.put( name, new Double(value) );
    }
    
    /**
     * Returns the current value assigned to the named independent variable.
     * This value can be changed by calling setIndependentValue.
     * @param name the name of the independent variable required.
     * @return the current value of the named independent variable.
     */
    public double getIndependentValue( String name )
    {
        Double value = independentValues.get( name );
        if ( null == value )
            throw new
                IllegalArgumentException("The independent variable "+name
                                         + " is not defined for the current Observation. The current Observation is as follows: "
                                         +toString());
        
        return value.doubleValue();
    }
    
    /**
     * Returns an array of all independent variable names. No checks are made
     * to ensure that the names are unique. Rather, the names are extracted
     * directly from the names used in defining and initializing the
     * Observation.
     * @return an array of independent variable names for this Observation.
     */
    public String[] getIndependentVariableNames()
    {
        String varNames[] = new String[independentValues.size()];
        
        Enumeration<String> keys = independentValues.keys();
        for ( int index=0; keys.hasMoreElements(); index++ )
            varNames[index] = keys.nextElement();
        
        return varNames;
    }
    
    /**
     * Compares the given DataPoint to the current DataPoint/Observation,
     * and returns true if, and only if, the two data points represent the
     * same data point. That is, the dependent value matches and all the
     * independent values match.
     * @param dp the DataPoint to compare this DataPoint/Observation object
     *        to.
     * @return true if the given DataPoint represents the same data point
     *         as this DataPoint/Observation object.
     */
    public boolean equals( DataPoint dp )
    {
        if ( dependentValue != dp.getDependentValue() )
            return false;
        
        // Get a list of independent variable names
        String[] vars = getIndependentVariableNames();
        
        // Check that the given DataPoint has the same number of variables
        String[] dpVars = getIndependentVariableNames();
        if ( vars.length != dpVars.length )
            return false;
        
        // Check the values of each variable match
        for ( int v=0; v<vars.length; v++ )
            {
                double thisValue = ((Number)independentValues.get(vars[v])).doubleValue();
                double dpValue = dp.getIndependentValue( vars[v] );
                if ( thisValue != dpValue )
                    return false;
            }
        
        // All variable values match, so the given DataPoint represents the
        // same data point as this Observation
        return true;
    }
    
    /**
     * Overrides the default toString method to provide a more meaningful
     * output of an Observation. Each independent variable is listed with its
     * value, then the dependent variable and its value are listed.
     * @return a string representation of this Observation.
     */
    public String toString()
    {
        String result = "(";
        
        Enumeration<String> e = independentValues.keys();
        while ( e.hasMoreElements() )
            {
                Object obj = e.nextElement();
                String key = obj.toString();
                String value = independentValues.get(obj).toString();
                
                result += key + "=" + value + ",";
            }
        
        return result + "dependentValue=" + dependentValue + ")";
    }
}
/*
 * Local Variables:
 * tab-width: 4
 * End:
 */
