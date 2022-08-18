# ChestProtect
Minecraft Plugin - Prevent greedy hands from entering your chests!\
Different to other Plugins, this one focus on only provide the necessary features instead of flooding with useless stuff that slow down the Server.

## Features
- Protect your chests
  - Protect against other players
  - Protect against Environment (TNT attacks, Hopper, Minecarts)
- Add trusted players to your chests
- Enable inside chest view for players with no permission to access
  - Player can see the content but cannot access it
- Double-Chest support
  - Double-Chests works like single chests

## How To
Simply place down a chest, and it will be marked at yours. Only you can access the settings to it.\
Note - Trusted Players have no access to the chest settings!\
**Sneak-Right-Click** -> Information about the chest (global)\
**Sneak-Left-Click** -> Enter the settings

**Default Settings** -> No protection, inside chest view, you as owner

---
Chest-Data is stored inside a json file located at the plugins config folder (``chests.json``)
```json
[
    {
        "location": {
            "x": 0,
            "y": 0,
            "z": 0,
            "world": "world"
        },
        "protected": false,
        "visual": true,
        "owner": "uuid",
        "trusted": []
    }
]
```
