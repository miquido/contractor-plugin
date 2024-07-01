package com.miquido.plugin.contractor.task

import org.gradle.api.tasks.Copy

open class ExecutableCopyAction: Copy() {
    fun execute(){
        super.copy()
    }
}
