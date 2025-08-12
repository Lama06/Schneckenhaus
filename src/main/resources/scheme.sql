CREATE TABLE data_version (
    data_version TEXT NOT NULL
) STRICT;

CREATE TABLE shells (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    world TEXT NOT NULL,
    position INTEGER NOT NULL CHECK ( position >= 1 ),

    type TEXT NOT NULL,

    creation_type TEXT NOT NULL,
    creator TEXT NOT NULL,
    creation_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    name TEXT,

    enter_permission_mode TEXT NOT NULL,
    build_permission_mode TEXT NOT NULL,

    UNIQUE (position, world)
) STRICT;

CREATE TABLE sized_shells (
    id INTEGER PRIMARY KEY,

    size INTEGER,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shulker_shells (
    id INTEGER PRIMARY KEY,

    color TEXT,
    rainbow INTEGER,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shulker_shell_rainbow_colors (
    id INTEGER,
    color TEXT NOT NULL,
    enabled INTEGER NOT NULL,

    PRIMARY KEY (id, color),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE custom_shells (
    id INTEGER PRIMARY KEY,

    template TEXT,

    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_tags (
    id INTEGER,
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
    id INTEGER,

    world TEXT,
    x INTEGER,
    y INTEGER,
    z INTEGER,

    placed_by TEXT,
    time TEXT DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (world, x, y, z),
    FOREIGN KEY (id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE home_shells (
    player TEXT PRIMARY KEY,
    home_id INTEGER,

    UNIQUE (home_id),
    FOREIGN KEY (home_id) REFERENCES shells(id) ON DELETE CASCADE
) STRICT;

CREATE TABLE shell_access_statistics (
    shell INTEGER NOT NULL,
    player TEXT NOT NULL,

    amount INTEGER NOT NULL CHECK (amount >= 0),
    last_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (shell, player),
    FOREIGN KEY (shell) REFERENCES shells(id) ON DELETE CASCADE
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