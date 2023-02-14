import org.xml.sax.SAXException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Bayesian_Network {

    HashMap<Variable, CPT> var_to_cpt;

    public Bayesian_Network(ArrayList<CPT> cpt_list)
    {
        this.var_to_cpt = CreateVarToCpt(cpt_list);
    }
    public static HashMap<Variable, CPT> CreateVarToCpt(ArrayList<CPT> cpt_list)
    {
        HashMap<Variable, CPT> var_to_cpt = new HashMap<>();
        for(CPT cpt : cpt_list)
        {
            var_to_cpt.put(cpt.query_var, cpt);
        }
        return var_to_cpt;
    }
    public double calculate_probability(HashMap<Variable, Integer> assignment, Variable query_variable)
    {
        return Conditional_probability.CalcQuery(this, assignment, query_variable);
    }

}
