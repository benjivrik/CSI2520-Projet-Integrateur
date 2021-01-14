
/**
 * 
 * Student : Benjamin Kataliko Viranga Student ID : 8842942 CSI2520 Projet
 * Intégrateur - Partie Orientée-objet (Java)
 * 
 */

import java.util.ArrayList;
import java.util.List;


public class Knapsack
{
    private int capacity;
    private List<Item> items;

    public Knapsack(int capacity)
    {
        this.capacity = capacity;
        this.items = new ArrayList<Item>();
    }

    /**
     * Ajouter un element dans le sac
     * @param item - un objet de la classe Item
     */

    public void addItem(Item item)
    {
        this.items.add(item);
    }

    /**
     * retirer un élément de la classe Item et retouner cette élément
     * @param item - un objet de la classe Item
     * @return Item
    */

    public Item removeItem(Item item)
    {
        if(! this.items.contains(item))
        {
            // retourner null si l'item  n'existe pas
            return null;
        }
        // retirer l'item en fonction de l'index de l'élément à retirer
        return this.items.remove(this.items.indexOf(item));
    }

    /**
     *  retourne somme du poid des éléments dans la sac
     *  @return int
     */

     public int getCurrentWeight()
     {
        int totalWeight = 0;
        for(Item item: this.items)
        {
            totalWeight += item.getWeight();
        }

        return totalWeight;
     }

     /**
     *  retourne somme de la valeur des éléments dans la sac
     *  @return int
     */

    public int getCurrentValue()
    {
       int totalValue = 0;
       for(Item item: this.items)
       {
           totalValue += item.getValue();
       }

       return totalValue;
    }

    /**
     * retourne une representation en string des items contenus dans le sac
     * Pour exemple, si les items A B et C sont contenus dans le sac, cette fonction retourne 
     * 'A B C'
     * @return string
     */

     public String getStringRepr()
     {
         String rpr = "";
         for(Item item: this.items)
         {
            rpr += item.getRepresentation();
            rpr += " ";
         }
         return rpr;
     }

    /**
     * getters and setters
     */
    public int getCapacity()
    {
        return this.capacity;
    }

    public void setCapacity(int capacity)
    {
        this.capacity = capacity;
    }

    /**
     * return the list of the all items inside the knapsack
     * @return List<Item> item
     */

    public List<Item> getItems()
    {
        return this.items;
    }

    /**
     * String representation of the object Knapsack
     */
    public String toString()
    {
        System.out.printf("\n----------- Information sur le sac (Knapsack) ------------\n");

        StringBuilder builder = new StringBuilder();

        builder.append(
            String.format("\n-> Bag capacity : %d \n", this.capacity)
        );
        builder.append(
            String.format("-> Items inside the bag : %s \n", this.getStringRepr())
        );
        builder.append(
            String.format("-> Current Value of the bag : %d \n", this.getCurrentValue())
        );
        builder.append(
            String.format("-> Current Weight of the bag : %d \n", this.getCurrentWeight())
        );

        String str = builder.toString();
        return str;
    }
}
