# TreeboTreasures

A Rewards Plugin

## Permisions
```
permissions:
  tbtreasures.addkey:
    default: op
  tbtreasures.removekey:
    default: op
  tbtreasures.configure:
    default: op
  tbtreasures.reward:
    default: op
  tbtreasures.relaod:
    default: op
  
```

## Commands
```
  ### General:
 commands:
  keyparty:
    description: gives random players a random key for X cycles on five minute intervals
    usage: <command> <donator name> <number of cycles>
    permission: tbtreasures.admin
  dailyreward:
    description: Opens the daily gui
    usage: <command>
  treasuresgui:
    description: Opens the treasures gui
    usage: <command>
  distributekeys:
    permission: tbtreasures.addkey
    description: Distributes keys to all players on a server.
    usage: <command> <keytype> <amount>
  addkey:
    permission: tbtreasures.addkey
    description: Allows player to grant reward keys to players
    usage: <command> <playername> <keytype> <amount>
  removekey:
    permission: tbtreasures.removekey
    description: Allows player to remove reward keys from players
    usage: <command> <playername> <keytype> <amount>
  configurerewards:
    permission: tbtreasures.configure
    description: Allows player to grant reward keys to players
    usage: <command> <keytype>
  showrewards:
    description: Allows player to grant reward keys to players
  issuereward:
    permission: tbtreasures.reward
    description: Issues player with reward of a specified key type. Note this will cost them a key.
    usage: <command> <playername> <keytype>
  ttreasurereload:
    permission: tbtreasures.reload
  ttreasuresave:
    permission: tbtreasures.reload

```
