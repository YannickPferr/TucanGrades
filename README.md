# TucanGrades

Reads grades from Tucan (TU Darmstadt) and sends any updates via email.

A config file "config.conf" with the following content is needed: <br>
username = TUCAN USERID <br>
password = TUCAN PASSWORD <br>
emailActive = true/false (specifies if emails should be sent) <br>
fromEmail = tucan@grades.checker (or other from name) <br>
toEmail = EMAIL ADDRESS TO RECEIVE UPDATES <br>
checkInterval = TIME INTERVAL TO CHECK GRADES IN MINUTES <br>
