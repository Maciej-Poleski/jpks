#include <QtGui/QApplication>
#include <QtCore/QTextCodec>

#include "ControlPanel.hxx"

int main(int argc,char**argv)
{
    QApplication app(argc,argv);
    QTextCodec* codec=QTextCodec::codecForName("UTF-8");
    QTextCodec::setCodecForCStrings(codec);
    QTextCodec::setCodecForLocale(codec);
    QTextCodec::setCodecForTr(codec);
    ControlPanelWidget controlPanelWidget;
    controlPanelWidget.show();
    return app.exec();
}