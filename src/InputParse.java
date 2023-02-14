import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputParse {
    private final String FILE_PATH; // the input file path
    private final HashMap<String, Variable> name_to_var;
    public InputParse(String file_path, HashMap<String, Variable> name_to_var)
    {
        this.FILE_PATH = file_path;
        this.name_to_var = name_to_var;
    }

    private static ArrayList<String> ReadLines(String FILE_PATH)
    {
        try
        {
            File f = new File(FILE_PATH);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            ArrayList<String> lines = new ArrayList<>();
            reader.readLine();
            String line = reader.readLine();
//            lines.add(line);
            while(line != null)
            {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;

        }
        catch (java.io.FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static String GetXmlPath(String INPUT_PATH)
    {
        try {
            File f = new File(INPUT_PATH);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            return reader.readLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * P(B=T|J=T,M=T),1
     * @return [{{B,T}, {J,T}, {M,T}}]
     */
    public ArrayList<HashMap<Variable, Integer>> GetAssignments()
    {
        ArrayList<HashMap<Variable, Integer>> Assignments = new ArrayList<>();
        ArrayList<String> lines = ReadLines(this.FILE_PATH);
        assert lines != null;
        for(String line : lines)
        {
            if(line == null)
                continue;
            Assignments.add(ParseLineToAssignment(line));
        }
        return Assignments;
    }

    /**
     *
     * @param line receives P(B=T|J=T,M=T),1
     * @return return B=T,J=T,M=T
     */
    private String GetInsideProbability(String line)
    {
        String[] split_line = line.split("\\(");
        String[] inside_braces = split_line[1].split("\\)");
        String[] variables_with_comma = inside_braces[0].split("\\|");
        if(variables_with_comma.length == 1)
        {
            return variables_with_comma[0];
        }
        return (variables_with_comma[0] + "," + variables_with_comma[1]);
    }

    /**
     * example:
     * @param line receives P(B=T|J=T,M=T),1
     * @return {{B,T}, {J,T}, {M,T}}
     */
    private HashMap<Variable, Integer> ParseLineToAssignment(String line)
    {
        // B=T,J=T,M=T
        String inside_probability = GetInsideProbability(line);
        String[] Var_assignments = inside_probability.split(",");
        HashMap<Variable,Integer> assignment = new HashMap<>();
        for(String assign : Var_assignments)
        {
            String[] var_to_value = assign.split("=");
            Variable currentVar = name_to_var.get(var_to_value[0]);
            assignment.put(currentVar,currentVar.value_names.indexOf(var_to_value[1]));
        }
        return assignment;
    }

    /**
     * This is called with GetAssignments, to see what exactly is the query variable of,
     *  each assignment
     *  for example, parses for input.txt -> [B,B,B,J,J,J]
     * @return
     */
    public ArrayList<Variable> GetQueries()
    {
        ArrayList<Variable> queries = new ArrayList<>();
        ArrayList<String> lines = ReadLines(this.FILE_PATH);
        assert lines != null;
        for(String line : lines)
        {
            if(line == null)
                continue;
            // B=T,J=T,M=T
            String inside_probability = GetInsideProbability(line);
            String[] var_to_values = inside_probability.split(",");
            String var_name = var_to_values[0].split("=")[0];
            queries.add(name_to_var.get(var_name));
        }
        return queries;
    }
    public ArrayList<Integer> GetAlgorithmIndex()
    {
        ArrayList<String> lines = ReadLines(this.FILE_PATH);
        assert lines != null;
        ArrayList<Integer> indexes = new ArrayList<>();
        for(String line : lines)
        {
            String[] split_line = line.split(",");
            indexes.add(Integer.parseInt(split_line[split_line.length-1]));
        }
        return indexes;
    }
}
