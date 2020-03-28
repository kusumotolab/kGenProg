<div align="center">
    <img src="assets/logo.png" width="40%">
</div>

<p align="center">
    <strong>kGenProg is an Automated Program Repair tool written in Java for Java.</strong><br>
    This is a reimplementation of GenProg, which automatically repairs bugs using genetic algorithm.
</p>

<p align=center>
    <a href="https://github.com/kusumotolab/kGenProg/releases/latest" alt="release"><img src="https://img.shields.io/github/release/kusumotolab/kGenProg.svg"></a>
    <a href="https://jitpack.io/#kusumotolab/kGenProg" alt="jitpack"><img src="https://jitpack.io/v/kusumotolab/kGenProg.svg"></a>
    <a href="https://circleci.com/gh/kusumotolab/kGenProg/tree/master" alt="CircleCI"><img src="https://circleci.com/gh/kusumotolab/kGenProg/tree/master.svg?style=shield"></a>
    <a href="https://codecov.io/gh/kusumotolab/kGenProg" alt="Codecov"><img src="https://codecov.io/gh/kusumotolab/kGenProg/branch/master/graph/badge.svg"></a>
    <a href="https://github.com/kusumotolab/kGenProg/blob/master/LICENSE" alt="license"><img src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
</p>

<p align=center>
    :us:English &nbsp; <a href="./doc/ja/README.md"> :jp:日本語</a>
</p>

---

## Requirements
- JDK8+


## Installation
Just [download](https://github.com/kusumotolab/kGenProg/releases/latest) a jar file.

There are sample bugs in [kusumotolab/kGenProg-example](https://github.com/kusumotolab/kGenProg-example).
You can download all the bugs from [here](https://github.com/kusumotolab/kGenProg-example/archive/master.zip).


## Gradle
kGenProg is also available for Gradle environments.
Currently, kGenProg is distributed on JitPack.
To add JitPack repository to your build file, append it in your build.gradle at the end of repositories.

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Then, please add the dependency to kGenProg.

```
dependencies {
    implementation 'com.github.kusumotolab:kGenProg:Tag'
```
Please replace `Tag` with the version ID that you want to use.
You can find more detailed descriptions [here](https://jitpack.io/#kusumotolab/kGenProg/).


## Usage
```
$ java -jar path/to/kGenProg.jar [(-r <path> -s <path>... -t <path>...) | --config <path>]
    [-x <fqn>...] [-c <path>...] [-w <path>] [-o <path>] [-v | -q] [--headcount <num>]
    [--max-generation <num>] [--time-limit <sec>] [--test-time-limit <sec>]
    [--required-solutions <num>] [--random-seed <num>] [--fault-localization <name>]
```

### Example
There are some artificial bugs for testing kGenProg in [kGenProg/example](example).
Run kGenProg with the following command for [kGenProg/example/CloseToZero01](example/CloseToZero01).

```sh
$ cd kGenProg/example/CloseToZero01
$ java -jar path/to/kGenProg.jar -r ./ -s src/example/CloseToZero.java -t src/example/CloseToZeroTest.java
```

You can configure parameters with a `.toml` file using `--config` option.
See [doc/kgenprog-config-template.toml](doc/kgenprog-config-template.toml) to learn how to write a config file.
```sh
$ java -jar path/to/kGenProg.jar --config kGenProg/example/CloseToZero01/kgenprog.toml
```

If you pass no options, kGenProg reads config file named `kgenprog.toml` in the current directory.
```sh
$ cd kGenProg/example/CloseToZero01
$ java -jar path/to/kGenProg.jar
```


### Options
| Option | Description | Default |
|---|---|---|
| `-r`, `--root-dir` | Specifies the path to the root directory of the target project. It is recommended to specify the current directory after moving into the root directory of the target project, for implementation reason. | Nothing |
| `-s`, `--src` | Specifies paths to "product" source code (i.e. main, non-test code), or to directories containing them. Paths are separated with spaces. | Nothing |
| `-t`, `--test` | Specifies paths to test source code, or to directories containing them. Paths are separated with spaces. | Nothing |
| `-x`, `--exec-test` | Specifies fully qualified names of test classes executed during evaluation of variants (i.e. fix-candidates). It is recommended to specify test classes detecting a bug. Class names are separated with spaces. | All test classes |
| `-c`, `--cp` | Specifies class paths needed to build the target project. Paths are separated with spaces. | Nothing |
| `-o`, `--out-dir` | Writes patches kGenProg generated under the specified directory. Patches are outputted to a directory having a name of the execution time and date under the specified directory. | A directory named `kgenprog-out` is created in the current directory. |
| `-v`, `--verbose` | Be more verbose, printing DEBUG level logs. | `false` |
| `-q`, `--quiet` | Be more quiet, suppressing non-ERROR logs. | `false` |
| `--config` |  Specifies the path to config file. | Reads config file named `kgenprog.toml` in the current directory. |
| `--mutation-generating-count` | Specifies how many variants are generated in a generation by a mutation. | 10 |
| `--crossover-generating-count` | Specifies how many variants are generated in a generation by a crossover. | 10 |
| `--headcount` | Specifies how many variants survive in a generation. | 100 |
| `--max-generation` | Terminates searching solutions when the specified number of generations reached. | 10 |
| `--time-limit` | Terminates searching solutions when the specified time in seconds has passed. | 60 |
| `--test-time-limit` | Specifies a time limit in seconds to build and test each variant. | 10 |
| `--required-solutions` | Terminates searching solutions when the specified number of solutions are found. | 1 |
| `--random-seed` | Specifies a random seed used by a random number generator. | 0 |
| `--scope` | Specify the scope from which source code to be reused is selected. (`PROJECT`, `PACKAGE`, `FILE`). | `PACKAGE` |
| `--fault-localization` | Specifies technique of fault localization. (`Ample`, `Jaccard`, `Ochiai`, `Tarantula`, `Zoltar`). | `Ochiai` |
| `--crossover-type` | Specifies crossover type. (`Random`, `Single`, `Uniform`). | `Random` |
| `--crossover-first-variant` | Specifies first variant selection strategy for crossover. (`Elite`, `Random`). | `Random` |
| `--crossover-second-variant` | Specifies second variant selection strategy for crossover.  (`Elite`, `GeneSimilarity`, `Random`, `TestSimilarity`). | `Random` |
| `--history-record` | Stores generation process of each variant and all generated variants. | `false` |


## Use in your research

If you are using kGenProg in your research, please cite the following paper:

Y. Higo, S. Matsumoto, R. Arima, A. Tanikado, K. Naitou, J. Matsumoto, Y. Tomida, and S. Kusumoto, "kGenProg: A High-Performance, High-Extensibility and High-Portability APR System," 2018 25th Asia-Pacific Software Engineering Conference (APSEC), Nara, Japan, 2018, pp. 697-698. [[available online](https://doi.org/10.1109/APSEC.2018.00094)]
```
@Inproceedings{8719559,
 author={Y. {Higo} and S. {Matsumoto} and R. {Arima} and A. {Tanikado} and K. {Naitou} and J. {Matsumoto} and Y. {Tomida} and S. {Kusumoto}},
 booktitle={2018 25th Asia-Pacific Software Engineering Conference (APSEC)},
 title={kGenProg: A High-Performance, High-Extensibility and High-Portability APR System},
 year={2018},
 pages={697-698},
}
```


## Awards
- [Best Poster Award - APSEC 2018](http://www.apsec2018.org/)  
kGenProg: A High-Performance, High-Extensibility and High-Portability APR System

