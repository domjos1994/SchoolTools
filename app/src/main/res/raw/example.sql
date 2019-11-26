INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(1,'lineare Notenliste 1',0,20,0,0,0,1,1,3.5,10,20,0);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(2,'lineare Notenliste 2',0,40,1,1,1,1,1,3.5,10,20,0);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(3,'Notenliste mit Knicks 1',1,20,0,0,0,1,1,4.4,10,16,4);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(4,'Notenliste mit Knicks 2',1,40,1,1,1,1,1,4.4,20,36,4);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(5,'exponentielle Notenliste 1',2,40,0,0,0,1,1,4.4,20,36,4);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(6,'exponentielle Notenliste 2',2,40,1,1,1,1,1,4.4,20,36,4);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(7,'IHK Notenliste 1',3,40,0,0,0,1,1,4.4,20,36,4);
INSERT INTO markLists(ID,title,type,maxPoints,tenthMarks,halfPoints,dictatMode,viewMode,markMode,customMark,customPoints,bestMarkAt,worstMarkTo) VALUES(8,'IHK Notenliste 2',3,40,1,1,1,1,1,4.4,20,36,4);

INSERT INTO teachers(ID,lastName,firstName,description) VALUES(1,'Max','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(2,'John','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(3,'Anna','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(4,'Horst','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(5,'Gerd','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(6,'Gertrud','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(7,'Kasper','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(8,'Johann','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(9,'Jusuf','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(10,'Wladimir','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(11,'Kevin','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(12,'Urs','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(13,'Ursula','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(14,'Anne','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(15,'Dadde','Mustermann','Beispiel-Lehrer');
INSERT INTO teachers(ID,lastName,firstName,description) VALUES(16,'Hasan','Mustermann','Beispiel-Lehrer');

UPDATE subjects SET teacher=1 WHERE ID=1;
UPDATE subjects SET teacher=2 WHERE ID=2;
UPDATE subjects SET teacher=3 WHERE ID=3;
UPDATE subjects SET teacher=4 WHERE ID=4;
UPDATE subjects SET teacher=5 WHERE ID=5;
UPDATE subjects SET teacher=6 WHERE ID=6;
UPDATE subjects SET teacher=7 WHERE ID=7;
INSERT INTO subjects(ID,title,alias,description,hoursInWeek,isMainSubject,backgroundColor,teacher) VALUES(8,'Betriebswirtschaftslehre','BWL','Profilfach',6,1,'',8);
INSERT INTO subjects(ID,title,alias,description,hoursInWeek,isMainSubject,backgroundColor,teacher) VALUES(9,'Volkswirtschaftslehre','VWL','Profilfach',2,1,'',9);
INSERT INTO subjects(ID,title,alias,description,hoursInWeek,isMainSubject,backgroundColor,teacher) VALUES(10,'Wirtschaftsinformatik','WI','Profilfach',4,1,'',10);

INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(1,'07:30','08:15',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(2,'08:15','09:00',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(3,'09:00','09:20',1);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(4,'09:20','10:05',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(5,'10:05','10:50',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(6,'10:50','11:10',1);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(7,'11:10','11:55',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(8,'11:55','12:40',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(9,'12:40','13:25',1);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(10,'13:25','14:10',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(11,'14:10','14:55',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(12,'14:55','15:15',1);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(13,'16:00','16:45',0);
INSERT INTO hours(ID,start_time,end_time,isBreak) VALUES(14,'16:45','17:30',0);

INSERT INTO classes(ID,title,numberOfPupils) VALUES(1,'Klasse 1',28);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(2,'Klasse 2',29);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(3,'Klasse 3',30);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(4,'Klasse 4',27);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(5,'Klasse 5',22);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(6,'Klasse 6',29);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(7,'Klasse 7',31);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(8,'Klasse 8',27);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(9,'Klasse 9',28);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(10,'Klasse 10',30);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(11,'Klasse 11',25);
INSERT INTO classes(ID,title,numberOfPupils) VALUES(12,'Klasse 12',22);

INSERT INTO years(ID,title) VALUES(1,'Jahr 2019 - 2020');
INSERT INTO years(ID,title) VALUES(2,'Jahr 2018 - 2019');
INSERT INTO years(ID,title) VALUES(3,'Jahr 2017 - 2018');
INSERT INTO years(ID,title) VALUES(4,'Jahr 2016 - 2017');
INSERT INTO years(ID,title) VALUES(5,'Jahr 2015 - 2016');
INSERT INTO years(ID,title) VALUES(6,'Jahr 2014 - 2015');
INSERT INTO years(ID,title) VALUES(7,'Jahr 2013 - 2014');
INSERT INTO years(ID,title) VALUES(8,'Jahr 2012 - 2013');

INSERT INTO plans(ID,title,class,plan_year) VALUES(1,'Klasse 1 Jahr 2012',1,8);
INSERT INTO plans(ID,title,class,plan_year) VALUES(2,'Klasse 12 Jahr 2019',12,1);
INSERT INTO plans(ID,title,class,plan_year) VALUES(3,'Klasse 5 Jahr 2016',5,4);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(1,1,0,1,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(2,1,0,2,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(3,1,0,4,2,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(4,1,0,5,2,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(5,1,0,7,3,1,0,111,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(6,1,1,1,3,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(7,1,1,2,3,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(8,1,1,4,4,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(9,1,1,5,4,1,0,111,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(10,1,2,1,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(11,1,2,2,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(12,1,2,4,2,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(13,1,2,5,2,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(14,1,2,1,1,1,0,111,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(15,1,3,1,3,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(16,1,3,2,3,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(17,1,3,4,4,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(18,1,3,5,4,1,0,111,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(19,1,4,1,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(20,1,4,2,1,1,0,111,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(21,1,4,4,1,0,111,0);


INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(22,2,0,1,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(23,2,0,2,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(24,2,0,4,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(25,2,0,5,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(26,2,0,7,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(27,2,0,8,3,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(28,2,1,1,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(29,2,1,2,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(30,2,1,4,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(31,2,1,5,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(32,2,1,7,6,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(33,2,1,8,6,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(34,2,2,1,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(35,2,2,2,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(36,2,2,4,8,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(37,2,2,5,8,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(38,2,2,7,9,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(39,2,2,8,9,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(40,2,3,1,10,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(41,2,3,2,10,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(42,2,3,4,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(43,2,3,5,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(44,2,3,7,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(45,2,3,8,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(46,2,3,10,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(47,2,3,11,3,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(48,2,4,1,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(49,2,4,2,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(50,2,4,4,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(51,2,4,5,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(52,2,4,7,6,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(53,2,4,8,6,1,0,112,0);


INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(54,3,0,1,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(55,3,0,2,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(56,3,0,4,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(57,3,0,5,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(58,3,0,7,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(59,3,0,8,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(60,3,0,10,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(61,3,0,11,4,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(62,3,1,1,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(63,3,1,2,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(64,3,1,4,6,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(65,3,1,5,6,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(66,3,1,7,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(67,3,1,8,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(68,3,1,10,8,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(69,3,1,11,8,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(70,3,2,1,9,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(71,3,2,2,9,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(72,3,2,4,10,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(73,3,2,5,10,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(74,3,2,7,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(75,3,2,8,1,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(76,3,2,10,2,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(77,3,2,11,2,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(78,3,3,1,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(79,3,3,2,3,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(80,3,3,4,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(81,3,3,5,4,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(82,3,3,7,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(83,3,3,8,5,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(84,3,3,10,6,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(85,3,3,11,6,1,0,112,0);

INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(86,3,4,1,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(87,3,4,2,7,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(88,3,4,4,8,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(89,3,4,5,8,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(90,3,4,7,9,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(91,3,4,8,9,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(92,3,4,10,10,1,0,112,0);
INSERT INTO timeTable(ID,plan,day,hour,subject,teacher,class,roomNumber,current_timetable) VALUES(93,3,4,11,10,1,0,112,0);

INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(1,'Arbeit 1','','',1.0,2.5,3.2,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(2,'Arbeit 2','','',1.0,2.8,2.7,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(3,'Test 1','','',0.5,3.5,2.8,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(4,'Arbeit 3','','',1.0,1.8,3.2,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(5,'Test 2','','',0.5,4.5,2.5,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(6,'Arbeit 4','','',1.0,2.6,3.2,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(7,'Arbeit 1','','',1.0,1.2,2.4,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(8,'Arbeit 2','','',1.0,2.5,2.5,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(9,'Test 2','','',0.5,2.9,3.2,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(10,'Arbeit 3','','',1.0,1.8,2.0,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(11,'Test 2','','',0.5,4.0,3.9,null);
INSERT INTO tests(ID,title,description,themes,weight,mark,average,testDate) VALUES(12,'Arbeit 4','','',1.0,4.2,2.5,null);

INSERT INTO schoolYears(ID,year,subject,test) VALUES(1,1,1,1);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(2,1,1,2);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(3,1,1,3);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(4,1,1,4);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(5,1,1,5);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(6,1,1,6);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(7,2,2,7);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(8,2,2,8);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(9,2,2,9);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(10,2,2,10);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(11,2,2,11);
INSERT INTO schoolYears(ID,year,subject,test) VALUES(12,2,2,12);

INSERT INTO notes(ID,title,description) VALUES(1,'Notiz 1','Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, ');
INSERT INTO notes(ID,title,description) VALUES(2,'Notiz 2','Auch gibt es niemanden, der den Schmerz an sich liebt, sucht oder wünscht, nur, weil er Schmerz ist, es sei denn, es kommt zu zufälligen Umständen, in denen Mühen und Schmerz ihm große Freude bereiten können. Um ein triviales Beispiel zu nehmen, wer von uns unterzieht sich je anstrengender körperlicher Betätigung, außer um Vorteile daraus zu ziehen? Aber wer hat irgend ein Recht, einen Menschen zu tadeln, der die Entscheidung trifft, eine Freude zu genießen, die keine unangenehmen Folgen hat, oder einen, der Schmerz vermeidet, welcher keine daraus resultierende Freude nach sich zieht? Auch gibt es niemanden, der den Schmerz an sich liebt, sucht oder wünscht, nur, weil er Schmerz ist, es sei denn, es kommt zu zufälligen Umständen, in denen Mühen und Schmerz ihm große Freude bereiten können. Um ein triviales Beispiel zu nehmen, wer von uns unterzieht sich je anstrengender körperlicher Betätigung, außer um Vorteile daraus zu ziehen? Aber wer hat irgend ein Recht, einen Menschen zu tadeln, der die Entscheidung trifft, eine Freude zu genießen, die keine unangenehmen Folgen hat, oder einen, der Schmerz vermeidet, welcher keine daraus resultierende Freude nach sich zieht?Auch gibt es niemanden, der den Schmerz an sich liebt, sucht oder wünscht, nur,');
INSERT INTO notes(ID,title,description) VALUES(3,'Notiz 3','Li Europan lingues es membres del sam familie. Lor separat existentie es un myth. Por scientie, musica, sport etc, litot Europa usa li sam vocabular. Li lingues differe solmen in li grammatica, li pronunciation e li plu commun vocabules. Omnicos directe al desirabilite de un nov lingua franca: On refusa continuar payar custosi traductores. At solmen va esser necessi far uniform grammatica, pronunciation e plu sommun paroles. Ma quande lingues coalesce, li grammatica del resultant lingue es plu simplic e regulari quam ti del coalescent lingues. Li nov lingua franca va esser plu simplic e regulari quam li existent Europan lingues. It va esser tam simplic quam Occidental in fact, it va esser Occidental. A un Angleso it va semblar un simplificat Angles, quam un skeptic Cambridge amico dit me que Occidental es.Li Europan lingues es membres del sam familie. Lor separat existentie es un myth. Por scientie, musica, sport etc, litot Europa usa li sam vocabular. Li lingues differe solmen in li grammatica, li pronunciation e li plu commun vocabules. Omnicos directe al desirabilite de un nov lingua franca: On refusa continuar payar custosi traductores. At solmen va esser necessi far uniform grammatica, pronunciation e plu sommun paroles. ');
INSERT INTO notes(ID,title,description) VALUES(4,'Notiz 4','Weit hinten, hinter den Wortbergen, fern der Länder Vokalien und Konsonantien leben die Blindtexte. Abgeschieden wohnen sie in Buchstabhausen an der Küste des Semantik, eines großen Sprachozeans. Ein kleines Bächlein namens Duden fließt durch ihren Ort und versorgt sie mit den nötigen Regelialien. Es ist ein paradiesmatisches Land, in dem einem gebratene Satzteile in den Mund fliegen. Nicht einmal von der allmächtigen Interpunktion werden die Blindtexte beherrscht – ein geradezu unorthographisches Leben. Eines Tages aber beschloß eine kleine Zeile Blindtext, ihr Name war Lorem Ipsum, hinaus zu gehen in die weite Grammatik. Der große Oxmox riet ihr davon ab, da es dort wimmele von bösen Kommata, wilden Fragezeichen und hinterhältigen Semikoli, doch das Blindtextchen ließ sich nicht beirren. Es packte seine sieben Versalien, schob sich sein Initial in den Gürtel und machte sich auf den Weg. Als es die ersten Hügel des Kursivgebirges erklommen hatte, warf es einen letzten Blick zurück auf die Skyline seiner Heimatstadt Buchstabhausen, die Headline von Alphabetdorf und die Subline seiner eigenen Straße, der Zeilengasse. Wehmütig lief ihm eine rhetorische Frage über die Wange, dann setzte es seinen Weg fort. Unterwegs traf es eine Copy. Die Copy warnte das Blindtextchen, da, wo sie herkäme wäre sie ');
INSERT INTO notes(ID,title,description) VALUES(5,'Notiz 5','Eine wunderbare Heiterkeit hat meine ganze Seele eingenommen, gleich den süßen Frühlingsmorgen, die ich mit ganzem Herzen genieße. Ich bin allein und freue mich meines Lebens in dieser Gegend, die für solche Seelen geschaffen ist wie die meine. Ich bin so glücklich, mein Bester, so ganz in dem Gefühle von ruhigem Dasein versunken, daß meine Kunst darunter leidet. Ich könnte jetzt nicht zeichnen, nicht einen Strich, und bin nie ein größerer Maler gewesen als in diesen Augenblicken. Wenn das liebe Tal um mich dampft, und die hohe Sonne an der Oberfläche der undurchdringlichen Finsternis meines Waldes ruht, und nur einzelne Strahlen sich in das innere Heiligtum stehlen, ich dann im hohen Grase am fallenden Bache liege, und näher an der Erde tausend mannigfaltige Gräschen mir merkwürdig werden; wenn ich das Wimmeln der kleinen Welt zwischen Halmen, die unzähligen, unergründlichen Gestalten der Würmchen, der Mückchen näher an meinem Herzen fühle, und fühle die Gegenwart des Allmächtigen, der uns nach seinem Bilde schuf, das Wehen des Alliebenden, der uns in ewiger Wonne schwebend trägt und erhält; mein Freund! Wenn''s dann um meine Augen dämmert, und die Welt um mich her und der Himmel ganz in meiner Seele ruhn wie die Gestalt einer ');
INSERT INTO notes(ID,title,description) VALUES(6,'Notiz 6','Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheueren Ungeziefer verwandelt. Und es war ihnen wie eine Bestätigung ihrer neuen Träume und guten Absichten, als am Ziele ihrer Fahrt die Tochter als erste sich erhob und ihren jungen Körper dehnte. »Es ist ein eigentümlicher Apparat«, sagte der Offizier zu dem Forschungsreisenden und überblickte mit einem gewissermaßen bewundernden Blick den ihm doch wohlbekannten Apparat. Sie hätten noch ins Boot springen können, aber der Reisende hob ein schweres, geknotetes Tau vom Boden, drohte ihnen damit und hielt sie dadurch von dem Sprunge ab. In den letzten Jahrzehnten ist das Interesse an Hungerkünstlern sehr zurückgegangen. Aber sie überwanden sich, umdrängten den Käfig und wollten sich gar nicht fortrühren.Jemand musste Josef K. verleumdet haben, denn ohne dass er etwas Böses getan hätte, wurde er eines Morgens verhaftet. »Wie ein Hund!« sagte er, es war, als sollte die Scham ihn überleben. Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich ');
INSERT INTO notes(ID,title,description) VALUES(7,'Notiz 7','s gibt im Moment in diese Mannschaft, oh, einige Spieler vergessen ihnen Profi was sie sind. Ich lese nicht sehr viele Zeitungen, aber ich habe gehört viele Situationen. Erstens: wir haben nicht offensiv gespielt. Es gibt keine deutsche Mannschaft spielt offensiv und die Name offensiv wie Bayern. Letzte Spiel hatten wir in Platz drei Spitzen: Elber, Jancka und dann Zickler. Wir müssen nicht vergessen Zickler. Zickler ist eine Spitzen mehr, Mehmet eh mehr Basler. Ist klar diese Wörter, ist möglich verstehen, was ich hab gesagt? Danke. Offensiv, offensiv ist wie machen wir in Platz. Zweitens: ich habe erklärt mit diese zwei Spieler: nach Dortmund brauchen vielleicht Halbzeit Pause. Ich habe auch andere Mannschaften gesehen in Europa nach diese Mittwoch. Ich habe gesehen auch zwei Tage die Training. Ein Trainer ist nicht ein Idiot! Ein Trainer sei sehen was passieren in Platz. In diese Spiel es waren zwei, drei diese Spieler waren schwach wie eine Flasche leer! Haben Sie gesehen Mittwoch, welche Mannschaft hat gespielt Mittwoch? Hat gespielt Mehmet oder gespielt Basler oder hat gespielt Trapattoni? Diese Spieler beklagen mehr als sie spielen! Wissen Sie, warum die Italienmannschaften kaufen nicht diese Spieler? Weil wir haben gesehen viele Male solche Spiel! Haben ');
INSERT INTO notes(ID,title,description) VALUES(8,'Notiz 8','Er hörte leise Schritte hinter sich. Das bedeutete nichts Gutes. Wer würde ihm schon folgen, spät in der Nacht und dazu noch in dieser engen Gasse mitten im übel beleumundeten Hafenviertel? Gerade jetzt, wo er das Ding seines Lebens gedreht hatte und mit der Beute verschwinden wollte! Hatte einer seiner zahllosen Kollegen dieselbe Idee gehabt, ihn beobachtet und abgewartet, um ihn nun um die Früchte seiner Arbeit zu erleichtern? Oder gehörten die Schritte hinter ihm zu einem der unzähligen Gesetzeshüter dieser Stadt, und die stählerne Acht um seine Handgelenke würde gleich zuschnappen? Er konnte die Aufforderung stehen zu bleiben schon hören. Gehetzt sah er sich um. Plötzlich erblickte er den schmalen Durchgang. Blitzartig drehte er sich nach rechts und verschwand zwischen den beiden Gebäuden. Beinahe wäre er dabei über den umgestürzten Mülleimer gefallen, der mitten im Weg lag. Er versuchte, sich in der Dunkelheit seinen Weg zu ertasten und erstarrte: Anscheinend gab es keinen anderen Ausweg aus diesem kleinen Hof als den Durchgang, durch den er gekommen war. Die Schritte wurden lauter und lauter, er sah eine dunkle Gestalt um die Ecke biegen. Fieberhaft irrten seine Augen durch die nächtliche Dunkelheit und suchten einen Ausweg. War jetzt wirklich alles vorbei, ');
INSERT INTO notes(ID,title,description) VALUES(9,'Notiz 9','Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten - man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede Schrift gut aus. Quod erat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 der Texte zur Pflicht werden. Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften aber fast immer enthalten sind. Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können eingebaute Kapitälchen, Kerning oder Ligaturen (sehr pfiffig) nicht richtig dargestellt werden.Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks ');
INSERT INTO notes(ID,title,description) VALUES(10,'Notiz 10','Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten - man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede Schrift gut aus. Quod erat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 der Texte zur Pflicht werden. Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften aber fast immer enthalten sind. Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können eingebaute Kapitälchen, Kerning oder Ligaturen (sehr pfiffig) nicht richtig dargestellt werden.Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks ');
INSERT INTO notes(ID,title,description) VALUES(11,'Notiz 11','Zwei flinke Boxer jagen die quirlige Eva und ihren Mops durch Sylt. Franz jagt im komplett verwahrlosten Taxi quer durch Bayern. Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich. Vogel Quax zwickt Johnys Pferd Bim. Sylvia wagt quick den Jux bei Pforzheim. Polyfon zwitschernd aßen Mäxchens Vögel Rüben, Joghurt und Quark. "Fix, Schwyz!" quäkt Jürgen blöd vom Paß. Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich. Falsches Üben von Xylophonmusik quält jeden größeren Zwerg. Heizölrückstoßabdämpfung. Zwei flinke Boxer jagen die quirlige Eva und ihren Mops durch Sylt. Franz jagt im komplett verwahrlosten Taxi quer durch Bayern. Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich. Vogel Quax zwickt Johnys Pferd Bim. Sylvia wagt quick den Jux bei Pforzheim. Polyfon zwitschernd aßen Mäxchens Vögel Rüben, Joghurt und Quark. "Fix, Schwyz!" quäkt Jürgen blöd vom Paß. Victor jagt zwölf Boxkämpfer quer über den großen Sylter Deich. Falsches Üben von Xylophonmusik quält jeden größeren Zwerg. Heizölrückstoßabdämpfung.Zwei flinke Boxer jagen die quirlige Eva und ihren Mops durch Sylt. Franz jagt im komplett verwahrlosten Taxi quer durch Bayern. Zwölf Boxkämpfer jagen Viktor quer über den großen Sylter Deich. Vogel Quax zwickt Johnys Pferd Bim. Sylvia wagt quick den Jux ');
INSERT INTO notes(ID,title,description) VALUES(12,'Notiz 12','Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks oder Handgloves, um Schriften zu testen. Manchmal Sätze, die alle Buchstaben des Alphabets enthalten - man nennt diese Sätze »Pangrams«. Sehr bekannt ist dieser: The quick brown fox jumps over the lazy old dog. Oft werden in Typoblindtexte auch fremdsprachige Satzteile eingebaut (AVAIL® and Wefox™ are testing aussi la Kerning), um die Wirkung in anderen Sprachen zu testen. In Lateinisch sieht zum Beispiel fast jede Schrift gut aus. Quod erat demonstrandum. Seit 1975 fehlen in den meisten Testtexten die Zahlen, weswegen nach TypoGb. 204 § ab dem Jahr 2034 Zahlen in 86 der Texte zur Pflicht werden. Nichteinhaltung wird mit bis zu 245 € oder 368 $ bestraft. Genauso wichtig in sind mittlerweile auch Âçcèñtë, die in neueren Schriften aber fast immer enthalten sind. Ein wichtiges aber schwierig zu integrierendes Feld sind OpenType-Funktionalitäten. Je nach Software und Voreinstellungen können eingebaute Kapitälchen, Kerning oder Ligaturen (sehr pfiffig) nicht richtig dargestellt werden.Dies ist ein Typoblindtext. An ihm kann man sehen, ob alle Buchstaben da sind und wie sie aussehen. Manchmal benutzt man Worte wie Hamburgefonts, Rafgenduks ');

INSERT INTO toDoLists(ID,title,description) VALUES(1,'Wunschliste','');
INSERT INTO toDoLists(ID,title,description) VALUES(2,'Einkaufsliste','');
INSERT INTO toDoLists(ID,title,description) VALUES(3,'Kuchen','');

INSERT INTO todos(ID,title,importance,solved,todoList) VALUES(1,'Äpfel',7,0,2);
INSERT INTO todos(ID,title,importance,solved,todoList) VALUES(2,'Birnen',7,1,2);
INSERT INTO todos(ID,title,importance,solved,todoList) VALUES(3,'Playstation 5',9,0,1);

INSERT INTO timerEvents(ID,title,description,category,deadline,subject,teacher,schoolclass,eventDate) VALUES(1,)