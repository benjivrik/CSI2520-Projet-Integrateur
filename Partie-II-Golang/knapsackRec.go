package main

import "fmt"
import "time"
import "runtime"


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

// Driver code 
func main()  { 

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
