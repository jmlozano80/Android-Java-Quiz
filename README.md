Android-Java-Quiz
=================

Developed for Android verion 4.4.2 (API 19) and above
.
The application requires authentication against data stored in an external database at https://blistering-fire-1687.firebaseio.com (NoSQL database). The first time that the application runs, it download the questions and answers from this database and the questions are stored into the application database (Sqlite). Every time when a user logs in, the application checks for any updates via a version attribute related to the questions to download new questions. In addition, the score of the user is recorded into the internal Sqlite database to retrieve later the user’s top score.
