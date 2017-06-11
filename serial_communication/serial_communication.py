
import serial
import SocketServer 
import socket
import sys
from time import ctime
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



def color_detect(red, green, blue):
    #print("red %d" %int(red))
    #print("green %d" %int(green))
    #print("blue %d" %int(blue))
    color=""
    if (red > green and red > blue):
        #print("red")
        color = "red"
    if (green > red and green > blue):
        #print("green")
        color = "green"
    if (blue > red and blue > green):
        #print("blue")
        color = "green"
    return color

cnt=0
green=""
blue=""
red=""
traffic_color=""
temp_color=""
flag =0


def get_traffic_light():
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
                if(flag ==0):
                    temp_color ,traffic_color= color_detect(red, green, blue)
                    flag =1
                temp_color = color_detect(red, green, blue)
                green=""
                blue=""
                red=""
                cnt=0
                if(temp_color != traffic_color):
                    traffic_color = temp_color
                    print("color change to %s" %traffic_color)


'''
def server():
    while connection_list:
        try:
            print('==============================================')
            print('waiting cli')
            print('==============================================')

            # select 로 요청을 받고, 10초마다 블럭킹을 해제하도록 함
            read_socket, write_socket, error_socket = select(connection_list, [], [], 10)

            for sock in read_socket:
                # 새로운 접속
                if sock == serverSocket:
                    clientSocket, addr_info = serverSocket.accept()
                    connection_list.append(clientSocket)
                    print('[INFO][%s] client(%s)is connected.' % (ctime(), addr_info[0]))

               
                # 접속한 사용자(클라이언트)로부터 새로운 데이터 받음
                else:
                    data = sock.recv(BUFSIZE)
                    if data:
                        print('[INFO][%s] messege from client.' % ctime())
                        #print("client : %s" , % data)
                        for socket_in_list in connection_list:
                            if socket_in_list != serverSocket and socket_in_list != sock:
                                try:
                                    socket_in_list.send('[%s] %s' % (ctime(), data))
                                    print('[INFO][%s] data is sended to client.' % ctime())
                                except Exception as e:
                                    print(e.message)
                                    socket_in_list.close()
                                    connection_list.remove(socket_in_list)
                                    continue
                    else:
                        connection_list.remove(sock)
                        sock.close()
                        print('[INFO][%s] client disconnected.' % ctime())
        except KeyboardInterrupt:
            serverSocket.close()
            sys.exit()

            '''

t1 = Thread(target = get_traffic_light)
t2 = Thread(target = server)

t1.start

        

 


