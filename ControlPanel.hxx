#include <QtGui/QWidget>

#include "ui_ControlPanel.h"

#include <QtCore/QHash>
#include <QtCore/QTimer>
#include <QtNetwork/QAbstractSocket>

class queue;
class QTcpSocket;
class QLabel;

class ControlPanelWidget : public QTabWidget, public Ui::ControlPanel
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
    void sendMessage(const QString& message);
    
private:
    QTcpSocket *socket;
    bool loggedIn;
    QString image;
    QString question;
    QHash<QString,QString> database;
    QTimer syncTimer;
    QTimer reconnectTimer;
    queue* effectivenessQueue;
    QLabel* effectivenessLabel;
    
    void closeEvent(QCloseEvent *event);
    QString UTF8ASC(const QString& string);
    QString ASCUTF8(const QString& string);
    
private slots:
    void dispatchSocketState(QAbstractSocket::SocketState socketState);
    void dispatchClientState(bool loggedIn);
    void dispatchIncommingData();
    void dispatchConnectionFailure();
    void dispatchEffectivenessChange(double);
    void dispatchEnteredMessage();
};
