[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="img/logo.jpg" alt="Logo" width="114">
  </a>

<h3 align="center">Exbook</h3>

  <p align="center">
    Website for exchanging or buying books with other users
    <br />
    <br />
    <a href="">View Demo (Not set up at the moment)</a>
    ·
    <a href="https://github.com/Ejden/exbook-backend/issues">Report Bug</a>
    ·
    <a href="https://github.com/Ejden/exbook-backend/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#features">Features</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

#### Note that this project is in early development!
This is backend part of Exbook web application.

Exbook is web app that will help people sell and exchange their book with
other people.

If you want to see frontend code go [here](https://github.com/Ejden/exbook-frontend).


### Backend Built With

* [Kotlin](https://kotlinlang.org/)
* [Spring Boot](https://spring.io/)
* [MongoDB](https://www.mongodb.com/)
* [Kotest](https://kotest.io/)


<!-- GETTING STARTED -->
## Getting Started
To quickly run local Exbook server follow instructions bellow

### Docker MongoDB setup


To run this app locally, You want to create mongoDB database first.
Create docker stack.yml file and paste this code in this file:
```yml
# Use root/example as user/password credentials
version: '3.1'

services:

  mongo:
    image: mongo
    restart: always
    ports:
     - 27017:27017
    environment:
      MONGO_INITDB_DATABASE: exbook
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: password
    volumes:
     - ./data/mongo/init_db_users.js:/docker-entrypoint-initdb.d/init_db_users.js:ro
```
In folder when you created stack.yml file create data folder, in data folder create mongo folder.
In created mongo folder create init_db_users.js file with below structure. This will create basic user in exbook database with read and write privileges:
```js
db.createUser(
    {
        user: "admin",
        pwd: "password",
        roles:[
            {
                role: "readWrite",
                db:   "exbook"
            }
        ]
    }
);
```
To run database open terminal, go to folder with stack.yml file and type
```
docker-compose -f stack.yml up
```
To shutdown database type
```
docker-compose -f stack.yml down
```

### App secrets in .env file


Application to work needs various secrets like database url or jwt token secret key.
In root folder of project create local.env file, and paste bellow code to this file.
```dotenv
JWT_EXP_TIME=36000000
JWT_SECRET=uwkyi43875cny43875ctyb4375btc34t67rtb43g76fb453g7ctg98243n7gfxc7yxn924gx
MONGODB_HOST=localhost
MONGODB_port=27017
MONGODB_DATABASE_NAME=exbook
MONGODB_DATABASE_USERNAME=admin
MONGODB_DATABASE_PASSWORD=password
```

###Application


To download the latest .jar stable version of server go to [releases](https://github.com/Ejden/exbook-backend/releases) page.
Otherwise, if you want, you can download code from any branch (be carefully, there is no guaranty that this code will run or compile without problems).
Just simply clone repo to your IDE and run it with JVM system parameter:
````
-Dspring.profiles.active=local
````
This will activate local profile of the application.
## Features (backend part)

### Implemented
* Categories
* Offers
* Account
* Offer stock
* Basket
* Images
* Shipping Methods
* JWT Token Authorization
* Swagger

### Not implemented yet
* Searching offers
* Displaying offers
* Messaging users
* Exchanging/Buying books
* Recommendations


See the [open issues](https://github.com/Ejden/exbook-backend/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the GPL License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Adrian Stypiński - [adrian.stypinski+github@gmail.com](mailto:adrian.stypinski+github@gmail.com) - Mail





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Ejden/exbook-backend.svg?style=for-the-badge
[contributors-url]: https://github.com/Ejden/exbook-backend/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Ejden/exbook-backend.svg?style=for-the-badge
[forks-url]: https://github.com/Ejden/exbook-backend/network/members
[stars-shield]: https://img.shields.io/github/stars/Ejden/exbook-backend.svg?style=for-the-badge
[stars-url]: https://github.com/Ejden/exbook-backend/stargazers
[issues-shield]: https://img.shields.io/github/issues/Ejden/exbook-backend.svg?style=for-the-badge
[issues-url]: https://github.com/Ejden/exbook-backend/issues
[license-shield]: https://img.shields.io/github/license/Ejden/exbook-backend.svg?style=for-the-badge
[license-url]: https://github.com/Ejden/exbook-backend/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/adrian-stypi%C5%84ski-74b319198/
