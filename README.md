# MoveInSync Assignment
## Smart Cab Allocation
My idea for the implementation was to use redis DBs, because they're very fast and the data that we would need isn't big enough to cause an issue.
This removes our need for a cache.
Another Idea that I had was using GeoHashing i.e mapping a piece of the grid on the planet to a number, and hence would make our lives easier trying to find
closest points. 
And to solve the task of ride alloting, taxis who are about to finish their jounrey to customers, I wanted to use the expire function in redis and set it to value close to the
ETA, after which it would be available for allotment.

## L0 Diagram
<img width="674" alt="Screen Shot 2023-12-07 at 3 53 49 PM" src="https://github.com/yeetholmes619/MoveInSync/assets/79030279/055f03c9-a8b7-4cf7-baf2-79248ba09682">

## L1 Diagram
<img width="947" alt="Screen Shot 2023-12-07 at 3 53 43 PM" src="https://github.com/yeetholmes619/MoveInSync/assets/79030279/ce131151-da1f-478f-90cf-4db69c66d9f6">


## Class Diagram

<img width="652" alt="Screen Shot 2023-12-07 at 7 23 47 PM" src="https://github.com/yeetholmes619/MoveInSync/assets/79030279/0255faf4-515b-4adf-91e8-560404ab7d96">


The implementation that I have submitted here unfortunately doesn't work due to some technical difficulty :)
