package org.wellijohn.androidwebp

import org.gradle.api.*

class AndroidWebpPlugin implements Plugin<Project> {
    void apply(Project project){
        project.android.applicationVariants.all{//拿到的是ApplicationVariant
            applicationVariant->applicationVariant.outputs.each{//拿到的是BaseVariantOutput
                baseVariantOutput->

                def webTask = project.task(type:WebpTask){
                    resourcesPath = applicationVariant.mergeResources.outputDir
                }

                webTask.dependsOn variant.mergeResources
                applicationVariant.processResources.dependsOn webTask

            }
        }
    }


    class WebpTask extends DefaultTask{
        File resourcesPath;

        @Override
        Task doLast(Action<? super Task> action) {
            Action action1 = new Action() {
                @Override
                void execute(Object o) {
                    resourcesPath.eachDirMatch(~/^drawable.*|^mipmap.*/) { dir ->
                        dir.eachFileMatch(FileType.FILES, ~/.*\.png$/) { file ->
                            if(!(file.name ==~/.*\.9\.png$/)) {
                                logger.debug("found file: ${file}")
                                String webpFileName = file.getAbsolutePath()
                                webpFileName = webpFileName[0..webpFileName.lastIndexOf('.')-1] + ".webp"
                                logger.debug("webp file: ${webpFileName}")
                                try {
                                    def cwebp = [
                                            "${project.androidwebp.cwebp}",
                                            "-q",
                                            "${project.androidwebp.quality}",
                                            file,
                                            "-o",
                                            webpFileName
                                    ]
                                            .execute()
                                    cwebp.waitFor()

                                    if(cwebp.exitValue() != 0) {
                                        logger.error("cwebp with error code ${cwebp.exitValue()} and: ${cwebp.err.text}")
                                    } else {
                                        file.delete()
                                    }

                                } catch(IOException ioe) {
                                    logger.error("Could not find 'cwebp'. Tried these locations: ${System.getenv('PATH')}")
                                }
                            }
                        }
                    }
                }
            }
            return action1;
        }
    }
}