import cv2
import time
import fer
import mysql.connector

cameras = [cv2.VideoCapture(0)]
# For demonstration purposes, my computer's webcam is used


for camera in cameras:
    if not camera.isOpened():
        # Make sure each webcam is initialised
        print("Could not open camera " + camera.name)


detector = fer.FER()  # Initialise the emotion detection

with open("mysqlpasswd.env", "r") as pwd:
    # Open the connection to the MySQL database
    conn = mysql.connector.connect(
                                host="localhost",
                                user="root",
                                password=pwd.readline().strip(),
                                database="imgrec_one")

# Get the cursor to the database
cur = conn.cursor()


print("Starting Detection")
while True:
    for camera in range(len(cameras)):
        # Read the emotion detected by each camera
        ret, frame = cameras[camera].read()

        em = detector.top_emotion(frame)
        print(em)

        if em[0] in ("sad", "fear"):  # If sadness or fear is detected
            mysql.execute("""
                UPDATE `imgrec_one`.`rooms`
                SET
                `problem` = 1
                WHERE `id` = {cam_id};
            """.format(cam_id=camera + 1))  # MySQL ids are not zero-indexed

        elif em[0] == "happy":
            mysql.execute("""
                UPDATE `imgrec_one`.`rooms`
                SET
                `problem` = 0
                WHERE `id` = {cam_id};
            """.format(cam_id=camera + 1))

    time.sleep(3)
