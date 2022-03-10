# jianqi-blog


![build](https://img.shields.io/circleci/build/github/jianqibot/jianqi-blog/main) ![](https://img.shields.io/github/languages/top/jianqibot/jianqi-blog) ![](https://img.shields.io/github/repo-size/jianqibot/jianqi-blog)


## $ Demo


## $ Feature

* Frame: Maven + Spring Boot + MySQL + MyBatis + Flyway 
* CI/CD: CircleCI
* Deploy: docker-compose

## $ Quick Start

Install [Docker](https://docs.docker.com/get-docker/) and [Docker-Compose](https://docs.docker.com/compose/install/)
```sh
# Download code
$ git clone https://github.com/jianqibot/jianqi-blog.git
$ cd jianqi-blog 

# Build docker image
$ docker build . -t blog

# run
$ docker-compoe up

# In your browser, visit `localhost:8080` to see the front page

# When you finish, run 
$ docker-compose down 
```

## $ Api Doc

```sh
cd jianqi-blog
mvn spring-boot:run
# view online api doc at http://localhost:8080/swagger-ui/index.html
```
Here are some examples
![](./apiDoc/example/api-overall.png?raw=true)

![](./apiDoc/example/api-specific.png?raw=true)

## $ Project Structure

```
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── github
    │   │           └── jianqi
    │   │               └── jianqiblog
    │   │                   ├── config
    │   │                   │   └── WebSecurityConfig.java
    │   │                   ├── controller
    │   │                   │   ├── AuthController.java  # authentification
    │   │                   │   ├── BlogController.java  
    │   │                   │   └── IndexController.java  # for index.html
    │   │                   ├── dao
    │   │                   │   ├── BlogDao.java  
    │   │                   │   └── UserMapper.java
    │   │                   ├── entity
    │   │                   │   ├── Blog.java
    │   │                   │   ├── BlogListResult.java  # JSON return type
    │   │                   │   ├── BlogResult.java  # JSON return type
    │   │                   │   ├── LoginResult.java  # JSON return type
    │   │                   │   ├── Result.java  # JSON return type
    │   │                   │   └── User.java
    │   │                   ├── JianqiBlogApplication.java
    │   │                   └── service
    │   │                       ├── AuthService.java
    │   │                       ├── AvatarGenerator.java
    │   │                       ├── BlogService.java
    │   │                       └── UserService.java
    │   └── resources
    │       ├── application.properties  # config for deploy environment
    │       └── db
    │           ├── migration  # Flyway
    │           │   ├── V1__Create_User_Table.sql
    │           │   ├── V2__Create_Blog_Table.sql
    │           │   └── V3__Add_Dummy_Data_To_User_And_Blog_DB.sql
    │           └── mybatis
    │               ├── mappers
    │               │   └── blog-mapper.xml
    │               └── mybatis-config.xml
    └── test
        ├── java
        │   └── com
        │       └── github
        │           └── jianqi
        │               └── jianqiblog
        │                   ├── controller
        │                   │   ├── AuthControllerTest.java
        │                   │   └── BlogControllerTest.java
        │                   ├── integration
        │                   │   ├── DefaultHttpRequestBuilder.java
        │                   │   ├── HttpRequestBuilder.java
        │                   │   └── IntegrationTest.java
        │                   ├── JianqiBlogApplicationTests.java
        │                   └── service
        │                       ├── BlogServiceTest.java
        │                       └── UserServiceTest.java
        └── resources
            └── test.properties  # config for test environment

```

## $ ToDo

