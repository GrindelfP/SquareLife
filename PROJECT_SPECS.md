## Important questions
A) How the information about e. should be transferred between detector and simulator.God? 

B) How the God should implement the received information?

C) How to make e. placing on the board relative to their positioning from the photo?

D*) How to recognize the photo's information?

### A)

### B)
1. CREATE data container "Mailman" for holding information about entities and their positions.
This task is partially done by now, because the Entity class already holds all needed information.
2. REMAKE GOD's functionality, so it receives the "Mailman" with list of entities. As far as the Entity
type already contains the information of its kind and coordinates, Mailman consists of the list of the Entity
and the size of the board.


### C)
1. DEFINE how entities are placed on the board in current version.
2. DEFINE is it needed to implement new functions for placing e. on board, or it is enough to modify existing function.

### D)
1. Add footer to gif, it can contain info about the population state and evolution cycle.
2. 

### E)
1. Split visual and logical parts of BOARD class.
