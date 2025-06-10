# Campus Event Management System

This is a simple application to manage campus events. A console version is provided as well as a basic Swing based GUI. Students can browse and register for events, while organizers can create events. Login now requires both user ID and password.

Compile and run with:

```
javac -encoding UTF-8 src/*.java
java -cp src CampusEventManagementSystem
```

To start the GUI instead run:

```
javac -encoding UTF-8 src/*.java
java -cp src CampusEventManagementGUI
```

Sample accounts (passwords in users.csv):
```
Organizers: org1 / pass1, org2 / pass2
Students: stu1 / p1, stu2 / p2
```

Features:
- Search events by keyword or date
- Export participant lists to CSV
- Event status shows whether an event is open, full, or ended
- Events are listed with open events first, then full, then ended, sorted by date
  and finished events cannot be registered or canceled
- Events include a start time and end time in the format `YYYY-MM-DD HH:mm`
- Only the organizer who created an event can edit it
