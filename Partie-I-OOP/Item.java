/**
 * 
 * Student : Benjamin Kataliko Viranga
 * Student ID : 8842942
 * CSI2520
 * Projet Intégrateur -  Partie Orientée-objet (Java)
 * 
 */

public class Item 
{

    // un Item est composé d'une valeur et d'un poids
    private int value;
    private int weight;
    // un item possède également sa représentation String
    private String repr;

    public Item(String repr, int value, int weight)
    {
        this.repr = repr;
        this.value = value;
        this.weight = weight;
    }
    
    /**
     * getters and setters
     */

    public String getRepresentation()
    {
        return this.repr;
    }
    public void setRepresentation(String repr)
    {
        this.repr = repr;
    }

    public int getValue()
    {
        return this.value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }
    
    public int getWeight()
    {
        return this.weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public String toString()
    {
        String item = String.format(
            "\nItem: %s\nValue: %s\nWeight: %s\n", 
             this.repr, this.value, this.weight
            );
        
        return item;
    }

}
