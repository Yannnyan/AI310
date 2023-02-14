import java.util.ArrayList;

public class Variable {
    /**
     * This class represents variables that can have multiple variable assignments,
     * example: boolean variable can have True or False assignments.
     * example2: a variable with 3 assignments can have v1 or v2 or v3 assignments.
     */
    public ArrayList<String> value_names;
    public String variable_name;

    public Variable()
    {
        this.value_names = new ArrayList<>();
    }
    public Variable(String variable_name, ArrayList<String> value_names)
    {
        this.variable_name = variable_name;
        this.value_names = value_names;

    }

    public ArrayList<String> getValue_names() {
        return value_names;
    }

    public void setValue_names(ArrayList<String> value_names) {
        this.value_names = value_names;
    }

    public String getVariable_name() {
        return variable_name;
    }

    public void setVariable_name(String variable_name) {
        this.variable_name = variable_name;
    }


}
