CREATE TABLE IF NOT EXISTS markLists(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    type INTEGER NOT NULL,
    maxPoints INTEGER NOT NULL,
    tenthMarks INTEGER DEFAULT 1,
    halfPoints INTEGER DEFAULT 0,
    dictatMode INTEGER DEFAULT 0,
    viewMode INTEGER DEFAULT 2,
    markMode INTEGER DEFAULT 0,
    customMark DOUBLE DEFAULT 3.5,
    customPoints DOUBLE DEFAULT 10.0,
    bestMarkAt DOUBLE DEFAULT 20.0,
    worstMarkTo DOUBLE DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS teachers(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    lastName VARCHAR(500) NOT NULL,
    firstName VARCHAR(500) DEFAULT '',
    description TEXT DEFAULT '',
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subjects(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    alias VARCHAR(5) NOT NULL,
    description TEXT DEFAULT '',
    hoursInWeek INTEGER DEFAULT 0,
    isMainSubject INTEGER DEFAULT 0,
    backgroundColor VARCHAR(10) DEFAULT '',
    teacher INTEGER DEFAULT 0,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(teacher) REFERENCES teachers(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hours(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    start_time VARCHAR(5) NOT NULL,
    end_time VARCHAR(5) NOT NULL,
    isBreak INTEGER DEFAULT 0,
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS classes(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    numberOfPupils INTEGER DEFAULT 0,
    description TEXT DEFAULT ''
);

CREATE TABLE IF NOT EXISTS years(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT ''
);

CREATE TABLE IF NOT EXISTS plans(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    class INTEGER DEFAULT 0,
    plan_year INTEGER NOT NULL,
    description TEXT DEFAULT '',
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(class) REFERENCES classes(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(plan_year) REFERENCES years(ID)
);

CREATE TABLE IF NOT EXISTS timeTable(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    plan INTEGER NOT NULL,
    day INTEGER NOT NULL,
    hour INTEGER NOT NULL,
    subject INTEGER NOT NULL,
    teacher INTEGER DEFAULT 0,
    class INTEGER DEFAULT 0,
    roomNumber VARCHAR(255) DEFAULT '',
    entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    current_timetable INTEGER DEFAULT 0,
    FOREIGN KEY(plan) REFERENCES plans(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(hour) REFERENCES hours(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(subject) REFERENCES subjects(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(teacher) REFERENCES teachers(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(class) REFERENCES classes(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS memories(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    memoryDate DATE DEFAULT NULL,
    itemID INTEGER NOT NULL,
    [table] VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS tests(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    themes TEXT DEFAULT '',
    weight DOUBLE DEFAULT 1.0,
    mark DOUBLE DEFAULT 0.0,
    average DOUBLE DEFAULT 0.0,
    testDate DATE DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS schoolYears(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    year INTEGER NOT NULL,
    subject INTEGER NOT NULL,
    test INTEGER NOT NULL,
    FOREIGN KEY(year) REFERENCES years(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(subject) REFERENCES subjects(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(test) REFERENCES tests(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notes(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT ''
);

CREATE TABLE IF NOT EXISTS toDoLists(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    listDate DATE DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS toDos(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    importance INTEGER DEFAULT 1,
    solved INTEGER DEFAULT 0,
    category VARCHAR(50) DEFAULT '',
    toDoList INTEGER NOT NULL,
    FOREIGN KEY(toDoList) REFERENCES toDoLists(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS timerEvents(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    category VARCHAR(50) DEFAULT '',
    subject INTEGER DEFAULT 0,
    teacher INTEGER DEFAULT 0,
    schoolClass INTEGER DEFAULT 0,
    eventDate DATE NOT NULL,
    FOREIGN KEY(subject) REFERENCES subjects(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(teacher) REFERENCES teachers(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(schoolClass) REFERENCES classes(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learningCardGroups(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    category VARCHAR(50) DEFAULT '',
    deadline DATE DEFAULT NULL,
    subject INTEGER DEFAULT 0,
    teacher INTEGER DEFAULT 0,
    FOREIGN KEY(subject) REFERENCES subjects(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(teacher) REFERENCES teachers(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learningCards(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    category VARCHAR(50) DEFAULT '',
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    note1 TEXT,
    note2 TEXT,
    priority INTEGER DEFAULT 0,
    cardGroup INTEGER NOT NULL,
    FOREIGN KEY(cardGroup) REFERENCES learningCardGroups(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learningCardQueries(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    description TEXT DEFAULT '',
    cardGroup INTEGER DEFAULT 0,
    category VARCHAR(500) DEFAULT '',
    priority INTEGER DEFAULT 0,
    wrongCardsOfQuery INTEGER DEFAULT 0,
    periodic INTEGER DEFAULT 0,
    period INTEGER DEFAULT 0,
    untilDeadLine INTEGER DEFAULT 0,
    answerMustEqual INTEGER DEFAULT 0,
    showNotes INTEGER DEFAULT 0,
    tries INTEGER DEFAULT 0,
    showNotesImmediately INTEGER DEFAULT 0,
    randomVocab INTEGER DEFAULT 0,
    randomVocabNumber INTEGER DEFAULT 0,
    FOREIGN KEY(cardGroup) REFERENCES learningCardGroups(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(wrongCardsOfQuery) REFERENCES learningCardQueries(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learningCardQueryTrainings(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    learningCardQuery INTEGER DEFAULT 0,
    current_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS learningCardQueryResults(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    learningCardQueryTraining INTEGER DEFAULT 0,
    learningCard INTEGER DEFAULT 0,
    answerTry1 TEXT,
    resultTry1 INTEGER DEFAULT 0,
    answerTry2 TEXT,
    resultTry2 INTEGER DEFAULT 0,
    answerTry3 TEXT,
    resultTry3 INTEGER DEFAULT 0,
    resultWhole DOUBLE DEFAULT 0.0,
    FOREIGN KEY(learningCardQueryTraining) REFERENCES learningCardQueryTrainings(ID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY(learningCard) REFERENCES learningCards(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookmarks(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    title VARCHAR(500) NOT NULL,
    tags TEXT DEFAULT '',
    link TEXT DEFAULT '',
    themes TEXT DEFAULT '',
    subject INTEGER DEFAULT 0,
    description TEXT DEFAULT '',
    preview BLOB DEFAULT NULL,
    contentData BLOB DEFAULT NULL,
    FOREIGN KEY(subject) REFERENCES subjects(ID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS settings(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    alias VARCHAR(50) NOT NULL,
    byteContent BLOB DEFAULT NULL,
    stringContent TEXT DEFAULT ''
);

CREATE TABLE IF NOT EXISTS synchronisation(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    type VARCHAR(50) NOT NULL,
    ts DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dict(
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    motherLanguage VARCHAR(2) NOT NULL,
    motherItem TEXT NOT NULL,
    foreignLanguage VARCHAR(2) NOT NULL,
    foreignItem TEXT NOT NULL
);