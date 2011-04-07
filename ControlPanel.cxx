#include "ControlPanel.hxx"

#include <QtCore/QCoreApplication>
#include <QtCore/QString>
#include <QtCore/QFile>
#include <QtCore/QDataStream>
#include <QtNetwork/QTcpSocket>
#include <QtGui/QCloseEvent>

ControlPanelWidget::ControlPanelWidget(QWidget *parent) : QWidget(parent), loggedIn(false)
{
    connect(this,SIGNAL(clientStateChanged(bool)),this,SLOT(dispatchClientState(bool)));
    setupUi(this);
    socket=new QTcpSocket(this);
    connect(socket,SIGNAL(stateChanged(QAbstractSocket::SocketState)),this,SLOT(dispatchSocketState(QAbstractSocket::SocketState)));
    dispatchSocketState(socket->state());
    connect(socket,SIGNAL(readyRead()),this,SLOT(dispatchIncommingData()));
    
    QFile input(QCoreApplication::applicationDirPath()+"/database");
    input.open(QIODevice::ReadOnly);
    QDataStream stream(&input);
    stream>>database;
    input.close();
    
    databaseSizeLabel->setText(tr("Rozmiar: %1").arg(database.size()));
}

ControlPanelWidget::~ControlPanelWidget()
{
}

bool ControlPanelWidget::getClientState()
{
    return loggedIn;
}

void ControlPanelWidget::setClientState(bool loggedIn)
{
    if(this->loggedIn!=loggedIn)
    {
        this->loggedIn=loggedIn;
        emit clientStateChanged(loggedIn);
    }
}

void ControlPanelWidget::dispatchSocketState(QAbstractSocket::SocketState socketState)
{
    connectButton->disconnect();
    switch(socketState)
    {
        case QAbstractSocket::UnconnectedState:
            setClientState(false);
            logButton->disconnect();
            logButton->setEnabled(true);
            logButton->setText(tr("Zakończ"));
            connect(logButton,SIGNAL(clicked()),this,SLOT(attemptQuit()));
            connectButton->setEnabled(true);
            connectButton->setText(tr("Połącz"));
            stateLabel->setText(tr("<b><font color='red'>Rozłączony</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(attemptConnection()));
            break;
        case QAbstractSocket::HostLookupState:
            connectButton->setText(tr("Przerwij"));
            stateLabel->setText(tr("<b><font color='yellow'>Wyszukiwanie nazwy hosta</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(abortConnection()));
            break;
        case QAbstractSocket::ConnectingState:
            connectButton->setText(tr("Przerwij"));
            stateLabel->setText(tr("<b><font color='yellow'>Nawiązywanie połączenia</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(abortConnection()));
            break;
        case QAbstractSocket::ConnectedState:
            connectButton->setText(tr("Rozłącz"));
            stateLabel->setText(tr("<b><font color='green'>Połączony</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(attemptDisconnect()));
            dispatchClientState(loggedIn);
            break;
        case QAbstractSocket::ClosingState:
            connectButton->setText(tr("Przerwij"));
            stateLabel->setText(tr("<b><font color='yellow'>Rozłączanie</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(abortConnection()));
            dispatchClientState(loggedIn);
            logButton->setEnabled(false);
            break;
        default:
            connectButton->setText(tr("Przerwij"));
            stateLabel->setText(tr("<b><font color='red'>Błąd gniazda</font></b>"));
            connect(connectButton,SIGNAL(clicked()),this,SLOT(abortConnection()));
            break;
    }
}

void ControlPanelWidget::dispatchClientState(bool loggedIn)
{
    logButton->disconnect();
    switch(loggedIn)
    {
        case false:
            logButton->setEnabled(true);
            logButton->setText(tr("Zaloguj się"));
            connect(logButton,SIGNAL(clicked()),this,SLOT(attemptLogin()));
            break;
        case true:
            logButton->setEnabled(true);
            logButton->setText(tr("Wyloguj się"));
            connect(logButton,SIGNAL(clicked()),this,SLOT(attemptLogout()));
            break;
    }
}

void ControlPanelWidget::attemptConnection()
{
    socket->setSocketOption(QAbstractSocket::LowDelayOption,true);
    socket->connectToHost("jpks.v-lo.krakow.pl",quint16(6666));
}

void ControlPanelWidget::attemptLogin()
{
    socket->write(QString("log"+(nickLineEdit->text()==""?"NN":nickLineEdit->text())+"\n").toUtf8());
    logButton->setText(tr("Logowanie..."));
    logButton->setEnabled(false);
    if(socket->waitForBytesWritten(-1))
        setClientState(true);
    else
        dispatchClientState(loggedIn);
}

void ControlPanelWidget::attemptLogout()
{
    socket->write("out\n");
    logButton->setText(tr("Wylogowywanie..."));
    logButton->setEnabled(false);
    if(socket->waitForBytesWritten(-1))
        setClientState(false);
    else
        dispatchClientState(loggedIn);
}

void ControlPanelWidget::attemptQuit()
{
    close();
}

void ControlPanelWidget::abortConnection()
{
    socket->abort();
}

void ControlPanelWidget::attemptDisconnect()
{
    socket->disconnectFromHost();
}

void ControlPanelWidget::dispatchIncommingData()
{
    while(socket->canReadLine())
    {
        QString line=socket->readLine();
        line.truncate(line.length()-1);
        if(line.left(3)=="txt");
        else if(line.left(3)=="cnt");
        else if(line.left(3)=="img")
        {
            image=line.right(line.length()-3);
            consoleTextBrowser->append("<font color='blue'>Załadowano obraz: "+image+"</font>");
        }
        else if(line.left(3)=="que")
        {
            question+=line.right(line.length()-3);
            consoleTextBrowser->append("<font color='blue'>Pytanie: "+question+"</font>");
            if(image!="" && database.find(image+"#"+question)!=database.end())
            {
                consoleTextBrowser->append("<font color='green'>Znam odpowiedź: "+database.value(image+"#"+question)+"</font>");
                socket->write(("ans"+database.value(image+"#"+question)).toUtf8());
            }
        }
        else if(line.left(3)=="lib")
        {
            QString answer=line.right(line.length()-3);
            consoleTextBrowser->append("<font color='blue'>Poznano odpowiedź: "+answer+"</font>");
            if(image!="" && question!="")
            {
                database.insert(image+"#"+question,answer);
                databaseSizeLabel->setText(tr("Rozmiar: %1").arg(database.size()));
            }
            question="";
        }
        else if(line.left(3)=="rnk")
        {
            consoleTextBrowser->append("<font color='black'>Ranking: "+line.right(line.length()-3)+"</font>");
        }
        else if(line.left(3)=="cle");
        else if(line.left(3)=="pre");
        else if(line.left(3)=="rpr");
        else if(line.left(3)=="non");
        else if(line.left(3)=="rep");
        else
        {
            consoleTextBrowser->append("<font color='red'>Błąd protokołu: "+line+"</font>");
        }
    }
}

void ControlPanelWidget::closeEvent(QCloseEvent *event)
{
    QFile output(QCoreApplication::applicationDirPath()+"/database");
    output.open(QIODevice::WriteOnly);
    QDataStream stream(&output);
    stream<<database;
    output.close();
    event->accept();
}
