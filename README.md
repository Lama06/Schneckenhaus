# Schneckenhaus Plugin
Ever wanted to know how a shulker box looks from the inside? 
Using this Minecraft plugin, you can enter shulker boxes, chests and more!
Inside you can, for example, build your base.
After leaving the shulker box again, you can break it and take it (and the base it contains) with you in your inventory.

## Documentation

Table of contents:
1. [Commands](#command-system)
2. [Custom Shells](#custom-shells)
3. [Configuration](#configuration)
4. [Permissions](#permissions)

## Command System

### Shell Selectors

When a command requires you to specify one or multiple shells like `/sh tp shell <shell>` or `/sh info <shell>`,
you have multiple ways to do so:

- `here` resolves to the shell at your location
- `selection` resolves to all shells you selected, see [Selections](#selections)
- `<id>` resolves to the shell with the specified id

### Commands

- `sh`
  - `tp`
    - `shell <shell>` teleport to a shell
    - `world <world>` teleport to a world that contains snail shells
  - `info <shell>` displays a lot of information about a certain shell in the chat
  - `menu <shell>` opens a snail shell's menu
  - `item <shell>` gives you an item associated with the given shell
  - `count`
    - `total` displays the total number of snail shells on this server
    - `world <world>` displays the number of snail shells which are stored inside a world
  - `delete <shell>` deletes a shell
  - `create <type>` type can be `shulker`, `chest`, `head` or `custom`; depending on the type, you specify one or more of these options:
    - `owner <owner>`
    - `world <world>` world in which the snail shell will be stored
    - `permission`
      - `enter <enterPermissionMode>`
      - `build <buildPermissionMode>`
    - `size <size>` only for shulker and chest shells, will be random in unspecified
    - `color <color>` only for shulker shells
    - `rainbow <rainbow>` only for shulker shells
    - `head-owner <headOwner>` only for head shells
    - `template <template>` only for custom shells
  - `home`
    - `tp` teleports to your own home shell
    - `tp <player>` teleport to someone else's home shell
    - `set <player> <home>`
    - `unset <player>`
  - `language <language>` changes the plugin's language
  - `discord` shows the link to join our discord server
  - `tag <shell>`
    - `list`
    - `add <tag>`
    - `remove <tag>`
    - `clear`

### Selections

Selections is a powerful feature which enabled you to search for snail shells using a variety of criteria. Besides,
selections make it possible to run other commands on all shells that are selected.
Every player and the server console has a current selection which is empty be default.

- `sh`
  - `selection`
    - `clear` deletes your current selection
    - `list` lists the ids of currently selected snail shells
  - `select` this command takes one or more criteria that must all be true:
    - `id <id>` you also specify a range like `7..` or `7..42` 
    - `world <world>` the world where the shell is stored
    - `position <position>` the id of the shell's position, can also be a range
    - `type <type>` the type of the shell, like `shulker`, `chest`, `head` or `custom`
    - `creation`
      - `reason <creationType>` can be `crafting`, `command` or `home`
      - `time` timestamps must be specified like this: '2025-08-21 21:01:42'
        - `after <creationTimeMin>`
        - `before <creationTimeMax>`
        - `between <creationTimeMin> and <creationTimeMax>`
    - `creator <creator>` can be an uuid, name or player selector
    - `name <name>` name of the shell
    - `permission` permission modes can be `whitelist`, `blacklist`, `everybody`, `nobody`
      - `enter <enterPermissionMode>`
      - `build <buildPermissionMode>`
    - `size <size>` size of chest and shulker shells, can also be a range like `4..8`
    - `color <color>` color of shulker shells
    - `rainbow <rainbow>` a boolean that indicates whether rainbow mode is enabled for a shulker shell
    - `wood <wood>` the wood type of a chest shell like `oak`
    - `template <template>` the template name of a custom shell
    - `head <head>` the head owner of a head shell
    - `tag <tag>` selects shells that have a certain tag
    - `owner <owner>`
    - `sort by <sortCriterion> <sortOrder>`
      - the sort criterion can be `id`, `position`, `size` or `creation_date`
      - the sort order can be `ascending` or `descending`
    - `limit <limit>` only includes the first `<limit>` results in the selection
    - `offset <offset>`
    - `combine <combine>` specifies how the old and new selection should be merged
      - `replace` (default) means to replace the old selection with the new one
      - `append` means to append the new to the old (excluding duplicates)
      - `remove` keep the old selection except for the shells that are in the new selection
      - `intersect` keep only shells in both the new and old selection

## Custom Shells

### Creating Custom Shells

You first have to build a template for your custom shell type. 
When the custom shell type is instantiated, e.g. through crafting, a copy of the template is created in the snails shell world.

The template can be built in any world, but it is advisable to build it in the snail shell world.
To do that, execute `/sh tp world schneckenhaus` to be teleported to the default snail shell world.
You must build your template at a position where every block of the template has either a negative x or z coordinate.
Otherwise, there could be conflicts between shells created by the plugin and your template.

When you are finished building, run `/sh custom add <name> <world> <position1> <position2> <item> <ingredient>`.
`name` can be freely chosen by you and is used to identify your custom shell type.
Renaming your custom shell type afterward is not possible.
`world` is the world where you built the template, for example `schneckenhaus`.
`position1` and `position2` are the corner positions of your template.
Look at these corner blocks and use tab completion to make your life easier.
`item` is the item/block that will represent an instance of your custom shell.
`ingredient` is an ingredient used to craft your custom shell type.
You can later edit and add ingredients in the config file.

Using the command `/sh custom edit <name> ...` you can set some other properties of your custom shell type.
You could also change this is the config file.

#### Spawn Point

`/sh custom edit <name> set-spawn`

Configure where the player spawns in the snail shell and in which direction he will look.

#### Initial Blocks

`/sh custom edit <name> add-initial-block <block>`

`/sh custom edit <name> add-initial-block-area <position1> <position2>`

If you want a block to be copied, but you want the player to be able to break it and replace it afterward, configure the block as an initial one.

#### Menu Block

`/sh custom edit <name> set-menu-block <block>`

Clicking this block will open the snail shell menu. 

#### Exit Blocks

`/sh custom edit <name> add-exit-block <block>`

Clicking one of these blocks will make the player leave the shell.

#### Alternative Blocks

`/sh custom edit <name> add-alternative-block <block> <alternative>`

For example, if you have a cauldron and want the player to be able to place water/lava in it, you could add minecraft:lava_cauldron
as an alternative block. Otherwise, the automatic shell repair system will not allow the block type to change.

### Exporting Custom Shell Types

Run `/sh custom export <name>`. Copy the file created in the `plugins/Schneckenhaus/export` directory.

### Importing Custom Shell Types

Place the file to import in the `plugins/Schneckenhaus/import` directory.
Move to the world and position where you want the template to be placed.
It is recommended to place your template in the default snail shell world.
To do that, type `/sh tp world schneckenhaus` to be teleported to this world.
Now make sure that your x and z coordinate are both negative, and that you are far enough away from positive x or z coordinates (e. g. x = z = -100)
This is important because if you place the template too near to other shells (which always have positive x and z coordinates), the template's area
and the shell's area could overlap.
Now run `/sh custom import <filename> as <name>`
`name` will be the alias you use to refer to the newly imported shell type.
Custom shell types can't be renamed afterwad.
After importing, you can use the crafting recipe (see config file) or `/sh create custom template <name>` to create instances of your custom shell type. 


## Configuration

### Shell Conditions

Shell conditions are YAML objects that can be evaluated for shells that are being created or are already created.
Using these conditions, you can enable some features only for some shells.
There are multiple types of shell conditions.
If you have to specify a list of conditions, usually at least one has to be true.

Examples of the different condition types:

```yaml
type: shulker
size: {min: 5, max: 10} # optional
colors: green, red, white # optional
rainbow: false # optional
```

```yaml
type: chest
size: {min: 5, max: 10} # optional
wood: birch # optional
```

```yaml
type: head
```

```yaml
type: custom
template: your_template_name # optional
```

```yaml
type: creation
creator: 7370723c-1f89-4e7c-a9fe-30ba8b4f0ae3 # optional
creation_type: crafting # optional, can also be command or home
permission: some.permission # optional, permission which the creator must have, 
                            # can only be checked for online players
```

```yaml
type: not
condition:
  type: shulker
  size: {min: 10}
```

```yaml
type: and
conditions:
  - type: shulker
  - type: creation
    creation_type: crafting
```

## Permissions

- `schneckenhaus.craft_shell` (default: true)
- `schneckenhaus.place_shell` (default: true)
- `schneckenhaus.enter_shell` (default: true)
- `schneckenhaus.quickly_enter_shell` (default: false)
- `schneckenhaus.enter_nested_shells` (default: true)
- `schneckenhaus.ask_for_enter_permission` (default: true)
- `schneckenhaus.change_enter_permission` (default: true)
- `schneckenhaus.bypass_shell_enter_permission` (default: op)
- `schneckenhaus.change_build_permission` (default: true)
- `schneckenhaus.bypass_shell_build_permission` (default: op)
- `schneckenhaus.bypass_theft_prevention` (default: op)
- `schneckenhaus.home_shell` (default: false)
- `schneckenhaus.never_homeless` (default: true)
- `schneckenhaus.shell_in_ender_chest` (default: true)
- `schneckenhaus.open_own_snail_shell_menu` (default: true)
- `schneckenhaus.open_other_snail_shell_menus` (default: op)
- `schneckenhaus.create_snail_shell_copies` (default: op)
- `schneckenhaus.rename_snail_shell` (default: true)
- `schneckenhaus.edit_owners` (default: true)
- `schneckenhaus.change_snail_shell_color` (default: true)
- `schneckenhaus.toggle_rainbow_mode` (default: true)
- `schneckenhaus.upgrade_snail_shell_size` (default: true)
- `schneckenhaus.change_shell_wood` (default: true)
- `schneckenhaus.delete_shell` (default: op)
- `schneckenhaus.placements.view` (default: true)
- `schneckenhaus.placements.view_positions` (default: true)
- `schneckenhaus.placements.teleport` (default: op)
- `schneckenhaus.bypass_escape_prevention` (default: op)
- `schneckenhaus.command.select` (default: op)
- `schneckenhaus.command.list` (default: op)
- `schneckenhaus.command.create` (default: op)
- `schneckenhaus.command.info` (default: op)
- `schneckenhaus.command.tag` (default: op)
- `schneckenhaus.command.item` (default: op)
- `schneckenhaus.command.count` (default: op)
- `schneckenhaus.command.delete` (default: op)
- `schneckenhaus.command.tp` (default: op)
- `schneckenhaus.command.home.tp.own` (default: op)
- `schneckenhaus.command.home.tp.others` (default: op)
- `schneckenhaus.command.home.manage` (default: op)
- `schneckenhaus.command.discord` (default: op)
- `schneckenhaus.command.custom` (default: op)
- `schneckenhaus.command.debug` (default: false)