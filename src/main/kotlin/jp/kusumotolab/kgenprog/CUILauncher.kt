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

class CUILauncher {

    var sourceFiles = listOf("src/main/java")
    var testFiles = emptyList<String>()
    var classPaths = emptyList<String>()

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
            /*
		CmdLineParser parser = new CmdLineParser(launcher);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			parser.printUsage(System.err);
			System.exit(1);
		}
		*/
            launcher.launch()
        }
    }
}
