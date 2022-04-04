# FilmQueryProject

### Description

This application is a film query application that takes input from a user and retrieves record information related to the film in the database.

### Functionality:
- Look up film by ID or search keyword
- View film record and greater details
- Exit application

##### Running the program:
```
- Import project into Eclipse
- Compile and run main in `FilmQueryApp.java`
- Note: Must run MySQL with relevant database (sdvid) locally
```

### Classes

- `FilmQueryApp`: Application for film queries
- `DatabaseAccessor`: Defines database methods
- `DatabaseAccessorObject`: Implements DatabaseAccessor
- `Film`: Film object
- `Actor`: Actor object

### Lessons Learned

- Structured Query Language (SQL) queries (including joins and binding variables) provide robust interactivity embedded in a Java application.
- Coding to the interface which defines the database access methods helps separate concerns related to implementation.
- Static code block is helpful to load the JDBC driver in a try-catch block

### Technologies Used

- Java Database Connectivity (JDBC)
- MySQL
- JavaSE-1.8
  - Interfaces
  - Collections; List, ArrayList
- Eclipse IDE

### Author

- Andy Cary (@acary)
