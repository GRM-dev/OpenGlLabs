using System;
using System.Diagnostics;
using System.Threading;

namespace OpenGlProject.Core
{
    public class TpsTimer
    {
        private long _lastTickTime;
        private int _currentTicks;
        private long _lastTps;
        private int _calcTickTime;
        private int _setTps;
        private long _lastCallTime;
        private int _sleepTime;

        public void InitTime(int tps)
        {
            _calcTickTime = 1000 / tps;
            _setTps = tps;
            GetDelta();
            _lastCallTime = GetTime();
        }

        public void Sync()
        {
            UpdateTps();
            Wait();
        }

        private void UpdateTps()
        {
            var currentTime = GetTime();
            var cCycleTime = (currentTime - _lastCallTime);
            if (currentTime - _lastCallTime >= 1000)
            {
                _currentTicks = 0;
                if (cCycleTime == 0)
                {
                    _lastTps = 1;
                }
                else
                {
                    _lastTps = _setTps * 1000 / cCycleTime;
                }
                _lastCallTime = currentTime;
                _sleepTime = 0;
            }
            else
            {
                _sleepTime = (int)(1000 - cCycleTime > 0 ? cCycleTime - (_calcTickTime * CurrentTicks) : 0);
                _currentTicks++;
            }
        }

        private void Wait()
        {
            if (_sleepTime > 0)
            {
                try
                {
                    Thread.Sleep(_sleepTime);
                }
                catch (Exception e)
                {
                    Debug.WriteLine(e);
                }
                _sleepTime = 0;
            }
        }

        public bool IsFullCycle()
        {
            return _currentTicks == 0;
        }

        public long GetDelta()
        {
            var time = GetTime();
            var delta = time - _lastTickTime;
            _lastTickTime = time;
            return delta;
        }

        public long GetTime()
        {
            return DateTime.Now.Ticks / TimeSpan.TicksPerMillisecond;
        }

        public int CurrentTicks => _currentTicks;
        public long LastTps => _lastTps;
    }
}
