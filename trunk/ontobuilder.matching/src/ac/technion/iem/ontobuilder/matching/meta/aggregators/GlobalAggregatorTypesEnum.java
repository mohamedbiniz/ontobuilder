package ac.technion.iem.ontobuilder.matching.meta.aggregators;

public enum GlobalAggregatorTypesEnum
{
    SUM(0),
    MIN(1),
    AVERAGE(2);
    
    private int _id;
    
    private GlobalAggregatorTypesEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
    
    public String[] getAllNames()
    {
        String[] allNames = {"Sum", "Min", "Average"};
        return allNames;
    };
}
