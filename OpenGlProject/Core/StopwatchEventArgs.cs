using System;

namespace OpenGlProject.Core
{
    public class StopwatchEventArgs:EventArgs
    {
        public StopwatchEventArgs(long cycle, long elapsedTime, int tick)
        {
            Cycle = cycle;
            ElapsedTime = elapsedTime;
            Tick = tick;
        }

        public long Cycle { get; }
        public long ElapsedTime { get; }
        public int Tick { get; }
    }
}