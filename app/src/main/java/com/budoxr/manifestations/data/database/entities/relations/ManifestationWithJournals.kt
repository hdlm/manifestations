package com.budoxr.manifestations.data.database.entities.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import java.util.Objects

class ManifestationWithJournals {
    @Embedded
    lateinit var manifestation: ManifestationEntity

    @Relation(parentColumn = "id", entityColumn = "manifestation_id")
    lateinit var _journals: List<JournalEntity>

    @get:Ignore
    val journal : JournalEntity
        get() = _journals.last()

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1(): ManifestationEntity = manifestation
    operator fun component2(): List<JournalEntity> = _journals

    override fun equals(other: Any?): Boolean = when {
        other == this -> true
        other is ManifestationWithJournals -> manifestation == other.manifestation && _journals == other._journals
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(manifestation, _journals)
    
}