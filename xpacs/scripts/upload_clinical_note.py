import MySQLdb
import csv
import os
import sys
import time
import datetime
mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='Root@cmrgc@p!23',
    db='xpacs')
cursor = mydb.cursor()
print(os.getcwd())
fileName = sys.argv[1]
filename = "clinical_note.csv"
csv_data = csv.reader(open(str(os.getcwd())+'/../downloads/'+fileName))

for row in csv_data:
	row[2] = float(row[2])
	d = row[1].split("/")
	now = datetime.datetime(int(d[2]),int(d[0]),int(d[1]))
	row[1] = now.strftime('%Y-%m-%d')
	row[7] = int(row[7])
	print(row[1])
	cursor.execute('INSERT INTO clinical_note(assessment_date, age, height,weight,diagnosis,note,patient_infofk_id) VALUES(%s, "%s", "%s", "%s","%s","%s","%s")',row[1:])
	#cursor.execute('INSERT INTO clinical_note(assessment_date) VALUES("%s")',date)
#close the connection to the database.
mydb.commit()
cursor.close()
os.remove(str(os.getcwd())+'/downloads/'+fileName)
print("Done")
