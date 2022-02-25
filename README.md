# jianqi-blog
![build](https://img.shields.io/circleci/build/github/jianqibot/jianqi-blog/main) ![](https://img.shields.io/github/languages/top/jianqibot/jianqi-blog) ![](https://img.shields.io/github/repo-size/jianqibot/jianqi-blog)



### Description
A simple online blog system build mainly with `Spring-boot` and `MySQL`. All visitors are able to see blogs posted by other people if the author set the blog to be public. A new user can register, login, start to create his/her new posts and update/delete those blogs later on. 


### How to install and run project
1. Download code from Github 
2. Install [Docker](https://docs.docker.com/get-docker/) and [Docker-Compose](https://docs.docker.com/compose/install/)
3. In terminal, at root directory of the code, run `docker build . -tag blog`
4. In terminal, at root directory of the code, run `docker-compoe up` to start app
5. In your browser, visit `localhost:8080` to see the front page
6. When you finish, run `docker-compose down` to clean up 

### Demo
