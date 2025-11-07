package com.owesome.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.owesome.data.entities.GroupCompact
import com.owesome.data.repository.GroupRepository
import kotlinx.coroutines.flow.StateFlow

class GroupViewModel(private val repository: GroupRepository): ViewModel() {
    fun getText(): String {
        return "Group View Model";
    }

    fun getAllGroups(): List<GroupCompact> {
        return mutableListOf(
            GroupCompact(0, "Vacation to Prague", "", -400),
            GroupCompact(1, "Household", "", 2500),
        )
    }
}