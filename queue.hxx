#include <queue>
#include <QtCore/QtGlobal>
#include <QtCore/QObject>

class queue : public QObject, std::queue<bool>
{
    Q_OBJECT
    quint16 sum;
public:
    queue(QObject* parent=0);
    void push(const bool& o);
    double getEffectiveness() const;
    
signals:
    void effectivenessChanged(double);
};

inline double queue::getEffectiveness() const
{
    return size()==0?-1:static_cast<double>(sum)/size();
}