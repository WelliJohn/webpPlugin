package org.wellijohn.androidwebp

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidWebpPlugin implements Plugin<Project> {
    private static final String TASK_NAME = "WebpPlugin"

    void apply(Project project) {
        project.extensions.create("androidwebp", AndroidWebpConfig)

        project.android.applicationVariants.all {
                //拿到的是ApplicationVariant
            variant ->
                variant.outputs.each { output ->
                    def webpTask = project.task(type: WebpTask, "${TASK_NAME}${variant.name.capitalize()}") {
                        resourcesPath = variant.mergeResources.outputDir
                    }

                    webpTask.dependsOn variant.mergeResources
                    output.processResources.dependsOn webpTask
                }
        }
    }
}

class AndroidWebpConfig{
    String cwebp = "cwebp"
    int quality = 80
}