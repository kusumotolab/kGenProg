:us: [:jp:](./doc/ja/README.md)

[![CircleCI](https://circleci.com/gh/kusumotolab/kGenProg/tree/master.svg?style=svg)](https://circleci.com/gh/kusumotolab/kGenProg/tree/master)

# kGenProg
kGenProg is an Automated Program Repair tool written in Java for Java.
This is a reimplementation of GenProg, which automatically repairs bugs using genetic algorithm.


## Requirements
- JDK8


## Installation
Just [download](https://github.com/kusumotolab/kGenProg/releases/latest) a jar file.

There are sample bugs in [kusumotolab/kGenProg-example](https://github.com/kusumotolab/kGenProg-example).
You can download all the bugs from [here](https://github.com/kusumotolab/kGenProg-example/archive/master.zip).


## Usage
```
$ java -jar path/to/kGenProg.jar
$ java -jar path/to/kGenProg.jar --config <path>
$ java -jar path/to/kGenProg.jar -r <path> -s <path>... -t <path>... [-x <fqn>...] [-c <path>...]
    [-w <path>] [-o <path>] [-v | -q] [--siblings-count <num>] [--headcount <num>]
    [--max-generation <num>] [--time-limit <sec>] [--test-time-limit <sec>]
    [--required-solutions <num>] [--random-seed <num>]

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
| `-w`, `--working-dir` | Specifies a path to a working directory. | A working directory having a name that starts with `kgenprog-work` is created under the system temporary directory (i.e. `$TMPDIR` on *nix, `%TMP%` on Windows) every kGenProg execution. |
| `-o`, `--out-dir` | Writes patches kGenProg generated under the specified directory. Patches are outputted to a directory having a name of the execution time and date under the specified directory. | A directory named `kgenprog-out` is created in the current directory. |
| `-v`, `--verbose` | Be more verbose, printing DEBUG level logs. | `false` |
| `-q`, `--quiet` | Be more quiet, suppressing non-ERROR logs. | `false` |
| `--mutation-generating-count` | Specifies how many variants are generated in a generation by a mutation. | 10 |
| `--crossover-generating-count` | Specifies how many variants are generated in a generation by a crossover. | 10 |
| `--headcount` | Specifies how many variants survive in a generation. | 100 |
| `--max-generation` | Terminates searching solutions when the specified number of generations reached. | 10 |
| `--time-limit` | Terminates searching solutions when the specified time in seconds has passed. | 60 |
| `--test-time-limit` | Specifies a time limit in seconds to build and test each variant. | 10 |
| `--required-solutions` | Terminates searching solutions when the specified number of solutions are found. | 1 |
| `--random-seed` | Specifies a random seed used by a random number generator. | 0 |
| `--scope` | Specify the scope from which source code to be reused is selected. (`PROJECT`, `PACKAGE`, `FILE`). | `PACKAGE` |

