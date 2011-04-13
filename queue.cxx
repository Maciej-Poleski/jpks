#include "queue.hxx"

queue::queue(QObject* parent) : QObject(parent), sum(0)
{}

void queue::push(const bool& o)
{
    double oldEffectiveness=getEffectiveness();
    if(size()==500)
    {
        sum-=front();
        pop();
    }
    std::queue<bool>::push(o);
    sum+=o;
    if(oldEffectiveness!=getEffectiveness())
    {
        emit effectivenessChanged(getEffectiveness());
    }
}
