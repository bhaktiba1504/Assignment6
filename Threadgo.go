package main

import (
	"fmt"
	"log"
	"sync"
	"time"
)

var (
	numWorkers = 4
	taskQueue  = make(chan string, 10)
	results    []string
	resLock    sync.Mutex
	wg         sync.WaitGroup
)

func main() {
	// Add tasks
	for i := 1; i <= 10; i++ {
		taskQueue <- fmt.Sprintf("Ride Request #%d", i)
	}
	close(taskQueue) // Important: close the queue so workers can exit

	// Start workers
	for i := 1; i <= numWorkers; i++ {
		wg.Add(1)
		go worker(i)
	}

	wg.Wait()

	// Print results
	fmt.Println("===========All ride requests processed:================")
	for _, r := range results {
		fmt.Println(r)
	}
}

func worker(id int) {
	defer wg.Done()
	log.Printf("Worker %d started.\n", id)
	for task := range taskQueue {
		time.Sleep(500 * time.Millisecond) // simulate delay

		result := fmt.Sprintf("Worker %d processed: %s", id, task)
		resLock.Lock()
		results = append(results, result)
		resLock.Unlock()
	}
	log.Printf("Worker %d completed.\n", id)
}
