# FlakyHoover

FlakyHoover is a test smell detector that implements test smells that are related to non determinnistic behaviour, i.e. flaky tests. FlakyHoover was implemented as a part of the thesis "Non-deterministic tests and where to find them: empirically investigating the relationship between flaky tests and test smells by examining test order dependency" conducted at Linköping University 2018.

### Introduction
Continuous Integration (CI) is a software practice that relies on developers integrating their work continuously in order to always keep the software ready for deployment. CI systems allow this process to be automated by compiling, building, testing and deploying software [[1](https://martinfowler.com/articles/continuousIntegration.html), [2](https://www.amazon.com/Continuous-Integration-Improving-Software-Reducing/dp/0321336380)]. The purpose of Continuous Integration is not fulfilled when code that breaks test suites is integrated, when this occurs the discipline is not realized and the tests lose their sense of purpose. Always keeping the software ready for deployment is carried out by testing newly added code. If all tests pass, then, the new code is integrated and developers can be confident that the code is correct. However, this tactic is not always easy to follow when flaky tests infect the test suites. Flaky tests are tests that pass and fail when no changes have been made, thus developers do not know if their newly added code broke the test suite or not. 

### Getting Started

##### Prerequisites
Minimum requirement to run is a Java Runtime Environment (JRE). For more information about JRE, see Santa Cruz answer of StackOverflow [[3]](https://stackoverflow.com/questions/1906445/what-is-the-difference-between-jdk-and-jre).

##### How to use
FlakyHoover is easy run, simply run the jar file like the example below:
```Bash
$ java -jar flakyhoover.jar <path to project> <project name> 
```
The following command would then produce an csv file containing all test methods that were detected as having a smell. The name of the file follow the following syntax: "project name_results.csv".

#### Changelog (dev branch)

#### 2020-02-16 - Refactored and optimized Test Run War and made the implementation more correct. 

In this version FlakyHoover only detect test methods annotated with @Test compared to previous version. Annotations were orignally introduced in Junit4 that was released in 2006. 

### Contact

Please feel free to contact me on my student mail andla830@student.liu.se.


### Sources
[1] Martin Fowler, “Continuous Integration,” 2006. [Online]. Available:
https://martinfowler.com/articles/continuousIntegration.html

[2] P. M. Duvall, S. Matyas, and A. Glover, Continuous integration : improving
software quality and reducing risk. Addison-Wesley, 2007.

[3] Pablo Santa Cruz, Dec 15 '09 at 10:21 
https://stackoverflow.com/questions/1906445/what-is-the-difference-between-jdk-and-jre
