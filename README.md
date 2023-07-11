# simple loot piles <br>
a simple loot pile mod for random loot <br>
has a editable config
## config example
```
{
  "0": [
    {
      "item": "minecraft:dirt",
      "chance": 100,
      "nbt": {},
      "amount": 10
    },
    {
      "item": "minecraft:cobblestone",
      "chance": 10,
      "nbt": {},
      "amount": 5
    }
  ],
  "1": [
    {
      "item": "minecraft:iron_ingot",
      "chance": 90,
      "nbt": {},
      "amount": 5
    }
  ],
  "2": [
    {
      "item": "minecraft:enchanted_golden_apple",
      "chance": 15,
      "nbt": {},
      "amount": 10
    }
  ]
}
```
and it supports items with nbt 
```
{
  "0": [
    {
      "item": "minecraft:dirt",
      "chance": 100.0,
      "nbt": {
        "Enchantments": [
          {
            "id": "blast_protection",
            "lvl": 2
          }
        ]
      },
      "amount": 10
    }
  ],
  "1": [],
  "2": []
}
```
