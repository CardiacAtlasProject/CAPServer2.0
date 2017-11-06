import MySQLdb
import csv
import os
import sys
mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='Root@cmrgc@p!23',
    db='xpacs')
cursor = mydb.cursor()
print(os.getcwd())
fileName = sys.argv[1]
csv_data = csv.reader(open(str(os.getcwd())+'/downloads/'+fileName))
for row in csv_data:
	row[0] = int(row[0])
	cursor.execute('INSERT INTO patient_info(patient_id, cohort, ethnicity,gender,primary_diagnosis) VALUES("%s", "%s", "%s", "%s","%s")',row[1:])
#close the connection to the database.
mydb.commit()
cursor.close()
os.remove(str(os.getcwd())+'/downloads/'+fileName)
print("Done")
