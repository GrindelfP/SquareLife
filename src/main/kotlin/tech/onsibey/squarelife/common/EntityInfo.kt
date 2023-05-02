package tech.onsibey.squarelife.common

import tech.onsibey.squarelife.simulator.entities.Coordinate
import tech.onsibey.squarelife.simulator.entities.Entity
import kotlin.reflect.KClass

data class EntityInfo(
    val entityType: KClass<out Entity>,
    val coordinates: Set<Coordinate>
)
