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


/**
 * Defines the interface to a single data point, consisting of one value of a
 * dependent variable, and one or more values of independent variables. Note
 * that a data point can refer to a previously observed data value, or a
 * future forecast value.
 *
 * <p>Note that in earlier versions DataPoint was defined as a class (not an
 * interface). Therefore any code written using OpenForecast 0.20 or earlier
 * that instantiates DataPoint objects directly will need to be modified. Use
 * the Observation class instead.
 * @see Observation
 * @author Steven R. Gould
 * @since 0.3
 */
public interface DataPoint
{
    /**
     * Sets the dependent variables' value to the given value.
     * @param value the new value for the dependent variable.
     */
    public void setDependentValue( double value );

    /**
     * Returns the current value assigned to the dependent variable. This value
     * can be changed by calling setDependentValue.
     * @return the current value of the dependent variable.
     */
    public double getDependentValue();

    /**
     * Sets the named independent variables' value to the given value. Each
     * data point can have one or more name-value pairs that represent the
     * independent variables and their associated values.
     * @param value the new value for the dependent variable.
     */
    public void setIndependentValue( String name, double value );

    /**
     * Returns the current value assigned to the named independent variable.
     * This value can be changed by calling setIndependentValue.
     * @param name the name of the independent variable required.
     * @return the current value of the named independent variable.
     */
    public double getIndependentValue( String name );

    /**
     * Returns an array of all independent variable names. No checks are made
     * to ensure that the names are unique. Rather, the names are extracted
     * directly from the names used in defining and initializing the data point.
     * @return an array of independent variable names for this data point.
     */
    public String[] getIndependentVariableNames();

    /**
     * Compares the given DataPoint object to the current DataPoint object,
     * and returns true if, and only if, the two data points represent the same
     * data point. That is, the dependent value matches for the matching
     * independent values.
     * @param dp the DataPoint to compare this DataPoint object to.
     * @return true if the given DataPoint object represents the same data
     *         point as this DataPoint object.
     */
    public boolean equals( DataPoint dp );
}
// Local Variables:
// tab-width: 4
// End:
