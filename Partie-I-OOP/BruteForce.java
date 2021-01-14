
/**
 * 
 * Student : Benjamin Kataliko Viranga
 * Student ID : 8842942
 * CSI2520
 * Projet Intégrateur -  Partie Orientée-objet (Java)
 * 
 */


import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Reference used for building this class : https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java

public class BruteForce 
{
    private int capacity;
    private boolean overflow = false;
    private  int currentValue = 0;
    private  int maxValue = 0;
    private  int currentWeight = 0;

    /**
     * Cette méthode évalue la méthode Brute Force pour le knapsack problem
     * @params : capacity - capacité du sac
     *           availableItems - Liste des items disponible à ajouter dans le sac
     *           file - which is the file in which the solution has to be written 
     * 
     * @return la valeur optimale des possibilité d'items à ajouter dans le sac
     */

    public int bruteForce(int capacity, 
                          List<Item> availableItems, File file)
    {
        // capacité du sac
        this.capacity = capacity;

        // boolean pour indiquer que les valeurs dans le tableau déborde
        overflow =  false; 


        // nombre d'items disponibles
        int n_items = availableItems.size();

        // tableaux pour le poids ainsi que les valeurs des items
        int[] items_values = new int[n_items];
        int[] items_weight = new int[n_items];
        // tableaux pour les représentations des variables A, B, ...
        String[] items_repr =  new String[n_items];

        // initialization des tableaux mentionnés ci-haut
        int index = 0; 
        for(Item i : availableItems)
        {
            items_values[index] = i.getValue();
            items_weight[index] = i.getWeight();
            items_repr[index]   = i.getRepresentation();

            index++;
        }

        // drapeaux pour indiquer les items sélectionnés
        int[] flags = new int[n_items];
        // array de copie pour les flags afin d'initialiser le knapsack
        int[] flags_copy = new int[n_items];

        // réinitialiser le tableau
        flags = reset(flags);


        // proceder à l'analyse;
        // la section qui suit provient la référence github et a été légèrement modifié pour adaption
        // référence :  https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java
        System.out.printf(
            String.format("\n-> Flags set for items: %s \n", String.join("",items_repr))
        );
        while (!overflow) {
            //go through the array of permutations
            for (int i = 0; i < flags.length; i++){

                //if there is an item the thief is taking, add it's weight
                //to the weight the thief is considering taking
                if(flags[i]  == 1) {
                    currentWeight += items_weight[i];

                    //if that weight fits in the knapsack, add the
                    //values of the prices
                    if (currentWeight <= this.capacity) {
                        currentValue += items_values[i];
                        //find the most optimal value for the thief
                        if(currentValue > maxValue)
                            maxValue = currentValue;
                    }
                    //if the weight was too much for the knapsack's capacity, value is 0
                    else
                        currentValue = 0;
                }
            }

            //to print all of the combinations of the knapsack considered
            //the last one printed is the most optimal
            if(currentValue == maxValue)
            {
                System.out.println("the flags set are now:  " + toString(flags) + " Weight: " + currentWeight + " Value: " + currentValue + "\t");
                // copier le array de flags
                // la dernière collectée est la valeur optimale hors de la loop
                index = 0;
                for(int i : flags)
                {
                    flags_copy[index++] = flags[i];
                }
            }
                

            currentWeight = 0;
            currentValue = 0;

            // reinitialisation du tableau de flags afin de passer à la prochaine combinaison
            if (!overflow) {
                flags = bump(flags);
            }
        }

        // initialize the knapsack back
        Knapsack sac = new Knapsack(this.capacity);
        // ajouter les items dans le knapsack
        index = 0;
        for(int i : flags_copy)
        {
            // System.out.println(toString(flags_copy)); << DEBUG purpose
            if(flags_copy[index] == 1)
            {
                sac.addItem(availableItems.get(index));
            }
            index++;
        }

        // afficher l'informations sur le sac
        System.out.println(sac);

        // write solution inside the appropriate file
        this.writeSolutionInFile(file, sac);

        return maxValue;
    }

    /**
     * Écrire la solution final dans un fichier .sol
     * @param data
     */

    private void writeSolutionInFile(File file, Knapsack sac)
    {

        String filename = file.getName();

        // https://stackoverflow.com/questions/924394/how-to-get-the-filename-without-the-extension-in-java
        // add the new file extension .sol instead of .txt
        filename = filename.replaceFirst("[.][^.]+$", "");
        filename += ".sol";

        File solution = new File(filename);

        // Append a text at the end of the file
        try {
            
           
            // utiliser la classe Filewriter pour écrire le contenu
            FileWriter writer =  new FileWriter(solution.getAbsolutePath());

            // write the content ot the .sol file.
            writer.write(sac.getCurrentValue()+"\n");
            writer.write(sac.getStringRepr()+"\n");

            writer.close();

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }

        System.out.println("\n> La solution Force Brute (F) se trouve dans le fichier : " + solution.getName());

    }

    /**
     * Reference used for building this method: https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java
     * resets the permutation array to all 0's.
     * Pre-condition:  Array has a length > 0
     * Post condition:  The returned array is filled with integer "0" and
     * overflow is set to "false".
     * 
     * Added description : Cette méthode réinitialise le tableau à 0 le tableau passé en paramètre
     */
    public int[] reset(int[] x)
    {
        assert (x.length > 0);
        int i = 0;
        overflow = false;
        while (i < x.length)
        {
            x[i] = 0;
            i++;
        }
        return x;
    }

    /**
     * Reference used for building this method: https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java
     * returns a permutation of all possible combinations of "1"s and "0"s that an array of
     * size n can have
     *
     * Pre-condition:  Array contains only "0" and "1" and length > 0
     * Post condition:  The returned array is "bumpped" by 1 as a binary counter
     *                If the binary counter overflows, overflow is set to
     *               "true" otherwise overflow is set to "false"
     */
    public  int[] bump(int[] x)
    {
        assert (x.length > 0);
        assert (isBinary(x));
        int i = x.length - 1;
        overflow = true;
        while ((i >= 0) && (overflow))
        {
            if (x[i] == 1)
            {
                x[i] = 0;
            }
            else
            {
                x[i] = 1;
                overflow = false;
            }
            i--;
        }
        return x;
    }

    
    /**
     * 
     * @param x array of integers
     * @return a String representation of the array's content
     * 
     * takes the array of permutations and transforms it to a printable string
     * reference for this method : Reference used for building this method: https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java
     */
    public String toString(int[] x)
    {
        String result = " ";
        int i = 0;
        while (i < x.length)
        {
            result = result + x[i];
            i++;
        }
        return result;
    }


    /**
     * 
     * @param x array of integers
     * @return a String representation of the array's content
     * assert methods
     * confirms that the integers in the permutation array are just 0s and 1s
     * reference for this method : Reference used for building this method: https://github.com/mraediaz/Knapsack/blob/master/BruteForce.java
     * 
     * */
    public boolean isBinary(int[] x)
    {
        boolean result = true;
        int i = 0;
        while ((i <= x.length) && (result))
        {
            if ((x[i] != 0) && (x[i] != 1))
            {
                result = false;
            }
            i++;
        }
        return result;
    }
}
