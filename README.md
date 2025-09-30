Простой Rest Api сервер на  Ktor

Для запуска:

./gradlew build

./gradlew run

Сервер запустится на http://localhost:8080

API

Получить все элементы:

GET /items

Получить элементы по id:

GET /items/{id}

Создать элемент:

POST /items?name={name}

Удалить элемент:

DELETE /items/{id}



