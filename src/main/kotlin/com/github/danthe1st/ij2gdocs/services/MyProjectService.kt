package com.github.danthe1st.ij2gdocs.services

import com.github.danthe1st.ij2gdocs.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
