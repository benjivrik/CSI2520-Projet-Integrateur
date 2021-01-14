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

// reference used for building this class : https://medium.com/@fabianterh/how-to-solve-the-knapsack-problem-with-dynamic-programming-eb88c706d3cf
public class DynamicProgramming 
{
    private Knapsack[][] KTable;

     /**
     * Cette méthode évalue la méthode en programmation dynamique pour le knapsack problem
     * @params : capacity - capacité du sac
     *           availableItems - Liste des items disponible à ajouter dans le sac
     *           file - which is the file in which the solution has to be written 
     * 
     * @return la valeur optimale des possibilité d'items à ajouter dans le sac
     */

    public int dynamicProgramming(int capacity,List<Item> availableItems, File file)
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

        


        return 0;
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

        System.out.println("\n> La solution en Programmation Dynamique (D) se trouve dans le fichier : " + solution.getName());

    }
    
}
