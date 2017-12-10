package ru.artembotnev.tasks

import java.util.*

/**
 * Created by Artem Botnev on 11.11.2017.
 */

data class Task(val id: UUID){
    constructor(): this(UUID.randomUUID())

    var title = ""
    var description = ""
    var createDate: Date? = null
    var eventDate: Date? = null
    var alertDate: Date? = null
    var isAlertActive = false

    init {
        createDate = Date()
    }

    override fun toString(): String = title
}

