using System;

namespace SenryakuShuriken.Core
{
    public class StopwatchEventArgs : EventArgs
    {
        public StopwatchEventArgs(long cycle, long elapsedTime, int tick, float delta)
        {
            Cycle = cycle;
            ElapsedTime = elapsedTime;
            Tick = tick;
            Delta = delta;
        }

        public long Cycle { get; }
        public long ElapsedTime { get; }
        public int Tick { get; }
        public float Delta { get; }
    }
}