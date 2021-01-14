/**
 * 
 * Student : Benjamin Kataliko Viranga
 * Student ID : 8842942
 * CSI2520
 * Projet Intégrateur -  Partie Orientée-objet (Java)
 * 
 */

import java.util.List;

import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// reference used for building this class : https://medium.com/@fabianterh/how-to-solve-the-knapsack-problem-with-dynamic-programming-eb88c706d3cf
public class DynamicProgramming 
{
    // tableau des instances de Knapsack
    private Knapsack[][] KTable;

     /**
     * Cette méthode évalue la méthode en programmation dynamique pour le knapsack problem
     * @params : capacity - capacité du sac
     *           availableItems - Liste des items disponible à ajouter dans le sac
     *           file - which is the file in which the solution has to be written 
     * 
     * @return la Knaspack optimale
     */

    public Knapsack dynamicProgramming(int capacity,List<Item> availableItems, File file)
    {
        int n_row = availableItems.size()+1;
        int n_col = capacity + 1;
        this.KTable = new Knapsack[n_row][n_col];

        // initialiser les rangées et colonnes vides pour la capicité de sac = 0
        // avec KTable[i][j] with [i][j=0]
        for(int row = 0; row < n_row; row++)
        {
            KTable[row][0] = new Knapsack(0);
        }

        // initialiser la rangée avec des objets Knapsack 
        // ayant pour capacité les valeurs assignées aux colonnes mais n'ayant pas d'item à l'intérieur
        for(int col =0; col < n_col;  col++)
        {
            KTable[0][col] = new Knapsack(col);
        }

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
        
        // remplir la table KTable
        // cette section a été légèrement modifié pour adaptation et provient de
        // reference : https://medium.com/@fabianterh/how-to-solve-the-knapsack-problem-with-dynamic-programming-eb88c706d3cf
        for( int row = 1; row < n_row; row++ )
        {
            // columns (capacity)
            for(int cap = 1 ; cap < n_col; cap ++)
            {
                // la colonne et la ligne, KTable[n][0] et KTable[0][n], ont été initialisés précedemment
                int maxValWithoutCurr = KTable[row - 1][cap].getCurrentValue(); 
                // initialisation de la valeur maximale avec l'élément actuel à ajouter
                int maxValWithCurr = 0; 
                // initialisation de la capacité restante à 0 
                // cette valeur est changé dans le cas où un élement est ajouté dans le sac
                // et il y reste encore de l'espace
                int remainingCapacity = 0; 
                // obtenir la taille de l'élément actuel à ajouter 
                int weightOfCurr = items_weight[row-1]; 

                // si la capacité du sac à la colonne cap est supérieure au poids de l'élément à ajouter
                if (cap >= weightOfCurr) 
                { 
                    // la valeur maximale devrait commencer à partir de la valeur de l'élément à ajouter
                    maxValWithCurr = items_values[row- 1]; 
                    // initialiser la valeur de la capacité restante du sac
                    remainingCapacity = cap - weightOfCurr; 
                    // ajouter la valeur des items pour combler le vide 
                    // cette valeur correspond aux items ajouté dans le row-1 à la colonne remainingCapacity
                    maxValWithCurr += KTable[row - 1][remainingCapacity].getCurrentValue(); 
                }

                
                // initialiser le Knapsack
                int assignedValue = Math.max(maxValWithoutCurr, maxValWithCurr);

                // créer le Knapsack correspondant avec la capacité cap
                KTable[row][cap] = new Knapsack(cap);

                // si la valeur maximal n'a pas changé
                // càd l'élément à ajouter n'a pas été introduit dans le sac
                if(assignedValue == maxValWithoutCurr)
                {
                   
                    // ajouter les élements correspondent à la rangée précédente dans 
                    // le Knapsack actuel
                    for(Item i: KTable[row - 1][cap].getItems())
                    {
                        KTable[row][cap].addItem(i);

                    }
                }
                else
                {
                    // assignedValue == maxValWithCurr
                    assert(assignedValue == maxValWithCurr);

                    // ajouter les items pour maximiser la capacité du sac

                    // ajouter les éléments pour combler la capacité restante si remainingCapacity != 0
                    // si remainingCapacity = 0 tous les éléments Knapsack at KTable[n][0] et KTable[0][n] n'ont pas d'items
                    for(Item i: KTable[row - 1][remainingCapacity].getItems())
                    {
                        KTable[row][cap].addItem(i);
                    }
                    // l'item disponible à l'index row-1 est ajouté dans le sac (row,cap)
                    // System.out.println(availableItems.get(row-1)); << DEBUG Purpose
                    KTable[row][cap].addItem(availableItems.get(row-1));
                    
                }

            }
        }


        // obtenir le sac de la solution optimale
        Knapsack sac = KTable[n_items][capacity];
        // le dernier Knapsack est le Knapsack à valeur optimale
        System.out.println(sac);

        // write solution inside the appropriate file
        this.writeSolutionInFile(file, sac);
  
        // retourner le sac correspondant au scenario optimal
        return KTable[n_items][capacity];
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

        System.out.println("\n> La solution en Programmation Dynamique (D) se trouve dans le fichier : " + solution.getName());

    }
    
}
