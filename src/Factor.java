import java.util.ArrayList;
import java.util.HashMap;

public class Factor {
    ArrayList<Variable> relevant_variables;
    HashMap<HashMap<Variable, Integer>, Double> factor_table;
    public Factor(ArrayList<Variable> relevant_variables)
    {
        factor_table = new HashMap<>();
        this.relevant_variables = new ArrayList<>(relevant_variables);
    }
    public Factor(CPT cpt)
    {
        this.relevant_variables = new ArrayList<>(cpt.variables);
        this.factor_table = new HashMap<>(cpt.cpt_table);
    }
    public Factor(Factor factor)
    {
        this.relevant_variables = new ArrayList<>(factor.relevant_variables);
        this.factor_table = new HashMap<>(factor.factor_table);
    }
    public void AddProbability(HashMap<Variable, Integer> assignment, double value)
    {
        factor_table.put(assignment, value);
    }
    public double GetProbability(HashMap<Variable, Integer> assignment)
    {
        return factor_table.get(assignment);
    }

}
