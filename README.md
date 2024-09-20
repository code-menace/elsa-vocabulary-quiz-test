# Vocabulary Quiz App

### How to run app (assuming Linux/MacOS)

In terminal, the project root, with maven wrapper (rebuilding project), run:
`./mvnw clean package -DskipTests && docker compose up --build`

Or with local maven (rebuilding project), run:
`mvn clean package -DskipTests && docker compose up --build`

Or just (using pre-built jar), run:
`docker compose up --build`

Web-app available here: [http://localhost:8080](http://localhost:8080)

### How to run tests

In terminal, in the project root, run: `./mvnw test`

### **Component Descriptions**

- **Client (Web Browser)**: The client is responsible for rendering the user interface, allowing users to join a quiz, submit answers, and view real-time leader board updates. The client establishes a WebSocket connection with the server to receive real-time updates.
- **Quiz Service (Spring Boot Application)**: The WebSocket server handles real-time communication between the client and server. It listens for incoming messages (e.g., quiz answer submissions) and pushes updates back to the client (e.g., updated leader board). It ensures a persistent connection for continuous updates between the server and the client. The app handles main business logic, such as creating quiz sessions, managing users, validating user submissions, calculating scores, updating the leader board, and handles all communication with Redis and database.
- **Queue (RabbitMQ)**: The queue handles asynchronous task processing, decoupling the real-time WebSocket server from other time-consuming tasks, such as saving results to the database. By sending tasks to the queue, the server can handle more concurrent users without blocking the connection.
- **Redis Cache**: Redis is used to store real-time leader board data and cache frequently accessed information (like user scores and quiz states). This allows for quick retrieval of real-time data without constantly hitting the database.
- **PostgreSQL**: The relational database stores persistent data such as quiz results, user information, and historical data. It ensures that quiz data is stored reliably for long-term reporting and analytics.

### **Technology Justification**:

- **Spring Boot**: I went with Spring Boot because it’s great for quickly building applications that need to scale. It comes with built-in support for WebSockets, which made setting up real-time communication straightforward. Plus, its integration with other tools, like messaging queues and databases, is seamless, which helps in keeping everything connected. And last but not least, it's the framework I'm most familiar with.
- **RabbitMQ**: RabbitMQ was chosen to handle tasks asynchronously. This helps offload processes like writing data to PostgreSQL or performing other time-consuming tasks. So the WebSocket server can stay focused on real-time responses. RabbitMQ is reliable and ensures the system can handle a high volume of tasks without slowing down.
- **Redis**: I'm using Redis to store real-time data, like scores and leader boards. Since it’s an in-memory data store, it’s incredibly fast, which is exactly what we need for real-time solution. Redis also provides data consistency if used correctly: by using INCR (or DECR) command we ensure that all updates to scores are atomic and sequential, ruling out any inconsistencies.
- **PostgreSQL**: PostgreSQL is the choice for our primary data storage because of its strong data integrity and ability to handle more complex queries. It’s reliable for storing the final quiz results and ensures everything is stored correctly for reporting and future analysis.
- **WebSocket (STOMP)**: WebSocket with STOMP was selected to handle real-time updates to the clients. This allows us to push leader board changes back to the client instantly, keeping the experience truly interactive for users.

Notes:
- Scalability is achieved through horizontal scaling. We add more app instances, redis instances, DB instances, and rely more on async processing.
- I never worked with WebSockets in SpringBoot, so setting it up was a new thing for me.
- The way our app can be improved further is to offload any calculations that are possible to offload - to background processes.