public class GlobalCounter {
    public static int AdditionCounter = 0;
    public static int MultiplicationCounter = 0;

    public static void ZeroCounters()
    {
        AdditionCounter = 0;
        MultiplicationCounter = 0;
    }
    public static void Add()
    {
        AdditionCounter += 1;
    }
    public static void Multiply()
    {
        MultiplicationCounter+=1;
    }




}
