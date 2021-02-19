package main

import(
	"fmt"
	"time"
	"runtime"
	"log"
    "os"
	"bufio"
	"strconv"
	"strings"
)


// structure for the Item read from the inputs file
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

/* A brute force recursive implementation of 0-1 Knapsack problem 
modified from: https://www.geeksforgeeks.org/0-1-knapsack-problem-dp-10 */

func Max(x, y int) int {
    if x < y {
        return y
    }
    return x
}

// Returns the maximum value that 
// can be put in a knapsack of capacity W 
func KnapSack(W int, wt []int, val []int) int { 

	// Base Case 
	if (len(wt) == 0 || W == 0) {
		return 0 
	}
	last := len(wt)-1

	// If weight of the nth item is more 
	// than Knapsack capacity W, then 
	// this item cannot be included 
	// in the optimal solution 
	if wt[last] > W { 
		return KnapSack(W, wt[:last], val[:last])	 

	// Return the maximum of two cases: 
	// (1) nth item included 
	// (2) item not included 
	} else {
		return Max(val[last] + KnapSack(W - wt[last], wt[:last], val[:last]), KnapSack(W, wt[:last], val[:last]))
	}
} 

// verifie si une erreur s'est produite
// ref : https://gobyexample.com/reading-files
func check(e error) {
    if e != nil {
		fmt.Println()
        panic(e)
    }
}

// Driver code 
func main()  { 


	// Reading a file line by line in golang
	// ref : https://stackoverflow.com/questions/8757389/reading-a-file-line-by-line-in-go/16615559#16615559
    // ref : https://stackoverflow.com/questions/35080109/golang-how-to-read-input-filename-in-go
	// expect to read the filename from the command line
	if len(os.Args) < 2 {
		fmt.Println("Expecting two arguments (File to run and filename). You must provide the name of the filename via the command line. \n")
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
	// availableItems := []Item{}

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
		// fmt.Printf("\n>> %d", len(splitLine)) // << DEBUG Purpose

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

		// fmt.Println(scanner.Scan())
		n_items--
    }

	fmt.Println(len(os.Args))



	
    fmt.Println("Number of cores: ",runtime.NumCPU())
	
	// simple example
	W:= 7
	weights := []int{1,2,3,5}
	values := []int{1,6,10,15}
	
	start := time.Now();
	fmt.Println(KnapSack(W, weights , values)) 
	end := time.Now();
    fmt.Printf("Total runtime: %s\n", end.Sub(start))	


} 
