# MoveInSync Assignment
## Smart Cab Allocation
My idea for the implementation was to use redis DBs, because they're very fast and the data that we would need isn't big enough to cause an issue.
This removes our need for a cache.
Another Idea that I had was using GeoHashing i.e mapping a piece of the grid on the planet to a number, and hence would make our lives easier trying to find
closest points. 
And to solve the task of ride alloting, taxis who are about to finish their jounrey to customers, I wanted to use the expire function in redis and set it to value close to the
ETA, after which it would be available for allotment.

Basically we would have 3 tables, one being trip_start, trip_end, driver_loc (apart from the TripRequest table which would only make sense if we handle our work asynchronously)

trip_start would talk about the starting locations of the trips.
trip_end would talk about the ending locations of the trips.
and driver_loc would tel us the location of the active drivers.

The implementation that I have submitted here unfortunately doesn't work due to some techincal difficulty :)
