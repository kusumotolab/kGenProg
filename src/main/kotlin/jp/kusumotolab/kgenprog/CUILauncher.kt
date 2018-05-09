package jp.kusumotolab.kgenprog

import jp.kusumotolab.kgenprog.fl.FaultLocalization
import jp.kusumotolab.kgenprog.fl.Ochiai
import jp.kusumotolab.kgenprog.ga.Crossover
import jp.kusumotolab.kgenprog.ga.DefaultCodeValidation
import jp.kusumotolab.kgenprog.ga.DefaultSourceCodeGeneration
import jp.kusumotolab.kgenprog.ga.DefaultVariantSelection
import jp.kusumotolab.kgenprog.ga.Mutation
import jp.kusumotolab.kgenprog.ga.RandomMutation
import jp.kusumotolab.kgenprog.ga.SiglePointCrossover
import jp.kusumotolab.kgenprog.ga.SourceCodeGeneration
import jp.kusumotolab.kgenprog.ga.SourceCodeValidation
import jp.kusumotolab.kgenprog.ga.VariantSelection
import jp.kusumotolab.kgenprog.project.ClassPath
import jp.kusumotolab.kgenprog.project.SourceFile
import jp.kusumotolab.kgenprog.project.TargetProject
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.util.*

class CUILauncher {

    @Option(name = "-s", aliases = ["--src"], required = true, usage = "Paths of the root directories holding source codes")
    var sourceFiles = mutableListOf("src/main/java")
    @Option(name = "-t", aliases = ["--test"], required = true, usage = "Paths of the root directories holding test codes")
    var testFiles = mutableListOf<String>()
    @Option(name = "-c", aliases = ["--cp"], required = true, usage = "Class paths required to build the target project")
    var classPaths = mutableListOf<String>()

    fun launch() {
        val sourceFiles = this.sourceFiles
                .map { SourceFile(it) }
                .toList()

        val testFiles = this.testFiles
                .map { SourceFile(it) }
                .toList()

        val classPaths = this.classPaths
                .map { ClassPath(it) }
                .toList()

        val targetProject = TargetProject(sourceFiles, testFiles, classPaths)
        val faultLocalization: FaultLocalization = Ochiai()
        val mutation: Mutation = RandomMutation()
        val crossover: Crossover = SiglePointCrossover()
        val sourceCodeGeneration: SourceCodeGeneration = DefaultSourceCodeGeneration()
        val sourceCodeValidation: SourceCodeValidation = DefaultCodeValidation()
        val variantSelection: VariantSelection = DefaultVariantSelection()

        val kGenProgMain = KGenProgMain(targetProject, faultLocalization, mutation, crossover, sourceCodeGeneration, sourceCodeValidation, variantSelection)
        kGenProgMain.run()
    }

    companion object {


        @JvmStatic
        fun main(args: Array<String>) {
            val launcher = CUILauncher()
            val parser = CmdLineParser(launcher)

            try {
                parser.parseArgument(*args)
            } catch (e: CmdLineException) {
                System.err.println(e.message)
                parser.printUsage(System.err)
                System.exit(1)
            }
            launcher.launch()
        }
    }
}
