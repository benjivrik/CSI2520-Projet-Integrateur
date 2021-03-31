/**
 * 
 * Student : Benjamin Kataliko Viranga
 * Student ID : 8842942
 * CSI2520
 * Projet Intégrateur -  Partie Concurrente (Go)
 * 
 */


package main

import(
	"fmt"
	"time"
	//"runtime"
	"log"
    "os"
	"bufio"
	"strconv"
	"strings"
	"path/filepath"
)


// structure for the Item read from the input file
type Item struct{
	repr  string
	value int
	weight int
}

// function for creating an Item
func NewItem(repr string, value, weight int)(*Item){

	i := new(Item)
	i.repr = repr
	i.value = value
	i.weight = weight

	return i
}

// string representation of the structure Item
func (i *Item) ToString() (string){
	s := fmt.Sprintf("\nItem : %s\n", i.repr)
	s += fmt.Sprintf("> Value: %d\n", i.value)
	s += fmt.Sprintf("> Weight : %d\n", i.weight)
	return s
}

// custom function for removing empty string from a slice
// @ params slice
// @ return a new slice without the empty string
func filter(slice []string) ([]string){
	newSlice := []string{}
	for _, value := range slice {
		if value != "" {
			newSlice = append(newSlice, value)
		}
	}
	return newSlice
}

// custom function for finding the corresponding item selected
// find the item by its value and its weight
// find an item in the slice and return its representation
func getItemRepresentation(availableItems []Item, value,weight int) (string){
	repr := ""
	for _, item :=  range availableItems {
		if item.weight ==  weight && item.value == value {
			repr = item.repr
		}
	}

	return repr
}

// custom function for finding the minimum value in the array of duration
func findMinimumDuration(durations []time.Duration) (int,time.Duration){
	// assume len(durations) > 0
	min_index := 0
	min_duration := durations[min_index].Microseconds()

	for index, duration  := range durations{
		if duration.Microseconds() <= min_duration {
			min_index = index
			min_duration = duration.Microseconds()
		}
	}	

	return min_index, durations[min_index]
}

/* A brute force recursive implementation of 0-1 Knapsack problem 
modified from: https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10 */

func Max(x, y int) int {
    if x < y {
        return y
    }
    return x
}

// Returns the maximum value  and the corresponding items that 
// can be put in a knapsack of capacity W 
// initial recursive function 
/**
* 
* @params
* W - weight of the Knapsack
* Wt - a slice of the weights associated to the items to be added
* val - a slice of the values associated to the items to be added
* availableItems - slice of items collected from the input file
* included_characters - sequence of the string included in the funcion
*
**/
func KnapSackRec(W int, wt []int, val []int, included_characters string, availableItems []Item) (int,string) { 

	// Base Case 
	if (len(wt) == 0 || W == 0) {
		return 0, ""
	}
	last := len(wt)-1


	// If weight of the nth item is more 
	// than Knapsack capacity W, then 
	// this item cannot be included 
	// in the optimal solution 
	if wt[last] > W { 
		return KnapSackRec(W, wt[:last], val[:last], included_characters, availableItems)	 

	// Return the maximum of two cases: 
	// (1) nth item included 
	// (2) item not included 
	} else {

		next_value_included, str := KnapSackRec(W - wt[last], wt[:last], val[:last], included_characters, availableItems)
		next_value_not_included, n_str := KnapSackRec(W, wt[:last], val[:last], included_characters, availableItems)

		nth_included := val[last] + next_value_included
		nth_not_included := next_value_not_included

		max :=  Max(nth_included,nth_not_included)

		if max == nth_included {
			// find the item corresponding to the 
			included_characters += getItemRepresentation(availableItems, val[last], wt[last]) + " " + str
			// add the character found to the result
            
		}else{
			included_characters += n_str + " "
		}

		return max, included_characters
	}
} 



// Implemented KnapSack function using concurrent programming
/**
* 
* @params
* W - weight of the Knapsack
* Wt - a slice of the weights associated to the items to be added
* val - a slice of the values associated to the items to be added
* result - an integer channel for storing the result returned by the goroutine for the optimal sequence
* result_characters - an integer channel for storing the selected characters for the optimal sequence
* availableItems - slice of items collected from the input file
* 
* @return none
*
* This function creates 1 or 2 new go routines for each goroutine until the number of items in the array
* is less than 1
* (depending on the condition inside if..else describing the weight of the item to be added )
* La valeur du seuil pour resoudre en utilisant les fils concurrents
*
**/
func KnapSackOptimal(W int, wt []int, val []int, 
	                    result chan int, result_characters chan string, availableItems []Item, 
						seuil int, routine_increment *int)  {

	//fmt.Println("Number of active go routines : ",runtime.NumGoroutine())

	*routine_increment++ // increment the number of goroutine created

    if (len(wt) == 0 || W==0) {

		result <- 0 
		result_characters <- ""
		return
	}

	// seuil
	// if (len(wt) <= 1){
	// 	last := len(wt)-1
	// 	if (wt[last] > W){
	// 		// do not include the item
	// 		result <- 0
	// 		result_characters <- ""
	// 		return
	// 	}else{
	// 		// the item is included
	// 		result <- val[last]
	// 		result_characters <- getItemRepresentation(availableItems, val[last], wt[last])
	// 		return
	// 	}
	// }

	if (len(wt) <= seuil){
		// last := len(wt)-1

		included := ""
		optimal_val_rec, repr_rec := KnapSackRec(W, wt , val, included, availableItems)

		result <- optimal_val_rec
		result_characters <- repr_rec

		return // terminate the go routine
	}

	last := len(wt)-1

	if (wt[last] > W){ // the item can not be included in the Knapsack
		// initialize a new goroutine without that item but the keep the initial channels passed as parameters
		go KnapSackOptimal(W, wt[:last], val[:last], result, result_characters, availableItems,seuil, routine_increment)
		return // terminate the goroutine

	}else{ // the item could  be included in the Knapsack
			
		included := make(chan int)      // collect the next value after the nth item is included
		not_included := make(chan int)  // collect the next value after the nth item is not included

		// channels for collecting the included characters
		character_included := make(chan string)       // a channel for when the nth item is included
		character_not_included := make(chan string)	  // a channel for when the nth item is not included

		// initialize the parallel sequences
		go KnapSackOptimal(W - wt[last], wt[:last], val[:last], included, character_included, availableItems,seuil, routine_increment)
		go KnapSackOptimal(W, wt[:last], val[:last],not_included, character_not_included, availableItems,seuil, routine_increment)

		// get the corresponding item values
		nth_included := val[last] + (<-included) // if the nth is included
		nth_not_included := (<-not_included)	 // if the nth item is not included

		// get the maximum value between the two possibilities
		max := Max(nth_included, nth_not_included)

		// write the value to the channel 
		result <- max

		// get the corresponding items representation
		if max == nth_included {

			// find the item corresponding to the 
			repr := getItemRepresentation(availableItems, val[last], wt[last])
			// add the character found to the result
			result_characters <- repr + " " + <-character_included
		}else{
			result_characters <- (<-character_not_included) + " "
		}

		return // terminate the routine
	}

}



// verifie si une erreur s'est produite
// ref : https://gobyexample.com/reading-files
func check(e error){
    if e != nil {
		fmt.Println()
        panic(e)
    }
}

// get filename without extension
// ref : https://freshman.tech/snippets/go/filename-no-extension/
func fileNameWithoutExtTrimSuffix(fileName string) string {
	return strings.TrimSuffix(fileName, filepath.Ext(fileName))
}

// Driver code 
func main()  { 


	// Reading a file line by line in golang
	// ref : https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go/16615559#16615559
    // ref : https://stackoverflow.com/questions/35080109/golang-how-to-read-input-filename-in-go
	// expect to read the filename from the command line
	if len(os.Args) < 2 {
		fmt.Println("\n> Expecting two arguments (File to run and filename). You must provide the name of the filename via the command line. \n")
		return
	}

	file, err := os.Open(os.Args[1])

	fmt.Printf("\n> Reading the file: %s\n", file.Name())
	fmt.Println()

    if err != nil {
        log.Fatal(err)
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
	// initialize a slice of item in which the availables items will be stored
	availableItems := []Item{}

	// number of items supposed to be added in knapsack
	// data initialized after reading the file
	n_items := 0

	// get the number of element inside to add in the Knapsack
	if(scanner.Scan()){

		line :=  scanner.Text()

		splitLine := strings.Split(line," ")
		// fmt.Println(">>", len(splitLine)) // << DEBUG PURPOSE
		// make sure there is not emply strings left
		splitLine = filter(splitLine)
		// get the value
		n_items,err = strconv.Atoi(splitLine[0])
		
		// make sure there is no error
		check(err)
	}

	// fmt.Println(">>>",n_items) // Debug Purpose
	fmt.Printf("\n> %d expected items to be found in the file '%s'.\n",n_items, file.Name())

    // get the items from the file
    for scanner.Scan() && n_items > 0 {
        line := scanner.Text()
		splitLine := strings.Split(line," ")

		//fmt.Printf("\n>> %q", splitLine) // << DEBUG Purpose
		//fmt.Printf("\n>> %d", len(splitLine)) // << DEBUG Purpose

		// remove empty string
		splitLine = filter(splitLine)

		// fmt.Printf("\n>> %q", splitLine) // << DEBUG Purpose

		// representation
		repr := splitLine[0]
		// value
		value,err1 := strconv.Atoi(splitLine[1])
		check(err1)
		// weight
		weight,err2 := strconv.Atoi(splitLine[2])
		check(err2)

		// initialize the new item
		item := NewItem(repr, value,weight)
		// display item information
		fmt.Println(item.ToString())

		// add the item in the array of the available items 
		availableItems = append(availableItems, *item)
		
		// fmt.Println(scanner.Scan())
		n_items--
    }

	// fmt.Printf(">> length of the available items : %d. | Items: %v\n", len(availableItems), availableItems) // << DEBUG PURPOSE

    // get the capacity of the knapsack from the file
	// get the number of element inside to add in the Knapsack
	// no need for scanner.Scan() since it is one of the condition for breaking the loop
	line :=  scanner.Text()

	maxWeight := 0

	if line != "" {   // make sure the line is not empty
		splitLine := strings.Split(line," ")
		// fmt.Println(">>", len(splitLine)) // << DEBUG PURPOSE
		// make sure there is not emply strings left
		splitLine = filter(splitLine)
		// get the value
		maxWeight,err = strconv.Atoi(splitLine[0])
		
		// make sure there is no error
		check(err)
	}else{
		fmt.Println("Could not get the value for the maximum capacity of the Knapsack.")
		fmt.Println("Please check the content of your file : ", file.Name())
		return
	}

    
	// initialize the parameters required for the defined function KnapSack
	// maximum Knapsack capacity
	W :=  maxWeight

	// initialize empty slice for the weights and the values
	weights := []int{}
	values := []int{}

	// initialize the above slices
	for _,item := range availableItems{
		weights = append(weights,item.weight)
		values  = append(values,item.value)
	}

	fmt.Println("\n>>>> Collected data <<<<< \n")
	fmt.Printf("Knapsack capacity : %d\n", maxWeight)
	fmt.Printf("Weights: %v\nValues: %v\n", weights, values)
	
	//--------------------------------------------------//
	// (Un)comment this block for comparing the values  //
	// obtained by recursion with the one obtained with //
	// the concurrent programming                       // 
	//--------------------------------------------------//

	fmt.Println("\n>>>> Recursive Solution <<<<< \n")
	// fmt.Println("Number of cores: ",runtime.NumCPU())
	included := ""
	new_start := time.Now()

	optimal_val_rec, repr_rec := KnapSackRec(W, weights , values, included, availableItems)

	fmt.Println(repr_rec) 
	fmt.Println(optimal_val_rec) 

	new_end := time.Now()
    fmt.Printf("Total runtime: %d us\n", new_end.Sub(new_start).Microseconds())	

	fmt.Println("\n>>>> Concurrent Solution <<<<< \n")

	// initialize the channels for storing the required results from the goroutines
	result := make(chan int, 1)                 // un channel pour collecter la valeur renvoyee par la goroutine
	result_characters := make(chan string, 1)   // un channel pour collecter les caracteres renvoyee par la goroutine

	defer close(result)
	defer close(result_characters)

	// valeur du seuil pour l'execution en mode concurrent 
	seuils := []int{len(weights),len(weights)/2, len(weights)/3} 

	// un slice pour collecter le temps d'execution
	collected_duration := [] time.Duration{}

	// array for collecting the number of goroutine created corresponding to the respective duration
	created_goroutine := []int{}
	// counter of the go routine created
	// runtime.NumGoroutine() gives a global number of the goroutine created
	// https://stackoverflow.com/questions/37522286/golang-calculate-how-many-goroutines-are-started-by-worker-itself
	// this counter could do the trick for knowing how many goroutines are used for the 
	// corresponding 'seuil' number
	number_of_goroutine := 0 // gets incremented when the Knapsack function is called 

	included_characters := ""  // list of the characters included in the Knapsack
	optimal_result := 0		   // optimal result obtained for the Knapsack


	for index , seuil := range seuils {

		fmt.Println("\nValeur du seuil : ",seuil)

		number_of_goroutine = 0

		start := time.Now()

		KnapSackOptimal(W, weights , values, result, result_characters, availableItems,seuil, &number_of_goroutine)
		// fmt.Println("Number of cores: ",runtime.NumCPU())

		included_characters = <- result_characters  // get the list of items inside the Knapsack
		optimal_result = <- result					// get the optimal result

		fmt.Println(included_characters)
		fmt.Println(optimal_result) 

		end := time.Now()

		// store the calculated duration
		collected_duration = append(collected_duration, end.Sub(start))

		// get the number of go routine created
		created_goroutine = append(created_goroutine, number_of_goroutine)

		fmt.Printf("Total runtime: %d us\n", collected_duration[index].Microseconds())
		fmt.Printf("Total number of created goroutines: %d \n", number_of_goroutine)
		

		included_characters = ""
	    optimal_result = 0

		fmt.Println("---")

	}
	
    fmt.Printf("\n> Valeurs de seuils initialisées : %v", seuils)
	fmt.Printf("\n> Durées d'exécution des routines collectées : %v", collected_duration)
	fmt.Printf("\n> Tableau des valeurs des goroutines créées pour chaque seuil: %v\n", created_goroutine)

	// get the optimal duration
	optimal_duration_index, optimal_duration := findMinimumDuration(collected_duration)

	fmt.Printf("\n\n> Le temps optimal correspond au seuil %d avec une duree de %v et %d goroutines créé(es). \n", 
				seuils[optimal_duration_index], optimal_duration, created_goroutine[optimal_duration_index])

	// fmt.Println("Number of active go routines : ",runtime.NumGoroutine())

	fmt.Printf("\n\n> Initialisation de la solution optimal dans le fichier .sol correspondant <\n")


	// get the Optimal solution and store it inside the .sol
	included_characters = ""
	optimal_result = 0
	number_of_goroutine = 0

	// initialize the channels
	result = make(chan int,1)                 
	result_characters = make(chan string,1) 

	KnapSackOptimal(W, weights , values, result, result_characters, availableItems, seuils[optimal_duration_index], &number_of_goroutine)

	// get the optimal values
	
	included_characters = <- result_characters
	optimal_result = <- result

	// ref : https://www.golangprograms.com/golang-read-write-create-and-delete-text-file.html
	// write solution in the .sol file
	file_without_extension :=  fileNameWithoutExtTrimSuffix(file.Name())

	// add the .sol extension
	file_with_extension := file_without_extension + ".sol"

	// create the file

	// check if the file exists
	var _, err4 = os.Stat(file_with_extension)

    // if the file does not already exist, create it
    if os.IsNotExist(err4) {
        var file, err5 = os.Create(file_with_extension)
		// make sure there is no error
        check(err5) // if yes panic

        defer file.Close()

		fmt.Printf("\n> File '%s' created cuccessfully\n", file_with_extension )
    } else {
		// remove the file and rewrite inside
		e := os.Remove(file_with_extension) 
		// make sure there is no error
		check(e)

		// 
		fmt.Printf("\n> Old file '%s' deleted cuccessfully\n", file_with_extension )

		var file, err5 = os.Create(file_with_extension)
		// make sure there is no error
        check(err5) // if yes panic

        defer file.Close()

		fmt.Printf("\n> New File '%s' created cuccessfully\n", file_with_extension )


	}

	
	// Open file using READ & WRITE permission.
    var file_to_open, err6 = os.OpenFile(file_with_extension, os.O_RDWR, 0644)
    
	check(err6)

    defer file_to_open.Close()

    // Write some text line-by-line to file.
    _, err6 = file_to_open.WriteString(strconv.Itoa(optimal_result)+"\n")
    
	check(err6)

    _, err6 = file_to_open.WriteString(included_characters)
   
	check(err6)

    // Save file changes.
    err6 = file_to_open.Sync()

	check(err6)

    fmt.Printf("\n>> File '%s' Updated Successfully.\n", file_with_extension)

} 
