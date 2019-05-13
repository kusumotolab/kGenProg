# kGenProg Contribution Guide
## Introduction
Thank you for interest in contributing to **kGenProg**.
This document describes how to setup development environment as a kGenProg contributor.

## Requirements
- JDK 1.8
- Gradle

## Getting setup
1. Get the repository
```shell
$ git clone https://github.com/kusumotolab/kGenProg
$ cd kGenProg
```
2. Resolve dependencies and build the project
```shell
$ ./gradlew build
```

### for Eclipse developers
1. Create eclipse configuration
```shell
$ ./gradlew eclipse
```

2. Import the project
```
(on Eclipse menubar)

File
 -> Import
 -> Existing projects into Workspace
 -> Next
 -> Specify "PATH_TO_REPO/"
 -> Finish
```

3. Follow the coding style
```
(on Eclipse menubar)

Project
 -> Properties
 -> Java Code Style
 -> Formatter
 -> Enable project specific settings
 -> Import
 -> Specify "PATH_TO_REPO/settings/eclipse-coding-style.xml"
 -> Apply
```

### for Intellij developers
1. Create intellij configuration
```shell
$ ./gradlew idea
```

2. Import the project
```
Import Project

PATH_TO_REPO
```

## Coding style
- Contributors must follow our defined coding style ([eclipse](../blob/master/settings/eclipse-coding-style.xml), [intellij](../blob/master/settings/intellij-coding-style.xml)).
This style is based on [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
- Please use **LF** as line ending.
- Please use **spaces**, do not use tabs.
