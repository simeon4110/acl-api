# Arts Criticism Lab Database Public Source

**Under very active development, not quite ready for live use.**

This repository contains the currently deployed version of the code running at 
https://api.acriticismlab.org. Contributions to the code base are welcomed, 
anyone is free to create a merge request or fork the code to run on another site.
All sensitive parameters are handled as environment variables, the names of which
can be found in application.properties.

The code is all Spring Boot (Security, Hibernate, JPA, MVC) based on the back end, 
and angular on the front end (the front-end code is not in this repo). There is 
very little deviation from the standard Spring MVC design pattern. 

All of the project's code and dependencies are open source, but not all of the 
dependencies are licenced under the same agreement: please ensure you have 
read and understood any licencing requirements before you put this code into 
production. The code in this repository is all licensed under the GPL-3.0 license.

### API Documentation

All documentation for interacting over the internet with the ACL database can be found at 
[https://api.acriticismlab.org/swagger-ui.html](https://api.acriticismlab.org/swagger-ui.html). 
This readme only applies to those who wish to contribute to the back-end of the 
ACL project. If you're using the database, the api documentation is where you need to go.

## Architecture

*If Spring Boot does it, don't do it again.*

*Write functions in Scala. Java f##king hates them, and you'll hate yourself for 
coding anything static in an OO-only language anyway.*

**Production JDK:** AdoptOpenJdk 11.0.3 with HotSpot. This is the only JDK the 
project is developed with and verified to run on.

**Production Scala SDK:** 2.12.8. This is the only SDK the project will even
compile on. Don't try to use anything else.

The basic pattern for the entire project is MVC style. Controllers handle endpoint
definitions, documentation (via SpringFox,) cache policies, and security--they 
are heavy on annotations and light on code (they look like shit aesthetically, 
but what can you do?) Services handle all business logic, and models are where 
the object models are defined. 

The Spring Boot automatic repositories are relied upon for 99.99% of code to
database interfacing. Anything weird is handled via direct JPQL in the 
repository interface. (Note: there are still a few stored procedures that have
not yet been converted to JPQL in the PoemRepository).   

Things are still a bit disorganized package wise, but should be cleaned up
and finalized before version 4.0 sometime mid-July. 

### Persistence (Spring JPA / Hibernate)

**Database Tech:** MariaDB.

**Cache Tech:** Redis.

Everything related to object persistence is handled by Spring JPA and Hibernate.
There is nothing out of the ordinary here. Inbound data from POST requests is
always mapped onto a DTO for validation of the fields, outbound data for single
objects is directly generated from the source model, and outbound data for 
large requests are mapped to OutDto's via JPQL to save on data size and
processor cycles.
 
* Object models are in ../persistence/models
* Repository interfaces are in ../persistence/repositories
* DTO models are in ../persistence/dtos

The use of @ManyToAny annotations requires a bit of finesse with orphan removal,
the logic behind this can be found in ../persistence/listeners and an in-depth
explanation of the problem can be found 
[here](https://medium.com/@joshuajharkema/spring-boot-hibernate-and-manytoany-orphan-removal-aeb17a457b21).

Everything high-traffic is cached in Redis (cache policies are defined in controllers).
Eviction and cache policies are complicated af, but if you look at a working
policy (poems are a good one) it's not too hard to figure it out. Err on the
side of purging all keys to prevent old data from hanging out in the cache and
gumming up the works. 

**A note on Corpora:** Corpora are comprised of an @ManyToAny collection of Items
(except Play sub-items...). With this in mind, you should understand why the whole
object model is inheritance based: users must be able to make a collection
of all different item types, and the easiest way to deal with this is to
have all item types inherit the same super class. 

### Security and Authentication

All of this is handled by a mostly stock implementation of Spring Security.
OAuth 2.0 tokens are generated and stored in the database directly; the 
user credential flow is used because security isn't enough of an issue
to merit the implicit flow. 

### Controllers

Basic REST principles are followed wherever possible. Endpoints are split up
by object type. Plays are a bit weird because of how much composition is involved
in organizing them correctly. It might be a good idea to merge all Play related
functionality into a single controller, but this is a problem for another day.

### Services

All logic is handled in Services, no other type of Object should contain methods
more complex than a simple return statement--helpers and tool classes aside. 
Anything repetitive and static should be coded in Scala rather than Java. FP
should be used whenever possible (for an example of how powerful this is when
applied correctly, take a look at the functional implementation of Lucene.)

All user facing exceptions must return a status code and must have its stack
trace masked. These status codes must also be added to the documentation of
any controller that may return the exception code. 

### Search

Search is handled by Lucene 8.0.0 *not* the standard Spring/Hibernate search
implementation (it's a mess and nearly two years behind the current version of
Lucene.) The implementation is pretty spread out, but all the heavy lifting
is done in /scala/org/acl/database/search/SearchRepository.scala, 
../config/LuceneConfig handles all the initial indexing on application load, and
all query related functionality is handled in ../services/search/SearchQueryHandlerService. 

Search is probably not a good place to jump into this project. It's a bit of a
mess. 

### Tests

Lol. Have at er'! The project has been too fluid for tests to have value,
now is probably a good time to start writing them. 

## Contributing

Basically just grab an issue and get started. To start working on the project 
hit up joshua.harkema@ucalgary.ca so he can send you some test SQL data (or 
connect you to a test database over VPN.)