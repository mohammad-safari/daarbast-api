# Daarbast - Server API ![Build status](https://github.com/mohammad-safari/daarbast-api/workflows/CI/badge.svg?branch=master)

daarbast is  is a web-based, kanban-style, list-making application developed for Web Engineering project. This project includes server api functionality for [daarbast-android](https://github.com/mh-zeynal/daarbast).

## Development usage guide

In order to run the project a version of latest Java LTS(21) is needed. using existing maven wrapper you can run or build the application.

install dependacies and build app

> ./mvnw clean install

run the project using the following command

> ./mvnw spring-boot:run

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* redis: [`redis:latest`](https://hub.docker.com/_/redis)

Please review the tags of the used images and set them to the same as you're running in production.

## Contributing

Here are key notes for contributing
* keep your code style steady and clean as possible for easy code review!
* commits are preferred to be as small as the task's scope
* master branch commit messages must be brief, clear and obey following the format:
  > `<COMMIT_TYPE_IN_UPPERCASE_AND_IN_BRACKETS>`50-char-limited commit message
  * commit types are 
    - `[INIT]` - for initializing the branch
    - `[ADD]` - for feature commits
    - `[FIX]` - for bug fix commits
    - `[ENH]` - for infra enhancement commits
    - `[REV]<target_commit_hash>` - for revising & refactoring previous commits
  * if you really need more than 50 chars to tell about your commit you can put it **in the next line** following this format
  >   descr:<your_extra_description>

## License

This application is released under GNU GPLv3 (see [LICENSE](LICENSE)).
