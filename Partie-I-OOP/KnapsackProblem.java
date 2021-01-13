/**
 * 
 * Student : Benjamin Kataliko Viranga
 * Student ID : 8842942
 * CSI2520
 * Projet Intégrateur -  Partie Orientée-objet (Java)
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class KnapsackProblem
{

    
    public static void main(String[] args)
    {
    
       // Attendant d'avoir deux arguments : nom de fichier et le mode à utiliser
       if(args.length == 2)
       {

            // Utilisez un objet File afin de representer le fichier à ouvrir
            File file = new File(args[0]);
            // Force Brute (F) ou programmation dynamique (D)
            String mode = args[1];

            Scanner sc;
            try {

                sc = new Scanner(file);

                System.out.printf(
                    String.format("\n----------- Lecture du fichier %s ------------\n", file.getName())
                );
                // lire le contenu du fichier
                while (sc.hasNextLine()) 
                    System.out.println(sc.nextLine());

                System.out.println("\nMode entré : " + mode);

                System.out.printf(
                    String.format("\n----------- Extraction des données du fichier %s ------------\n", file.getName())
                );

                sc.close();

            
                sc = new Scanner(file);
                // capacité du sac
                int n_items = 0;

                // la première ligne est le nombre d'items
                if(sc.hasNextLine()) 
                {
                    String line = sc.nextLine();
                    // separer les items à l'espace
                    String[] values = line.split(" ");

                    n_items = Integer.parseInt(values[0]);

                    System.out.println("n items : " + n_items);
                }

                // liste des items

                // TO ADD

                // compter n_items à partir de la suivante
                while(n_items-- > 0)
                {

                    if(sc.hasNextLine())
                    {
                        String line = sc.nextLine();
                        // separer les items à l'espace 
                        String[] values = line.split("\\s+");
   
                        // attendant 3 données par ligne en ce qui concerne les items
                        if(values.length == 3)
                        {
                            Item i = new Item(
                                values[0],  // repr - string representation ofthe Item
                                Integer.parseInt(values[1]), // value of the item
                                Integer.parseInt(values[2])  // weight of the item
                            );

                            System.out.println(i); // < DEBUG purpose
                        }
                    }
                       
                }

                // la dernière ligne est la capacité du sac
                int capacity = 0;
                
                if(sc.hasNextLine())
                {
                    String line = sc.nextLine();
                    // separer les items à l'espace 
                    String[] values = line.split("\\s+");

                    capacity = Integer.parseInt(values[0]);

                    System.out.println("Bag capacity: " + capacity);
                }
                else
                {
                    System.out.println("> PROBLÈME - La dernière ligne pour la capacité du sac n'existe pas."+
                                        "\nVeuillez vérifier le contenu votre fichier: " + file.getName());
                    System.exit(0);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
       }
       else
       {
           System.out.println(
               "\n> Le programme s'attend à deux arguments : Le nom du fichier et le mode à utiliser. <\n"
            );
       }
    }

}