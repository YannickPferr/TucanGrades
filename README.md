# TucanGrades

Reads grades from Tucan (TU Darmstadt) and sends any updates via email.

A config file "config.conf" with the following content is needed: <br>
```
username = <TUCAN USERID>
password = <TUCAN PASSWORD>
emailActive = true/false (specifies if emails should be sent)
fromEmail = tucan@grades.checker (or other from name)
toEmail = <EMAIL ADDRESS TO RECEIVE UPDATES>
checkInterval = <TIME INTERVAL TO CHECK GRADES IN MINUTES>
```
