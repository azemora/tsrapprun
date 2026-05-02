package com.tsrapprun

expect fun getPlatformName(): String

/**
 * Relógio multiplataforma.
 * Em millis desde epoch. Usado por scheduling/timestamps em commonMain.
 */
expect fun currentTimeMillis(): Long
