
import serial
import SocketServer 
from socket import * 
from select import * 
import sys
import time
from threading import Thread

ser = serial.Serial()
ser.port = 'COM3' 
ser.baudrate = 9600
ser.open() 
"""
arduino
"""

HOST = ''
PORT = 7878
BUFSIZE = 1024
ADDR = (HOST, PORT)
"""
sock
"""

serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind(ADDR)
serverSocket.listen(10)
connection_list = [serverSocket]

cnt=0
traffic_color=""
temp_color=""
flag =0
send_flag =0

def color_detect(red_val, green_val, blue_val):
    #print("red %d" %int(red))
    #print("green %d" %int(green))
    #print("blue %d" %int(blue))
    color=""
    red = int(red_val)
    blue = int(blue_val)
    green = int(green_val)
    if (red < green and red < blue):
        #print("red")
        color = "red"
    if (green < red and green < blue):
        #print("green")
        color = "green"
    if (blue < red and blue < green):
        #print("blue")
        color = "green"
    return color


def get_traffic_light():
    global cnt
    global flag
    global traffic_color
    global temp_color
    global send_flag
    green_val=""
    blue_val=""
    red_val=""
    print("start sensing")
    while(True):
        if(ser.inWaiting() > 0):
            obj = ser.readline()
            str = obj[:-2].decode()
            #print (str)
            if str[0:3] == "red":
                red_val = int(str[3:])
                #print(str[0:3] , red_val)
                #print type(red_val)
                cnt += 1
            elif str[0:5] == "green":
                green_val = int(str[5:])
                #print(str[0:5], green_val)
                cnt += 1
            elif str[0:4] == "blue":
                blue_val = int(str[4:])
                #print(str[0:4],blue_val)
                cnt += 1
                if(cnt != 3):
                    cnt =0
                    print(cnt)

            if (cnt ==3):
                #print("flag =0")
                if(flag ==0):
                    temp_color = color_detect(red_val, green_val, blue_val)
                    traffic_color = temp_color
                    flag =1
                if(green_val >0) | (red_val >0) | (blue_val >0):
                    temp_color = color_detect(red_val, green_val, blue_val)
                    #print(temp_color)
                    if(temp_color != traffic_color):
                        traffic_color = temp_color
                        print("color change to %s" %traffic_color)
                        send_flag =1
                    green_val=""
                    blue_val=""
                    red_val=""
                    cnt=0

    



def server():
    global send_flag
    global traffic_color
    print('==============================================')
    print('waiting cli')
    print('==============================================')
    while connection_list:
        try:
            if(send_flag ==1): 
                print("ccccc")
            print ('[INFO] waiting request...')
            read_socket, write_socket, error_socket = select(connection_list, [], [], 10)

            for sock in read_socket:
                if sock == serverSocket:
                    clientSocket, addr_info = serverSocket.accept()
                    connection_list.append(clientSocket)
                    print('[INFO][%s] client(%s)is connected.' % (ctime(), addr_info[0]))

               
                else:
                    data = sock.recv(BUFSIZE)
                    if (data == "START") | (data == "START\n"):
                        print('[INFO][%s] start messege from client.' % ctime())

                        while(True):
                            if (send_flag ==1):
                                sock.send(traffic_color)
                            send_flag =0
                            if (data == "END") | (data == "END\n"):
                                break

                    else:
                        connection_list.remove(sock)
                        sock.close()
                        print('[INFO][%s] client disconnected.' % ctime())
        except KeyboardInterrupt:
            serverSocket.close()
            sys.exit()

   

t1 = Thread(target = get_traffic_light)
t2 = Thread(target = server)

t1.start()
t2.start()

        

 


