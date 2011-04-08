#include <QtGui/QWidget>

#include "ui_ControlPanel.h"

#include <QtCore/QHash>
#include <QtCore/QTimer>
#include <QtNetwork/QAbstractSocket>

class QTcpSocket;

class ControlPanelWidget : public QWidget, public Ui::ControlPanel
{
    Q_OBJECT
    
public:
    ControlPanelWidget(QWidget* parent=0);
    virtual ~ControlPanelWidget();
    
    bool getClientState();
    void setClientState(bool loggedIn);
    
signals:
    void clientStateChanged(bool loggedIn);
    
public slots:
    void attemptConnection();
    void attemptLogin();
    void attemptLogout();
    void attemptQuit();
    void abortConnection();
    void attemptDisconnect();
    void store();
    void load();
    
private:
    QTcpSocket *socket;
    bool loggedIn;
    QString image;
    QString question;
    QHash<QString,QString> database;
    QTimer syncTimer;
    QTimer reconnectTimer;
    
    void closeEvent(QCloseEvent *event);
    
private slots:
    void dispatchSocketState(QAbstractSocket::SocketState socketState);
    void dispatchClientState(bool loggedIn);
    void dispatchIncommingData();
    void dispatchConnectionFailure();
};
