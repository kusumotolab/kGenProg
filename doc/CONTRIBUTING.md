# kGenProg Contribution Guide
## Introduction
Thank you for interest in contributing to **kGenProg**.
This document describes how to setup development environment as a kGenProg contributor.

## Requirements
- JDK11+
- Gradle

## Getting setup
We strongly recommend to use IntelliJ instead of Eclipse due to the difference of code formatter.

### for IntelliJ developers

1. Get the repository
```shell
$ git clone https://github.com/kusumotolab/kGenProg
$ cd kGenProg
$ git submodule update --init
```

2. Import the project
```
(on IntelliJ)

Import Project
  -> Specify "PATH_TO_REPO"
  -> Check "Import project from external model"
  -> Select "Gradle"
  -> Check "Use default Gradle wrapper (recommended)"
```

3. Follow the coding style
```
(on IntelliJ menubar)

File
  -> Settings
  -> Editor > Code Style
  -> Gear icon
  -> Import Scheme
  -> IntelliJ IDEA code style XML
  -> Specify "PATH_TO_REPO/settings/intellij-coding-style.xml"
```


### for Eclipse developers

1. Get the repository
```shell
$ git clone https://github.com/kusumotolab/kGenProg
$ cd kGenProg
$ git submodule update --init
```

2. Resolve dependencies and build the project
```shell
$ ./gradlew build
```

3. Create eclipse configuration
```shell
$ ./gradlew eclipse
```

4. Import the project
```
(on Eclipse menubar)

File
 -> Import
 -> Existing projects into Workspace
 -> Next
 -> Specify "PATH_TO_REPO/"
 -> Finish
```

5. Follow the coding style
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

## Coding style
- Contributors must follow our defined coding style ([eclipse](../blob/master/settings/eclipse-coding-style.xml), [intellij](../blob/master/settings/intellij-coding-style.xml)).
This style is based on [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
- Please use **LF** as line ending.
- Please use **spaces**, do not use tabs.
