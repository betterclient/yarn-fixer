# Yarn Fixer

## Made for people who wants to use yarn mappings for a minecraft client

## Can Now Also Remove @Enviorment(EnvType.CLIENT) without needing source code

# Building
Get jdk 1.9+, asm-all and build it like any other project

# Removes Lines (source)

@Environment(EnvType.CLIENT)

import net.fabricmc.api.Environment;

import net.fabricmc.api.EnvType;

/*      */

/* linenumber */

# Removes Lines (compiled)

@Environment(EnvType.CLIENT)

import net.fabricmc.api.Environment;

import net.fabricmc.api.EnvType;
