CREATE TABLE data_version (
    plugin_version TEXT NOT NULL,
    game_version TEXT NOT NULL
) STRICT;

CREATE TABLE custom_shell_type (
    size_x INTEGER NOT NULL,
    size_y INTEGER NOT NULL,
    size_z INTEGER NOT NULL,

    item TEXT NOT NULL, -- item identifier

    menu_block_x INTEGER,
    menu_block_y INTEGER,
    menu_block_z INTEGER,

    spawn_x REAL,
    spawn_y REAL,
    spawn_z REAL,
    spawn_yaw REAL,
    spawn_pitch REAL,

    creation_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
) STRICT;

CREATE TABLE blocks (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    block TEXT NOT NULL, -- block data string
    initial INTEGER NOT NULL CHECK (initial IN (TRUE, FALSE)),

    PRIMARY KEY (x, y, z)
) STRICT;

CREATE TABLE exit_blocks (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,

    PRIMARY KEY (x, y, z)
) STRICT;

CREATE TABLE crafting_ingredients (
    item TEXT NOT NULL, -- item identifier
    amount INTEGER NOT NULL
) STRICT;

CREATE TABLE alternative_blocks (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    alternative_block TEXT NOT NULL, -- block identifier

    PRIMARY KEY (x, y, z, alternative_block)
) STRICT;