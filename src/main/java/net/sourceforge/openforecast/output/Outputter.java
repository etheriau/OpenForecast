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

import net.sourceforge.openforecast.DataSet;

/**
 * An interface that defines the minimal requirements for a class to be
 * considered an "outputter" - a class that can be used to assist with the
 * output of a DataSet (set of DataPoint objects) to a variety of different
 * output destinations. This serves to bring some consistency between the
 * potentially very different outputter classes.
 * @author Steven R. Gould
 * @since 0.4
 */
public interface Outputter
{
    /**
     * Outputs the DataSet - a collection of DataPoints - to the current
     * output destination. The DataSet should contain all DataPoints to be
     * output.
     * @param dataSet the DataSet to be output to the current output
     * destination.
     * @throws Exception if an error occurred writing/outputting the DataSet
     * to the output destination.
     */
    public void output( DataSet dataSet )
        throws Exception;
}
// Local Variables:
// tab-width: 4
// End:
