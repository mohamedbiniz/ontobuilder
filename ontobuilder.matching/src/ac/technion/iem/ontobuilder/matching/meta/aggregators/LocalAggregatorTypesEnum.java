package ac.technion.iem.ontobuilder.matching.meta.aggregators;

public enum LocalAggregatorTypesEnum
{
    SUM(0),
    PRODUCT(1),
    AVERAGE(2);
    
    private int _id;
    
    private LocalAggregatorTypesEnum(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
    
    public String[] getAllNames()
    {
        String[] allNames = {"Sum", "Product", "Average"};
        return allNames;
    };
}
