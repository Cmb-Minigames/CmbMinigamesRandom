![CmbMinigamesBanner.png](https://github.com/Cmb-Minigames/CmbMinigamesRandom/blob/master/docs/images/CmbMinigamesBanner.png?raw=true)
# Cmb Minigames Random Server
The server code for the random minigames server. A server where every time a minigame ends, another random one will start

## Minigames
There are currently a small group of added minigames, with more to come in the future. If it is added, it will be checked below.
- [x] Capture the Flag
  - Steal the flag from the opposing team and return it to your base!
- [x] Kaboomers
  - Claim area around the map as your own by shooting a fireball at it, whichever team has the most claimed by the end wins
- [x] Sniffer Caretaker
  - Keep your team’s sniffer alive by giving it food, dirt, and other resources found around the map. You win if the other team’s sniffer is not taken care of enough and dies.
- [x] Cooking Chaos
  - Race for resources to cook for your animal patreons where pvp is enabled, you can sabotage the other team or play it safe and get your own resources. The team with the most customers fed in 10 minutes wins. 
- [x] Teleporters
  - A minigame where you are given a stack of pearls and have to stay on the platform. At the start of the game, the amount of lives will be selected, which can either be 1, 5, 10, 15, or 20. Over time, items used to push other players off will be given through events that happen every 60 seconds. Last person standing wins!
> [!WARNING]\
> This is a large-scale project maintained by a very small pool of people, so do not expect updates too frequently.

## How to contribute
To modify the plugin, you must follow some of the hardcoding rules
- There must be a world called 'pregame' where the players will be teleported to when they spawn in
- The spawn-point of said world should be at -26.5, -43.5, -18
- There must be at least 1 map in the `config.yml` following the provided format

To compile the plugin, open it up in your choice of IDE, if it has gradle tasks embedded, run the `build` task, if not, run `gradlew build` in your terminal.

In order to contribute to any of the Cmb Minigames projects, you must follow the following contributing guidelines
- Pull requests made to the repositories must be relevant to the project
- All contributions should explain exactly what was added or changed

If all of these guidelines are followed, your pull request will be accepted and merged into the main branch.