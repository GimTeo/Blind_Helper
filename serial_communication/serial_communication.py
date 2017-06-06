
import serial
ser = serial.Serial()
ser.port = 'COM3' 
ser.baudrate = 9600
ser.open() 


def color_detect(red, green, blue):
    #print("red %d" %int(red))
    #print("green %d" %int(green))
    #print("blue %d" %int(blue))
    if (red > green and red > blue):
        print ("colour is ")
        print("red")
    if (green > red and green > blue):
        print ("colour is ")
        print("green")
    if (blue > red and blue > green):
        print ("colour is ")
        print("blue")   
cnt=0
green=""
blue=""
red=""
while(True):
    if(ser.inWaiting() > 0):
        obj = ser.readline()
        str = obj[:-2].decode()
        #print type(str)
        if str[0:3] == "red":
            red = str[3:]
            #print(red)
            cnt += 1
        if str[0:4] == "blue":
            blue = str[4:]
            #print(blue)
            cnt += 1
        if str[0:5] == "green":
            green = str[5:]
            #print(green)
            cnt += 1
        #print len(green) >0
        #print len(blue) >0
        if (cnt ==3):
            print "d"
            color_detect(red, green, blue)
            green=""
            blue=""
            red=""
            cnt=0

 


