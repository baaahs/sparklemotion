package baaahs

import kotlin.random.Random

fun <E> List<E>.random(): E? = if (size > 0) get(Random.nextInt(size)) else null

fun <E> List<E>.random(random: Random): E? = if (size > 0) get(random.nextInt(size)) else null