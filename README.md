# Beyond Aporia Public Source

This repository contains the currently deployed version of the code running at https://beyondaporia.com. Contributions to the code base are welcomed, anyone is free to create a merge request or fork the code to run on another site.

The code is all Spring Boot (Security, Hibernate, JPA, MVC) based on the back end, and Bootstrap 4, JQuery, and Thymeleaf on the front end. There is very little deviation from the standard Spring MVC design pattern. 

All of the project's code and dependencies are open source, but not all of the dependencies are licenced under the same agreement: please ensure you have read and understood any licencing requirements before you put this code into production. The code in this repository is all licensed under the GPL-3.0 license.

### API Documentation

All documentation for the ACL database can be found in the wiki [https://git.joshharkema.com/sonnet-dev/sonnet/wikis/API-Documentation](https://git.joshharkema.com/sonnet-dev/sonnet/wikis/API-Documentation). This readme only applies to those who wish to contribute to the back-end of the ACL database. If you're using the database, the wiki is where you need to go.

### Guidelines (More to Come)

1. Use DTO's on *intake* and expose model objects directly on display.
2. Never expose a repository to a controller, use a service.
3. Controllers should always return a string (simple is better than complex).
4. Services that return a success/error response should return the response as a redirect string with an ?param to trigger an error message.
5. Do not add any proprietary JS that isn't already included in the code itself. This does not include any *custom* JS either inlined or linked from resources/static assets.
6. Add Maven dependencies only after consulting with the repo managers.
7. Don't fuck with the config files (unless you *really* need to; see guideline 8). 
8. Write tests *before* you commit.
9. SonarQube server access will be granted to anyone who wants to make a merge request. The CI integration with SQ will be happening in the next couple weeks. If you want to check against a personal server, the quality gate is set to the defaul SonarWay for both Java and XML.
10. All config is done with @Bean annotations, XML configs will be denied outright.
11. In short, **the code base is heavilly oppinionated toward the Spring Boot v4 model.**
12. This is not a democracy, untill there are more than 3 active contributors to this project, it's a Josh-ocracy.
13. Sometimes we do things for the Qube that don't make sense, but we do them anyways. All hail the Qube.

### Instructions for Development Install

1. Clone the repo.
2. Change the SQL server settings in src/main/resources/application.properties to your local settings. If you need dummy data let the repo managers know and we'll make some. The tests all generate their own content and the tests/tools directory has a random sonnet and specific sonnet generator to cover 99% of use cases. The SonnetEditTest needs an existing sonnet, but it's easy to just save one for this purpose.
3. Run "mvn clean" and "mvn install" in the project root to install project dependencies.
4. Configure your IDE to run the Spring Boot main class "com.sonnets.sonnet.SonnetApplication." 
5. Make your changes (don't track application.properties or TestJpaConfig.java).
6. Run all tests (adding / changing tests to meet any modified / added code; less than 80% coverage on new code will automatically fail the quality gate--there are exceptions to this of course (i.e. config files), but they must be authorized on a case-by-case basis).
7. Create a merge request.

### For Running Tests

1. Change the SQL server settings in src/test/com/sonnets/sonnet/config/TestJpaConfig.java to your local settings.
2. Run all tests with "mvn test".
3. Make sure all tests pass.

**Notes on tests:**

The TEI test fails if you use the git version, simply copy the test output to validTEI.xml (from the failed test) and run it again. This is assuming you haven't modified the TEI converter, if you have, make sure your output produces valid TEI formatted files. 
