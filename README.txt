# Pac-Man

To run the game, run the file src/Driver.java.
Press backslash ('\') to gain a heart.

# Responsibilities

## Christopher

- Ghost pathing (two strategies: shortest path to player, shortest path to an arbitrary square)
- Ghost special behaviours
- Movable class
- Maze graphics generation
- Main menu, tutorial, credits, settings screen
- Music and sound effects

## Jonathan

- Player
- Ghost respawn
- Score+timer
- Leaderboard
- Ghost graphics
- Pacman graphics
- Pick ups

# Functionalities missing

- 3 pickups are missing (fire trail, frost trail, missile)
- Ghost cannot do “path to three squares in front of player” strategy

# Added functionalities

- Leaderboard
    - Can sort by name/score/player
    - Can search and then sort the subset

# Known Bugs/Errors

- Occasionally doesn’t allow button inputs, though this bug has not occurred in a while (we might have fixed it or just got lucky)
- Some sound effects get cut off (e.g. when you eat two ghosts in quick succession)




This is for reference.

# maze.txt specification

- First two numbers are rows and columns
- . represents coin on floor
- \* represents big coin on floor
- a represents accessible but blank area (not coin)
- B represents inaccessible area but not wall
- 1 represents single-line wall
- 2 represents double-line wall
- 3 represents special wall
- 4 represents sharp corner (used around ghost spawning area)
- 5 represents the exit to the ghost respawn area.
- L represents the tile to the left of the ghost respawn exit
- R represents the tile to the right of the ghost respawn exit

Rules:
- All dimensions of blocks of 1's must be at least 2
- The border of the maze will not be displayed
- Special wall (3's) must be surrounded by a 3 2 1 as shown below, up to rotation and reflection:

| 2 | **3** | 3 |
| --- | --- | --- |
| - | 1 | - |

- Sharp corners (4's) must be surrounded by double-line walls (2's) on two sides (not in a straight line)