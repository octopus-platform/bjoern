package bjoern.structures;

public class RegisterFamily {
    private String name;
    private int start;
    private int end;

    public RegisterFamily(String name, int start, int end)
    {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public boolean overlaps(RegisterFamily otherFamily)
    {
        boolean overlap;

        return start <= otherFamily.end && end >= otherFamily.start;
    }

    public void merge(RegisterFamily otherFamily)
    {
        name = otherFamily.name;
        start = Math.min(start, otherFamily.start);
        end = Math.max(end, otherFamily.end);
    }

    public String getName()
    {
        return name;
    }

}
