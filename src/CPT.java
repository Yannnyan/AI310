import org.xml.sax.SAXException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CPT {

    ArrayList<Variable> variables; // all the variables of the required conditional probability
    // {{M=T}, {J=T}, {E=T}, {A=T}, {B=T}}
   HashMap<HashMap<Variable, Integer>, Double> cpt_table;
   Variable query_var;

    /**
     * The first variable in the list is ALWAYS the query variable
     * @param variables_in_order the variable list
     * @throws SAXException
     */
   public CPT(ArrayList<Variable> variables_in_order) throws SAXException {
       if (variables_in_order.size() == 0)
            throw(new SAXException("list empty"));
       variables = variables_in_order;
       cpt_table = new HashMap<>();
       query_var = variables_in_order.get(0);
   }

    /**
     * It is used when we want to check if the table is complete,
     * and it is used when doing variable elimination.
     * @return The amount of assignments this table has
     */
   public int CalcTableSize()
   {
       int i = 1;
       for(Variable var : variables)
           i *= var.value_names.size();
       return i;
   }
    public void AddProbability(HashMap<Variable, Integer> assignment, double value)
    {
        cpt_table.put(assignment, value);
    }
    public Double GetProbability(HashMap<Variable, Integer> assignment)
    {
        return cpt_table.get(assignment);
    }
    public boolean CheckCPTComplete()
    {
        return cpt_table.size() == CalcTableSize();
    }
}
