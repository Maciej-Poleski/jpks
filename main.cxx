#include <iostream>
#include <QtNetwork>
#include <QCoreApplication>
#include <QDataStream>

int main(int argc,char**argv)
{
    QCoreApplication app(argc,argv);
    QTcpSocket socket;
    socket.connectToHost("jpks.v-lo.krakow.pl",quint16(6666));
    if(socket.waitForConnected())
    {
        std::cout<<"Nawiązano połączenie!\n";
        socket.write("logtest\n");
        socket.waitForBytesWritten();
        while(socket.waitForReadyRead(-1))
        {
            if(socket.canReadLine())
            {
                std::cout<<socket.readLine().data()<<std::flush;
            }
        }
        socket.disconnectFromHost();
        socket.waitForDisconnected();
    }
    else
    {
        std::cout<<"Połączenie nieudane: "<<socket.errorString().toStdString()<<std::endl;
        return 1;
    }

    return 0;
}