/*
 * @author: Dev Bharadwaj
 * 										         Operator
 * 								       				 |
 * 				--------------------------------------------------------------------------------
 * 				|								     |									       |
 * 			   Leaf 						       Unary                                     Binary				
 * 		-------------------			-------------------------------------	         --------------------
 * 		|                 |	        |       |         |        |        |            |                  |
 *    Source            Nested*   Select  Project  Aggregate  GroupBy  OrderBy     Join               Union
 *    					  | 	                      |
 *    			    -----------       -----------------------------------
 *    			    |         |       |      |      |     |      |      |
 *    			   In       Exists   Sum   Count   Avg   Mean   Mean   Limit 
 */

package edu.buffalo.cse562.model.operatorabstract;

import edu.buffalo.cse562.mock.Datum;

public interface Operator {

    public void dataIn(Datum data);

    public Datum dataOut();

}
