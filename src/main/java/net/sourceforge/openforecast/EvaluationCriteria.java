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
 * A class of constants defining the various "evaluation criteria" that can be
 * used to compare the accuracy of two forecasting models.
 * @since 0.5
 */
public class EvaluationCriteria
{
    /**
     * EvaluationCriteria referring to the Akaike Information Criteria measure.
     */
    public final static EvaluationCriteria AIC
        = new EvaluationCriteria("Akaike Information Citeria");

    /**
     * EvaluationCriteria referring to Bias.
     */
    public final static EvaluationCriteria BIAS
        = new EvaluationCriteria("Bias");
    
    /**
     * EvaluationCriteria referring to the Mean Absolute Deviation.
     */
    public final static EvaluationCriteria MAD
        = new EvaluationCriteria("Mean Absolute Deviation");
    
    /**
     * EvaluationCriteria referring to the Mean Absolute Percentage Error.
     */
    public final static EvaluationCriteria MAPE
        = new EvaluationCriteria("Mean Absolute Percentage Error");
    
    /**
     * EvaluationCriteria referring to the Mean Squared Error.
     */
    public final static EvaluationCriteria MSE
        = new EvaluationCriteria("Mean Squared Error");
    
    /**
     * EvaluationCriteria referring to the Sum of the Absolute Error.
     */
    public final static EvaluationCriteria SAE
        = new EvaluationCriteria("Sum Absolute Error");
    
    /**
     * EvaluationCriteria referring to a reasonable blend of all other
     * evaluation criteria. Rather than rely solely on one specific evaluation
     * criteria to pick a "good" model, assess them all and select the model
     * generally regarded as "better" by the majority of the criteria.
     */
    public final static EvaluationCriteria BLEND
        = new EvaluationCriteria("Blend of Bias, MAD, MAPE, MSE, and SAE");
    // Not really sure about the best short description for this one???
    
    private String description;
    
    /**
     * Creates a new EvaluationCriteria with the given description.
     * @param description a description of the evaluation criteria.
     */
    private EvaluationCriteria(String description)
    {
        this.description = description;
    }
    
    /**
     * Compares two EvaluationCriteria objects for equality.
     * @return true if the given Object is an EvaluationCriteria equivalent to
     * this EvaluationCriteria; otherwise returns false.
     */
    public boolean equals(Object obj)
    {
        if ( obj instanceof EvaluationCriteria
             && obj == this )
            return true;
        
        return false;
    }
    
    /**
     * Returns a textual description of this evaluation criteria object.
     * @return a string representation of the object.
     */
    public String toString()
    {
        return description;
    }
}
// Local Variables:
// tab-width: 4
// End:
