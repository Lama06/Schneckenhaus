CREATE TABLE data_version (
    data_version TEXT NOT NULL
) STRICT;

CREATE TABLE shells (
    -- needs to be autoincrement to prevent ids from being reused after shell deletion
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    world TEXT NOT NULL,
    -- id of the shell's position
    position INTEGER NOT NULL,

    -- can be crafting, command or home
    creation_type TEXT NOT NULL,
    -- uuid of the creator
    creator TEXT NOT NULL,
    creation_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- can be shulker, chest, head or custom
    type TEXT NOT NULL,

    name TEXT,

    -- can be everybody, nobody, whitelist or blacklist
    enter_permission_mode TEXT NOT NULL,
    build_permission_mode TEXT NOT NULL,

    UNIQUE (position, world)
) STRICT;

-- when a shell is deleted, its position is added to this table for reuse
CREATE TABLE unused_shell_positions (
    world TEXT NOT NULL,
    -- id of the unused position
    position INTEGER NOT NULL,

    PRIMARY KEY (world, position)
) STRICT;

-- stores the size of shulker and chest shells
CREATE TABLE sized_shells (
    id INTEGER PRIMARY KEY,

    size INTEGER NOT NULL,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shulker_shells (
    id INTEGER PRIMARY KEY,

    color TEXT NOT NULL,
    rainbow INTEGER NOT NULL,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

-- stores the colors the rainbow mode will cycle through on a shulker shell
CREATE TABLE shulker_shell_rainbow_colors (
    id INTEGER NOT NULL,
    color TEXT NOT NULL,

    enabled INTEGER NOT NULL,

    PRIMARY KEY (id, color),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE chest_shells (
    id INTEGER PRIMARY KEY,

    wood TEXT NOT NULL,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE head_shells (
    id INTEGER PRIMARY KEY,

    -- uuid of the head owner
    head_owner TEXT NOT NULL,
    texture INTEGER NOT NULL,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE,
    FOREIGN KEY (texture) REFERENCES head_shell_textures(id)
) STRICT;

CREATE TABLE head_shell_textures (
    id INTEGER PRIMARY KEY,

    texture BLOB NOT NULL,

    UNIQUE (texture)
) STRICT;

CREATE TABLE custom_shells (
    id INTEGER PRIMARY KEY,

    template TEXT NOT NULL,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_tags (
    id INTEGER NOT NULL,
    tag TEXT NOT NULL,

    PRIMARY KEY (id, tag),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_permissions (
    id INTEGER NOT NULL,
    player TEXT NOT NULL,

    owner INTEGER NOT NULL DEFAULT FALSE,
    enter_blacklist INTEGER NOT NULL DEFAULT FALSE,
    enter_whitelist INTEGER NOT NULL DEFAULT FALSE,
    build_blacklist INTEGER NOT NULL DEFAULT FALSE,
    build_whitelist INTEGER NOT NULL DEFAULT FALSE,

    PRIMARY KEY (id, player),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_placements(
    id INTEGER NOT NULL,

    world TEXT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,

    -- uuid of the player who placed this, null if unknown (e.g., placed by dispenser)
    placed_by TEXT,
    time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    name TEXT,

    exit_position_x REAL,
    exit_position_y REAL,
    exit_position_z REAL,
    exit_position_yaw REAL,
    exit_position_pitch REAL,

    PRIMARY KEY (world, x, y, z),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE home_shells (
    player TEXT PRIMARY KEY,
    id INTEGER NOT NULL,

    UNIQUE (id),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_access_statistics (
    id INTEGER NOT NULL,
    player TEXT NOT NULL,

    amount INTEGER NOT NULL,
    last_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id, player),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE previous_player_locations (
    player TEXT NOT NULL,
    time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    world TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL,
    yaw REAL NOT NULL,
    pitch REAL NOT NULL,

    PRIMARY KEY (player, time)
) STRICT;